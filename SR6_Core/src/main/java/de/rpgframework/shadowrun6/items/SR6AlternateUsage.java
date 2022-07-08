package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.IgnoreMissingAttributes;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.AlternateUsage;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="usage")
@IgnoreMissingAttributes("id")
public class SR6AlternateUsage extends AlternateUsage<SR6UsageMode> {
	
	@Attribute
	private ItemType type;
	@Attribute
	private ItemSubType subtype;

	@ElementListUnion({
		@ElementList(entry="weapon", type = WeaponData.class, inline = true),
		@ElementList(entry="armor", type=ArmorData.class, inline=true),
		@ElementList(entry="matrix", type=MatrixData.class, inline=true),
		@ElementList(entry="vehicle", type=VehicleData.class, inline=true),
	})
	private List<IGearTypeData> shortcuts; 

	//-------------------------------------------------------------------
	public SR6AlternateUsage() {
		shortcuts = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public SR6AlternateUsage(SR6UsageMode mode) {
		this();
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return mode+"(type="+type+", subtype="+subtype+") with "+shortcuts;
	}

	//-------------------------------------------------------------------
	public List<WeaponData> getAttacks() {
		for (IGearTypeData sc : shortcuts) {
			if (sc instanceof WeaponData)
				return List.of( (WeaponData)sc );
		}
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public ItemType getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the subtype
	 */
	public ItemSubType getSubtype() {
		return subtype;
	}
}
