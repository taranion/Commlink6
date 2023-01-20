package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageQualities;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.QualityFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class SR6WizardPageQualities extends AWizardPageQualities {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	private Label lbNumber;

	//-------------------------------------------------------------------
	public SR6WizardPageQualities(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard, charGen);
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		super.initComponents();
		lbNumber = new Label("?");
		lbNumber.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

//		selection.setFilterNode(new QualityFilterNode(RES, selection, QualityType.NORMAL));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(charGen.getQualityController()));
		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL);

		QualityFilterNode filter = new QualityFilterNode(RES, selection, QualityType.NORMAL, QualityType.ADEPT_WAY);
		selection.setFilterNode(filter);
		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL || qv.getModifyable().getType()==QualityType.ADEPT_WAY);
		selection.setRequirementResolver(Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		selection.setModificationResolver(Shadowrun6Tools.modificationResolver(Locale.getDefault()));

		bxDescription = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	protected void initLayout() {
		super.initLayout();
		Label hdNumber = new Label(ResourceI18N.get(RES, "page.qualities.numQualities"));
		Label lbNumberMax = new Label("/6");
		line.getChildren().addAll(hdNumber, lbNumber, lbNumberMax);
		HBox.setMargin(hdNumber, new Insets(0,0,0,10));
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		super.refresh();
		lbNumber.setText(String.valueOf( ((CommonQualityGenerator)charGen.getQualityController()).getNumberOfQualities()));
	}

}
