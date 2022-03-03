package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.IAttribute;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class CommonEquipmentController extends ControllerImpl<ItemTemplate> implements IEquipmentController {

	//-------------------------------------------------------------------
	public CommonEquipmentController(SR6CharacterController parent) {
		super(parent);
	}

	@Override
	public List<ItemTemplate> getAvailable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CarriedItem<ItemTemplate>> getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecommendationState getRecommendationState(ItemTemplate value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecommendationState getRecommendationState(CarriedItem<ItemTemplate> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Choice> getChoicesToDecide(ItemTemplate value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(ItemTemplate value, Decision... decisions) {
		// Ensure all choices are made
		for (Choice choice : value.getChoices()) {
			boolean choiceNotFound = true;
			for (Decision dec : decisions) {
				if (dec.getChoiceUUID()==choice.getUUID()) {
					choiceNotFound = false;
					// ToDo: validate choice value
					break;
				}
			}
			if (choiceNotFound)
				return new Possible(false, "Choice "+choice.getUUID()+" missing");
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, Decision... decisions) {
		logger.log(Level.TRACE, "ENTER select({0}, {1}", value, List.of(decisions));
		try {
			Possible poss = canBeSelected(value, decisions);
			if (!poss.get()) {
				logger.log(Level.ERROR, "Trying to select {0} which may not be selected: {1}", value, poss.toString());
				return new OperationResult<>(poss);
			}

			CarriedItem<ItemTemplate> item = new CarriedItem<ItemTemplate>(value, null);
			item.getDecisions().addAll(List.of(decisions));
			
			logger.log(Level.INFO, "Add {0} to model", item.toString());
			getModel().addCarriedItem(item);
			
			return new OperationResult<CarriedItem<ItemTemplate>>(item);
		} finally {
			logger.log(Level.TRACE, "LEAVE select({0}, {0}", value, List.of(decisions));
		}
	}

	@Override
	public Possible canBeDeselected(CarriedItem<ItemTemplate> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deselect(CarriedItem<ItemTemplate> value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return unprocessed;
	}

	@Override
	public float getSelectionCost(ItemTemplate data) {
		// TODO Auto-generated method stub
		return data.getAttribute(SR6ItemAttribute.PRICE).getDistributed();
	}

}
