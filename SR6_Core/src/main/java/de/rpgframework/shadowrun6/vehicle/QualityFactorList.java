package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="qualityfactors")
@ElementList(entry="qualityfactor",type=QualityFactor.class)
public class QualityFactorList extends ArrayList<QualityFactor> {

}
