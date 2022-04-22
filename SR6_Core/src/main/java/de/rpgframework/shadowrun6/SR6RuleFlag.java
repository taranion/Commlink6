package de.rpgframework.shadowrun6;

import de.rpgframework.genericrpg.data.RuleFlag;

/**
 * Flags how to handle specific rules in a character. They originate from
 * qualities, adept powers and other elements that change how rules apply
 * to a character.
 * This flags are not preconfigured, but injected by modifications.
 * 
 * @author prelle
 *
 */
public enum SR6RuleFlag implements RuleFlag {

	UNARMED_DAMAGE_IS_PHYSICAL,
	MENTOR_SPIRIT_BOTH_ADVANTAGES,
	
}
