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

	public static Rule CHARGEN_RAISE_ABOVE_6    = new Rule(EffectOn.CHARGEN,"CHARGEN_RAISE_ABOVE_6", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_MAX_INITIATION   = new Rule(EffectOn.CHARGEN,"CHARGEN_MAX_INITIATION", Rule.Type.INTEGER, RES, "99");
	public static Rule CHARGEN_MAX_SUBMERSION   = new Rule(EffectOn.CHARGEN,"CHARGEN_MAX_SUBMERSION", Rule.Type.INTEGER, RES, "99");
	public static Rule CHARGEN_MAX_TRANSHUMAN   = new Rule(EffectOn.CHARGEN,"CHARGEN_MAX_TRANSHUMAN", Rule.Type.INTEGER, RES, "99");
	public static Rule CHARGEN_MAX_ANIMALISM    = new Rule(EffectOn.CHARGEN,"CHARGEN_MAX_ANIMALISM" , Rule.Type.INTEGER, RES, "2");
	public static Rule CHARGEN_ADJUSTMENT_ON_LOWERED_MAX = new Rule(EffectOn.CHARGEN,"CHARGEN_ADJUSTMENT_ON_LOWERED_MAX", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_MAX_AVAILABILITY = new Rule(EffectOn.CHARGEN,"CHARGEN_MAX_AVAILABILITY", Rule.Type.INTEGER, RES, "6");
	public static Rule CHARGEN_BUY_SPELLS_KARMA = new Rule(EffectOn.CHARGEN,"CHARGEN_BUY_SPELLS_KARMA", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_ERRATED_POINT_BUY= new Rule(EffectOn.CHARGEN,"CHARGEN_ERRATED_POINT_BUY"   , Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_PRIO_ADJUSTED_MAGIC_RESO = new Rule(EffectOn.CHARGEN,"CHARGEN_PRIO_ADJUSTED_MAGIC_RESO", Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_EXTENDED_CONTACT = new Rule(EffectOn.CHARGEN,"CHARGEN_EXTENDED_CONTACT" , Rule.Type.BOOLEAN, RES, "false");
	public static Rule CHARGEN_MORE_KNOWLEDGE   = new Rule(EffectOn.CHARGEN,"CHARGEN_MORE_KNOWLEDGE"   , Rule.Type.BOOLEAN, RES, "false");

	public static Rule ALLOW_TRANSHUMANISM      = new Rule(EffectOn.COMMON,"ALLOW_TRANSHUMANISM"      , Rule.Type.BOOLEAN, RES, "false");
	public static Rule ALLOW_NEUROMORPHISM      = new Rule(EffectOn.COMMON,"ALLOW_NEUROMORPHISM"      , Rule.Type.BOOLEAN, RES, "false");
	public static Rule ALLOW_ANIMALISM          = new Rule(EffectOn.COMMON,"ALLOW_ANIMALISM"          , Rule.Type.BOOLEAN, RES, "false");
	public static Rule MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP = new Rule(EffectOn.COMMON,"MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP", Rule.Type.BOOLEAN, RES, "true");
	public static Rule ADD_STRENGTH_TO_MELEE_AR = new Rule(EffectOn.COMMON,"ADD_STRENGTH_TO_MELEE_AR" , Rule.Type.BOOLEAN, RES, "true");
	public static Rule HIGH_STRENGTH_ADDS_DAMAGE= new Rule(EffectOn.COMMON,"HIGH_STRENGTH_ADDS_DAMAGE", Rule.Type.BOOLEAN, RES, "false");

	public static Rule CARGOFACTOR_IS_WITHOUT_SEATS = new Rule(EffectOn.COMMON,"CARGOFACTOR_IS_WITHOUT_SEATS", Rule.Type.BOOLEAN, RES, "false");


	//-------------------------------------------------------------------
	public static Rule[] values() {
		Rule[] sr6 = new Rule[] {
				CHARGEN_RAISE_ABOVE_6,
				CHARGEN_MAX_INITIATION,
				CHARGEN_MAX_SUBMERSION,
				CHARGEN_MAX_TRANSHUMAN,
				CHARGEN_MAX_ANIMALISM,
				CHARGEN_ADJUSTMENT_ON_LOWERED_MAX,
				CHARGEN_PRIO_ADJUSTED_MAGIC_RESO,
				CHARGEN_BUY_SPELLS_KARMA,
				CHARGEN_MAX_AVAILABILITY,
				CHARGEN_ERRATED_POINT_BUY,
				ALLOW_TRANSHUMANISM,
				ALLOW_NEUROMORPHISM,
				CHARGEN_EXTENDED_CONTACT,
				MYSTADEPT_ADVANCE_RAISE_MAGIC_RAISE_PP,
				ADD_STRENGTH_TO_MELEE_AR,
				HIGH_STRENGTH_ADDS_DAMAGE,
				CHARGEN_MORE_KNOWLEDGE,
				CARGOFACTOR_IS_WITHOUT_SEATS
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
