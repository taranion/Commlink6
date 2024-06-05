package de.rpgframework.shadowrun6.chargen.jfx.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.ShadowrunAction;
import de.rpgframework.shadowrun.ShadowrunAction.Category;
import de.rpgframework.shadowrun.ShadowrunAction.Type;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.SignatureManeuver;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author Stefan
 *
 */
public class EditSignatureManeuverDialog extends ManagedDialog {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditSignatureManeuverDialog.class.getPackageName()+".Dialogs");

	private NavigButtonControl btnControl;

	private ChoiceBox<SR6Skill> cbSkill;
	private ChoiceBox<ShadowrunAction.Category> cbActionCat1;
	private ChoiceBox<ShadowrunAction.Category> cbActionCat2;
	private ChoiceBox<ShadowrunAction> cbAction1;
	private ChoiceBox<ShadowrunAction> cbAction2;
	private TextField tfName;

	private SignatureManeuver selectedItem;

	//--------------------------------------------------------------------
	public EditSignatureManeuverDialog(SignatureManeuver data, boolean isEdit) {
		super(ResourceI18N.get(UI,"dialog.title"), null, CloseType.APPLY);
		if (!isEdit) {
			buttons.setAll(CloseType.OK, CloseType.CANCEL);
		}
		this.selectedItem    = data;
		btnControl = new NavigButtonControl();

		initCompoments();
		initLayout();
		initInteractivity();
		if (isEdit)
			refresh();
	}

	//--------------------------------------------------------------------
	private void initCompoments() {
		cbSkill = new ChoiceBox<SR6Skill>();
		cbSkill.getItems().addAll(Shadowrun6Core.getSkills());
		cbSkill.setConverter(new StringConverter<SR6Skill>() {
			public String toString(SR6Skill val) { return (val!=null)?val.getName():"-"; }
			public SR6Skill fromString(String arg0) {return null;}
		});

		Category[] allowed = new Category[] {
				Category.BOOST,
				Category.COMBAT,
				Category.POSITION,
				Category.DRIVING,
				Category.MATRIX,
				Category.SOCIAL,
				Category.OTHER,
		};

		cbActionCat1 = new ChoiceBox<>();
		cbActionCat1.getItems().addAll(allowed);
		cbActionCat1.setConverter(new StringConverter<Category>() {
			public String toString(Category val) { return (val!=null)?(val.getName()):"-"; }
			public Category fromString(String arg0) {return null;}
		});
		cbAction1 = new ChoiceBox<>();
		cbAction1.getItems().addAll(Shadowrun6Core.getItemList(ShadowrunAction.class).stream().filter(a -> a.getType()==Type.BOOST||a.getType()==Type.EDGE).collect(Collectors.toList()));
		Collections.sort(cbAction1.getItems());
		cbAction1.setConverter(new StringConverter<ShadowrunAction>() {
			public String toString(ShadowrunAction val) { return (val!=null)?(val.getName()+" ("+val.getCost()+")"):"-"; }
			public ShadowrunAction fromString(String arg0) {return null;}
		});

		cbActionCat2 = new ChoiceBox<>();
		cbActionCat2.getItems().addAll(allowed);
		cbActionCat2.setConverter(new StringConverter<Category>() {
			public String toString(Category val) { return (val!=null)?(val.getName()):"-"; }
			public Category fromString(String arg0) {return null;}
		});
		cbAction2 = new ChoiceBox<>();
		cbAction2.getItems().addAll(Shadowrun6Core.getItemList(ShadowrunAction.class).stream().filter(a -> a.getType()==Type.BOOST||a.getType()==Type.EDGE).collect(Collectors.toList()));
		cbAction2.setConverter(new StringConverter<ShadowrunAction>() {
			public String toString(ShadowrunAction val) { return (val!=null)?(val.getName()+" ("+val.getCost()+")"):"-"; }
			public ShadowrunAction fromString(String arg0) {return null;}
		});

		tfName = new TextField();
		tfName.setPromptText(ResourceI18N.get(UI, "prompt.name"));
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		// Description
		Label whenPerformingA = new Label(ResourceI18N.get(UI, "label.whenPerformingA"));
		Label test = new Label(ResourceI18N.get(UI, "label.test"));
		Label combine = new Label(ResourceI18N.get(UI, "label.combine"));
		Label and = new Label(ResourceI18N.get(UI, "label.and"));
		Label nameThis = new Label(ResourceI18N.get(UI, "label.nameThis"));

		HBox bxOpt1 = new HBox(5, cbActionCat1, cbAction1);
		HBox bxOpt2 = new HBox(5, cbActionCat2, cbAction2);

		FlowPane flow = new FlowPane(10,10, whenPerformingA, cbSkill, test, combine);

		VBox layout = new VBox(10, flow, bxOpt1, and, bxOpt2, nameThis, tfName);
		setContent(layout);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		Comparator<ShadowrunAction> compare = new Comparator<ShadowrunAction>() {
			public int compare(ShadowrunAction o1, ShadowrunAction o2) {
				int i = o1.getName().compareTo(o2.getName());
				if (i!=0) return i;
				return Integer.compare(o1.getCost(), o2.getCost());
			}
		};

		tfName.textProperty().addListener( (ov,o,n) -> {
			if (selectedItem!=null)
				selectedItem.setName(n);
			updateOKButton();
		});
		tfName.setOnAction(ev -> refresh());
		tfName.focusedProperty().addListener( (ov,o,n) -> refresh());

		cbSkill.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (selectedItem!=null)
				selectedItem.setSkill(n);
			updateOKButton();
		});

		cbActionCat1.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			cbAction1.getItems().setAll(Shadowrun6Core.getItemList(ShadowrunAction.class).stream()
					.filter(a -> a.getCategory()==n)
					.filter(a -> a.getType()==Type.BOOST||a.getType()==Type.EDGE).collect(Collectors.toList()));
			Collections.sort(cbAction1.getItems(), compare);
			cbAction1.getSelectionModel().clearAndSelect(0);
		});
		cbAction1.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (selectedItem!=null)
				selectedItem.setAction1(n);
			updateOKButton();
		});
		cbActionCat2.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			cbAction2.getItems().setAll(Shadowrun6Core.getItemList(ShadowrunAction.class).stream()
					.filter(a -> a.getCategory()==n)
					.filter(a -> a.getType()==Type.BOOST||a.getType()==Type.EDGE).collect(Collectors.toList()));
			Collections.sort(cbAction2.getItems(), compare);
			cbAction2.getSelectionModel().clearAndSelect(0);
		});
		cbAction2.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (selectedItem!=null)
				selectedItem.setAction2(n);
			updateOKButton();
		});
	}

	//-------------------------------------------------------------------
	private void updateOKButton() {
		btnControl.setDisabled(CloseType.OK,
				tfName.getText()!=null &&
				cbSkill.getValue()!=null &&
				cbAction1.getValue()!=null
				&& cbAction2.getValue()!=null);
	}

	//--------------------------------------------------------------------
	private void refresh()  {
		tfName.setText(selectedItem.getName());
		cbSkill.setValue(selectedItem.getSkill());
		cbAction1.setValue(selectedItem.getAction1());
		cbAction2.setValue(selectedItem.getAction2());
	}

	//-------------------------------------------------------------------
	public NavigButtonControl getButtonControl() {
		return btnControl;
	}

}
