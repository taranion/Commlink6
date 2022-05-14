package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SpliMoCharacterController;

/**
 * @author prelle
 *
 */
public class SR6PriorityMetatypeController extends ControllerImpl<SR6MetaType> implements IMetatypeController<SR6MetaType> {
	
	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(PriorityCharacterGenerator.class, Locale.ENGLISH, Locale.ENGLISH);

	private Map<SR6MetaType, MetaTypeOption> availableOptions;
	private static Random random = new Random();

	//-------------------------------------------------------------------
	public SR6PriorityMetatypeController(SpliMoCharacterController parent) {
		super(parent);
		availableOptions  = new HashMap<SR6MetaType,MetaTypeOption>();
	}

	@Override
	public void decide(SR6MetaType decideFor, Choice choice, Decision decision) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MetaTypeOption> getAvailable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getKarmaCost(SR6MetaType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canBeSelected(SR6MetaType type) {
		// TODO Auto-generated method stub
		return true;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#randomizeSizeWeight()
	 */
	@Override
	public void randomizeSizeWeight() {
		MetaType value = getModel().getMetatype();
		if (value==null)
			return;
		// Roll until you get a sensible distribution result
		for (int i=0; i<10; i++) {
			float gauss = (float)random.nextGaussian();
			float diff  = 0.15f*gauss;
			float diff2 = 0.10f*gauss;
			getModel().setSize(Math.round(value.getSize()+value.getSize()*diff));
			getModel().setWeight(Math.round(value.getWeight()+value.getWeight()*diff2));
			if (gauss>1.0f || gauss<-1.0f)
				continue;
			break;
		}	
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#select(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public boolean select(SR6MetaType value) {
		logger.log(Level.DEBUG, "ENTER select("+value+")");
		try {
			if (!canBeSelected(value))
				return false;

			getModel().setMetatype(value);
			randomizeSizeWeight();

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE select("+value+")");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();
		Shadowrun6Character model = getModel();

		try {

			for (Modification mod : previous) {
				unprocessed.add(mod);
			}

			// Add modifications from selection
			MetaType meta = getModel().getMetatype();
			if (meta == null) {
				todos.add(new ToDoElement(Severity.STOPPER, RES, "generror.metatype_not_selected"));
			} else {
				if (meta.getKarma() != 0) {
					logger.log(Level.INFO, "Pay {0} Karma for metatype '{1}'", meta.getKarma(), meta.getId());
					model.setKarmaFree(model.getKarmaFree() - meta.getKarma());
				}
				// Add more modifications
				unprocessed.addAll(meta.getModifications());
			}
		return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#selectBodyType(de.rpgframework.shadowrun.BodyType)
	 */
	@Override
	public boolean selectBodyType(BodyType value) {
		logger.log(Level.DEBUG, "ENTER selectBodyType("+value+")");
		try {
			getModel().setBodytype(value);

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.DEBUG, "LEAVE selectBodyType("+value+")");
		}
	}

}
