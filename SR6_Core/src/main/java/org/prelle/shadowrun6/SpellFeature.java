/**
 * 
 */
package org.prelle.shadowrun6;

import java.text.Collator;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="spellfeature")
public class SpellFeature extends DataItem implements Comparable<SpellFeature> {
	
	@Attribute(required=true)
	private String id;

	//-------------------------------------------------------------------
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SpellFeature o) {
		return Collator.getInstance().compare(getName(), o.getName());
	}

}
