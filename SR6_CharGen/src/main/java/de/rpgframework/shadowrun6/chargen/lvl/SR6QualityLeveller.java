package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.ApplyableValueModification;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.lvl.AQualityLeveller;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6QualityLeveller extends AQualityLeveller<Shadowrun6Character> {

	//-------------------------------------------------------------------
	protected SR6QualityLeveller(IShadowrunCharacterController<?, ?, ?,Shadowrun6Character> parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<Quality> getAvailable() {
		return Shadowrun6Core.getItemList(SR6Quality.class).stream()
				.filter(p -> parent.showDataItem(p))
				.filter(p -> !model.hasQuality(p.getId()) || p.isMulti())
				.filter(p -> p.getType()!=QualityType.QUALITY_PATH)
				.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(Quality value) {
		return new ArrayList<Choice>( value.getChoices() );
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(Quality data, Decision... decisions) {
		QualityValue fake = new QualityValue(data,0);
		for (Decision dec : decisions) fake.addDecision(dec);
		int cost = fake.getKarmaCost();
		
		if (data.isNoDouble())
			return cost;
		return cost;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(Quality data) {
		return String.valueOf(getSelectionCost(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		todos.clear();
		// Check if requirements of qualities are still met
		for (QualityValue val : model.getQualities()) {
			Quality q = val.getResolved();
			for (Requirement req : q.getRequirements()) {
				Possible poss = Shadowrun6Tools.areRequirementsMet(model, q, val.getDecisionArray());
				if (!poss.get()) {
					logger.log(Level.WARNING, "Requirement of {0} not met: {1}", val.getName(), req);
					todos.add(new ToDoElement(Severity.WARNING, q.getName(parent.getLocale())+": "+Shadowrun6Tools.getRequirementString(req, parent.getLocale())));
				}
			}
		}

		return unprocessed;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.ComplexDataItem,
	 *      de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0})", value);
		int cost = (int)getSelectionCost(value, decisions);
		OperationResult<QualityValue> res = super.select(value, decisions);
		if (res.wasSuccessful()) {
			// Add to history
			cost = res.get().getKarmaCost();
			DataItemModification mod = new DataItemModification(ShadowrunReference.QUALITY, value.getId());
			mod.setDate(Date.from(Instant.now()));
			if (!value.isPositive()) cost=0;
			mod.setExpCost(cost);
			model.addToHistory(mod);
			
			// Special handling for "Latent Awakening" (6WC) to mark the moment magic was set to 0
			if (value.getId().equals("latent_awakening")) {
				logger.log(Level.WARNING, "****Mark essence loss for MAG/RES");
				int val = model.getAttribute(ShadowrunAttribute.ESSENCE).getModifiedValue();
				model.setEssenceLossZero(val);
			}

			Shadowrun6Tools.recordEssenceChange(model, value);
			
			parent.runProcessors();
		}
		return res;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(QualityValue value) {
		logger.log(Level.TRACE, "ENTER select({0})", value);
		int cost = (int)getSelectionCost(value.getResolved());
		boolean success = super.deselect(value);
		if (success) {
			// Add to history
			DataItemModification mod = new DataItemModification(ShadowrunReference.QUALITY, value.getKey());
			mod.setDate(Date.from(Instant.now()));
			if (value.getResolved().isPositive()) cost=0;
			mod.setExpCost(cost);
			model.addToHistory(mod);
			
			// Special handling for "Latent Awakening" (6WC) to mark the moment magic was set to 0
			if (value.getModifyable().getId().equals("latent_awakening")) {
				model.setMagicOrResonanceType(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane"));
				model.setEssenceLossZero(null);
			}

			Shadowrun6Tools.removeEssenceChange(model, value, RemoveMode.UNDO);

			parent.runProcessors();
		}
		return success;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<QualityValue> increase(QualityValue value) {
		logger.log(Level.TRACE, "ENTER increase({0})", value);
		try {
			Possible poss = canBeIncreased(value);
			if (!poss.get()) {
				logger.log(Level.ERROR, "Tried to increase {0} which is not allowed: {1}", value, poss.toString());
				return new OperationResult<QualityValue>(poss);
			}

			value.setDistributed(value.getDistributed()+1);
			logger.log(Level.INFO, "increased quality ''{0}'' to {1}", value.getModifyable().getId(), value.getDistributed());

			Shadowrun6Tools.recordEssenceChange(model, value.getResolved());

			parent.runProcessors();

			return new OperationResult<QualityValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE increase({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<QualityValue> decrease(QualityValue value) {
		logger.log(Level.TRACE, "ENTER decrease({0})", value);
		try {
			Possible poss = canBeDecreased(value);
			if (!poss.get()) {
				logger.log(Level.ERROR, "Tried to decrease {0} which is not allowed: {1}", value, poss.toString());
				return new OperationResult<QualityValue>(poss);
			}

			value.setDistributed(value.getDistributed()-1);
			logger.log(Level.INFO, "decreased quality '{0}' to {1}", value.getModifyable().getId(), value.getDistributed());

			Shadowrun6Tools.removeEssenceChange(model, value.getResolved(), RemoveMode.UNDO);

			parent.runProcessors();

			return new OperationResult<QualityValue>(value);
		} finally {
			logger.log(Level.TRACE, "LEAVE decrease({0})", value);
		}
	}

}
