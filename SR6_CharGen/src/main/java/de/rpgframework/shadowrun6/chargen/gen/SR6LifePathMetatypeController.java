package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SR6LifePathMetatypeController extends ControllerImpl<SR6MetaType> implements IMetatypeController<SR6MetaType> {
	
	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(LifePathCharacterGenerator.class, Locale.ENGLISH, Locale.ENGLISH);

	private List<MetaTypeOption> available;
	private static Random random = new Random();

	//-------------------------------------------------------------------
	public SR6LifePathMetatypeController(SR6CharacterController parent) {
		super(parent);
		// Build available
		available  = new ArrayList<>();
		for (SR6MetaType tmp : Shadowrun6Core.getItemList(SR6MetaType.class)) {
			MetaTypeOption opt = new MetaTypeOption(tmp, tmp.getKarma());
			available.add(opt);
		}
		Collections.sort(available, new Comparator<MetaTypeOption>() {
			public int compare(MetaTypeOption arg0, MetaTypeOption arg1) {
				return Collator.getInstance().compare(arg0.getResolved().getName(), arg1.getResolved().getName());
			}
		});
	}

	@Override
	public void decide(SR6MetaType decideFor, Choice choice, Decision decision) {
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#getAvailable()
	 */
	@Override
	public List<MetaTypeOption> getAvailable() {
		return available;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#getKarmaCost(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public int getKarmaCost(SR6MetaType type) {
		return type.getKarma();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController#canBeSelected(de.rpgframework.shadowrun.MetaType)
	 */
	@Override
	public boolean canBeSelected(SR6MetaType type) {
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
					logger.log(Level.INFO, "Pay {} Karma for metatype '{}'", meta.getKarma(), meta.getId());
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
