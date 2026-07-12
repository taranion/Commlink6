package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;

/**
 * Life Path carries all remaining Karma and Nuyen into career mode.
 */
public class SR6LifePathRemainingKarmaNuyenController extends ControllerImpl<Object> implements PartialController<Object> {

	protected static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	protected final static Logger logger = System.getLogger(SR6LifePathRemainingKarmaNuyenController.class.getPackageName());

	//-------------------------------------------------------------------
	public SR6LifePathRemainingKarmaNuyenController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		todos.clear();
		Shadowrun6Character model = getModel();
		logger.log(Level.INFO, "Life Path carries over {0} Karma and {1} Nuyen", model.getKarmaFree(), model.getNuyen());
		if (model.getKarmaFree()<0) {
			todos.add(new ToDoElement(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.TODO_NEGATIVE_KARMA, Math.abs(model.getKarmaFree())));
		}
		boolean allowNegative = parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_NEGATIVE_NUYEN);
		if (model.getNuyen()<0 && !allowNegative) {
			todos.add(new ToDoElement(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.TODO_NEGATIVE_NUYEN, model.getNuyen()));
		}
		return unprocessed;
	}

}
