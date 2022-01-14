package de.rpgframework.shadowrun6.modifications;

import de.rpgframework.genericrpg.data.CheckInfluence;

/**
 * @author prelle
 *
 */
public enum ShadowrunCheckInfluence implements CheckInfluence {

	/** Bonus added to result roll */
	BONUS,
	/** Egde gained by a test */
	EDGE,
	/** Can test be made at all */
	USAGE,
	EDGE_COST_MALUS
	
}
