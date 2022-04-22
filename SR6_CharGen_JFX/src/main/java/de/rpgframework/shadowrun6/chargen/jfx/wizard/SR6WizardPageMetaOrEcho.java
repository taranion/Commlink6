package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.Wizard;

import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author prelle
 *
 */
public class SR6WizardPageMetaOrEcho extends AWizardPageMetaOrEcho {
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageMetaOrEcho.class.getPackageName()+".SR6WizardPages");

	//-------------------------------------------------------------------
	public SR6WizardPageMetaOrEcho(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard, charGen);
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initComponents() {
		super.initComponents();
//		selection.setFilterNode(new QualityFilterNode(RES, selection, QualityType.NORMAL));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(FlexibleApplication.getInstance(), charGen.getMetamagicOrEchoController()));
//		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL);
		
		Function<Requirement,String> resolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
		bxDescription = new GenericDescriptionVBox(resolver);
	}

}
