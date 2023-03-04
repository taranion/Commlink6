package de.rpgframework.eden.roll20.sr6;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.ASpell.Duration;
import de.rpgframework.shadowrun.ASpell.Range;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualFeatureReference;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.items.AmmunitionSlot;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class Converter {

	private final static Logger logger = System.getLogger(Converter.class.getPackageName());

	private static Map<String, List<String>> mapBelongs = new HashMap<>();

	//-------------------------------------------------------------------
	private static void addBlob(String key, DataItem item, Locale loc) {
		List<String> list = mapBelongs.get(key);
		if (list==null) {
			list = new ArrayList<>();
			mapBelongs.put(key, list);
		}
		String toAdd = getCategoryPage(item, loc);
		list.add(toAdd);

		logger.log(Level.ERROR, "Add "+key+"  "+toAdd);
	}

	//-------------------------------------------------------------------
	static String getCategoryPage(DataItem item, Locale loc) {
		if (item instanceof SR6NPC) {
			SR6NPC npc = (SR6NPC)item;
			switch (npc.getType()) {
			case GRUNT: return "NPCs:"+npc.getName(loc);
			}
		}
		if (item instanceof ItemTemplate) {
			ItemTemplate temp = (ItemTemplate)item;
			switch (temp.getItemType()) {
			case WEAPON_FIREARMS: case WEAPON_RANGED: case WEAPON_SPECIAL: return "Ranged:"+temp.getName(loc);
			case WEAPON_CLOSE_COMBAT: return "Melee:"+temp.getName(loc);
			case SOFTWARE: return "Programs:"+temp.getName(loc);
			default:
				return "Gear:"+temp.getName(loc);
			}
		}
		System.err.println("Converter.getCategoryPage: No category support for "+item);
		return item.toString();
	}

	//-------------------------------------------------------------------
	public static String getCategoryName(Class<? extends DataItem> cls) {
		if (cls==SR6NPC.class) {
			throw new IllegalArgumentException("Does not work here - depends on instance type");
		}
		if (cls==ComplexForm.class) return "Forms";
		System.err.println("Converter.getCategoryPage: No category support for "+cls);
		return "??";
	}

	//-------------------------------------------------------------------
	public static String makeBelongsTo(String first, DataItem keyItem, Locale loc) {
		List<String> list = mapBelongs.get(keyItem.toString());
		if (list==null) return first;

		List<String> ret = new ArrayList<>();
		ret.add(first);
		ret.addAll(list);
		return String.join(",", ret);
	}

	//-------------------------------------------------------------------
	public static void convertAdeptPower(AdeptPower item, Locale loc, Row row, int col) {
		int x = col;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getActivation().name().toLowerCase());
		row.createCell(x++, CellType.STRING).setCellValue(item.getActivation().getName(Locale.ENGLISH));
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1));
		if (item.hasLevel()) {
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1)+" PP / level");

		} else {
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1)+" PP");
		}
	}

	//-------------------------------------------------------------------
	private static String mapModifications(ComplexDataItem item) {
		if (!item.getModifications().isEmpty()) {
			List<String> modNames = new ArrayList<>();
			for (Modification tmp : item.getModifications()) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					switch ((ShadowrunReference)mod.getReferenceType()) {
					case ATTRIBUTE:
					case SKILL:
						modNames.add(mod.getKey()+":"+mod.getRawValue());
						break;
					}
				}
			}
			if (modNames.isEmpty()) return null;
			return  "{"+String.join(", ", modNames)+"}";
		}
		return  null;
	}

	//-------------------------------------------------------------------
	private static String mapModifications(ComplexDataItemValue item) {
		if (!item.getModifications().isEmpty()) {
			List<String> modNames = new ArrayList<>();
			for (Modification tmp : item.getModifications()) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					switch ((ShadowrunReference)mod.getReferenceType()) {
					case ATTRIBUTE:
					case SKILL:
						modNames.add(mod.getKey()+":"+mod.getRawValue());
						break;
					}
				}
			}
			if (modNames.isEmpty()) return null;
			return  "{"+String.join(", ", modNames)+"}";
		}
		return  null;
	}

	//-------------------------------------------------------------------
	public static void convertAugmentation(ItemTemplate item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		x++; // Variant
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue( formatAvailability(item.getAttribute(SR6ItemAttribute.AVAILABILITY),loc));
		else x++;
		// Nuyen
		if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved()) {
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.PRICE).getValue());
		} else x++;
		if (item.getAttribute(SR6ItemAttribute.PRICE).getLookupTable()!=null) {
			row.createCell(x++, CellType.STRING).setCellValue("TABLE");
		} else
			row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue()));

		// Has rating
		row.createCell(x++, CellType.BOOLEAN).setCellValue( (item.getAttribute(SR6ItemAttribute.PRICE).getRawValue().contains("RATING"))?"true":"false");
		// Capacity cost
		Usage usage = item.getUsage(CarryMode.EMBEDDED);
		if (usage!=null)
			row.createCell(x++, CellType.STRING).setCellValue(usage.getRawValue());
		else x++;
		// Essence cost
		usage = item.getUsage(CarryMode.IMPLANTED);
		if (item.getAttribute(SR6ItemAttribute.ESSENCECOST)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.ESSENCECOST).getRawValue()));
		else if (usage!=null) {
			row.createCell(x++, CellType.STRING).setCellValue(usage.getRawValue());
		}
		else x++;
		// Modifications
		String mods = mapModifications(item);
		if (mods!=null) {
			row.createCell(x++, CellType.STRING).setCellValue(mods);
		}
		else x++;
	}

	//-------------------------------------------------------------------
	public static void convertAugmentation(CarriedItem<ItemTemplate> item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue( ((ItemType)item.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue()).name());
		row.createCell(x++, CellType.STRING).setCellValue( ((ItemSubType)item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue()).name());
		// data-app_commlinkVariantID
		if (item.getVariantID()!=null)
			row.createCell(x++, CellType.STRING).setCellValue( item.getVariantID() );
		else x++;

		// data-availability
		if (item.hasAttribute(SR6ItemAttribute.AVAILABILITY))
			row.createCell(x++, CellType.STRING).setCellValue( formatAvailability(item.getAsObject(SR6ItemAttribute.AVAILABILITY), loc) );
		else x++;

		// data-cost
		row.createCell(x++, CellType.NUMERIC).setCellValue( item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		// data-cost_text
		row.createCell(x++, CellType.STRING).setCellValue( prettyRating(item.getResolved().getAttribute(SR6ItemAttribute.PRICE).getRawValue())+" \u00A5");
		// Has rating
		row.createCell(x++, CellType.BOOLEAN).setCellValue( (item.getResolved().getAttribute(SR6ItemAttribute.PRICE).getRawValue().contains("RATING"))?"true":"false");
		// Capacity cost
		if (item.hasAttribute(SR6ItemAttribute.CAPACITY))
			row.createCell(x++, CellType.STRING).setCellValue( item.getAsValue(SR6ItemAttribute.CAPACITY).getModifiedValue() );
		else x++;
		// Essence cost
		if (item.hasAttribute(SR6ItemAttribute.ESSENCECOST))
			row.createCell(x++, CellType.STRING).setCellValue( String.format("%1.2f", item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue() ));
		else x++;
		// Modifications
		String mods = mapModifications(item);
		if (mods!=null) {
			row.createCell(x++, CellType.STRING).setCellValue(mods);
		}
		else x++;
	}

	//-------------------------------------------------------------------
	private static String prettyRating(String input) {
		if (!input.contains("$RATING")) return input;
		String r1 = input.replace("$RATING", "Rating");
		r1 = r1.replace("Rating*", "Rating x ");
		r1 = r1.replace("*Rating", " x Rating");
		return r1;
	}

	//-------------------------------------------------------------------
	private static String mapDataSkill(ItemTemplate item, Locale loc) {
		String id = item.getAttribute(SR6ItemAttribute.SKILL).getRawValue();
		SR6Skill skill = Shadowrun6Core.getSkill(id);
		return skill.getName(loc);
	}

	//-------------------------------------------------------------------
	public static void convertWeapon(ItemTemplate item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(formatAvailability(item.getAttribute(SR6ItemAttribute.AVAILABILITY),loc));
		else x++;
		// Nuyen
		if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved())
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.PRICE).getValue());
		else x++;
		row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue()+" \u00A5"));

		if (item.getAttribute(SR6ItemAttribute.SKILL)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(mapDataSkill(item,loc));
		else x++;
		if (item.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION).getRawValue());
		else x++;
		if (item.getAttribute(SR6ItemAttribute.DAMAGE)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue());
		else x++;
		if (item.getAttribute(SR6ItemAttribute.ATTACK_RATING)!=null) {
			try {
				int[] ar = item.getAttribute(SR6ItemAttribute.ATTACK_RATING).getValue();
				if (ar[0]>0) row.createCell(x++, CellType.NUMERIC).setCellValue(ar[0]); else x++;
				if (ar[1]>0) row.createCell(x++, CellType.NUMERIC).setCellValue(ar[1]); else x++;
				if (ar[2]>0) row.createCell(x++, CellType.NUMERIC).setCellValue(ar[2]); else x++;
				if (ar[3]>0) row.createCell(x++, CellType.NUMERIC).setCellValue(ar[3]); else x++;
				if (ar[4]>0) row.createCell(x++, CellType.NUMERIC).setCellValue(ar[4]); else x++;
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error: "+e);
			}
		}
		else x+=5;
		// Firemodes
		if (item.getAttribute(SR6ItemAttribute.FIREMODES)!=null) {
			List<FireMode> modes = item.getAttribute(SR6ItemAttribute.FIREMODES).getValue();
			List<String> names = modes.stream().map(m -> map(m)).collect(Collectors.toList());
			row.createCell(x++, CellType.STRING).setCellValue(String.join("/", names));
		} else x++;
		// Ammunition
		if (item.getAttribute(SR6ItemAttribute.AMMUNITION)!=null) {
			List<AmmunitionSlot> modes = item.getAttribute(SR6ItemAttribute.AMMUNITION).getValue();
			List<String> names = modes.stream().map(m -> map(m)).collect(Collectors.toList());
			row.createCell(x++, CellType.STRING).setCellValue(String.join("/", names));
		} else x++;
	}

	//-------------------------------------------------------------------
	public static void convertVehicle(ItemTemplate item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (ItemType.isDrone(item.getItemType())) {
			row.createCell(x++, CellType.STRING).setCellValue("Drone");
		} else {
			row.createCell(x++, CellType.STRING).setCellValue("Vehicle");
		}
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(formatAvailability(item.getAttribute(SR6ItemAttribute.AVAILABILITY),loc));
		else x++;
		// Nuyen
		if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved())
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.PRICE).getValue());
		else x++;
		row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue()+" \u00A5"));
		row.createCell(x++, CellType.NUMERIC).setCellValue(mapAsDroneType(item.getItemType(), item.getItemSubtype()));

		x = convertOnRoadOffRoad(item, loc, row, x, item.getAttribute(SR6ItemAttribute.HANDLING));
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.ACCELERATION).getRawValue());
		x = convertOnRoadOffRoad(item, loc, row, x, item.getAttribute(SR6ItemAttribute.SPEED_INTERVAL));
		x = convertOnRoadOffRoad(item, loc, row, x, item.getAttribute(SR6ItemAttribute.TOPSPEED));
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.BODY).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.ARMOR).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PILOT).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.SENSORS).getRawValue());
		if (item.getAttribute(SR6ItemAttribute.SEATS)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.SEATS).getRawValue());
		else x++;

		int phys = Math.round(item.getAttribute(SR6ItemAttribute.BODY).getDistributed()/2.0f) +8;
		row.createCell(x++, CellType.NUMERIC).setCellValue(phys);
		int matr = Math.round(item.getAttribute(SR6ItemAttribute.SENSORS).getDistributed()/2.0f) +8;
		row.createCell(x++, CellType.NUMERIC).setCellValue(matr);
	}

	//-------------------------------------------------------------------
	private static int convertOnRoadOffRoad(ItemTemplate item, Locale loc, Row row, int x, ItemAttributeDefinition def) {
		if (def.isInteger()) {
			int onOff = (Integer)def.getValue();
			row.createCell(x++, CellType.NUMERIC).setCellValue( onOff);
			row.createCell(x++, CellType.NUMERIC).setCellValue( onOff);
		} else {
			OnRoadOffRoadValue onOff = def.getValue();
			row.createCell(x++, CellType.NUMERIC).setCellValue( onOff.getOnRoad());
			row.createCell(x++, CellType.NUMERIC).setCellValue( onOff.getOffRoad());
		}
		return x;
	}

	//-------------------------------------------------------------------
	public static void convertOtherGear(ItemTemplate item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null) {
			row.createCell(x++, CellType.STRING).setCellValue(formatAvailability(item.getAttribute(SR6ItemAttribute.AVAILABILITY), loc));
		} else x++;
		if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved())
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.PRICE).getValue());
		else x++;
		row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue()));
		row.createCell(x++, CellType.BOOLEAN).setCellValue( item.getChoice(ItemTemplate.UUID_RATING)!=null);
	}

	//-------------------------------------------------------------------
	public static void convertDevice(ItemTemplate item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		row.createCell(x++, CellType.STRING).setCellValue("Devices");
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue( formatAvailability( item.getAttribute(SR6ItemAttribute.AVAILABILITY), loc));
		else x++;
		if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved())
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.PRICE).getValue());
		else x++;
		row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue()));
		switch (item.getItemSubtype()) {
		case COMMLINK:
			row.createCell(x++, CellType.STRING).setCellValue( "Commlink"); break;
		case CYBERDECK:
			row.createCell(x++, CellType.STRING).setCellValue( "Cyberdeck"); break;
		case RIGGER_CONSOLE:
			row.createCell(x++, CellType.STRING).setCellValue( "Command Console"); break;
		case TAC_NET:
			row.createCell(x++, CellType.STRING).setCellValue( "M-TOC"); break;
		default:
			if ("cyberjack".equals(item.getId())) {
				row.createCell(x++, CellType.STRING).setCellValue( "Cyberjack");
			} else if ("control_rig".equals(item.getId())) {
				row.createCell(x++, CellType.STRING).setCellValue( "Control Rig");
			} else
				x++;
		}
		row.createCell(x++, CellType.BOOLEAN).setCellValue( item.getChoice(ItemTemplate.UUID_RATING)!=null);
		if (item.getAttribute(SR6ItemAttribute.DEVICE_RATING)!=null) {
			if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved())
				row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.DEVICE_RATING).getValue());
			else
				row.createCell(x++, CellType.STRING).setCellValue( item.getAttribute(SR6ItemAttribute.DEVICE_RATING).getRawValue());
		} else x++;
		if (item.getAttribute(SR6ItemAttribute.ATTACK)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.ATTACK).getValue());
		else x++;
		if (item.getAttribute(SR6ItemAttribute.SLEAZE)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.SLEAZE).getValue());
		else x++;
		if (item.getAttribute(SR6ItemAttribute.DATA_PROCESSING)!=null) {
			if (item.getAttribute(SR6ItemAttribute.DATA_PROCESSING).getFormula().isResolved())
				row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.DATA_PROCESSING).getValue());
			else
				row.createCell(x++, CellType.STRING).setCellValue( item.getAttribute(SR6ItemAttribute.DATA_PROCESSING).getRawValue());
		} else x++;
		if (item.getAttribute(SR6ItemAttribute.FIREWALL)!=null) {
			if (item.getAttribute(SR6ItemAttribute.FIREWALL).getFormula().isResolved())
				row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.FIREWALL).getValue());
			else
				row.createCell(x++, CellType.STRING).setCellValue( item.getAttribute(SR6ItemAttribute.FIREWALL).getRawValue());
		} else x++;
		if (item.getAttribute(SR6ItemAttribute.CONCURRENT_PROGRAMS)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.CONCURRENT_PROGRAMS).getValue());
		else x++;
	}

	//-------------------------------------------------------------------
	public static void convertArmor(ItemTemplate item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(formatAvailability(item.getAttribute(SR6ItemAttribute.AVAILABILITY),loc));
		else x++;
		if (item.getAttribute(SR6ItemAttribute.PRICE).getFormula().isResolved())
			row.createCell(x++, CellType.NUMERIC).setCellValue( (int)item.getAttribute(SR6ItemAttribute.PRICE).getValue());
		else x++;
		row.createCell(x++, CellType.STRING).setCellValue(prettyRating(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue()));

		if (item.getAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL).getRawValue());
		else x++;
		if (item.getAttribute(SR6ItemAttribute.DEFENSE_SOCIAL)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.DEFENSE_SOCIAL).getRawValue());
		else x++;
	}

