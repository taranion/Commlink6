package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="consoletypes")
@ElementList(entry="consoletype",type=ConsoleType.class)
public class ConsoleTypeList extends ArrayList<ConsoleType> {

}
