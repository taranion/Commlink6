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
	ADD_2_TWO_LOWEST_CYBERJACK_ATTRIBUTES,
	/** Discount of 2 Karma for martial arts or technique */
	MARTIAL_ARTS_PRODIGY,
	/** 
	 * You may learn new language skills at a cost of 2 Karma per rank. 
	 * You may improve language skills for only 1 Karma per rank. 
	 * You may improve language skills up to rank 4 (native). 
	 */
	POLYGLOT,
	MAX_3_CONTACT_LOYALTY,
	DECK_5000_PER_KARMA
}
