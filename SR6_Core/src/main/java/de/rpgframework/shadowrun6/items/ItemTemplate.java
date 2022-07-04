package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "item")
public class ItemTemplate extends PieceOfGear<SR6VariantMode,SR6UsageMode,SR6PieceOfGearVariant,SR6AlternateUsage> {
	
	public static String FLAG_AUGMENTATION = "AUGMENTATION";
	public static UUID CHOICE_AUGMENTATION_QUALITY = UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170c");
	

	@Attribute(name="avail",required=false)
	@AttribConvert(AvailabilityConverter.class)
	private Availability availability;
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
	
	@Attribute(name="reqVariant")
	private boolean requireVariant;

	//-------------------------------------------------------------------
	public ItemTemplate() {
		shortcuts = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
//		ArrayList<IGearTypeData> ret = new ArrayList<>();
//		if (!weapons.isEmpty())
//			ret.add(weapons.get(0));
		return shortcuts;
	}

	//-------------------------------------------------------------------
	public List<WeaponData> getAttacks() {
		ArrayList<WeaponData> ret = new ArrayList<>();
		for (IGearTypeData tmp : shortcuts) {
			if (tmp instanceof WeaponData) {
				ret.add((WeaponData) tmp);
			}
		}
		// Add from usages
		for (SR6AlternateUsage usage : super.getAlternates()) {
			logger.log(Level.WARNING, "ToDo: Usage: "+usage);
			ret.addAll(usage.getAttacks());
		}
		
		return ret;
	}

	//-------------------------------------------------------------------
	@Override
	public void validate() {
//		attributes.clear();
		if (type==null) throw new DataErrorException(this, "sort type not set for '"+id+"'");
		if (subtype==null) throw new DataErrorException(this, "sort subtype not set for '"+id+"'");
		
		
		setAttribute(SR6ItemAttribute.PRICE, super.price);
		setAttribute(SR6ItemAttribute.ITEMTYPE, type);
		setAttribute(SR6ItemAttribute.ITEMSUBTYPE, subtype);

		if (availability!=null) 
			setAttribute(SR6ItemAttribute.AVAILABILITY, availability);
		
		/* If there is no USAGE assume a NORMAL mode and no slot */
		if (usages.isEmpty()) {
			usages.add(new Usage(CarryMode.CARRIED));
		}
//		if (variants.isEmpty()) {
//			SR6PieceOfGearVariant add = new SR6PieceOfGearVariant(SR6VariantMode.NORMAL);
//			variants.add(add);
//		}
//		if (alternates.isEmpty()) {
//			SR6AlternateUsage add = new SR6AlternateUsage(SR6UsageMode.PRIMARY);
//			alternates.add(add);
//		}

		// TODO
		// Make a implant version of every weapon
		// for that remove normal accessory slots and replace them with
		// implant versions
		
		
		// Validate hook identifier in modifications
		for (Modification tmp : getModifications()) {
			if (tmp instanceof EmbedModification) {
				try {
					((EmbedModification)tmp).getHook();
					//((EmbedModification)tmp).getResolvedKey();
				} catch (ClassCastException e) {
					throw new DataErrorException(this, "Internal class cast error: "+e.getMessage());
				} catch (ReferenceException e) {
					throw new DataErrorException(this, e.getError());
				} catch (Exception e) {
					e.printStackTrace();
					throw new DataErrorException(this, "Internal error: "+e.getMessage());
				}
				
			}
		}

		// If it has an AUGMENTATION flag, add that decision
		if (flags.contains(ItemTemplate.FLAG_AUGMENTATION) && this.getChoice(ItemTemplate.CHOICE_AUGMENTATION_QUALITY)==null) {
			Choice choice = new Choice(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, ShadowrunReference.AUGMENTATION_QUALITY);
			choices.add(choice);
		}
		
		super.validate();
	}

//	//-------------------------------------------------------------------
//	public List<SR6GearUsage> getAlternates() {
//		if (alternates.isEmpty()) {
//			return Arrays.asList(new SR6GearUsage(SR6UsageMode.PRIMARY));
//		}
//		return alternates;
//	}

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

	//-------------------------------------------------------------------
	public boolean requiresVariant() {
		return requireVariant;
	}

}
