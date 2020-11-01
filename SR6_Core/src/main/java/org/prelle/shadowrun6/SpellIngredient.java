package org.prelle.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;

/**
 * @author prelle
 *
 */
public class SpellIngredient extends DataItem {

	@Attribute
	private int drain;
	@Attribute
	private Selector select;
	@Attribute
	private boolean multi;
	
}
