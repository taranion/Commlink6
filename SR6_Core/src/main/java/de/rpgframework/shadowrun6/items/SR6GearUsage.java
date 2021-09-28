package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.IgnoreMissingAttributes;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGearUsage;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="usage")
@IgnoreMissingAttributes("id")
public class SR6GearUsage extends PieceOfGearUsage<SR6UsageMode> {
	
	@Attribute
	private ItemHook slot;
	@Attribute
	private float volume;

	@Element
	private WeaponData weapon; 

	//-------------------------------------------------------------------
	public SR6GearUsage() {
	}

	//-------------------------------------------------------------------
	public SR6GearUsage(SR6UsageMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	public SR6GearUsage(SR6UsageMode mode, ItemHook slot) {
		this(mode);
		this.slot = slot;
	}

	//-------------------------------------------------------------------
	public ItemHook getSlot() {
		return slot;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * How many capacity points are used up by this item
	 * @return the volume
	 */
	public float getVolume() {
		return volume;
	}

}
