package de.rpgframework.shadowrun6.items;

import java.util.List;
import java.util.function.Predicate;

import de.rpgframework.genericrpg.items.CarryMode;

/**
 * @author prelle
 *
 */
public class ItemTypeFilter implements Predicate<ItemTemplate> {

	private CarryMode mode;
	private List<ItemType> validTypes;

	//-------------------------------------------------------------------
	public ItemTypeFilter(CarryMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	public ItemTypeFilter(CarryMode mode, ItemType...types) {
		this.mode = mode;
		validTypes = List.of(types);
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(ItemTemplate temp) {
		boolean hasCarryMode = temp.getUsage(mode)!=null || temp.getVariant(mode)!=null;
		if (!hasCarryMode) return false;
		
		boolean typeMatches = true;
		if (validTypes!=null) {
			typeMatches = (temp.getUsage(mode)!=null && validTypes.contains( temp.getItemType(mode) ));
			if (!typeMatches && temp.getVariant(mode)!=null) {
				if (temp.getAttribute(SR6ItemAttribute.ITEMTYPE)!=null) {
					typeMatches = (temp.getVariant(mode).getUsage(mode)!=null && validTypes.contains( temp.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() ));
				}
				if (temp.getVariant(mode).getAttribute(SR6ItemAttribute.ITEMTYPE)!=null) {
					typeMatches = (temp.getVariant(mode).getUsage(mode)!=null && validTypes.contains( temp.getVariant(mode).getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() ));
				}
			}
		}
		
		return hasCarryMode && typeMatches;
	}

}
