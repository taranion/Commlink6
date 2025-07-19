package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.Focus;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.chargen.jfx.section.ComplexFormSection;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.DataStructuresSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.EmulatedProgramsSection;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class ResonancePage extends Page {

	private final static Logger logger = System.getLogger(ResonancePage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private ComplexFormSection secCplx;
	private MetamagicOrEchoSection secMeta;
	private EmulatedProgramsSection secPrograms;
	private DataStructuresSection secDataStruct;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private SR6CharacterController ctrl;
	private Predicate<CarriedItem<ItemTemplate>> filter;

	//-------------------------------------------------------------------
	public ResonancePage() {
		super(ResourceI18N.get(RES, "page.resonance.title"));
		filter = carried -> carried.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue()==ItemType.ELECTRONICS && List.of(ItemSubType.matrixDevices()).contains(carried.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue());
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initComplexForms();
		initMetamagic();
		initPrograms();
		initDataStructures();
	}

	//-------------------------------------------------------------------
	private void initComplexForms() {
		secCplx = new ComplexFormSection(
				ResourceI18N.get(RES, "page.resonance.section.complexforms"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				item -> (new ChoiceSelectorDialog<ComplexForm,ComplexFormValue>(ctrl.getComplexFormController()).apply(item, item.getChoices())));
		secCplx.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secCplx, 4);
		FlexGridPane.setMinHeight(secCplx, 6);
		FlexGridPane.setMediumWidth(secCplx, 5);
		FlexGridPane.setMediumHeight(secCplx, 8);
	}

	//-------------------------------------------------------------------
	private void initMetamagic() {
		secMeta = new MetamagicOrEchoSection(
				ResourceI18N.get(RES, "page.resonance.section.echoes"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				MetamagicOrEcho.Type.ECHO
				);
		secMeta.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secMeta, 4);
		FlexGridPane.setMinHeight(secMeta, 6);
		FlexGridPane.setMediumWidth(secMeta, 5);
		FlexGridPane.setMediumHeight(secMeta, 8);
	}

	//-------------------------------------------------------------------
	private void initPrograms() {
		secPrograms = new EmulatedProgramsSection(
				ResourceI18N.get(RES, "page.resonance.section.emulated"));
		secPrograms.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secPrograms, 4);
		FlexGridPane.setMinHeight(secPrograms, 6);
		FlexGridPane.setMediumWidth(secPrograms, 5);
		FlexGridPane.setMediumHeight(secPrograms, 8);
	}

	//-------------------------------------------------------------------
	private void initDataStructures() {
		secDataStruct = new DataStructuresSection(
				ResourceI18N.get(RES, "page.resonance.section.datastructures")) {
			@Override
			protected Decision[] requestUserDecisions(DataStructure value) {
				logger.log(Level.WARNING, "Present choice dialog");
				ChoiceSelectorDialog<DataStructure, DataStructureValue> dialog = new ChoiceSelectorDialog<>(ctrl.getDataStructureController());
				Decision[] dec = dialog.apply(value, value.getChoices());
				logger.log(Level.WARNING, "decisions: "+Arrays.toString(dec));
				return dec;
			}};
		secDataStruct.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secDataStruct, 4);
		FlexGridPane.setMinHeight(secDataStruct, 6);
		FlexGridPane.setMediumWidth(secDataStruct, 5);
		FlexGridPane.setMediumHeight(secDataStruct, 8);
	}

	//-------------------------------------------------------------------
	private void initLayout() {

		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secCplx,secMeta, secPrograms, secDataStruct);

		layout = new OptionalNodePane(flex, new Label(ResourceI18N.get(RES,"select.for.description")));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secCplx.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secMeta.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secPrograms.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secDataStruct.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(CarriedItem<ItemTemplate> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new CarriedItemDescriptionPane( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), ctrl, n));
			layout.setTitle(n.getModifyable().getName());
		}
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox(
					Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}

	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");

		this.ctrl = ctrl;
		secMeta.updateController(ctrl);
		secCplx.updateController(ctrl);
		secPrograms.updateController(ctrl);
		secDataStruct.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secCplx.refresh();
		secMeta.refresh();
		secPrograms.refresh();
		secDataStruct.refresh();
	}

}
