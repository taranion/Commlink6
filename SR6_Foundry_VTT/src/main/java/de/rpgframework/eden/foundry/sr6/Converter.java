package de.rpgframework.eden.foundry.sr6;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;

import de.rpgframework.foundry.ActorData;
import de.rpgframework.foundry.ItemData;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.EmbedModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.foundry.ActionSkills.ActionSkillValue;
import de.rpgframework.shadowrun6.foundry.FVTTAdeptPower;
import de.rpgframework.shadowrun6.foundry.FVTTComplexForm;
import de.rpgframework.shadowrun6.foundry.FVTTCritter;
import de.rpgframework.shadowrun6.foundry.FVTTCritterPower;
import de.rpgframework.shadowrun6.foundry.FVTTGear;
import de.rpgframework.shadowrun6.foundry.FVTTNPCActor;
import de.rpgframework.shadowrun6.foundry.FVTTQuality;
import de.rpgframework.shadowrun6.foundry.FVTTSpell;
import de.rpgframework.shadowrun6.foundry.FVTTVehicle;
import de.rpgframework.shadowrun6.foundry.FVTTVehicleActor;
import de.rpgframework.shadowrun6.foundry.FVTTWeapon;
import de.rpgframework.shadowrun6.foundry.GeneralActor;
import de.rpgframework.shadowrun6.foundry.LifeformActor;
import de.rpgframework.shadowrun6.foundry.LifeformActor.Type;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class Converter {
	
	private final static Logger logger = System.getLogger(Converter.class.getPackageName());

	//-------------------------------------------------------------------
	public static ItemData<FVTTAdeptPower> convertAdeptPower(AdeptPower item, Locale loc) {
		FVTTAdeptPower data = new FVTTAdeptPower();
		// Definition fields
		data.genesisID   = item.getId();
		data.activation	 = item.getActivation().name().toLowerCase();
		data.cost        = item.getCostForLevel(1);
		data.hasLevel    = item.hasLevel();

		return new ItemData<FVTTAdeptPower>(item.getName(loc), "adeptpower", data);
	}

//	//-------------------------------------------------------------------
//	public static Item<FVTTAdeptPower> convertQuality(AdeptPowerValue val, Locale loc) {
//		Item<FVTTAdeptPower> ret = convertAdeptPower(val.getModifyable(), loc);
//		FVTTAdeptPower fVal = ret.getData();
//		// Value fields
//		fVal.choice = val.getChoice();
//
//		return ret;
//	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTCritterPower> convert(CritterPower item, Locale loc) {
		FVTTCritterPower data = new FVTTCritterPower();
		// Definition fields
		data.genesisID   = item.getId();
		data.action 	 = item.getAction().name().toLowerCase();
		data.duration    = item.getDuration().name().toLowerCase();
		data.range       = item.getRange().name().toLowerCase();
		data.type        = item.getType().name().toLowerCase();

		return new ItemData<FVTTCritterPower>(item.getName(loc), "critterpower", data);
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTCritterPower> convert(CritterPowerValue val, Locale loc) {
		ItemData<FVTTCritterPower> ret = convert(val.getModifyable(), loc);
		FVTTCritterPower fVal = ret.getData();
		// Value fields
//		fVal.rating = val.get

		return ret;
	}

	//-------------------------------------------------------------------
	private static FVTTWeapon.FireMode toVTTMode(List<FireMode> modes) {
		FVTTWeapon.FireMode ret = new FVTTWeapon.FireMode();
		for (FireMode tmp : modes) {
			switch (tmp) {
			case SINGLE_SHOT: ret.SS = true;
			case BURST_FIRE : ret.BF = true;
			case FULL_AUTO  : ret.FA = true;
			case SEMI_AUTOMATIC: ret.SA = true;
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTGear> convertGear(ItemTemplate tmp, Locale loc) {
		FVTTGear data = new FVTTGear();
		if (ItemType.isWeapon(tmp.getItemType(CarryMode.CARRIED)))
			data = new FVTTWeapon();
		if (ItemType.isVehicle(tmp.getItemType(CarryMode.CARRIED)))
			data = new FVTTVehicle();
		// Definition fields
		data.genesisID  = tmp.getId();
		if (tmp.getItemType(CarryMode.CARRIED)!=null)
			data.type       = tmp.getItemType(CarryMode.CARRIED).name();
		if (tmp.getItemSubtype(CarryMode.CARRIED)!=null)
			data.subtype    = tmp.getItemSubtype(CarryMode.CARRIED).name();
		// If necessary, consult IMPLANTED
		if (data.type==null && tmp.getItemType(CarryMode.IMPLANTED)!=null) 
			data.type       = tmp.getItemType(CarryMode.IMPLANTED).name();
		if (data.subtype==null && tmp.getItemSubtype(CarryMode.IMPLANTED)!=null) 
			data.subtype       = tmp.getItemSubtype(CarryMode.IMPLANTED).name();
		
		if (tmp.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			data.availDef   = tmp.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue();
		if (tmp.getAttribute(SR6ItemAttribute.PRICE)!=null) {
			if (tmp.getAttribute(SR6ItemAttribute.PRICE).isInteger())
				data.price      = tmp.getAttribute(SR6ItemAttribute.PRICE).getValue();
		}
		if (tmp.getAttribute(SR6ItemAttribute.SKILL)!=null)
			data.skill      = tmp.getAttribute(SR6ItemAttribute.SKILL).getRawValue();
		if (tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION)!=null)
			data.skillSpec  = tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION).getRawValue();
		if (tmp.getAttribute(SR6ItemAttribute.ESSENCECOST)!=null)
			data.essence   = tmp.getAttribute(SR6ItemAttribute.ESSENCECOST).getDistributed();

		if ((data instanceof FVTTWeapon) && tmp.getAttribute(SR6ItemAttribute.DAMAGE)!=null) {
			((FVTTWeapon)data).dmgDef       = tmp.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue();
			try {
				((FVTTWeapon)data).attackRating = tmp.getAttribute(SR6ItemAttribute.ATTACK_RATING).getValue();
			} catch (IllegalStateException e) {
				logger.log(Level.ERROR, "Error converting {0}: {1}", tmp.getId(), e.toString());
			}
			if (tmp.getAttribute(SR6ItemAttribute.FIREMODES)!=null)
				((FVTTWeapon)data).modes        = toVTTMode(tmp.getAttribute(SR6ItemAttribute.FIREMODES).getValue());
			try {
				((FVTTWeapon)data).dmg        = ((Damage)tmp.getAttribute(SR6ItemAttribute.DAMAGE).getValue()).getModifiedValue();
			} catch (IllegalStateException e) {
				logger.log(Level.ERROR, "Error converting {0}: {1}", tmp.getId(), e.toString());
			}
		}
		// Vehicles
		if (data instanceof FVTTVehicle) {
			((FVTTVehicle)data).handlOn = tmp.getAttribute(SR6ItemAttribute.HANDLING).getDistributed();
			((FVTTVehicle)data).bod = tmp.getAttribute(SR6ItemAttribute.BODY).getDistributed();
			((FVTTVehicle)data).arm = tmp.getAttribute(SR6ItemAttribute.ARMOR).getDistributed();
			((FVTTVehicle)data).pil = tmp.getAttribute(SR6ItemAttribute.PILOT).getDistributed();
			((FVTTVehicle)data).sen = tmp.getAttribute(SR6ItemAttribute.SENSORS).getDistributed();
			if (tmp.getAttribute(SR6ItemAttribute.SEATS)!=null)
			((FVTTVehicle)data).sea = tmp.getAttribute(SR6ItemAttribute.SEATS).getDistributed();
		}

		return new ItemData<FVTTGear>(tmp.getName(loc), "gear", data);
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTGear> convertGear(CarriedItem<ItemTemplate> val, Locale loc) {
		ItemTemplate tmp = val.getResolved();
		
		FVTTGear data = new FVTTGear();
		try {
			if (ItemType.isWeapon(val.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue()))
				data = new FVTTWeapon();
			if (ItemType.isVehicle(val.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue()))
				data = new FVTTVehicle();
			// Definition fields
			data.genesisID  = tmp.getId();
			data.type       = ((ItemType)val.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue()).name();
			data.subtype    = ((ItemSubType)val.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue()).name();
			if (val.getAsObject(SR6ItemAttribute.AVAILABILITY)!=null)
				data.availDef   = ((Availability)val.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getName(loc);
			data.price   = val.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
//		if (val.getAsObject(SR6ItemAttribute.SKILL)!=null)
//			data.availDef   = ((Availability)val.getAsObject(SR6ItemAttribute.SKILL).getModifiedValue()).getName(loc);
			
			
			if (tmp.getAttribute(SR6ItemAttribute.SKILL)!=null)
				data.skill      = tmp.getAttribute(SR6ItemAttribute.SKILL).getRawValue();
			if (tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION)!=null)
				data.skillSpec  = tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION).getRawValue();
			if (val.getAsFloat(SR6ItemAttribute.ESSENCECOST)!=null)
				data.essence   = (int) val.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue();

			if ((data instanceof FVTTWeapon) && tmp.getAttribute(SR6ItemAttribute.DAMAGE)!=null) {
				((FVTTWeapon)data).dmgDef       = tmp.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue();
				try {
					((FVTTWeapon)data).attackRating = tmp.getAttribute(SR6ItemAttribute.ATTACK_RATING).getValue();
				} catch (IllegalStateException e) {
					logger.log(Level.ERROR, "Error converting {}: {}", tmp.getId(), e.toString());
				}
				if (tmp.getAttribute(SR6ItemAttribute.FIREMODES)!=null)
					((FVTTWeapon)data).modes        = toVTTMode(tmp.getAttribute(SR6ItemAttribute.FIREMODES).getValue());
				try {
					((FVTTWeapon)data).dmg        = ((Damage)tmp.getAttribute(SR6ItemAttribute.DAMAGE).getValue()).getModifiedValue();
				} catch (IllegalStateException e) {
					logger.log(Level.ERROR, "Error converting {}: {}", tmp.getId(), e.toString());
				}
			}
			// Vehicles
			if (data instanceof FVTTVehicle) {
				((FVTTVehicle)data).handlOn = tmp.getAttribute(SR6ItemAttribute.HANDLING).getDistributed();
				((FVTTVehicle)data).bod = tmp.getAttribute(SR6ItemAttribute.BODY).getDistributed();
				((FVTTVehicle)data).arm = tmp.getAttribute(SR6ItemAttribute.ARMOR).getDistributed();
				((FVTTVehicle)data).pil = tmp.getAttribute(SR6ItemAttribute.PILOT).getDistributed();
				((FVTTVehicle)data).sen = tmp.getAttribute(SR6ItemAttribute.SENSORS).getDistributed();
				if (tmp.getAttribute(SR6ItemAttribute.SEATS)!=null)
				((FVTTVehicle)data).sea = tmp.getAttribute(SR6ItemAttribute.SEATS).getDistributed();
			}
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed converting carried item for "+tmp.getId()+": "+e);
			e.printStackTrace();
			System.exit(1);
		}

		ItemData<FVTTGear> ret = new ItemData<FVTTGear>(tmp.getName(loc), "gear", data);
		ret.name = val.getNameWithRating(loc);

		return ret;
	}

//	//-------------------------------------------------------------------
//	public static Item<FVTTAdeptPower> convertQuality(AdeptPowerValue val, Locale loc) {
//		Item<FVTTAdeptPower> ret = convertAdeptPower(val.getModifyable(), loc);
//		FVTTAdeptPower fVal = ret.getData();
//		// Value fields
//		fVal.choice = val.getChoice();
//
//		return ret;
//	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTQuality> convertQuality(Quality item, Locale loc) {
		FVTTQuality fVal = new FVTTQuality();
		// Definition fields
		fVal.genesisID = item.getId();
		fVal.category  = item.getType().name();
		fVal.level     = item.getMax()>0;
		fVal.positive  = item.isPositive();

		return new ItemData<FVTTQuality>(item.getName(loc), "quality", fVal);
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTQuality> convertQuality(QualityValue val, Locale loc) {
		ItemData<FVTTQuality> ret = convertQuality(val.getModifyable(), loc);
		FVTTQuality fVal = ret.getData();
		// Value fields
		fVal.value = val.getModifiedValue();
		fVal.explain = val.getDescription();

		return ret;
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTSpell> convertSpell(SR6Spell item, Locale loc) {
		FVTTSpell spell = new FVTTSpell();

		spell.genesisID = item.getId();
		spell.data.category  = item.getCategory().name().toLowerCase();
		spell.data.duration  = item.getDuration().name().toLowerCase();
		spell.data.drain     = item.getDrain();
		spell.data.range     = item.getRange().name().toLowerCase();
		spell.data.type      = item.getType().name().toLowerCase();
		if (item.getDamage()!=null)
			spell.data.damage    = item.getDamage().name().toLowerCase();
		spell.data.isOpposed = item.isOpposed();
		spell.data.withEssence = item.isEssence();
		spell.data.wild      = item.isWild();
		for (SpellFeatureReference ref : item.getFeatures()) {
			switch (ref.getFeature().getId()) {
			case "sense_multi": spell.data.multiSense=true; break; 
			}
		}

		ItemData<FVTTSpell> foundry = new ItemData<FVTTSpell>(item.getName(loc), "spell", spell);
		return foundry;
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTSpell> convertSpell(SpellValue<SR6Spell> item, Locale loc) {
		return convertSpell(item.getModifyable(), loc);
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTComplexForm> convertComplexForm(ComplexForm item, Locale loc) {
		FVTTComplexForm spell = new FVTTComplexForm();

		spell.genesisID = item.getId();
		spell.duration  = item.getDuration().name().toLowerCase();
		spell.fading    = item.getFading();

		ItemData<FVTTComplexForm> foundry = new ItemData<FVTTComplexForm>(item.getName(loc), "complexform", spell);
		return foundry;
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTComplexForm> convertComplexForm(ComplexFormValue item, Locale loc) {
		ItemData<FVTTComplexForm> ret = convertComplexForm(item.getModifyable(), loc);
		if (item.getDecisions()!=null && !item.getDecisions().isEmpty())
			ret.getData().choice = item.getDecisions().get(0).getValue();
		
		return ret;
	}

	//-------------------------------------------------------------------
	public static ActorData<? extends FVTTGear> convertActor(ItemTemplate item, Locale loc) {
		ActorData foundry = convertVehicleActor(item, loc); //new ActorData(item.getName(loc), "vehicle", actor);
		return foundry;
	}

	//-------------------------------------------------------------------
	private static void fillAttributes(LifeformActor actor, Lifeform<ShadowrunAttribute,SR6Skill,SR6SkillValue> life) {
		actor.attributes.agi.base = life.getAttribute(ShadowrunAttribute.AGILITY).getDistributed();
		actor.attributes.agi.mod  = life.getAttribute(ShadowrunAttribute.AGILITY).getModifier();
		actor.attributes.agi.pool = life.getAttribute(ShadowrunAttribute.AGILITY).getModifiedValue();
		actor.attributes.bod.base = life.getAttribute(ShadowrunAttribute.BODY).getDistributed();
		actor.attributes.bod.mod  = life.getAttribute(ShadowrunAttribute.BODY).getModifier();
		actor.attributes.bod.pool = life.getAttribute(ShadowrunAttribute.BODY).getModifiedValue();
		actor.attributes.cha.base = life.getAttribute(ShadowrunAttribute.CHARISMA).getDistributed();
		actor.attributes.cha.mod  = life.getAttribute(ShadowrunAttribute.CHARISMA).getModifier();
		actor.attributes.cha.pool = life.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue();
		actor.attributes.inn.base = life.getAttribute(ShadowrunAttribute.INTUITION).getDistributed();
		actor.attributes.inn.mod  = life.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
		actor.attributes.inn.pool = life.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue();
		actor.attributes.log.base = life.getAttribute(ShadowrunAttribute.LOGIC).getDistributed();
		actor.attributes.log.mod  = life.getAttribute(ShadowrunAttribute.LOGIC).getModifier();
		actor.attributes.log.pool = life.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue();
		actor.attributes.rea.base = life.getAttribute(ShadowrunAttribute.REACTION).getDistributed();
		actor.attributes.rea.mod  = life.getAttribute(ShadowrunAttribute.REACTION).getModifier();
		actor.attributes.rea.pool = life.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue();
		actor.attributes.str.base = life.getAttribute(ShadowrunAttribute.STRENGTH).getDistributed();
		actor.attributes.str.mod  = life.getAttribute(ShadowrunAttribute.STRENGTH).getModifier();
		actor.attributes.str.pool = life.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue();
		actor.attributes.wil.base = life.getAttribute(ShadowrunAttribute.WILLPOWER).getDistributed();
		actor.attributes.wil.mod  = life.getAttribute(ShadowrunAttribute.WILLPOWER).getModifier();
		actor.attributes.wil.pool = life.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue();
		actor.attributes.mag.base = life.getAttribute(ShadowrunAttribute.MAGIC).getDistributed();
		actor.attributes.mag.mod  = life.getAttribute(ShadowrunAttribute.MAGIC).getModifier();
		actor.attributes.mag.pool = life.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue();
		actor.attributes.res.base = life.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed();
		actor.attributes.res.mod  = life.getAttribute(ShadowrunAttribute.RESONANCE).getModifier();
		actor.attributes.res.pool = life.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue();
		actor.edge.max = life.getAttribute(ShadowrunAttribute.EDGE).getModifiedValue();
	}

	//-------------------------------------------------------------------
	private static void fillSkillValue(ActionSkillValue fvtt, SR6SkillValue val) {
		if (val==null)
			return;
		fvtt.points = val.getDistributed();
		fvtt.modifier = val.getModifier();
		
		for (SkillSpecializationValue<SR6Skill> spec : val.getSpecializations()) {
			if (spec.getDistributed()==2)
				fvtt.specialization = spec.getResolved().getId();
			if (spec.getDistributed()==3)
				fvtt.expertise = spec.getResolved().getId();
		}
	}

	//-------------------------------------------------------------------
	private static void fillSkills(LifeformActor actor, Lifeform<ShadowrunAttribute,SR6Skill,SR6SkillValue> life) {
		fillSkillValue( actor.skills.astral      , life.getSkillValue(Shadowrun6Core.getSkill("astral")));
		fillSkillValue( actor.skills.athletics   , life.getSkillValue(Shadowrun6Core.getSkill("athletics")));
		fillSkillValue( actor.skills.biotech     , life.getSkillValue(Shadowrun6Core.getSkill("biotech")));
		fillSkillValue( actor.skills.close_combat, life.getSkillValue(Shadowrun6Core.getSkill("close_combat")));
		fillSkillValue( actor.skills.con         , life.getSkillValue(Shadowrun6Core.getSkill("con")));
		fillSkillValue( actor.skills.conjuring   , life.getSkillValue(Shadowrun6Core.getSkill("conjuring")));
		fillSkillValue( actor.skills.cracking    , life.getSkillValue(Shadowrun6Core.getSkill("cracking")));
		fillSkillValue( actor.skills.electronics    , life.getSkillValue(Shadowrun6Core.getSkill("electronics")));
		fillSkillValue( actor.skills.enchanting  , life.getSkillValue(Shadowrun6Core.getSkill("enchanting")));
		fillSkillValue( actor.skills.engineering , life.getSkillValue(Shadowrun6Core.getSkill("engineering")));
		fillSkillValue( actor.skills.exotic_weapons, life.getSkillValue(Shadowrun6Core.getSkill("exotic_weapons")));
		fillSkillValue( actor.skills.firearms    , life.getSkillValue(Shadowrun6Core.getSkill("firearms")));
		fillSkillValue( actor.skills.influence   , life.getSkillValue(Shadowrun6Core.getSkill("influence")));
		fillSkillValue( actor.skills.outdoors    , life.getSkillValue(Shadowrun6Core.getSkill("outdoors")));
		fillSkillValue( actor.skills.perception  , life.getSkillValue(Shadowrun6Core.getSkill("perception")));
		fillSkillValue( actor.skills.piloting    , life.getSkillValue(Shadowrun6Core.getSkill("piloting")));
		fillSkillValue( actor.skills.sorcery     , life.getSkillValue(Shadowrun6Core.getSkill("sorcery")));
		fillSkillValue( actor.skills.stealth     , life.getSkillValue(Shadowrun6Core.getSkill("stealth")));
		fillSkillValue( actor.skills.tasking     , life.getSkillValue(Shadowrun6Core.getSkill("tasking")));
	}

	//-------------------------------------------------------------------
	private static void fillVehicleAttributes(FVTTVehicleActor actor, ItemTemplate item) {
		actor.handlOn  = ((OnRoadOffRoadValue) item.getAttribute(SR6ItemAttribute.HANDLING).getValue()).getOnRoad();		
		actor.handlOff = ((OnRoadOffRoadValue) item.getAttribute(SR6ItemAttribute.HANDLING).getValue()).getOffRoad();		
		actor.accOn    = ((OnRoadOffRoadValue) item.getAttribute(SR6ItemAttribute.ACCELERATION).getValue()).getOnRoad();		
		actor.accOff   = ((OnRoadOffRoadValue) item.getAttribute(SR6ItemAttribute.ACCELERATION).getValue()).getOffRoad();		
		actor.spdiOn   = ((OnRoadOffRoadValue) item.getAttribute(SR6ItemAttribute.SPEED_INTERVAL).getValue()).getOnRoad();		
		actor.spdiOff  = ((OnRoadOffRoadValue) item.getAttribute(SR6ItemAttribute.SPEED_INTERVAL).getValue()).getOffRoad();		
		actor.tspd     = item.getAttribute(SR6ItemAttribute.TOPSPEED).getDistributed();
		actor.bod      = item.getAttribute(SR6ItemAttribute.BODY).getDistributed();
		actor.arm      = item.getAttribute(SR6ItemAttribute.ARMOR).getDistributed();
		actor.pil      = item.getAttribute(SR6ItemAttribute.PILOT).getDistributed();
		actor.sen      = item.getAttribute(SR6ItemAttribute.SENSORS).getDistributed();
		if (item.getAttribute(SR6ItemAttribute.SEATS)!=null)
			actor.sea      = item.getAttribute(SR6ItemAttribute.SEATS).getDistributed();
		//actor.vtype    = item.getAttribute(SR6ItemAttribute.VEHICLE_TYPE).getValue();
	}

	//-------------------------------------------------------------------
	private static void fillAccessories(ActorData actor, ItemTemplate item, Locale loc) {
		for (Modification mod : item.getModifications()) {
			System.err.println("fillAccessories("+item.getId()+"): "+mod);
			if (mod instanceof EmbedModification) {
				EmbedModification embed = (EmbedModification)mod;
				logger.log(Level.INFO, "Embed "+embed.getKey()+" into "+item.getId());
				actor.addItem( convertGear(item, loc));
			}
		}
	}

	//-------------------------------------------------------------------
	public static ActorData<? extends GeneralActor> convertActor(SR6NPC item, Locale loc) {
		switch (item.getType()) {
		case CRITTER:
		case CRITTER_AWAKENED:
			return convertCritterActor(item, loc);
		case GRUNT:
			return convertNPCActor(item, loc);
		}
		return null;
	}

	//-------------------------------------------------------------------
	private static ActorData<FVTTCritter> convertCritterActor(SR6NPC data, Locale loc) {
		FVTTCritter actor = new FVTTCritter();

		ActorData<FVTTCritter> foundry = new ActorData<FVTTCritter>(data.getName(loc), "Critter", actor);
		fillAttributes(foundry.data, data);
		fillSkills(foundry.data, data);
		
		data.getQualities().forEach(tmp -> foundry.addItem(convertQuality(tmp,loc)));
		data.getCritterPowers().forEach(tmp -> foundry.addItem(convert(tmp,loc)));
		data.getGear().forEach(tmp -> {
			SR6GearTool.recalculate("", data, tmp);
			foundry.addItem(convertGear(tmp,loc));});

		return foundry;
	}

	//-------------------------------------------------------------------
	private static ActorData<FVTTNPCActor> convertNPCActor(SR6NPC data, Locale loc) {
		FVTTNPCActor actor = new FVTTNPCActor();
		actor.genesisID = data.getId();
		switch (data.getType()) {
		case GRUNT:
		case CONTACT:
			actor.type = Type.NPC; break;
		case CRITTER: 
		case CRITTER_AWAKENED: 
			actor.type = Type.CRITTER; break;
		case SPIRIT:  actor.type = Type.SPIRIT; break;
		case SPRITE:  actor.type = Type.SPRITE; break;
		}
		
		actor.movement.walk = 5;
		actor.movement.sprint = 10;
		actor.movement.perHit = 1;

		ActorData<FVTTNPCActor> foundry = new ActorData<FVTTNPCActor>(data.getName(loc), "NPC", actor);
		fillAttributes(foundry.data, data);
		fillSkills(foundry.data, data);
		
		data.getQualities().forEach(tmp -> foundry.addItem(convertQuality(tmp,loc)));
		data.getCritterPowers().forEach(tmp -> foundry.addItem(convert(tmp,loc)));
		data.getSpells().forEach(tmp -> foundry.addItem(convertSpell(tmp,loc)));
		data.getGear().forEach(tmp -> {
			SR6GearTool.recalculate("", data, tmp);
			ItemData<FVTTGear> converted = convertGear(tmp, loc);
			converted.getData().usedForPool=true;
			foundry.addItem(convertGear(tmp,loc));
		});
		
		
		return foundry;
	}

	//-------------------------------------------------------------------
	private static ActorData<FVTTVehicleActor> convertVehicleActor(ItemTemplate data, Locale loc) {
		FVTTVehicleActor actor = new FVTTVehicleActor();
		actor.genesisID = data.getId();
		
		ActorData<FVTTVehicleActor> foundry = new ActorData<FVTTVehicleActor>(data.getName(loc), "Vehicle", actor);

		fillVehicleAttributes(foundry.data, data);
		fillAccessories(foundry, data, loc);
//		fillSkills(foundry.data, data);
//		
//		data.getQualities().forEach(tmp -> foundry.addItem(convertQuality(tmp,loc)));
//		data.getCritterPowers().forEach(tmp -> foundry.addItem(convert(tmp,loc)));

		return foundry;
	}

}
