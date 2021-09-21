package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ResourceBundle;

import de.rpgframework.ResourceI18N;

public enum PriorityVariant {
	STANDARD,
	PRIME_RUNNER,
	STREET_LEVEL
	;
	public String getName() { 
		return ResourceI18N.get(ResourceBundle.getBundle(PriorityCharacterGenerator.class.getName()), "variant."+this.name().toLowerCase());
	}
}