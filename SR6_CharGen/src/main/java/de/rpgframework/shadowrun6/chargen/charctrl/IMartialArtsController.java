/**
 *
 */
package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.shadowrun.ATechnique;
import de.rpgframework.shadowrun.TechniqueValue;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Technique;

/**
 * @author prelle
 *
 */
public interface IMartialArtsController extends PartialController<MartialArts>, ComplexDataItemController<MartialArts,MartialArtsValue> {

	//-------------------------------------------------------------------
	public List<ATechnique> getAvailableTechniques(MartialArtsValue style);

	//-------------------------------------------------------------------
	public Possible canBeSelected(MartialArtsValue learnedIn, Technique data);

	//-------------------------------------------------------------------
	public Possible canBeDeselected(TechniqueValue<Technique> data);

	//-------------------------------------------------------------------
	public TechniqueValue<Technique> select(MartialArtsValue learnedIn, Technique data);

	//-------------------------------------------------------------------
	public boolean deselect(TechniqueValue<Technique> data);

}
