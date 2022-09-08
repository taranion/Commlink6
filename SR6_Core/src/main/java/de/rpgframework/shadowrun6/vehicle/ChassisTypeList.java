/**
 * 
 */
package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="chassistypes")
@ElementList(entry="chassistype",type=ChassisType.class)
public class ChassisTypeList extends ArrayList<ChassisType> {

}
