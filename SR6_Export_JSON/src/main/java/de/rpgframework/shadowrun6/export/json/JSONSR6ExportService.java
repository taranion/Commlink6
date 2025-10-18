package de.rpgframework.shadowrun6.export.json;

import static de.rpgframework.shadowrun.ShadowrunAttribute.AGILITY;
import static de.rpgframework.shadowrun.ShadowrunAttribute.BODY;
import static de.rpgframework.shadowrun.ShadowrunAttribute.CHARISMA;
import static de.rpgframework.shadowrun.ShadowrunAttribute.COMPOSURE;
import static de.rpgframework.shadowrun.ShadowrunAttribute.DEFENSE_POOL_PHYSICAL;
import static de.rpgframework.shadowrun.ShadowrunAttribute.EDGE;
import static de.rpgframework.shadowrun.ShadowrunAttribute.INITIATIVE_ASTRAL;
import static de.rpgframework.shadowrun.ShadowrunAttribute.INITIATIVE_MATRIX;
import static de.rpgframework.shadowrun.ShadowrunAttribute.INITIATIVE_PHYSICAL;
import static de.rpgframework.shadowrun.ShadowrunAttribute.INTUITION;
import static de.rpgframework.shadowrun.ShadowrunAttribute.JUDGE_INTENTIONS;
import static de.rpgframework.shadowrun.ShadowrunAttribute.LIFT_CARRY;
import static de.rpgframework.shadowrun.ShadowrunAttribute.LOGIC;
import static de.rpgframework.shadowrun.ShadowrunAttribute.MAGIC;
import static de.rpgframework.shadowrun.ShadowrunAttribute.MEMORY;
import static de.rpgframework.shadowrun.ShadowrunAttribute.POWER_POINTS;
import static de.rpgframework.shadowrun.ShadowrunAttribute.REACTION;
import static de.rpgframework.shadowrun.ShadowrunAttribute.RESONANCE;
import static de.rpgframework.shadowrun.ShadowrunAttribute.STRENGTH;
import static de.rpgframework.shadowrun.ShadowrunAttribute.WILLPOWER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.LicenseValue;
import de.rpgframework.shadowrun.Lifestyle;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.SignatureManeuver;
import de.rpgframework.shadowrun6.export.json.model.JSONAdeptPower;
import de.rpgframework.shadowrun6.export.json.model.JSONArmor;
import de.rpgframework.shadowrun6.export.json.model.JSONAttribute;
import de.rpgframework.shadowrun6.export.json.model.JSONAugmentation;
import de.rpgframework.shadowrun6.export.json.model.JSONCharacter;
import de.rpgframework.shadowrun6.export.json.model.JSONCloseCombatWeapon;
import de.rpgframework.shadowrun6.export.json.model.JSONComplexForm;
import de.rpgframework.shadowrun6.export.json.model.JSONContact;
import de.rpgframework.shadowrun6.export.json.model.JSONDrone;
import de.rpgframework.shadowrun6.export.json.model.JSONInitiative;
import de.rpgframework.shadowrun6.export.json.model.JSONItem;
import de.rpgframework.shadowrun6.export.json.model.JSONItemAccessory;
import de.rpgframework.shadowrun6.export.json.model.JSONLicense;
import de.rpgframework.shadowrun6.export.json.model.JSONLifestyle;
import de.rpgframework.shadowrun6.export.json.model.JSONLongRangeWeapon;
import de.rpgframework.shadowrun6.export.json.model.JSONMartialArts;
import de.rpgframework.shadowrun6.export.json.model.JSONMatrixItem;
import de.rpgframework.shadowrun6.export.json.model.JSONMetaMagicOrEcho;
import de.rpgframework.shadowrun6.export.json.model.JSONQuality;
import de.rpgframework.shadowrun6.export.json.model.JSONRitual;
import de.rpgframework.shadowrun6.export.json.model.JSONSignatureManeuver;
import de.rpgframework.shadowrun6.export.json.model.JSONSin;
import de.rpgframework.shadowrun6.export.json.model.JSONSkill;
import de.rpgframework.shadowrun6.export.json.model.JSONSkillSpecialization;
import de.rpgframework.shadowrun6.export.json.model.JSONSpell;
import de.rpgframework.shadowrun6.export.json.model.JSONVehicle;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;

public class JSONSR6ExportService {

	private final Locale loc = Locale.getDefault();

