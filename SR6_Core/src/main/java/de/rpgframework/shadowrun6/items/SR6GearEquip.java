package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.IgnoreMissingAttributes;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGearEquip;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="equip")
@IgnoreMissingAttributes("id")
public class SR6GearEquip extends PieceOfGearEquip<SR6EquipMode> {
	
	private ItemHook slot;

	//-------------------------------------------------------------------
	public SR6GearEquip() {
	}

	//-------------------------------------------------------------------
	public SR6GearEquip(SR6EquipMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	public SR6GearEquip(SR6EquipMode mode, ItemHook slot) {
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
