package de.rpgframework.shadowrun6.modifications;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.modification.Modification;

/**
 * @author prelle
 *
 */
public class EdgeModification extends Modification {

	public enum ModificationType {
		COST,
		BONUS,
	}

	@Attribute(name="edge")
	private ModificationType edgeType;

}
