package de.rpgframework.shadowrun6.filter;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.function.Predicate;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

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
		if (item.getAsObject(SR6ItemAttribute.ITEMTYPE)==null) {
			System.getLogger(CarriedItemItemTypeFilter.class.getPackageName()).log(Level.WARNING, "No ITEMTYPE in "+item.getKey());
			return false;
		}
		ItemType foundType = item.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
		
		boolean hasCarryMode = mode==null;
		if (mode!=null && item.getCarryMode()==mode) hasCarryMode=true;
		
		boolean typeMatches = validTypes.contains(foundType);
		
		return hasCarryMode && typeMatches;
	}

}
