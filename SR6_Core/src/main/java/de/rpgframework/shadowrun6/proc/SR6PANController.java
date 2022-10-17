package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.ctrl.IPANController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6AlternateUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;

/**
 * @author prelle
 *
 */
public class SR6PANController implements IPANController<ItemTemplate> {
	
	protected static Logger logger = System.getLogger(SR6PANController.class.getPackageName());

	private Shadowrun6Character model;
	private Locale loc = Locale.getDefault();

	//-------------------------------------------------------------------
	public SR6PANController(Shadowrun6Character model, Locale loc) {
		this.model = model;
		this.loc   = loc;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ctrl.IPANController#getModel()
	 */
	@Override
	public <C extends ShadowrunCharacter<?, ?, ?, ?>> C getModel() {
		return (C) model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// If there is no primary access device, define one
		if (getUsedAccessDevice() == null) {
			int bestDF = -1;
			CarriedItem<ItemTemplate> best = null;
			for (CarriedItem<ItemTemplate> item : getAvailableAccessDevices()) {
				int d = item.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue();
				int f = item.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue();
				int df = d+f;
				if (df>bestDF) {
					best = item;
					bestDF = df;
				}
			}
			if (best!=null) {
				logger.log(Level.INFO, "Auto-define {0} as matrix access device", best.getKey());
				best.addFlag(SR6ItemFlag.PRIMARY);
			}
		}

		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ctrl.IPANController#getAvailableAccessDevices()
	 */
	@Override
	public List<CarriedItem<ItemTemplate>> getAvailableAccessDevices() {
		List<CarriedItem<ItemTemplate>> ret = new ArrayList<>();
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems(ItemType.ELECTRONICS)) {
			if (item.hasAttribute(SR6ItemAttribute.DATA_PROCESSING)) {
				ret.add(item);
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ctrl.IPANController#getUsedAccessDevice()
	 */
	@Override
	public CarriedItem<ItemTemplate> getUsedAccessDevice() {
		for (CarriedItem<ItemTemplate> item : getAvailableAccessDevices()) {
			if (item.hasFlag(SR6ItemFlag.PRIMARY)) {
				return item;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ctrl.IPANController#setUsedAccessDevice()
	 */
	@Override
	public void setUsedAccessDevice(CarriedItem<ItemTemplate> data) {
		for (CarriedItem<ItemTemplate> item : getAvailableAccessDevices()) {
			if (item.hasFlag(SR6ItemFlag.PRIMARY) && item!=data) {
				item.removeFlag(SR6ItemFlag.PRIMARY);
			}
		}
		data.addFlag(SR6ItemFlag.PRIMARY);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ctrl.IPANController#getDevicesInPAN()
	 */
	@Override
	public List<CarriedItem<ItemTemplate>> getDevicesInPAN() {
		List<CarriedItem<ItemTemplate>> ret = new ArrayList<>();
		for (CarriedItem<ItemTemplate> tmp : model.getCarriedItemsRecursive()) {
			ItemType type = tmp.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
			if (ItemType.isVehicle(type) || ItemType.isWeapon(type) || tmp.hasAttribute(SR6ItemAttribute.FIREWALL)) {
				ret.add(tmp);
			} else {
				for (SR6AlternateUsage alt : tmp.getResolved().getAlternates()) {
					type = alt.getType();
					if (ItemType.isVehicle(type) || ItemType.isWeapon(type)) {
						ret.add(tmp);
					}
				}
			}
		}
		return ret;
	}

}
