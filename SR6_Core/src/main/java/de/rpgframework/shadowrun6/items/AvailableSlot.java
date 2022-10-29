package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.items.AAvailableSlot;
import de.rpgframework.genericrpg.items.CarriedItem;

/**
 * @author prelle
 *
 */
public class AvailableSlot extends AAvailableSlot<ItemHook, ItemTemplate>  {

	private final static Logger logger = System.getLogger(AvailableSlot.class.getPackageName());

	@Attribute
	private ItemHook ref;

	
	//-------------------------------------------------------------------
	public AvailableSlot() {
	}
	
	//-------------------------------------------------------------------
	public AvailableSlot(ItemHook hook) {
		super();
		this.ref = hook;
		if (hook.hasCapacity())
			throw new IllegalArgumentException("Hook "+hook+" has capacity - use other constructor");
	}
	
	//-------------------------------------------------------------------
	public AvailableSlot(ItemHook hook, float capacity) {
		super(capacity);
		this.ref = hook;
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
		return 0;
	}

	//-------------------------------------------------------------------
	public float getFreeCapacity() {
		if (ref.hasCapacity) {
			float free = capacity;
			for (CarriedItem<ItemTemplate> accessory : embedded) {
				float size = 1.0f;
				if (accessory.hasAttribute(SR6ItemAttribute.SIZE)) {
					if (accessory.isFloat(SR6ItemAttribute.SIZE))
						size = accessory.getAsFloat(SR6ItemAttribute.SIZE).getModifiedValue();
					else
						size = accessory.getAsValue(SR6ItemAttribute.SIZE).getModifiedValue();
				}
				free -= size;
			}
			return free;
		} else {
			return embedded.isEmpty()?1:0;
		}
	}

}
