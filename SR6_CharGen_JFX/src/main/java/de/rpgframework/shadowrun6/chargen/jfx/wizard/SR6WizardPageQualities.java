package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityListCell;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageQualities;
import de.rpgframework.shadowrun6.SR6MetaType;
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
	private QualityFilterNode filter;

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
		selection.setSelectedFilter(qv -> typesToShow.contains(qv.getModifyable().getType()) );
		selection.setAvailableCellFactory(lv -> new QualityListCell(selection.getController()) {
			public String requirementToText(Requirement req) {
				return Shadowrun6Tools.getRequirementString(req, Locale.getDefault());
			}
		});


		filter = new QualityFilterNode(RES, selection, QualityType.NORMAL, QualityType.ADEPT_WAY);
		selection.setFilterNode(filter);
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
		lbNumber.setText(String.valueOf( ((CommonQualityGenerator)charGen.getQualityController()).getNumberOfQualities()));

		MagicOrResonanceType type = charGen.getModel().getMagicOrResonanceType();
		SR6MetaType meta = charGen.getModel().getMetatype();
		System.getLogger(getClass().getPackageName()).log(Level.WARNING, "type="+type);

		typesToShow.clear();
		typesToShow.add(QualityType.NORMAL);
		if (type!=null && type.usesPowers()) {
			typesToShow.add(QualityType.ADEPT_WAY);
		}
		if (type!=null && type.usesResonance()) {
			typesToShow.add(QualityType.STREAM);
		}
		if (meta!=null && meta.isAI()) {
			typesToShow.add(QualityType.VIRTUAL_LIFE);
		}
		filter.setAllowedTypes(typesToShow);

		super.refresh();
	}

}
