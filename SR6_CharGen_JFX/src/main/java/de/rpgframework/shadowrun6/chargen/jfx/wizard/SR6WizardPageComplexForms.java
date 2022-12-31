package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.ResourceBundle;

import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageComplexForms;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityComplexFormGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityRitualGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class SR6WizardPageComplexForms extends WizardPageComplexForms {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	protected NumberUnitBackHeader backHeaderCP;

	//-------------------------------------------------------------------
	public SR6WizardPageComplexForms(Wizard wizard, IShadowrunCharacterController<?, ?, ?, ?> charGen) {
		super(wizard, charGen);
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		super.initComponents();
		selection.setOptionCallback( (item,list) -> {
			ChoiceSelectorDialog<ComplexForm, ComplexFormValue> dialog = new ChoiceSelectorDialog<>(charGen.getComplexFormController());
			return dialog.apply(item, list);
		});
	}

	//-------------------------------------------------------------------
	@Override
	protected void initBackHeader() {
		super.initBackHeader();
		backHeaderCP = new NumberUnitBackHeader(ResourceI18N.get(RES, "label.pointbuy.cp"));
		backHeaderCP.setVisible(true);
		HBox.setMargin(backHeaderCP, new Insets(0,10,0,10));

		Region buf = new Region();
		buf.setMaxWidth(Double.MAX_VALUE);
		HBox box = new HBox(backHeaderKarma, backHeaderCP);
		HBox backHeader = new HBox(10, box, buf, new SymbolIcon("setting"));
		HBox.setHgrow(buf, Priority.ALWAYS);
		//backHeader.setMaxWidth(Double.MAX_VALUE);
		HBox.setMargin(box, new Insets(0,0,0,10));
		HBox.setMargin(backHeader.getChildren().get(2), new Insets(0,10,0,0));
		super.setBackHeader(backHeader);
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		super.refresh();
		IComplexFormController spellCtrl = charGen.getComplexFormController();
		SR6CharacterGenerator real = (SR6CharacterGenerator) charGen;
		if (charGen instanceof GeneratorWrapper)
			real = ((GeneratorWrapper)charGen).getWrapped();

		if (spellCtrl instanceof SR6PriorityComplexFormGenerator) {
			backHeaderCP.setVisible(false);
		} else
		if (real instanceof ISR6PointBuyGenerator) {
			backHeaderCP.setValue( ((ISR6PointBuyGenerator)real).getSettings().characterPoints );
			backHeaderCP.setVisible(true);
		}
	}

}
