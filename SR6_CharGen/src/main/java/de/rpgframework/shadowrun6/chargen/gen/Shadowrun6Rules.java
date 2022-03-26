package de.rpgframework.shadowrun6.chargen.gen;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.shadowrun.chargen.charctrl.ShadowrunRules;

/**
 * @author stefa
 *
 */
public interface Shadowrun6Rules extends ShadowrunRules {

	static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(Shadowrun6Rules.class,
			Locale.ENGLISH, Locale.GERMAN);

	public static enum PRIORITY_MAGIC {
		/** Only use magic rating from priority */
		PRIO_MAGIC,
		/** Use the priority magic plus any points added by Karma */
		MAGIC_PLUS_KARMA,
		/** Use the magic rating with any adjustments */
		FINAL_MAGIC,
	}
	
	public static Rule CHARGEN_ADJUSTMENT_ON_LOWERED_MAX = new Rule("CHARGEN_ADJUSTMENT_ON_LOWERED_MAX", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_PRIO_ADEPT_PP = new Rule("CHARGEN_PRIO_ADEPT_PP", PRIORITY_MAGIC.class, RES, PRIORITY_MAGIC.FINAL_MAGIC);

	
	//-------------------------------------------------------------------
	public static Rule[] values() {
		return new Rule[] {
				CHARGEN_ALLOW_INITIATION,
				CHARGEN_MAX_KARMA_REMAIN,
				CHARGEN_MAX_NUYEN_REMAIN,
				CHARGEN_ADJUSTMENT_ON_LOWERED_MAX,
				CAREER_PAY_GEAR
		};
	}
	
	//-------------------------------------------------------------------
	static Rule getRule(String id) {
		for (Rule tmp : values()) {
			if (tmp.getID().equals(id))
				return tmp;
		}
		return null;
	}
}
