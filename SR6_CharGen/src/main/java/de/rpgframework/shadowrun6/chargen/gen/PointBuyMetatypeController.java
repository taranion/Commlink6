package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
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
public class PointBuyMetatypeController extends ControllerImpl<SR6MetaType> implements IMetatypeController<SR6MetaType> {
	
	private static MultiLanguageResourceBundle RES = PointBuyCharacterGenerator.RES;

	private List<SR6MetaType> availableOptions;
	private static Random random = new Random();

	//-------------------------------------------------------------------
	public PointBuyMetatypeController(SR6CharacterController parent) {
		super(parent);
		availableOptions  = new ArrayList<>();
		availableOptions.addAll(Shadowrun6Core.getItemList(SR6MetaType.class));
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
		logger.debug("ENTER select("+value+")");
		try {
			if (!canBeSelected(value))
				return false;

			getModel().setMetatype(value);
			randomizeSizeWeight();

			parent.runProcessors();
			return true;
		} finally {
			logger.debug("LEAVE select("+value+")");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();
		
		for (Modification mod : previous) {
			unprocessed.add(mod);
		}
		
		Shadowrun6Character model = parent.getModel();
		logger.debug("Available metatype options: "+availableOptions);
		SR6MetaType selected = model.getMetatype();
		logger.debug("  selected: "+selected);

		/*
		 * If a metatype is selected, apply it
		 */
		if (selected==null) {
			todos.add(new ToDoElement(Severity.STOPPER, RES.getString("pointbuy.todo.metatype")));
		} else {
			// A selection has been made
			if (!availableOptions.contains(selected)) {
				logger.warn("Deselected metatype since it is not available anymore");
				model.setMetatype(null);
				todos.add(new ToDoElement(Severity.STOPPER, RES.getString("pointbuy.todo.metatype")));
			} else {
				int karma = selected.getKarma();
				if (karma>0) {
					logger.info("Pay "+karma+" for metatype "+selected.getId());
					model.setKarmaFree(model.getKarmaFree()-karma);
				}
				// Applying modification is a special CharacterProcessor from Core
			}
		}
		
		return unprocessed;
	}

}
