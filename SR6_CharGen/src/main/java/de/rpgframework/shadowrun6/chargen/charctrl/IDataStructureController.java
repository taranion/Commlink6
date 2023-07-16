/**
 *
 */
package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureValue;

/**
 * @author prelle
 *
 */
public interface IDataStructureController extends PartialController<DataStructure>, ComplexDataItemController<DataStructure,DataStructureValue> {

	//--------------------------------------------------------------------
	/**
	 * You cannot bind more than RESONANZx5 Data Structure points
	 */
	public int getPointsLeft();

}
