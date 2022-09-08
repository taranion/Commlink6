package de.rpgframework.shadowrun6.vehicle;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.shadowrun.ShadowrunCore;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class VehicleTools {

	private final static Logger logger = System.getLogger("shadowrun6");

	//---------------------------------------------------------
	public static VehicleUnarmedAttack getVehicleUnarmed(Shadowrun6Character model, CarriedItem vehicle, VehicleOperationMode mode) {
		logger.log(Level.WARNING, "TODO: getVehicleUnarmed not implemented yet");
		// Collect some necessary data
//		CarriedItem simrig = model.getItem("control_rig");
//		int simRigRating = (simrig==null)?0:simrig.getRating();
//		CarriedItem rcc = Shadowrun6Tools.getBestRCC(model);
//		CarriedItem simSenseOverdrive = model.getItem("simsense_overdrive");
//		
//		Skill pilotSkill = Shadowrun6Core.getSkill("piloting");
//		String spec = (Shadowrun6Tools.getSpecializationForVehicle(vehicle.getItem())!=null)?Shadowrun6Tools.getSpecializationForVehicle(vehicle.getItem()).getId():null;
//		List<Shadowrun6Tools.PoolCalculation> driverPilotPool = Shadowrun6Tools.getSkillPoolCalculationWithoutAttribute(model, pilotSkill, spec);
//		if (mode==VehicleOperationMode.AUTONOMOUS) {
//			CarriedItem autosoft = vehicle.getEmbeddedItem("maneuvering");
//			driverPilotPool.clear();
//			if (autosoft == null)
//				driverPilotPool.add(new Shadowrun6Tools.PoolCalculation(-1, ShadowrunCore.getItem("maneuvering").getName()));
//			else
//				driverPilotPool.add(new Shadowrun6Tools.PoolCalculation(autosoft.getRating(), autosoft.getNameWithRating()));
//		} else if (mode==VehicleOperationMode.RCC) {
//			if (rcc==null) return null;
//			CarriedItem autosoft = rcc.getEmbeddedItem("maneuvering");
//			driverPilotPool.clear();
//			if (autosoft == null)
//				driverPilotPool.add(new Shadowrun6Tools.PoolCalculation(-1, ShadowrunCore.getItem("maneuvering").getName()));
//			else
//				driverPilotPool.add(new Shadowrun6Tools.PoolCalculation(autosoft.getRating(), autosoft.getNameWithRating()));			
//		}
//		
//		int sensor = vehicle.getAsValue(ItemAttribute.SENSORS).getModifiedValue();
//		int body   = vehicle.getAsValue(ItemAttribute.BODY).getModifiedValue();
//		int armor  = vehicle.getAsValue(ItemAttribute.ARMOR).getModifiedValue();
//		int pilot  = vehicle.getAsValue(ItemAttribute.PILOT).getModifiedValue();
//		
//		VehicleUnarmedAttack ret = new VehicleUnarmedAttack(mode);
//		// Damage is (Body / 2, rounded up, +1 for each current Speed Interval)
//		// Physical damage
//		int dmg= Math.round(body/2.0f);
//		ret.setDamage(new Damage(dmg, false, Damage.Type.PHYSICAL, WeaponDamageType.NORMAL));
//		
//		switch (mode) {
//		case DRIVING:
//			// The Attack Rating of a vehicle is Piloting of the driver + Sensor.
//			List<Shadowrun6Tools.PoolCalculation> arPool = new ArrayList<>(driverPilotPool);
//			arPool.add(new Shadowrun6Tools.PoolCalculation(sensor, ItemAttribute.SENSORS.getName()));
//			int ar = (int)arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setAttackRating(ar);
//			ret.setAttackRating(arPool);
////			logger.info("Unarmed: "+vehicle.getName()+": AR   driven "+arPool);
//			// The attempt to hit another being or vehicle is an Opposed test
//			// — Piloting + Reaction vs. 
//			List<Shadowrun6Tools.PoolCalculation> attPool = new ArrayList<>(driverPilotPool);
//			attPool.addAll(Shadowrun6Tools.getAttributePoolCalculation(model, Attribute.REACTION));
//			//logger.debug("Unarmed(MANUAL  ): "+vehicle.getName()+"::  "+attPool);
//			int dice = (int)attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setPool(dice);
//			ret.setPool(attPool);
//			return ret;
//		case JUMPED_IN:
//			// The Attack Rating of a vehicle is Piloting of the driver + Sensor.
//			// Assume rating of simrig is added
//			if (simrig==null)
//				return null;
//			arPool = new ArrayList<>(driverPilotPool);
//			arPool.add(new Shadowrun6Tools.PoolCalculation(sensor, ItemAttribute.SENSORS.getName()));
//			arPool.add(new Shadowrun6Tools.PoolCalculation(simRigRating, simrig.getNameWithRating()));
//			ar = (int)arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setAttackRating(ar);
//			ret.setAttackRating(arPool);
////			logger.info("Unarmed: "+vehicle.getName()+": Rigged "+arPool);
//			// The attempt to hit another being or vehicle is an Opposed test
//			// — Piloting + Reaction vs. 
//			attPool = new ArrayList<>(driverPilotPool);
//			attPool.addAll(Shadowrun6Tools.getAttributePoolCalculation(model, Attribute.INTUITION));
//			if (simSenseOverdrive!=null) {
//				int maxAdd = 4 - model.getAttribute(Attribute.INTUITION).getModifier();
//				int add  = Math.min(maxAdd, simSenseOverdrive.getRating());
//				attPool.add(new PoolCalculation(add, simSenseOverdrive.getNameWithRating()));
//			}
//			attPool.add(new Shadowrun6Tools.PoolCalculation(simRigRating, simrig.getNameWithRating()));
//			//logger.info("Unarmed: "+vehicle.getName()+": Rigged "+attPool);
//			dice = (int)attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setPool(dice);
//			ret.setPool(attPool);
//			logger.info("Unarmed(JUMPED ): "+vehicle.getName()+"::  "+attPool);
//			return ret;
//		case AUTONOMOUS:
//			arPool = new ArrayList<>(driverPilotPool);
//			arPool.add(new Shadowrun6Tools.PoolCalculation(sensor, ItemAttribute.SENSORS.getName()));
//			ar = (int) arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setAttackRating(ar);
//			ret.setAttackRating(arPool);
////			logger.info("Unarmed: "+vehicle.getName()+": Autonom "+arPool);
//			// The attempt to hit another being or vehicle is an Opposed test
//			// — Piloting + Reaction vs.
//			attPool = new ArrayList<>(driverPilotPool);
//			attPool.add(new Shadowrun6Tools.PoolCalculation(pilot, ItemAttribute.PILOT.getName()));
//			dice = (int) attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setPool(dice);
//			ret.setPool(attPool);
//			//logger.info("Unarmed(AUTONOM): "+vehicle.getName()+"::  "+attPool);
//			return ret;
//		case RCC:
//			arPool = new ArrayList<>(driverPilotPool);
//			arPool.add(new Shadowrun6Tools.PoolCalculation(sensor, ItemAttribute.SENSORS.getName()));
//			ar = (int) arPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			ret.setAttackRating(ar);
//			ret.setAttackRating(arPool);
////			logger.info("Unarmed: "+vehicle.getName()+": RCC "+arPool);
//			// The attempt to hit another being or vehicle is an Opposed test
//			// — Piloting + Reaction vs.
//			attPool = new ArrayList<>(driverPilotPool);
//			attPool.add(new Shadowrun6Tools.PoolCalculation(pilot, ItemAttribute.PILOT.getName()));
//			dice = (int) attPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//			//logger.info("Unarmed(RCC    ): "+vehicle.getName()+"::  "+attPool);
//			ret.setPool(dice);
//			ret.setPool(attPool);
//			return ret;
//		}
		
		return null;
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
	public static List<CarriedItem> getVehicleWeapons(Shadowrun6Character model, CarriedItem vehicle) {
		List<CarriedItem> list = new ArrayList<>();
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
//			Skill skill = ShadowrunCore.getSkill("engineering");
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
		public static DronePool getDronePool(Shadowrun6Character model, CarriedItem drone, DroneAction action) {
			logger.log(Level.WARNING, "TODO: getDronePool not implemented yet");
//			logger.info("...."+drone.getName()+"..."+action);
			// Ensure it is a drone or vehicle
//			if (!drone.getItem().isType(Arrays.asList(ItemType.droneTypes())) && !drone.getItem().isType(ItemType.VEHICLES) && !drone.getItem().isType(ItemType.DRONES)) {
//				logger.error(drone+" is neither a vehicle, nor a drone");
//				return null;
//			}
//			
//			// Collect some necessary data
//			CarriedItem ctrlRig = model.getItem("control_rig");
//			int rigRating = (ctrlRig==null)?0:ctrlRig.getRating();
//			CarriedItem rcc = Shadowrun6Tools.getBestRCC(model);
//			CarriedItem simSenseOverdrive = model.getItem("simsense_overdrive");
			
			DronePool pool = new DronePool();
//			Skill pilotSkill = ShadowrunCore.getSkill("piloting");
//			SkillSpecialization spec = Shadowrun6Tools.getSpecializationForVehicle(drone.getItem());
//			String specS = (spec!=null)?spec.getId():null;
//			List<Shadowrun6Tools.PoolCalculation> pilotPool = Shadowrun6Tools.getSkillPoolCalculation(model, pilotSkill, Attribute.REACTION, specS);
//
//			switch (action) {
//			case EVADE:
//				// Manually driven
//				pool.manual = (int)pilotPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//
//				// Assumption: Use Skill Pilot to evade when jumped in
//				if (ctrlRig!=null) {
//					// Use INTUITION instead REACTION (jumped in attribte)
//					int modifier = (model.getSkillValue(pilotSkill)!=null)?model.getSkillValue(pilotSkill).getModifier():0;
//					int maxRigBonus = Math.min( (4-modifier), rigRating);
//					pool.rigged = Shadowrun6Tools.getSkillPool(model, pilotSkill, Attribute.INTUITION, (spec!=null)?spec.getId():null) + maxRigBonus;
//					if (simSenseOverdrive!=null) {
//						int maxAdd = 4 - model.getAttribute(Attribute.INTUITION).getModifier();
//						pool.rigged += Math.min(maxAdd, simSenseOverdrive.getRating());
//					}
//				} else {
//					// Jumped in impossible without rig
//					pool.rigged = 0;
//				}
//				// Via RCC: Requires evasion autosoft on rcc
//				if (rcc==null) {
//					pool.viaRCC = 0;
//				} else {
//					CarriedItem autosoft = rcc.getEmbeddedItem("evasion");
//					if (autosoft==null)
//						pool.viaRCC = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue()-1;
//					else
//						pool.viaRCC = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue() + autosoft.getRating();
//				}
//				// Autonomous
//				CarriedItem autosoft = drone.getEmbeddedItem("evasion");
//				if (autosoft==null)
//					pool.autonomous = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue()-1;
//				else
//					pool.autonomous = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue() + autosoft.getRating();
//				break;
//			case PERCEPTION:
//				// Manually driven
////				spec = Shadowrun6Tools.getSpecializationForVehicle(drone.getItem());
////				String specS = (spec!=null)?spec.getId():null;
//				pilotPool = Shadowrun6Tools.getSkillPoolCalculation(model, ShadowrunCore.getSkill("perception"), Attribute.INTUITION);
//				pool.manual = (int)pilotPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//
//				// Jumped in (dont make an assumption about specializations)
//				if (ctrlRig!=null) {
//					// Use INTUITION instead REACTION (jumped in attribte)
//					pool.rigged = Shadowrun6Tools.getSkillPool(model, ShadowrunCore.getSkill("perception"), (String)null);
//					if (Locale.getDefault()!=Locale.GERMAN) {
//						int modifier = (model.getSkillValue(ShadowrunCore.getSkill("perception"))!=null)?model.getSkillValue(ShadowrunCore.getSkill("perception")).getModifier():0;
//						int cappedRigBonus = Math.min( (4-modifier), rigRating);
//						pool.rigged += cappedRigBonus;
//					}
//					if (simSenseOverdrive!=null) {
//						int maxAdd = 4 - model.getAttribute(Attribute.INTUITION).getModifier();
//						pool.rigged += Math.min(maxAdd, simSenseOverdrive.getRating());
//					}
//				} else {
//					// Jumped in impossible without rig
//					pool.rigged = 0;
//				}
//				// Via RCC: Requires evasion autosoft on rcc
//				if (rcc==null) {
//					pool.viaRCC = 0;
//				} else {
//					autosoft = rcc.getEmbeddedItem("clearsight");
//					if (autosoft==null)
//						pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue()-1;
//					else
//						pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//				}
//				// Autonomous
//				autosoft = drone.getEmbeddedItem("clearsight");
//				if (autosoft==null)
//					pool.autonomous = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue()-1;
//				else
//					pool.autonomous = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//
//				break;
//			case CRACKING:
//				// Jumped in
//				if (ctrlRig!=null) {
//					pool.rigged = Shadowrun6Tools.getSkillPool(model, ShadowrunCore.getSkill("cracking"), "electronic_warfare") + rigRating;
//				} else {
//					// Jumped in impossible without rig
//					pool.rigged = 0;
//				}
//				// Via RCC: Requires electronic warfare autosoft on rcc
//				if (rcc==null) {
//					pool.viaRCC = 0;
//				} else {
//					autosoft = rcc.getEmbeddedItem("electronic_warfare");
//					if (autosoft==null)
//						pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue()-1;
//					else
//						pool.viaRCC = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//				}
//				// Autonomous
//				autosoft = drone.getEmbeddedItem("electronic_warfare");
//				if (autosoft==null)
//					pool.autonomous = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue()-1;
//				else
//					pool.autonomous = drone.getAsValue(ItemAttribute.SENSORS).getModifiedValue() + autosoft.getRating();
//				break;
//			case STEALTH:
//				// Jumped in
//				if (ctrlRig!=null) {
//					String specID = (Shadowrun6Tools.getSpecializationForVehicle(drone.getItem())!=null)?Shadowrun6Tools.getSpecializationForVehicle(drone.getItem()).getId():null;
//					pool.rigged = Shadowrun6Tools.getSkillPool(model, ShadowrunCore.getSkill("stealth"), Attribute.LOGIC,specID) + rigRating;
//					// Question: does this include SimRig rating? German says "bei Proben, bei denen es um das Steuern eines Fahrzeugs geht"
//					//    while US says: "all tests involving the operation of a vehicle"
//					if (Locale.getDefault()!=Locale.GERMAN) {
//						SkillValue stealth = model.getSkillValue(ShadowrunCore.getSkill("stealth"));
//						int cappedRigBonus = (stealth==null)?rigRating:Math.min( (4-model.getSkillValue(ShadowrunCore.getSkill("stealth")).getModifier()), rigRating);
//						pool.rigged += cappedRigBonus;
//					}
//				} else {
//					// Jumped in impossible without rig
//					pool.rigged = 0;
//				}
//				// Via RCC: Requires stealth autosoft on rcc
//				if (rcc==null) {
//					pool.viaRCC = 0;
//				} else {
//					autosoft = rcc.getEmbeddedItem("stealth_auto");
//					if (autosoft==null)
//						pool.viaRCC = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue()-1;
//					else
//						pool.viaRCC = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue() + autosoft.getRating();
//				}
//				// Autonomous
//				autosoft = drone.getEmbeddedItem("stealth_auto");
//				if (autosoft==null)
//					pool.autonomous = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue()-1;
//				else
//					pool.autonomous = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue() + autosoft.getRating();
//				break;
//			case PILOT:
//				// Manually driven
//				pool.manual = (int)pilotPool.stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
//
//				// Jumped in
//				if (ctrlRig!=null) {
//					// Use INTUITION instead REACTION (jumped in attribte)
//					int modifier = (model.getSkillValue(pilotSkill)!=null)?model.getSkillValue(pilotSkill).getModifier():0;
//					int maxRigBonus = Math.min( (4-modifier), rigRating);
//					pool.rigged = Shadowrun6Tools.getSkillPool(model, pilotSkill, Attribute.INTUITION, (spec!=null)?spec.getId():null) + maxRigBonus;
//					if (simSenseOverdrive!=null) {
//						int maxAdd = 4 - model.getAttribute(Attribute.INTUITION).getModifier();
//						pool.rigged += Math.min(maxAdd, simSenseOverdrive.getRating());
//					}
//				} else {
//					// Jumped in impossible without rig
//					pool.rigged = 0;
//				}
//
//				
//				// Via RCC: Requires pilot maneuver on rcc
//				if (rcc==null) {
//					pool.viaRCC = 0;
//				} else {
//					autosoft = rcc.getEmbeddedItem("maneuvering");
//					if (autosoft==null)
//						pool.viaRCC = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue()-1;
//					else
//						pool.viaRCC = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue() + autosoft.getRating();
//				}
//				// Autonomous
//				autosoft = drone.getEmbeddedItem("maneuvering");
//				if (autosoft==null)
//					pool.autonomous = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue()-1;
//				else
//					pool.autonomous = drone.getAsValue(ItemAttribute.PILOT).getModifiedValue() + autosoft.getRating();
//				break;
//			case DEFENSE_RATING:
//				// "the Defense Rating for vehicles is Piloting of the driver + Armor"
//				int armor = drone.getAsValue(ItemAttribute.ARMOR).getModifiedValue();
//				// Driven
//				pool.manual = Shadowrun6Tools.getSkillPoolWithoutAttribute(model, pilotSkill, "maneuvering") + armor;
//				// Jumped in
//				if (ctrlRig!=null) {
//					pool.rigged = Shadowrun6Tools.getSkillPoolWithoutAttribute(model, pilotSkill, "maneuvering") + armor;
//					int modifier = (model.getSkillValue(pilotSkill)!=null)?model.getSkillValue(pilotSkill).getModifier():0;
//					int cappedRigBonus = Math.min( (4-modifier), rigRating);
//					pool.rigged += cappedRigBonus;
//				} else {
//					// Jumped in impossible without rig
//					pool.rigged = 0;
//				}
//				// Via RCC: Requires electronic warfare autosoft on rcc
//				if (rcc==null) {
//					pool.viaRCC = 0;
//				} else {
//					autosoft = rcc.getEmbeddedItem("maneuvering");
//					if (autosoft==null)
//						pool.viaRCC = armor-1;
//					else
//						pool.viaRCC = armor + autosoft.getRating();
//				}
//				// Autonomous
//				autosoft = drone.getEmbeddedItem("maneuvering");
//				if (autosoft==null)
//					pool.autonomous = armor-1;
//				else
//					pool.autonomous = armor + autosoft.getRating();
//				break;
//			default:
//				logger.warn("DroneAction "+action+" not implemented yet");
//			}
			
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
		public static String getVehicleAccessoryString(CarriedItem item) {
			logger.log(Level.WARNING, "TODO: getVehicleAccessoryString not implemented yet");
			class Counted {
				CarriedItem inst;
				int count;
				public Counted(CarriedItem item) {
					inst = item;
					count=1;
				}
				public String toString() {
					//if (count==1) return inst.getNameWithCount();
					return inst.getNameWithRating()+" ("+count+"x)";
				}
			}
			Map<ItemTemplate, Counted> map = new LinkedHashMap<>();
			List<String> list = new ArrayList<>();
//			item.getEffectiveAccessories().forEach( ci -> {
//				ItemSubType sub = ItemSubType.ACCESSORY;
//				if (ci!=null && ci.getUsedAsSubType()!=null)
//					sub = ci.getUsedAsSubType();
//				else {
//					if (ci.getUsedAsType()!=null && ci.getItem().getSubtype(ci.getUsedAsType())!=null) {
//						sub = ci.getItem().getSubtype(ci.getUsedAsType());
//					} else
//						logger.warn("No subtype set for "+ci+" / "+ci.getUsedAsType()+" / "+ci.getItem().getSubtype(ci.getUsedAsType()));
//				}
//				switch (sub) {
//				case HACKING_PROGRAM:
//				case BASIC_PROGRAM:
//				case RIGGER_PROGRAM:
//				case AUTOSOFT:
//				case SKILLSOFT:
//					break;
//				default:
//					// Don't print hardpoints
//					if (ci.getItem().getId().startsWith("hardpoint"))
//						return;
//					if (ci.getItem().getId().startsWith("modslot_"))
//						return;
//					if (ci.getItem().getId().startsWith("improved_"))
//						return;
//					if (ci.getItem().getId().startsWith("enhanced_"))
//						return;
//					if (ci.getItem().getId().startsWith("weapon_mount"))
//						return;
//					// Sum up
//					if (map.containsKey(ci.getItem())) {
//						map.get(ci.getItem()).count++;
//					} else {
//						map.put(ci.getItem(), new Counted(ci));
//					}
//				}
//			});
			map.values().forEach(c-> list.add(c.toString()));
			
			String mods = String.join(", ", list);
			return mods;
		}
	
}
