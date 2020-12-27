package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.IGearTypeData;
import de.rpgframework.genericrpg.data.PieceOfGear;
import de.rpgframework.genericrpg.data.PieceOfGearUsage;
import de.rpgframework.shadowrun6.persist.AvailabilityConverter;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "item")
public class ItemTemplate extends PieceOfGear {

	@Attribute(name="avail",required=false)
	@AttribConvert(AvailabilityConverter.class)
	private Availability availability;

	@Element
	private WeaponData weapon; 
	
	@Override
	public List<? extends PieceOfGearUsage> getUsages() {
		// TODO Auto-generated method stub
		return null;
	}

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
		if (availability!=null) 
			setAttribute(SR6ItemAttribute.AVAILABILITY, availability);
		
		super.validate();
	}

}
