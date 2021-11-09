package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.function.Function;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.ShadowrunCore;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6ReferenceTypeConverter<T> implements Function<ModifiedObjectType, String> {

	private final static MultiLanguageResourceBundle CORE = ShadowrunCore.getI18nResources();

	//-------------------------------------------------------------------
	private static String toString(ShadowrunReference ref) {
//		if (ref==ShadowrunReference.CULTURE_LORE) return null;
		String key = "reference."+ref.name().toLowerCase()+".name";
		return CORE.getString(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public String apply(ModifiedObjectType t) {
		return toString((ShadowrunReference) t);
	}

}
