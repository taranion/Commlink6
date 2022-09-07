package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author stefa
 *
 */
public class AccessoriesSection extends GearSection {
	
	private CarriedItem<ItemTemplate> device;

	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param carry
	 * @param selectFilter
	 * @param showFilter
	 */
	public AccessoriesSection(String title, Predicate<ItemTemplate> selectFilter,
			Predicate<CarriedItem<ItemTemplate>> showFilter) {
		super(title, CarryMode.EMBEDDED, selectFilter, showFilter);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public void setDevice(CarriedItem<ItemTemplate> device) {
		this.device = device;
		refresh();
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.jfx.section.GearSection#refresh()
	 */
	@Override
	public void refresh() {
		if (device==null) {
			list.getItems().clear();
			return;
		}
		List<CarriedItem<ItemTemplate>> data = device.getAccessories()
			.stream()
			.filter(ci -> ci.getCarryMode()==CarryMode.EMBEDDED)
			.filter(ci -> ci.getUsedSlot()==ItemHook.ELECTRONIC_ACCESSORY)
			.collect(Collectors.toList());
		list.getItems().setAll(data);
	}

}
