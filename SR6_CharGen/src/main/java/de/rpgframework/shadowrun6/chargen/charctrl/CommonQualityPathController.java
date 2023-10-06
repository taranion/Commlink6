package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathStep;
import de.rpgframework.shadowrun6.QualityPathStepValue;
import de.rpgframework.shadowrun6.QualityPathValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;

/**
 * @author stefa
 *
 */
public class CommonQualityPathController extends ControllerImpl<QualityPath> implements IQualityPathController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public CommonQualityPathController(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<QualityPath> getAvailable() {
		List<QualityPath> ret = Shadowrun6Core.getItemList(QualityPath.class);
		// Remove those already selected
		for (QualityPathValue tmp : getModel().getQualityPaths()) {
			QualityPath picked = tmp.getResolved();
			ret.remove(picked);
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<QualityPathValue> getSelected() {
		return getModel().getQualityPaths();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(QualityPath value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(QualityPathValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(QualityPath value) {
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(QualityPath value, Decision... decisions) {
		return Shadowrun6Tools.checkDecisionsAndRequirements(getModel(), value, decisions);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<QualityPathValue> select(QualityPath value, Decision... decisions) {
		Possible poss = canBeSelected(value, decisions);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to select quality path ''{0}'', but that is not allowed due to: {1}", value.getId(), poss.getMostSevere());
			return new OperationResult<>(poss);
		}

		QualityPathValue qVal = new QualityPathValue(value);
		getModel().addQualityPath(qVal);
		logger.log(Level.INFO, "Added quality path: "+value);

		parent.runProcessors();

		return new OperationResult<QualityPathValue>(qVal);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(QualityPathValue value) {
		if (getModel().getQualityPaths().contains(value))
			return Possible.FALSE;
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(QualityPathValue value) {
		Possible poss = canBeDeselected(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to deselect quality path ''{0}'', but that is not allowed due to: {1}", value.getKey(), poss.getMostSevere());
			return false;
		}

		getModel().removeQualityPath(value);
		logger.log(Level.INFO, "Removed quality path: "+value);

		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(QualityPath data) {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		logger.log(Level.DEBUG, "process");
		return unprocessed;
	}

	@Override
	public int getKarmaBalance() {
		// TODO Auto-generated method stub
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IQualityPathController#canBeSelected(de.rpgframework.shadowrun6.QualityPathValue, de.rpgframework.shadowrun6.QualityPathStep, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(QualityPathValue path, QualityPathStep step, Decision... decisions) {
		if (path.hasStepTaken(step)) {
			logger.log(Level.WARNING, "cannot select "+step+" , because already selected");
			return Possible.FALSE;
		}

		// Find possible next steps from current position
		List<QualityPathStep> possible = new ArrayList<>();
		if (path.getStepsTaken().isEmpty()) {
			// If no steps has taken yet, the first is the only one
			possible.add( path.getResolved().getSteps().get(0));
		} else {
			// Find the last step taken and check its next steps
			QualityPathStepValue lastV = path.getStepsTaken().get(path.getStepsTaken().size()-1);
			for (String stepID : lastV.getResolved().getNextSteps()) {
				QualityPathStep next = path.getResolved().getStep(stepID);
				if (next==null) {
					logger.log(Level.ERROR, "Quality path ''{0}'' references unknown next step ''{1}''", path.getKey(), stepID);
				} else {
					possible.add(next);
				}
			}
		}

		// Now that we know possible next steps, check if requested step is among them
		if (!possible.contains(step)) {
			logger.log(Level.WARNING, "cannot select "+step+" , because not among next steps ("+possible+")");
			return new Possible(IRejectReasons.IMPOSS_NOT_AVAILABLE);
		}

		// Check if requirements - if present - are met
		Possible poss = Shadowrun6Tools.areRequirementsMet(getModel(), step, decisions);
		if (!poss.get()) {
			logger.log(Level.WARNING, "cannot select "+step+" , because requirement not met. "+poss);
			return poss;
		}

		// TODo: Check for enough Karma
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IQualityPathController#select(de.rpgframework.shadowrun6.QualityPathValue, de.rpgframework.shadowrun6.QualityPathStep, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<QualityPathStepValue> select(QualityPathValue path, QualityPathStep step,
			Decision... decisions) {
		Possible poss = canBeSelected(path, step, decisions);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to select unselectable quality path ''{0}'': {1}", step.getId(), poss.toString());
			return new OperationResult<>(poss);
		}

		QualityPathStepValue stepVal = new QualityPathStepValue(step);
		path.addStepTaken(stepVal);
		logger.log(Level.INFO, "Took step on quality path ''{0}'': {1}", path.getResolved().getId(), step.getId());

		// Pay
		//parent.getQualityController().getSelectionCost(null);


		return new OperationResult<>(stepVal);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.IQualityPathController#canBeDeselected(de.rpgframework.shadowrun6.QualityPathValue, de.rpgframework.shadowrun6.QualityPathStepValue)
	 */
	@Override
	public Possible canBeDeselected(QualityPathValue path, QualityPathStepValue step) {
		if (!path.getStepsTaken().contains(step)) {
			return Possible.FALSE;
		}
		return Possible.TRUE;
	}

	@Override
	public OperationResult<QualityPathStepValue> deselect(QualityPathValue path, QualityPathStepValue step) {
		// TODO Auto-generated method stub
		return null;
	}

}
