package de.rpgframework.shadowrun6.export.fvtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.foundry.ActorData;
import de.rpgframework.foundry.ItemData;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun.Lifestyle;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.RitualFeatureReference;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.TechniqueValue;
import de.rpgframework.shadowrun6.foundry.ActionSkills.ActionSkillValue;
import de.rpgframework.shadowrun6.foundry.FVTTAdeptPower;
import de.rpgframework.shadowrun6.foundry.FVTTArmor;
import de.rpgframework.shadowrun6.foundry.FVTTBodyware;
import de.rpgframework.shadowrun6.foundry.FVTTComplexForm;
import de.rpgframework.shadowrun6.foundry.FVTTContact;
import de.rpgframework.shadowrun6.foundry.FVTTEcho;
import de.rpgframework.shadowrun6.foundry.FVTTFocus;
import de.rpgframework.shadowrun6.foundry.FVTTGear;
import de.rpgframework.shadowrun6.foundry.FVTTLifestyle;
import de.rpgframework.shadowrun6.foundry.FVTTMAStyle;
import de.rpgframework.shadowrun6.foundry.FVTTMATechnique;
import de.rpgframework.shadowrun6.foundry.FVTTMetamagic;
import de.rpgframework.shadowrun6.foundry.FVTTQuality;
import de.rpgframework.shadowrun6.foundry.FVTTRitual;
import de.rpgframework.shadowrun6.foundry.FVTTSIN;
import de.rpgframework.shadowrun6.foundry.FVTTSkill;
import de.rpgframework.shadowrun6.foundry.FVTTSpell;
import de.rpgframework.shadowrun6.foundry.FVTTVehicle;
import de.rpgframework.shadowrun6.foundry.FVTTWeapon;
import de.rpgframework.shadowrun6.foundry.Shadowrun6FoundryCharacter;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.VehicleData.VehicleType;
import de.rpgframework.shadowrun6.persist.WeaponDamageConverter;

public class FoundryExportService {

	public final static String VERSION = "0.8.9";

