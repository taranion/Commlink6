package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;

import org.prelle.javafx.Wizard;

import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author prelle
 *
 */
public class SR6WizardPageMetaOrEcho extends AWizardPageMetaOrEcho {

	private final static Logger logger = System.getLogger(SR6WizardPageMetaOrEcho.class.getPackageName()+".metaecho");

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
		selection.setAvailableCellFactory(lv -> new ComplexDataItemListCell<MetamagicOrEcho>( () -> selection.getController(), Shadowrun6Tools.requirementResolver(Locale.getDefault())));

		bxDescription = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();

		MagicOrResonanceType type = charGen.getModel().getMagicOrResonanceType();
		if (type!=null && type.usesMagic() && charGen.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CHARGEN_ALLOW_INITIATION)) {
			logger.log(Level.DEBUG, type+" can initiate - enable page");
			activeProperty().set(true);
		} else if (type!=null && type.usesResonance() && charGen.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CHARGEN_ALLOW_INITIATION)) {
			logger.log(Level.DEBUG, type+" can submerse - enable page");
			activeProperty().set(true);
		} else if (charGen.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.ALLOW_TRANSHUMANISM)) {
			logger.log(Level.DEBUG, "Transhumanism allowed enable page");
			activeProperty().set(true);
		} else {
			logger.log(Level.DEBUG, "No magic, no resonance, no transhumansism - disable page");
			activeProperty().set(false);
		}
	}

}
