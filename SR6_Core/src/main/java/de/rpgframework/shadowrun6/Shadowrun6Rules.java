package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.Rule.EffectOn;
import de.rpgframework.shadowrun.ShadowrunRules;

/**
 * @author prelle
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
		FINAL_MAGIC
		;
		public String getName(Locale loc) {
			return RES.getString("rule.chargen_prio_adept_pp."+name().toLowerCase(), loc);
		}
		public String toString() {
			return RES.getString("rule.chargen_prio_adept_pp."+name().toLowerCase());
		}
	}
	
	public static Rule CHARGEN_ADJUSTMENT_ON_LOWERED_MAX = new Rule(EffectOn.CHARGEN,"CHARGEN_ADJUSTMENT_ON_LOWERED_MAX", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_PRIO_ADEPT_PP    = new Rule(EffectOn.CHARGEN,"CHARGEN_PRIO_ADEPT_PP", PRIORITY_MAGIC.class, RES, PRIORITY_MAGIC.PRIO_MAGIC);
	public static Rule CHARGEN_ALLOW_LEGAL_AVAIL7PLUS = new Rule(EffectOn.CHARGEN,"CHARGEN_ALLOW_LEGAL_AVAIL7PLUS", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_BUY_SPELLS_KARMA = new Rule(EffectOn.CHARGEN,"CHARGEN_BUY_SPELLS_KARMA", Rule.Type.BOOLEAN, RES, "false");
	public static Rule ALLOW_TRANSHUMANISM      = new Rule(EffectOn.COMMON,"ALLOW_TRANSHUMANISM", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_EXTENDED_CONTACT = new Rule(EffectOn.COMMON,"CHARGEN_EXTENDED_CONTACT", Rule.Type.BOOLEAN, RES, "false");
	public static Rule MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP = new Rule(EffectOn.COMMON,"MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP", Rule.Type.BOOLEAN, RES, "true");
	public static Rule ADD_STRENGTH_TO_MELEE_AR = new Rule(EffectOn.COMMON,"ADD_STRENGTH_TO_MELEE_AR", Rule.Type.BOOLEAN, RES, "true");

	
	//-------------------------------------------------------------------
	public static Rule[] values() {
		Rule[] sr6 = new Rule[] {
				CHARGEN_ADJUSTMENT_ON_LOWERED_MAX,
				CHARGEN_PRIO_ADEPT_PP,
				CHARGEN_BUY_SPELLS_KARMA,
				CHARGEN_ALLOW_LEGAL_AVAIL7PLUS,
				ALLOW_TRANSHUMANISM,
				CHARGEN_EXTENDED_CONTACT,
				MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP,	
				ADD_STRENGTH_TO_MELEE_AR,
		};
		
		List<Rule> merged = new ArrayList<>();
		merged.addAll(List.of(ShadowrunRules.values()));
		merged.addAll(List.of(sr6));
		return merged.toArray(new Rule[merged.size()]);
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
