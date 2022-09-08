package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="designopts")
@ElementList(entry="designopt",type=DesignOption.class)
public class DesignOptionList extends ArrayList<DesignOption> {

}
