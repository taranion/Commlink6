package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.UUID;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;

/**
 * Clean up character from old mistakes / design decisions
 * @author prelle
 *
 */
public class FixCharacterStep implements ProcessingStep {

	private final static Logger logger = System.getLogger(FixCharacterStep.class.getPackageName()+".reset");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public FixCharacterStep(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {

		// Clean up empty flags
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
			item.clearEmptyFlags();
		}

		return unprocessed;
	}

}
