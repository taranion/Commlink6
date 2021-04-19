package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.PieceOfGearEquip;
import de.rpgframework.genericrpg.items.PieceOfGearUsage;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "item")
public class ItemTemplate extends PieceOfGear<SR6EquipMode,SR6UsageMode> {

	@Attribute(name="avail",required=false)
	@AttribConvert(AvailabilityConverter.class)
	private Availability availability;
	@Attribute
	private ItemType type;
	@Attribute
	private ItemSubType subtype;

	@Element
	private WeaponData weapon; 

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
		ArrayList<IGearTypeData> ret = new ArrayList<>();
		if (weapon!=null) ret.add(weapon);
		return ret;
	}

	//-------------------------------------------------------------------
	@Override
	public void validate() {
		attributes.clear();
		
		setAttribute(SR6ItemAttribute.PRICE, super.price);

		if (availability!=null) 
			setAttribute(SR6ItemAttribute.AVAILABILITY, availability);
		
		/* If there is no USAGE assume a NORMAL mode and no slot */
		if (equips.isEmpty()) {
			PieceOfGearEquip<SR6EquipMode> add = new SR6GearEquip(SR6EquipMode.NORMAL);
			equips.add(add);
		}
		if (usages.isEmpty()) {
			PieceOfGearUsage<SR6UsageMode> add = new SR6GearUsage(SR6UsageMode.NORMAL);
			usages.add(add);
		}
		
		
		super.validate();
	}

}
