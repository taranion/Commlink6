package de.rpgframework.shadowrun6.chargen.jfx.dialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import org.prelle.javafx.ApplicationScreen;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.ScreenManagerProvider;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.jfx.listcell.ItemEnhancementValueListCell;
import de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.chargen.jfx.selector.FilterItemEnhancement;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemEnhancementSelector;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.util.Callback;

/**
 * @author Stefan Prelle
 *
 */
public class EditCarriedItemDialog extends ACarriedItemPage<ItemTemplate, ItemHook, AvailableSlot> {

//	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditCarriedItemDialog.class.getName());

	protected final static Logger logger = System.getLogger(EditCarriedItemDialog.class.getPackageName());

	protected SR6CharacterController control;

	//--------------------------------------------------------------------
	public EditCarriedItemDialog(SR6CharacterController ctrl, CarriedItem<ItemTemplate> data) {
		super(ctrl, data);
		this.control = ctrl;

		switch ((ItemType)data.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue()) {
		case WEAPON_CLOSE_COMBAT:
		case WEAPON_RANGED:
		case WEAPON_FIREARMS:
		case AMMUNITION: // to enable modifications for grenades
		case WEAPON_SPECIAL:
			view.setName(2, ResourceI18N.get(UI, "label.modifications"));
			view.getList(2).setAll(selectedItem.getEnhancements());
//			view.setCellFactory(2, (lv)-> new ItemEnhancementValueObjectListCell(selectedItem,control.getEquipmentController()));
			view.setOnAddAction(2, ev -> addModificationClicked());
			break;
		}

		switch ((ItemSubType)data.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue()) {
		case CYBER_LIMBS:
			view.setName(2, ResourceI18N.get(UI, "label.modifications"));
			view.getList(2).setAll(selectedItem.getEnhancements());
			view.setOnAddAction(2, ev -> addModificationClicked());
			break;
		}
	}

