/**
 * 
 */
package de.rpgframework.shadowrun6;

import java.text.Collator;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="spellfeature")
public class SpellFeature extends DataItem implements Comparable<SpellFeature> {

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
