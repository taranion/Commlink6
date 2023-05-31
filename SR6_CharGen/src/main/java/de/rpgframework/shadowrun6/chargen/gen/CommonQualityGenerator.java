package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.SetItem;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.QualityGenerator;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyModificationsGeneric;

/**
 * @author prelle
 *
 */
public class CommonQualityGenerator extends QualityGenerator<Shadowrun6Character> implements IQualityController {

	private final static Logger logger = System.getLogger(CommonQualityGenerator.class.getPackageName()+".quality");

	public final static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	private int numberOfQualities;

	//-------------------------------------------------------------------
	public CommonQualityGenerator(SR6CharacterGenerator parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public int getNumberOfQualities() { return numberOfQualities; }

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.NumericalDataItemController#canBeIncreased(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	public Possible canBeIncreased(QualityValue value) {
		Possible poss = super.canBeIncreased(value);
		if (!poss.get())
			return poss;

		// For previously not user-selected qualities, ensure limit is not reached yet
		if (value.getDistributed()==0 && numberOfQualities>=6) {
			// Already 6 qualities
			return new Possible(Severity.STOPPER, RES, SR6RejectReasons.IMPOSS_QUALITY_ALREADY_6);
		}

		return Possible.TRUE;
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
		Possible poss = super.canBeSelected(value, decisions);
		if (!poss.get())
			return poss;

		// Check if all requirements are met
		List<Requirement> notMet = new ArrayList<>();
		for (Requirement req : value.getRequirements()) {
			try {
				if (!Shadowrun6Tools.isRequirementMet(model, value, req, decisions)) {
					notMet.add(req);
				}
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error in quality "+value.getId(),e);
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (notMet.size()>0) {
			return new Possible(notMet, (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
		}


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
					logger.log(Level.ERROR, "Unknown choice ''{0}'' for choice {1}", dec.getValue(), dec.getChoiceUUID());
				}
			}
		}

		if (value.isPositive() && karma>model.getKarmaFree()) {
			return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, karma);
		}
		if (!value.isPositive() && (karmaGain+karma)>20) {
			return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_QUALITY_KARMAGAIN, karma);
		}
		if (value.getType()==QualityType.METAGENIC && (karmaSURGE+Math.abs(karma))>30) {
			return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_QUALITY_KARMASURGE, karmaSURGE);
		}

		/* If a quality resolves to a ComplexDataItem, check for choices there too */
		for (Decision dec : decisions) {
			if (dec==null) continue;
			Choice choice = value.getChoice( dec.getChoiceUUID() );
			if (choice==null) continue;
			switch ( (ShadowrunReference)choice.getChooseFrom()) {
			case SUBSELECT:
			case TEXT:
				continue;
			}
			Object item = choice.getChooseFrom().resolve(dec.getValue());
			if (item instanceof ComplexDataItem) {
				outer:
				for (Choice tmpC : ((ComplexDataItem)item).getChoices()) {
					for (Decision dec2 : decisions) {
						if (dec2.getChoiceUUID().equals(tmpC.getUUID())) {
							continue outer;
						}
					}
					requiredChoices.add(tmpC);
				}
			}
		}

		// If there are decisions open, don't allow selection
		if (!requiredChoices.isEmpty()) {
			// Convert open decisions into names or at least identifiers
			List<String> names = new ArrayList<>();
			requiredChoices.forEach(c -> names.add(
					(c.getChooseFrom()==ShadowrunReference.SUBSELECT)?value.getChoiceName(c, Locale.getDefault()):String.valueOf(c.getChooseFrom())));
			return new Possible(Severity.WARNING, IRejectReasons.RES, IRejectReasons.IMPOSS_MISSING_DECISIONS,names);
		}

		// Is Karma gain >20 and there is no chance to prevent gaining to much Karma
		int cost = value.getKarmaCost();
		if (!value.isPositive() && ((karmaGain+cost)>20) && numberOfQualities>4)
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_QUALITY_KARMAGAIN);

		// No more than 6 user-selected qualities
		if (numberOfQualities>=6) {
			// Already 6 qualities
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_QUALITY_ALREADY_6);
		}

		return Possible.TRUE;
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.ComplexDataItem, de.rpgframework.genericrpg.data.Decision[])
//	 */
//	@Override
//	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
//		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER select");
//
//		try {
//			Possible possible = canBeSelected(value, decisions);
//			if (!possible.get()) {
//				logger.log(Level.WARNING, "User tries to select {0} but that is not possible because of {1}", value, possible.getI18NKey());
//				return new OperationResult<>(possible);
//			}
//			if (possible.getMostSevere()!=null && possible.getMostSevere().getSeverity()!=Severity.INFO) {
//				possible.setState(State.IMPOSSIBLE);
//				logger.log(Level.WARNING, "User tries to select {0} but that is not possible because of {1}", value, possible.getI18NKey());
//				return new OperationResult<>(possible);
//			}
//
//			return super.select(value, decisions);
//		} finally {
//			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE select");
//		}
//	}

	//-------------------------------------------------------------------
	private void calculateKarmaSURGE() {
		karmaSURGE = 0;
		for (QualityValue val : model.getQualities()) {
			if (val.isAutoAdded())
				continue;
			Quality item = val.getResolved();
			if (item.getType()!=QualityType.METAGENIC)
				continue;
			karmaSURGE += val.getKarmaCost();
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
			karmaGain = 0;
			karmaSURGE = 0;
			// Reset
			todos.clear();
			numberOfQualities=0;
//			reset();

			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.QUALITY) {
					logger.log(Level.INFO, "Consume "+tmp);
					ApplyModificationsGeneric.applyModification(model, tmp);
				} else {
					unprocessed.add(tmp);
				}
			}

			// Pay Karma for SURGE collective
			if (model.getSurgeCollective()!=null) {
				SetItem collective = model.getSurgeCollective().getResolved();
				int cost = collective.getCost();
				logger.log(Level.INFO, "Pay {0} Karma for SURGE collective ''{1}''", cost, collective.getId());
				model.setKarmaFree(model.getKarmaFree() - cost);
			}

			// Pay or grant Karma for qualities
			for (QualityValue val : model.getQualities()) {
				Quality item = val.getModifyable();
				int cost = val.getKarmaCost();
//				if (item.hasLevel())
//					cost *= val.getDistributed();
//				else if (val.isAutoAdded())
//					cost = 0;
				if (cost != 0) {
					if (item.isPositive()) {
						if (item.hasLevel()) {
							logger.log(Level.INFO, "Pay {0} Karma for ''{1}'' on level {2}", cost, item.getId(), val.getDistributed());
						} else {
							logger.log(Level.INFO, "Pay {0} Karma for ''{1}''", cost, item.getId());
						}
						model.setKarmaFree(model.getKarmaFree() - cost);
						karmaGain -= cost;
					} else {
						logger.log(Level.INFO, "Get {0} Karma for ''{1}''  (previous {2})", cost, item.getId(), model.getKarmaFree());
						model.setKarmaFree(model.getKarmaFree() + cost);
						karmaGain += cost;
					}
				}

				if (val.isAutoAdded()) {
					if (val.getDistributed()>0) {
						numberOfQualities++;
					}
				} else
					numberOfQualities++;

				calculateKarmaSURGE();
				// Done in GetModificationsFromQualities
			}

			// Error conditions
			if (karmaGain>20) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, SR6RejectReasons.TODO_QUALITY_KARMAGAIN));
				logger.log(Level.WARNING, "Gained more than 20 Karma ({0})", karmaGain);
			}
			if (karmaSURGE>30) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, SR6RejectReasons.TODO_QUALITY_KARMASURGE));
				logger.log(Level.WARNING, "Spent more than 30 Karma ({0}) on SURGE", karmaSURGE);
			}
			if (numberOfQualities>6) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, SR6RejectReasons.TODO_QUALITY_TOO_MANY));
				logger.log(Level.WARNING, "Added more than 6 qualities ({0})", numberOfQualities);
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
		return Shadowrun6Core.getItemList(Quality.class).stream()
				.filter(p -> parent.showDataItem(p))
				.filter(p -> p.isFreeSelectable())
				.filter(p -> !model.hasQuality(p.getId()) || p.isMulti())
				.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<QualityValue> getSelected() {
		return model.getQualities();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(Quality data) {
		return data.getKarmaCost();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(Quality data) {
		return String.valueOf(getSelectionCostString(data));
	}

}
