package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.SignatureManeuver;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditSignatureManeuverDialog;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SignatureManeuverListCell extends ListCell<SignatureManeuver> {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditSignatureManeuverDialog.class.getPackageName()+".Dialogs");

	private final static String NORMAL_STYLE = "maneuver-cell";

	private Label lbName;
	private Label lbSkill;
	private Label lbActions;
	private Label lbCost;
	private Label lbWarning;
	private VBox bxNameSkill;
	private HBox layout;

	//---------------------------------------------------------
	public SignatureManeuverListCell() {
		lbName = new Label();
		lbName.getStyleClass().add("base");
		lbSkill= new Label();
		lbActions = new Label();
		lbWarning = new Label("Not connected with quality");
		lbWarning.setText(ResourceI18N.format(UI, "dialog.editsignature.label.warning.format", Shadowrun6Core.getItem(SR6Quality.class,"signature_maneuver").getName()));
		lbWarning.setStyle("-fx-text-fill: red");
		bxNameSkill = new VBox(5, new HBox(5,lbName, lbSkill), lbActions, lbWarning);
		bxNameSkill.setMaxWidth(Double.MAX_VALUE);
		lbCost = new Label();
		lbCost.setStyle("-fx-font-size: 200%; -fx-font-weight: bold");
		layout = new HBox(10, bxNameSkill, lbCost);
		layout.setAlignment(Pos.BASELINE_LEFT);
		HBox.setHgrow(bxNameSkill, Priority.ALWAYS);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(SignatureManeuver item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
		} else {
			lbName.setText(item.getName());
			lbSkill.setText("("+item.getSkill().getName()+")");
			lbActions.setText(item.getAction1().getName()+" & "+item.getAction2().getName());
			int cost = item.getAction1().getCost() + item.getAction2().getCost();
			lbCost.setText(String.valueOf(cost));
			boolean warn = item.getQuality()==null;
			lbWarning.setVisible(warn);
			lbWarning.setManaged(warn);
			setGraphic(layout);
		}
	}
}
