package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.Wizard;

import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author prelle
 *
 */
public class SR6WizardPageMetaOrEcho extends AWizardPageMetaOrEcho {
	
	private final static Logger logger = System.getLogger(SR6WizardPageMetaOrEcho.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageMetaOrEcho.class.getPackageName()+".SR6WizardPages");

	//-------------------------------------------------------------------
	public SR6WizardPageMetaOrEcho(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard, charGen);
	}
	
	//-------------------------------------------------------------------
	protected void initComponents() {
		super.initComponents();
//		selection.setFilterNode(new QualityFilterNode(RES, selection, QualityType.NORMAL));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getMetamagicOrEchoController()));
//		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL);
		
		Function<Requirement,String> resolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
		bxDescription = new GenericDescriptionVBox(resolver);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		
		MagicOrResonanceType type = charGen.getModel().getMagicOrResonanceType();
		if (type!=null && type.usesMagic()) {
			logger.log(Level.WARNING, type+" can initiate - enable page");
			activeProperty().set(true);
		} else if (type!=null && type.usesResonance()) {
			logger.log(Level.WARNING, type+" can submerse - enable page");
			activeProperty().set(true);
		} else if (charGen.getModel().getRuleValueAsBoolean(Shadowrun6Rules.ALLOW_TRANSHUMANISM)) {
			logger.log(Level.WARNING, "Transhumanism allowed enable page");
			activeProperty().set(true);
		} else {
			logger.log(Level.WARNING, "No magic, no resonance, no transhumansism - disable page");
			activeProperty().set(false);
		}
	}

}
