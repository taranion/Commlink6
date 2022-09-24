package de.rpgframework.shadowrun6.export.json.model;

import de.rpgframework.core.RoleplayingSystem;

import java.util.List;

public class JSONCharacter {
    private String system = RoleplayingSystem.SHADOWRUN6.name();
    private String version = "3.2.0";

    public String name;
    public String streetName;
    public String metaType;
    public int size;
    public int weight;
    public String age;
    public String gender;
    public int heat;
    public int reputation;
    public int karma;
    public int freeKarma;
    public int nuyen;
    public int initiation;
    public int submersion;
    public List<JSONAttribute> attributes;
    public List<JSONInitiative> initiatives;
    public List<JSONQuality> qualities;
    public List<JSONSkill> skills;
    public List<JSONSpell> spells;
    public List<JSONRitual> rituals;
    public List<JSONMetaMagicOrEcho> metamagic;
    public List<JSONMetaMagicOrEcho> echoes;
    public List<JSONComplexForm> complexForms;
    public List<JSONAdeptPower> adeptPowers;
    public List<JSONLongRangeWeapon> longRangeWeapons;
    public List<JSONCloseCombatWeapon> closeCombatWeapons;
    public List<JSONArmor> armors;
    public List<JSONItem> items;
    public List<JSONAugmentation> augmentations;
    public List<JSONVehicle> vehicles;
    public List<JSONDrone> drones;
    public List<JSONLifestyle> lifestyles;
    public List<JSONSin> sins;
    public List<JSONContact> contacts;
    public List<JSONLicense> licenses;
    public List<JSONMatrixItem> matrixItems;
    public List<JSONMartialArts> martialArts;
    public List<JSONSignatureManeuver> signatureManeuvers;
    public String notes;
}
