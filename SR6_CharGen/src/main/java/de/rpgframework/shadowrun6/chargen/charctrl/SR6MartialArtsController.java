package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.time.Instant;
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
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6MartialArtsController extends ControllerImpl<MartialArts> implements IMartialArtsController {

	private final static Logger logger = System.getLogger(SR6MartialArtsController.class.getPackageName()+".martial");

	//-------------------------------------------------------------------
	public SR6MartialArtsController(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<MartialArts> getAvailable() {
		List<MartialArts> ret = Shadowrun6Core.getItemList(MartialArts.class);
		for (MartialArtsValue tmp : getModel().getMartialArts()) {
			ret.remove(tmp.getResolved());
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(MartialArts data, Decision...dec) {
		// Is it already selected?
		for (MartialArtsValue tmp : getModel().getMartialArts()) {
			if (tmp.getResolved()==data)
				return new Possible(IRejectReasons.IMPOSS_ALREADY_PRESENT);
		}

		// Is there enough Karma
		if (getModel().getKarmaFree()<7) {
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);
		}

		// If in career mode, is there enough Nuyen
		if (getModel().isInCareerMode() && getModel().getNuyen()<2500) {
			return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN);
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMartialArtsController#canBeDeselected(de.rpgframework.shadowrun.AMartialArtsValue)
	 */
	@Override
	public Possible canBeDeselected(MartialArtsValue data) {
		if (!getModel().getMartialArts().contains(data)) {
			return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);
		}

		// Before removing a martial art, unlearn all techniques
		logger.log(Level.WARNING, "TODO: check for techniques to unlearn first");

		// In career mode, only removing
		if (getModel().isInCareerMode()) {
			boolean undoCareer = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER);
			boolean undoChargen= parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN);
			boolean addedInCareer = getModification(data)!=null;
			if (addedInCareer && !undoCareer)
				return Possible.FALSE;
			if (!addedInCareer && !undoChargen)
				return Possible.FALSE;
		}

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	protected DataItemModification getModification(MartialArtsValue key) {
		DataItemModification ret = null;
		for (Modification mod : getModel().getHistory()) {
			if (!(mod instanceof DataItemModification))
				continue;
			DataItemModification amod = (DataItemModification)mod;
			if (amod.getReferenceType()!=ShadowrunReference.MARTIAL_ART)
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
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<MartialArtsValue> select(MartialArts data, Decision...dec) {
		Possible poss = canBeSelected(data);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to select martial arts '%s', but cannot do that: {1}", data.getId(), poss.toString());
			return new OperationResult<>(poss);
		}

		Shadowrun6Character model = getModel();
		MartialArtsValue ret = new MartialArtsValue(data);
		model.addMartialArt(ret);
		if (model.isInCareerMode()) {
			logger.log(Level.INFO, "Add martial art '%s' for 7 Karma and 2500 Nuyen", data.getId());
			// Pay karma
			model.setKarmaFree(model.getKarmaFree() -7);
			model.setKarmaInvested(model.getKarmaInvested() +7);
			// Pay Nuyen
			model.setNuyen(model.getNuyen() -2500);

			DataItemModification mod = new DataItemModification(ShadowrunReference.MARTIAL_ART, data.getId());
			mod.setDate(Date.from(Instant.now()));
			mod.setExpCost(2500);
			model.addToHistory(mod);
		} else {
			logger.log(Level.INFO, "Add martial art '{0}'", data.getId());
		}

		parent.runProcessors();

		return new OperationResult<MartialArtsValue>(ret);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMartialArtsController#deselect(de.rpgframework.shadowrun.AMartialArtsValue)
	 */
	@Override
	public boolean deselect(MartialArtsValue data) {
		Possible poss = canBeDeselected(data);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to deselect martial arts '%s', but cannot do that: {1}", data.getKey(), poss.toString());
			return false;
		}

		Shadowrun6Character model = getModel();
		model.removeMartialArt(data);
		if (model.isInCareerMode()) {
			DataItemModification mod = getModification(data);
			if (mod!=null) {
				// Had been added in career
				// Grant karma
				model.setKarmaFree(model.getKarmaFree() +7);
				model.setKarmaInvested(model.getKarmaInvested() -7);
				// Grant Nuyen
				model.setNuyen(model.getNuyen() + mod.getExpCost());
				// Remove from history
				model.removeFromHistory(mod);
			}
		}
		logger.log(Level.INFO, "Removed martial arts '%s'", data.getKey());

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<MartialArtsValue> getSelected() {
		return getModel().getMartialArts();
	}

	@Override
	public RecommendationState getRecommendationState(MartialArts value) {
		return RecommendationState.NEUTRAL;
	}

	@Override
	public RecommendationState getRecommendationState(MartialArtsValue value) {
		return RecommendationState.NEUTRAL;
	}

	@Override
	public List<Choice> getChoicesToDecide(MartialArts value) {
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(MartialArts data) {
		return 7;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(MartialArts data) {
		return "7";
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IMartialArtsController#getTechniqueController(de.rpgframework.shadowrun6.MartialArtsValue)
	 */
	@Override
	public ITechniqueController getTechniqueController(MartialArtsValue style) {
		return new CommonTechniqueController(parent, style);
	}

}
