package de.rpgframework.shadowrun6.vehicle;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.PoolCalculation;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.VehicleData;
import de.rpgframework.shadowrun6.items.VehicleData.VehicleType;

/**
 * @author prelle
 *
 */
public class VehicleTools {

	private final static Logger logger = System.getLogger("shadowrun6");

	//---------------------------------------------------------
	public static VehicleUnarmedAttack getVehicleUnarmed(Shadowrun6Character model, CarriedItem<ItemTemplate> vehicle, VehicleOperationMode mode) {
		// Collect some necessary data
		CarriedItem<ItemTemplate> simrig = model.getCarriedItem("control_rig");
		int simRigRating = (simrig==null)?0:ItemUtil.getRating(simrig);
		CarriedItem<ItemTemplate> rcc = Shadowrun6Tools.getBestRCC(model);
		CarriedItem<ItemTemplate> simSenseOverdrive = model.getCarriedItem("simsense_overdrive");

		SR6Skill pilotSkill = Shadowrun6Core.getSkill("piloting");
		String spec = (getSpecializationForVehicle(vehicle.getResolved())!=null)?getSpecializationForVehicle(vehicle.getResolved()).getId():null;
		List<PoolCalculation<Integer>> driverPilotPool = Shadowrun6Tools.getSkillPool(model, pilotSkill,(ShadowrunAttribute)null, spec).getCalculation(ValueType.ARTIFICIAL);
		if (mode==VehicleOperationMode.AUTONOMOUS) {
			CarriedItem<ItemTemplate> autosoft = vehicle.getEmbeddedItem("maneuvering");
			driverPilotPool.clear();
			if (autosoft == null)
				driverPilotPool.add(new PoolCalculation<Integer>(-1, Shadowrun6Core.getItem(ItemTemplate.class,"maneuvering").getName()));
			else
				driverPilotPool.add(new PoolCalculation<Integer>(ItemUtil.getRating(autosoft), autosoft.getNameWithRating()));
		} else if (mode==VehicleOperationMode.RCC) {
			if (rcc==null) return null;
			CarriedItem<ItemTemplate> autosoft = rcc.getEmbeddedItem("maneuvering");
			driverPilotPool.clear();
			if (autosoft == null)
				driverPilotPool.add(new PoolCalculation<Integer>(-1, Shadowrun6Core.getItem(ItemTemplate.class,"maneuvering").getName()));
			else
				driverPilotPool.add(new PoolCalculation<Integer>(ItemUtil.getRating(autosoft), autosoft.getNameWithRating()));
		}

		int sensor = vehicle.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue();
		int body   = vehicle.getAsValue(SR6ItemAttribute.BODY).getModifiedValue();
		int armor  = vehicle.getAsValue(SR6ItemAttribute.ARMOR).getModifiedValue();
		int pilot  = vehicle.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue();

		ToIntFunction<PoolCalculation<Integer>> pcInt = new ToIntFunction<PoolCalculation<Integer>>() {
			public int applyAsInt(PoolCalculation<Integer> pc) {
				return pc.value;
			}
		};
		VehicleUnarmedAttack ret = new VehicleUnarmedAttack(mode);
		// Damage is (Body / 2, rounded up, +1 for each current Speed Interval)
		// Physical damage
		int dmg= Math.round(body/2.0f);
		ret.setDamage(new Damage(dmg, DamageType.PHYSICAL, DamageElement.REGULAR));

		switch (mode) {
		case DRIVING:
			// The Attack Rating of a vehicle is Piloting of the driver + Sensor.
			List<PoolCalculation<Integer>> arPool = new ArrayList<>(driverPilotPool);
			arPool.add(new PoolCalculation<Integer>(sensor, SR6ItemAttribute.SENSORS.getName()));
			int ar = (int)arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setAttackRating(ar);
			ret.setAttackRating(arPool);
			logger.log(Level.INFO,"Unarmed: "+vehicle.getNameWithoutRating()+": AR   driven "+arPool);
//			 The attempt to hit another being or vehicle is an Opposed test
//			 — Piloting + Reaction vs.
			List<PoolCalculation<Integer>> attPool = new ArrayList<>(driverPilotPool);
			//attPool.addAll(Shadowrun6Tools.getAttributePoolCalculation(model, ShadowrunAttribute.REACTION));
//			//logger.debug("Unarmed(MANUAL  ): "+vehicle.getName()+"::  "+attPool);
			int dice = (int)attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setPool(dice);
			ret.setPool(attPool);
			break;
		case JUMPED_IN:
			// The Attack Rating of a vehicle is Piloting of the driver + Sensor.
			// Assume rating of simrig is added
			if (simrig==null)
				return null;
			arPool = new ArrayList<>(driverPilotPool);
			arPool.add(new PoolCalculation(sensor, SR6ItemAttribute.SENSORS.getName()));
			arPool.add(new PoolCalculation(simRigRating, simrig.getNameWithRating()));
			ar = (int)arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setAttackRating(ar);
			ret.setAttackRating(arPool);
//			logger.info("Unarmed: "+vehicle.getName()+": Rigged "+arPool);
			// The attempt to hit another being or vehicle is an Opposed test
			// — Piloting + Reaction vs.
			attPool = new ArrayList<>(driverPilotPool);
			attPool.addAll(model.getAttribute(ShadowrunAttribute.INTUITION).getPool().getCalculation(ValueType.AUGMENTED));
			if (simSenseOverdrive!=null) {
				int maxAdd = 4 - model.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
				int add  = Math.min(maxAdd, simSenseOverdrive.getDistributed());
				attPool.add(new PoolCalculation(add, simSenseOverdrive.getNameWithRating()));
			}
			attPool.add(new PoolCalculation(simRigRating, simrig.getNameWithRating()));
			//logger.info("Unarmed: "+vehicle.getName()+": Rigged "+attPool);
			dice = (int)attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setPool(dice);
			ret.setPool(attPool);
			logger.log(Level.INFO,"Unarmed(JUMPED ): "+vehicle.getNameWithoutRating()+"::  "+attPool);
			break;
		case AUTONOMOUS:
			arPool = new ArrayList<>(driverPilotPool);
			arPool.add(new PoolCalculation(sensor, SR6ItemAttribute.SENSORS.getName()));
			ar = (int) arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setAttackRating(ar);
			ret.setAttackRating(arPool);
//			logger.info("Unarmed: "+vehicle.getName()+": Autonom "+arPool);
			// The attempt to hit another being or vehicle is an Opposed test
			// — Piloting + Reaction vs.
			attPool = new ArrayList<>(driverPilotPool);
			attPool.add(new PoolCalculation(pilot, SR6ItemAttribute.PILOT.getName()));
			dice = (int) attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setPool(dice);
			ret.setPool(attPool);
			//logger.info("Unarmed(AUTONOM): "+vehicle.getName()+"::  "+attPool);
			break;
		case RCC:
			arPool = new ArrayList<>(driverPilotPool);
			arPool.add(new PoolCalculation(sensor, SR6ItemAttribute.SENSORS.getName()));
			ar = (int) arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			ret.setAttackRating(ar);
			ret.setAttackRating(arPool);
//			logger.info("Unarmed: "+vehicle.getName()+": RCC "+arPool);
			// The attempt to hit another being or vehicle is an Opposed test
			// — Piloting + Reaction vs.
			attPool = new ArrayList<>(driverPilotPool);
			attPool.add(new PoolCalculation(pilot, SR6ItemAttribute.PILOT.getName()));
			dice = (int) attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
			//logger.info("Unarmed(RCC    ): "+vehicle.getName()+"::  "+attPool);
			ret.setPool(dice);
			ret.setPool(attPool);
			break;
		}

		return ret;
	}