//	//-------------------------------------------------------------------
//	public static ItemData<FVTTGear> convert(CarriedItem<ItemTemplate> val, Locale loc) {
//		ItemData<FVTTGear> ret = convert(val.getModifyable(), loc);
//		FVTTGear fVal = ret.getData();
//		// Value fields
////		fVal.customName = val.get;
////		fVal.explain = val.getDescription();
//
//		return ret;
//	}
//
////	//-------------------------------------------------------------------
////	public static Item<FVTTAdeptPower> convertQuality(AdeptPowerValue val, Locale loc) {
////		Item<FVTTAdeptPower> ret = convertAdeptPower(val.getModifyable(), loc);
////		FVTTAdeptPower fVal = ret.getData();
////		// Value fields
////		fVal.choice = val.getChoice();
////
////		return ret;
////	}

	//-------------------------------------------------------------------
	public static void convertQuality(Quality item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.STRING).setCellValue(item.getType().name().toLowerCase());
		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isPositive());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getMax());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getKarmaCost());
		row.createCell(x++, CellType.STRING).setCellValue(Shadowrun6CompendiumFactory.extractGameEffect(Shadowrun6CompendiumFactory.pretty(item.getDescription(loc))));
		row.createCell(x++, CellType.STRING).setCellValue(Shadowrun6CompendiumFactory.getCostText(item));
	}

	//-------------------------------------------------------------------
	private static String map(Range value) {
		switch (value) {
		case LINE_OF_SIGHT: return "LOS";
		case LINE_OF_SIGHT_AREA: return "LOS(A)";
		case SELF: return "SELF";
		case SELF_AREA: return "SELF(A)";
		case SPECIAL: return "SPECIAL";
		case TOUCH: return "T";
		default:
			return value.name().toLowerCase();
		}
	}

	//-------------------------------------------------------------------
	private static String map(Duration value) {
		switch (value) {
		case INSTANTANEOUS: return "Instant";
		case SUSTAINED: return "Sustained";
		case LIMITED: return "Limited";
		case PERMANENT: return "Permanent";
		case SPECIAL: return "Special";
		case ALWAYS: return "Always";
		default:
			return value.name().toUpperCase();
		}
	}

	//-------------------------------------------------------------------
	private static String map(DamageType value) {
		switch (value) {
		case STUN: return "Stun";
		case PHYSICAL: return "Physical";
		case PHYSICAL_SPECIAL: return "Physical, Special";
		case STUN_SPECIAL: return "Stun, Special";
		default:
			return value.name();
		}
	}

	//-------------------------------------------------------------------
	private static String map(SpellFeature value) {
		switch (value.getId()) {
		case "area": return "Area";
		case "indirect": return "Indirect Combat";
		case "direct": return "Direct Combat";
		case "sense_single": return "Single-Sense";
		case "sense_multi": return "Multi-Sense";
		default:
		return value.getId();
		}
	}

	//-------------------------------------------------------------------
	private static String map(FireMode value) {
		switch (value) {
		case BURST_FIRE: return "BF";
		case FULL_AUTO: return "FA";
		case SEMI_AUTOMATIC: return "SA";
		case SINGLE_SHOT: return "SS";
		default:
		return value.name();
		}
	}

	//-------------------------------------------------------------------
	private static String map(AmmunitionSlot value) {
		switch (value.getType()) {
		case BELT: return value.getAmount()+"(b)";
		case BREAK_ACTION: return value.getAmount()+"(ba)";
		case CLIP: return value.getAmount()+"(c)";
		case CYLINDER: return value.getAmount()+"(cy)";
		case DRUM: return value.getAmount()+"(d)";
		case MAGAZINE: return value.getAmount()+"(m)";
		case MUZZLE_LOADER: return value.getAmount()+"(ml)";
		default:
		return value.getType().name();
		}
	}

	//-------------------------------------------------------------------
	private static String map(ASpell.Type value) {
		switch (value) {
		case MANA: return "M";
		case PHYSICAL: return "P";
		case RESONANCE: return "R";
		default:
		return value.name();
		}
	}

	//-------------------------------------------------------------------
	private static String map(CritterPower.Action value) {
		switch (value) {
		case MAJOR: return "Major";
		case MINOR: return "Minor";
		case AUTO: return "Auto";
		default:
		return value.name();
		}
	}

	//-------------------------------------------------------------------
	private static String mapAsDroneType(ItemType type, ItemSubType sub) {
		switch (sub) {
		case BIKES: return "Bike";
		case ATVS: return "ATV";
		case CARS: return "Car";
		case TRUCKS: return "Truck";
		case VANS: return "Van";
		case TRACKED: return "Tracked";
		case HOVERCRAFT: return "Hovercraft";
		case PWC: return "PWC";
		case BOATS: return "Boat";
		case SHIPS: return "Ship";
		case SUBMARINES: return "Submarine";
		case FIXED_WING: return "Fixed Wing Aircraft";
		case ROTORCRAFT: return "Rotorcraft";
		case VTOL: return "VTOL/VSTOL";
		case LTAV: return "LTAV";
		case LAV: return "LAV";
		case GRAV: return "Gravity";
		case MOD_TRAILER: return "Trailer";
		case MICRODRONES: return "Microdrone";
		case MINIDRONES: return "Minidrone";
		case SMALL_DRONES: return "Small Drone";
		case MEDIUM_DRONES: return "Medium Drone";
		case LARGE_DRONES: return "Large Drone";
		default:
			switch (type) {
			case DRONE_MICRO: return "Micro Drone";
			case DRONE_MINI: return "Mini Drone";
			case DRONE_SMALL: return "Small Drone";
			case DRONE_MEDIUM: return "Medium Drone";
			case DRONE_LARGE: return "Large Drone";
			}
			return "?"+sub.getName()+"?";
		}
	}

	//-------------------------------------------------------------------
	private static String formatAvailability(ItemAttributeDefinition def, Locale loc) {
		StringBuffer buf = new StringBuffer();
		if (def.getFormula().isResolved()) {
			Availability avail = def.getValue();
			if (avail.isAddToAvailability())
				buf.append("+");
			buf.append(avail.getValue());
			switch (avail.getLegality()) {
			case FORBIDDEN: buf.append("(I)"); break;
			case RESTRICTED: buf.append("(L)"); break;
			}
			return buf.toString();
		} else {
			String val = def.getRawValue().trim();
			if (val.endsWith("L"))
				return val.substring(0, val.length()-2)+"(L)";
			if (val.endsWith("I"))
				return val.substring(0, val.length()-2)+"(I)";
			return def.getRawValue();
		}
	}

	//-------------------------------------------------------------------
	private static String formatAvailability(ItemAttributeObjectValue def, Locale loc) {
		StringBuffer buf = new StringBuffer();
			Availability avail = (Availability) def.getModifiedValue();
			if (avail.isAddToAvailability())
				buf.append("+");
			buf.append(avail.getValue());
			switch (avail.getLegality()) {
			case FORBIDDEN: buf.append("(I)"); break;
			case RESTRICTED: buf.append("(L)"); break;
			}
			return buf.toString();
	}

	//-------------------------------------------------------------------
	private static String upFirst(String value) {
		StringBuffer buf = new StringBuffer(value);
		buf.setCharAt(0, Character.toUpperCase(value.charAt(0)));
		return buf.toString();
	}

	//-------------------------------------------------------------------
	public static void convertSpell(SR6Spell item, Locale loc, Row row, int x) {
		List<String> feats = new ArrayList<>();
		List<String> featNames = new ArrayList<>();
		for (SpellFeatureReference feat :  item.getFeatures()) {
			feats.add(map(feat.getFeature()));
			featNames.add(feat.getNameWithoutRating());
		}

		row.createCell(x++, CellType.STRING).setCellValue(item.getType().name().toLowerCase());
		row.createCell(x++, CellType.STRING).setCellValue(map(item.getRange()));
		row.createCell(x++, CellType.STRING).setCellValue(map(item.getDuration()));
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getDrain());
		row.createCell(x++, CellType.STRING).setCellValue(upFirst(item.getCategory().name().toLowerCase()));
		row.createCell(x++, CellType.STRING).setCellValue(String.join(", ", feats));
		row.createCell(x++, CellType.STRING).setCellValue("sorcery");
		if (item.getDamage()!=null)
			row.createCell(x++, CellType.STRING).setCellValue(map(item.getDamage()));
		else
			x++;
		row.createCell(x++, CellType.STRING).setCellValue(item.getCategory().getName(Locale.ENGLISH));
		row.createCell(x++, CellType.STRING).setCellValue(item.getDuration().getName(Locale.ENGLISH));
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getDrain());
		row.createCell(x++, CellType.STRING).setCellValue(item.getRange().getName(Locale.ENGLISH));
		row.createCell(x++, CellType.STRING).setCellValue(item.getType().getName(Locale.ENGLISH));
		if (item.getDamage()!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getDamage().getName(Locale.ENGLISH));
		else
			x++;
