package de.rpgframework.shadowrun6.items;

import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifyableImpl;

public abstract class ItemAttributeValue extends ModifyableImpl {

	protected SR6ItemAttribute attribute;

	//--------------------------------------------------------------------
	public ItemAttributeValue(SR6ItemAttribute attr, List<Modification> mods) {
		this.attribute = attr;
		super.modifications = mods;
	}

	//--------------------------------------------------------------------
	public SR6ItemAttribute getModifyable() {
		return attribute;
	}

}
