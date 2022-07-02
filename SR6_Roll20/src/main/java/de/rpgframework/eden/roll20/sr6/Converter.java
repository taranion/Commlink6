package de.rpgframework.eden.roll20.sr6;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class Converter {
	
	private final static Logger logger = System.getLogger(Converter.class.getPackageName());

	//-------------------------------------------------------------------
	public static void convertAdeptPower(AdeptPower item, Locale loc, Row row) {
		int x = 4;
		row.createCell(x++, CellType.STRING).setCellValue(item.getActivation().name().toLowerCase());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getCostForLevel(1));
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

//	//-------------------------------------------------------------------
//	public static ItemData<FVTTQuality> convertQuality(QualityValue val, Locale loc) {
//		ItemData<FVTTQuality> ret = convertQuality(val.getModifyable(), loc);
//		FVTTQuality fVal = ret.getData();
//		// Value fields
//		fVal.value = val.getModifiedValue();
//		fVal.explain = val.getDescription();
//
//		return ret;
//	}

	//-------------------------------------------------------------------
	public static void convertSpell(SR6Spell item, Locale loc, Row row) {
		int x=4;
		row.createCell(x++, CellType.STRING).setCellValue(item.getCategory().name().toLowerCase());
		row.createCell(x++, CellType.STRING).setCellValue(item.getDuration().name().toLowerCase());
		row.createCell(x++, CellType.NUMERIC).setCellValue(item.getDrain());
		row.createCell(x++, CellType.STRING).setCellValue(item.getRange().name().toLowerCase());
		row.createCell(x++, CellType.STRING).setCellValue(item.getType().name().toLowerCase());
		if (item.getDamage()!=null)
			row.createCell(x++, CellType.STRING).setCellValue(item.getDamage().name().toLowerCase());
		else
			x++;
//		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isOpposed());
//		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isEssence());
//		row.createCell(x++, CellType.BOOLEAN).setCellValue(item.isWild());

		List<String> feats = new ArrayList();
		for (SpellFeatureReference ref : item.getFeatures()) {
			feats.add(ref.getFeature().getId());
		}
		row.createCell(x++, CellType.STRING).setCellValue(String.join(", ", feats));

		// Description
		
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