//		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isOpposed());
//		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isEssence());
//		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isWild());

		row.createCell(x++, CellType.STRING).setCellValue(String.join(", ", featNames));

		// Description

	}

	//-------------------------------------------------------------------
	public static void convertRitual(Ritual item, Locale loc, Row row, int x) {
		List<String> feats = new ArrayList<>();
		List<String> featNames = new ArrayList<>();
		for (RitualFeatureReference feat :  item.getFeatures()) {
			feats.add(feat.getKey());
			featNames.add(feat.getModifyable().getName(loc));
		}

		row.createCell(x++, CellType.STRING).setCellValue(item.getThreshold());
		row.createCell(x++, CellType.STRING).setCellValue(String.join(", ", featNames));
	}

	//-------------------------------------------------------------------
	public static void convertComplexForm(ComplexForm item, Locale loc, Row row, int x) {
		// data-duration
		row.createCell(x++, CellType.STRING).setCellValue(item.getDuration().name().toLowerCase());
		// data-fading
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getFading());
	}

	//-------------------------------------------------------------------
	public static void convertEcho(MetamagicOrEcho item, Locale loc, Row row, int x) {
		// data-rating
		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.hasLevels());
	}

	//-------------------------------------------------------------------
	public static void convertCritterPower(CritterPower item, Locale loc, Row row, int x) {
		List<String> feats = new ArrayList<>();

		row.createCell(x++, CellType.STRING).setCellValue(map(item.getType()));
		row.createCell(x++, CellType.STRING).setCellValue(map(item.getAction()));
		row.createCell(x++, CellType.STRING).setCellValue(map(item.getRange()));
		row.createCell(x++, CellType.STRING).setCellValue(map(item.getDuration()));
		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.hasLevel());
	}

	//-------------------------------------------------------------------
	private static int createNormalAndModified(SR6NPC item, Row row, ShadowrunAttribute attr, int x) {
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(attr).getDistributed());
		if (item.getAttribute(attr).getModifier()>0)
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(attr).getModifiedValue());
		else x++;
		return x;
	}

	//-------------------------------------------------------------------
	private static void convertLifeform(SR6NPC item, Locale loc, Row row, int x) {
		x = createNormalAndModified(item, row, ShadowrunAttribute.BODY, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.AGILITY, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.REACTION, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.STRENGTH, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.WILLPOWER, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.LOGIC, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.INTUITION, x);
		x = createNormalAndModified(item, row, ShadowrunAttribute.CHARISMA, x);

		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(ShadowrunAttribute.EDGE).getModifiedValue());
