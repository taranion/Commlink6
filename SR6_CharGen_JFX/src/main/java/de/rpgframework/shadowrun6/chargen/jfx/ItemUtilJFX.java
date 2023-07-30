package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.items.AmmunitionSlot;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun.persist.AmmunitionConverter;
import de.rpgframework.shadowrun.persist.FireModesConverter;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.persist.AttackRatingArrayConverter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class ItemUtilJFX {

	private final static Logger logger = System.getLogger("de.rpgframework.shadowrun6.jfx");

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(Shadowrun6Tools.class.getName());

	private static AttackRatingArrayConverter arConv = new AttackRatingArrayConverter();
	private static FireModesConverter firemodeConv = new FireModesConverter();
	private static AmmunitionConverter ammoConv = new AmmunitionConverter();

	//-------------------------------------------------------------------
	private static void addColumn(GridPane table, CarriedItem<ItemTemplate> carried, SR6ItemAttribute attrib, int width) {
		ItemAttributeValue<?> raw = carried.getAttributeRaw(attrib);
		if (raw==null)
			return;

		int x = table.getColumnCount();
		Label header  = new Label(attrib.getShortName());
		header.getStyleClass().add("table-head");
		header.setMaxWidth(Double.MAX_VALUE);
		header.setAlignment(Pos.CENTER);

		table.getColumnConstraints().add(new ColumnConstraints(width));
		table.add(header, x, 0);

		Label value= getItemAttributeLabel(carried, attrib);
		table.add(value, x, 1);
		GridPane.setHalignment(value, HPos.CENTER);
	}

	//-------------------------------------------------------------------
	private static void addColumn(GridPane table, String title, String value, int width) {
		int x = table.getColumnCount();
		Label header  = new Label(title);
		header.getStyleClass().add("table-head");
		header.setMaxWidth(Double.MAX_VALUE);
		header.setAlignment(Pos.CENTER);

		table.getColumnConstraints().add(new ColumnConstraints(width));
		table.add(header, x, 0);

		Label value2= new Label(value);
		table.add(value2, x, 1);
		GridPane.setHalignment(value2, HPos.CENTER);
	}

	//-------------------------------------------------------------------
	/**
	 * @param detailed Include augmentation, price and availability
	 */
	public static Node getItemInfoNode(CarriedItem<ItemTemplate> item, SR6CharacterController ctrl, boolean detailed) {
		logger.log(Level.DEBUG, "create InfoNode for "+item);
		Shadowrun6Character model = null; // ctrl.getModel();
		CarryMode carry = item.getCarryMode();

		GridPane table = new GridPane();

		// Weapons
		addColumn(table, item, SR6ItemAttribute.ATTACK_RATING, 90);
		addColumn(table, item, SR6ItemAttribute.DAMAGE, 50);
		addColumn(table, item, SR6ItemAttribute.FIREMODES, 100);
		addColumn(table, item, SR6ItemAttribute.AMMUNITION, 50);
		// Armor
		addColumn(table, item, SR6ItemAttribute.DEFENSE_PHYSICAL, 40);
		addColumn(table, item, SR6ItemAttribute.DEFENSE_SOCIAL, 50);
		// Vehicle
		addColumn(table, item, SR6ItemAttribute.HANDLING, 50);
		addColumn(table, item, SR6ItemAttribute.ACCELERATION, 50);
		addColumn(table, item, SR6ItemAttribute.TOPSPEED, 50);
		addColumn(table, item, SR6ItemAttribute.BODY    , 50);
		addColumn(table, item, SR6ItemAttribute.ARMOR   , 40);
		addColumn(table, item, SR6ItemAttribute.PILOT   , 40);
		addColumn(table, item, SR6ItemAttribute.SENSORS , 40);
		addColumn(table, item, SR6ItemAttribute.SEATS   , 40);
		// Matrix Device
		addColumn(table, item, SR6ItemAttribute.DEVICE_RATING, 40);
		addColumn(table, item, SR6ItemAttribute.ATTACK, 50);
		addColumn(table, item, SR6ItemAttribute.SLEAZE, 50);
		addColumn(table, item, SR6ItemAttribute.FIREWALL, 50);
		addColumn(table, item, SR6ItemAttribute.DATA_PROCESSING, 50);
		addColumn(table, item, SR6ItemAttribute.CONCURRENT_PROGRAMS, 40);
		// Generic
		if (detailed) {
			// Augmentations
			addColumn(table, item, SR6ItemAttribute.ESSENCECOST, 50);
			addColumn(table, item, SR6ItemAttribute.CAPACITY, 40);
			addColumn(table, item, SR6ItemAttribute.AVAILABILITY , 40);
			addColumn(table, item, SR6ItemAttribute.PRICE , 60);
		}


		VBox box = new VBox(10);
		box.setStyle("-fx-spacing:0.5em; ");
		box.setMaxWidth(Double.MAX_VALUE);

		if (!table.getChildren().isEmpty())
			box.getChildren().add(table);
		box.getChildren().add(getAccessoryInfoNode(item, ctrl, detailed));

		return box;
	}

	//-------------------------------------------------------------------
	/**
	 * @param detailed Include augmentation, price and availability
	 */
	public static Node getItemInfoNode(FocusValue item, SR6CharacterController ctrl) {
		logger.log(Level.WARNING, "create InfoNode for "+item);

		GridPane table = new GridPane();
		// Weapons
		addColumn(table, ResourceI18N.get(UI, "label.karma"), String.valueOf(item.getCostKarma()), 90);
		addColumn(table, ResourceI18N.get(UI, "label.nuyen"), String.valueOf(item.getCostNuyen()+" \u00A5"), 90);

		VBox box = new VBox(10);
		box.setStyle("-fx-spacing:0.5em; ");
		box.setMaxWidth(Double.MAX_VALUE);

		box.getChildren().add(table);

		return box;
	}

	//-------------------------------------------------------------------
	/**
	 * @param detailed Include augmentation, price and availability
	 */
	public static Node getAccessoryInfoNode(CarriedItem<ItemTemplate> item, SR6CharacterController ctrl, boolean detailed) {
		logger.log(Level.DEBUG, "create InfoNode for "+item);

		VBox box = new VBox(10);
		box.setStyle("-fx-spacing:0.5em; ");
		box.setMaxWidth(Double.MAX_VALUE);

		/*
		 * Accessories
		 */
		//		logger.warn(item.dump());
		//		logger.debug("Accessories of "+item+" are "+item.getAccessories());
		if (!item.getEffectiveAccessories().isEmpty()) {
			List<String> accessNames = new ArrayList<String>();
			item.getEffectiveAccessories().forEach(sub -> accessNames.add(sub.getNameWithRating()));
			//			lblAccessories.setText(String.join(", ", item.getAccessories()));
			Label heaModif = new Label(ResourceI18N.get(UI,"label.accessories")+": ");
			heaModif.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

			FlowPane flow = new FlowPane();
			flow.getChildren().add(heaModif);
			Iterator<String> it = accessNames.iterator();
			while (it.hasNext()) {
				Label lbl = new Label(it.next());
				if (it.hasNext())
					lbl.setText(lbl.getText()+",  ");
				flow.getChildren().add(lbl);
			}
			box.getChildren().add(flow);
			VBox.setMargin(flow, new Insets(5, 0, 5, 0));
		}

		/*
		 * WiFi Advantages
		 */
		if (detailed) {
			Collection<String> wifi = Shadowrun6Tools.getWiFiAdvantageStrings(item, Locale.getDefault());
			if (!wifi.isEmpty()) {
				box.getChildren().add(getWiFiAdvantagesNode(wifi));
			}
		}

		/*
		 * Modifications
		 */
//		List<Modification> mods = item.getCharacterModifications();
//		if (!mods.isEmpty()) {
//			box.getChildren().add(getModificationsNode(item));
//		}

		return box;
	}

	//-------------------------------------------------------------------
	public static VBox getItemInfoNode(ItemTemplate item, Shadowrun6Character model, CarryMode carry) {
		VBox box = new VBox(10);
		box.setStyle("-fx-spacing:0.5em; ");
		box.setMaxWidth(Double.MAX_VALUE);

		int nextCol = 0;

		GridPane table = new GridPane();

		Choice chRating = item.getChoice(ItemTemplate.UUID_RATING);
		if (chRating==null)
			chRating = item.getChoice("RATING");
		System.err.println("ItemUtilJFX: Choice = "+chRating);

		int[] ratings = null;
		if (chRating!=null) {
			if (chRating.getChoiceOptions()!=null) {
				ratings = new int[chRating.getChoiceOptions().length];
				int pos=0;
				for (String t : chRating.getChoiceOptions())
					ratings[pos++] = Integer.parseInt(t);
			} else {
				ratings = new int[chRating.getMaxFormula().getAsInteger()];
				for (int i=0; i<ratings.length; i++)
					ratings[i] = i+1;
			}
		}
		logger.log(Level.WARNING, "ratings = "+Arrays.toString(ratings));

		List<AGearData> possibilities = item.getPossibilities(carry);
		logger.log(Level.WARNING, "possibilities of {0} = {1}", carry,possibilities);
		if (possibilities.isEmpty())
			throw new IllegalArgumentException("CarryMode "+carry+" is not possible in "+item.getId());

		if (possibilities.size() > 1) {
			Label heaName = new Label(ResourceI18N.get(UI, "label.variant"));
			heaName.getStyleClass().add("table-head");
			heaName.setAlignment(Pos.CENTER);
			heaName .setMaxWidth(Double.MAX_VALUE);
			table.add(heaName, nextCol, 0);
			for (int i = 0; i < possibilities.size(); i++) {
				Label lbName = new Label();
				lbName.setAlignment(Pos.CENTER_LEFT);
				AGearData data = possibilities.get(i);
				if (data instanceof SR6PieceOfGearVariant) {
					lbName.setText(((SR6PieceOfGearVariant) data).getName(Locale.getDefault()));
				} else if (data instanceof ItemTemplate) {
					lbName.setText( ((ItemTemplate)data).getName(Locale.getDefault()));
				} else {
					lbName.setText( ResourceI18N.get(UI, "label.variant.standard"));
				}
				table.add(lbName, nextCol, 1 + i);
			}
			nextCol++;
		}

		// Flags to mark what details have been already added
		boolean augment = false;
		boolean matrix  = false;
		if (item.isAugmentation()) {
			addAugmentationColumns(item, model, carry, table, possibilities);
			augment = true;
		}
		if (item.isMatrixDevice()) {
			addMatrixDeviceColumns(item, model, carry, table, possibilities);
			matrix = true;
		}

		if (!augment && !matrix) {
			switch (item.getItemType(carry)) {
			case WEAPON_CLOSE_COMBAT:
			case WEAPON_FIREARMS:
			case WEAPON_RANGED:
			case WEAPON_SPECIAL:
				addWeaponColumns(item, model, carry, table, possibilities);
				break;
			default:
				logger.log(Level.WARNING,"No special display for itemtype "+item.getItemType(carry));
			}
		}

		nextCol = table.getColumnCount();
		Label heaAvail  = new Label(SR6ItemAttribute.AVAILABILITY.getShortName());
		heaAvail.getStyleClass().add("table-head");
		heaAvail.setAlignment(Pos.CENTER);
		heaAvail .setMaxWidth(Double.MAX_VALUE);
		Label heaPrice = new Label(SR6ItemAttribute.PRICE.getShortName());
		heaPrice.getStyleClass().add("table-head");
		heaPrice.setAlignment(Pos.CENTER);
		heaPrice .setMaxWidth(Double.MAX_VALUE);

		table.add(heaAvail, nextCol  , 0);
		table.add(heaPrice, nextCol+1, 0);

		if (possibilities.size() > 0) {
			for (int i = 0; i < possibilities.size(); i++) {
				// Availability
				Label lbAvail = new Label();
				lbAvail.setAlignment(Pos.CENTER);
				lbAvail .setMaxWidth(Double.MAX_VALUE);
				GridPane.setMargin(lbAvail, new Insets(0, 5, 0, 5));
				AGearData data = possibilities.get(i);
				ItemAttributeDefinition def = data.getAttribute(SR6ItemAttribute.AVAILABILITY);
				if (def==null)
					def = item.getAttribute(SR6ItemAttribute.AVAILABILITY);
				if (def !=null) {
					lbAvail.setText( translateVariables( def.getRawValue(), def.getLookupTable() ));
				}
				// Price
				Label lbPrice = new Label();
				lbPrice.setAlignment(Pos.CENTER_RIGHT);
				lbPrice .setMaxWidth(Double.MAX_VALUE);
				GridPane.setMargin(lbPrice, new Insets(0, 5, 0, 5));
				data = possibilities.get(i);
				def = data.getAttribute(SR6ItemAttribute.PRICE);
				if (def !=null) {
					lbPrice.setText( translateVariables( def.getRawValue(), def.getLookupTable() ));
				}
				table.add(lbAvail, nextCol, 1 + i);
				table.add(lbPrice, nextCol+1, 1 + i);
			}
		}


		box.getChildren().add(table);


//		// WiFi
//		if (!item.getWiFiAdvantageStrings().isEmpty()) {
//			box.getChildren().add(getWiFiAdvantagesNode(item));
//		}
//
//		// Requirements
//		for (Requirement req : item.getRequirements()) {
//			if (!ShadowrunTools.isRequirementMet(req, model)) {
//				Label notMet = new Label(ShadowrunTools.getRequirementString(req));
//				notMet.setStyle("-fx-text-fill: textcolor-stopper");
//				box.getChildren().add(notMet);
//			}
//		}


		return box;
	}

	//-------------------------------------------------------------------
	private static String translateVariables(String raw, String[] table) {
		String rtg = Shadowrun6Core.getI18nResources().getString("label.rating.short");
		if (raw.equals("$RATING") && table!=null)
			return table[0]+"+";
		if (raw.indexOf("$RATING")>-1)
			raw = raw.replace("$RATING", rtg);
		return raw;
	}

	//-------------------------------------------------------------------
	private static Node getWiFiAdvantagesNode(Collection<String> wifi) {
		Label heaWifi = new Label(ResourceI18N.get(UI,"label.wifiadvantage")+": ");
		heaWifi.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		Label lblWifi = new Label(String.join(",\n", wifi));
		lblWifi.setWrapText(true);
//		lblWifi.setStyle("-fx-max-width: 24em");

		VBox flow = new VBox();
		flow.getChildren().addAll(heaWifi, lblWifi);
		VBox.setMargin(flow, new Insets(5, 0, 5, 0));
		return flow;
	}

	//-------------------------------------------------------------------
	private static String makeAttributeValueString(ItemTemplate data, SR6ItemAttribute attrib) {
		ItemAttributeDefinition def = data.getAttribute(attrib);

		String raw = data.getAttribute(attrib).getRawValue();
		String[] table = data.getAttribute(attrib).getLookupTable();
		if (data.requiresVariant()) {
			int min = Integer.MAX_VALUE;
			for (SR6PieceOfGearVariant variant : data.getVariants()) {
				if (variant.getAttribute(attrib)!=null) {
					int t = variant.getAttribute(attrib).getDistributed();
					min = Math.min(min, t);
				}
			}
			return String.valueOf(min)+"+";
		}
		String rtg = Shadowrun6Core.getI18nResources().getString("label.rating.short");
		if (raw.equals("$RATING") && table!=null)
			return table[0]+"+";
		if (raw.indexOf("$RATING")>-1)
			raw = raw.replace("$RATING", rtg);
		return raw;
	}

	//-------------------------------------------------------------------
	private static Label getItemAttributeLabel(CarriedItem<ItemTemplate> item, SR6ItemAttribute attr) {
		Label ret = new Label("?");
		if (!item.hasAttribute(attr))
			return ret;

		Object obj = null;
		switch (attr) {
		case AMMUNITION:
			obj = item.getAsObject(attr).getModifiedValue();
			ret.setText( String.join(", ", ((List<AmmunitionSlot>)obj).stream().map(a -> a.toString()).toList()) );
			break;
		case AVAILABILITY:
			obj = item.getAsObject(attr).getModifiedValue();
			ret.setText(String.valueOf( (Availability)obj));
			break;
		case ATTACK_RATING:
			if (item.getAttributeRaw(attr) instanceof ItemAttributeObjectValue) {
				obj = item.getAsObject(attr).getModifiedValue();
				ret.setText(Shadowrun6Tools.getAttackRatingString( (int[])obj));
			} else {
				ret.setText( String.valueOf( item.getAsValue(attr).getModifiedValue() ));
			}
			break;
		case PRICE:
			int ny = item.getAsValue(attr).getModifiedValue();
			ret.setText( ny+" \u00A5");
			break;
		case ESSENCECOST:
			if (item.getAsFloat(attr)!=null) {
				float fVal = item.getAsFloat(attr).getModifiedValue();
				ret.setText( String.format("%.2f", fVal));
			}
			break;
		case QUALITY:
			if (item.getAsObject(attr)!=null) {
				obj = item.getAsObject(attr).getModifiedValue();
				ret.setText( ((AugmentationQuality)obj).getName());
			}
			break;
		case ACCELERATION:
		case DAMAGE:
		case HANDLING:
		case SPEED_INTERVAL:
			obj = item.getAsObject(attr).getModifiedValue();
			ret.setText(obj.toString());
			break;
		case FIREMODES:
			if (item.getAsObject(attr)!=null) {
				obj = item.getAsObject(attr).getModifiedValue();
				List<FireMode> fmodes = (List<FireMode>)obj;
				ret.setText(String.join(",", fmodes.stream().map(m -> m.getName(Locale.getDefault())).collect(Collectors.toList())));
			}
			break;
		case ARMOR:
		case ATTACK:
		case BODY:
		case CAPACITY:
		case CARGO:
		case CONCURRENT_PROGRAMS:
		case DATA_PROCESSING:
		case DEVICE_RATING:
		case FIREWALL:
		case PILOT:
		case SEATS:
		case SENSORS:
		case SLEAZE:
		case TOPSPEED:
			if (item.getAsValue(attr)==null) {
				ret.setText("-");
			} else {
				ret.setText(String.valueOf(item.getAsValue(attr).getModifiedValue()));
			}
			break;
		case DEFENSE_PHYSICAL:
		case DEFENSE_SOCIAL:
		case DEFENSE_MATRIX:
			if (item.getAsValue(attr)==null) {
				ret.setText("-");
			} else {
				int v = item.getAsValue(attr).getModifiedValue();
				if (v<0)
					ret.setText(String.valueOf(v));
				else
					ret.setText("+"+String.valueOf(v));
			}
			break;

		default:
			logger.log(Level.ERROR, "Don't know how to handle "+attr);
		}

//		if (obj!=null) {
//			if (!item.getAsObject(attr).getModifications().isEmpty()) {
//				ret.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
//				Tooltip tooltip = new Tooltip();
//				ret.setTooltip(tooltip);
//				switch (attr) {
//				case ATTACK_RATING:
//					tooltip.setText("?");
//					break;
//				default:
//					logger.log(Level.WARNING, "Don't know how to make tooltips for "+attr);
//				}
//			}
//		} else {
//			switch (attr) {
//			case ESSENCECOST:
//				if (item.getAsFloat(attr)!=null && !item.getAsFloat(attr).getModifications().isEmpty()) {
//					logger.log(Level.WARNING, "Don't know how to make tooltips for "+attr);
//					ret.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
//					Tooltip tooltip = new Tooltip();
//					ret.setTooltip(tooltip);
//				}
//				break;
//			default:
//				if (item.getAsValue(attr)!=null && !item.getAsValue(attr).getModifications().isEmpty()) {
//					logger.log(Level.WARNING, "Don't know how to make tooltips for "+attr+" / "+item.getAsValue(attr).getModifications());
//					ret.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
//					Tooltip tooltip = new Tooltip();
//					ret.setTooltip(tooltip);
//				}
//			}
//		}

		return ret;
	}

	//-------------------------------------------------------------------
	private static Label getItemAttributeLabel(ItemTemplate item, SR6ItemAttribute attr) {
		Label ret = new Label("?");
		if (item!=null) {
			Object obj = item.getAttribute(attr).getValue();
			ret.setText(String.valueOf(obj));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private static GridPane getWeaponNode(ItemTemplate item) {
		int COL_DMG  = 0;
		int COL_AR   = 1;
		int COL_MODE = 2;
		int COL_AMMO = 3;
		int COL_AVAIL= 4;
		int COL_COST = 5;

		Label heaAcc  = new Label(SR6ItemAttribute.ATTACK_RATING.getShortName());
		Label heaDmg  = new Label(SR6ItemAttribute.DAMAGE.getShortName());
		Label heaMode = new Label(SR6ItemAttribute.FIREMODES.getShortName());
		Label heaAmmo = new Label(SR6ItemAttribute.AMMUNITION.getShortName());
		Label heaAvail= new Label(SR6ItemAttribute.AVAILABILITY.getShortName());
		Label heaCost = new Label(SR6ItemAttribute.PRICE.getShortName());

		heaAcc  .getStyleClass().add("table-head");
		heaDmg  .getStyleClass().add("table-head");
		heaMode .getStyleClass().add("table-head");
		heaAmmo .getStyleClass().add("table-head");
		heaAvail.getStyleClass().add("table-head");
		heaCost .getStyleClass().add("table-head");

		heaAcc .setMaxWidth(Double.MAX_VALUE);
		heaDmg .setMaxWidth(Double.MAX_VALUE);
		heaMode.setMaxWidth(Double.MAX_VALUE);
		heaAmmo.setMaxWidth(Double.MAX_VALUE);
		heaAvail.setMaxWidth(Double.MAX_VALUE);
		heaCost.setMaxWidth(Double.MAX_VALUE);

		heaAcc .setAlignment(Pos.CENTER);
		heaDmg .setAlignment(Pos.CENTER);
		heaMode.setAlignment(Pos.CENTER);
		heaAmmo.setAlignment(Pos.CENTER);
		heaAvail.setAlignment(Pos.CENTER);
		heaCost.setAlignment(Pos.CENTER);

		GridPane grid = new GridPane();
		grid.setId("weapon-stats");
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Damage
		grid.getColumnConstraints().add(new ColumnConstraints( 80)); // Attack Rating
		grid.getColumnConstraints().add(new ColumnConstraints(100)); // Mode
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Ammo
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Availability
		grid.getColumnConstraints().add(new ColumnConstraints( 60)); // Cost
		grid.add(heaAcc , COL_AR, 0);
		grid.add(heaDmg , COL_DMG , 0);
		grid.add(heaMode, COL_MODE, 0);
		grid.add(heaAmmo, COL_AMMO, 0);
		grid.add(heaAvail, COL_AVAIL, 0);
		grid.add(heaCost, COL_COST, 0);

		// Data
		Label lblAcc  = new Label(Shadowrun6Tools.getAttackRatingString(item.getAttribute(SR6ItemAttribute.ATTACK_RATING).getValue()));
		Label lblDmg  = new Label(((Damage)item.getAttribute(SR6ItemAttribute.DAMAGE).getValue()).toString());
		Label lblMod  = new Label(String.join(", ", ((List<FireMode>)item.getAttribute(SR6ItemAttribute.FIREMODES).getValue()).stream().map(fm -> fm.getName(Locale.getDefault())).collect(Collectors.toList())));
//		Label lblAmm  = new Label(String.join(", ", item.getWeaponData().getAmmunitionNames()));
		Label lblAvail= new Label( ((Availability)item.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).toString());
		Label lblCost = new Label(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue());
		grid.add(lblAcc, COL_AR, 1);
		grid.add(lblDmg, COL_DMG , 1);
		grid.add(lblMod, COL_MODE, 1);
//		grid.add(lblAmm, COL_AMMO, 1);
		grid.add(lblAvail, COL_AVAIL, 1);
		grid.add(lblCost, COL_COST, 1);
		GridPane.setHalignment(lblAcc, HPos.CENTER);
		GridPane.setHalignment(lblDmg, HPos.CENTER);
		GridPane.setHalignment(lblMod, HPos.CENTER);
//		GridPane.setHalignment(lblAmm, HPos.CENTER);
		GridPane.setHalignment(lblAvail, HPos.CENTER);
		GridPane.setHalignment(lblCost, HPos.CENTER);

		return grid;
	}

	//-------------------------------------------------------------------
	private static void addWeaponColumns(CarriedItem<ItemTemplate> carried, Shadowrun6Character model, GridPane table) {
		if (carried==null)
			throw new NullPointerException("Empty item");

		addColumn(table, carried, SR6ItemAttribute.ATTACK_RATING, 90);
		addColumn(table, carried, SR6ItemAttribute.DAMAGE, 50);
		addColumn(table, carried, SR6ItemAttribute.FIREMODES, 100);
		addColumn(table, carried, SR6ItemAttribute.AMMUNITION, 50);
	}

	//-------------------------------------------------------------------
	private static void addWeaponColumns(ItemTemplate item, Shadowrun6Character model, CarryMode carry, GridPane table, List<AGearData> possibilities) {
		if (item==null)
			throw new NullPointerException("Empty item");
		logger.log(Level.WARNING,"No special display for itemtype "+item.getItemType(carry));

		boolean hasM = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.FIREMODES)!=null);
		boolean hasA = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.AMMUNITION)!=null);

		int COL_DMG  = 0;
		int COL_MOD  = (hasM)?(COL_DMG+1):COL_DMG;
		int COL_AR   = COL_MOD+1;
		int COL_AMMO = (hasA)?(COL_AR+1):COL_AR;

		Label heaDmg = new Label(SR6ItemAttribute.DAMAGE.getShortName());
		Label heaMod = new Label(SR6ItemAttribute.FIREMODES.getShortName());
		Label heaAR  = new Label(SR6ItemAttribute.ATTACK_RATING.getShortName());
		Label heaAmmo = new Label(SR6ItemAttribute.AMMUNITION.getShortName());

		heaDmg.getStyleClass().add("table-head");
		heaAR .getStyleClass().add("table-head");
		heaMod.getStyleClass().add("table-head");
		heaAmmo.getStyleClass().add("table-head");

		heaDmg.setMaxWidth(Double.MAX_VALUE);
		heaAR .setMaxWidth(Double.MAX_VALUE);
		heaMod.setMaxWidth(Double.MAX_VALUE);
		heaAmmo.setMaxWidth(Double.MAX_VALUE);

		heaDmg.setAlignment(Pos.CENTER);
		heaAR .setAlignment(Pos.CENTER);
		heaMod.setAlignment(Pos.CENTER);
		heaAmmo.setAlignment(Pos.CENTER);

		int startCol = table.getColumnCount();
		table.add(heaDmg, startCol+COL_DMG, 0);
		if (hasM) table.add(heaMod, startCol+COL_MOD, 0);
		table.add(heaAR, startCol+COL_AR, 0);
		if (hasA) table.add(heaAmmo, startCol+COL_AMMO, 0);

		// Now add data
		for (int i = 0; i < possibilities.size(); i++) {
			AGearData data = possibilities.get(i);
			table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.DAMAGE), startCol+COL_DMG, i+1);
			if (hasM) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.FIREMODES), startCol+COL_MOD, i+1);
			table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.ATTACK_RATING), startCol+COL_AR, i+1);
			if (hasA) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.AMMUNITION), startCol+COL_AMMO, i+1);
		}
	}

	//-------------------------------------------------------------------
	private static Node getMatrixDeviceNode(CarriedItem<ItemTemplate> item, Shadowrun6Character model) {
		VBox layout = new VBox();
		layout.setStyle("-fx-spacing: 0.5em");
		layout.setMaxWidth(Double.MAX_VALUE);

		int COL_DEV  = 0;
		int COL_ATT  = 1;
		int COL_SLZ  = 2;
		int COL_FIR  = 4;
		int COL_PRO  = 3;
		int COL_PRG  = 5;

		Label heaDev  = new Label(SR6ItemAttribute.DEVICE_RATING.getShortName());
		Label heaAtt  = new Label(SR6ItemAttribute.ATTACK.getShortName());
		Label heaSlz  = new Label(SR6ItemAttribute.SLEAZE.getShortName());
		Label heaFir  = new Label(SR6ItemAttribute.FIREWALL.getShortName());
		Label heaPro  = new Label(SR6ItemAttribute.DATA_PROCESSING.getShortName());
		Label heaPrg  = new Label(SR6ItemAttribute.CONCURRENT_PROGRAMS.getShortName());

		heaDev .getStyleClass().add("table-head");
		heaAtt .getStyleClass().add("table-head");
		heaSlz .getStyleClass().add("table-head");
		heaFir .getStyleClass().add("table-head");
		heaPro .getStyleClass().add("table-head");
		heaPrg .getStyleClass().add("table-head");

		heaDev .setMaxWidth(Double.MAX_VALUE);
		heaAtt .setMaxWidth(Double.MAX_VALUE);
		heaSlz .setMaxWidth(Double.MAX_VALUE);
		heaFir .setMaxWidth(Double.MAX_VALUE);
		heaPro .setMaxWidth(Double.MAX_VALUE);
		heaPrg .setMaxWidth(Double.MAX_VALUE);

		heaDev .setAlignment(Pos.CENTER);
		heaAtt .setAlignment(Pos.CENTER);
		heaSlz .setAlignment(Pos.CENTER);
		heaFir .setAlignment(Pos.CENTER);
		heaPro .setAlignment(Pos.CENTER);
		heaPrg .setAlignment(Pos.CENTER);

		GridPane grid = new GridPane();
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Device Rating
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Attack
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Sleaze
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Firewall
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Data processing
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Programs
		grid.add(heaDev, COL_DEV, 0);
		grid.add(heaAtt, COL_ATT , 0);
		grid.add(heaSlz, COL_SLZ, 0);
		grid.add(heaFir, COL_FIR, 0);
		grid.add(heaPro, COL_PRO, 0);
		grid.add(heaPrg, COL_PRG, 0);

		Label lblRat = getItemAttributeLabel(item, SR6ItemAttribute.DEVICE_RATING);
		Label lblAtt = getItemAttributeLabel(item, SR6ItemAttribute.ATTACK);
		Label lblSlz = getItemAttributeLabel(item, SR6ItemAttribute.SLEAZE);
		Label lblFir = getItemAttributeLabel(item, SR6ItemAttribute.FIREWALL);
		Label lblPro = getItemAttributeLabel(item, SR6ItemAttribute.DATA_PROCESSING);
		Label lblPrg = getItemAttributeLabel(item, SR6ItemAttribute.CONCURRENT_PROGRAMS);
		grid.add(lblRat, COL_DEV, 1);
		grid.add(lblAtt, COL_ATT , 1);
		grid.add(lblSlz, COL_SLZ, 1);
		grid.add(lblFir, COL_FIR, 1);
		grid.add(lblPro, COL_PRO, 1);
		grid.add(lblPrg, COL_PRG, 1);
		GridPane.setHalignment(lblRat, HPos.CENTER);
		GridPane.setHalignment(lblAtt, HPos.CENTER);
		GridPane.setHalignment(lblSlz, HPos.CENTER);
		GridPane.setHalignment(lblFir, HPos.CENTER);
		GridPane.setHalignment(lblPro, HPos.CENTER);
		GridPane.setHalignment(lblPrg, HPos.CENTER);

		layout.getChildren().add(grid);
		return layout;
	}

	//-------------------------------------------------------------------
	private static Label createLabelWithValue(ItemTemplate item, CarryMode carry, AGearData data, SR6ItemAttribute attrib) {
		Label lbAvail = new Label();
		lbAvail.setAlignment(Pos.CENTER);
		lbAvail .setMaxWidth(Double.MAX_VALUE);
		GridPane.setMargin(lbAvail, new Insets(0, 5, 0, 5));

		ItemAttributeDefinition def = data.getAttribute(attrib);
		if (def==null)
			def = item.getAttribute(attrib);
		logger.log(Level.WARNING, "Def2 {0}", def);
		if (attrib == SR6ItemAttribute.ESSENCECOST) {
			Usage usage = data.getUsage(carry);
			if (usage == null)
				usage = item.getUsage(carry);
			logger.log(Level.WARNING, "Def3 {0}", usage);
			if (usage != null && usage.getRawValue() != null) {
				lbAvail.setText(translateVariables(usage.getRawValue(), null));
			} else if (def != null) {
				lbAvail.setText(translateVariables(def.getRawValue(), def.getLookupTable()));
			}
		} else if (attrib == SR6ItemAttribute.ATTACK_RATING) {
			if (def!=null) {
				if (def.getRawValue().contains("$RATING")) {
					lbAvail.setText(translateVariables(def.getRawValue(), def.getLookupTable()));
				} else {
					try {
						lbAvail.setText(arConv.write( (int[])def.getValue()));
					} catch (Exception e) {
						logger.log(Level.ERROR, e);
					}
				}
			}
		} else if (attrib == SR6ItemAttribute.FIREMODES) {
			if (def!=null) {
				lbAvail.setText(firemodeConv.write(def.getValue()));
			}
		} else if (attrib == SR6ItemAttribute.AMMUNITION) {
			if (def!=null) {
				lbAvail.setText(ammoConv.write(def.getValue()));
			}
		} else {
			if (def!=null)
				lbAvail.setText(translateVariables(def.getRawValue(), def.getLookupTable()));
		}
		return lbAvail;
	}

	//-------------------------------------------------------------------
	private static Label createLabelWithValue(CarriedItem<ItemTemplate> item, SR6ItemAttribute attrib) {
		Label lbAvail = new Label();
		lbAvail.setAlignment(Pos.CENTER);
		lbAvail .setMaxWidth(Double.MAX_VALUE);
		GridPane.setMargin(lbAvail, new Insets(0, 5, 0, 5));

		ItemAttributeValue val = item.getAttributeRaw(attrib);
		if (val==null)
			return lbAvail;
		logger.log(Level.WARNING, "Def1 {0}", val);

		StringBuffer buf = new StringBuffer();
		if (val instanceof ItemAttributeFloatValue) {
			ItemAttributeFloatValue fVal = (ItemAttributeFloatValue)val;
			buf.append( String.format("%2.2f", fVal.getDistributed()) );
			if (fVal.getFloatModifier()!=0f) {
				buf.append(" ("+fVal.getModifiedValue()+")");
			}
		} else if (val instanceof ItemAttributeNumericalValue) {
			ItemAttributeNumericalValue fVal = (ItemAttributeNumericalValue)val;
			buf.append( String.format("%2d", fVal.getDistributed()) );
			if (fVal.getModifier()!=0) {
				buf.append(" ("+fVal.getModifiedValue()+")");
			}
		} else {
			logger.log(Level.WARNING, "ToDo: Deal with "+val.getClass());
		}
		lbAvail.setText(buf.toString());
		return lbAvail;
	}

	//-------------------------------------------------------------------
	private static void addMatrixDeviceColumns(ItemTemplate item, Shadowrun6Character model, CarryMode carry, GridPane table, List<AGearData> possibilities) {
		if (item==null)
			throw new NullPointerException("Empty item");

		int COL_DEV  = 0;
		int COL_ATT  = 1;
		int COL_SLZ  = 2;
		int COL_FIR  = 4;
		int COL_PRO  = 3;
		int COL_PRG  = 5;

		Label heaDev  = new Label(SR6ItemAttribute.DEVICE_RATING.getShortName()+" ");
		Label heaAtt  = new Label(SR6ItemAttribute.ATTACK.getShortName()+" ");
		Label heaSlz  = new Label(SR6ItemAttribute.SLEAZE.getShortName()+" ");
		Label heaFir  = new Label(SR6ItemAttribute.FIREWALL.getShortName()+" ");
		Label heaPro  = new Label(SR6ItemAttribute.DATA_PROCESSING.getShortName()+" ");
		Label heaPrg  = new Label(SR6ItemAttribute.CONCURRENT_PROGRAMS.getShortName()+" ");

		heaDev .getStyleClass().add("table-head");
		heaAtt .getStyleClass().add("table-head");
		heaSlz .getStyleClass().add("table-head");
		heaFir .getStyleClass().add("table-head");
		heaPro .getStyleClass().add("table-head");
		heaPrg .getStyleClass().add("table-head");

		heaDev .setMaxWidth(Double.MAX_VALUE);
		heaAtt .setMaxWidth(Double.MAX_VALUE);
		heaSlz .setMaxWidth(Double.MAX_VALUE);
		heaFir .setMaxWidth(Double.MAX_VALUE);
		heaPro .setMaxWidth(Double.MAX_VALUE);
		heaPrg .setMaxWidth(Double.MAX_VALUE);

		heaDev .setAlignment(Pos.CENTER);
		heaAtt .setAlignment(Pos.CENTER);
		heaSlz .setAlignment(Pos.CENTER);
		heaFir .setAlignment(Pos.CENTER);
		heaPro .setAlignment(Pos.CENTER);
		heaPrg .setAlignment(Pos.CENTER);

		int startCol = table.getColumnCount();
		boolean hasA = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.ATTACK)!=null);
		boolean hasS = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.SLEAZE)!=null);
		boolean hasD = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.DATA_PROCESSING)!=null);
		boolean hasF = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.FIREWALL)!=null);
		table.add(heaDev , startCol+COL_DEV , 0);
		if (hasA) table.add(heaAtt , startCol+COL_ATT , 0);
		if (hasS) table.add(heaSlz , startCol+COL_SLZ , 0);
		if (hasD) table.add(heaFir , startCol+COL_FIR , 0);
		if (hasF) table.add(heaPro , startCol+COL_PRO , 0);
		table.add(heaPrg , startCol+COL_PRG , 0);


		// Now add data
		for (int i = 0; i < possibilities.size(); i++) {
			AGearData data = possibilities.get(i);
			table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.DEVICE_RATING), startCol+COL_DEV, i+1);
			if (hasA) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.ATTACK), startCol+COL_ATT, i+1);
			if (hasS) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.SLEAZE), startCol+COL_SLZ, i+1);
			if (hasD) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.DATA_PROCESSING), startCol+COL_PRO, i+1);
			if (hasF) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.FIREWALL), startCol+COL_FIR, i+1);
			table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.CONCURRENT_PROGRAMS), startCol+COL_PRG, i+1);
		}
	}

	//-------------------------------------------------------------------
	private static GridPane getCyberdeckNode(ItemTemplate item) {
		int COL_DEV  = 0;
		int COL_ATT  = 1;
		int COL_PRO  = 2;

		Label heaDev  = new Label(SR6ItemAttribute.DEVICE_RATING.getShortName());
		Label heaAtt  = new Label(SR6ItemAttribute.ATTACK.getShortName()+"/"+SR6ItemAttribute.SLEAZE.getShortName());
		Label heaPro  = new Label(SR6ItemAttribute.CONCURRENT_PROGRAMS.getShortName());

		heaDev .getStyleClass().add("table-head");
		heaAtt .getStyleClass().add("table-head");
		heaPro .getStyleClass().add("table-head");

		heaDev .setMaxWidth(Double.MAX_VALUE);
		heaAtt .setMaxWidth(Double.MAX_VALUE);
		heaPro .setMaxWidth(Double.MAX_VALUE);

		heaDev .setAlignment(Pos.CENTER);
		heaAtt .setAlignment(Pos.CENTER);
		heaPro .setAlignment(Pos.CENTER);

		GridPane grid = new GridPane();
		//		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Accuracy
		grid.add(heaDev , COL_DEV , 0);
		grid.add(heaAtt , COL_ATT , 0);
		grid.add(heaPro , COL_PRO , 0);

		// Data
		Label lblDev = getItemAttributeLabel(item, SR6ItemAttribute.DEVICE_RATING);
		Label lblAtt = getItemAttributeLabel(item, SR6ItemAttribute.ATTACK);
		Label lblPro = getItemAttributeLabel(item, SR6ItemAttribute.SLEAZE);
		lblDev .setMaxWidth(Double.MAX_VALUE);
		lblAtt .setMaxWidth(Double.MAX_VALUE);
		lblPro .setMaxWidth(Double.MAX_VALUE);
		lblDev .setAlignment(Pos.CENTER);
		lblAtt .setAlignment(Pos.CENTER);
		lblPro .setAlignment(Pos.CENTER);
		grid.add(lblDev, COL_DEV , 1);
		grid.add(lblAtt, COL_ATT , 1);
		grid.add(lblPro, COL_PRO , 1);
		GridPane.setHalignment(heaDev, HPos.CENTER);

		return grid;
	}

	//-------------------------------------------------------------------
	private static Node getAugmentationNode(CarriedItem item, Shadowrun6Character model) {
		if (item==null)
			throw new NullPointerException("Empty item");

		Label heaQual  = new Label(SR6ItemAttribute.QUALITY.getShortName());
		Label heaEss   = new Label(SR6ItemAttribute.ESSENCECOST.getShortName());

		Label lblQual= getItemAttributeLabel(item, SR6ItemAttribute.QUALITY);
		Label lblEss = getItemAttributeLabel(item, SR6ItemAttribute.ESSENCECOST);

		Region spacing = new Region();
		spacing.setMaxWidth(Double.MAX_VALUE);

		HBox line = new HBox(5, heaQual, lblQual, spacing, heaEss, lblEss);
		line.setStyle("-fx-max-width: 18em");
		HBox.setHgrow(spacing, Priority.ALWAYS);

		return line;
	}

	//-------------------------------------------------------------------
	private static void addAugmentationColumns(ItemTemplate item, Shadowrun6Character model, CarryMode carry, GridPane table, List<AGearData> possibilities) {
		if (item==null)
			throw new NullPointerException("Empty item");

		boolean hasE = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.ESSENCECOST)!=null);
		boolean hasC = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.CAPACITY)!=null);
		boolean hasS = possibilities.stream().anyMatch(p -> p.getAttribute(SR6ItemAttribute.SIZE)!=null);

		int COL_ESS  = 0;
		int COL_CAP  = (hasE)?(COL_ESS+1):COL_ESS;
		int COL_SIZ  = (hasC)?(COL_CAP+1):COL_CAP;

		Label heaDev  = new Label(SR6ItemAttribute.ESSENCECOST.getShortName());
		Label heaCap = new Label(SR6ItemAttribute.CAPACITY.getShortName());
		Label heaPrg  = new Label(SR6ItemAttribute.SIZE.getShortName());

		heaDev .getStyleClass().add("table-head");
		heaPrg .getStyleClass().add("table-head");
		heaCap .getStyleClass().add("table-head");

		heaDev .setMaxWidth(Double.MAX_VALUE);
		heaPrg .setMaxWidth(Double.MAX_VALUE);
		heaCap .setMaxWidth(Double.MAX_VALUE);

		heaDev .setAlignment(Pos.CENTER);
		heaPrg .setAlignment(Pos.CENTER);
		heaCap .setAlignment(Pos.CENTER);

		int startCol = table.getColumnCount();
		logger.log(Level.ERROR, "ESS={0} CAP={1} SIZ={2}", hasE, hasC, hasS);
		logger.log(Level.ERROR, "ESS={0} CAP={1} SIZ={2}", COL_ESS, COL_CAP, COL_SIZ);
		logger.log(Level.ERROR, "Before "+table.getColumnCount());
		if (hasE) table.add(heaDev, startCol+COL_ESS, 0);
		if (hasC) table.add(heaCap, startCol+COL_CAP, 0);
		if (hasS) table.add(heaPrg, startCol+COL_SIZ, 0);
		logger.log(Level.ERROR, "After "+table.getColumnCount());

		// Now add data
		for (int i = 0; i < possibilities.size(); i++) {
			AGearData data = possibilities.get(i);
			logger.log(Level.ERROR, "A "+data.getAttribute(SR6ItemAttribute.CAPACITY));
			logger.log(Level.ERROR, "  A "+createLabelWithValue(item,carry,data,SR6ItemAttribute.CAPACITY));
			if (hasE) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.ESSENCECOST), startCol+COL_ESS, i+1);
			if (hasC) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.CAPACITY), startCol+COL_CAP, i+1);
			if (hasS) table.add(createLabelWithValue(item,carry,data,SR6ItemAttribute.SIZE), startCol+COL_SIZ, i+1);
		}
	}

	//-------------------------------------------------------------------
	private static void addAugmentationColumns(CarriedItem<ItemTemplate> carried, Shadowrun6Character model, GridPane table) {
		if (carried==null)
			throw new NullPointerException("Empty item");

		boolean hasE = carried.getAttributeRaw(SR6ItemAttribute.ESSENCECOST)!=null;
		boolean hasC = carried.getAttributeRaw(SR6ItemAttribute.CAPACITY)!=null;
		boolean hasS = carried.getAttributeRaw(SR6ItemAttribute.SIZE)!=null;

		int COL_ESS  = 0;
		int COL_CAP  = (hasE)?(COL_ESS+1):COL_ESS;
		int COL_SIZ  = (hasC)?(COL_CAP+1):COL_CAP;

		Label heaDev  = new Label(SR6ItemAttribute.ESSENCECOST.getShortName());
		Label heaCap = new Label(SR6ItemAttribute.CAPACITY.getShortName());
		Label heaPrg  = new Label(SR6ItemAttribute.SIZE.getShortName());

		heaDev .getStyleClass().add("table-head");
		heaPrg .getStyleClass().add("table-head");
		heaCap .getStyleClass().add("table-head");

		heaDev .setMaxWidth(Double.MAX_VALUE);
		heaPrg .setMaxWidth(Double.MAX_VALUE);
		heaCap .setMaxWidth(Double.MAX_VALUE);

		heaDev .setAlignment(Pos.CENTER);
		heaPrg .setAlignment(Pos.CENTER);
		heaCap .setAlignment(Pos.CENTER);

		int startCol = table.getColumnCount();
		if (hasE) table.add(heaDev, startCol+COL_ESS, 0);
		if (hasC) table.add(heaCap, startCol+COL_CAP, 0);
		if (hasS) table.add(heaPrg, startCol+COL_SIZ, 0);

		if (hasE) table.add(createLabelWithValue(carried, SR6ItemAttribute.ESSENCECOST), startCol+COL_ESS, 1);
		if (hasC) table.add(createLabelWithValue(carried, SR6ItemAttribute.CAPACITY), startCol+COL_CAP, 1);
		if (hasS) table.add(createLabelWithValue(carried, SR6ItemAttribute.SIZE), startCol+COL_SIZ, 1);
	}

	//-------------------------------------------------------------------
	private static void addVehicleColumns(CarriedItem carried, Shadowrun6Character model, GridPane table) {
		if (carried==null)
			throw new NullPointerException("Empty item");
		boolean hasS = carried.getAttributeRaw(SR6ItemAttribute.SEATS)!=null;

		int COL_HAND = 0;
		int COL_ACCL = 1;
		int COL_SPDI = 2;
		int COL_SPED = 3;
		int COL_BODY = 4;
		int COL_ARMR = 5;
		int COL_PILT = 6;
		int COL_SENS = 7;
		int COL_SEAT = 8;

		Label heaHand  = new Label(SR6ItemAttribute.HANDLING.getShortName());
		Label heaAccl   = new Label(SR6ItemAttribute.ACCELERATION.getShortName());
		Label heaSpdI = new Label(SR6ItemAttribute.SPEED_INTERVAL.getShortName());
		Label heaSpd  = new Label(SR6ItemAttribute.TOPSPEED.getShortName());
		Label heaBody  = new Label(SR6ItemAttribute.BODY.getShortName());
		Label heaArmr = new Label(SR6ItemAttribute.ARMOR.getShortName());
		Label heaPilt   = new Label(SR6ItemAttribute.PILOT.getShortName());
		Label heaAmmo = new Label(SR6ItemAttribute.SENSORS.getShortName());
		Label heaSeat = new Label(SR6ItemAttribute.SEATS.getShortName());

		heaHand.getStyleClass().add("table-head");
		heaAccl.getStyleClass().add("table-head");
		heaSpdI.getStyleClass().add("table-head");
		heaSpd .getStyleClass().add("table-head");
		heaBody.getStyleClass().add("table-head");
		heaArmr.getStyleClass().add("table-head");
		heaPilt.getStyleClass().add("table-head");
		heaAmmo.getStyleClass().add("table-head");
		heaSeat.getStyleClass().add("table-head");

		heaHand.setMaxWidth(Double.MAX_VALUE);
		heaAccl.setMaxWidth(Double.MAX_VALUE);
		heaSpdI.setMaxWidth(Double.MAX_VALUE);
		heaSpd .setMaxWidth(Double.MAX_VALUE);
		heaBody.setMaxWidth(Double.MAX_VALUE);
		heaArmr.setMaxWidth(Double.MAX_VALUE);
		heaPilt.setMaxWidth(Double.MAX_VALUE);
		heaAmmo.setMaxWidth(Double.MAX_VALUE);
		heaSeat.setMaxWidth(Double.MAX_VALUE);

		heaHand.setAlignment(Pos.CENTER);
		heaAccl.setAlignment(Pos.CENTER);
		heaSpdI.setAlignment(Pos.CENTER);
		heaSpd .setAlignment(Pos.CENTER);
		heaBody.setAlignment(Pos.CENTER);
		heaArmr.setAlignment(Pos.CENTER);
		heaPilt.setAlignment(Pos.CENTER);
		heaAmmo.setAlignment(Pos.CENTER);
		heaSeat.setAlignment(Pos.CENTER);

		table.getColumnConstraints().add(new ColumnConstraints( 50)); // Handling
		table.getColumnConstraints().add(new ColumnConstraints( 50)); // Acceleration
		table.getColumnConstraints().add(new ColumnConstraints( 50)); // Speed
		table.getColumnConstraints().add(new ColumnConstraints( 50)); // Body
		table.getColumnConstraints().add(new ColumnConstraints( 40)); // Armor
		table.getColumnConstraints().add(new ColumnConstraints( 40)); // Pilot
		table.getColumnConstraints().add(new ColumnConstraints( 40)); // Sensor
		if (hasS)
			table.getColumnConstraints().add(new ColumnConstraints( 40)); // Seats
		table.add(heaHand, COL_HAND, 0);
		table.add(heaAccl, COL_ACCL, 0);
		table.add(heaSpdI, COL_SPDI, 0);
		table.add(heaSpd , COL_SPED, 0);
		table.add(heaBody, COL_BODY , 0);
		table.add(heaArmr, COL_ARMR, 0);
		table.add(heaPilt, COL_PILT  , 0);
		table.add(heaAmmo, COL_SENS, 0);
		if (hasS)
			table.add(heaSeat, COL_SEAT, 0);

		Label lblHand= getItemAttributeLabel(carried, SR6ItemAttribute.HANDLING);
		Label lblAcc = getItemAttributeLabel(carried, SR6ItemAttribute.ACCELERATION);
		Label lblSpdI= getItemAttributeLabel(carried, SR6ItemAttribute.SPEED_INTERVAL);
		Label lblSpd = getItemAttributeLabel(carried, SR6ItemAttribute.TOPSPEED);
		Label lblRea = getItemAttributeLabel(carried, SR6ItemAttribute.BODY);
		Label lblMod = getItemAttributeLabel(carried, SR6ItemAttribute.ARMOR);
		Label lblRC  = getItemAttributeLabel(carried, SR6ItemAttribute.PILOT);
		Label lblAmm = getItemAttributeLabel(carried, SR6ItemAttribute.SENSORS);
		Label lblSea = getItemAttributeLabel(carried, SR6ItemAttribute.SEATS);
//		Skill skill = ShadowrunTools.getSkillForVehicle(item.getItem());
//		if (skill==null)
//			logger.warn("Failed to detect skill for "+item.getItem().getType());
//		Label lblSkil = new Label( (skill!=null)?skill.getName():"Skill not set"); //item.getItem().getVehicleData().getSkill().getName());
//		//		if (item.getItem().getVehicleData().getSpecialization()!=null)
//		//			lblSkil.setText(lblSkil.getText()+"/"+item.getItem().getWeaponData().getSpecialization().getName());
//		Label lblPool = new Label( (skill!=null)?(String.valueOf(model.getSkillPool(skill))):"TODO"); //String.valueOf(ShadowrunTools.getWeaponPool(model, item)));
//		logger.warn("Which skill is required for vehicle?");
		table.add(lblHand, COL_HAND, 1);
		table.add(lblAcc , COL_ACCL, 1);
		table.add(lblSpdI, COL_SPDI, 1);
		table.add(lblSpd, COL_SPED, 1);
		table.add(lblRea, COL_BODY , 1);
		table.add(lblMod, COL_ARMR, 1);
		table.add(lblRC , COL_PILT  , 1);
		table.add(lblAmm, COL_SENS, 1);
		table.add(lblSea, COL_SEAT, 1);
		GridPane.setHalignment(lblHand, HPos.CENTER);
		GridPane.setHalignment(lblAcc , HPos.CENTER);
		GridPane.setHalignment(lblSpd , HPos.CENTER);
		GridPane.setHalignment(lblSpdI, HPos.CENTER);
		GridPane.setHalignment(lblRea, HPos.CENTER);
		GridPane.setHalignment(lblMod, HPos.CENTER);
		GridPane.setHalignment(lblRC , HPos.CENTER);
		GridPane.setHalignment(lblAmm, HPos.CENTER);
		GridPane.setHalignment(lblSea, HPos.CENTER);
	}

}
