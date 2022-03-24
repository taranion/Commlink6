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
	private ItemHook slot;
	@Attribute
	private float volume;

	@ElementListUnion({
		@ElementList(entry="weapon", type = WeaponData.class, inline = true),
		@ElementList(entry="armor", type=ArmorData.class, inline=true),
		@ElementList(entry="matrix", type=MatrixData.class, inline=true),
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
	public SR6AlternateUsage(SR6UsageMode mode, ItemHook slot) {
		this(mode);
		this.slot = slot;
	}

	//-------------------------------------------------------------------
	public ItemHook getSlot() {
		return slot;
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
	/**
	 * How many capacity points are used up by this item
	 * @return the volume
	 */
	public float getVolume() {
		return volume;
	}

}
