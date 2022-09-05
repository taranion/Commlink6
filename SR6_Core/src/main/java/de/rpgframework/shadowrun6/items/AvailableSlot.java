package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.items.AAvailableSlot;
import de.rpgframework.genericrpg.items.CarriedItem;

/**
 * @author prelle
 *
 */
public class AvailableSlot extends AAvailableSlot<ItemHook, ItemTemplate>  {

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
		return 1;
	}

}
