package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="actions")
@ElementListUnion({
    @ElementList(entry="action", type=Shadowrun6Action.class),
})
public class ActionList extends ArrayList<Shadowrun6Action> {

	private static final long serialVersionUID = -4307087686099123762L;

	//-------------------------------------------------------------------
	/**
	 */
	public ActionList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public ActionList(Collection<? extends Shadowrun6Action> c) {
		super(c);
	}

}
