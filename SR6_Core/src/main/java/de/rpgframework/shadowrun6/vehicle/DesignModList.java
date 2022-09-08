package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="designmods")
@ElementList(entry="designmod",type=DesignMod.class)
public class DesignModList extends ArrayList<DesignMod> {

}
