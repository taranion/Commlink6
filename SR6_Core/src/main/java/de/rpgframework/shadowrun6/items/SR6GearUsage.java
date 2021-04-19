package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGearUsage;

/**
 * @author prelle
 *
 */
public class SR6GearUsage extends PieceOfGearUsage<SR6UsageMode> {
	
	private ItemHook slot;

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

}
