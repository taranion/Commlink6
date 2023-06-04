package de.rpgframework.shadowrun6.comlink.input;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.SearchableComboBox;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun.ANPC;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.CarriedItemListCell;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class GearInputPane extends VBox {

	private SearchableComboBox<ItemTemplate> cbItems;
	private Button btnAdd;
	private ListView<CarriedItem<ItemTemplate>> lvItems;

	private ANPC<SR6Skill,SR6SkillValue, SR6Spell> model;

	//-------------------------------------------------------------------
	public GearInputPane() {
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		cbItems = new SearchableComboBox<>();
		cbItems.setConverter(new StringConverter<ItemTemplate>() {
			public ItemTemplate fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			public String toString(ItemTemplate data) {
				if (data==null) return null;
				return data.getName();
			}});
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		cbItems.getItems().setAll(list);
		lvItems = new ListView<>();
		lvItems.setCellFactory( lv -> new CarriedItemListCell(null));

		btnAdd = new Button("Add");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		getChildren().addAll(new HBox(10,cbItems,btnAdd), lvItems);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnAdd.setOnAction(ev -> {
			ItemTemplate temp = cbItems.getValue();
			CarriedItem item = new CarriedItem(temp, null, CarryMode.CARRIED);
			lvItems.getItems().add(item);
			model.getGear().add(item);
		});
	}

	//-------------------------------------------------------------------
	public void setModel(ANPC<SR6Skill,SR6SkillValue, SR6Spell> model) {
		this.model = model;
		List<CarriedItem<ItemTemplate>> temp = new ArrayList<>();
		for (CarriedItem item : model.getGear()) {
			temp.add(item);
		}
		lvItems.getItems().setAll(temp);
	}
}
