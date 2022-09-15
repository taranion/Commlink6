package de.rpgframework.shadowrun6;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="techniques")
@ElementList(entry="technique",type=Technique.class)
public class TechniqueList extends ArrayList<Technique> {

}
