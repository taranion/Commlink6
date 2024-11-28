package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.SR6ContactGenerator;

/**
 * 
 */
public class SR6LifePathContactGenerator extends SR6ContactGenerator {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6LifePathContactGenerator(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.ERROR)) logger.log(Level.TRACE, "ENTER process "+previous);
		List<Modification> unprocessed = new ArrayList<>();
		try {
			todos.clear();

			for (Modification tmp : previous) {
//				if (tmp.getReferenceType()==ShadowrunReference.CONTACT) {
//				} else
					unprocessed.add(tmp);
			}


		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
		return unprocessed;
	}

}
