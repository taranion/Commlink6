/**
 * 
 */
package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="items")
@ElementList(entry="item",type=ItemTemplate.class,inline=true)
public class ItemTemplateList extends ArrayList<ItemTemplate> {

	//-------------------------------------------------------------------
	/**
	 */
	public ItemTemplateList() {
	}

}
