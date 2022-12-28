package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name = "itemenhancements")
@ElementList(entry="itemenhancement",type=SR6ItemEnhancement.class)
public class ItemEnhancementList extends ArrayList<SR6ItemEnhancement> {

	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------
	public ItemEnhancementList() {
	}

	//-------------------------------------------------------------------
	public ItemEnhancementList(Collection<? extends SR6ItemEnhancement> c) {
		super(c);
	}

}
