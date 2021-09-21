package de.rpgframework.shadowrun6.chargen.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class PriorityMetatypeController extends ControllerImpl<SR6MetaType> implements IMetatypeController<SR6MetaType> {

	private Map<SR6MetaType, MetaTypeOption> availableOptions;
	private static Random random = new Random();

	//-------------------------------------------------------------------
	public PriorityMetatypeController(SR6CharacterController parent) {
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

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return null;
	}

}
