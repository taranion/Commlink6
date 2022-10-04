package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import org.prelle.javafx.Wizard;

import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageComplexForms;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author prelle
 *
 */
public class SR6WizardPageComplexForms extends WizardPageComplexForms {

	//-------------------------------------------------------------------
	public SR6WizardPageComplexForms(Wizard wizard, IShadowrunCharacterController<?, ?, ?, ?> charGen) {
		super(wizard, charGen);
	}
	
	protected void initComponents() {
		super.initComponents();
		selection.setOptionCallback( (item,list) -> {
			ChoiceSelectorDialog<ComplexForm, ComplexFormValue> dialog = new ChoiceSelectorDialog<>(charGen.getComplexFormController());
			return dialog.apply(item, list);
		});
	}

}
