package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.ResourceBundle;

import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageRituals;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuyRitualGenerator;
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
public class SR6WizardPageRituals extends WizardPageRituals {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	protected NumberUnitBackHeader backHeaderCP;

	//-------------------------------------------------------------------
	public SR6WizardPageRituals(Wizard wizard, SR6CharacterController charGen) {
		super(wizard, charGen);
		// TODO Auto-generated constructor stub
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
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		super.handleControllerEvent(type, param);
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			selection.setOptionCallback(new ChoiceSelectorDialog<>(selection.getController()));
		}
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		super.refresh();
		IRitualController spellCtrl = charGen.getRitualController();
		SR6CharacterGenerator real = (SR6CharacterGenerator) charGen;
		if (charGen instanceof GeneratorWrapper)
			real = ((GeneratorWrapper)charGen).getWrapped();

		if (spellCtrl instanceof SR6PriorityRitualGenerator) {
			backHeaderCP.setVisible(false);
		} else
		if (real instanceof ISR6PointBuyGenerator) {
			backHeaderCP.setValue( ((ISR6PointBuyGenerator)real).getSettings().characterPoints );
			backHeaderCP.setVisible(true);
		}
	}

}
