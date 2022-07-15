package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.util.List;
import java.util.function.Predicate;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;

/**
 * @author prelle
 *
 */
public class CarriedItemItemTypeFilter implements Predicate<CarriedItem<ItemTemplate>> {

	private CarryMode mode;
	private List<ItemType> validTypes;

	//-------------------------------------------------------------------
	public CarriedItemItemTypeFilter(CarryMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	public CarriedItemItemTypeFilter(CarryMode mode, ItemType...types) {
		this.mode = mode;
		validTypes = List.of(types);
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(CarriedItem<ItemTemplate> item) {
		ItemTemplate temp = item.getModifyable();
		if (temp==null) {
			System.getLogger(CarriedItemItemTypeFilter.class.getPackageName()).log(System.Logger.Level.WARNING, "CarriedItem {0} refers to unknown item {1}", item.getUuid(), item.getKey());
			return false;
		}
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant)item.getVariant();
		
		boolean hasCarryMode = temp.getUsage(mode)!=null || temp.getVariant(mode)!=null;
		if (!hasCarryMode) return false;
		
		boolean typeMatches = true;
		if (validTypes!=null) {
			typeMatches = (temp.getUsage(mode)!=null && validTypes.contains( temp.getItemType() ));
			if (!typeMatches && variant!=null) {
				if (variant.getAttribute(SR6ItemAttribute.ITEMTYPE)!=null) {
					typeMatches = (variant.getUsage(mode)!=null && validTypes.contains(variant.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue() ));
				}
			}
		}
		
		return hasCarryMode && typeMatches;
	}

}
