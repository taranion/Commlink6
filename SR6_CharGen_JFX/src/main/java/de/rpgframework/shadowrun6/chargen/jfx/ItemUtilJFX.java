package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
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

	//-------------------------------------------------------------------
	public static Node getItemInfoNode(CarriedItem<ItemTemplate> item, SR6CharacterController ctrl) {
		logger.log(Level.WARNING, "create InfoNode for "+item);
		Shadowrun6Character model = ctrl.getModel();

		VBox box = new VBox(10);
		box.setStyle("-fx-spacing:0.5em; ");
		box.setMaxWidth(Double.MAX_VALUE);

		ItemTemplate raw = item.getModifyable();
		try {
			switch (raw.getItemType()) {
//			case AMMUNITION:
//				int units = item.getCount()*10;
//				switch (raw.getItemSubtype()) {
//				case AMMUNITION:
//					if (item.getChoice()!=null)
//						box.getChildren().add(new Label(ResourceI18N.format(UI, "iteminfonode.ammo", units, ((AmmunitionType)item.getChoice()).getName())));
//					break;
//				case ROCKETS:
//				} 
//				break;
//			case ARMOR:
//				box.getChildren().add(getArmorNode(item,model));
//				break;
			case WEAPON_CLOSE_COMBAT:
			case WEAPON_RANGED:
			case WEAPON_FIREARMS:
			case WEAPON_SPECIAL:
				box.getChildren().add(getWeaponNode(item,model));
				break;
//			case BIOWARE:
//			case CYBERWARE:
//				box.getChildren().add(getAugmentationNode(item, ctrl.getEquipmentController()));
//				break;
			case VEHICLES:
			case DRONE_LARGE: case DRONE_MEDIUM: case DRONE_MICRO: case DRONE_MINI: case DRONE_SMALL:
				box.getChildren().add(getVehicleNode(item,model));
				break;
			case ELECTRONICS:
				ItemSubType st = raw.getItemSubtype();
//				if (st==null)
//					st = item.getSubtype(null);
				if (st==null) {
					logger.log(Level.ERROR, "No subtype found for "+item);
					System.err.println("No subtype found for "+item);
				}
				if (st!=null) {
					switch (st) {
					case COMMLINK:
					case RIGGER_CONSOLE:
					case CYBERDECK:
						box.getChildren().add(getMatrixDeviceNode(item,model));
						break;
					default:
						logger.log(Level.WARNING,"No special display for "+ItemType.ELECTRONICS+"/"+st);
					}
				}
				break;
			case CYBERWARE:
			case BIOWARE:
				box.getChildren().add(getAugmentationNode(item,model));
				break;
			default:
				logger.log(Level.ERROR,"No special handling for "+raw.getItemType()+"/"+raw.getItemSubtype()+": "+raw.getId());
			}
		} catch (Exception e) {
			box.getChildren().add(new Label("ERROR: "+e));
			logger.log(Level.ERROR,"Failed getting item data",e);
		}

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
//		if (!item.getWiFiAdvantageStringRecursivly().isEmpty()) {
//			box.getChildren().add(getWiFiAdvantagesNode(item));
//		}

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
	public static VBox getItemInfoNode(ItemTemplate item, ShadowrunCharacter model) {
		VBox box = new VBox(10);
		box.setStyle("-fx-spacing:0.5em; ");
		box.setMaxWidth(Double.MAX_VALUE);

		switch (item.getItemType()) {
		case WEAPON_CLOSE_COMBAT:
		case WEAPON_RANGED:
		case WEAPON_FIREARMS:
		case WEAPON_SPECIAL:
			box.getChildren().add(getWeaponNode(item));
			break;
//		case BIOWARE:
//		case CYBERWARE:
//			box.getChildren().add(getAugmentationNode(item));
//			break;
//		case ARMOR:
//			box.getChildren().add(getArmorNode(item));
//			break;
//		case VEHICLES:
//		case DRONE_MICRO:
//		case DRONE_MINI:
//		case DRONE_SMALL:
//		case DRONE_MEDIUM:
//		case DRONE_LARGE:
//			box.getChildren().add(getVehicleNode(item));
//			break;
		case ELECTRONICS:
			ItemSubType st = item.getItemSubtype();
//			if (st==null)
//				st = item.getSubtype(null);
			if (st==null) {
				logger.log(Level.ERROR, "No subtype found for "+item);
				System.err.println("No subtype found for "+item);
			}
			if (st!=null) {
				switch (st) {
				case COMMLINK:
				case RIGGER_CONSOLE:
					box.getChildren().add(getMatrixDeviceNode(item));
					break;
				case CYBERDECK:
					box.getChildren().add(getCyberdeckNode(item));
					break;
				default:
					logger.log(Level.WARNING,"No special display for "+ItemType.ELECTRONICS+"/"+st);
				}
			}
			break;
		default:
			logger.log(Level.WARNING,"No special display for "+item.getItemType());
//			VBox modBox = new VBox(3);
//			for (Modification mod : item.getModifications()) {
//				modBox.getChildren().add(new Label(ShadowrunTools.getModificationString(mod)));
//			}
//			box.getChildren().add(modBox);
		}

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
	private static Label getItemAttributeLabel(CarriedItem<ItemTemplate> item, SR6ItemAttribute attr) {
		Label ret = new Label("?");
		Object obj = null;
		switch (attr) {
		case ATTACK_RATING:
			obj = item.getAsObject(attr).getModifiedValue();
			ret.setText(Shadowrun6Tools.getAttackRatingString( (int[])obj));
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
		default:
			logger.log(Level.ERROR, "Don't know how to handle "+attr);
		}
		
		if (obj!=null) {
			if (!item.getAsObject(attr).getModifications().isEmpty()) {
				logger.log(Level.WARNING, "Don't know how to make tooltips for "+attr);
				ret.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
				Tooltip tooltip = new Tooltip();
				ret.setTooltip(tooltip);
			}
		}
		
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
	private static Node getWeaponNode(CarriedItem<ItemTemplate> item, Shadowrun6Character model) {
		VBox layout = new VBox();
		layout.setStyle("-fx-spacing: 0.5em");
		layout.setMaxWidth(Double.MAX_VALUE);

		int COL_ACCU = 0;
		int COL_DMG  = 1;
		int COL_MODE = 2;
		int COL_AMMO = 3;
		int COL_POOL = 4;

		Label heaAcc  = new Label(SR6ItemAttribute.ATTACK_RATING.getShortName());
		Label heaDmg  = new Label(SR6ItemAttribute.DAMAGE.getShortName());
		Label heaMode = new Label(SR6ItemAttribute.FIREMODES.getShortName());
		Label heaAmmo = new Label(SR6ItemAttribute.AMMUNITION.getShortName());
		Label heaPool = new Label(ResourceI18N.get(UI,"label.pool"));

		heaAcc .getStyleClass().add("table-head");
		heaDmg .getStyleClass().add("table-head");
		heaMode.getStyleClass().add("table-head");
		heaAmmo.getStyleClass().add("table-head");
		heaPool.getStyleClass().add("table-head");

		heaAcc .setMaxWidth(Double.MAX_VALUE);
		heaDmg .setMaxWidth(Double.MAX_VALUE);
		heaMode.setMaxWidth(Double.MAX_VALUE);
		heaAmmo.setMaxWidth(Double.MAX_VALUE);
		heaPool.setMaxWidth(Double.MAX_VALUE);

		heaAcc .setAlignment(Pos.CENTER);
		heaDmg .setAlignment(Pos.CENTER);
		heaMode.setAlignment(Pos.CENTER);
		heaAmmo.setAlignment(Pos.CENTER);
		heaPool.setAlignment(Pos.CENTER);

		GridPane grid = new GridPane();
		grid.getColumnConstraints().add(new ColumnConstraints( 90)); // Attack Rating
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Damage
		grid.getColumnConstraints().add(new ColumnConstraints(100)); // Mode
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Ammo
		grid.getColumnConstraints().add(new ColumnConstraints( 40)); // Pool
		grid.add(heaAcc , COL_ACCU, 0);
		grid.add(heaDmg , COL_DMG , 0);
		grid.add(heaMode, COL_MODE, 0);
		grid.add(heaAmmo, COL_AMMO, 0);
		grid.add(heaPool, COL_POOL, 0);

		Label lblAcc = getItemAttributeLabel(item, SR6ItemAttribute.ATTACK_RATING);
		Label lblDmg = getItemAttributeLabel(item, SR6ItemAttribute.DAMAGE);
		Label lblMod = getItemAttributeLabel(item, SR6ItemAttribute.FIREMODES);
		Label lblAmm = getItemAttributeLabel(item, SR6ItemAttribute.AMMUNITION);
//		if (raw.getWeaponData().getSpecialization()!=null)
//			lblSkil.setText(lblSkil.getText()+"/"+raw.getWeaponData().getSpecialization().getName());
//		Label lblPool = new Label(String.valueOf(Shadowrun6Tools.getWeaponPool(model, item)));
		Label lblPool = new Label("Pool?");
//		Tooltip tt = new Tooltip(Shadowrun6Tools.getWeaponPoolExplanation(model, item));
//		lblPool.setTooltip(tt);
		grid.add(lblAcc, COL_ACCU, 1);
		grid.add(lblDmg, COL_DMG , 1);
		grid.add(lblMod, COL_MODE, 1);
		grid.add(lblAmm, COL_AMMO, 1);
		grid.add(lblPool, COL_POOL, 1);
		GridPane.setHalignment(lblAcc, HPos.CENTER);
		GridPane.setHalignment(lblDmg, HPos.CENTER);
		GridPane.setHalignment(lblMod, HPos.CENTER);
		GridPane.setHalignment(lblAmm, HPos.CENTER);
		GridPane.setHalignment(lblPool, HPos.CENTER);

		layout.getChildren().add(grid);
		return layout;
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
	private static GridPane getMatrixDeviceNode(ItemTemplate item) {
		int COL_DEV  = 0;
//		int COL_ATT  = 1;
//		int COL_SLZ  = 2;
		int COL_FIR  = 4;
		int COL_PRO  = 3;
		int COL_PRG  = 5;

		Label heaDev  = new Label(SR6ItemAttribute.DEVICE_RATING.getShortName());
//		Label heaAtt  = new Label(ItemAttribute.ATTACK.getShortName());
//		Label heaSlz  = new Label(ItemAttribute.SLEAZE.getShortName());
		Label heaFir  = new Label(SR6ItemAttribute.FIREWALL.getShortName());
		Label heaPro  = new Label(SR6ItemAttribute.DATA_PROCESSING.getShortName());
		Label heaPrg  = new Label(SR6ItemAttribute.CONCURRENT_PROGRAMS.getShortName());

		heaDev .getStyleClass().add("table-head");
//		heaAtt .getStyleClass().add("table-head");
//		heaSlz .getStyleClass().add("table-head");
		heaFir .getStyleClass().add("table-head");
		heaPro .getStyleClass().add("table-head");
		heaPrg .getStyleClass().add("table-head");

		heaDev .setMaxWidth(Double.MAX_VALUE);
//		heaAtt .setMaxWidth(Double.MAX_VALUE);
//		heaSlz .setMaxWidth(Double.MAX_VALUE);
		heaFir .setMaxWidth(Double.MAX_VALUE);
		heaPro .setMaxWidth(Double.MAX_VALUE);
		heaPrg .setMaxWidth(Double.MAX_VALUE);

		heaDev .setAlignment(Pos.CENTER);
//		heaAtt .setAlignment(Pos.CENTER);
//		heaSlz .setAlignment(Pos.CENTER);
		heaFir .setAlignment(Pos.CENTER);
		heaPro .setAlignment(Pos.CENTER);
		heaPrg .setAlignment(Pos.CENTER);

		GridPane grid = new GridPane();
		//		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Accuracy
		grid.add(heaDev , COL_DEV , 0);
//		grid.add(heaAtt , COL_ATT , 0);
//		grid.add(heaSlz , COL_SLZ , 0);
		grid.add(heaFir , COL_FIR , 0);
		grid.add(heaPro , COL_PRO , 0);
		grid.add(heaPrg , COL_PRG , 0);

		// Data
		Label lblDev = getItemAttributeLabel(item, SR6ItemAttribute.DEVICE_RATING);
		Label lblFir = getItemAttributeLabel(item, SR6ItemAttribute.FIREWALL);
		Label lblPro = getItemAttributeLabel(item, SR6ItemAttribute.DATA_PROCESSING);
		Label lblPrg = getItemAttributeLabel(item, SR6ItemAttribute.CONCURRENT_PROGRAMS);
		grid.add(lblDev, COL_DEV , 1);
		grid.add(lblFir, COL_FIR , 1);
		grid.add(lblPro, COL_PRO , 1);
		grid.add(lblPrg, COL_PRG , 1);
		GridPane.setHalignment(heaDev, HPos.CENTER);

		return grid;
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
	private static Node getAugmentationNode(CarriedItem item, ShadowrunCharacter model) {
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
	private static Node getVehicleNode(CarriedItem item, ShadowrunCharacter model) {
		if (item==null)
			throw new NullPointerException("Empty item");
		VBox layout = new VBox();
		layout.setStyle("-fx-spacing: 0.5em");
		layout.setMaxWidth(Double.MAX_VALUE);

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

		GridPane grid = new GridPane();
		grid.getColumnConstraints().add(new ColumnConstraints( 60)); // Handling
		grid.getColumnConstraints().add(new ColumnConstraints( 60)); // Acceleration
		grid.getColumnConstraints().add(new ColumnConstraints( 60)); // Speed
		grid.getColumnConstraints().add(new ColumnConstraints( 50)); // Body
		grid.getColumnConstraints().add(new ColumnConstraints( 40)); // Armor
		grid.getColumnConstraints().add(new ColumnConstraints( 40)); // Pilot
		grid.getColumnConstraints().add(new ColumnConstraints( 40)); // Sensor
		grid.getColumnConstraints().add(new ColumnConstraints( 40)); // Seats
		grid.add(heaHand, COL_HAND, 0);
		grid.add(heaAccl, COL_ACCL, 0);
		grid.add(heaSpdI, COL_SPDI, 0);
		grid.add(heaSpd , COL_SPED, 0);
		grid.add(heaBody, COL_BODY , 0);
		grid.add(heaArmr, COL_ARMR, 0);
		grid.add(heaPilt, COL_PILT  , 0);
		grid.add(heaAmmo, COL_SENS, 0);
		grid.add(heaSeat, COL_SEAT, 0);

		Label lblHand= getItemAttributeLabel(item, SR6ItemAttribute.HANDLING);
		Label lblAcc = getItemAttributeLabel(item, SR6ItemAttribute.ACCELERATION);
		Label lblSpdI= getItemAttributeLabel(item, SR6ItemAttribute.SPEED_INTERVAL);
		Label lblSpd = getItemAttributeLabel(item, SR6ItemAttribute.TOPSPEED);
		Label lblRea = getItemAttributeLabel(item, SR6ItemAttribute.BODY);
		Label lblMod = getItemAttributeLabel(item, SR6ItemAttribute.ARMOR);
		Label lblRC  = getItemAttributeLabel(item, SR6ItemAttribute.PILOT);
		Label lblAmm = getItemAttributeLabel(item, SR6ItemAttribute.SENSORS);
		Label lblSea = getItemAttributeLabel(item, SR6ItemAttribute.SEATS);
//		Skill skill = ShadowrunTools.getSkillForVehicle(item.getItem());
//		if (skill==null)
//			logger.warn("Failed to detect skill for "+item.getItem().getType());
//		Label lblSkil = new Label( (skill!=null)?skill.getName():"Skill not set"); //item.getItem().getVehicleData().getSkill().getName());
//		//		if (item.getItem().getVehicleData().getSpecialization()!=null)
//		//			lblSkil.setText(lblSkil.getText()+"/"+item.getItem().getWeaponData().getSpecialization().getName());
//		Label lblPool = new Label( (skill!=null)?(String.valueOf(model.getSkillPool(skill))):"TODO"); //String.valueOf(ShadowrunTools.getWeaponPool(model, item)));
//		logger.warn("Which skill is required for vehicle?");
		grid.add(lblHand, COL_HAND, 1);
		grid.add(lblAcc , COL_ACCL, 1);
		grid.add(lblSpdI, COL_SPDI, 1);
		grid.add(lblSpd, COL_SPED, 1);
		grid.add(lblRea, COL_BODY , 1);
		grid.add(lblMod, COL_ARMR, 1);
		grid.add(lblRC , COL_PILT  , 1);
		grid.add(lblAmm, COL_SENS, 1);
		grid.add(lblSea, COL_SEAT, 1);
		GridPane.setHalignment(lblHand, HPos.CENTER);
		GridPane.setHalignment(lblAcc , HPos.CENTER);
		GridPane.setHalignment(lblSpd , HPos.CENTER);
		GridPane.setHalignment(lblSpdI, HPos.CENTER);
		GridPane.setHalignment(lblRea, HPos.CENTER);
		GridPane.setHalignment(lblMod, HPos.CENTER);
		GridPane.setHalignment(lblRC , HPos.CENTER);
		GridPane.setHalignment(lblAmm, HPos.CENTER);
		GridPane.setHalignment(lblSea, HPos.CENTER);

		layout.getChildren().add(grid);
		return layout;
	}

}