	//-------------------------------------------------------------------
	public String exportCharacter(Shadowrun6Character character) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		ActorData<Shadowrun6FoundryCharacter> actor = new ActorData<Shadowrun6FoundryCharacter>(character.getName(), "Player", getJSONCharacter(character));
//		actor.exportVersion = VERSION;
		addFoundryItems(actor, character);
		return gson.toJson(actor);
	}

	//-------------------------------------------------------------------
	private Shadowrun6FoundryCharacter getJSONCharacter(Shadowrun6Character character) {
		Shadowrun6FoundryCharacter jsonCharacter = new Shadowrun6FoundryCharacter();
		jsonCharacter.nuyen = character.getNuyen();
		jsonCharacter.mortype  = character.getMagicOrResonanceType().getId();
		setAttributes(jsonCharacter, character);
		setDerivedAttributes(jsonCharacter, character);
		setSkills(jsonCharacter, character);
		jsonCharacter.metatype = character.getMetatype().getName();
		jsonCharacter.gender   = character.getGender().toString();
		return jsonCharacter;
	}


	//-------------------------------------------------------------------
	private void setAttributes(Shadowrun6FoundryCharacter json, Shadowrun6Character model) {
		for (ShadowrunAttribute attribute : ShadowrunAttribute.values()) {
			switch (attribute) {
			case CHARISMA :
				json.attributes.cha.base = model.getAttribute(attribute).getDistributed();
				json.attributes.cha.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.cha.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.cha.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case AGILITY  :
				json.attributes.agi.base = model.getAttribute(attribute).getDistributed();
				json.attributes.agi.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.agi.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.agi.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case INTUITION:
				json.attributes.inn.base = model.getAttribute(attribute).getDistributed();
				json.attributes.inn.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.inn.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.inn.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case BODY:
				json.attributes.bod.base = model.getAttribute(attribute).getDistributed();
				json.attributes.bod.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.bod.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.bod.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case REACTION   :
				json.attributes.rea.base = model.getAttribute(attribute).getDistributed();
				json.attributes.rea.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.rea.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.rea.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case STRENGTH :
				json.attributes.str.base = model.getAttribute(attribute).getDistributed();
				json.attributes.str.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.str.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.str.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case LOGIC     :
				json.attributes.log.base = model.getAttribute(attribute).getDistributed();
				json.attributes.log.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.log.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.log.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case WILLPOWER:
				json.attributes.wil.base = model.getAttribute(attribute).getDistributed();
				json.attributes.wil.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.wil.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.wil.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case MAGIC:
				json.attributes.mag.base = model.getAttribute(attribute).getDistributed();
				json.attributes.mag.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.mag.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.mag.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case RESONANCE:
				json.attributes.res.base = model.getAttribute(attribute).getDistributed();
				json.attributes.res.mod   = model.getAttribute(attribute).getModifier();
				json.attributes.res.pool = model.getAttribute(attribute).getModifiedValue();
				json.attributes.res.modString = model.getAttribute(attribute).getPool().toExplainString();
				break;
			case EDGE:
				json.edge.max = model.getAttribute(attribute).getDistributed();
				json.edge.value = model.getAttribute(attribute).getDistributed();
				break;
			default:
			}
		}
	}

	//-------------------------------------------------------------------
	private void setDerivedAttributes(Shadowrun6FoundryCharacter json, Shadowrun6Character model) {
		json.physical.base = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getDistributed();
		json.physical.mod = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getModifier();
		json.physical.modString = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getPool().toExplainString();
		json.physical.value = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getModifiedValue();

		json.stun.base = model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getDistributed();
		json.stun.mod = model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getModifier();
		json.stun.modString = model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getPool().toExplainString();
		json.stun.value = model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getModifiedValue();

		json.initiative.physical.dice = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL).getModifiedValue();
		json.initiative.physical.mod = 0; //model.getAttribute(ShadowrunAttribute.INITIATIVE_PHYSICAL).getModifier();
		json.initiative.astral.dice = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL).getModifiedValue();
		json.initiative.astral.mod = 0; //model.getAttribute(ShadowrunAttribute.INITIATIVE_ASTRAL).getModifier();

		json.derived.attack_rating.base = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getDistributed();
		json.derived.attack_rating.mod  = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getModifier();
		json.derived.attack_rating.pool = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getModifiedValue();

		json.derived.defense_rating.base = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getDistributed();
		json.derived.defense_rating.mod  = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getModifier();
		json.derived.defense_rating.pool = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getModifiedValue();

		json.derived.composure.base = model.getAttribute(ShadowrunAttribute.COMPOSURE).getModifiedValue();

		json.derived.judge_intentions.base = model.getAttribute(ShadowrunAttribute.JUDGE_INTENTIONS).getModifiedValue();

		json.derived.memory.base = model.getAttribute(ShadowrunAttribute.MEMORY).getModifiedValue();

		json.derived.lift_carry.base = model.getAttribute(ShadowrunAttribute.LIFT_CARRY).getModifiedValue();

		json.resist.attacks.base = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE).getModifier();
		json.resist.attacks.pool = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE).getModifiedValue();

		json.resist.toxin.base = model.getAttribute(ShadowrunAttribute.RESIST_TOXIN).getModifier();
		json.resist.toxin.pool = model.getAttribute(ShadowrunAttribute.RESIST_TOXIN).getModifiedValue();
	}

	//-------------------------------------------------------------------
	private void setSpecialization(ActionSkillValue target, SR6SkillValue val) {
		List<String> spec = new ArrayList<>();
		List<String> exp  = new ArrayList<>();
		for (SkillSpecializationValue<SR6Skill> tmp : val.getSpecializations()) {
			if (tmp.getDistributed()==3)
				exp.add(tmp.getResolved().getId());
			else
				spec.add(tmp.getResolved().getId());
		}
		if (!spec.isEmpty()) target.specialization = String.join(",", spec);
		if (!exp.isEmpty()) target.expertise = String.join(",", exp);
	}

	//-------------------------------------------------------------------
	private void setSkills(Shadowrun6FoundryCharacter json, Shadowrun6Character model) {
		for (SR6SkillValue val : model.getSkillValues()) {
			switch (val.getModifyable().getId()) {
			case "astral" :
				json.skills.astral.points = val.getDistributed();
				json.skills.astral.modifier = val.getModifier();
				json.skills.astral.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.astral, val);
				break;
			case "athletics" :
				json.skills.athletics.points = val.getDistributed();
				json.skills.athletics.modifier = val.getModifier();
				json.skills.athletics.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.athletics, val);
				break;
			case "biotech" :
				json.skills.biotech.points = val.getDistributed();
				json.skills.biotech.modifier = val.getModifier();
				json.skills.biotech.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.biotech, val);
				break;
			case "close_combat" :
				json.skills.close_combat.points = val.getDistributed();
				json.skills.close_combat.modifier = val.getModifier();
				json.skills.close_combat.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.close_combat, val);
				break;
			case "con" :
				json.skills.con.points = val.getDistributed();
				json.skills.con.modifier = val.getModifier();
				json.skills.con.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.con, val);
				break;
			case "conjuring" :
				json.skills.conjuring.points = val.getDistributed();
				json.skills.conjuring.modifier = val.getModifier();
				json.skills.conjuring.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.conjuring, val);
				break;
			case "cracking" :
				json.skills.cracking.points = val.getDistributed();
				json.skills.cracking.modifier = val.getModifier();
				json.skills.cracking.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.cracking, val);
				break;
			case "electronics" :
				json.skills.electronics.points = val.getDistributed();
				json.skills.electronics.modifier = val.getModifier();
				json.skills.electronics.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.electronics, val);
				break;
			case "enchanting" :
				json.skills.enchanting.points = val.getDistributed();
				json.skills.enchanting.modifier = val.getModifier();
				json.skills.enchanting.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.enchanting, val);
				break;
			case "engineering" :
				json.skills.engineering.points = val.getDistributed();
				json.skills.engineering.modifier = val.getModifier();
				json.skills.engineering.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.engineering, val);
				break;
			case "exotic_weapons" :
				json.skills.exotic_weapons.points = val.getDistributed();
				json.skills.exotic_weapons.modifier = val.getModifier();
				json.skills.exotic_weapons.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.exotic_weapons, val);
				break;
			case "firearms" :
				json.skills.firearms.points = val.getDistributed();
				json.skills.firearms.modifier = val.getModifier();
				json.skills.firearms.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.firearms, val);
				break;
			case "influence" :
				json.skills.influence.points = val.getDistributed();
				json.skills.influence.modifier = val.getModifier();
				json.skills.influence.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.influence, val);
				break;
			case "outdoors" :
				json.skills.outdoors.points = val.getDistributed();
				json.skills.outdoors.modifier = val.getModifier();
				json.skills.outdoors.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.outdoors, val);
				break;
			case "perception" :
				json.skills.perception.points = val.getDistributed();
				json.skills.perception.modifier = val.getModifier();
				json.skills.perception.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.perception, val);
				break;
			case "piloting" :
				json.skills.piloting.points = val.getDistributed();
				json.skills.piloting.modifier = val.getModifier();
				json.skills.piloting.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.piloting, val);
				break;
			case "sorcery" :
				json.skills.sorcery.points = val.getDistributed();
				json.skills.sorcery.modifier = val.getModifier();
				json.skills.sorcery.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.sorcery, val);
				break;
			case "stealth" :
				json.skills.stealth.points = val.getDistributed();
				json.skills.stealth.modifier = val.getModifier();
				json.skills.stealth.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.stealth, val);
				break;
			case "tasking" :
				json.skills.tasking.points = val.getDistributed();
				json.skills.tasking.modifier = val.getModifier();
				json.skills.tasking.modString = val.getPool().toExplainString();
				setSpecialization(json.skills.tasking, val);
				break;
			default:
			}
		}
	}

	//-------------------------------------------------------------------
	private void addFoundryItems(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		addSkills(actor, character);
		addQualities(actor, character);
		addGear(actor, character);
		addMartialArts(actor, character);
		addSpells(actor, character);
		addAdeptPowers(actor, character);
		addRituals(actor, character);
		addMetamagics(actor, character);
		addFoci(actor, character);
		addComplexForms(actor, character);
		addEchoes(actor, character);
		addSINs(actor, character);
		addLifestyles(actor, character);
		addContacts(actor, character);
	}

	//-------------------------------------------------------------------
	private void addSkills(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character model) {
		for (SR6SkillValue val : model.getSkillValues()) {
			if (!(val.getModifyable().getId().equals("language") || val.getModifyable().getId().equals("knowledge"))) {
				continue;
			} else {
				FVTTSkill fVal = new FVTTSkill();
				fVal.genesisID = val.getModifyable().getId();
				fVal.points    = val.getDistributed();
				fVal.modifier  = val.getModifier();

				ItemData<FVTTSkill> item = new ItemData<FVTTSkill>(val.getName(), "skill", fVal);
				actor.addItem(item);
			}
		}
	}

	//-------------------------------------------------------------------
	private void addQualities(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character model) {
		for (QualityValue val : model.getQualities()) {
			FVTTQuality fVal = new FVTTQuality();
			// Definition fields
			fVal.genesisID = val.getModifyable().getId();
			fVal.category  = val.getModifyable().getType().name();
			fVal.level     = val.getModifyable().getMax()>0;
			// Value fields
			fVal.value = val.getDistributed();
			fVal.explain = val.getDescription();

			ItemData<FVTTQuality> item = new ItemData<FVTTQuality>(val.getName(), "quality", fVal);
			actor.addItem(item);
		}
	}

	//-------------------------------------------------------------------
	private static FVTTWeapon.FireMode getFireModes(List<FireMode> value) {
		FVTTWeapon.FireMode modes = new FVTTWeapon.FireMode();
		if (value!=null) {
			for (FireMode mode : value) {
				switch (mode) {
				case BURST_FIRE: modes.BF=true; break;
				case FULL_AUTO : modes.FA=true; break;
				case SEMI_AUTOMATIC: modes.SA=true; break;
				case SINGLE_SHOT: modes.SS=true; break;
				}
			}
		}
		return modes;
	}

	//-------------------------------------------------------------------
	private void addGear(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		WeaponDamageConverter dmgConv = new WeaponDamageConverter();
		for (CarriedItem<ItemTemplate> item : character.getCarriedItems()) {
			if (ItemTemplate.UUID_UNARMED.equals( item.getUuid())) continue;

			ItemType type = item.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
			ItemSubType subtype = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue();
			FVTTGear gear = new FVTTGear();
			switch (type) {
			case WEAPON_CLOSE_COMBAT:
			case WEAPON_FIREARMS:
			case WEAPON_RANGED:
			case WEAPON_SPECIAL:
				gear = new FVTTWeapon();
				((FVTTWeapon)gear).dmg = ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getValue();
				((FVTTWeapon)gear).dmgDef = dmgConv.write((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue());
				break;
			case ARMOR:
				gear = new FVTTArmor();
				break;
			case BIOWARE:
			case CYBERWARE:
			case NANOWARE:
				gear = new FVTTBodyware();
				break;
			case VEHICLES:
			case DRONE_LARGE: case DRONE_MEDIUM: case DRONE_MICRO: case DRONE_MINI: case DRONE_SMALL:
				gear = new FVTTVehicle();
				break;
			}

			gear.genesisID = item.getResolved().getId();
			gear.type = type.name();
			gear.subtype = subtype.name();
			if (item.hasAttribute(SR6ItemAttribute.AVAILABILITY)) {
				gear.avail = ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getValue()).getValue();
				gear.availDef = ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getValue()).toString();
			}
			System.out.println("Item "+item.getNameWithoutRating());
			if (item.hasAttribute(SR6ItemAttribute.PRICE))
				gear.price = item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue();
			gear.notes = item.getNotes();
			gear.customName = item.getCustomName();
			gear.countable  = item.getResolved().isCountable();
			if (item.getResolved().isCountable())
				gear.count = item.getCount();
			gear.needsRating = item.getResolved().getChoice(ItemTemplate.UUID_RATING)!=null;
			if (gear.needsRating)
				gear.rating = item.getDecision(ItemTemplate.UUID_RATING).getValueAsInt();

			// Accessories
			List<String> accList = new ArrayList<>();
			item.getEffectiveAccessories().forEach( ci -> accList.add(ci.getNameWithRating()));
			if (!accList.isEmpty()) {
				gear.accessories = String.join(", ", accList);
			}

			if (ItemType.isWeapon(type)) {
				if (item.hasAttribute(SR6ItemAttribute.SKILL)) {
					SR6Skill skill = item.getAsObject(SR6ItemAttribute.SKILL).getModifiedValue();
					gear.skill = skill.getId();
				}
				if (item.hasAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION)) {
					SkillSpecialization<SR6Skill> spec = item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION).getModifiedValue();
					gear.skillSpec = spec.getId();
				}
				if (item.hasAttribute(SR6ItemAttribute.DAMAGE)) {
					Damage dmg = item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue();
					((FVTTWeapon)gear).dmg    = dmg.getValue();
					((FVTTWeapon)gear).dmgDef = dmg.toString();
					((FVTTWeapon)gear).stun   = dmg.getType()==DamageType.STUN;
				}
				if (item.hasAttribute(SR6ItemAttribute.ATTACK_RATING)) {
					((FVTTWeapon)gear).attackRating = (int[])item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
				}
				if (item.hasAttribute(SR6ItemAttribute.FIREMODES)) {
					((FVTTWeapon)gear).modes = getFireModes((List<FireMode>)item.getAsObject(SR6ItemAttribute.FIREMODES).getModifiedValue());
				}
			}
			if (item.hasAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL) && gear instanceof FVTTArmor)
				((FVTTArmor)gear).defense = item.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue();
			if (item.hasAttribute(SR6ItemAttribute.DEFENSE_SOCIAL) && gear instanceof FVTTArmor)
				((FVTTArmor)gear).social = item.getAsValue(SR6ItemAttribute.DEFENSE_SOCIAL).getModifiedValue();

			if (gear instanceof FVTTBodyware) {
				if (item.hasAttribute(SR6ItemAttribute.ESSENCECOST))
					((FVTTBodyware)gear).essence = item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.CAPACITY))
					((FVTTBodyware)gear).capacity = (int)item.getAsValue(SR6ItemAttribute.CAPACITY).getModifiedValue();
			}
			if (gear instanceof FVTTVehicle) {
				if (item.hasAttribute(SR6ItemAttribute.ACCELERATION)) {
					((FVTTVehicle)gear).accOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.ACCELERATION).getModifiedValue()).getOffRoad();
					((FVTTVehicle)gear).accOn  = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.ACCELERATION).getModifiedValue()).getOnRoad();
				}
				if (item.hasAttribute(SR6ItemAttribute.HANDLING)) {
					((FVTTVehicle)gear).handlOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.HANDLING).getModifiedValue()).getOffRoad();
					((FVTTVehicle)gear).handlOn  = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.HANDLING).getModifiedValue()).getOnRoad();
				}
				if (item.hasAttribute(SR6ItemAttribute.SPEED_INTERVAL))
					((FVTTVehicle)gear).spdiOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.SPEED_INTERVAL).getModifiedValue()).getOffRoad();
					((FVTTVehicle)gear).spdiOn  = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.SPEED_INTERVAL).getModifiedValue()).getOnRoad();
				if (item.hasAttribute(SR6ItemAttribute.TOPSPEED))
					((FVTTVehicle)gear).tspd = item.getAsValue(SR6ItemAttribute.TOPSPEED).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.BODY))
					((FVTTVehicle)gear).bod = item.getAsValue(SR6ItemAttribute.BODY).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.ARMOR))
					((FVTTVehicle)gear).arm = item.getAsValue(SR6ItemAttribute.ARMOR).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.PILOT))
					((FVTTVehicle)gear).pil = item.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.SENSORS))
					((FVTTVehicle)gear).sen = item.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.SEATS))
					((FVTTVehicle)gear).sea = item.getAsValue(SR6ItemAttribute.SEATS).getModifiedValue();
				if (item.hasAttribute(SR6ItemAttribute.VEHICLE_TYPE))
					((FVTTVehicle)gear).vtype = ((VehicleType)item.getAsObject(SR6ItemAttribute.VEHICLE_TYPE).getModifiedValue()).name();
			}

			ItemData<FVTTGear> foundry = new ItemData<FVTTGear>(item.getNameWithoutRating(), "gear", gear);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addMartialArts(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character model) {
		for (MartialArtsValue val : model.getMartialArts()) {
			FVTTMAStyle fVal = new FVTTMAStyle();
			// Definition fields
			fVal.genesisID = val.getResolved().getId();
			List<String> names = val.getResolved().getCategories().stream().map( (c) -> c.name().toLowerCase()).collect(Collectors.toList());
			fVal.category  = String.join(", ", names);

			ItemData<FVTTMAStyle> item = new ItemData<FVTTMAStyle>(val.getResolved().getName(), "martialartstyle", fVal);
			actor.addItem(item);

			for (TechniqueValue tech : model.getTechniques(val.getResolved())) {
				FVTTMATechnique toAdd = new FVTTMATechnique();
				toAdd.genesisID = tech.getResolved().getId();
				if (tech.getMartialArt()!=null)
					toAdd.style = tech.getMartialArt().getId();
				ItemData<FVTTMATechnique> item2 = new ItemData<FVTTMATechnique>(tech.getResolved().getName(), "martialarttech", toAdd);
				actor.addItem(item2);
			}
		}
	}

	//-------------------------------------------------------------------
	private void addSpells(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (SpellValue<SR6Spell> item : character.getSpells()) {
			FVTTSpell spell = new FVTTSpell();

			spell.genesisID = item.getModifyable().getId();
			spell.data.category  = item.getModifyable().getCategory().name().toLowerCase();
			spell.data.duration  = item.getModifyable().getDuration().name().toLowerCase();
			spell.data.drain     = item.getModifyable().getDrain();
			spell.data.range     = item.getModifyable().getRange().name().toLowerCase();
			spell.data.type      = item.getModifyable().getType().name().toLowerCase();
			if (item.getModifyable().getDamage()!=null)
				spell.data.damage    = item.getModifyable().getDamage().name().toLowerCase();
			spell.data.isOpposed = item.getModifyable().isOpposed();
			spell.data.withEssence = item.getModifyable().isEssence();
			for (SpellFeatureReference ref : item.getModifyable().getFeatures()) {
				switch (ref.getFeature().getId()) {
				case "sense_multi": spell.data.multiSense=true; break;
				}
			}

			ItemData<FVTTSpell> foundry = new ItemData<FVTTSpell>(item.getNameWithoutRating(), "spell", spell);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addAdeptPowers(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (AdeptPowerValue item : character.getAdeptPowers()) {
			FVTTAdeptPower spell = new FVTTAdeptPower();

			spell.genesisID = item.getModifyable().getId();
			spell.cost      = (float)item.getCost();
			spell.activation= item.getModifyable().getActivation().name().toLowerCase();

			ItemData<FVTTAdeptPower> foundry = new ItemData<FVTTAdeptPower>(item.getNameWithoutRating(), "adeptpower", spell);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addRituals(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (RitualValue item : character.getRituals()) {
			FVTTRitual spell = new FVTTRitual();

			spell.genesisID = item.getModifyable().getId();
			spell.threshold = item.getModifyable().getThreshold();
			for (RitualFeatureReference ref : item.getModifyable().getFeatures()) {
				switch (ref.getResolved().getId()) {
				case "material_link": spell.features.material_link=true; break;
				case "anchored": spell.features.anchored=true; break;
				case "minion": spell.features.minion=true; break;
				case "spell": spell.features.spell=true; break;
				case "spotter": spell.features.spotter=true; break;
				}
			}

			ItemData<FVTTRitual> foundry = new ItemData<FVTTRitual>(item.getModifyable().getName(), "ritual", spell);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addMetamagics(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character model) {
		for (MetamagicOrEchoValue val : model.getMetamagicOrEchoes()) {
			if (val.getModifyable().getType()==MetamagicOrEcho.Type.ECHO)
				continue;

			FVTTMetamagic fVal = new FVTTMetamagic();
			// Definition fields
			fVal.genesisID = val.getModifyable().getId();
			fVal.level     = val.getModifyable().hasLevel();
			fVal.adepts    = (val.getModifyable().getType()==MetamagicOrEcho.Type.METAMAGIC_ADEPT);
			fVal.mages     = true;

			ItemData<FVTTMetamagic> item = new ItemData<FVTTMetamagic>(val.getNameWithoutRating(), "metamagic", fVal);
			actor.addItem(item);
		}
	}

	//-------------------------------------------------------------------
	private void addFoci(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (FocusValue item : character.getFoci()) {
			FVTTFocus focus = new FVTTFocus();

			focus.genesisID = item.getModifyable().getId();
			focus.rating    = item.getLevel();
			focus.choice    = item.getDecisionString(Locale.getDefault(), character);

			ItemData<FVTTFocus> foundry = new ItemData<FVTTFocus>(item.getModifyable().getName(), "focus", focus);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addComplexForms(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (ComplexFormValue item : character.getComplexForms()) {
			FVTTComplexForm cplx = new FVTTComplexForm();

			cplx.genesisID = item.getModifyable().getId();
			cplx.duration  = item.getModifyable().getDuration().name().toLowerCase();
			cplx.fading    = item.getModifyable().getFading();
//			spell.isOpposed = item.getModifyable().isOpposed();
			if (!item.getDecisions().isEmpty()) {
				cplx.choice = item.getDecisionString(Locale.getDefault());
			}

			ItemData<FVTTComplexForm> foundry = new ItemData<FVTTComplexForm>(item.getNameWithoutRating(), "complexform", cplx);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addEchoes(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character model) {
		for (MetamagicOrEchoValue val : model.getMetamagicOrEchoes()) {
			if (val.getModifyable().getType()==MetamagicOrEcho.Type.METAMAGIC)
				continue;

			FVTTEcho fVal = new FVTTEcho();
			// Definition fields
			fVal.genesisID = val.getModifyable().getId();
			fVal.level     = val.getModifyable().hasLevel();
			if (!val.getDecisions().isEmpty()) {
				fVal.choice = val.getDecisionString(Locale.getDefault());
			}

			ItemData<FVTTEcho> item = new ItemData<FVTTEcho>(val.getNameWithoutRating(), "echo", fVal);
			actor.addItem(item);
		}
	}

	//-------------------------------------------------------------------
	private void addSINs(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (SIN item : character.getSINs()) {
			FVTTSIN data = new FVTTSIN();

			data.quality = item.getQuality().name();
			data.description = item.getDescription();

			ItemData<FVTTSIN> foundry = new ItemData<FVTTSIN>(item.getName(), "sin", data);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addLifestyles(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (Lifestyle item : character.getLifestyles()) {
			FVTTLifestyle data = new FVTTLifestyle();

			data.genesisID = item.getResolved().getId();
			data.type      = item.getResolved().getId();
			data.paid      = item.getDistributed();
			data.cost      = item.getCost();
			data.description = item.getDescription();
			if (item.getSIN()!=null)
			data.sin       = item.getSIN().toString();

			ItemData<FVTTLifestyle> foundry = new ItemData<FVTTLifestyle>(item.getNameWithoutRating(), "lifestyle", data);
			actor.addItem(foundry);
		}
	}

	//-------------------------------------------------------------------
	private void addContacts(ActorData<Shadowrun6FoundryCharacter> actor, Shadowrun6Character character) {
		for (Contact item : character.getContacts()) {
			FVTTContact data = new FVTTContact();

			data.rating      = item.getRating();
			data.loyalty     = item.getLoyalty();
			data.type        = item.getType().getName();
			data.description = item.getDescription();

			ItemData<FVTTContact> foundry = new ItemData<FVTTContact>(item.getName(), "contact", data);
			if (foundry.name==null)
				foundry.name = "Unnamed";
			actor.addItem(foundry);
		}
	}

}
