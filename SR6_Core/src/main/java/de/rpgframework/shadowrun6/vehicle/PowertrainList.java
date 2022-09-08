package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="powertrains")
@ElementList(entry="powertrain",type=Powertrain.class)
public class PowertrainList extends ArrayList<Powertrain> {

}