	//---------------------------------------------------------
	public static List<VehicleUnarmedAttack> getVehicleRamming(Shadowrun6Character model, CarriedItem vehicle) {
		List<VehicleUnarmedAttack> list = new ArrayList<>();
		list.add(getVehicleUnarmed(model, vehicle, VehicleOperationMode.DRIVING));
		list.add(getVehicleUnarmed(model, vehicle, VehicleOperationMode.AUTONOMOUS));
		VehicleUnarmedAttack rcc = getVehicleUnarmed(model, vehicle, VehicleOperationMode.RCC);
		if (rcc!=null)
			list.add(rcc);
		VehicleUnarmedAttack rigged = getVehicleUnarmed(model, vehicle, VehicleOperationMode.JUMPED_IN);
		if (rigged!=null)
			list.add(rigged);
		return list;
	}

	//---------------------------------------------------------
	public static List<CarriedItem<ItemTemplate>> getVehicleWeapons(Shadowrun6Character model, CarriedItem vehicle) {
		List<CarriedItem<ItemTemplate>> list = new ArrayList<>();
		logger.log(Level.WARNING, "TODO: getVehicleWeapons not implemented yet");
		// Ensure it is a drone or vehicle
//		if (!vehicle.getItem().isType(Arrays.asList(ItemType.droneTypes())) && !vehicle.getItem().isType(ItemType.VEHICLES))
//			return list;
//
//		vehicle.getEffectiveAccessories().forEach( ci -> {
//			// Add all mounted weapons
//			// Medium
//			AvailableSlot slot = ci.getSlot(ItemHook.VEHICLE_WEAPON);
//			if (slot!=null && !slot.getAllEmbeddedItems().isEmpty())
//				list.add(slot.getAllEmbeddedItems().get(0));
//			// Large
//			slot = ci.getSlot(ItemHook.VEHICLE_WEAPON_LARGE);
//			if (slot!=null && !slot.getAllEmbeddedItems().isEmpty())
//				list.add(slot.getAllEmbeddedItems().get(0));
//			// Small
//			slot = ci.getSlot(ItemHook.VEHICLE_WEAPON_SMALL);
//			if (slot!=null && !slot.getAllEmbeddedItems().isEmpty())
//				list.add(slot.getAllEmbeddedItems().get(0));
//
//			switch (ci.getUsedAsType()) {
//			case WEAPON_CLOSE_COMBAT:
//			case WEAPON_FIREARMS:
//			case WEAPON_RANGED:
//			case WEAPON_SPECIAL:
//				list.add(ci);
//				break;
//			}
//		});

		return list;
	}

