package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;

/**
 *
 */
public class OneOrTwoQualityController extends CommonQualityGenerator implements IQualityController {

	private Supplier<Quality> quality1Supplier;
	private Consumer<Quality> quality1Consumer;
	private Supplier<Quality> quality2Supplier;
	private Consumer<Quality> quality2Consumer;
	private Predicate<Quality> quality1Predicate;

	//-------------------------------------------------------------------
	public OneOrTwoQualityController(SR6LifepathCharacterGenerator parent,
			Supplier<Quality> quality1Supplier, Consumer<Quality> quality1Consumer,
			Supplier<Quality> quality2Supplier, Consumer<Quality> quality2Consumer) {
		super(parent);
		this.quality1Supplier = quality1Supplier;
		this.quality1Consumer = quality1Consumer;
		this.quality2Supplier = quality2Supplier;
		this.quality2Consumer = quality2Consumer;
	}

	//-------------------------------------------------------------------
	private SR6LifePathSettings getSettings() {
		return ((SR6LifepathCharacterGenerator) parent).getSettings();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(QualityValue value) {
		return value.getDistributed();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeIncreased(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeDecreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public Possible canBeDecreased(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<QualityValue> increase(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<QualityValue> decrease(QualityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getAvailable()
	 */
	@Override
	public List<Quality> getAvailable() {
		Quality q1 = quality1Supplier.get();
		Quality q2 = quality2Supplier.get();
		if (q1==null && q2==null)
			return super.getAvailable();
		if (q1!=null && q2!=null)
			return List.of();
		if (q1!=null)
			return super.getAvailable().stream()
					// If you choose two qualities, one must be a positive quality and the other must be a negative quality
					.filter(p -> p.isPositive() == !q1.isPositive())
					.collect(Collectors.toList());
		else
			return super.getAvailable().stream()
					// If you choose two qualities, one must be a positive quality and the other must be a negative quality
					.filter(p -> p.isPositive() == !q2.isPositive())
					.collect(Collectors.toList());
//		if (handleFirstQuality) {
//			return super.getAvailable();
//		} else {
//			if (getSettings().getBornQuality1()==null) return List.of();
//			return super.getAvailable().stream()
//					// If you choose two qualities, one must be a positive quality and the other must be a negative quality
//					.filter(p -> p.isPositive() == !getSettings().getBornQuality1().isPositive())
//					.collect(Collectors.toList());
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getSelected()
	 */
	@Override
	public List<QualityValue> getSelected() {
		List<String> selected = new ArrayList<>();
		if (quality1Supplier.get()!=null) selected.add(quality1Supplier.get().getId());
		if (quality2Supplier.get()!=null) selected.add(quality2Supplier.get().getId());
		return super.getSelected().stream().filter(q -> selected.contains(q.getKey())).toList();
//		Quality q1 = quality1Supplier.get();
//		Quality q2 = quality2Supplier.get();
//		if (handleFirstQuality) {
//			if (getSettings().getBornQuality1()!=null) {
//				return List.of(parent.getModel().getQuality(getSettings().getBornQuality1().getId()));
//			} else {
//				return List.of();
//			}
//		} else {
//			if (getSettings().getBornQuality2()!=null) {
//				return List.of(parent.getModel().getQuality(getSettings().getBornQuality2().getId()));
//			} else {
//				return List.of();
//			}
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public RecommendationState getRecommendationState(Quality value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getRecommendationState(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public RecommendationState getRecommendationState(QualityValue value) {
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#getChoicesToDecide(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public List<Choice> getChoicesToDecide(Quality value) {
		return value.getChoices();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(Quality value, Decision... decisions) {
		return super.canBeSelected(value, decisions);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<QualityValue> select(Quality quality, Decision... decisions) {
		Possible poss = canBeSelected(quality, decisions);
		if (!poss.get()) {
			if (poss.getMostSevereExcept(List.of(SR6RejectReasons.IMPOSS_ALREADY_PRESENT))!=null) {
				logger.log(Level.WARNING, "Trying to select({0}) but not possible because {1}", quality, poss.getMostSevere());
				return new OperationResult<QualityValue>(poss);
			}
		}

		QualityValue ret = new QualityValue(quality, 1);
		ret.setInjectedBy(this);
		for (Decision dec : decisions) ret.addDecision(dec);

		Quality q1 = quality1Supplier.get();
		Quality q2 = quality2Supplier.get();
		if (q1==null) {
			quality1Consumer.accept(quality);
		} else {
			quality2Consumer.accept(quality);
		}
//		if (handleFirstQuality) {
//			logger.log(Level.ERROR, "call "+quality1Consumer);
//			quality1Consumer.accept(quality);
//		} else {
//			logger.log(Level.ERROR, "call "+quality2Consumer);
//			quality2Consumer.accept(quality);
//		}

		if (parent.getModel().hasQuality(quality.getId())) {
			ret = parent.getModel().getQuality(quality.getId());
			ret.setDistributed(1);
			logger.log(Level.ERROR, "Added +1 to quality : {0}\n", ret);
		} else {
			ret.setDistributed(1);
			parent.getModel().addQuality(ret);
		}
		logger.log(Level.ERROR, "Select quality : {0}\n", quality.getId());

		logger.log(Level.ERROR, "Call runProcessor on "+parent);
		parent.runProcessors();
		return new OperationResult<QualityValue>(ret);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeDeselected(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public Possible canBeDeselected(QualityValue value) {
		Quality q1 = quality1Supplier.get();
		Quality q2 = quality2Supplier.get();
		logger.log(Level.WARNING, "canBeDeselected: {0} while q1={1} and q2={2}\n", value.getKey(), q1, q2);
		if (value.getResolved()==q1 || value.getResolved()==q2) return Possible.TRUE;
		return Possible.FALSE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#deselect(de.rpgframework.genericrpg.data.DataItemValue)
	 */
	@Override
	public boolean deselect(QualityValue value) {
		Possible poss = canBeDeselected(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to deselect({0}) but not possible because {1}", value.getKey(), poss.getMostSevere());
			return false;
		}
		Quality q1 = quality1Supplier.get();
		Quality q2 = quality2Supplier.get();
		if (value.getResolved()==q1) {
			value.setDistributed(0);
			super.deselect(value);
			quality1Consumer.accept(null);
			parent.runProcessors();
			return true;
		} else if (value.getResolved()==q2) {
			value.setDistributed(0);
			super.deselect(value);
			quality2Consumer.accept(null);
			parent.runProcessors();
			return true;
		} else {
			logger.log(Level.WARNING, "Cannot deselect what isn't selected: "+model.getQualities());
		}

		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#roll()
	 */
	@Override
	public void roll() {
		// TODO Auto-generated method stub

	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.PartialController#getChoiceUUIDs()
//	 */
//	@Override
//	public List<UUID> getChoiceUUIDs() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.PartialController#decide(java.lang.Object, java.util.UUID, de.rpgframework.genericrpg.data.Decision)
//	 */
//	@Override
//	public void decide(Quality decideFor, UUID choice, Decision decision) {
//		// TODO Auto-generated method stub
//
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
//	 */
//	@Override
//	public List<Modification> process(List<Modification> unprocessed) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