//		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(ShadowrunAttribute.ESSENCE).getModifiedValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue());
	}

	//-------------------------------------------------------------------
	public static void convertGrunt(SR6NPC item, Locale loc, Row row, int x) {
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getRating());
		convertLifeform(item, loc, row, x);
	}

	//-------------------------------------------------------------------
	public static void convertGruntBlobs(SR6NPC item, Locale loc) {
		for (SpellValue<SR6Spell> spell : item.getSpells()) {
			String key = spell.getResolved().toString();
			addBlob(key, item, loc);
		}
		for (CarriedItem tmp : item.getGear()) {
			String key = tmp.getResolved().toString();
			addBlob(key, item, loc);
		}
	}

//	//-------------------------------------------------------------------
//	public static ActorData<? extends FVTTGear> convertActor(ItemTemplate item, Locale loc) {
//		FVTTVehicleActor actor = new FVTTVehicleActor();
//
//		ActorData foundry = new ActorData(item.getName(loc), "vehicle", actor);
//		return foundry;
//	}
//
//	//-------------------------------------------------------------------
//	private static void fillAttributes(LifeformActor actor, Lifeform<ShadowrunAttribute,SR6Skill,SR6SkillValue> life) {
//		actor.attributes.agi.base = life.getAttribute(ShadowrunAttribute.AGILITY).getDistributed();
//		actor.attributes.agi.mod  = life.getAttribute(ShadowrunAttribute.AGILITY).getModifier();
//		actor.attributes.agi.pool = life.getAttribute(ShadowrunAttribute.AGILITY).getModifiedValue();
//		actor.attributes.bod.base = life.getAttribute(ShadowrunAttribute.BODY).getDistributed();
//		actor.attributes.bod.mod  = life.getAttribute(ShadowrunAttribute.BODY).getModifier();
//		actor.attributes.bod.pool = life.getAttribute(ShadowrunAttribute.BODY).getModifiedValue();
//		actor.attributes.cha.base = life.getAttribute(ShadowrunAttribute.CHARISMA).getDistributed();
//		actor.attributes.cha.mod  = life.getAttribute(ShadowrunAttribute.CHARISMA).getModifier();
//		actor.attributes.cha.pool = life.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue();
//		actor.attributes.inn.base = life.getAttribute(ShadowrunAttribute.INTUITION).getDistributed();
//		actor.attributes.inn.mod  = life.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
//		actor.attributes.inn.pool = life.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue();
//		actor.attributes.log.base = life.getAttribute(ShadowrunAttribute.LOGIC).getDistributed();
//		actor.attributes.log.mod  = life.getAttribute(ShadowrunAttribute.LOGIC).getModifier();
//		actor.attributes.log.pool = life.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue();
//		actor.attributes.rea.base = life.getAttribute(ShadowrunAttribute.REACTION).getDistributed();
//		actor.attributes.rea.mod  = life.getAttribute(ShadowrunAttribute.REACTION).getModifier();
//		actor.attributes.rea.pool = life.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue();
//		actor.attributes.str.base = life.getAttribute(ShadowrunAttribute.STRENGTH).getDistributed();
//		actor.attributes.str.mod  = life.getAttribute(ShadowrunAttribute.STRENGTH).getModifier();
//		actor.attributes.str.pool = life.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue();
//		actor.attributes.wil.base = life.getAttribute(ShadowrunAttribute.WILLPOWER).getDistributed();
//		actor.attributes.wil.mod  = life.getAttribute(ShadowrunAttribute.WILLPOWER).getModifier();
//		actor.attributes.wil.pool = life.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue();
//		actor.attributes.mag.base = life.getAttribute(ShadowrunAttribute.MAGIC).getDistributed();
//		actor.attributes.mag.mod  = life.getAttribute(ShadowrunAttribute.MAGIC).getModifier();
//		actor.attributes.mag.pool = life.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue();
//		actor.attributes.res.base = life.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed();
//		actor.attributes.res.mod  = life.getAttribute(ShadowrunAttribute.RESONANCE).getModifier();
//		actor.attributes.res.pool = life.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue();
//		actor.edge.max = life.getAttribute(ShadowrunAttribute.EDGE).getModifiedValue();
//	}
//
//	//-------------------------------------------------------------------
//	private static void fillSkillValue(ActionSkillValue fvtt, SR6SkillValue val) {
//		if (val==null)
//			return;
//		fvtt.points = val.getDistributed();
//		fvtt.modifier = val.getModifier();
//
//		for (SkillSpecializationValue<SR6Skill> spec : val.getSpecializations()) {
//			if (spec.getDistributed()==1)
//				fvtt.specialization = spec.getKey();
//			if (spec.getDistributed()==2)
//				fvtt.expertise = spec.getKey();
//		}
//	}
//
//	//-------------------------------------------------------------------
//	private static void fillSkills(LifeformActor actor, Lifeform<ShadowrunAttribute,SR6Skill,SR6SkillValue> life) {
//		fillSkillValue( actor.skills.astral      , life.getSkillValue(Shadowrun6Core.getSkill("astral")));
//		fillSkillValue( actor.skills.athletics   , life.getSkillValue(Shadowrun6Core.getSkill("athletics")));
//		fillSkillValue( actor.skills.biotech     , life.getSkillValue(Shadowrun6Core.getSkill("biotech")));
//		fillSkillValue( actor.skills.close_combat, life.getSkillValue(Shadowrun6Core.getSkill("close_combat")));
//		fillSkillValue( actor.skills.con         , life.getSkillValue(Shadowrun6Core.getSkill("con")));
//		fillSkillValue( actor.skills.conjuring   , life.getSkillValue(Shadowrun6Core.getSkill("conjuring")));
//		fillSkillValue( actor.skills.cracking    , life.getSkillValue(Shadowrun6Core.getSkill("cracking")));
//		fillSkillValue( actor.skills.electronics    , life.getSkillValue(Shadowrun6Core.getSkill("electronics")));
//		fillSkillValue( actor.skills.enchanting  , life.getSkillValue(Shadowrun6Core.getSkill("enchanting")));
//		fillSkillValue( actor.skills.engineering , life.getSkillValue(Shadowrun6Core.getSkill("engineering")));
//		fillSkillValue( actor.skills.exotic_weapons, life.getSkillValue(Shadowrun6Core.getSkill("exotic_weapons")));
//		fillSkillValue( actor.skills.firearms    , life.getSkillValue(Shadowrun6Core.getSkill("firearms")));
//		fillSkillValue( actor.skills.influence   , life.getSkillValue(Shadowrun6Core.getSkill("influence")));
//		fillSkillValue( actor.skills.outdoors    , life.getSkillValue(Shadowrun6Core.getSkill("outdoors")));
//		fillSkillValue( actor.skills.perception  , life.getSkillValue(Shadowrun6Core.getSkill("perception")));
//		fillSkillValue( actor.skills.piloting    , life.getSkillValue(Shadowrun6Core.getSkill("piloting")));
//		fillSkillValue( actor.skills.sorcery     , life.getSkillValue(Shadowrun6Core.getSkill("sorcery")));
//		fillSkillValue( actor.skills.stealth     , life.getSkillValue(Shadowrun6Core.getSkill("stealth")));
//		fillSkillValue( actor.skills.tasking     , life.getSkillValue(Shadowrun6Core.getSkill("tasking")));
//	}
//
//	//-------------------------------------------------------------------
//	public static ActorData<? extends GeneralActor> convertActor(SR6NPC item, Locale loc) {
//		switch (item.getType()) {
//		case CRITTER:
//		case CRITTER_AWAKENED:
//			return convertCritterActor(item, loc);
//		case GRUNT:
//			return convertNPCActor(item, loc);
//		}
//		return null;
//	}
//
//	//-------------------------------------------------------------------
//	private static ActorData<FVTTCritter> convertCritterActor(SR6NPC data, Locale loc) {
//		FVTTCritter actor = new FVTTCritter();
//
//		ActorData<FVTTCritter> foundry = new ActorData<FVTTCritter>(data.getName(loc), "Critter", actor);
//		fillAttributes(foundry.data, data);
//		fillSkills(foundry.data, data);
//
//		data.getQualities().forEach(tmp -> foundry.addItem(convertQuality(tmp,loc)));
//		data.getCritterPowers().forEach(tmp -> foundry.addItem(convert(tmp,loc)));
//		data.getGear().forEach(tmp -> {
//			foundry.addItem(convert(tmp,loc));});
//
//		return foundry;
//	}
//
//	//-------------------------------------------------------------------
//	private static ActorData<FVTTNPCActor> convertNPCActor(SR6NPC data, Locale loc) {
//		FVTTNPCActor actor = new FVTTNPCActor();
//
//		ActorData<FVTTNPCActor> foundry = new ActorData<FVTTNPCActor>(data.getName(loc), "NPC", actor);
//		fillAttributes(foundry.data, data);
//		fillSkills(foundry.data, data);
//
//
//		return foundry;
//	}

}
