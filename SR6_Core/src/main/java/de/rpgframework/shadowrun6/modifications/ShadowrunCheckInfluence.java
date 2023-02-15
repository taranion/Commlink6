package de.rpgframework.shadowrun6.modifications;

import de.rpgframework.genericrpg.data.CheckInfluence;

/**
 * @author prelle
 *
 */
public enum ShadowrunCheckInfluence implements CheckInfluence {

	/** Bonus added to result roll */
	HIT,
	DICE,
	REDUCE_THRESHOLD,
	/** Edge gained by a test */
	EDGE,
	/** Edge gained only for use in that test */
	EDGE_ONLY_TEST,
	/** Can test be made at all */
	USAGE,
	EDGE_COST_MALUS,
	/** Value refers to cost of Edge boosts for checks */
	EDGE_BOOST,
	/** Replace one die with a wild die */
	REPLACE_WITH_WILD,
	/** Cannot earn edge in that test */
	NOT_EARN_EDGE,
	NOT_SPEND_EDGE,
	OTHER

}
