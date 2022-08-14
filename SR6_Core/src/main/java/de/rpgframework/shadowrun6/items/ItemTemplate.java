package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "item")
public class ItemTemplate extends PieceOfGear<SR6VariantMode,SR6UsageMode,SR6PieceOfGearVariant,SR6AlternateUsage> {
	
	public final static String FLAG_AUGMENTATION = "AUGMENTATION";
	public final static String FLAG_MATRIX_DEVICE = "MATRIX_DEVICE";
	private static String FLAG_NOWIFI = "NOWIFI"; 
	public final static UUID UUID_AUGMENTATION_QUALITY = UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170c");
	public final static Choice CHOICE_AUGMENTATION_QUALITY = new Choice(
			ItemTemplate.UUID_AUGMENTATION_QUALITY, 
			ShadowrunReference.AUGMENTATION_QUALITY);
	

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
	public ItemAttributeDefinition getAttribute(IItemAttribute attrib, SR6PieceOfGearVariant variant) {
		ItemAttributeDefinition def = null;
		if (variant!=null) {
			def = variant.getAttribute(attrib);
			if (def!=null)
				return def;
		}
		
		return cache.get(attrib);
	}

//	//-------------------------------------------------------------------
//	public Choice getChoice(UUID uuid) {
//		if (UUID_AUGMENTATION_QUALITY.equals(uuid)) 
//			return CHOICE_AUGMENTATION_QUALITY;
//		return super.getChoice(uuid);
//	}

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
			if (requireVariant) {
				// Ensure all variants have a USAGE mode
				for (SR6PieceOfGearVariant variant : variants) {
					if (variant.getUsages()==null || variant.getUsages().isEmpty()) {
						variant.addUsage(new Usage(CarryMode.CARRIED));
					}
				}
			} else {
				usages.add(new Usage(CarryMode.CARRIED));
			}
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
		if (flags.contains(ItemTemplate.FLAG_AUGMENTATION) && this.getChoice(ItemTemplate.UUID_AUGMENTATION_QUALITY)==null) {
			addChoice(CHOICE_AUGMENTATION_QUALITY);
		}
		if (variants!=null) {
			for (SR6PieceOfGearVariant variant : variants) {
				variant.setParentItem(this);
				if (variant.hasFlag(FLAG_AUGMENTATION) && variant.getChoice(UUID_AUGMENTATION_QUALITY)==null) {
					variant.addChoice(CHOICE_AUGMENTATION_QUALITY);
				}
			}
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
	 * @deprecated Use getItemType(CarryMode)
	 */
	public ItemType getItemType() {
		return type;
	}

	//-------------------------------------------------------------------
	public ItemType getItemType(CarryMode carry) {
		if (getUsage(carry)!=null) return type;
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) getVariant(carry);
		if (variant!=null && variant.getUsage(carry)!=null) {
			if (variant.getAttribute(SR6ItemAttribute.ITEMTYPE)!=null)
				return variant.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue();
			return type;
		}
		if (usages==null || usages.isEmpty()) return type;
		return null;
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
	 * @deprecated Use getItemSubtype(CarryMode)
	 */
	public ItemSubType getItemSubtype() {
		return subtype;
	}

//	//-------------------------------------------------------------------
//	public boolean isType(List<ItemType> values) {
//		return values
//		if (values.contains(this.type))
//			return true;
//		for (UseAs use : usedAs) {
//			if (values.contains(use.getType()))
//				return true;
//		}
//		return false;
//	}

	//-------------------------------------------------------------------
	public ItemSubType getItemSubtype(CarryMode carry) {
		if (getUsage(carry)!=null) return subtype;
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) getVariant(carry);
		if (variant!=null && variant.getUsage(carry)!=null) {
			if (variant.getAttribute(SR6ItemAttribute.ITEMSUBTYPE)!=null)
				return variant.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue();
			return subtype;
		}
		if (usages==null || usages.isEmpty()) return subtype;
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @param subtype the subtype to set
	 */
	public void setItemSubtype(ItemSubType subtype) {
		this.subtype = subtype;
	}

	//-------------------------------------------------------------------
	public Collection<String> getWiFiAdvantageStrings(CarryMode carry, PieceOfGearVariant variant, Locale locale) {
		List<String> ret = new ArrayList<>();
		
		String key = "item."+id+".wifi";
		String multiLine = getLocalizedString(locale, key);
		if (!multiLine.equals(key)) {
			StringTokenizer tok = new StringTokenizer(multiLine,"\n");
			while (tok.hasMoreTokens())
				ret.add(tok.nextToken());
		}
		// For firearms, add general wireless functionality
		ItemType type = getItemType(carry);
		List<ItemType> firearms = Arrays.asList(ItemType.WEAPON_FIREARMS, ItemType.WEAPON_SPECIAL);
		if (firearms.contains(type) && !hasFlag(ItemTemplate.FLAG_NOWIFI)) {
			StringTokenizer tok = new StringTokenizer(Shadowrun6Core.getI18nResources().getString("wireless.firearms_general", locale),"\n");
			while (tok.hasMoreTokens())
				ret.add(tok.nextToken());
		}
		
		// TODO: Variant
		
		return ret;
	}

	//-------------------------------------------------------------------
	public boolean requiresVariant() {
		return requireVariant;
	}

	//-------------------------------------------------------------------
	public List<AGearData> getPossibilities(CarryMode carry) {
		List<AGearData> ret = new ArrayList<>();
		if (getUsage(carry)!=null && !requiresVariant()) 
			ret.add(ItemUtil.calculateVirtualItem(this, null, carry));
		for (SR6PieceOfGearVariant variant : getVariants()) {
			if (variant.getUsage(carry)!=null || (getUsage(carry)!=null && requiresVariant())) {
				ret.add(ItemUtil.calculateVirtualItem(this, variant, carry));
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public boolean isAugmentation() {
		ItemAttributeDefinition attr = getAttribute(SR6ItemAttribute.ITEMTYPE);
		return hasFlag(FLAG_AUGMENTATION) || attr.getValue()==ItemType.CYBERWARE || attr.getValue()==ItemType.BIOWARE;
	}

	//-------------------------------------------------------------------
	public boolean isMatrixDevice() {
		ItemAttributeDefinition attr = getAttribute(SR6ItemAttribute.ITEMTYPE);
		ItemSubType sub = getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue();
		return hasFlag(FLAG_MATRIX_DEVICE) || attr.getValue()==ItemType.ELECTRONICS &&
				( sub==ItemSubType.COMMLINK ||  sub==ItemSubType.CYBERDECK ||  sub==ItemSubType.RIGGER_CONSOLE  ||  sub==ItemSubType.TAC_NET );
	}

}

class VariantItemTemplate extends ItemTemplate {

	SR6PieceOfGearVariant variant;
	
	public VariantItemTemplate(SR6PieceOfGearVariant variant) {
		this.variant = variant;
	}

	//--------------------------------------------------------------------
	public String getName(Locale locale) {
		if (parentItem!=null) {
			String key = parentItem.getTypeString()+"."+parentItem.getId().toLowerCase()+".variant."+id.toLowerCase();
			return getLocalizedString(locale, key);
		}
		String key = getTypeString()+"."+id.toLowerCase();
		return getLocalizedString(locale, key);
	}
}
