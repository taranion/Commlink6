package de.rpgframework.shadowrun6.chargen.jfx.dialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.ScreenManagerProvider;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.image.Image;

/**
 * @author Stefan Prelle
 *
 */
public class EditCarriedItemDialog extends ACarriedItemPage<ItemTemplate, ItemHook, AvailableSlot> {

//	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditCarriedItemDialog.class.getName());

	private final static Logger logger = System.getLogger(EditCarriedItemDialog.class.getPackageName());

	private SR6CharacterController control;

	//--------------------------------------------------------------------
	public EditCarriedItemDialog(SR6CharacterController ctrl, CarriedItem data, ScreenManagerProvider prov) {
		super(ctrl, data, prov);
		this.control = ctrl;
	}

	//--------------------------------------------------------------------
	protected int getIndexFor(AvailableSlot slot) {
		boolean large = false;
		int index = -1;
		switch (slot.getHook()) {
		// Available slots: 2-6, 8-11 (1 = data, 7 = name and notes, 12 = cost and number)

		// Melee weapons
		// Slot 2 is used for modifications
		case MELEE_EXTERNAL    : index= 9; break; 
		// Firearms
		// Slot 2 is used for modifications
		case UNDER             : index= 3; break;
		case UNDER_WEAPON_MOUNT: index= 3; break; // Ok to overlap with UNDER as UNDER_WEAPON_MOUNT replaces UNDER
		case STOCK             : index= 4; break;
		// case INTERNAL          : index= 4; break; // This slot is no longer used
		case WEAPON_SECURITY   : index= 5; break; // Overlaps with SIDE_R, ideally choice of WEAPON_SECURITY should be a mod inside the modification, not a new slot
		case SIDE_R            : index= 5; break; 
		case SIDE_L            : index= 6; break;
		// case SMARTGUN          : index= 6; break; // This slot is no longer used
		case OPTICAL           : index= 8; break; // Created by smartgun_system, also affects ELECTRONICS OPTICAL
//		case RANGED_EXTERNAL   : index= 9; break;
		case FIREARMS_EXTERNAL : index= 9; break;
		case TOP               : index=10; break;
		case BARREL            : index=11; break;

		// Armor
		case ARMOR             : index= 2; break;		
		case ARMOR_REACTIVE    : index= 3; break;		
		case ARMOR_ADDITION    : index= 4; break;		
		case ARMOR_MEMS        : index= 8; break;		
		case HELMET_ACCESSORY  : index= 9; break;		

		// Cyberware
		case HEADWARE_IMPLANT   : index= 2; break;		
		case SKILLJACK          : index= 3; break;		
		case CYBERLIMB_IMPLANT  : index= 4; break;		
		case CYBEREYE_IMPLANT   : index= 8; break;		
		case CYBEREAR_IMPLANT   : index= 9; break;		

		// Vehicles
//		case VEHICLE_PROTECTION : index= 3; break; 
		case VEHICLE_CHASSIS    : index= 2; break;
		case VEHICLE_ELECTRONICS: index= 3; break;
		case VEHICLE_POWERTRAIN : index= 4; break;
		// note: SOFTWARE_DRONE uses slot 5, see Kommlinks
		case VEHICLE_TIRES      : index= 6; break;
		case VEHICLE_ACCESSORY  : index= 8; break;
		case VEHICLE_BODY       : index= 9; break;
		case VEHICLE_CF         : index=10; break; // place for VEHICLE_WEAPON not needed, as those are inside hardpoints (VEHICLE_BODY)
//		case VEHICLE_COSMETICS  : index=11; break;

		// Kommlinks/Rigger Consoles/Decks/Tac-Nets
		case ELECTRONIC_ACCESSORY: index= 3; break;
		case SOFTWARE            : index= 8; break;

		// Devices
		// note: OPTICAL uses slot 8, see Firearms
		case AUDIO             : index= 9; break;		
		case SENSOR_HOUSING    : index= 2; break;
		case SENSOR_FUNCTION   : index= 3; break; 
		
		// Instruments
		case INSTRUMENT_SLOT  : index= 2; break;
		case INSTRUMENT_WEAPON: index= 9; break;

		// Procam
		// note: OPTICAL uses slot 8, see Firearms
		case PROCAM_SLOT      : index= 3; break;
		}
		
		return index;
	}

