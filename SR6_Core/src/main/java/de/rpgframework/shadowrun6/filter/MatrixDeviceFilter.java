package de.rpgframework.shadowrun6.filter;

import java.util.function.Predicate;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class MatrixDeviceFilter implements Predicate<CarriedItem<ItemTemplate>> {

	//-------------------------------------------------------------------
	public MatrixDeviceFilter() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(CarriedItem<ItemTemplate> item) {
		ItemTemplate temp = item.getModifyable();
		if (temp==null) {
			System.getLogger(MatrixDeviceFilter.class.getPackageName()).log(System.Logger.Level.WARNING, "CarriedItem {0} refers to unknown item {1}", item.getUuid(), item.getKey());
			return false;
		}		
		return temp.isMatrixDevice();
	}

}
