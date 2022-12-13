package de.rpgframework.shadowrun6.items;

import de.rpgframework.genericrpg.data.ComplexDataItemValue;

public class ItemEnhancementValue extends ComplexDataItemValue<SR6ItemEnhancement> {

	//--------------------------------------------------------------------
	public ItemEnhancementValue() {
	}

	//--------------------------------------------------------------------
	public ItemEnhancementValue(SR6ItemEnhancement ref) {
		setResolved(ref);
	}

//	//--------------------------------------------------------------------
//	public ItemEnhancementValue(ItemEnhancement ref, boolean auto) {
//		setResolved(ref);
//		this.autoAdded = auto;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @return the autoAdded
//	 */
//	public boolean isAutoAdded() {
//		return autoAdded;
//	}

}
