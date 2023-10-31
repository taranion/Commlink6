package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.lvl.AQualityLeveller;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
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
	public float getSelectionCost(Quality data) {
		return data.getKarmaCost()*2;
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
		int cost = (int)getSelectionCost(value);
		OperationResult<QualityValue> res = super.select(value, decisions);
		if (res.wasSuccessful()) {
			// Add to history
			DataItemModification mod = new DataItemModification(ShadowrunReference.QUALITY, value.getId());
			mod.setDate(Date.from(Instant.now()));
			if (!value.isPositive()) cost=0;
			mod.setExpCost(cost);
			model.addToHistory(mod);

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

			parent.runProcessors();
		}
		return success;
	}

}
