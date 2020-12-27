package de.rpgframework.shadowrun6.chargen.gen;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;

/**
 * @author prelle
 *
 */
public class CommonSkillController extends ControllerImpl<SR6Skill> implements SR6SkillController {
	
	protected final static Logger logger = LogManager.getLogger("shadowrun6.gen.skill");

	protected Shadowrun6Character model;

	//-------------------------------------------------------------------
	public CommonSkillController(SR6CharacterController parent) {
		super(parent);
		model = parent.getModel();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(SR6Skill item) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean canBeIncreased(SR6SkillValue value) {
		if (!model.getSkillValues().contains(value)) {
			// Value not present in character
			return false;
		}
		return value.getDistributed()<6;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean canBeDecreased(SR6SkillValue value) {
		if (!model.getSkillValues().contains(value)) {
			// Value not present in character
			return false;
		}
		return value.getDistributed()>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean increase(SR6SkillValue ref) {
		logger.debug("increase "+ref);
		if (!canBeIncreased(ref)) {
			logger.warn("Trying to increase a skill which cannot be increased: "+ref);
			return false;
		}
	
		// Change model
		ref.setDistributed(ref.getDistributed()+1);
		logger.info("Increase skill "+ref.getModifyable().getId()+" to "+ref.getDistributed());
		
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean decrease(SR6SkillValue ref) {
		logger.debug("decrease "+ref);
		if (!canBeDecreased(ref))
			return false;
	
		// Change model
		ref.setDistributed(ref.getDistributed()-1);
		if (ref.getModifiedValue()==0)
			model.removeSkillValue( ref);
		logger.info("Decrease skill "+ref.getModifyable().getId()+" to "+ref.getDistributed());
		
		return true;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SR6Skill> getAvailable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SR6SkillValue> getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getSelectionCost(SR6Skill data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDeselectionCost(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canBeSelected(SR6Skill data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeDeselected(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SR6SkillValue select(SR6Skill data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deselect(SR6SkillValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsOptionSelection(SR6Skill toSelect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<?> getOptions(SR6Skill toSelect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SR6SkillValue select(SR6Skill data, Object option) {
		// TODO Auto-generated method stub
		return null;
	}

}
