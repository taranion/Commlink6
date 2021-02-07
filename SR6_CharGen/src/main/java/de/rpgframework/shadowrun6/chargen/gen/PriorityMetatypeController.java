package de.rpgframework.shadowrun6.chargen.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
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

	@Override
	public boolean select(SR6MetaType value) {
		if (!canBeSelected(value))
			return false;
		
		getModel().setMetatype(value);
		
		parent.runProcessors();
		return true;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return null;
	}

}
