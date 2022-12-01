package de.rpgframework.eden.roll20.sr6;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ASpell.Duration;
import de.rpgframework.shadowrun.ASpell.Range;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualFeatureReference;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class Converter {
	
	private final static Logger logger = System.getLogger(Converter.class.getPackageName());

	//-------------------------------------------------------------------
	public static void convertAdeptPower(AdeptPower item, Locale loc, Row row) {
		int x = 4;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getActivation().name().toLowerCase());
		row.createCell(x++, CellType.STRING).setCellValue(item.getActivation().getName(Locale.ENGLISH));
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1));
		if (item.hasLevel()) {
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1)+" PP / level");
			
		} else {
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1)+" PP");
		}
	}

//	//-------------------------------------------------------------------
//	public static ItemData<FVTTCritterPower> convert(CritterPower item, Locale loc) {
//		FVTTCritterPower data = new FVTTCritterPower();
//		// Definition fields
//		data.genesisID   = item.getId();
//		data.action 	 = item.getAction().name().toLowerCase();
//		data.duration    = item.getDuration().name().toLowerCase();
//		data.range       = item.getRange().name().toLowerCase();
//		data.type        = item.getType().name().toLowerCase();
//
//		return new ItemData<FVTTCritterPower>(item.getName(loc), "critterpower", data);
//	}
//
//	//-------------------------------------------------------------------
//	public static ItemData<FVTTCritterPower> convert(CritterPowerValue val, Locale loc) {
//		ItemData<FVTTCritterPower> ret = convert(val.getModifyable(), loc);
//		FVTTCritterPower fVal = ret.getData();
//		// Value fields
////		fVal.rating = val.get
//
//		return ret;
//	}

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
	public static void convertAugmentation(ItemTemplate item, Locale loc, Row row) {
		int x = 5;
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue());
		else x++;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue());
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
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.ESSENCECOST).getRawValue());
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
	public static void convertAugmentation(CarriedItem<ItemTemplate> item, Locale loc, Row row) {
		int x = 5;
		row.createCell(x++, CellType.STRING).setCellValue( ((ItemType)item.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue()).name());
		row.createCell(x++, CellType.STRING).setCellValue( ((ItemSubType)item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue()).name());
		if (item.hasAttribute(SR6ItemAttribute.AVAILABILITY))
			row.createCell(x++, CellType.STRING).setCellValue( ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).toString() );
		else x++;
		row.createCell(x++, CellType.NUMERIC).setCellValue( item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
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
	public static void convertWeapon(ItemTemplate item, Locale loc, Row row) {
		int x = 4;
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue());
		else x++;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue());
		if (item.getAttribute(SR6ItemAttribute.SKILL)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.SKILL).getRawValue());
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
				row.createCell(x++, CellType.NUMERIC).setCellValue(ar[0]);
				row.createCell(x++, CellType.NUMERIC).setCellValue(ar[1]);
				row.createCell(x++, CellType.NUMERIC).setCellValue(ar[2]);
				row.createCell(x++, CellType.NUMERIC).setCellValue(ar[3]);
				row.createCell(x++, CellType.NUMERIC).setCellValue(ar[4]);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error: "+e);
			}
		}
		else x+=5;
	}

	//-------------------------------------------------------------------
	public static void convertVehicle(ItemTemplate item, Locale loc, Row row) {
		int x = 4;
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue());
		else x++;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue());
		row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.HANDLING).getRawValue());
		row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.ACCELERATION).getRawValue());
		row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.SPEED_INTERVAL).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.TOPSPEED).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.BODY).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.ARMOR).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PILOT).getRawValue());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.SENSORS).getRawValue());
		if (item.getAttribute(SR6ItemAttribute.SEATS)!=null)
			row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.SEATS).getRawValue());
		else x++;
	}

	//-------------------------------------------------------------------
	public static void convertOtherGear(ItemTemplate item, Locale loc, Row row) {
		int x = 5;
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue());
		else x++;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue());
	}

	//-------------------------------------------------------------------
	public static void convertArmor(ItemTemplate item, Locale loc, Row row) {
		int x = 5;
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemType().name());
		row.createCell(x++, CellType.STRING).setCellValue(item.getItemSubtype().name());
		if (item.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue());
		else x++;
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getAttribute(SR6ItemAttribute.PRICE).getRawValue());
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
	public static void convertQuality(Quality item, Locale loc, Row row) {
		int x=4;
		row.createCell(x++, CellType.STRING).setCellValue(item.getType().name().toLowerCase());
		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isPositive());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getMax());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getKarmaCost());
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
	private static String upFirst(String value) {
		StringBuffer buf = new StringBuffer(value);
		buf.setCharAt(0, Character.toUpperCase(value.charAt(0)));
		return buf.toString();
	}
	
	//-------------------------------------------------------------------
	public static void convertSpell(SR6Spell item, Locale loc, Row row) {
		List<String> feats = new ArrayList<>(); 
		List<String> featNames = new ArrayList<>();
		for (SpellFeatureReference feat :  item.getFeatures()) {
			feats.add(map(feat.getFeature()));
			featNames.add(feat.getNameWithoutRating());
		}
		
		int x=5;
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
	public static void convertRitual(Ritual item, Locale loc, Row row) {
		List<String> feats = new ArrayList<>(); 
		List<String> featNames = new ArrayList<>();
		for (RitualFeatureReference feat :  item.getFeatures()) {
			feats.add(feat.getKey());
			featNames.add(feat.getModifyable().getName(loc));
		}
		
		int x=4;
		row.createCell(x++, CellType.STRING).setCellValue(item.getThreshold());
		row.createCell(x++, CellType.STRING).setCellValue(String.join(", ", featNames));
	}
	
	//-------------------------------------------------------------------
	public static void convertComplexForm(ComplexForm item, Locale loc, Row row) {
		int x=4;
		// data-duration
		row.createCell(x++, CellType.STRING).setCellValue(item.getDuration().name().toLowerCase());
		// data-fading
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getFading());
	}

	//-------------------------------------------------------------------
	public static void convertEcho(MetamagicOrEcho item, Locale loc, Row row) {
		int x=4;
		// data-rating
		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.hasLevels());
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