	//--------------------------------------------------------------------
		/**
		 * Get the dice pools for using the weapon in a drone
		 * Rigged = Riggers skill with replaced physical attribute plus implanted
		 *          simrig rating
		 * RCC    =
		 */
		public static DronePool getDroneWeaponPool(Shadowrun6Character model, CarriedItem drone, CarriedItem rcc, CarriedItem weapon) {
			// Collect some necessary data
			logger.log(Level.WARNING, "TODO: getDroneWeaponPool not implemented yet");
//			CarriedItem rig = model.getItem("control_rig");
//			int rigRating = (rig==null)?0:rig.getRating();
//
			DronePool pool = new DronePool();
//			/*
//			 *  Rigged
//			 */
//			Skill skill = Shadowrun6Core.getSkill("engineering");
//			if (rig==null) {
//				pool.rigged = 0;
//			} else {
//				pool.rigged = Shadowrun6Tools.getSkillPool(model, skill, "gunnery");
//				if (Locale.GERMAN != Locale.getDefault()) {
//					int modifier = (model.getSkillValue(skill)!=null)?model.getSkillValue(skill).getModifier():0;
//					int maxBonusRemain = (4-modifier);
//					pool.rigged += Math.min(maxBonusRemain, rigRating);
//				}
//			}
//
//			/*
//			 * Driven (using Gunnery)
//			 */
//			pool.manual = Shadowrun6Tools.getSkillPool(model, skill, "gunnery");
//
//			/*
//			 * RCC
//			 * Requires targeting autosoft on RCC
//			 */
//			CarriedItem rccAutosoft = null;
//			if (rcc==null) {
//				pool.viaRCC = 0;
//			} else {
//				// Search matching autosoft
//	//			boolean anyTargeting = false;
//				CarriedItem autosoft = null;
//				for (CarriedItem item : rcc.getEffectiveAccessories()) {
//					if (!item.getItem().getId().equals("targeting"))
//						continue;
//	//				anyTargeting = true;
//					if (item.getChoiceReference()==null) {
//						logger.warn("Weapon model of targeting autosoft not defined in drone "+drone);
//					} else if (item.getChoiceReference().equals(weapon.getItem().getId()))
//						autosoft = item;
//				}
//				if (autosoft!=null) {
//					// Matching autosoft
//					rccAutosoft = autosoft;
//					pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//				} else {
//					// If the drone does not have the [Weapon] Targeting autosoft, then use Sensor rating – 1.
//					pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue()-1;
//				}
//			}
//			// Autonomous
//			// Search matching autosoft
//	//		boolean anyTargeting = false;
//			CarriedItem autosoft = null;
//			switch (weapon.getUsedAsSubType()) {
//			case BLADES:
//			case CLUBS:
//			case WHIPS:
//			case UNARMED:
//			case OTHER_CLOSE:
//				for (CarriedItem item : drone.getEffectiveAccessories()) {
//					if (!item.getItem().getId().equals("soft_close_combat"))
//						continue;
//					// anyTargeting = true;
//					if (item.getChoiceReference() == null) {
//						logger.warn("Weapon model of soft_close_combat autosoft not defined in drone " + drone);
//					} else if (item.getChoiceReference().equals(weapon.getItem().getId()))
//						autosoft = item;
//				}
//				break;
//			default:
//				for (CarriedItem item : drone.getEffectiveAccessories()) {
//					if (!item.getItem().getId().equals("targeting"))
//						continue;
//					// anyTargeting = true;
//					if (item.getChoiceReference() == null) {
//						logger.warn("Weapon model of targeting autosoft not defined in drone " + drone);
//					} else if (item.getChoiceReference().equals(weapon.getItem().getId()))
//						autosoft = item;
//				}
//			}
//			if (autosoft!=null) {
//				// Matching autosoft
//				pool.autonomous = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//			} else {
//				// If the drone does not have the [Weapon] Targeting autosoft, then use Sensor rating – 1.
//				pool.autonomous = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue()-1;
//			}
//			// German special rules: if the local autosoft is better, the
//			//   rcc controlled drone may use this instead
//			if (rccAutosoft!=null && autosoft!=null && autosoft.getRating()>rccAutosoft.getRating()) {
//				pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//			}

			return pool;
		}

