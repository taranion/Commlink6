package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyModificationsGeneric;

/**
 * @author prelle
 *
 */
public class SR6FreeQualityGenerator extends CommonQualityGenerator implements IQualityController {

	private final static Logger logger = System.getLogger(SR6FreeQualityGenerator.class.getPackageName()+".quality");

	public final static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	private int numberOfQualities;

	//-------------------------------------------------------------------
	public SR6FreeQualityGenerator(SR6CharacterGenerator parent) {
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

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.ComplexDataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(Quality value, Decision... decisions) {
		Possible poss = super.canBeSelected(value, decisions);
		if (!poss.get()) {
			if (poss.getMostSevere()==null) return poss;
			switch (poss.getMostSevere().getKey()) {
			case SR6RejectReasons.IMPOSS_QUALITY_ALREADY_6:
			case SR6RejectReasons.IMPOSS_QUALITY_KARMAGAIN:
				break;
			default:
				return poss;
			}
		}


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
				} else {
					logger.log(Level.ERROR, "Unknown choice ''{0}'' for choice {1}", dec.getValue(), dec.getChoiceUUID());
				}
			}
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

		return Possible.TRUE;
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
						karmaGain -= cost;
					} else {
						logger.log(Level.INFO, "Get {0} Karma for ''{1}''  (previous {2})", cost, item.getId(), model.getKarmaFree());
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
//				// Inject modifications from qualities
//				for (Modification mod : val.getEffectiveModifications(model)) {
//					logger.log(Level.DEBUG, "Add modification {0}", mod);
//					unprocessed.add(mod);
//				}
				// Done in GetModificationsFromQualities
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
		return Shadowrun6Core.getItemList(SR6Quality.class).stream()
				.filter(p -> parent.showDataItem(p))
//				.filter(p -> p.isFreeSelectable())
				.filter(p -> !model.hasQuality(p.getId()) || p.isMulti())
				.collect(Collectors.toList());
	}

}
