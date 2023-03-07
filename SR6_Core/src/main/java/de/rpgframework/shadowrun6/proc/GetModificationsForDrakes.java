package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.Movement;
import de.rpgframework.shadowrun.Movement.MovementType;
import de.rpgframework.shadowrun6.DrakeTypeValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
public class GetModificationsForDrakes implements ProcessingStep {

	protected static final Logger logger = System.getLogger(GetModificationsForDrakes.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public GetModificationsForDrakes(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.proc.CharacterProcessor#process(org.prelle.shadowrun5.ShadowrunCharacter, java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.TRACE, "ENTER: process");
		try {
			DrakeTypeValue drake = model.getDrakeType();
			if (drake!=null) {
				for (Modification mod : drake.getResolved().getModifications()) {
					logger.log(Level.INFO, "Drake {0} adds {1}", drake.getKey(), mod);
					unprocessed.add(mod);
				}

				// Add new body
				BodyForm body = new BodyForm(BodyType.DRAKE);
				body.addMovement( new Movement( model.getBodyForms().get(0).getMovement(MovementType.GROUND) ));
				model.addBodyForm(body);
				logger.log(Level.INFO, "Added Drake body");
			}
		} finally {
			logger.log(Level.TRACE, "LEAVE : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

}
