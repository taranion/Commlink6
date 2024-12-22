package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.AItemEnhancement;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;

/**
 * @author prelle
 *
 */
public class CommonItemEnhancementController extends ControllerImpl<SR6ItemEnhancement>
		implements
		ComplexDataItemController<SR6ItemEnhancement, ItemEnhancementValue<SR6ItemEnhancement>> {

	protected static Logger logger = System.getLogger(ControllerImpl.class.getPackageName()+".enhance");

	private CarriedItem<ItemTemplate> toModify;

	//-------------------------------------------------------------------
	/**
	 */
	public CommonItemEnhancementController(SR6CharacterController parent, CarriedItem<ItemTemplate> toModify) {
		super(parent);
		this.toModify = toModify;
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
	private List<SR6ItemEnhancement> getEnhancementsIn() {
		List<SR6ItemEnhancement> ret = new ArrayList<SR6ItemEnhancement>();
		for (ItemEnhancementValue<AItemEnhancement> tmp : toModify.getEnhancements()) {
			ret.add((SR6ItemEnhancement) tmp.getModifyable());
		}

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<SR6ItemEnhancement> getAvailable() {
		List<SR6ItemEnhancement> ret = new ArrayList<>();
		for (SR6ItemEnhancement enh : Shadowrun6Core.getItemEnhancements(getModel(), toModify.getResolved())) {
			if (getEnhancementsIn().contains(enh))
				continue;
			if (enh.isSelectableByModificationOnly())
				continue;

			ret.add(enh);
		}
		logger.log(Level.WARNING, "STOP : getAvailableEnhancementsFor returns "+ret.size()+" elements");
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<ItemEnhancementValue<SR6ItemEnhancement>> getSelected() {
		return toModify.getEnhancements();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(SR6ItemEnhancement value) {
		// TODO Auto-generated method stub
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(ItemEnhancementValue<SR6ItemEnhancement>e) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(SR6ItemEnhancement value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(SR6ItemEnhancement value, Decision... decisions) {
		ItemAttributeNumericalValue<SR6ItemAttribute> val = toModify.getAsValue(SR6ItemAttribute.MODIFICATION_SLOTS);
		if (val==null) {
			logger.log(Level.ERROR, "No information about modification slots for {0}", toModify);
			return Possible.FALSE;
		}
		// Enough space
		if (val.getModifiedValue() < (toModify.getModificationSlotsUsed()+value.getSize()))
			return new Possible(IRejectReasons.IMPOSS_CAPACITY);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<ItemEnhancementValue<SR6ItemEnhancement>> select(SR6ItemEnhancement value, Decision... decisions) {
		logger.log(Level.INFO, "Select "+value);
		Possible poss = canBeSelected(value, decisions);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to select an ItemEnhancement that may not be selected: "+poss);
			return new OperationResult<>(poss);
		}

		ItemEnhancementValue<SR6ItemEnhancement> iVal = new ItemEnhancementValue<SR6ItemEnhancement>(value);
		toModify.addEnhancement(iVal);
		logger.log(Level.INFO, "Added ItemEnhancement {0} to {1}", iVal, toModify);

		SR6GearTool.recalculate("", getModel(), toModify);
		parent.runProcessors();

		return new OperationResult<ItemEnhancementValue<SR6ItemEnhancement>>(iVal);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(ItemEnhancementValue<SR6ItemEnhancement> value) {
		boolean found = toModify.getEnhancements().contains(value);
		if (found) return Possible.TRUE;
		logger.log(Level.WARNING, "Enhancement {0} is not part of {1}", value, toModify.getEnhancements());
		return new Possible(IRejectReasons.IMPOSS_NOT_PRESENT);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(ItemEnhancementValue<SR6ItemEnhancement> value) {
		logger.log(Level.INFO, "Deselect "+value);
		Possible poss = canBeDeselected(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to deselect an ItemEnhancement that may not be deselected: "+poss);
			return false;
		}

		toModify.removeEnhancement(value);
		logger.log(Level.INFO, "Removed ItemEnhancement {0} from {1}", value, toModify);

		SR6GearTool.recalculate("", getModel(), toModify);
		logger.log(Level.INFO, "Done recalculating {0}", toModify);
		parent.runProcessors();

		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public float getSelectionCost(SR6ItemEnhancement data, Decision... decisions) {
		return data.getPrice();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCostString(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public String getSelectionCostString(SR6ItemEnhancement data) {
		return String.valueOf(data.getPrice());
	}

}
