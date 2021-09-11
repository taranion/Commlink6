package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.PieceOfGearEquip;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "item")
public class ItemTemplate extends PieceOfGear<SR6EquipMode,SR6UsageMode,SR6GearUsage> {

	@Attribute(name="avail",required=false)
	@AttribConvert(AvailabilityConverter.class)
	private Availability availability;
	@Attribute
	private ItemType type;
	@Attribute
	private ItemSubType subtype;

	@ElementList(entry = "weapon", type = WeaponData.class, inline = true)
	private List<WeaponData> weapons; 

	//-------------------------------------------------------------------
	public ItemTemplate() {
		weapons = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
		ArrayList<IGearTypeData> ret = new ArrayList<>();
		if (!weapons.isEmpty())
			ret.add(weapons.get(0));
		return ret;
	}

	//-------------------------------------------------------------------
	public List<WeaponData> getAttacks() {
		ArrayList<WeaponData> ret = new ArrayList<>(weapons);
		return ret;
	}

	//-------------------------------------------------------------------
	@Override
	public void validate() {
//		attributes.clear();
		
		setAttribute(SR6ItemAttribute.PRICE, super.price);

		if (availability!=null) 
			setAttribute(SR6ItemAttribute.AVAILABILITY, availability);
		
		/* If there is no USAGE assume a NORMAL mode and no slot */
		if (equips.isEmpty()) {
			PieceOfGearEquip<SR6EquipMode> add = new SR6GearEquip(SR6EquipMode.NORMAL);
			equips.add(add);
		}
		if (usages.isEmpty()) {
			SR6GearUsage add = new SR6GearUsage(SR6UsageMode.NORMAL);
			usages.add(add);
		}
		
		
		super.validate();
	}

	//-------------------------------------------------------------------
	public List<SR6GearUsage> getUsages() {
		if (usages.isEmpty()) {
			return Arrays.asList(new SR6GearUsage(SR6UsageMode.NORMAL));
		}
		return usages;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public ItemType getItemType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setItemType(ItemType type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the subtype
	 */
	public ItemSubType getItemSubtype() {
		return subtype;
	}

	//-------------------------------------------------------------------
	/**
	 * @param subtype the subtype to set
	 */
	public void setItemSubtype(ItemSubType subtype) {
		this.subtype = subtype;
	}

}
