package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 *
 */
public class SR6LifePathModuleGenerator extends ControllerImpl<LifepathModule>
		implements ComplexDataItemController<LifepathModule, LifepathModuleValue> {

	private int selectionsLeft;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6LifePathModuleGenerator(SR6LifepathCharacterGenerator parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<LifepathModule> getAvailable() {
		return Shadowrun6Core.getItemList(LifepathModule.class).stream()
			.filter(m -> parent.showDataItem(m))
			.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<LifepathModuleValue> getSelected() {
		return ((SR6LifepathCharacterGenerator) parent).getSettings().getModules();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(LifepathModule item) {
		if (item==null) return RecommendationState.NEUTRAL;
		// No recommender set, means no recommendation
		if (parent.getRecommender().isEmpty()) {
			return RecommendationState.NEUTRAL;
		}

		return parent.getRecommender().get().getRecommendationState(item);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(LifepathModuleValue value) {
		return getRecommendationState(value.getResolved());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(LifepathModule value) {
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(LifepathModule value, Decision... decisions) {
		if (value==null) return Possible.FALSE;

		return Shadowrun6Tools.checkDecisionsAndRequirements(getModel(), value, decisions);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<LifepathModuleValue> select(LifepathModule value, Decision... decisions) {
		logger.log(Level.DEBUG, "ENTER select({0})", value);
		try {
			Possible poss = canBeSelected(value, decisions);
			if (poss.getState()!=Possible.State.POSSIBLE) {
				logger.log(Level.WARNING, "Trying to select({0}) but not possible because {1}", value, poss.getMostSevere());
				return new OperationResult<>(poss);
			}

			LifepathModuleValue val = new LifepathModuleValue(value);
			for (Decision dec : decisions) {
				val.addDecision(dec);
			}
			((SR6LifepathCharacterGenerator) parent).getSettings().addModule(val);
			logger.log(Level.INFO, "Selected lifepath {1} module:{0}", value.getId(), value.getType());

			parent.runProcessors();
			return new OperationResult<LifepathModuleValue>(val);
		} finally {
			logger.log(Level.DEBUG, "LEAVE select({0})", value);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(LifepathModuleValue value) {
		if (!((SR6LifepathCharacterGenerator) parent).getSettings().getModules().contains(value)) {
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_NOT_PRESENT, value.getNameWithoutRating());
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(LifepathModuleValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(LifepathModule data) {
		return 1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		selectionsLeft = 0;
		List<Modification> unprocessed = new ArrayList<>();
		try {
			for (Modification tmp : previous) {
				switch ((ShadowrunReference)tmp.getReferenceType()) {
				case CREATION_POINTS:
					ValueModification valMod = (ValueModification)tmp;
					CreatePoints what = valMod.getResolvedKey();
					switch (what) {
					case LIFEPATH_MODULES:
						selectionsLeft += valMod.getValue();
						break;
					default:
						unprocessed.add(valMod);
					}
					break;
				default:
					unprocessed.add(tmp);
				}
			}
			logger.log(Level.DEBUG, "I can select {0} lifepath modules", selectionsLeft);

			// Now inject modifications from modules
			for (LifepathModuleValue modVal : getSelected()) {
				selectionsLeft--;
				LifepathModule module = modVal.getResolved();
				for (Modification tmp : module.getOutgoingModifications()) {
					if (tmp instanceof DataItemModification) {
						DataItemModification mod = (DataItemModification)tmp;
						if (mod.getConnectedChoice()!=null) {
							Choice choice = module.getChoice(mod.getConnectedChoice());
							Decision dec  = modVal.getDecision(choice.getUUID());
							List<Modification> toAdd = GenericRPGTools.decisionToModifications(mod, choice, dec);
							logger.log(Level.INFO, "For {0} decision {1} for {2} leads to {3}",modVal.getKey(), dec.getValue(), dec.getChoiceUUID(), toAdd);
							unprocessed.addAll(toAdd);
						} else {
							unprocessed.add(tmp);
						}
					}
				}

			}

		} finally {

		}

		return unprocessed;
	}

}
