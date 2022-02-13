package de.rpgframework.eden.foundry.sr6;

import java.lang.System.Logger;
import java.util.Locale;

import de.rpgframework.foundry.ActorData;
import de.rpgframework.foundry.ItemData;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.foundry.FVTTAdeptPower;
import de.rpgframework.shadowrun6.foundry.FVTTGear;
import de.rpgframework.shadowrun6.foundry.FVTTQuality;
import de.rpgframework.shadowrun6.foundry.FVTTSpell;
import de.rpgframework.shadowrun6.foundry.FVTTVehicleActor;
import de.rpgframework.shadowrun6.foundry.FVTTWeapon;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
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
	public static ItemData<FVTTGear> convert(ItemTemplate tmp, Locale loc) {
		FVTTGear data = new FVTTGear();
		if (ItemType.isWeapon(tmp.getItemType()))
			data = new FVTTWeapon();
		// Definition fields
		data.genesisID  = tmp.getId();
		if (tmp.getItemType()!=null)
			data.type       = tmp.getItemType().name();
		if (tmp.getItemSubtype()!=null)
			data.subtype    = tmp.getItemSubtype().name();
		if (tmp.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
			data.availDef   = tmp.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue();
		if (tmp.getAttribute(SR6ItemAttribute.SKILL)!=null)
			data.skill      = tmp.getAttribute(SR6ItemAttribute.SKILL).getRawValue();
		if (tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION)!=null)
			data.skillSpec  = tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION).getRawValue();

		if ((data instanceof FVTTWeapon) && tmp.getAttribute(SR6ItemAttribute.DAMAGE)!=null) 	
			((FVTTWeapon)data).dmgDef     = tmp.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue();

		return new ItemData<FVTTGear>(tmp.getName(loc), "gear", data);
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTGear> convert(CarriedItem<ItemTemplate> val, Locale loc) {
		ItemData<FVTTGear> ret = convert(val.getModifyable(), loc);
		FVTTGear fVal = ret.getData();
		// Value fields
//		fVal.customName = val.get;
//		fVal.explain = val.getDescription();

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
		spell.category  = item.getCategory().name().toLowerCase();
		spell.duration  = item.getDuration().name().toLowerCase();
		spell.drain     = item.getDrain();
		spell.range     = item.getRange().name().toLowerCase();
		spell.type      = item.getType().name().toLowerCase();
		if (item.getDamage()!=null)
			spell.damage    = item.getDamage().name().toLowerCase();
		spell.isOpposed = item.isOpposed();
		spell.withEssence = item.isEssence();
		spell.wild      = item.isWild();
		for (SpellFeatureReference ref : item.getFeatures()) {
			switch (ref.getFeature().getId()) {
			case "sense_multi": spell.multiSense=true; break; 
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
	public static ActorData<? extends FVTTGear> convertActor(ItemTemplate item, Locale loc) {
		FVTTVehicleActor actor = new FVTTVehicleActor();

		ActorData foundry = new ActorData(item.getName(loc), "vehicle", actor);
		return foundry;
	}

}
