package de.rpgframework.shadowrun6.items;

import java.util.List;
import java.util.function.Predicate;

import de.rpgframework.genericrpg.items.CarryMode;

/**
 * @author prelle
 *
 */
public class ItemSubTypeFilter implements Predicate<ItemTemplate> {

	private CarryMode mode;
	private List<ItemSubType> validTypes;

	//-------------------------------------------------------------------
	public ItemSubTypeFilter(CarryMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	public ItemSubTypeFilter(CarryMode mode, ItemSubType...types) {
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
			typeMatches = (temp.getUsage(mode)!=null && validTypes.contains( temp.getItemSubtype(mode) ));
			if (!typeMatches && temp.getVariant(mode)!=null) {
				if (temp.getAttribute(SR6ItemAttribute.ITEMSUBTYPE)!=null) {
					typeMatches = (temp.getVariant(mode).getUsage(mode)!=null && validTypes.contains( temp.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() ));
				}
				if (temp.getVariant(mode).getAttribute(SR6ItemAttribute.ITEMSUBTYPE)!=null) {
					typeMatches = (temp.getVariant(mode).getUsage(mode)!=null && validTypes.contains( temp.getVariant(mode).getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() ));
				}
			}
		}
		
		return hasCarryMode && typeMatches;
	}

}
