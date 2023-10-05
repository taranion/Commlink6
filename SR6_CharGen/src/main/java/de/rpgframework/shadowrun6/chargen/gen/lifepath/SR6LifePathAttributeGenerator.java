package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
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
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6LifePathAttributeGenerator extends CommonAttributeGenerator implements IAttributeController {

	private final static Logger logger = System.getLogger(SR6LifePathAttributeGenerator.class.getPackageName()+".attrib");

	private boolean redistribute;

	//-------------------------------------------------------------------
	public SR6LifePathAttributeGenerator(SR6CharacterController parent) {
		super(parent);
		Shadowrun6Character model = parent.getModel();
//		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
//			PerAttributePoints per = model.getCharGenSettings(SR6LifePathSettings.class).perAttrib.get(key);
//			per.base=1;
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#getMaximizedAttributes()
	 */
	@Override
	protected List<ShadowrunAttribute> getMaximizedAttributes() {
		List<ShadowrunAttribute> maxed = new ArrayList<ShadowrunAttribute>();
		Shadowrun6Character model = parent.getModel();
//		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
//			PerAttributePoints per = model.getCharGenSettings(SR6LifePathSettings.class).perAttrib.get(key);
//			if (per.getSum() >= model.getAttribute(key).getMaximum())
//				maxed.add(key);
//		}
		return maxed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#isAnotherAttributeAlreadyMaxed(ShadowrunAttribute)
	 */
	@Override
	protected boolean isAnotherAttributeAlreadyMaxed(ShadowrunAttribute key) {
		if (key.isSpecial())
			return false;

		Collection<ShadowrunAttribute> alreadyMaxed = getMaximizedAttributes();
		alreadyMaxed.remove(key);

		for (ShadowrunAttribute attr : ShadowrunAttribute.primaryValues()) {
			AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(attr);
			if (aVal.getModifiedValue()>=getMaximumValue(attr)) {
				if (logger.isLoggable(Level.TRACE))
					logger.log(Level.TRACE, "Increasing "+key+" would reach maximum of "+getMaximumValue(key)+".  Is already one maxed = "+alreadyMaxed+" of "+numAttributesToMax);
				return alreadyMaxed.size()>=numAttributesToMax;
			}
		}
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeIncreased(AttributeValue)
	 */
	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		if (! (parent.getModel().hasCharGenSettings(SR6LifePathSettings.class))) {
			return Possible.FALSE;
		}

		ShadowrunAttribute key = value.getModifyable();
		if (value.getModifiedValue()>=getMaximumValue(key))
			return new Possible(IRejectReasons.IMPOSS_MAX_LEVEL_REACHED);

		if (isAnotherAttributeAlreadyMaxed(key))
			return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_ALREADY_MAX_LIMIT);
		return new Possible(allowedAdjust.contains(value.getModifyable()), "no_special_attribute");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeDecreased(AttributeValue)
	 */
	@Override
	public Possible canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		if (! (parent.getModel().hasCharGenSettings(SR6LifePathSettings.class))) {
			return Possible.FALSE;
		}
		return new Possible(value.getDistributed()>0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increase(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER increase({0})", value.getModifyable());

		try {
			ShadowrunAttribute key = value.getModifyable();
			Possible allowed = canBeIncreased(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to increase attribute " + key + " with adjustment points, although not possible");
				return new OperationResult<>(allowed);
			}

			value.setDistributed( value.getDistributed()+1);
			logger.log(Level.INFO, "Increased {0} to {1} by adjustment points", key, value.getModifiedValue());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE increase");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decrease(AttributeValue<ShadowrunAttribute> value) {
		if (logger.isLoggable(Level.TRACE))
			logger.log(Level.TRACE, "ENTER decrease({0})", value.getModifyable());

		try {
			ShadowrunAttribute key = value.getModifyable();
			Possible allowed = canBeDecreased(value);
			if (!allowed.get()) {
				logger.log(Level.WARNING, "Trying to decrease attribute {0} with adjustment points, although {1}", key, allowed.getI18NKey());
				return new OperationResult<>(allowed);
			}

			value.setDistributed( value.getDistributed()-1 );
			logger.log(Level.INFO, "Decreased {0} to {1}", key, value.getModifiedValue());

			parent.runProcessors();
			return new OperationResult<>(value);
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE decrease");
		}
	}

	//--------------------------------------------------------------------
	/**
	 * Validate that the maximum value of each attribute is not exceeded.
	 * If it should be the case, reduce in the following order:
	 */
	private void ensureMaximumNotExceeded() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> aVal = parent.getModel().getAttribute(key);
			int max = getMaximumValue(key);
			if (aVal.getModifiedValue()>max) {
					logger.log(Level.ERROR,"New value for "+key+":"+aVal.getModifiedValue()+" would exceed maximum of "+max+" - cannot reduce");
					continue;
			}
		}
	}

	//-------------------------------------------------------------------
	private void reset() {
		todos.clear();
		allowedAdjust.clear();
		allowedAdjust.add(ShadowrunAttribute.EDGE);
		numAttributesToMax = 1;

//		for (Entry<ShadowrunAttribute,PerAttributePoints> entry : getModel().getCharGenSettings(SR6LifePathSettings.class).perAttrib.entrySet()) {
//			switch (entry.getKey()) {
//			case MAGIC: case RESONANCE:
//				entry.getValue().base=0;
//				break;
//			default:
//				entry.getValue().base=1;
//			}
//		}
	}

	//-------------------------------------------------------------------
	private void ensureMaximumSet() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
			if (val.getMaximum()==0) {
				val.addModification(new ValueModification(ShadowrunReference.ATTRIBUTE, key.name(), 6, ApplyWhen.ALLCREATE, ValueType.MAX));
			}
			logger.log(Level.TRACE, "Maximum of {0} is {1}", key, val.getMaximum());
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
			SR6LifePathSettings prioSettings = getModel().getCharGenSettings(SR6LifePathSettings.class);
			logger.log(Level.ERROR, "settings = "+prioSettings);
			// Reset
			reset();

			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp instanceof AllowModification) {
					unprocessed.add(tmp);
					continue;
				}
				if (tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE) {
					ValueModification mod = (ValueModification)tmp;
					ShadowrunAttribute attr = mod.getResolvedKey();
					AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(attr);
					logger.log(Level.DEBUG, "Consume {0} when old val is {1} and old max is {2}", mod, aVal.getModifiedValue(),aVal.getMaximum());
					aVal.addModification(mod);
					if (mod.getSet()==ValueType.MAX) {
//						logger.log(Level.DEBUG, "After consuming {0} max is {1}", mod, aVal.getMaximum());
						// Optional: Allow adjustment points on lowered maximum
						if (mod.getValue()>6 || parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ADJUSTMENT_ON_LOWERED_MAX))
							allowedAdjust.add(attr);
					}
//					// Update base
//					if (mod.getSet()==ValueType.NATURAL && prioSettings.perAttrib.get(attr)!=null) {
//						logger.log(Level.DEBUG, "Updated base of "+attr+" with +"+mod.getValue());
//						prioSettings.perAttrib.get(attr).base += mod.getValue();
//					}
				} else {
					unprocessed.add(tmp);
				}
			}

			// Allow adjustment points to be spent on everything where the maximum is >6
			for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
				AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(key);
				int max = (aVal.getMaximum()>0)?aVal.getMaximum():6 ;
				if (max>6 || (max<6 && parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ADJUSTMENT_ON_LOWERED_MAX))) {
					allowedAdjust.add(key);
					logger.log(Level.DEBUG, "max. value of {0} is {1}", key, max);
				}
			}

			// Handle MAGIC or RESONANCE
			MagicOrResonanceType mor = getModel().getMagicOrResonanceType();
			if (mor!=null && mor.usesMagic())
				allowedAdjust.add(ShadowrunAttribute.MAGIC);
			else if (mor!=null && mor.usesResonance())
				allowedAdjust.add(ShadowrunAttribute.RESONANCE);

			ensureMaximumSet();
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
				// Pay adjustment points first
