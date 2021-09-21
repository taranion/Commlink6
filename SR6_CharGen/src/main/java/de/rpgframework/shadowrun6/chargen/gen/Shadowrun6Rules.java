package de.rpgframework.shadowrun6.chargen.gen;

import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.shadowrun.chargen.charctrl.ShadowrunRules;

/**
 * @author stefa
 *
 */
public interface Shadowrun6Rules extends ShadowrunRules {

	public static Rule CHARGEN_ADJUSTMENT_ON_LOWERED_MAX = new Rule("CHARGEN_ADJUSTMENT_ON_LOWERED_MAX", Rule.Type.BOOLEAN);

}
