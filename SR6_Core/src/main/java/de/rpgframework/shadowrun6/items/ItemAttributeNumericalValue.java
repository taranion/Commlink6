package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.ModifyableNumericalValue;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.modification.Modification;

public class ItemAttributeNumericalValue extends ItemAttributeValue implements
		ModifyableNumericalValue<SR6ItemAttribute> {

	protected int value;

	//--------------------------------------------------------------------
	public ItemAttributeNumericalValue(SR6ItemAttribute attr, int val, List<Modification> mods) {
		super(attr, mods);
		value = val;
	}

	//--------------------------------------------------------------------
	public ItemAttributeNumericalValue(SR6ItemAttribute attr, int val) {
		super(attr, new ArrayList<>());
		value = val;
	}

	//--------------------------------------------------------------------
	public String toString() {
		if (getModifier()==0)
			return String.valueOf(value);
		return value+" ("+getModifiedValue()+")";
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.ModifyableValue#getPoints()
	 */
	@Override
	public int getDistributed() {
		return value;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.ModifyableValue#setPoints(int)
	 */
	@Override
	public void setDistributed(int points) {
		value = points;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.ModifyableValue#getModifier()
	 */
	public int getModifier() {
		int count = 0;
		for (Modification mod : modifications) {
//			if (mod instanceof ItemAttributeModification) {
//				ItemAttributeModification sMod = (ItemAttributeModification)mod;
//				if (sMod.getModifiedItem()==attribute)
//					count += sMod.getValue();
//			}
		}
		return count;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.ModifyableValue#getModifiedValue()
	 */
	@Override
	public int getModifiedValue() {
		return value + getModifier();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.ModifyableNumericalValue#getModifiedValue(de.rpgframework.genericrpg.ValueType)
	 */
	@Override
	public int getModifiedValue(ValueType type) {
		return getModifiedValue();
	}

}