//				int required = per.points1;
//				if (required>0 &&adjustmentPoints>0) {
//					int payed = Math.min(required, adjustmentPoints);
//					logger.log(Level.INFO, "  pay {0} adjustment points for {1}", payed, key);
//					adjustmentPoints-=payed;
//					required-=payed;
//				}
//				// If there are still adjustment points to pay, but no remaining adjustment points
//				// move to attribute points (if possible) - otherwise reduce it
//				if (required>0) {
//					per.points1-=required;
//					if (key.isPrimary()) {
//						per.points2+=required;
//						logger.log(Level.WARNING, "Shifted {0} points from adjustment to attribute points for {1}", required, key);
//					} else {
//						per.points3+=required;
//						logger.log(Level.WARNING, "Shifted {0} points from adjustment to Karma for {1}", required, key);
//					}
//				}
//				// Now pay attribute points
//				required = per.points2;
//				if (required>0 && attributePoints>0) {
//					int payed = Math.min(required, attributePoints);
//					logger.log(Level.INFO, "  pay {0} attribute points for {1}", payed, key);
//					attributePoints-=payed;
//					required-=payed;
//				}
//				// If there are still attribute points to pay, but no remaining attribute points
//				// move karma
//				if (required>0) {
//					per.points2-=required;
//					per.points3+=required;
//					logger.log(Level.WARNING, "Shifted {0} points from attribute to Karma for {1}", required, key);
//				}
//				// Now pay karma
//				required = per.points3;
//				if (required>0) {
//					int karma = per.getKarmaInvest();
//					logger.log(Level.INFO, "  pay {0} Karma for {1}", karma, key);
//					getModel().setKarmaFree( getModel().getKarmaFree() - karma);
//				}
//
//				AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(key);
//				logger.log(Level.DEBUG, "Current {4} aVal={0}/{1}  sumWithout={2}  sum={3}", aVal.getModifiedValue(), aVal.getModifier(), per.getSumWithoutBase(), per.getSum(), key);
//				aVal.setDistributed(per.getSumWithoutBase());
			}
