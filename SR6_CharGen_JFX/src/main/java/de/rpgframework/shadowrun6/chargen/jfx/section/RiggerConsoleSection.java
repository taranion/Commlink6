package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 *
 */
public class RiggerConsoleSection extends GearSection {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(AdeptPowerSection.class.getPackageName()+".Section");
	private final static Predicate<ItemTemplate> SELECT_FILTER = (c) -> c.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue()==ItemType.ELECTRONICS && c.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue()==ItemSubType.RIGGER_CONSOLE ;
	private final static Predicate<CarriedItem<ItemTemplate>> SHOW_FILTER = (ci) -> {
		return ci.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue()==ItemSubType.RIGGER_CONSOLE;
	};

	private ChoiceBox<CarriedItem<ItemTemplate>> cbRig;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbRCC;

	//-------------------------------------------------------------------
	public RiggerConsoleSection() {
		super(ResourceI18N.get(RES, "section.rigging.title"), CarryMode.CARRIED, SELECT_FILTER, SHOW_FILTER);
		initDeviceSelector();
	}

	//-------------------------------------------------------------------
	private void initDeviceSelector() {
		cbRig = new ChoiceBox<>();
		cbRCC = new ChoiceBox<>();
		cbRig.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public String toString(CarriedItem<ItemTemplate> item) { return (item!=null)?item.getNameWithoutRating():"-"; }
			public CarriedItem<ItemTemplate> fromString(String string) { return null; }
		});
		cbRCC.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public String toString(CarriedItem<ItemTemplate> item) { return (item!=null)?item.getNameWithoutRating():"-"; }
			public CarriedItem<ItemTemplate> fromString(String string) { return null; }
		});

		Label hdAccess = new Label(ResourceI18N.get(RES, "section.rigging.rig"));
		Label hdDeck   = new Label(ResourceI18N.get(RES, "section.rigging.rcc"));
		hdAccess.setMinWidth(120);
		hdAccess.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdDeck.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);
		grid.add(hdAccess, 0, 0);
		grid.add(cbRig   , 1, 0);
		grid.add(hdDeck  , 0, 1);
		grid.add(cbRCC   , 1, 1);
		GridPane.setFillWidth(cbRig, true);
		GridPane.setFillWidth(cbRCC, true);

		super.setHeaderNode(grid);

		cbRig.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) o.removeFlag(SR6ItemFlag.PRIMARY);
			if (n!=null) n.addFlag(SR6ItemFlag.PRIMARY);
			logger.log(Level.INFO, "Set primary AS device to {0}", n);
			if (control!=null)
				control.runProcessors();
		});
		cbRCC.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) o.removeFlag(SR6ItemFlag.PRIMARY);
			if (n!=null) n.addFlag(SR6ItemFlag.PRIMARY);
			logger.log(Level.INFO, "Set primary DF device to {0}", n);
			if (control!=null)
				control.runProcessors();
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.jfx.section.GearSection#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		List<CarriedItem<ItemTemplate>> rigItems = new ArrayList<>();
		List<CarriedItem<ItemTemplate>> rccItems = new ArrayList<>();
		CarriedItem<ItemTemplate> primaryRig = null;
		CarriedItem<ItemTemplate> primaryRCC = null;
		if (model==null)
			return;
		for (CarriedItem<ItemTemplate> item : model.getCarriedItemsRecursive()) {
			ItemSubType subtype = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
			if ("control_rig".equals(item.getKey())) {
				rigItems.add(item);
				if (item.hasFlag(SR6ItemFlag.PRIMARY) || primaryRig==null)
					primaryRig = item;
			} else if (subtype==ItemSubType.RIGGER_CONSOLE) {
				rccItems.add(item);
				if (item.hasFlag(SR6ItemFlag.PRIMARY) || primaryRCC==null)
					primaryRCC = item;
			}
		}
		logger.log(Level.WARNING, "refresh with rigItems={0} and rccItems={1}", rigItems, rccItems);
		logger.log(Level.WARNING, "refresh with primaryRig={0} and primaryRCC={1}", primaryRig, primaryRCC);

		// Update, if necessary
		if (!cbRig.getItems().containsAll(rigItems)) {
			cbRig.getItems().setAll(rigItems);
		}
		if (!cbRCC.getItems().containsAll(rccItems)) {
			cbRCC.getItems().setAll(rccItems);
		}
		cbRig.setValue(primaryRig);
		cbRCC.setValue(primaryRCC);
	}

}