		//-------------------------------------------------------------------
		public static int[] getDroneMonitorArray(CarriedItem item) {
			ItemAttributeNumericalValue val = item.getAsValue(SR6ItemAttribute.BODY);
			if (val==null)
				throw new IllegalArgumentException("Item does not have a BODY attribute");
			int add = 8 + Math.round(val.getModifiedValue()/2.0f);
			int[] ret = new int[add];

			int start = 0;
			int every = 3;

			for (int i=start; i<ret.length; i++) {
				ret[i] = - ((i+1-start)/every);
			}
			return ret;
		}

		//--------------------------------------------------------------------
		/**
		 * Determine the dice pool in different modes when using the drone.
		 * May be 0, if not possible
		 * @param model  Character
		 * @param drone  Drone/Vehicle
		 * @param action Action to perform
		 * @return Pool object or null, if not a drone
		 */
		public static DronePool getDronePool(Shadowrun6Character model, CarriedItem<ItemTemplate> drone, DroneAction action) {
			logger.log(Level.INFO, "....{0}....{1}",drone.getNameWithoutRating(),action);
			// Ensure it is a drone or vehicle
			if (!Shadowrun6Tools.isType(drone, ItemType.vehicleTypes())) {
				logger.log(Level.ERROR,"{0} is neither a vehicle, nor a drone", drone);
				return null;
			}

			// Collect some necessary data
			CarriedItem<ItemTemplate> ctrlRig = model.getCarriedItem("control_rig");
			int rigRating = (ctrlRig==null)?0:SR6GearTool.getRating( ctrlRig);
			CarriedItem<ItemTemplate> rcc = Shadowrun6Tools.getBestRCC(model);
			CarriedItem<ItemTemplate> simSenseOverdrive = model.getCarriedItem("simsense_overdrive");

			DronePool pool = new DronePool();
			SR6Skill pilotSkill = Shadowrun6Core.getSkill("piloting");
			SkillSpecialization<SR6Skill> spec = SR6GearTool.getSpecializationForVehicle(drone.getResolved());
			String specS = (spec!=null)?spec.getId():null;
			List<PoolCalculation<Integer>> pilotPool = Shadowrun6Tools.getSkillPool(model, pilotSkill, ShadowrunAttribute.REACTION, specS).getCalculation(ValueType.ARTIFICIAL);

			logger.log(Level.WARNING, "TODO: getDronePool not implemented yet");
			switch (action) {
			case EVADE:
				// Manually driven
				pool.manual = (int)pilotPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();

				// Assumption: Use Skill Pilot to evade when jumped in
				if (ctrlRig!=null) {
					// Use INTUITION instead REACTION (jumped in attribte)
					int modifier = (model.getSkillValue(pilotSkill)!=null)?model.getSkillValue(pilotSkill).getModifier():0;
					int maxRigBonus = Math.min( (4-modifier), rigRating);
					pool.rigged = Shadowrun6Tools.getSkillPool(model, pilotSkill, ShadowrunAttribute.INTUITION, (spec!=null)?spec.getId():null).getValue(ValueType.AUGMENTED) + maxRigBonus;
					if (simSenseOverdrive!=null) {
						int maxAdd = 4 - model.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
						pool.rigged += Math.min(maxAdd, SR6GearTool.getRating( simSenseOverdrive ));
					}
				} else {
					// Jumped in impossible without rig
					pool.rigged = 0;
				}
				// Via RCC: Requires evasion autosoft on rcc
				if (rcc==null) {
					pool.viaRCC = 0;
				} else {
					CarriedItem<ItemTemplate> autosoft = rcc.getEmbeddedItem("evasion");
					if (autosoft==null)
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue()-1;
					else
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue() + SR6GearTool.getRating( autosoft );
				}
				// Autonomous
				CarriedItem<ItemTemplate> autosoft = drone.getEmbeddedItem("evasion");
				if (autosoft==null)
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue()-1;
				else
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue() + SR6GearTool.getRating(autosoft);
				break;
			case PERCEPTION:
				// Manually driven
//				spec = Shadowrun6Tools.getSpecializationForVehicle(drone.getItem());
//				String specS = (spec!=null)?spec.getId():null;
				pilotPool = Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("perception"), ShadowrunAttribute.INTUITION).getCalculation(ValueType.ARTIFICIAL);
				pool.manual = (int)pilotPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();

				// Jumped in (dont make an assumption about specializations)
				if (ctrlRig!=null) {
					// Use INTUITION instead REACTION (jumped in attribte)
					pool.rigged = Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("perception"), (ShadowrunAttribute)null).getValue(ValueType.AUGMENTED);
					if (Locale.getDefault()!=Locale.GERMAN) {
						int modifier = (model.getSkillValue(Shadowrun6Core.getSkill("perception"))!=null)?model.getSkillValue(Shadowrun6Core.getSkill("perception")).getModifier():0;
						int cappedRigBonus = Math.min( (4-modifier), rigRating);
						pool.rigged += cappedRigBonus;
					}
					if (simSenseOverdrive!=null) {
						int maxAdd = 4 - model.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
						pool.rigged += Math.min(maxAdd, SR6GearTool.getRating( simSenseOverdrive ));
					}
				} else {
					// Jumped in impossible without rig
					pool.rigged = 0;
				}
				// Via RCC: Requires evasion autosoft on rcc
				if (rcc==null) {
					pool.viaRCC = 0;
				} else {
					autosoft = rcc.getEmbeddedItem("clearsight");
					if (autosoft==null)
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue()-1;
					else
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue() + SR6GearTool.getRating(autosoft);
				}
				// Autonomous
				autosoft = drone.getEmbeddedItem("clearsight");
				if (autosoft==null)
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue()-1;
				else
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue() + SR6GearTool.getRating(autosoft);

