/**
 * 
 */
package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

import de.rpgframework.shadowrun.ANPC;

/**
 * @author prelle
 *
 */
@Root(name="npcs")
@ElementList(entry="npc",type=ANPC.class,inline=true)
public class NPCList extends ArrayList<SR6NPC> {

	private static final long serialVersionUID = -6620101179825758276L;

	//-------------------------------------------------------------------
	/**
	 */
	public NPCList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public NPCList(Collection<SR6NPC> c) {
		super(c);
	}

}
