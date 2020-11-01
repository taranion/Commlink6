/**
 *
 */
package org.prelle.shadowrun6;

import java.util.MissingResourceException;

/**
 * @author prelle
 *
 */
public enum SR6Attribute implements de.rpgframework.genericrpg.data.IAttribute {

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

    //-------------------------------------------------------------------
    public String getName() {
        try {
			return ShadowrunCore.getI18nResources().getString("attribute."+this.name().toLowerCase());
		} catch (MissingResourceException e) {
			System.err.println("Missing "+e.getKey()+" in "+ShadowrunCore.getI18nResources().getBaseBundleName());
			return e.getKey();
		}
    }

	//-------------------------------------------------------------------
	public static SR6Attribute[] primaryValues() {
		return new SR6Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] derivedValues() {
		return new SR6Attribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_ASTRAL, DEFENSIVE_POOL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY,
				};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] secondaryValues() {
		return new SR6Attribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_ASTRAL, DEFENSIVE_POOL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY, 
				ATTACK, SLEAZE, DATA_PROCESSING, FIREWALL, MELEE_DAMAGE, PHYSICAL_MONITOR, STUN_MONITOR, DEFENSE_RATING};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] specialAttributes() {
		return new SR6Attribute[]{EDGE,MAGIC,RESONANCE, POWER_POINTS, HEAT, REPUTATION};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] primaryAndSpecialValues() {
		return new SR6Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] physicalValues() {
		return new SR6Attribute[]{BODY,AGILITY,REACTION,STRENGTH};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] attributesToSave() {
		return new SR6Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE, POWER_POINTS};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] primaryTableValues() {
		return new SR6Attribute[]{BODY,AGILITY,REACTION,STRENGTH, WILLPOWER,LOGIC,INTUITION,CHARISMA,EDGE,MAGIC,RESONANCE};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] derivedTableValues() {
		return new SR6Attribute[]{INITIATIVE_PHYSICAL, INITIATIVE_MATRIX, INITIATIVE_MATRIX_VR_COLD, INITIATIVE_MATRIX_VR_HOT, INITIATIVE_ASTRAL, MINOR_ACTION, MINOR_ACTION_MATRIX, MINOR_ACTION_ASTRAL, DEFENSIVE_POOL, COMPOSURE, JUDGE_INTENTIONS, MEMORY, LIFT_CARRY};
	}

	//-------------------------------------------------------------------
	public static SR6Attribute[] matrixValues() {
		return new SR6Attribute[]{FIREWALL, SLEAZE, DATA_PROCESSING, ATTACK};
	}

	//-------------------------------------------------------------------
	public boolean isPrimary() {
		for (SR6Attribute key : primaryValues())
			if (this==key) return true;
		return false;
	}

	//-------------------------------------------------------------------
	public boolean isSpecial() {
		for (SR6Attribute key : specialAttributes())
			if (this==key) return true;
		return false;
	}

	//-------------------------------------------------------------------
	public boolean isPhysical() {
		for (SR6Attribute key : physicalValues())
			if (this==key) return true;
		return false;
	}

}