    public String exportCharacter(Shadowrun6Character character) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        return gson.toJson(getJSONCharacter(character));
    }

    private JSONCharacter getJSONCharacter(Shadowrun6Character character) {
        JSONCharacter jsonCharacter = new JSONCharacter();
        setGeneralInfo(jsonCharacter, character);
        setAttributes(jsonCharacter, character);
        setInitiatives(jsonCharacter, character);
        setSkills(jsonCharacter, character);
        setSpells(jsonCharacter, character);
        setComplexForms(jsonCharacter, character);
        setAdeptPowers(jsonCharacter, character);
        setConnections(jsonCharacter, character);
        setQualities(jsonCharacter, character);
        setLongRangeWeapons(jsonCharacter, character);
        setCloseCombatWeapons(jsonCharacter, character);
        setArmors(jsonCharacter, character);
        setItems(jsonCharacter, character);
        setAugmentations(jsonCharacter, character);
        setVehicles(jsonCharacter, character);
        setDrones(jsonCharacter, character);
        setRituals(jsonCharacter, character);
        setSINs(jsonCharacter, character);
        setLifestyles(jsonCharacter, character);
        setLicenses(jsonCharacter, character);
        setMatrixItems(jsonCharacter, character);
        setMartialArts(jsonCharacter, character);
        setSignatureMoves(jsonCharacter, character);
        return jsonCharacter;
    }

    private void setSignatureMoves(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONSignatureManeuver> jsonSignatureMoves = new ArrayList<>();
        for (SignatureManeuver signatureManeuver : character.getSignatureManeuvers()) {
            JSONSignatureManeuver jsonSignatureManeuver = new JSONSignatureManeuver();
            jsonSignatureManeuver.name = signatureManeuver.getName();
            if (signatureManeuver.getSkill()!=null) {
                jsonSignatureManeuver.skill = signatureManeuver.getSkill().getName();
            } else {
                jsonSignatureManeuver.skill = "No skill set";
            }
            jsonSignatureManeuver.action1Id = signatureManeuver.getAction1().getId();
            jsonSignatureManeuver.action1Name = signatureManeuver.getAction1().getName();
            jsonSignatureManeuver.action2Id = signatureManeuver.getAction2().getId();
            jsonSignatureManeuver.action2Name = signatureManeuver.getAction2().getName();
            jsonSignatureManeuver.cost = signatureManeuver.getAction1().getCost() + signatureManeuver.getAction2().getCost();
            jsonSignatureMoves.add(jsonSignatureManeuver);
        }
        jsonCharacter.signatureManeuvers = jsonSignatureMoves;
    }

    private void setMartialArts(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONMartialArts> jsonMartialArts = new ArrayList<>();
        for (MartialArtsValue martialArt : character.getMartialArts()) {
            JSONMartialArts ma = new JSONMartialArts();
            ma.name = martialArt.getModifyable().getName();
//            ma.page = getPageString(martialArt.getModifyable().getPage(), martialArt.getModifyable().getProductNameShort());
//            ma.techniques = character.getTechniques(martialArt.getModifyable()).stream().map(t -> t.getTechnique().getName()).collect(Collectors.toList());
            ma.description = getDescription(martialArt.getModifyable());
            jsonMartialArts.add(ma);
        }
        jsonCharacter.martialArts = jsonMartialArts;
    }

    private void setInitiatives(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONInitiative> jsonInitiativeList = new ArrayList<>();
        jsonInitiativeList.add(getJsonInitiative(character, INITIATIVE_ASTRAL, ShadowrunAttribute.INITIATIVE_DICE_ASTRAL));
        jsonInitiativeList.add(getJsonInitiative(character, INITIATIVE_PHYSICAL, ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL));
        jsonInitiativeList.add(getJsonInitiative(character, INITIATIVE_MATRIX, ShadowrunAttribute.INITIATIVE_DICE_MATRIX));
        jsonInitiativeList.add(getJsonInitiative(character, ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD, ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD));
        jsonInitiativeList.add(getJsonInitiative(character, ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT, ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_HOT));
        jsonCharacter.initiatives = jsonInitiativeList;
    }

    private JSONInitiative getJsonInitiative(Shadowrun6Character character, ShadowrunAttribute a, ShadowrunAttribute diceAttribute) {
        AttributeValue<ShadowrunAttribute> attribute = character.getAttribute(a);
        JSONInitiative i = new JSONInitiative();
        i.name = attribute.getModifyable().getName();
        i.id = a.name();
        i.value = attribute.getModifiedValue();
        i.dice = "+" +character.getAttribute(diceAttribute).getModifiedValue() + "D6";
        return i;
    }

    private void setLicenses(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONLicense> jsonLicenseList = new ArrayList<>();
        for (LicenseValue license : character.getLicenses()) {
            JSONLicense jsonLicense = new JSONLicense();
            jsonLicense.name = license.getName();
            UUID sinUUID = license.getSIN();
            SIN sin = getSIN(character, sinUUID);
            if (sin != null) {
                jsonLicense.sin =  sin.getName();
            }
            //TODO license type
//            jsonLicense.type = license.getType();
            jsonLicense.rating = license.getRating().name();
            jsonLicenseList.add(jsonLicense);
        }
        jsonCharacter.licenses = jsonLicenseList;
    }

    private SIN getSIN(Shadowrun6Character character, UUID sinUUID) {
        return character.getSINs().stream().filter(s -> s.getUniqueId()
                        .equals(sinUUID))
                .findFirst()
                .orElse(null);
    }

    private void setLifestyles(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONLifestyle> jsonLifestyleList = new ArrayList<>();
        for (Lifestyle lifestyleValue : character.getLifestyles()) {
            JSONLifestyle jsonLifestyle = new JSONLifestyle();
            jsonLifestyle.customName = lifestyleValue.getNameWithoutRating();
            jsonLifestyle.name = lifestyleValue.getResolved().getName();
//            jsonLifestyle.cost = Shadowrun6Tools.getLifestyleCost(character, lifestyleValue);
            jsonLifestyle.paidMonths = lifestyleValue.getDistributed();
//            jsonLifestyle.sin = lifestyleValue.getSIN() != null ? character.getSIN(lifestyleValue.getSIN()).getName() : null;
//            jsonLifestyle.options = lifestyleValue.getOptions().stream().map(o -> o.getOption().getName()).collect(Collectors.toList());
//            jsonLifestyle.description = getDescription(lifestyleValue.getLifestyle());
            jsonLifestyleList.add(jsonLifestyle);
        }
        jsonCharacter.lifestyles = jsonLifestyleList;
    }

    private void setSINs(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONSin> jsonSinList = new ArrayList<>();
        for (SIN sin : character.getSINs()) {
            JSONSin jsonSin = new JSONSin();
            jsonSin.name = sin.getName();
            jsonSin.description = sin.getDescription();
            jsonSin.quality = sin.getQualityValue();
            jsonSinList.add(jsonSin);
        }
        jsonCharacter.sins = jsonSinList;
    }

    private void setRituals(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONRitual> jsonRitualList = new ArrayList<>();
        for (RitualValue ritualValue : character.getRituals()) {
            JSONRitual jsonRitual = new JSONRitual();
            Ritual ritual = ritualValue.getModifyable();
            jsonRitual.name = ritual.getName();
            jsonRitual.features = ritual.getFeatures().stream().map(r -> r.getResolved().getName()).collect(Collectors.toList());
            jsonRitual.threshold = ritual.getThreshold();
            jsonRitual.page = getPageString(ritual);
            jsonRitual.description = getDescription(ritual);
            jsonRitualList.add(jsonRitual);
        }
        jsonCharacter.rituals = jsonRitualList;
    }

    private void setDrones(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONDrone> jsonDroneList = new ArrayList<>();
        for (CarriedItem<ItemTemplate> item : character.getCarriedItems(ItemType.droneTypes())) {
            JSONDrone drone = new JSONDrone();
            drone.name = item.getNameWithoutRating();
            drone.count = item.getCount();
            drone.handlOn = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.HANDLING).getModifiedValue()).getOnRoad();
            drone.handlOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.HANDLING).getModifiedValue()).getOffRoad();
            drone.speed = item.getAsValue(SR6ItemAttribute.TOPSPEED).getModifiedValue();
            drone.accelOn = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.ACCELERATION).getModifiedValue()).getOnRoad();
            drone.accelOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.ACCELERATION).getModifiedValue()).getOffRoad();
            drone.speedIntOn = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.SPEED_INTERVAL).getModifiedValue()).getOnRoad();
            drone.speedIntOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.SPEED_INTERVAL).getModifiedValue()).getOffRoad();
            drone.body = item.getAsValue(SR6ItemAttribute.BODY).getModifiedValue();
            drone.armor = item.getAsValue(SR6ItemAttribute.ARMOR).getModifiedValue();
            drone.pilot = item.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue();
            drone.sensor = item.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue();
            drone.page = getPageString(item.getResolved());
