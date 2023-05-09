package de.rpgframework.shadowrun6.items;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public enum Construction {

	OPEN,
	STANDARD,
	ENCLOSED
	;

	private static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

	//-------------------------------------------------------------------
	public String getName() {
		return RES.getString("construction."+this.name().toLowerCase());
	}

	//-------------------------------------------------------------------
	public String getName(Locale loc) {
		return RES.getString("construction."+this.name().toLowerCase(), loc);
	}

}
