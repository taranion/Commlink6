/**
 *
 */
package org.prelle.shadowrun6;

import java.util.MissingResourceException;

import de.rpgframework.ResourceI18N;

/**
 * @author prelle
 *
 */
public enum Attribute {

	BODY,
	AGILITY,
	REACTION,
	STRENGTH,
	WILLPOWER,
	LOGIC,
	INTUITION,
	CHARISMA,
	EDGE,
	MAGIC,
	RESONANCE,
	ESSENCE,

	INITIATIVE_PHYSICAL,
	INITIATIVE_MATRIX,
	INITIATIVE_MATRIX_VR_COLD,
	INITIATIVE_MATRIX_VR_HOT,
	INITIATIVE_ASTRAL,
	INITIATIVE_DICE_PHYSICAL,
	INITIATIVE_DICE_MATRIX,
	INITIATIVE_DICE_MATRIX_VR_COLD,
	INITIATIVE_DICE_MATRIX_VR_HOT,
	INITIATIVE_DICE_ASTRAL,
	MINOR_ACTION,
	MINOR_ACTION_ASTRAL,
	MINOR_ACTION_MATRIX,

	COMPOSURE,
	JUDGE_INTENTIONS,
	MEMORY,
	LIFT_CARRY,
	DEFENSIVE_POOL,
	DEFENSIVE_POOL_COMBAT_DIRECT,
	DEFENSIVE_POOL_COMBAT_INDIRECT,
	DEFENSIVE_POOL_RESIST_TOXIN_DAMAGE,
	DEFENSIVE_POOL_DRAIN,

	HEAT,
	REPUTATION,

	// Matrix attributes
	ATTACK,
	SLEAZE,
	DATA_PROCESSING,
	FIREWALL,
	
	//Combat
	ATTACK_RATING,
//	ARMOR,
	MELEE_DAMAGE,
	DAMAGE_RESISTANCE,
	DEFENSE_RATING,
	PHYSICAL_MONITOR,
	STUN_MONITOR,
	DAMAGE_OVERFLOW,
	HEALING,
	TOXIN_RESISTANCE_POOL,
	TOXIN_RESISTANCE_THRESHOLD,
	DRAIN_RESISTANCE,
	POWER_POINTS,
	
	/**
	 * From High Pain Tolerance
	 */
	PHYSICAL_WOUND_MOFIFIER,
	;

//	//-------------------------------------------------------------------
//	public String getShortName() {
//		return ResourceI18N.get(ShadowrunCore.getI18nResources(),"attribute."+this.name().toLowerCase()+".short");
//	}
//
//    //-------------------------------------------------------------------
//    public String getName() {
//        try {
//			return ShadowrunCore.getI18nResources().getString("attribute."+this.name().toLowerCase());
//		} catch (MissingResourceException e) {
//			System.err.println("Missing "+e.getKey()+" in "+ShadowrunCore.getI18nResources().getBaseBundleName());
//			return e.getKey();
//		}
//    }

	//-------------------------------------------------------------------
	public static Attribute[] primaryValues() {
		return new Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA};
	}

	//-------------------------------------------------------------------
	public static Attribute[] derivedValues() {
		return new Attribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_ASTRAL, DEFENSIVE_POOL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY,
				};
	}

	//-------------------------------------------------------------------
	public static Attribute[] secondaryValues() {
		return new Attribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_ASTRAL, DEFENSIVE_POOL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY, 
				ATTACK, SLEAZE, DATA_PROCESSING, FIREWALL, MELEE_DAMAGE, PHYSICAL_MONITOR, STUN_MONITOR, DEFENSE_RATING};
	}

	//-------------------------------------------------------------------
	public static Attribute[] specialAttributes() {
		return new Attribute[]{EDGE,MAGIC,RESONANCE, POWER_POINTS, HEAT, REPUTATION};
	}

	//-------------------------------------------------------------------
	public static Attribute[] primaryAndSpecialValues() {
		return new Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE};
	}

	//-------------------------------------------------------------------
	public static Attribute[] physicalValues() {
		return new Attribute[]{BODY,AGILITY,REACTION,STRENGTH};
	}

	//-------------------------------------------------------------------
	public static Attribute[] attributesToSave() {
		return new Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE, POWER_POINTS};
	}

	//-------------------------------------------------------------------
	public static Attribute[] primaryTableValues() {
		return new Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE};
	}

	//-------------------------------------------------------------------
	public static Attribute[] derivedTableValues() {
		return new Attribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_MATRIX_VR_COLD, INITIATIVE_MATRIX_VR_HOT, INITIATIVE_ASTRAL, MINOR_ACTION, MINOR_ACTION_MATRIX, MINOR_ACTION_ASTRAL, DEFENSIVE_POOL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY};
	}

	//-------------------------------------------------------------------
	public static Attribute[] matrixValues() {
		return new Attribute[]{FIREWALL, SLEAZE, DATA_PROCESSING, ATTACK};
	}

	//-------------------------------------------------------------------
	public boolean isPrimary() {
		for (Attribute key : primaryValues())
			if (this==key) return true;
		return false;
	}

	//-------------------------------------------------------------------
	public boolean isSpecial() {
		for (Attribute key : specialAttributes())
			if (this==key) return true;
		return false;
	}

	//-------------------------------------------------------------------
	public boolean isPhysical() {
		for (Attribute key : physicalValues())
			if (this==key) return true;
		return false;
	}

}