//			logger.log(Level.DEBUG, "Finish with {0} adjust and {1} attrib points and {2} Karma", adjustmentPoints, attributePoints, getModel().getKarmaFree());

			// Copy current setup
//			updateAttributeValues();

			for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {

				AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
				logger.log(Level.INFO, "Attribute {0} has value {1}", key, val.getModifiedValue());
			}
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void roll() {
		logger.log(Level.INFO, "ENTER roll()");
		try {
//			Random random = new Random();
//			SR6LifePathSettings settings = parent.getModel().getCharGenSettings(SR6LifePathSettings.class);
//			// Build a list of racial and special attributes - add special attributes twice for probability
//			List<ShadowrunAttribute> specials = new ArrayList<>(allowedAdjust);
//			for (ShadowrunAttribute attr : allowedAdjust) {
//				if (attr.isSpecial())
//					specials.add(attr);
//			}
//
//			// Now spend points
//			spendSpecial:
//			while (adjustmentPoints>0) {
//				// Try to find attribute to increase with special points
//				// Make max. 5 attempts
//				int attempts = 5;
//				while (attempts>0) {
//					attempts --;
//					int ran = random.nextInt(specials.size());
//					ShadowrunAttribute attr = specials.get(ran);
//					AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(attr);
//					if (canBeIncreasedPoints(aVal).get()) {
//						settings.perAttrib.get(attr).points1++;
//						adjustmentPoints--;
//						logger.log(Level.INFO, "Increased "+attr+" with adjustment points to "+settings.perAttrib.get(attr));
//						break;
//					}
//				}
//				if (attempts==0 && adjustmentPoints>0) {
//					logger.log(Level.WARNING, "Failed to find something to increase with adjustment points");
//					break spendSpecial;
//				}
//			}
//
//		// Now spend regular points
//		spendRegular:
//		while (attributePoints>0) {
//			// Try to find attribute to increase with special points
//			// Make max. 5 attempts
//			int attempts = 5;
//			while (attempts>0) {
//				attempts --;
//				int ran = random.nextInt(ShadowrunAttribute.primaryValues().length);
//				ShadowrunAttribute attr = ShadowrunAttribute.primaryValues()[ran];
//				AttributeValue<ShadowrunAttribute> aVal = getModel().getAttribute(attr);
//				if (canBeIncreasedPoints2(aVal).get()) {
//					settings.perAttrib.get(attr).points2++;
//					attributePoints--;
//					logger.log(Level.INFO, "Increased "+attr+" with attribute points to "+settings.perAttrib.get(attr));
//					break;
//				}
//			}
//			if (attempts==0 && attributePoints>0) {
//				logger.log(Level.WARNING, "Failed to find something to increase with attribute points");
//				break spendRegular;
//			}
//		}

			parent.runProcessors();

		} finally {
			logger.log(Level.INFO, "LEAVE roll()");
		}

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(AttributeValue<ShadowrunAttribute> value) {
		return value.getModifiedValue();
	}

}