	//--------------------------------------------------------------------
	protected void updateImage()  {
		
		logger.log(Level.INFO, "refresh");
		ItemSubType sub = selectedItem.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		ItemType type = selectedItem.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();

		if (selectedItem.getImage() != null) {
			Image img = new Image(new ByteArrayInputStream(selectedItem.getImage()));
			view.setImage(img);
			view.setEditButton(true);
		} else {
			String imgName = "Placeholder_" + sub + ".png";
			switch (sub) {
			case BLADES:
			case WHIPS:
			case PISTOLS_HEAVY:
			case PISTOLS_LIGHT:
			case MACHINE_PISTOLS:
			case THROWERS:
				view.setEditButton(true);
				break;
			case LMG:
			case MMG:
			case HMG:
				imgName = "Placeholder_Machine_Guns.png";
				view.setEditButton(true);
				break;
			case MICRODRONES:
			case MINIDRONES:
			case SMALL_DRONES:
			case MEDIUM_DRONES:
			case LARGE_DRONES:
				imgName = "Placeholder_Drone.png";
				view.setEditButton(true);
				break;
			default:
				if (List.of(ItemType.vehicleTypes()).contains(type)) {
					view.setEditButton(true);
				}
				if (List.of(ItemType.droneTypes()).contains(type)) {
					imgName = "Placeholder_Drone.png";
					view.setEditButton(true);
				}
				if (List.of(ItemType.weaponTypes()).contains(type)) {
					view.setEditButton(true);
				}
			}
			InputStream ins = EditCarriedItemDialog.class.getResourceAsStream(imgName);
			if (ins != null) {
				view.setImage(new Image(ins));
			} else {
				view.setImage(null);
			}
		}
		
//		switch (sub) {
////		case WEAPON_CLOSE_COMBAT:
////		case WEAPON_RANGED:
////		case WEAPON_FIREARMS:
//		case AMMUNITION: // to enable modifications for grenades
////		case WEAPON_SPECIAL:
//			view.setName(2, ResourceI18N.get(UI, "label.modifications"));
//			view.getList(2).setAll(selectedItem.getEnhancements());
//			view.setCellFactory(2, (lv)-> new ItemEnhancementValueObjectListCell(selectedItem,control.getEquipmentController()));
//			view.setOnAddAction(2, ev -> addModificationClicked());
//			break;
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @param slot
	 * @return
	 */
	@Override
	protected void addClicked(ItemHook slot) {
		logger.log(Level.INFO, "addClicked(--"+slot+")");
		
		List<ItemTemplate> data = control.getEquipmentController().getEmbeddableIn(selectedItem, slot);
		logger.log(Level.INFO, "getEmbeddableIn("+selectedItem+") returns "+data.size()+" elements");

		Predicate<ItemTemplate> templateFilter = i -> data.contains(i);
		
		ItemTemplateSelector selector = new ItemTemplateSelector(control, CarryMode.EMBEDDED, templateFilter);
		ManagedDialog dialog = new ManagedDialog(
				ResourceI18N.get(UI, "dialog.add.accessory.title"), 
				selector,
				CloseType.CANCEL, CloseType.OK);
		CloseType closed = getAppLayout().getApplication().showAlertAndCall(dialog, null);
		logger.log(Level.INFO, "Closed via "+closed);
//		if (closed==CloseType.OK) {
//			for (ItemTemplate master : dialog.getSelection()) {
//     			logger.log(Level.DEBUG, "add accessory "+master+" to "+selectedItem+" in slot "+slot);
//     			logger.log(Level.DEBUG, "embed "+master+" in "+selectedItem);
//     			UseAs usage = master.getUsageFor(slot);
//     			logger.log(Level.INFO, "use for slot "+slot+" is "+usage);
//     			ItemType useAs = (usage==null)?master.getNonAccessoryType():usage.getType();
//     			logger.log(Level.INFO, "ItemType is "+useAs);
//     			List<SelectionOptionType> options = control.getEquipmentController().getOptions(master, usage);
//				try {
//					if (!options.isEmpty()) {
//						logger.log(Level.INFO, "ask options");
//						Platform.runLater( () -> {
//							SelectionOption[] opts = ItemUtilJFX.askOptionsFor( provider, control.getEquipmentController(), master, selectedItem, useAs, selectedItem.getSlot(slot).getFreeCapacity(), usage);
//							CarriedItem added = control.getEquipmentController().embed(selectedItem, master, slot, opts);
//							logger.log(Level.INFO, "After adding =============="+selectedItem.getSlot(slot));
//							afterTryingToAdd(master, added);
//						});
//					} else {         			
//						logger.log(Level.INFO, "dont ask options");
//	    				CarriedItem added = control.getEquipmentController().embed(selectedItem, master, slot);
//						afterTryingToAdd(master, added);
//					}
//				} catch (Exception e) {
//					logger.log(Level.ERROR, "Failed asking for Options",e);
//					StringWriter out = new StringWriter();
//					e.printStackTrace(new PrintWriter(out));
//					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE,2,out.toString());
//				}
//			}
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage#getNuyenResolver()
	 */
	@Override
	protected Function<CarriedItem, ItemAttributeNumericalValue> getNuyenResolver() {
		return (item) -> item.getAsValue(SR6ItemAttribute.PRICE);
	}

//	//-------------------------------------------------------------------
//	private void addModificationClicked() {
//		logger.log(Level.INFO, "addModificationClicked");
//		
//		List<ItemEnhancement> data = control.getEquipmentController().getAvailableEnhancementsFor(selectedItem);
//		SelectPluginDataDialog<ItemEnhancement> dialog = new SelectPluginDataDialog<ItemEnhancement>(
//				ResourceI18N.get(UI, "dialog.add.enhancement.title"), 
//				data,
//				lv -> new ItemEnhancementListCell(control.getCharacter(), control.getEquipmentController(), selectedItem),
//				CloseType.CANCEL, CloseType.OK);
//		CloseType closed = provider.getScreenManager().showAlertAndCall(dialog, dialog.getButtonControl());
//		logger.log(Level.INFO, "Closed via "+closed);
//		if (closed==CloseType.OK) {
//			for (ItemEnhancement master : dialog.getSelection()) {
//     			logger.log(Level.DEBUG, "add modification "+master+" to "+selectedItem);
//     			ItemEnhancementValue result = control.getEquipmentController().modify(selectedItem, master);
//     			logger.log(Level.DEBUG, "embedding "+master+" in "+selectedItem+" returned "+result);
//     			if (result==null) {
//     				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 0, ResourceI18N.format(UI, "dialog.add.enhancement.fail", master.getName()));
//     			}
//			}
//		}
//	}


}
