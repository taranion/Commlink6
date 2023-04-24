package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.Lifeform;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.VehicleData.VehicleType;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6GearTool extends GearTool {

	public final static Logger logger = System.getLogger(SR6GearTool.class.getPackageName());

	public static CarriedItemProcessor[] SR6_PHASE1_STEPS = new CarriedItemProcessor[] {
			new SR6ModeDetailsStep(),
			new GetModificationsStep(),
			new SR6ResolveFormulasStep(),
			new ApplyAmmunitionTypeStep(),
			new ApplyStockModificationsStep(),
			new AddMissingWeaponSlots(),
			new CalculateVehicleSlots(),
			new SR6ResolveTemplatesStep(),
			new CreateAlternatesStep()
	};

	public static CarriedItemProcessor[] SR6_PHASE2_STEPS = new CarriedItemProcessor[] {
//			new DetermineStandardEssenceStep(),
			new DeriveCapacityAttributeStep(),
			new CalculateAccessorySizes(),
			new HandleAugmentationGradeStep(),
			new ApplyItemFlagsStep(),
			new AddUpPricesStep(),
			new AddChemicalPriceStep()
	};

	//-------------------------------------------------------------------
	public static Availability calculateModifiedValue(Availability base, List<Modification> mods) {
		Availability ret = new Availability(base.getValue(), base.getLegality(), false);
		for (Modification tmp : mods) {
			if (tmp instanceof ValueModification) {
				ValueModification mod = (ValueModification)tmp;
				ret.setValue(ret.getValue() + mod.getValue());
			}
		}

		return ret;
	}

	//-------------------------------------------------------------------
	public static int[] calculateModifiedValue(int[] base, List<Modification> mods) {
		int[] ret = new int[5];
		for (int i=0; i<base.length; i++)
			ret[i] = base[i];
		for (Modification tmp : mods) {
			if (tmp instanceof ValueModification) {
				ValueModification mod = (ValueModification)tmp;
				String[] keys = mod.getValueAsKeys();
				if (keys.length==1) {
					for (int i=0; i<base.length; i++) {
						ret[i] += mod.getValue();
					}
				} else {
					for (int i=0; i<Math.min(base.length,keys.length); i++) {
						if (!keys[i].isBlank()) {
							ret[i] += Integer.parseInt(keys[i].trim());
						}
					}
				}
			} else {
				logger.log(Level.ERROR, "########calculateModifiedARValue TODO: "+tmp);
			}
		}

		return ret;
	}

	//-------------------------------------------------------------------
	public static Damage calculateModifiedValue(Damage base, List<Modification> mods) {
		Damage ret = new Damage(base, List.of());
		for (Modification tmp : mods) {
			logger.log(Level.ERROR, "########calculateModifiedDamageValue: TODO: "+tmp);
			if (tmp instanceof ValueModification) {
				ValueModification mod = (ValueModification)tmp;
				ret.setValue( ret.getValue() + mod.getValue());
			}
		}
		logger.log(Level.ERROR, "--->Base damage {0} plus {1} = {2}", base, mods, ret);
		return ret;
	}

	//-------------------------------------------------------------------
	public static <I extends IItemAttribute> OperationResult<List<Modification>> recalculate(String indent, Lifeform user, CarriedItem<ItemTemplate> item) {
		logger.log(Level.INFO, "recalculate "+item.getKey());
		if (item.getResolved()==null) {
			if (item.getUuid().equals(ItemTemplate.UUID_UNARMED))
				return new OperationResult<>(new ArrayList<>());
			ItemTemplate temp = ShadowrunReference.GEAR.resolve(item.getTemplateID());
			if (temp==null)
				logger.log(Level.ERROR, "No such ItemTemplate {0}", item.getTemplateID());
			else
				item.setResolved(temp);
		}
		try {
			item.setDirty(false);
			return GearTool.recalculate(indent, ShadowrunReference.ITEM_ATTRIBUTE, user, item);
		} catch (DataErrorException e) {
			item.setDirty(true);
			if (e.getReferenceError()!=null) e.getReferenceError().setType(ShadowrunReference.GEAR);
			throw e;
		}
	}

	//-------------------------------------------------------------------
	public static int getRating(CarriedItem<ItemTemplate> item) {
		Decision dec = item.getDecision(ItemTemplate.UUID_RATING);
		if (dec==null) return 0;
		return Integer.parseInt(dec.getValue());
	}

	//-------------------------------------------------------------------
	public static SkillSpecialization<SR6Skill> getSpecializationForVehicle(ItemTemplate item) {
		SR6Skill pilot = Shadowrun6Core.getSkill("piloting");
		if (pilot==null)
			return null;
//		if (item.isNoSpecialization())
//			return null;

		ItemType typeI = item.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue();
		ItemSubType sub = item.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue();
		switch (typeI) {
		case VEHICLES:
			switch (sub) {
			case BIKES:
			case ATVS:
			case CARS:
			case TRUCKS:
			case VANS:
			case BUS:
			case TRACKED:
			case SPECIAL_VEHICLES:
			case WALKER:
				return pilot.getSpecialization("ground_craft") ;
			case HOVERCRAFT:
			case PWC:
			case BOATS:
			case SHIPS:
			case SUBMARINES:
				return pilot.getSpecialization("watercraft") ;
			case FIXED_WING:
			case ROTORCRAFT:
			case LAV:
			case LTAV:
			case GRAV:
			case SPACECRAFT:
			case VTOL:
				return pilot.getSpecialization("aircraft") ;
			default:
			}
			break;
		case DRONE_MICRO:
		case DRONE_MINI:
		case DRONE_SMALL:
		case DRONE_MEDIUM:
		case DRONE_LARGE:
			ItemAttributeDefinition vecTypDef = item.getAttribute(SR6ItemAttribute.VEHICLE_TYPE);
			if (vecTypDef==null) {
				// No explicit vehicle type given - use subtype for a default
				switch (sub) {
				case BIKES:
				case CARS:
				case TRUCKS:
					return pilot.getSpecialization("ground_craft") ;
				case BOATS:
				case SUBMARINES:
					return pilot.getSpecialization("watercraft") ;
				case FIXED_WING:
				case ROTORCRAFT:
				case VTOL:
					return pilot.getSpecialization("aircraft") ;
				default:
				}
				return null;
			}
			VehicleType type = vecTypDef.getValue();
			switch (type) {
			case GROUND:
				return pilot.getSpecialization("ground_craft") ;
			case AIR:
				return pilot.getSpecialization("aircraft") ;
			case WATER:
				return pilot.getSpecialization("watercraft") ;
			}

		default:
		}
		return null;
	}

}
