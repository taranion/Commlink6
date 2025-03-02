package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;

/**
 * 
 */
public class SR6ShifterGenerator extends ControllerImpl<Quality> implements
		ComplexDataItemController<Quality, QualityValue>, NumericalValueController<Quality, QualityValue> {

	private final static String SHIFTER_QUALITY_ID="shifter";
	
	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6ShifterGenerator(SR6CharacterGenerator parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public int getMinimalKarmaCost() {
		if (getModel().getQuality(SHIFTER_QUALITY_ID)==null) return 0;
		return getModel().getQuality(SHIFTER_QUALITY_ID).getKarmaCost();
	}

	//-------------------------------------------------------------------
	public int getCurrentKarmaCost() {
		System.err.println("SR6ShifterGenerator.getCurrentKarmaCost: Addons="+getModel().getShifterAddOns());
		return getModel().getShifterAddOns().stream()
				.map(qv -> {
					System.err.println("Karmacost of "+qv+" is "+qv.getKarmaCost()+" / "+qv.getModifiedValue());
					return qv;})
				.map(qv -> qv.getKarmaCost())
				.reduce(0, Integer::sum);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		logger.log(Level.WARNING, "process shifter");
		
		if (getModel().getBodytype()!=BodyType.SHAPESHIFTER) {
			// Remove an eventually existing shifter property
			if (getModel().hasQuality(SHIFTER_QUALITY_ID)) {
				getModel().removeQuality( getModel().getQuality(SHIFTER_QUALITY_ID));
			}
		} else {
			if (!getModel().hasQuality(SHIFTER_QUALITY_ID)) {
				QualityValue shifter = getModel().getQuality(SHIFTER_QUALITY_ID);
				
//				getModel().addQuality(shifterQual);
			}
		}
		return unprocessed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<Quality> getAvailable() {
		logger.log(Level.WARNING, "getAvailable()");
		return Shadowrun6Core.getItemList(SR6Quality.class).stream()
			.filter(q -> q.getType()==QualityType.SHIFTER)
			.filter(q -> getModel().getShifterAuto().stream().map(qv -> qv.getResolved()).anyMatch(x -> x!=q))
			.map(q -> (Quality)q)
			.toList();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<QualityValue> getSelected() {
		logger.log(Level.WARNING, "getSelected()");
		QualityValue baseShifter = getModel().getQuality(SHIFTER_QUALITY_ID);
		if (baseShifter==null)
			return List.of();
		
		List<QualityValue> ret = new ArrayList<>();
		ret.addAll(getModel().getShifterAuto());		
		ret.addAll(getModel().getShifterAddOns());
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(Quality value, Decision... decisions) {
		QualityValue baseShifter = getModel().getQuality("shifter");
		if (baseShifter==null)
			return Possible.FALSE;
		
		if (value.getType()!=QualityType.SHIFTER)
			return Possible.FALSE;
		
		// Reject qualities that the user already selected actively
		if (getModel().getShifterAddOns().stream().anyMatch(qv -> qv.getResolved()==value)) 
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_ALREADY_PRESENT);
		
		// Reject qualities that has been added automatically
		if (getModel().getShifterAuto().stream().anyMatch(qv -> qv.getResolved()==value)) 
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_AUTO_ADDED);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<QualityValue> select(Quality value, Decision... decisions) {
		logger.log(Level.INFO, "SELECT "+value);
		Possible poss = canBeSelected(value);
		if (!poss.get()) {
			return new OperationResult<>(poss);
		}
		
		QualityValue selected = new QualityValue(value, value.hasLevel()?1:0);
		for (Decision dec : decisions)
			selected.addDecision(dec);
		logger.log(Level.INFO, "Select shifter addon ''{0}''", value);
		getModel().addShifterAddOn(selected);
		
		parent.runProcessors();
		return new OperationResult<QualityValue>(selected);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(QualityValue value) {
		return new Possible( getModel().getShifterAddOns().contains(value) );
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(QualityValue value) {
		if (!canBeDeselected(value).get())
			return false;
		
		logger.log(Level.INFO, "Removing shifter add-on {0}", value);
		getModel().removeShifterAddOn(value);
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelectionCost(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
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
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(QualityValue value) {
		return value.getModifiedValue();
	}

	@Override
	public Possible canBeIncreased(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canBeDecreased(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<QualityValue> increase(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationResult<QualityValue> decrease(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

}
