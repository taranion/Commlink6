package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyWhen;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PrioritySR6AttributeGenerator extends CommonAttributeGenerator implements PriorityAttributeGenerator {

	private final static Logger logger = System.getLogger(PrioritySR6AttributeGenerator.class.getPackageName()+".attrib");
	
	private int adjustmentPoints;
	private int attributePoints;
	private boolean redistribute;
	
	//-------------------------------------------------------------------
	public PrioritySR6AttributeGenerator(SR6CharacterController parent) {
		super(parent);
//		try {
//			throw new RuntimeException("Trace "+this);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeIncreased(AttributeValue)
	 */
	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		Possible allowed = super.canBeIncreased(value); 
		if (!allowed.get())
			return allowed;
		
		allowed = canBeIncreasedPoints(value);
		if (allowed.get()) return allowed;
		
		allowed = canBeIncreasedPoints2(value);
		if (allowed.get()) return allowed;
		
		return canBeIncreasedPoints3(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeDecreased(AttributeValue)
	 */
	@Override
	public Possible canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		Possible allowed = super.canBeDecreased(value); 
		if (!allowed.get())
			return allowed;
		
		allowed = canBeDecreasedPoints(value);
		if (allowed.get()) return allowed;
		
		allowed = canBeDecreasedPoints2(value);
		if (allowed.get()) return allowed;
		
		return canBeDecreasedPoints3(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increase(AttributeValue<ShadowrunAttribute> value) {
		OperationResult<AttributeValue<ShadowrunAttribute>> result = increasePoints(value);
		if (result.wasSuccessful()) 	return result;
		
		result = increasePoints2(value);
		if (result.wasSuccessful()) 	return result;
		
		return increasePoints3(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decrease(AttributeValue<ShadowrunAttribute> value) {
		OperationResult<AttributeValue<ShadowrunAttribute>> result = decreasePoints(value);
		if (result.wasSuccessful()) 	return result;
		
		result = decreasePoints2(value);
		if (result.wasSuccessful()) 	return result;
		
		return decreasePoints3(value);
	}

	//-------------------------------------------------------------------
	private void payAdjustment(SR6PrioritySettings settings, ShadowrunAttribute key) {
		AttributeValue aVal = parent.getModel().getAttribute(key);
		
		int toPay = aVal.getModifiedValue() - settings.perAttrib.get(key).base;
		settings.perAttrib.get(key).points1 = toPay;
		logger.log(Level.DEBUG, "Pay "+toPay+" adjustment points for "+key);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void calculateDistribution( ) {
		logger.log(Level.INFO, "calculateDistribution");
		// Clear current settings
		SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
		for (PerAttributePoints tmp : settings.perAttrib.values()) {
			tmp.clear();
			tmp.base = 1;
		}
		settings.perAttrib.get(ShadowrunAttribute.MAGIC).base=0;
		settings.perAttrib.get(ShadowrunAttribute.RESONANCE).base=0;
		
		// ToDo: Modifications from priorities
		
		// Sort attributes highest value first
		List<AttributeValue<ShadowrunAttribute>> sorted = new ArrayList<>(parent.getModel().getAttributes());
		Collections.sort(sorted, new Comparator<AttributeValue>() {
			public int compare(AttributeValue a1, AttributeValue a2) {
				return -Integer.compare(a1.getModifiedValue(), a2.getModifiedValue());
			}
		});
		for (AttributeValue val : sorted)
			logger.log(Level.DEBUG, val.getModifyable()+" = "+val.getModifiedValue()+" = "+val.getModifications()+"   \tmax="+val.getMaximum());
		
		// Pay MAGIC, RESONANCE and EDGE with adjustment points
		payAdjustment(settings, ShadowrunAttribute.EDGE );
		if (parent.getModel().getMagicOrResonanceType()!=null && parent.getModel().getMagicOrResonanceType().usesMagic())
			payAdjustment(settings, ShadowrunAttribute.MAGIC);
		if (parent.getModel().getMagicOrResonanceType()!=null && parent.getModel().getMagicOrResonanceType().usesResonance())
			payAdjustment(settings, ShadowrunAttribute.RESONANCE);
		
		// Invest remaining adjustment points
		for (AttributeValue<ShadowrunAttribute> aVal : sorted) {
			ShadowrunAttribute key = aVal.getModifyable();
			if (key==ShadowrunAttribute.EDGE || key==ShadowrunAttribute.MAGIC || key==ShadowrunAttribute.RESONANCE)
				continue;
			
			int toPay = aVal.getModifiedValue() - settings.perAttrib.get(key).getSum();
			logger.log(Level.DEBUG, "  "+key+" is at "+settings.perAttrib.get(key).getSum()+" - need "+toPay+" more");
			if (toPay>0 && allowedAdjust.contains(aVal.getModifyable())) {
				int payed = Math.min(toPay, adjustmentPoints);
				settings.perAttrib.get(key).points1 = payed;
				adjustmentPoints -= payed;
				logger.log(Level.DEBUG, "Payed "+payed+" adjustment points for "+key);
			}
		}
		logger.log(Level.DEBUG, "After distributing adjustment points, there are "+adjustmentPoints+" left");
		
		// Invest attribute points
		for (AttributeValue<ShadowrunAttribute> aVal : sorted) {
			ShadowrunAttribute key = aVal.getModifyable();
			PerAttributePoints per = settings.perAttrib.get(key);
			int toPay = aVal.getDistributed()-1 - per.points1;
			if (toPay>0) {
				int payed = Math.min(toPay, attributePoints);
				per.points2 = payed;
				attributePoints -= payed;
				logger.log(Level.DEBUG, "Payed "+payed+" attribute points for "+key);
			}
		}
		logger.log(Level.DEBUG, "After distributing attribute points, there are "+attributePoints+" left");
		
		redistribute = false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#getAdjustmentPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return adjustmentPoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#getAttributePointsLeft()
	 */
	@Override
	public int getPointsLeft2() {
		return attributePoints;
	}

	//-------------------------------------------------------------------
	private boolean wouldMaximizeMoreThanAllowed(AttributeValue<ShadowrunAttribute> value) {
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#canBeDecreasedPoints1(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public Possible canBeDecreasedPoints(AttributeValue<ShadowrunAttribute> value) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(value.getModifyable());
		return new Possible(per.points1>0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#canBeIncreasedPoints(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public Possible canBeIncreasedPoints(AttributeValue<ShadowrunAttribute> value) {
		if (adjustmentPoints<1) return new Possible(false, "zero_adjustment_points");

		ShadowrunAttribute key = value.getModifyable();		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		if (per.getSum()>=getMaximumValue(key))
			return Possible.FALSE;

		if (isAnotherAttributeAlreadyMaxed(key))
			return new Possible(false, SR6RejectReasons.IMPOSS_ALREADY_MAX_LIMIT);
		return new Possible(allowedAdjust.contains(value.getModifyable()), "no_special_attribute");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#increaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increasePoints(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER increasePoints({0})", value.getModifyable());

		try {
			ShadowrunAttribute key = value.getModifyable();		
			Possible allowed = canBeIncreasedPoints(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase attribute " + key + " with adjustment points, although not possible");
				return new OperationResult<>(allowed);
			}

			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			per.points1++;
			logger.log(Level.INFO, "Increased {0} to {1} by adjustment points", key, per.getSum());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE increasePoints");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#decreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decreasePoints(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER decreasePoints2({0})", value.getModifyable());

		try {
			ShadowrunAttribute key = value.getModifyable();
			Possible allowed = canBeDecreasedPoints(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase attribute " + key + " with adjustment points, although {0}", allowed.getI18NKey());
				return new OperationResult<>(allowed);
			}

			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			per.points1--;
			logger.log(Level.INFO, "Decreased {0} to {1}", key, per.getSum());
			
			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE decreasePoints2");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#canDecreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public Possible canBeDecreasedPoints2(AttributeValue<ShadowrunAttribute> value) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(value.getModifyable());
		return new Possible(per.points2>0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#canIncreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public Possible canBeIncreasedPoints2(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();		
		if (value.getModifyable().isSpecial()) return Possible.FALSE;
		if (attributePoints<1) return Possible.FALSE;

		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		if (per.getSum()>=getMaximumValue(key))
			return Possible.FALSE;

		if (isAnotherAttributeAlreadyMaxed(key))
			return new Possible(false, SR6RejectReasons.IMPOSS_ALREADY_MAX_LIMIT);
		// TODO Auto-generated method stub
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#increaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increasePoints2(
			AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER increasePoints2");

		try {
			ShadowrunAttribute key = value.getModifyable();
			Possible allowed = canBeIncreasedPoints2(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase attribute " + key + " with attribute points, although not possible");
				return new OperationResult<>(allowed);
			}

			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			per.points2++;
			logger.log(Level.INFO, 
					"Increased attribute points for " + key + " to " + per.points2 + " - sum is now " + per.getSum());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE increasePoints2");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#decreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decreasePoints2(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER decreasePoints2");
		
		try {
			ShadowrunAttribute key = value.getModifyable();
			logger.log(Level.INFO, "decreaseAttrib(" + key + ")");
			Possible allowed = canBeDecreasedPoints2(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to decrease attribute " + key + " with attribute points, although not possible");
				return new OperationResult<>(allowed);
			}

			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			per.points2--;
			logger.log(Level.INFO, 
					"Decreased attribute points for " + key + " to " + per.points2 + " - sum is now " + per.getSum());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE decreasePoints2");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#canBeDecreasedPoints3(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public Possible canBeDecreasedPoints3(AttributeValue<ShadowrunAttribute> value) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(value.getModifyable());
		return new Possible(per.points3>0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#canBeIncreasedPoints3(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public Possible canBeIncreasedPoints3(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();		
		Shadowrun6Character model = parent.getModel();
		if (key==ShadowrunAttribute.RESONANCE && ( model.getMagicOrResonanceType()==null || model.getMagicOrResonanceType().usesResonance()))
			return Possible.FALSE;
		if (key==ShadowrunAttribute.MAGIC && (model.getMagicOrResonanceType()==null || model.getMagicOrResonanceType().usesMagic())) {
			return Possible.FALSE;
		}

		PerAttributePoints per = model.getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		if (per.getSum()>=getMaximumValue(key))
			return Possible.FALSE;

		if (isAnotherAttributeAlreadyMaxed(key))
			return new Possible(false, SR6RejectReasons.IMPOSS_ALREADY_MAX_LIMIT);
		
		int requiredKarma = (per.getSum()+1)*5;
		if (model.getKarmaFree()<requiredKarma) {
			return Possible.FALSE;
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#increasePoints3(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increasePoints3(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER increasePoints3");

		try {
			ShadowrunAttribute key = value.getModifyable();
			Possible allowed = canBeIncreasedPoints3(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase attribute " + key + " with Karma, although not possible");
				return new OperationResult<>(allowed);
			}

			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			per.points3++;
			logger.log(Level.INFO, 
					"Increased Karma for " + key + " to " + per.points3 + " - sum is now " + per.getSum());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE increasePoints3");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator#decreasePoints3(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decreasePoints3(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER decreasePoints3");
		
		try {
			ShadowrunAttribute key = value.getModifyable();
			Possible allowed = canBeDecreasedPoints3(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to decrease attribute " + key + " with Karma, although not possible");
				return new OperationResult<>(allowed);
			}

			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			per.points3--;
			logger.log(Level.INFO, 
					"Decreased karma for " + key + " to " + per.points2 + " - sum is now " + per.getSum());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE decreasePoints3");
		}
	}

	//--------------------------------------------------------------------
	private void updateAttributeValues() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			AttributeValue val = parent.getModel().getAttribute(key);
			val.setDistributed(per.getSum() -  val.getModifier());
		}
	}

	//--------------------------------------------------------------------
	/**
	 * Validate that the maximum value of each attribute is not exceeded.
	 * If it should be the case, reduce in the following order:
	 * - Ratings gained from Karma
	 * - Ratings gained from attribute points
	 * - Ratings gained from adjustment points
	 */
	private void ensureMaximumNotExceeded() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = parent.getModel().getAttribute(key);
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			int max = getMaximumValue(key);
			if (per!=null && per.getSum()>max) {
				if (per.points3>0) {
					per.points3--;
					logger.log(Level.ERROR,"New value for "+key+":"+per.getSum()+" would exceed maximum of "+max+" - reduce Karma invest");
				} else if (per.points2>0) {
					per.points2--;
					logger.log(Level.ERROR,"New value for "+key+":"+per.getSum()+" would exceed maximum of "+max+" - reduce attribute points invest");
				} else if (per.points1>0) {
					per.points1--;
					logger.log(Level.ERROR,"New value for "+key+":"+per.getSum()+" would exceed maximum of "+max+" - reduce adjustment points invest");
				} else {
					logger.log(Level.ERROR,"New value for "+key+":"+per.getSum()+" would exceed maximum of "+max+" - cannot reduce");
					continue;
				}
			} 
		}
	}

	//--------------------------------------------------------------------
	private void validateAdjustmentpoints() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			if (per.points1>0 && !allowedAdjust.contains(key)) {
				logger.log(Level.ERROR, "Attribute {0} has spent adjustment points, though it is not allowed - remove them", key);
				per.points1=0;
			}
		}
	}

	//-------------------------------------------------------------------
	private void reset() {
		todos.clear();
		adjustmentPoints = 0;
		attributePoints  = 0;
		allowedAdjust.clear();
		allowedAdjust.add(ShadowrunAttribute.EDGE);
		numAttributesToMax = 1;
		for (AttributeValue tmp : getModel().getAttributes()) {
			tmp.clearModifications();
			tmp.setDistributed(0);
		}
		
		for (Entry<ShadowrunAttribute,PerAttributePoints> entry : getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.entrySet()) {
			switch (entry.getKey()) {
			case MAGIC: case RESONANCE:
				entry.getValue().base=0;
				break;
			default:
				entry.getValue().base=1;
			}
		}
	}

	//-------------------------------------------------------------------
	private void ensureMaximumSet() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
			if (val.getMaximum()==0) {
				val.addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, key.name(), 6, ApplyWhen.ALLCREATE, ValueType.MAX));
			}
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
			SR6PrioritySettings prioSettings = getModel().getCharGenSettings(SR6PrioritySettings.class);
			// Reset
			reset();
			
			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.CREATION_POINTS) {
					ValueModification mod = (ValueModification)tmp;
					if (mod.getResolvedKey()==CreatePoints.ADJUST) {
						adjustmentPoints = mod.getValue();
						logger.log(Level.INFO, "Consume "+mod+" and set adjustment points to "+adjustmentPoints);
					} else if (mod.getResolvedKey()==CreatePoints.ATTRIBUTES) {
						attributePoints = mod.getValue();
						logger.log(Level.INFO, "Consume "+mod+" and set attribute points to "+attributePoints);
					} else {
						unprocessed.add(mod);
					}
				} else if (tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE) {
					ValueModification mod = (ValueModification)tmp;
					ShadowrunAttribute attr = mod.getResolvedKey();
					logger.log(Level.DEBUG, "Consume "+mod);
					getModel().getAttribute(attr).addModification(mod);
					if (mod.getSet()==ValueType.MAX) {
						// Optional: Allow adjustment points on lowered maximum
						if (mod.getValue()>6 || getModel().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ADJUSTMENT_ON_LOWERED_MAX))
							allowedAdjust.add(attr);					
					}
					// Update base
					if (mod.getSet()==ValueType.NATURAL && prioSettings.perAttrib.get(attr)!=null) {
						logger.log(Level.DEBUG, "Updated base of "+attr+" with +"+mod.getValue());
						prioSettings.perAttrib.get(attr).base += mod.getValue();
					}
				} else {
					unprocessed.add(tmp);
				}
			}
			
			// Handle MAGIC or RESONANCE
			MagicOrResonanceType mor = getModel().getMagicOrResonanceType();
			if (mor!=null && mor.usesMagic())
				allowedAdjust.add(ShadowrunAttribute.MAGIC);
			else if (mor!=null && mor.usesResonance())
				allowedAdjust.add(ShadowrunAttribute.RESONANCE);
			
			logger.log(Level.DEBUG, "Start with "+adjustmentPoints+" adjust and "+attributePoints+" attrib points");
			logger.log(Level.DEBUG, "Adjustment points may be used for "+allowedAdjust);
			
			ensureMaximumSet();
			validateAdjustmentpoints();
			ensureMaximumNotExceeded();
			
			List<ShadowrunAttribute> order = new ArrayList<>(List.of( ShadowrunAttribute.primaryAndSpecialValues() ));
			Collections.sort(order, new Comparator<ShadowrunAttribute>() {
				public int compare(ShadowrunAttribute o1, ShadowrunAttribute o2) {
					if (allowedAdjust.contains(o1) && !allowedAdjust.contains(o2)) return -1;
					if (!allowedAdjust.contains(o1) && allowedAdjust.contains(o2)) return +1;
					return -Integer.compare(getModel().getAttribute(o1).getModifiedValue(), getModel().getAttribute(o2).getModifiedValue());
				}
			});
			logger.log(Level.DEBUG, "Order: "+order);
			
			// Pay
			for (ShadowrunAttribute key : order) {
				PerAttributePoints per = prioSettings.perAttrib.get(key);
				// Pay adjustment points first
				int required = per.points1;
				if (required>0 &&adjustmentPoints>0) {
					int payed = Math.min(required, adjustmentPoints);
					logger.log(Level.INFO, "  pay {0} adjustment points for {1}", payed, key);
					adjustmentPoints-=payed;
					required-=payed;
				}
				// If there are still adjustment points to pay, but no remaining adjustment points
				// move to attribute points (if possible) - otherwise reduce it
				if (required>0) { 
					per.points1-=required;
					if (key.isPrimary()) {
						per.points2+=required;
						logger.log(Level.WARNING, "Shifted {0} points from adjustment to attribute points for {1}", required, key);
					} else {
						per.points3+=required;
						logger.log(Level.WARNING, "Shifted {0} points from adjustment to Karma for {1}", required, key);
					}
				}
				// Now pay attribute points
				required = per.points2;
				if (required>0 && attributePoints>0) {
					int payed = Math.min(required, attributePoints);
					logger.log(Level.INFO, "  pay {0} attribute points for {1}", payed, key);
					attributePoints-=payed;
					required-=payed;
				}
				// If there are still attribute points to pay, but no remaining attribute points
				// move karma
				if (required>0) { 
					per.points2-=required;
					per.points3+=required;
					logger.log(Level.WARNING, "Shifted {0} points from attribute to Karma for {1}", required, key);
				}
				// Now pay karma
				required = per.points3;
				if (required>0) {
					int karma = per.getKarmaInvest();
					logger.log(Level.INFO, "  pay {0} Karma for {1}", karma, key);
					getModel().setKarmaFree( getModel().getKarmaFree() - karma);
				}
			}
			logger.log(Level.DEBUG, "Finish with {0} adjust and {1} attrib points and {2} Karma", adjustmentPoints, attributePoints, getModel().getKarmaFree());
			
			if (adjustmentPoints>0) {
				todos.add(new ToDoElement(Severity.STOPPER, SR6CharacterGenerator.RES, SR6RejectReasons.TODO_ATTRIB_REMAIN_ADJUST, adjustmentPoints));
			}
			
			// Copy current setup 
			updateAttributeValues();
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	@Override
	public String getColumn3() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPointsLeft3() {
		// TODO Auto-generated method stub
		return getModel().getKarmaFree();
	}

	@Override
	public String getColumn2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumn1() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//-------------------------------------------------------------------
	@Override
	public void roll() {
		logger.log(Level.INFO, "ENTER roll()");
		try {
			Random random = new Random();
			SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
			// Build a list of racial and special attributes - add special attributes twice for probability
			List<ShadowrunAttribute> specials = new ArrayList<>(allowedAdjust);
			for (ShadowrunAttribute attr : allowedAdjust) {
				if (attr.isSpecial())
					specials.add(attr);
			}
				
			// Now spend points
			spendSpecial:
			while (adjustmentPoints>0) {			
				// Try to find attribute to increase with special points
				// Make max. 5 attempts
				int attempts = 5;
				while (attempts>0) {
					attempts --;
					int ran = random.nextInt(specials.size());
					ShadowrunAttribute attr = specials.get(ran);
					AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(attr);
					if (canBeIncreasedPoints(aVal).get()) {
						settings.perAttrib.get(attr).points1++;
						adjustmentPoints--;
						logger.log(Level.INFO, "Increased "+attr+" with adjustment points to "+settings.perAttrib.get(attr));
						break;
					}
				}
				if (attempts==0 && adjustmentPoints>0) {
					logger.log(Level.WARNING, "Failed to find something to increase with adjustment points");
					break spendSpecial;
				}
			}
			
		// Now spend regular points
		spendRegular:
		while (attributePoints>0) {			
			// Try to find attribute to increase with special points
			// Make max. 5 attempts
			int attempts = 5;
			while (attempts>0) {
				attempts --;
				int ran = random.nextInt(ShadowrunAttribute.primaryValues().length);
				ShadowrunAttribute attr = ShadowrunAttribute.primaryValues()[ran];
				AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(attr);
				if (canBeIncreasedPoints2(aVal).get()) {
					settings.perAttrib.get(attr).points2++;
					attributePoints--;
					logger.log(Level.INFO, "Increased "+attr+" with attribute points to "+settings.perAttrib.get(attr));
					break;
				}
			}
			if (attempts==0 && attributePoints>0) {
				logger.log(Level.WARNING, "Failed to find something to increase with attribute points");
				break spendRegular;
			}
		}
			
			parent.runProcessors();
			
		} finally {
			logger.log(Level.INFO, "LEAVE roll()");
		}
		
	}

}