//            drone.type = item.getUsedAsType().name();
//            drone.subtype = item.getUsedAsSubType().name();
//            drone.accessories = getJsonItemAccessories(item.getUserAddedAccessories());
            drone.description = getDescription(item.getResolved());
            jsonDroneList.add(drone);
        }
        jsonCharacter.drones = jsonDroneList;
    }

    private void setVehicles(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONVehicle> jsonVehicleList = new ArrayList<>();
        for (CarriedItem<ItemTemplate> item : character.getCarriedItems( ItemType.VEHICLES)) {
            JSONVehicle vehicle = new JSONVehicle();
            vehicle.name = item.getNameWithoutRating();
            vehicle.handlOn = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.HANDLING).getModifiedValue()).getOnRoad();
            vehicle.handlOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.HANDLING).getModifiedValue()).getOffRoad();
            vehicle.pilot = item.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue();
            vehicle.body = item.getAsValue(SR6ItemAttribute.BODY).getModifiedValue();
            vehicle.accelOn = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.ACCELERATION).getModifiedValue()).getOnRoad();
            vehicle.accelOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.ACCELERATION).getModifiedValue()).getOffRoad();
            vehicle.speedIntOn = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.SPEED_INTERVAL).getModifiedValue()).getOnRoad();
            vehicle.speedIntOff = ((OnRoadOffRoadValue)item.getAsObject(SR6ItemAttribute.SPEED_INTERVAL).getModifiedValue()).getOffRoad();
            vehicle.speed = item.getAsValue(SR6ItemAttribute.TOPSPEED).getModifiedValue();
            vehicle.armor = item.getAsValue(SR6ItemAttribute.ARMOR).getModifiedValue();
            vehicle.sensor = item.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue();
            vehicle.seats = item.getAsValue(SR6ItemAttribute.SEATS).getModifiedValue();