				break;
			case CRACKING:
				// Jumped in
				if (ctrlRig!=null) {
					pool.rigged = Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("cracking"), (ShadowrunAttribute)null, "electronic_warfare").getValue(ValueType.AUGMENTED) + rigRating;
				} else {
					// Jumped in impossible without rig
					pool.rigged = 0;
				}
				// Via RCC: Requires electronic warfare autosoft on rcc
				if (rcc==null) {
					pool.viaRCC = 0;
				} else {
					autosoft = rcc.getEmbeddedItem("electronic_warfare");
					if (autosoft==null)
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue()-1;
					else
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue() + SR6GearTool.getRating(autosoft);
				}
				// Autonomous
				autosoft = drone.getEmbeddedItem("electronic_warfare");
				if (autosoft==null)
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue()-1;
				else
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.SENSORS).getModifiedValue() + SR6GearTool.getRating(autosoft);
				break;
			case STEALTH:
				// Jumped in
				if (ctrlRig!=null) {
					String specID = (SR6GearTool.getSpecializationForVehicle(drone.getResolved())!=null)?SR6GearTool.getSpecializationForVehicle(drone.getResolved()).getId():null;
					pool.rigged = Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("stealth"), ShadowrunAttribute.LOGIC,specID).getValue(ValueType.AUGMENTED) + rigRating;
					// Question: does this include SimRig rating? German says "bei Proben, bei denen es um das Steuern eines Fahrzeugs geht"
					//    while US says: "all tests involving the operation of a vehicle"
					if (Locale.getDefault()!=Locale.GERMAN) {
						SR6SkillValue stealth = model.getSkillValue(Shadowrun6Core.getSkill("stealth"));
						int cappedRigBonus = (stealth==null)?rigRating:Math.min( (4-model.getSkillValue(Shadowrun6Core.getSkill("stealth")).getModifier()), rigRating);
						pool.rigged += cappedRigBonus;
					}
				} else {
					// Jumped in impossible without rig
					pool.rigged = 0;
				}
				// Via RCC: Requires stealth autosoft on rcc
				if (rcc==null) {
					pool.viaRCC = 0;
				} else {
					autosoft = rcc.getEmbeddedItem("stealth_auto");
					if (autosoft==null)
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue()-1;
					else
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue() + SR6GearTool.getRating(autosoft);
				}
				// Autonomous
				autosoft = drone.getEmbeddedItem("stealth_auto");
				if (autosoft==null)
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue()-1;
				else
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue() + SR6GearTool.getRating(autosoft);
				break;
			case PILOT:
				// Manually driven
				pool.manual = (int)pilotPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();

				// Jumped in
				if (ctrlRig!=null) {
					// Use INTUITION instead REACTION (jumped in attribte)
					int modifier = (model.getSkillValue(pilotSkill)!=null)?model.getSkillValue(pilotSkill).getModifier():0;
					int maxRigBonus = Math.min( (4-modifier), rigRating);
					pool.rigged = Shadowrun6Tools.getSkillPool(model, pilotSkill, ShadowrunAttribute.INTUITION, (spec!=null)?spec.getId():null).getValue(ValueType.AUGMENTED) + maxRigBonus;
					if (simSenseOverdrive!=null) {
						int maxAdd = 4 - model.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
						pool.rigged += Math.min(maxAdd, SR6GearTool.getRating( simSenseOverdrive ));
					}
				} else {
					// Jumped in impossible without rig
					pool.rigged = 0;
				}


				// Via RCC: Requires pilot maneuver on rcc
				if (rcc==null) {
					pool.viaRCC = 0;
				} else {
					autosoft = rcc.getEmbeddedItem("maneuvering");
					if (autosoft==null)
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue()-1;
					else
						pool.viaRCC = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue() + SR6GearTool.getRating(autosoft);
				}
				// Autonomous
				autosoft = drone.getEmbeddedItem("maneuvering");
				if (autosoft==null)
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue()-1;
				else
					pool.autonomous = drone.getAsValue(SR6ItemAttribute.PILOT).getModifiedValue() + SR6GearTool.getRating(autosoft);
				break;
			case DEFENSE_RATING:
				// "the Defense Rating for vehicles is Piloting of the driver + Armor"
				int armor = drone.getAsValue(SR6ItemAttribute.ARMOR).getModifiedValue();
				// Driven
				pool.manual = Shadowrun6Tools.getSkillPool(model, pilotSkill, (ShadowrunAttribute)null,"maneuvering").getValue(ValueType.AUGMENTED) + armor;
				// Jumped in
				if (ctrlRig!=null) {
					pool.rigged = Shadowrun6Tools.getSkillPool(model, pilotSkill, (ShadowrunAttribute)null, "maneuvering").getValue(ValueType.AUGMENTED) + armor;
					int modifier = (model.getSkillValue(pilotSkill)!=null)?model.getSkillValue(pilotSkill).getModifier():0;
					int cappedRigBonus = Math.min( (4-modifier), rigRating);
					pool.rigged += cappedRigBonus;
				} else {
					// Jumped in impossible without rig
					pool.rigged = 0;
				}
				// Via RCC: Requires electronic warfare autosoft on rcc
				if (rcc==null) {
					pool.viaRCC = 0;
				} else {
					autosoft = rcc.getEmbeddedItem("maneuvering");
					if (autosoft==null)
						pool.viaRCC = armor-1;
					else
						pool.viaRCC = armor + SR6GearTool.getRating(autosoft);
				}
				// Autonomous
				autosoft = drone.getEmbeddedItem("maneuvering");
				if (autosoft==null)
					pool.autonomous = armor-1;
				else
					pool.autonomous = armor + SR6GearTool.getRating(autosoft);
				break;
			default:
				logger.log(Level.WARNING, "DroneAction "+action+" not implemented yet");
			}

			return pool;
		}

		//--------------------------------------------------------------------
		/**
		 * Get the dice pools for using the weapon in a drone
		 * Rigged = Riggers skill with replaced physical attribute plus implanted
		 *          simrig rating
		 * RCC    =
		 */
		public static DronePool getDroneWeaponPool(Shadowrun6Character model, CarriedItem drone,  CarriedItem weapon) {
			return getDroneWeaponPool(model, drone, Shadowrun6Tools.getBestRCC(model), weapon);
		}

		//---------------------------------------------------------
		public static String getVehicleAccessoryString(CarriedItem<ItemTemplate> item) {
			logger.log(Level.WARNING, "TODO: getVehicleAccessoryString not implemented yet");
			System.err.println("VehicleTools.getVehicleAccessoryString not implemented yet");
			class Counted {
				CarriedItem<ItemTemplate> inst;
				int count;
				public Counted(CarriedItem<ItemTemplate> item) {
					inst = item;
					count=1;
				}
				public String toString() {
					if (count==1) return inst.getNameWithRating();
					return inst.getNameWithRating()+" ("+count+"x)";
				}
			}
			Map<ItemTemplate, Counted> map = new LinkedHashMap<>();
			List<String> list = new ArrayList<>();
			for (CarriedItem<ItemTemplate> ci : item.getEffectiveAccessories()) {
				ItemSubType sub = (ItemSubType)ci.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
//				if (ci!=null && ci.getUsedAsSubType()!=null)
//					sub = ci.getUsedAsSubType();
//				else {
//					if (ci.getUsedAsType()!=null && ci.getItem().getSubtype(ci.getUsedAsType())!=null) {
//						sub = ci.getItem().getSubtype(ci.getUsedAsType());
//					} else
//						logger.warn("No subtype set for "+ci+" / "+ci.getUsedAsType()+" / "+ci.getItem().getSubtype(ci.getUsedAsType()));
//				}
				switch (sub) {
				case HACKING_PROGRAM:
				case BASIC_PROGRAM:
				case RIGGER_PROGRAM:
				case AUTOSOFT:
				case SKILLSOFT:
					break;
				default:
					// Don't print hardpoints
					if (ci.getResolved().getId().startsWith("hardpoint"))
						continue;
					if (ci.getResolved().getId().startsWith("modslot_"))
						continue;
					if (ci.getResolved().getId().startsWith("improved_"))
						continue;
					if (ci.getResolved().getId().startsWith("enhanced_"))
						continue;
					if (ci.getResolved().getId().startsWith("weapon_mount"))
						continue;
					// Sum up
					if (map.containsKey(ci.getResolved())) {
						map.get(ci.getResolved()).count++;
					} else {
						map.put(ci.getResolved(), new Counted(ci));
					}
				}
			};
			map.values().forEach(c-> list.add(c.toString()));

			String mods = String.join(", ", list);
			return mods;
		}

		//-------------------------------------------------------------------
		public static SkillSpecialization getSpecializationForVehicle(ItemTemplate item) {
			SR6Skill pilot = Shadowrun6Core.getSkill("piloting");
			if (pilot==null)
				return null;
//			if (item.isNoSpecialization())
//				return null;

			ItemType typeI = item.getItemType(CarryMode.CARRIED);
			switch (typeI) {
			case VEHICLES:
				switch (item.getItemSubtype(CarryMode.CARRIED)) {
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
				VehicleType type = item.getTypeData(VehicleData.class).getType();
				if (type==null) {
					logger.log(Level.ERROR,"Cannot detect skill for drone without type");
					return null;
				}
				switch (type) {
				case GROUND:
					return pilot.getSpecialization("ground_craft") ;
				case AIR:
					return pilot.getSpecialization("aircraft") ;
				case WATER:
					return pilot.getSpecialization("watercraft") ;
				}
//				switch (item.getSubtype()) {
//				case BIKES:
//				case CARS:
//				case TRUCKS:
//					return Shadowrun6Core.getSkill("pilot_ground_craft");
//				case BOATS:
//				case SUBMARINES:
//					return Shadowrun6Core.getSkill("pilot_watercraft");
//				case FIXED_WING:
//				case ROTORCRAFT:
//				case VTOL:
//					return Shadowrun6Core.getSkill("pilot_aircraft");
//				default:
//				}

			default:
			}
			return null;
		}

}
