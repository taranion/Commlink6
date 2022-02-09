package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyQualityModifications;

/**
 * @author prelle
 *
 */
public class QualityGenerator extends ControllerImpl<Quality> implements IQualityController {

	public final static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;
	
	private int karmaGain;
	private int numberOfQualities;

	//-------------------------------------------------------------------
	public QualityGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.ComplexDataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(Quality value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(QualityValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#canBeIncreased(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean canBeIncreased(QualityValue value) {
		if (!getModel().getQualities().contains(value)) {
			return false; //new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_NOT_PRESENT);			
		}
		
		Quality qual = value.getModifyable();
		if (!qual.hasLevel())
			// Quality has no levels
			return false;
		
		if (value.getModifiedValue()>=qual.getMax())
			// Maximum already reached
			return false;
		
		// Is there enough Karma
		if (getModel().getKarmaFree() < qual.getKarmaCost()) {
			// Not enough Karma
			return false;
		}
		
		// For previously not user-selected qualities, ensure limit is not reached yet
		if (value.getDistributed()==0 && numberOfQualities>=6) {
			// Already 6 qualities
			return false;
		}
		
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#increase(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean increase(QualityValue value) {
		logger.log(Level.TRACE, "ENTER increase({})", value);
		try {
			if (canBeIncreased(value)) {
				return false;			
			}
			
			// Do increase
			value.setDistributed(value.getDistributed()+1);
			logger.log(Level.INFO, "increased quality '{}' to {}", value.getModifyable().getId(), value.getDistributed());
			
			parent.runProcessors();
			
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE increase({})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#canBeDecreased(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean canBeDecreased(QualityValue value) {
		if (!getModel().getQualities().contains(value)) {
			return false; //new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_NOT_PRESENT);			
		}
		
		if (value.getDistributed()<=0) {
			// Already at lowest - if it is still present, than it must be by modification
			return false;
		}
		
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#decrease(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean decrease(QualityValue value) {
		logger.log(Level.TRACE, "ENTER decrease({})", value);
		try {
			if (canBeDecreased(value)) {
				return false;			
			}
			
			// Do increase
			value.setDistributed(value.getDistributed()-1);
			logger.log(Level.INFO, "decreased quality '{}' to {}", value.getModifyable().getId(), value.getDistributed());
			if (value.getDistributed()==0 && value.getModifiedValue()==0) {
				getModel().removeQuality(value);
			}
			
			parent.runProcessors();
			
			return true;
		} finally {
			logger.log(Level.TRACE, "LEAVE decrease({})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.ComplexDataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(Quality value) {
		return new ArrayList<Choice>( value.getChoices() );
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.ComplexDataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(Quality value, Decision... decisions) {
		int karma = value.getKarmaCost();
		List<Choice> requiredChoices = value.getChoices();
		for (Decision dec : decisions) {
			logger.log(Level.INFO, "Decision "+dec);
			if (dec==null) continue;
			Choice choice = value.getChoice( dec.getChoiceUUID() );
			// If we found 
			if (choice!=null) requiredChoices.remove(choice);
			if (choice!=null && choice.getChooseFrom()==ShadowrunReference.SUBSELECT) {
				ChoiceOption subOpt = choice.getSubOption(dec.getValue());
				if (subOpt!=null) {
					karma += subOpt.getCost();
				} else {
					logger.log(Level.ERROR, "Unknown choice '{}' for choice {}", dec.getValue(), dec.getChoiceUUID());
				}
			}
		}
		
		// Does the character have enough Karma
		if (karma>getModel().getKarmaFree())
			return new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_NOT_ENOUGH_KARMA);

		// If there are decisions open, don't allow selection
		if (!requiredChoices.isEmpty()) {
			// Convert open decisions into names or at least identifiers
			List<String> names = new ArrayList<>();
			requiredChoices.forEach(c -> names.add( 
					(c.getChooseFrom()==ShadowrunReference.SUBSELECT)?value.getChoiceName(c, Locale.getDefault()):String.valueOf(c.getChooseFrom())));
			return new Possible(Severity.WARNING, RES, SR6CharacterGenerator.IMPOSS_MISSING_DECISIONS,names);
		}
		
		// Is Karma gain >20
		int cost = value.getKarmaCost();
		if (!value.isPositive() && ((karmaGain+cost)>20)) 
			return new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_QUALITY_KARMAGAIN);
		
		// No more than 6 user-selected qualities
		if (numberOfQualities>=6) {
			// Already 6 qualities 
			return new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_QUALITY_ALREADY_6);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.ComplexDataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER select");
		
		try {
			Possible possible = canBeSelected(value, decisions);
			if (!possible.get()) {
				logger.log(Level.WARNING, "User tries to select {} but that is not possible because of {}", value, possible.getI18NKey());
				return new OperationResult<>(possible);
			}
			if (possible.getMostSevere()!=null && possible.getMostSevere().getSeverity()!=Severity.INFO) {
				possible.setState(State.IMPOSSIBLE);
				logger.log(Level.WARNING, "User tries to select {} but that is not possible because of {}", value, possible.getI18NKey());
				return new OperationResult<>(possible);
			}
			
			// Ensure all required choices are made
			List<UUID> requiredChoices = new ArrayList<>();
			value.getChoices().forEach(c -> requiredChoices.add(c.getUUID()));
			
			logger.log(Level.INFO, "select {} with {} decisions", value, decisions.length);
			
			QualityValue selected = new QualityValue(value,0);
			for (Decision dec : decisions) {
				selected.addDecision(dec);
				requiredChoices.remove(dec.getChoiceUUID());
			}
			if (!requiredChoices.isEmpty()) {
				
			}
			
			getCharacterController().getModel().addQuality(selected);
			
			parent.runProcessors();
			return new OperationResult<QualityValue>(selected);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE select");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public Possible canBeDeselected(QualityValue value) {
		// Is the quality present in the character at all?
		if (!getModel().getQualities().contains(value)) {
			return new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_NOT_PRESENT);
		}
		// Is it auto-added by other circumstances
		if (value.isAutoAdded()) {
			return new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_AUTO_ADDED);
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean deselect(QualityValue value) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER deselect");
		try {
			if (!canBeDeselected(value).get())
				return false;

			getModel().removeQuality(value);
			logger.log(Level.INFO, "remove quality '{}'", value.getModifyable().getId());

			parent.runProcessors();
			return true;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE deselect");
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

		try {
			Shadowrun6Character model = getModel();
			karmaGain = 0;
			// Reset
//			reset();

			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.QUALITY) {
					logger.log(Level.INFO, "Consume "+tmp);
					ApplyQualityModifications.applyModification(getModel(), tmp);
				} else {
					unprocessed.add(tmp);
				}
			}
			
			// Pay or grant Karma for qualities
			for (QualityValue val : getModel().getQualities()) {
				logger.log(Level.DEBUG, "Quality "+val);
				Quality item = val.getModifyable();
				int cost = item.getKarmaCost();
				if (item.hasLevel())
					cost *= val.getDistributed();
				if (item.isPositive()) {
					logger.log(Level.INFO, "Pay {} Karma for '{}'", cost, item.getId());
					model.setKarmaFree( model.getKarmaFree() - cost);
					karmaGain -= cost;
				} else {
					logger.log(Level.INFO, "Get {} Karma for '{}'", cost, item.getId());
					model.setKarmaFree( model.getKarmaFree() + cost);
					karmaGain += cost;
				}
			}
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<Quality> getAvailable() {
		logger.log(Level.WARNING,"ToDo: getAvailable()");
		List<Quality> ret = new ArrayList<>(Shadowrun6Core.getItemList(Quality.class));
		// Remove those already present in character and not allowed for taking multiple times
		ret = ret.stream().filter(q -> !getModel().hasQuality(q.getId()) || q.isMulti()).collect(Collectors.toList());
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<QualityValue> getSelected() {
		return getCharacterController().getModel().getQualities();
	}

}
