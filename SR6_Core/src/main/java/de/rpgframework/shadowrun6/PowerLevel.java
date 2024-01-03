package de.rpgframework.shadowrun6;

import java.util.Locale;

public enum PowerLevel {
	LOW_LEVEL,
	STREET_LEVEL,
	STANDARD,
	PRIME_RUNNER,
	ELITE,
	;
	public String getName() {
		return getName(Locale.getDefault());
	}
	public String getName(Locale loc) {
		return Shadowrun6Core.getI18nResources().getString( "powerlevel."+this.name().toLowerCase(), loc);
	}
}