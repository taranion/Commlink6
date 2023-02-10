package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAdeptPowers;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.karma.SR6KarmaAdeptPowerGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuyAdeptPowerGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityAdeptPowerGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.AdeptPowerFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class SR6WizardPageAdeptPowers extends WizardPageAdeptPowers {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	protected NumberUnitBackHeader backHeaderCP;

	//-------------------------------------------------------------------
	public SR6WizardPageAdeptPowers(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard, charGen);
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		super.initComponents();
		selection.setFilterNode(new AdeptPowerFilterNode(RES, selection));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getAdeptPowerController()));
//		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL);

		bxDescription = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
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

		IShadowrunCharacterGenerator<?, ?, ?, ?> real = (charGen instanceof GeneratorWrapper)?((GeneratorWrapper)charGen).getWrapped():(IShadowrunCharacterGenerator<?, ?, ?, ?>) charGen;
		IAdeptPowerController ctrl = charGen.getAdeptPowerController();
		if (ctrl instanceof SR6PriorityAdeptPowerGenerator) {
			hdConvert.setText(ResourceI18N.get(WizardPageAdeptPowers.RES, "page.adeptpowers.convert"));
			backHeaderCP.setVisible(false);
		} else if (ctrl instanceof SR6PointBuyAdeptPowerGenerator) {
			hdConvert.setText(ResourceI18N.get(RES, "page.adeptpowers.convertCP"));
			backHeaderCP.setValue( ((PointBuyCharacterGenerator)real).getSettings().characterPoints );
			backHeaderCP.setVisible(true);
			lbValue.setText(String.valueOf(((PointBuyCharacterGenerator)real).getSettings().boughtPP  ));
		} else if (ctrl instanceof SR6KarmaAdeptPowerGenerator) {
			hdConvert.setText(ResourceI18N.get(RES, "page.adeptpowers.convertCP"));
			backHeaderCP.setVisible(false);
		} else {
			logger.log(Level.ERROR,"Unsupported AdeptPowerController: "+ctrl.getClass());
			System.err.println("Unsupported AdeptPowerController");
		}
	}

}
