package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.public_skins.GridPaneTableViewSkin;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;

/**
 * @author Stefan
 *
 */
public class AttributeTable extends TableView<AttributeValue> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(AttributeTable.class.getName());

	private SR6CharacterController ctrl;

	private TableColumn<AttributeValue, Boolean> recCol;
	private TableColumn<AttributeValue, String> nameCol;
	private TableColumn<AttributeValue, AttributeValue> valCol;
	private TableColumn<AttributeValue, Number> maxCol;
	private TableColumn<AttributeValue, Number> modCol;
	private TableColumn<AttributeValue, Number> sumCol;

	//--------------------------------------------------------------------
	public AttributeTable(SR6CharacterController ctrl) {
//		if (ctrl==null)
//			throw new NullPointerException();
		this.ctrl = ctrl;
		setSkin(new GridPaneTableViewSkin<>(this, true));
		initColumns();
		initValueFactories();
		initCellFactories();
		initLayout();

//		MagicOrResonanceType type = ctrl.getModel().getMagicOrResonanceType();
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
//			if (key==ShadowrunAttribute.RESONANCE && !type.usesResonance())
//				continue;
//			if (key==ShadowrunAttribute.MAGIC && !type.usesMagic() && ctrl.getModel().getAttribute(key).getMinimum()==0)
//				continue;
			getItems().add(ctrl.getModel().getAttribute(key));
		}

	}

	//--------------------------------------------------------------------
	private void initColumns() {
		recCol = new TableColumn<>();
		nameCol = new TableColumn<>(RES.getString("column.name"));
		valCol  = new TableColumn<>(RES.getString("column.value"));
		maxCol  = new TableColumn<>(RES.getString("column.max"));
		modCol  = new TableColumn<>(RES.getString("column.mod"));
		sumCol  = new TableColumn<>(RES.getString("column.sum"));

		recCol .setId("attrtable-rec");
		nameCol.setId("attrtable-name");
		valCol .setId("attrtable-val");
		maxCol .setId("attrtable-max");
		modCol .setId("attrtable-mod");
		sumCol .setId("attrtable-sum");

		valCol.setStyle("-fx-alignment: center");
		maxCol.setStyle("-fx-alignment: center");
		modCol.setStyle("-fx-alignment: center");
		sumCol.setStyle("-fx-alignment: center");

		recCol.setPrefWidth(40);
		nameCol.setMinWidth(120);
		valCol.setPrefWidth(110);
		maxCol.setMinWidth(50);
		modCol.setMinWidth(40);
		sumCol.setMinWidth(50);
	}

	//--------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initLayout() {
		getColumns().addAll(recCol, nameCol, valCol, modCol, sumCol);
	}

	//--------------------------------------------------------------------
	private void initValueFactories() {
//		recCol.setCellValueFactory(cdf -> new SimpleBooleanProperty(ctrl.getSkillController().isConceptSkill(cdf.getValue().getModifyable())));
		nameCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getModifyable().getName()));
		maxCol.setCellValueFactory(cdf -> new SimpleIntegerProperty(cdf.getValue().getModifiedValue(ValueType.MAX)));
		valCol .setCellValueFactory(cdf -> new SimpleObjectProperty<AttributeValue>(cdf.getValue()));
		modCol .setCellValueFactory(cdf -> new SimpleIntegerProperty(0));
		sumCol .setCellValueFactory(cdf -> new SimpleIntegerProperty(cdf.getValue().getModifiedValue(ValueType.NATURAL)));
	}

	//--------------------------------------------------------------------
	private void initCellFactories() {
//		valCol.setCellFactory( (col) -> new NumericalValueTableCell<Attribute,AttributeValue,AttributeValue>(ctrl.getAttributeController()));
		recCol.setCellFactory(col -> new TableCell<AttributeValue,Boolean>(){
			public void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					if (item) {
						Label lb = new Label("\uE735");
						lb.setStyle("-fx-text-fill: recommendation; -fx-font-family: 'Segoe MDL2 Assets';");
						setGraphic(lb);
					} else
						setGraphic(null);
				}
			}
		});
		modCol.setCellFactory( col -> new TableCell<AttributeValue,Number>() {
			public void updateItem(Number item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || ((Integer)item)==0) {
					setGraphic(null);
				} else {
					Label lb = new Label(String.valueOf(item));
					lb.setStyle("-fx-font-weight: bold;");
//					Tooltip tt = new Tooltip(String.join("\n", getTableRow().getItem().getModifierExplanation()));
//					lb.setTooltip(tt);
					setGraphic(lb);
				}
			}
		});
	}
}
