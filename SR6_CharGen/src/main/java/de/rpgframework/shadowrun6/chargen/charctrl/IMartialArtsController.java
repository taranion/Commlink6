/**
 *
 */
package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsValue;

/**
 * @author prelle
 *
 */
public interface IMartialArtsController extends PartialController<MartialArts>, ComplexDataItemController<MartialArts,MartialArtsValue> {

	public ITechniqueController getTechniqueController(MartialArtsValue style);

}
