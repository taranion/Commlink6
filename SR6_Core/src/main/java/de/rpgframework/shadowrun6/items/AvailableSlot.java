package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.items.AAvailableSlot;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;

/**
 * @author prelle
 *
 */
public class AvailableSlot extends AAvailableSlot<ItemHook, ItemTemplate>  {

	private final static Logger logger = System.getLogger(AvailableSlot.class.getPackageName());

	@Attribute
	private ItemHook ref;
	@Attribute(name="max")
	private Double maxSizePerItem;


	//-------------------------------------------------------------------
	public AvailableSlot() {
	}

	//-------------------------------------------------------------------
	public AvailableSlot(ItemHook hook) {
		super();
		this.ref = hook;
		if (hook.hasCapacity()) {
			capacity = 99;
//			throw new IllegalArgumentException("Hook "+hook+" has capacity - use other constructor");
		}
	}

	//-------------------------------------------------------------------
	public AvailableSlot(ItemHook hook, float capacity) {
		super(capacity);
		this.ref = hook;
	}
	@Override
	public void addIncomingModification(Modification mod) {
		if (mod.getOrigin()==null)
			throw new RuntimeException("Modifications without origin are not allowed");
		if (mod instanceof ValueModification vm) {
			if (vm.getFormula()!=null) {
				logger.log(Level.DEBUG, "Adding size modification {0} to slot {1}", vm, this);
			}
		}
		incomingModifications.add(mod);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.AAvailableSlot#getHook()
	 */
	@Override
	public ItemHook getHook() {
		return ref;
	}

	//-------------------------------------------------------------------
	public float getUsedCapacity() {
		return getCapacity() - getFreeCapacity();
	}

	//-------------------------------------------------------------------
	public float getFreeCapacity() {
		if (ref.hasCapacity) {
			double free = getCapacity();
			for (CarriedItem<ItemTemplate> accessory : embedded) {
				float size = 1.0f;
				if (accessory.hasAttribute(SR6ItemAttribute.SIZE)) {
					if (accessory.isFloat(SR6ItemAttribute.SIZE))
						size = accessory.getAsFloat(SR6ItemAttribute.SIZE).getModifiedValue();
					else
						size = accessory.getAsValue(SR6ItemAttribute.SIZE).getModifiedValue();
				}
				// Ignore auto-accessories
//				if ("DEFAULT".equals(accessory.getInjectedBy()))
				if (accessory.getInjectedBy()!=null)
					continue;
				free -= size;
			}
			return (float)free;
		} else {
			return embedded.isEmpty()?1:0;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @return the maxSizePerItem
	 */
	public Double getMaxSizePerItem() {
		return maxSizePerItem;
	}

	//-------------------------------------------------------------------
	/**
	 * @param maxSizePerItem the maxSizePerItem to set
	 */
	public void setMaxSizePerItem(Double maxSizePerItem) {
		this.maxSizePerItem = maxSizePerItem;
	}

}