	//--------------------------------------------------------------------
	protected void initCompoments() {
		super.initCompoments();

		description = new CarriedItemDescriptionPane(Shadowrun6Tools.requirementResolver(Locale.getDefault()), control);
		description.setStyle("-fx-pref-width: 30em; -fx-max-width: 30em");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage#getIndexFor(de.rpgframework.genericrpg.items.AAvailableSlot)
	 */
	protected int getIndexFor(AvailableSlot slot) {
		boolean large = false;
		int index = -1;
		switch (slot.getHook()) {
		// Available slots: 2-6, 8-11 (1 = data, 7 = name and notes, 12 = cost and number)

		// Melee weapons
		// Slot 2 is used for modifications
		case MELEE_EXTERNAL    : index= 9; break;

		// Ranged weapons
		// Slot 2 is used for modifications
		case RANGED_EXTERNAL   : index= 9; break;

		// Armor
		case ARMOR             : index= 2; break;
		case ARMOR_REACTIVE    : index= 3; break;
		case ARMOR_ADDITION    : index= 4; break;
		case ARMOR_MEMS        : index= 8; break;
		case HELMET_ACCESSORY  : index= 9; break;
		case FASHION		   : index= 10; break;
		
		// Cyberware
		case HEADWARE_IMPLANT   : index= 2; break;
		case SKILLJACK          : index= 3; break;
		case CYBERLIMB_IMPLANT  : index= 4; break;
		case CYBEREYE_IMPLANT   : index= 9; break;
		case CYBEREAR_IMPLANT   : index= 9; break;

		// Kommlinks/Rigger Consoles/Decks/Tac-Nets
		case ELECTRONIC_ACCESSORY: index= 3; break;
		case SOFTWARE            : index= 8; break;

		// Devices
		case SENSOR_HOUSING    : index= 2; break;
		case SENSOR_FUNCTION   : index= 3; break;
		case PROCAM_SLOT       : index= 4; break;
		case OPTICAL           : index= 8; break;
		case AUDIO             : index= 9; break;

		// Instruments
		case INSTRUMENT_SLOT  : index= 2; break;
		case INSTRUMENT_WEAPON: index= 9; break;

		// Firearms
		// Slot 2 is used for modifications
		case UNDER             : index= 3; break;
		case UNDER_WEAPON_MOUNT: index= 3; break; // Ok to overlap with UNDER as UNDER_WEAPON_MOUNT replaces UNDER
		case STOCK             : index= 4; break;
		case SIDE_R            : index= 5; break;
		//case WEAPON_SECURITY   : index= 5; break; // Overlaps with SIDE_R, should be a mod (pen symbol) inside the modification, not a new slot
		case SIDE_L            : index= 6; break;
		case INTERNAL          : index= 8; break;
		//case OPTICAL           : index= 8; break; // Created by smartgun_system, should be a mod (pen symbol) inside smartgun, not a new slot
		case FIREARMS_EXTERNAL : index= 9; break;
		case TOP               : index=10; break;
		case BARREL            : index=11; break;

		// Vehicles
		case VEHICLE_CHASSIS    : index= 2; break;
		case VEHICLE_ELECTRONICS: index= 3; break;
		case VEHICLE_POWERTRAIN : index= 4; break;
		case VEHICLE_TIRES      : index= 5; break;
		case VEHICLE_ACCESSORY  : index= 6; break;
		// SOFTWARE uses slot 8, see Kommlinks
		case VEHICLE_HARDPOINT  : index= 9; break;
		case VEHICLE_CF         : index=10; break;
		// slot 11 used for modslot circle (body, powertrain, electrical)
		// slot for VEHICLE_WEAPON not needed, as those are inside hardpoints (VEHICLE_BODY)


		default:
			logger.log(Level.WARNING, "No defined slot for {0}",slot.getHook());
		}

		return index;
	}

	//-------------------------------------------------------------------
	/**
	 * Position all elements that are NOT AvailableSlots
	 */
	protected void positionNonSlots() {
		logger.log(Level.DEBUG, "positionNonSlots");
		super.positionNonSlots();

		ItemType type = selectedItem.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		switch (type) {
		case WEAPON_CLOSE_COMBAT:
		case WEAPON_RANGED:
		case WEAPON_FIREARMS:
		case AMMUNITION: // to enable modifications for grenades
		case WEAPON_SPECIAL:
			logger.log(Level.WARNING, "Show modifications for "+type);
			view.setName(2, ResourceI18N.get(UI, "label.modifications"));
			view.getList(2).setAll(selectedItem.getEnhancements());
			view.setCellFactory(2, new Callback<ListView<Object>, ListCell<Object>>() {

				@Override
				public ListCell<Object> call(ListView<Object> lv) {
					ListCell<?> cell =  new ItemEnhancementValueListCell<SR6ItemEnhancement>(control, selectedItem, (c) -> refresh());
//					ListCell<?> cell = new ComplexDataItemValueListCell<SR6ItemEnhancement, ItemEnhancementValue<SR6ItemEnhancement>>( () -> control.getEquipmentController().getItemEnhancementController(selectedItem));
					return (ListCell<Object>) cell;
				}
			});
			view.setOnAddAction(2, ev -> addModificationClicked());
			break;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage#updateImage()
	 */
	protected void updateImage()  {
		logger.log(Level.INFO, "refresh");
		ItemSubType sub = selectedItem.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		ItemType type = selectedItem.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		view.setSubtitle(sub.getName());

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
				byte[] imgData = Shadowrun6DataPlugin.getPlaceholderGraphic(selectedItem);
				if (imgData!=null) {
					view.setImage(new Image(new ByteArrayInputStream(imgData)));
				} else {
					logger.log(Level.WARNING, "You may want to add a placeholder for ''{0}''", imgName);
					view.setImage(null);
				}
			}
		}
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

		ItemTemplateSelector selector = new ItemTemplateSelector(control, CarryMode.EMBEDDED, templateFilter, selectedItem, slot);
		ManagedDialog dialog = new ManagedDialog(
				ResourceI18N.format(UI, "dialog.add.accessory.title", slot.getName()),
				selector,
				CloseType.CANCEL, CloseType.OK);
		NavigButtonControl btnCtrl = new NavigButtonControl();
		selector.setButtonControl(btnCtrl);
		CloseType closed = getAppLayout().getApplication().showAlertAndCall(dialog, btnCtrl);
		logger.log(Level.INFO, "Closed via "+closed);
		if (closed==CloseType.OK) {
			refresh();
			ItemTemplate toAdd = selector.getSelected();
			CarryMode carry = CarryMode.EMBEDDED;
			OperationResult<CarriedItem<ItemTemplate>> result = null;
			// Eventually show decision dialog
			boolean needToAsk = !toAdd.getChoices().isEmpty();
			needToAsk |= !toAdd.getVariants().isEmpty();
			if (  control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.ALWAYS_ASK_FOR_FLAGS))
				needToAsk |= !toAdd.getUserSelectableFlags(SR6ItemFlag.class).isEmpty();
			if (needToAsk) {
				logger.log(Level.WARNING, "Select with choices or variants or flags");
				ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>> dia2 = new ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>>(control.getEquipmentController(), carry, slot, selectedItem);
				Decision[] dec = dia2.apply(toAdd, toAdd.getChoices());
				if (dec!=null) {
					// Not cancelled
					String variantID = dia2.getSelectedVariant();
					logger.log(Level.DEBUG, "After dialog: variant   = "+variantID);
					logger.log(Level.DEBUG, "After dialog: decisions = "+Arrays.toString(dec));
					result = control.getEquipmentController().embed(selectedItem, slot, selector.getSelected(),variantID, dec);
				}
			} else {
				logger.log(Level.WARNING, "Select without decisions");
				result = control.getEquipmentController().embed(selectedItem, slot, selector.getSelected(),null);
			}
			if (result != null) {
				if (result.wasSuccessful()) {
					logger.log(Level.WARNING, "Successful");
					refresh();
				} else {
					logger.log(Level.WARNING, "Failed: " + result.getError());
					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, result.getError());
				}
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage#getNuyenResolver()
	 */
	@Override
	protected Function<CarriedItem, ItemAttributeNumericalValue> getNuyenResolver() {
		return (item) -> item.getAsValue(SR6ItemAttribute.PRICE);
	}

	//-------------------------------------------------------------------
	private void addModificationClicked() {
		logger.log(Level.WARNING, "addModificationClicked");

		//List<SR6ItemEnhancement> data = control.getEquipmentController().getAvailableEnhancementsFor(selectedItem);

		//Predicate<ItemTemplate> templateFilter = i -> data.contains(i);
		ItemEnhancementSelector selector = new ItemEnhancementSelector(control.getEquipmentController(), selectedItem, new FilterItemEnhancement());
		ManagedDialog dialog = new ManagedDialog(
				ResourceI18N.get(UI, "dialog.add.modification.title"),
				selector,
				CloseType.CANCEL, CloseType.OK);
		NavigButtonControl btnCtrl = new NavigButtonControl();
		selector.setButtonControl(btnCtrl);
		CloseType closed = getAppLayout().getApplication().showAlertAndCall(dialog, btnCtrl);
//		SelectPluginDataDialog<ItemEnhancement> dialog = new SelectPluginDataDialog<ItemEnhancement>(
//				ResourceI18N.get(UI, "dialog.add.enhancement.title"),
//				data,
//				lv -> new ItemEnhancementListCell(control.getCharacter(), control.getEquipmentController(), selectedItem),
//				CloseType.CANCEL, CloseType.OK);
//		CloseType closed = provider.getScreenManager().showAlertAndCall(dialog, dialog.getButtonControl());
		logger.log(Level.INFO, "Closed via "+closed);
		if (closed==CloseType.OK) {
			SR6ItemEnhancement master = selector.getSelected();
//			for (SR6ItemEnhancement master : dialog.getSelection()) {
     			logger.log(Level.DEBUG, "add modification "+master+" to "+selectedItem);
     			OperationResult<ItemEnhancementValue<SR6ItemEnhancement>> result = control.getEquipmentController().getItemEnhancementController(selectedItem).select(master);
     			logger.log(Level.DEBUG, "embedding "+master+" in "+selectedItem+" returned "+result);
    			view.getList(2).setAll(selectedItem.getEnhancements());
    			refresh();
     			if (result.hasError()) {
     				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 0, ResourceI18N.format(UI, "dialog.add.enhancement.fail", master.getName(), result.getError()));
     			}
//			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.pages.ACarriedItemPage#editAccessory(de.rpgframework.genericrpg.items.CarriedItem)
	 */
	@Override
	protected void editAccessory(CarriedItem<ItemTemplate> accessoryToEdit) {
		logger.log(Level.ERROR, "TODO: override editAccessory() method of "+getClass());
		EditCarriedItemDialog dialog = new EditCarriedItemDialog(control, accessoryToEdit);
		dialog.setAppLayout(FlexibleApplication.getInstance().getAppLayout());
		FlexibleApplication.getInstance().openScreen(new ApplicationScreen(dialog));
//		CloseType result = (CloseType) FlexibleApplication.getInstance().showAlertAndCall(dialog, dialog.getButtonControl());
//		if (result==CloseType.OK) {
//			this.requestLayout();
//		}
	}


}
