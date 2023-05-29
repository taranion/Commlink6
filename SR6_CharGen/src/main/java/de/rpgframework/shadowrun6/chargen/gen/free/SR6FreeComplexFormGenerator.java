package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.gen.IComplexFormGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6ComplexFormGenerator;

/**
 * @author prelle
 *
 */
public class SR6FreeComplexFormGenerator extends CommonSR6ComplexFormGenerator implements IComplexFormController, IComplexFormGenerator {

	protected static Logger logger = System.getLogger(ControllerImpl.class.getPackageName()+".cforms");

	//-------------------------------------------------------------------
	protected SR6FreeComplexFormGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(ComplexForm value, Decision... decisions) {
		Possible poss = super.canBeSelected(value, decisions);
		if (!poss.get())
			return poss;

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		return previous;
	}

}
