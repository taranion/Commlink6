package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class SR6MetamagicOrEchoController extends ControllerImpl<MetamagicOrEcho>
		implements IMetamagicOrEchoController {

	//-------------------------------------------------------------------
	public SR6MetamagicOrEchoController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(MetamagicOrEcho item) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<MetamagicOrEcho> getAvailable() {
		MagicOrResonanceType type = getModel().getMagicOrResonanceType();		
		if (type!=null && type.usesMagic()) {
			return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.METAMAGIC||m.getType()==Type.METAMAGIC_ADEPT)
					.collect(Collectors.toList());
		} else if (type!=null && type.usesResonance()) {
			return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.ECHO)
					.collect(Collectors.toList());
		} else {
			return Shadowrun6Core.getItemList(MetamagicOrEcho.class).stream()
					.filter(p -> parent.showDataItem(p))
					.filter(p -> !getModel().hasMetamagicOrEcho(p.getId()) || p.hasLevel())
					.filter(m -> m.getType()==Type.TRANSHUMANISM)
					.collect(Collectors.toList());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<MetamagicOrEchoValue> getSelected() {
		return getModel().getMetamagicOrEchoes();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(MetamagicOrEchoValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(MetamagicOrEcho value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController#getGrade()
	 */
	@Override
	public int getGrade() {
		MagicOrResonanceType type = getModel().getMagicOrResonanceType();
		List<MetamagicOrEchoValue> list = null;
		if (type != null && type.usesMagic()) {
			list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.METAMAGIC || m.getModifyable().getType() == Type.METAMAGIC_ADEPT)
					.collect(Collectors.toList());
		} else if (type != null && type.usesResonance()) {
			list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.ECHO)
					.collect(Collectors.toList());
		} else {
			list = getSelected().stream()
					.filter(m -> m.getModifyable().getType() == Type.TRANSHUMANISM)
					.collect(Collectors.toList());
		}
		// Determine the grade
		int grade = 0;
		for (MetamagicOrEchoValue tmp : list) {
			if (tmp.getModifyable().hasLevel())
				grade += tmp.getDistributed();
			else
				grade ++;
		}
		return grade;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(MetamagicOrEcho value, Decision... decisions) {
		// Is it available in general?
		if (!getAvailable().contains(value)) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_AVAILABLE);
		}

		// Calculate Karma cost
		int karma = 10 + getGrade() +1;
		
		if (getModel().getKarmaFree()<karma) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<MetamagicOrEchoValue> select(MetamagicOrEcho value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0})", value);
		try {
			Possible possible = canBeSelected(value, decisions);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to select a metamagic/echo that cannot be selected: {0}",possible.getI18NKey());
				return new OperationResult<MetamagicOrEchoValue>(possible, false);
			}

			MetamagicOrEchoValue selected = new MetamagicOrEchoValue(value);
			logger.log(Level.INFO, "{0} has Level = {1}", value, value.hasLevel());
			if (value.hasLevel()) {
				selected.setDistributed(1);
			}
			for (Decision dec : decisions) {
				selected.addDecision(dec);
			}

			int karma = 10 + getGrade() +1;
			getModel().addMetamagicOrEcho(selected);
			logger.log(Level.INFO, "Add metamagic/echo '" + value.getId() + "' for " + karma + " karma");
			Shadowrun6Character model = getModel(); 
			model.setKarmaFree( model.getKarmaFree() - karma);
			model.setKarmaInvested( model.getKarmaInvested() + karma);

			parent.runProcessors();
			return new OperationResult<MetamagicOrEchoValue>(selected);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(MetamagicOrEchoValue value) {
		// Is it selected?
		if (getSelected().contains(value)) {
			return new Possible(false, IRejectReasons.IMPOSS_NOT_PRESENT);
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(MetamagicOrEchoValue value) {
		logger.log(Level.TRACE, "ENTER deselect({0})", value);
		try {
			Possible possible = canBeDeselected(value);
			if (possible.getState()!=State.POSSIBLE) {
				logger.log(Level.ERROR, "Trying to deselect a metamagic/echo that cannot be deselected: {0}",possible.getI18NKey());
				return false;
			}

			int karma = 10 + getGrade();
			Shadowrun6Character model = getModel(); 
			model.removeMetamagicOrEcho(value);
			model.setKarmaFree( model.getKarmaFree() + karma);
			model.setKarmaInvested( model.getKarmaInvested() - karma);
			
			logger.log(Level.INFO, "Remove metamagic/echo '" + value.getModifyable().getId() + "' for " + karma + " karma");

			parent.runProcessors();
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE deselect({0})", value);
		}
	}

	@Override
	public float getSelectionCost(MetamagicOrEcho data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Possible canBeIncreased(MetamagicOrEchoValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canBeDecreased(MetamagicOrEchoValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<MetamagicOrEchoValue> increase(MetamagicOrEchoValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<MetamagicOrEchoValue> decrease(MetamagicOrEchoValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

}
