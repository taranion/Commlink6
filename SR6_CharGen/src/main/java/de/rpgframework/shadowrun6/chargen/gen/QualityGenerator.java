package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
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

	@Override
	public boolean canBeIncreased(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean increase(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeDecreased(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decrease(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
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
		
		
		if (karma>9)
			return new Possible(Severity.STOPPER, RES, SR6CharacterGenerator.IMPOSS_NOT_ENOUGH_KARMA);

		// If there are decisions open, don't allow selection
		if (!requiredChoices.isEmpty()) {
			// Convert open decisions into names or at least identifiers
			List<String> names = new ArrayList<>();
			requiredChoices.forEach(c -> names.add( 
					(c.getChooseFrom()==ShadowrunReference.SUBSELECT)?value.getChoiceName(c, Locale.getDefault()):String.valueOf(c.getChooseFrom())));
			return new Possible(Severity.WARNING, RES, SR6CharacterGenerator.IMPOSS_MISSING_DECISIONS,names);
		}
		
		return Possible.TRUE;
	}

	@Override
	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER select");
		
		try {
			Possible possible = canBeSelected(value, decisions);
			if (!possible.get()) {
				logger.log(Level.WARNING, "User tries to select {} but that is not possible because of {}", value, possible.getI18NKey());
				return new OperationResult<>(possible);
			}
			
			logger.log(Level.INFO, "select {} with {} decisions", value, decisions.length);
			
			QualityValue selected = new QualityValue(value,0);
			for (Decision dec : decisions)
				selected.addDecision(dec);
			getCharacterController().getModel().addQuality(selected);
			
			parent.runProcessors();
			return new OperationResult<QualityValue>(selected);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE select");
		}
	}

	@Override
	public Possible canBeDeselected(QualityValue value) {
		// TODO Auto-generated method stub
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public boolean deselect(QualityValue value) {
		// TODO Auto-generated method stub
		return false;
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
			SR6PrioritySettings prioSettings = getModel().getCharGenSettings(SR6PrioritySettings.class);
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
		return Shadowrun6Core.getItemList(Quality.class);
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