//            vehicle.type = item.getUsedAsType().name();
//            vehicle.subtype = item.getUsedAsSubType().name();
//            vehicle.accessories = getJsonItemAccessories(item.getUserAddedAccessories());
            vehicle.page = getPageString(item.getResolved());
            vehicle.description = getDescription(item.getResolved());
            jsonVehicleList.add(vehicle);
        }
        jsonCharacter.vehicles = jsonVehicleList;
    }

    private void setAugmentations(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONAugmentation> jsonAugmentationList = new ArrayList<>();
        for (CarriedItem<ItemTemplate> item : character.getCarriedItems( ItemType.bodytechTypes())) {
            JSONAugmentation jsonAugmentation = new JSONAugmentation();
            jsonAugmentation.name = item.getNameWithoutRating();
            //TODO itemRating, quality
//            jsonAugmentation.level = item.getRating() > 0 ? String.valueOf(item.getRating()) : "-";
//            jsonAugmentation.quality = item.getQuality().name();
            if (item.getAsFloat(SR6ItemAttribute.ESSENCECOST)!=null)
            	jsonAugmentation.essence =  item.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue() / 1000.0f;
            jsonAugmentation.page = getPageString(item.getResolved());
            jsonAugmentation.description = getDescription(item.getResolved());
            jsonAugmentation.accessories = getJsonItemAccessories(item.getAccessories());
            jsonAugmentationList.add(jsonAugmentation);
        }
        jsonCharacter.augmentations = jsonAugmentationList;
    }

    private void setAdeptPowers(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONAdeptPower> jsonAdeptPowerList = new ArrayList<>();
        for (AdeptPowerValue adeptPower : character.getAdeptPowers()) {
            JSONAdeptPower jsonAdeptPower = new JSONAdeptPower();
            jsonAdeptPower.name = adeptPower.getNameWithoutRating();
            //TODO Adept power levels
//            if (adeptPower.getModifyable().hasLevels()) {
//                jsonAdeptPower.level = adeptPower.getModifiedLevel();
//            }
            jsonAdeptPower.activation = adeptPower.getModifyable().getActivation().name();
            jsonAdeptPower.cost = adeptPower.getCost();
            jsonAdeptPower.page = getPageString(adeptPower.getModifyable());
            jsonAdeptPower.description = getDescription(adeptPower.getModifyable());
            jsonAdeptPowerList.add(jsonAdeptPower);
        }
        jsonCharacter.adeptPowers = jsonAdeptPowerList;
    }

    private void setArmors(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONArmor> jsonArmorList = new ArrayList<>();
        for (CarriedItem<ItemTemplate> carriedItem : character.getCarriedItems(ItemType.ARMOR)) {
            JSONArmor jsonArmor = new JSONArmor();
            jsonArmor.name = carriedItem.getNameWithoutRating(loc);
            jsonArmor.isIgnored = carriedItem.hasFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS);
            if (carriedItem.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL)!=null)
            	jsonArmor.rating = carriedItem.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue();
            if (carriedItem.getAsValue(SR6ItemAttribute.DEFENSE_SOCIAL)!=null)
            	jsonArmor.socialrating =  carriedItem.getAsValue(SR6ItemAttribute.DEFENSE_SOCIAL).getModifiedValue();
            jsonArmor.accessories = getItemAccessories(carriedItem);
            jsonArmor.page = getPageString(carriedItem.getResolved());
            jsonArmor.description = getDescription(carriedItem.getResolved());
            jsonArmor.primary = carriedItem.hasFlag(SR6ItemFlag.PRIMARY);
            jsonArmorList.add(jsonArmor);
        }
        jsonCharacter.armors = jsonArmorList;
    }

    private JSONItem getJsonItem(CarriedItem<ItemTemplate> item) {
        JSONItem jsonItem = new JSONItem();
        jsonItem.name = item.getNameWithoutRating(loc);
        jsonItem.type = getItemType(item).getName();
        jsonItem.subType = getItemSubType(item).getName();
        jsonItem.count = item.getCount();
        //TODO item.getRating()
//        jsonItem.rating = item.getRating();
        jsonItem.accessories = getItemAccessories(item);
        jsonItem.page = getPageString(item.getResolved());
        jsonItem.description = getDescription(item.getResolved());
        return jsonItem;
    }

    private void setComplexForms(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONComplexForm> jsonComplexFormList = new ArrayList<>();
        for (ComplexFormValue complexForm : character.getComplexForms()) {
            JSONComplexForm jsonComplexForm = new JSONComplexForm();
            jsonComplexForm.name = complexForm.getNameWithoutRating(loc);
            jsonComplexForm.duration = complexForm.getModifyable().getDuration().getName();
            jsonComplexForm.fading = complexForm.getModifyable().getFading();
            List<DataItem> influences = Shadowrun6Tools.getInfluences(complexForm);
            jsonComplexForm.influences = influences.stream().map(DataItem::getName).collect(Collectors.toList());
            jsonComplexForm.page = getPageString(complexForm.getModifyable());
            jsonComplexForm.description = getDescription(complexForm.getModifyable());
            jsonComplexFormList.add(jsonComplexForm);
        }

        jsonCharacter.complexForms = jsonComplexFormList;
    }

    private void setCloseCombatWeapons(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONCloseCombatWeapon> jsonCloseCombatWeaponList = new ArrayList<>();
        for (CarriedItem<ItemTemplate> closeCombatWeapon : getCloseCombatWeapons(character)) {
            JSONCloseCombatWeapon jsonCloseCombatWeapon = new JSONCloseCombatWeapon();
            jsonCloseCombatWeapon.name = closeCombatWeapon.getNameWithoutRating(loc);
            jsonCloseCombatWeapon.type = getItemType(closeCombatWeapon).getName();
            jsonCloseCombatWeapon.subtype = getItemSubType(closeCombatWeapon).getName();
            //TODO weapon skill
//            jsonCloseCombatWeapon.skill = closeCombatWeapon.getResolved().getWeaponData().getSkill().getId();
            jsonCloseCombatWeapon.pool = Shadowrun6Tools.getWeaponPool(character, closeCombatWeapon).getNatural();
            jsonCloseCombatWeapon.damage = Shadowrun6Tools.getWeaponDamage(character, closeCombatWeapon).toString();
            jsonCloseCombatWeapon.attackRating = Shadowrun6Tools.getAttackRatingString(closeCombatWeapon.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());
            jsonCloseCombatWeapon.wifi = Shadowrun6Tools.getWiFiAdvantageStrings(closeCombatWeapon, loc);
            jsonCloseCombatWeapon.accessories = getItemAccessories(closeCombatWeapon);
            jsonCloseCombatWeapon.page = getPageString(closeCombatWeapon.getResolved());
            jsonCloseCombatWeapon.description = getDescription(closeCombatWeapon.getResolved());
            jsonCloseCombatWeapon.primary = closeCombatWeapon.hasFlag(SR6ItemFlag.PRIMARY);
            jsonCloseCombatWeaponList.add(jsonCloseCombatWeapon);
        }
        jsonCharacter.closeCombatWeapons = jsonCloseCombatWeaponList;
    }

    private void setLongRangeWeapons(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONLongRangeWeapon> jsonLongRangeWeapons = new ArrayList<>();
        for (CarriedItem<ItemTemplate> longRangeWeapon : getLongRangeWeapons(character)) {
            JSONLongRangeWeapon jsonLongRangeWeapon = new JSONLongRangeWeapon();
            jsonLongRangeWeapon.name = longRangeWeapon.getNameWithoutRating(loc);
            jsonLongRangeWeapon.type = getItemType(longRangeWeapon).getName();
            jsonLongRangeWeapon.subtype = getItemSubType(longRangeWeapon).getName();
            //TODO WeaponSkill
            //jsonLongRangeWeapon.skill = longRangeWeapon.getResolved().getWeaponData().getSkill().getId();
            jsonLongRangeWeapon.pool = Shadowrun6Tools.getWeaponPool(character, longRangeWeapon).getNatural();
            jsonLongRangeWeapon.damage = Shadowrun6Tools.getWeaponDamage(character, longRangeWeapon).toString();
            jsonLongRangeWeapon.attackRating = Shadowrun6Tools.getAttackRatingString(longRangeWeapon.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());
            ItemAttributeObjectValue<SR6ItemAttribute> firemodes = longRangeWeapon.getAsObject(SR6ItemAttribute.FIREMODES);
            if (firemodes != null) {
                jsonLongRangeWeapon.mode = ((ArrayList<FireMode>)firemodes.getValue()).stream().map(fm -> fm.getName(loc)).collect(Collectors.joining(","));
            }
            jsonLongRangeWeapon.ammunition = Shadowrun6Tools.getItemAttributeString(character, longRangeWeapon, SR6ItemAttribute.AMMUNITION);
            jsonLongRangeWeapon.wifi = Shadowrun6Tools.getWiFiAdvantageStrings(longRangeWeapon, loc);
            jsonLongRangeWeapon.accessories = getItemAccessories(longRangeWeapon);
            jsonLongRangeWeapon.page = getPageString(longRangeWeapon.getResolved());
            jsonLongRangeWeapon.description = getDescription(longRangeWeapon.getResolved());
            jsonLongRangeWeapon.primary = longRangeWeapon.hasFlag(SR6ItemFlag.PRIMARY);
            jsonLongRangeWeapons.add(jsonLongRangeWeapon);
        }
        jsonCharacter.longRangeWeapons = jsonLongRangeWeapons;
    }

    private void setQualities(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONQuality> jsonQualities = new ArrayList<>();
        for (QualityValue qualityValue : character.getQualities()) {
            JSONQuality jsonQuality = new JSONQuality();
            Quality quality = qualityValue.getModifyable();
            jsonQuality.name = quality.getName(loc);
            jsonQuality.choice = qualityValue.getDecisionString(loc);
            jsonQuality.id = quality.getId();
            if (quality.getMax()>0) {
                jsonQuality.rating = qualityValue.getModifiedValue();
            }
            jsonQuality.positive = quality.isPositive();
            jsonQuality.page = getPageString(quality);
            jsonQuality.description = getDescription(quality);
            jsonQualities.add(jsonQuality);
        }
        jsonCharacter.qualities = jsonQualities;
    }

    private void setConnections(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONContact> jsonContacts = new ArrayList<>();
        for (Contact connection : character.getContacts()) {
            JSONContact jsonContact = new JSONContact();
            jsonContact.name = connection.getName();
            if (jsonContact.name==null) jsonContact.name="Unnamed";
            jsonContact.type = connection.getTypeName();
            jsonContact.loyalty = connection.getLoyalty();
            jsonContact.influence = connection.getRating();
            jsonContact.description = connection.getDescription();
            jsonContact.favors = connection.getFavors();
            jsonContacts.add(jsonContact);
        }
        jsonCharacter.contacts = jsonContacts;
    }

    private void setMatrixItems(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONMatrixItem> matrixItems = new ArrayList<>();
        for (CarriedItem<ItemTemplate> matrixItem : Shadowrun6Tools.getMatrixItems(character)) {
            JSONMatrixItem item = new JSONMatrixItem();
            item.name = matrixItem.getNameWithoutRating(loc);
            item.subType = getItemSubType(matrixItem);
            if (getItemSubType(matrixItem) != null) {
                item.type = getItemType(matrixItem).getName();
            }
            if (matrixItem.hasAttribute(SR6ItemAttribute.ATTACK)) {
                item.attack = matrixItem.getAsValue(SR6ItemAttribute.ATTACK).getModifiedValue();
            }
            if (matrixItem.hasAttribute(SR6ItemAttribute.DATA_PROCESSING)) {
                item.dataProcessing = matrixItem.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue();
            }
            if (matrixItem.hasAttribute(SR6ItemAttribute.SLEAZE)) {
                item.sleaze = matrixItem.getAsValue(SR6ItemAttribute.SLEAZE).getModifiedValue();
            }
            if (matrixItem.hasAttribute(SR6ItemAttribute.FIREWALL)) {
                item.firewall = matrixItem.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue();
            }
            if (matrixItem.getResolved()!=null) {
            	item.page = getPageString(matrixItem.getResolved());
                //TODO item deviceRating
//                item.deviceRating =  matrixItem.getResolved().getDeviceRating();
                item.description = getDescription(matrixItem.getResolved());
                ItemAttributeNumericalValue<SR6ItemAttribute> concurrentPrograms = matrixItem.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS);
                if (concurrentPrograms != null) {
                    item.concurrentPrograms = concurrentPrograms.getModifiedValue();
                }
                item.programs = getJsonItemList(getProgramsOnDevice(matrixItem));
                item.accessories = getJsonItemAccessories(getAccessoriesWithoutPrograms(matrixItem));
            }
            matrixItems.add(item);
        }
        jsonCharacter.matrixItems = matrixItems;
    }

    private void setItems(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<CarriedItem<ItemTemplate>> items = character.getCarriedItems( ItemType.gearTypes());
        items.addAll(character.getCarriedItems( ItemType.MAGICAL));
        items.removeAll(Shadowrun6Tools.getMatrixItems(character));
        List<CarriedItem<ItemTemplate>> filteredItems = new ArrayList<>();
        for (CarriedItem item : items) {
            ItemType type = getItemType(item);
            if (type != ItemType.ARMOR) {
                filteredItems.add(item);
            }
        }
        jsonCharacter.items = getJsonItemList(filteredItems);
    }

    private List<JSONItem> getJsonItemList(List<CarriedItem<ItemTemplate>> filteredItems) {
        List<JSONItem> jsonItems = new ArrayList<>();
        for (CarriedItem<ItemTemplate> item : filteredItems) {
            JSONItem jsonItem = getJsonItem(item);
            jsonItems.add(jsonItem);
        }
        return jsonItems;
    }

    private List<JSONItemAccessory> getItemAccessories(CarriedItem<ItemTemplate> item) {
        Collection<CarriedItem<ItemTemplate>> carriedItemAccessories = item.getAccessories();
        return getJsonItemAccessories(carriedItemAccessories);
    }

    private List<JSONItemAccessory> getJsonItemAccessories(Collection<CarriedItem<ItemTemplate>> carriedItemAccessories) {
        List<JSONItemAccessory> jsonItemAccessoryList = new ArrayList<>();
        for (CarriedItem<ItemTemplate> userAddedAccessory : carriedItemAccessories) {
            JSONItemAccessory jsonItemAccessory = new JSONItemAccessory();
            jsonItemAccessory.name = userAddedAccessory.getNameWithoutRating(loc);
            //TODO item rating
//            jsonItemAccessory.rating = userAddedAccessory.getRating();
            jsonItemAccessory.description = getDescription(userAddedAccessory.getResolved());
            if (getItemType(userAddedAccessory) != null) {
                jsonItemAccessory.type = getItemType(userAddedAccessory).getName();
            }
            if (getItemSubType(userAddedAccessory) != null) {
                jsonItemAccessory.subType = getItemSubType(userAddedAccessory).getName();
            }
            jsonItemAccessoryList.add(jsonItemAccessory);
        }
        return jsonItemAccessoryList;
    }

    private void setSpells(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONSpell> jsonSpells = new ArrayList<>();
        for (SpellValue<SR6Spell> spellValue : character.getSpells()) {
            JSONSpell jsonSpell = new JSONSpell();
            SR6Spell spell = spellValue.getModifyable();
            jsonSpell.name = spell.getName();
            jsonSpell.id = spell.getId();
            jsonSpell.category = spell.getCategory().getName();
            jsonSpell.type = spell.getType().getName();
            jsonSpell.duration = spell.getDuration().getName();
            jsonSpell.range = spell.getRange().getName();
            jsonSpell.drain = spell.getDrain();
            jsonSpell.page = getPageString(spell);
            jsonSpell.features = spell.getFeatures().stream().map(f -> f.getFeature().getId()).collect(Collectors.toList());
            List<DataItem> influences = Shadowrun6Tools.getInfluences(spellValue);
            jsonSpell.influencedBy = influences.stream().map(DataItem::getName).collect(Collectors.toList());
            jsonSpell.description = getDescription(spell);
            jsonSpells.add(jsonSpell);
        }
        jsonCharacter.spells = jsonSpells;
    }

    private String getPageString(DataItem item) {
		List<String> refs = new ArrayList<>();
		for (PageReference ref : item.getPageReferences()) {
			if (ref.getLanguage().equals(loc.getLanguage())) {
				refs.add(ref.getProduct().getName(loc)+" "+ref.getPage());
			}
		}
		return String.join(", ", refs);
    }

    private void setSkills(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONSkill> jsonSkills = new ArrayList<>();
        for (SR6SkillValue skillValue : character.getSkillValues()) {
            jsonSkills.add(getJSONSkill(skillValue, character));
        }
        jsonCharacter.skills = jsonSkills;
    }

    private JSONSkill getJSONSkill(SR6SkillValue skillValue, Shadowrun6Character character) {
        JSONSkill jsonSkill = new JSONSkill();
        jsonSkill.name = skillValue.getNameWithoutRating();
        jsonSkill.id = skillValue.getModifyable().getId();
        ShadowrunAttribute attr = skillValue.getModifyable().getAttribute();
        jsonSkill.attribute = attr.getName();
        jsonSkill.rating = skillValue.getDistributed();
        jsonSkill.pool = Shadowrun6Tools.getSkillPool(character, skillValue.getModifyable()).getValue(ValueType.AUGMENTED);
        jsonSkill.description = getDescription(skillValue.getModifyable());
        List<SkillSpecializationValue<SR6Skill>> specializations = skillValue.getSpecializations();
        List<JSONSkillSpecialization> jsonSkillSpecializationList = new ArrayList<>();
        for (SkillSpecializationValue<SR6Skill> specialization : specializations) {
            JSONSkillSpecialization jsonSpec = new JSONSkillSpecialization();
            ShadowrunAttribute specAttr = attr;
            if (specialization.getResolved().getAttribute() != null) {
                String attrString = specialization.getResolved().getAttribute();
                if (attrString != null) {
                    specAttr = ShadowrunAttribute.valueOf(attrString);
                }
            }
            jsonSpec.attribute = specAttr.getName();
            jsonSpec.name = specialization.getNameWithoutRating(loc);
            jsonSpec.id = specialization.getResolved().getId();
            jsonSpec.expertise = specialization.getDistributed() == 2;
            jsonSpec.description = getDescription(specialization.getResolved());
            int mod = specialization.getDistributed();
            jsonSpec.pool = mod + Shadowrun6Tools.getSkillPool(character, skillValue.getModifyable(), specAttr).getValue(ValueType.AUGMENTED);
            jsonSkillSpecializationList.add(jsonSpec);
        }
        jsonSkill.specializations = jsonSkillSpecializationList;
        List<DataItem> influences = Shadowrun6Tools.getInfluences(skillValue);
        jsonSkill.influencedBy = influences.stream().map(DataItem::getName).collect(Collectors.toList());
        return jsonSkill;
    }

    private void setAttributes(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        List<JSONAttribute> jsonAttributes = new ArrayList<>();
        List<ShadowrunAttribute> shadowrunAttributes = new ArrayList<>(Arrays.asList(attributesToSave()));
        shadowrunAttributes.addAll(Arrays.asList(derivedValues()));
        List<AttributeValue<ShadowrunAttribute>> attributes = new ArrayList<>();
        for (ShadowrunAttribute shadowrunAttribute : shadowrunAttributes) {
            attributes.add(character.getAttribute(shadowrunAttribute));
        }
        attributes = attributes.stream().filter(a -> !a.getModifyable().equals(INITIATIVE_ASTRAL)
                && !a.getModifyable().equals(INITIATIVE_MATRIX)
                && !a.getModifyable().equals(INITIATIVE_PHYSICAL)).collect(Collectors.toList());
        for (AttributeValue<ShadowrunAttribute> attribute : attributes) {
            jsonAttributes.add(getJSONAttribute(attribute));
        }
        jsonCharacter.attributes = jsonAttributes;
    }

    private JSONAttribute getJSONAttribute(AttributeValue<ShadowrunAttribute> attribute) {
        JSONAttribute jsonAttribute = new JSONAttribute();
        jsonAttribute.name = attribute.getModifyable().getName();
        jsonAttribute.id = attribute.getModifyable().name();
        jsonAttribute.points = attribute.getDistributed();
        jsonAttribute.modifiedValue = attribute.getModifiedValue();
        return jsonAttribute;
    }

    private void setGeneralInfo(JSONCharacter jsonCharacter, Shadowrun6Character character) {
        jsonCharacter.name = character.getRealName();
        jsonCharacter.streetName = character.getName();
        jsonCharacter.metaType = character.getMetatype().getName();
        jsonCharacter.size = character.getSize();
        jsonCharacter.weight = character.getWeight();
        jsonCharacter.age = character.getAge();
        jsonCharacter.gender = character.getGender().toString();
        jsonCharacter.karma = character.getKarmaFree() + character.getKarmaInvested();
        jsonCharacter.freeKarma = character.getKarmaFree();
        //TODO notes fehlen noch
//        jsonCharacter.notes = character.getNotes();
        jsonCharacter.nuyen = character.getNuyen();
        jsonCharacter.heat = character.getAttribute(ShadowrunAttribute.HEAT).getModifiedValue();
        jsonCharacter.reputation = character.getAttribute(ShadowrunAttribute.REPUTATION).getModifiedValue();
        MagicOrResonanceType magicOrResonanceType = character.getMagicOrResonanceType();
        if (magicOrResonanceType != null && magicOrResonanceType.usesMagic()) {
            jsonCharacter.initiation = character.getInitiateSubmersionLevel();
            jsonCharacter.metamagic = getMetaMagicOrEcho(character);
        } else if (magicOrResonanceType != null && magicOrResonanceType.usesResonance()) {
            jsonCharacter.submersion = character.getInitiateSubmersionLevel();
            jsonCharacter.echoes = getMetaMagicOrEcho(character);
        }
    }

    private List<JSONMetaMagicOrEcho> getMetaMagicOrEcho(Shadowrun6Character character) {
        List<JSONMetaMagicOrEcho> jsonMetaMagicOrEchoList = new ArrayList<>();
        for (MetamagicOrEchoValue metamagicOrEcho : character.getMetamagicOrEchoes()) {
            JSONMetaMagicOrEcho jsonMeta = new JSONMetaMagicOrEcho();
            jsonMeta.name = metamagicOrEcho.getNameWithoutRating(loc);
            jsonMeta.page = getPageString(metamagicOrEcho.getModifyable());
            jsonMeta.description = getDescription(metamagicOrEcho.getModifyable());
            jsonMetaMagicOrEchoList.add(jsonMeta);
        }
        return jsonMetaMagicOrEchoList;
    }

    private static List<CarriedItem<ItemTemplate>> getLongRangeWeapons(Shadowrun6Character character) {
        List<CarriedItem<ItemTemplate>> result = new ArrayList<>();
        List<CarriedItem<ItemTemplate>> items = character.getCarriedItems(ItemType.weaponTypes());
        for (CarriedItem<ItemTemplate> weapon : items) {
            if (Shadowrun6Tools.getItemType(weapon)!=ItemType.WEAPON_CLOSE_COMBAT) {
                result.add(weapon);
            }
        }
        return result;
    }

    private static List<CarriedItem<ItemTemplate>> getCloseCombatWeapons(Shadowrun6Character character) {
        List<CarriedItem<ItemTemplate>> result = new ArrayList<>();
        List<CarriedItem<ItemTemplate>> items = character.getCarriedItems(ItemType.weaponTypes());
        for (CarriedItem<ItemTemplate> weapon : items) {
            if (Shadowrun6Tools.getItemType(weapon)==ItemType.WEAPON_CLOSE_COMBAT) {
                result.add(weapon);
            }
        }
        return result;
    }



    private static List<CarriedItem<ItemTemplate>> getProgramsOnDevice(CarriedItem<ItemTemplate> item) {
        Collection<CarriedItem<ItemTemplate>> userAddedAccessories = item.getAccessories();
        return userAddedAccessories.stream()
                .filter(carriedItem -> (Shadowrun6Tools.isSubtype(carriedItem, ItemSubType.BASIC_PROGRAM) || Shadowrun6Tools.isSubtype(carriedItem, ItemSubType.HACKING_PROGRAM))).collect(Collectors.toList());
    }

    private static List<CarriedItem<ItemTemplate>> getAccessoriesWithoutPrograms(CarriedItem<ItemTemplate> item) {
        Collection<CarriedItem<ItemTemplate>> userAddedAccessories = item.getAccessories();
        return userAddedAccessories.stream()
                .filter(carriedItem -> (!Shadowrun6Tools.isSubtype(carriedItem,ItemSubType.BASIC_PROGRAM)
                        && !Shadowrun6Tools.isSubtype(carriedItem, ItemSubType.HACKING_PROGRAM)))
                .collect(Collectors.toList());
    }

    private String getDescription(DataItem data) {
        //TODO custom helptext
//        if (data.hasCustomHelpText()) {
//            return data.getCustomHelpText();
//        } else {
            return "";
//        }
    }

    private ShadowrunAttribute[] derivedValues() {
        return new ShadowrunAttribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_ASTRAL,
                DEFENSE_POOL_PHYSICAL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY
        };
    }

    private ShadowrunAttribute[] attributesToSave() {
        return new ShadowrunAttribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,
                CHARISMA,EDGE,MAGIC,RESONANCE, POWER_POINTS};
    }

    private ItemType getItemType(CarriedItem<ItemTemplate> item) {
        return item.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
    }

    private ItemSubType getItemSubType(CarriedItem<ItemTemplate> item) {
        return item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue();
    }
}
