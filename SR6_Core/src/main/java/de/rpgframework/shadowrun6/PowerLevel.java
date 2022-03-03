package de.rpgframework.shadowrun6;

import java.util.ResourceBundle;

import de.rpgframework.ResourceI18N;

public enum PowerLevel {
	LOW_LEVEL,
	STREET_LEVEL,
	STANDARD,
	PRIME_RUNNER,
	EILTE,
	;
	public String getName() { 
		return ResourceI18N.get(ResourceBundle.getBundle(Shadowrun6Character.class.getName()), "variant."+this.name().toLowerCase());
	}
}