package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger.Level;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.Technique;
import de.rpgframework.shadowrun6.TechniqueValue;
import de.rpgframework.shadowrun6.Technique.Category;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CommonTechniqueController extends ControllerImpl<Technique> implements ITechniqueController {

	private MartialArtsValue style;

	//-------------------------------------------------------------------
	public CommonTechniqueController(SR6CharacterController parent, MartialArtsValue style) {
		super(parent);
		this.style = style;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<Technique> getAvailable() {
		List<Technique> ret = new ArrayList<Technique>();
		// Add all theoretically possible
		for (Technique tech : Shadowrun6Tools.filterByPluginSelection(Shadowrun6Core.getItemList(Technique.class), getModel())) {
			if (tech==style.getResolved().getSignatureTechnique())
				continue;
			for (Technique.Category cat : tech.getCategories()) {
				if (style.getResolved().getCategories().contains(cat) || cat==Category.GENERAL) {
					// match by category
					ret.add(tech);
				}
			}
		}

		// Now remove those already learned
		for (Technique tmp : new ArrayList<Technique>(ret)) {
			if (getModel().hasTechnique(tmp))
				ret.remove(tmp);
		}

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<TechniqueValue> getSelected() {
		return getModel().getTechniques(style.getResolved());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(Technique value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(TechniqueValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(Technique value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(Technique value, Decision... decisions) {
		// TODO Auto-generated method stub
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<TechniqueValue> select(Technique data, Decision... decisions) {
		Possible poss = canBeSelected(data, decisions);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to select unselectable technique {0}: {1}", data, poss.toString());
			return new OperationResult<>(poss);
		}

		logger.log(Level.INFO,"Select {0} in style {1}", data.getId(), style.getKey());
		TechniqueValue val = new TechniqueValue(data, style.getResolved());
		Shadowrun6Character model = getModel();
		model.addTechnique(val);

		if (model.isInCareerMode()) {
			logger.log(Level.INFO, "Add technique '%s' for 5 Karma and 1500 Nuyen", data.getId());
			// Pay karma
			int cost = (int)getSelectionCost(data);
			model.setKarmaFree(model.getKarmaFree() -cost);
			model.setKarmaInvested(model.getKarmaInvested() +cost);
			// Pay Nuyen
			model.setNuyen(model.getNuyen() -1500);

			DataItemModification mod = new DataItemModification(ShadowrunReference.TECHNIQUE, data.getId());
			mod.setDate(Date.from(Instant.now()));
			mod.setExpCost(1500);
			model.addToHistory(mod);
		} else {
			logger.log(Level.INFO, "Add martial art '{0}'", data.getId());
		}

		parent.runProcessors();

		return new OperationResult<TechniqueValue>(val);
	}

	//-------------------------------------------------------------------
	protected DataItemModification getModification(TechniqueValue key) {
		DataItemModification ret = null;
		for (Modification mod : getModel().getHistory()) {
			if (!(mod instanceof DataItemModification))
				continue;
			DataItemModification amod = (DataItemModification)mod;
			if (amod.getReferenceType()!=ShadowrunReference.TECHNIQUE)
				continue;
			if (amod.getResolvedKey()!=key.getResolved())
				continue;

//			if (mod.getSource()!=this && mod.getSource()!=null)
//				continue;
			if (ret==null || amod.getExpCost()>ret.getExpCost())
				ret = amod;
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(TechniqueValue value) {
		Shadowrun6Character model = getModel();
		if (!model.getTechniques().contains(value))
			return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);

		// In career mode, only removing
		if (getModel().isInCareerMode()) {
			boolean undoCareer = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER);
			boolean undoChargen= parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN);
			boolean addedInCareer = getModification(value)!=null;
			if (addedInCareer && !undoCareer)
				return Possible.FALSE;
			if (!addedInCareer && !undoChargen)
				return Possible.FALSE;
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(TechniqueValue data) {
		Possible poss = canBeDeselected(data);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to deselect technique '%s', but cannot do that: {1}", data.getKey(), poss.toString());
			return false;
		}

		Shadowrun6Character model = getModel();
		model.removeTechnique(data);
		if (model.isInCareerMode()) {
			DataItemModification mod = getModification(data);
			if (mod!=null) {
				// Had been added in career
				// Grant karma
				int cost = (int)getSelectionCost(data.getResolved());
				model.setKarmaFree(model.getKarmaFree() +cost);
				model.setKarmaInvested(model.getKarmaInvested() -cost);
				// Grant Nuyen
				model.setNuyen(model.getNuyen() + mod.getExpCost());
				// Remove from history
				model.removeFromHistory(mod);
			}
		}
		logger.log(Level.INFO, "Removed technique '%s'", data.getKey());

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(Technique data) {
		if (getModel().hasRuleFlag(SR6RuleFlag.MARTIAL_ARTS_PRODIGY))
			return 3;
		return 5;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(Technique data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		try {
			todos.clear();

			Shadowrun6Character model = getModel();

			if (!model.isInCareerMode()) {
				for (TechniqueValue mVal : model.getTechniques()) {
					if (mVal.isAutoAdded())
						continue;
					// Pay 7 Karma
					int karmaNeeded = 5;
					logger.log(Level.INFO, "Pay {0} Karma for {1}", karmaNeeded, mVal.getKey());
					model.setKarmaFree( model.getKarmaFree() - karmaNeeded);
					model.setKarmaInvested( model.getKarmaInvested() - karmaNeeded);
				}
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
