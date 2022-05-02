package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.IAttribute;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;

/**
 * @author prelle
 *
 */
public class RemainingKarmaNuyenController extends ControllerImpl<Object> implements PartialController<Object> {

	protected static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;
	
	protected final static Logger logger = System.getLogger(RemainingKarmaNuyenController.class.getPackageName());

	//-------------------------------------------------------------------
	protected RemainingKarmaNuyenController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		todos.clear();
		Shadowrun6Character model = getModel();
		logger.log(Level.INFO, "Have {0} Karma and {1} Nuyen remaining", model.getKarmaFree(), model.getNuyen());
		if (model.getKarmaFree()>5) {
			todos.add(new ToDoElement(Severity.WARNING, RES, IRejectReasons.TODO_LOOSE_KARMA, model.getKarmaFree()));
		}
		if (model.getNuyen()>5000) {
			todos.add(new ToDoElement(Severity.WARNING, RES, IRejectReasons.TODO_LOOSE_NUYEN, model.getNuyen()));
		}
		return unprocessed;
	}

}
