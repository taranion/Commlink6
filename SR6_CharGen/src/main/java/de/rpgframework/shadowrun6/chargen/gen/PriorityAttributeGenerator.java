package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.ApplyWhen;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author stefa
 *
 */
public class PriorityAttributeGenerator extends CommonAttributeGenerator implements PriorityAttributeController {

	private final static Logger logger = LogManager.getLogger(PriorityAttributeGenerator.class.getPackageName());
	
	private int adjustmentPoints;
	private int attributePoints;
	private List<ShadowrunAttribute> allowedAdjust = new ArrayList<>();
	private boolean redistribute;
	
	//-------------------------------------------------------------------
	public PriorityAttributeGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeIncreased(AttributeValue)
	 */
	@Override
	public boolean canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		if (!super.canBeIncreased(value))
			return false;
		ShadowrunAttribute key = value.getModifyable();
		return canIncreaseAdjust(key) || canIncreaseAttrib(key) || canIncreaseKarma(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeDecreased(AttributeValue)
	 */
	@Override
	public boolean canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		return canDecreaseAdjust(key) || canDecreaseAttrib(key) || canDecreaseKarma(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean increase(AttributeValue<ShadowrunAttribute> value) {
		boolean increased = super.increase(value);
		if (increased) {
			redistribute = true;
		}
		logger.info("Increased "+value.getModifyable()+" to "+value.getModifiedValue());
		int t1 = value.getModifiedValue();
		
		parent.runProcessors();
		int t2 = value.getModifiedValue();
		logger.info("Increased2 "+value.getModifyable()+" to "+value.getModifiedValue());
		if (t1!=t2) {
			logger.error("Increasing "+value.getModifyable()+" failed");
			return false;
		}

		return increased;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean decrease(AttributeValue<ShadowrunAttribute> value) {
		boolean decreased = super.decrease(value);
		if (decreased) {
			redistribute = true;
		}
		logger.info("Decreased "+value.getModifyable()+" to "+value.getModifiedValue());
		
		parent.runProcessors();

		return decreased;
	}

	//-------------------------------------------------------------------
	private void payAdjustment(SR6PrioritySettings settings, ShadowrunAttribute key) {
		AttributeValue aVal = parent.getModel().getAttribute(key);
		
		int toPay = aVal.getModifiedValue() - settings.perAttrib.get(key).base;
		settings.perAttrib.get(key).adjust = toPay;
		logger.debug("Pay "+toPay+" adjustment points for "+key);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void calculateDistribution( ) {
		logger.info("calculateDistribution");
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
			logger.debug(val.getModifyable()+" = "+val.getModifiedValue()+" = "+val.getModifications()+"   \tmax="+val.getMaximum());
		
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
			logger.debug("  "+key+" is at "+settings.perAttrib.get(key).getSum()+" - need "+toPay+" more");
			if (toPay>0 && allowedAdjust.contains(aVal.getModifyable())) {
				int payed = Math.min(toPay, adjustmentPoints);
				settings.perAttrib.get(key).adjust = payed;
				adjustmentPoints -= payed;
				logger.debug("Payed "+payed+" adjustment points for "+key);
			}
		}
		logger.debug("After distributing adjustment points, there are "+adjustmentPoints+" left");
		
		// Invest attribute points
		for (AttributeValue<ShadowrunAttribute> aVal : sorted) {
			ShadowrunAttribute key = aVal.getModifyable();
			PerAttributePoints per = settings.perAttrib.get(key);
			int toPay = aVal.getDistributed()-1 - per.adjust;
			if (toPay>0) {
				int payed = Math.min(toPay, attributePoints);
				per.regular = payed;
				attributePoints -= payed;
				logger.debug("Payed "+payed+" attribute points for "+key);
			}
		}
		logger.debug("After distributing attribute points, there are "+attributePoints+" left");
		
		redistribute = false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#getAdjustmentPointsLeft()
	 */
	@Override
	public int getAdjustmentPointsLeft() {
		return adjustmentPoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#getAttributePointsLeft()
	 */
	@Override
	public int getAttributePointsLeft() {
		return attributePoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canDecreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canDecreaseAdjust(ShadowrunAttribute key) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		return per.adjust>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canIncreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canIncreaseAdjust(ShadowrunAttribute key) {
		if (adjustmentPoints<1) return false;
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#increaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean increaseAdjust(ShadowrunAttribute key) {
		logger.info("increaseAdjust("+key+")");
		if (!canIncreaseAdjust(key)) {
			logger.warn("Trying to increase attribute "+key+" with adjustment points, although not possible");
			return false;
		}
		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		per.adjust++;
		logger.info("Increased attribute points for "+key+" to "+per.regular+" - sum is now "+per.getSum());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#decreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean decreaseAdjust(ShadowrunAttribute key) {
		logger.info("decreaseAdjust("+key+")");
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		per.adjust--;
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canDecreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canDecreaseAttrib(ShadowrunAttribute key) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		return per.regular>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canIncreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canIncreaseAttrib(ShadowrunAttribute key) {
		if (attributePoints<1) return false;
		// TODO Auto-generated method stub
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#increaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean increaseAttrib(ShadowrunAttribute key) {
		logger.info("increaseAttrib("+key+")");
		if (!canIncreaseAttrib(key)) {
			logger.warn("Trying to increase attribute "+key+" with attribute points, although not possible");
			return false;
		}
		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		per.regular++;
		logger.info("Increased attribute points for "+key+" to "+per.regular+" - sum is now "+per.getSum());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#decreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean decreaseAttrib(ShadowrunAttribute key) {
		logger.info("decreaseAttrib("+key+")");
		if (!canDecreaseAttrib(key)) {
			logger.warn("Trying to decrease attribute "+key+" with attribute points, although not possible");
			return false;
		}
		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		per.regular--;
		logger.info("Decreased attribute points for "+key+" to "+per.regular+" - sum is now "+per.getSum());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canDecreaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canDecreaseKarma(ShadowrunAttribute key) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		return per.karma>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canIncreaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canIncreaseKarma(ShadowrunAttribute key) {
		Shadowrun6Character model = parent.getModel();
		if (key==ShadowrunAttribute.RESONANCE && model.getMagicOrResonanceType()!=null && !model.getMagicOrResonanceType().usesResonance())
			return false;
		if (key==ShadowrunAttribute.MAGIC && model.getMagicOrResonanceType()!=null && !model.getMagicOrResonanceType().usesMagic()) {
			return false;
		}

		PerAttributePoints per = model.getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		if (per.getSum()>=getMaximumValue(key))
			return false;

		if (isAnotherAttributeAlreadyMaxed(key))
			return false;
		
		int requiredKarma = (per.getSum()+1)*5;
		if (model.getKarmaFree()<requiredKarma) {
			return false;
		}
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#increaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean increaseKarma(ShadowrunAttribute key) {
		logger.info("increaseKarma("+key+")");
		if (!canIncreaseKarma(key)) {
			logger.warn("Trying to increase attribute "+key+" with Karma, although not possible");
			return false;
		}

		parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key).karma++;
		logger.info("Increased "+key+" with karma");
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#decreaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean decreaseKarma(ShadowrunAttribute key) {
		if (!canDecreaseKarma(key))
			return false;

		parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key).karma--;
		logger.info("Decreased "+key+" with karma");
		parent.runProcessors();
		return true;
	}

	//--------------------------------------------------------------------
	private void updateAttributeValues() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			AttributeValue<ShadowrunAttribute> val = parent.getModel().getAttribute(key);
			if (per.getSum()>7 || ( val.getMaximum()>0 && per.getSum()>val.getMaximum())) {
				logger.error("New value for "+key+":"+per.getSum()+" would exceed maximum of "+val.getMaximum());
				System.exit(1);
			}
			logger.info(key+" = "+per.getSum()+" - "+per.base + " = "+(per.getSum()-per.base));
			parent.getModel().getAttribute(key).setDistributed(per.getSum() - per.base);
		}
	}

	//-------------------------------------------------------------------
	public int getMaximumValue(ShadowrunAttribute key) {
		return parent.getModel().getAttribute(key).getMaximum();
	}

	//-------------------------------------------------------------------
	private List<ShadowrunAttribute> getMaximizedAttributes() {
		List<ShadowrunAttribute> maxed = new ArrayList<ShadowrunAttribute>();
		Shadowrun6Character model = parent.getModel();
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
			PerAttributePoints per = model.getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			if (per.getSum() >= model.getAttribute(key).getMaximum())
				maxed.add(key);
		}
		return maxed;
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
		for (ShadowrunAttribute key : ShadowrunAttribute.specialAttributes()) {
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
			if (per!=null) {
				while (per.karma>0 && per.getSum()>getMaximumValue(key))
					per.karma--;
				while (per.regular>0 && per.getSum()>getMaximumValue(key))
					per.regular--;
				while (per.adjust>0 && per.getSum()>getMaximumValue(key))
					per.adjust--;
			} 
		}
	}
	
	//-------------------------------------------------------------------
	private boolean isAnotherAttributeAlreadyMaxed(ShadowrunAttribute key) {
		if (key.isSpecial())
			return false;

		Collection<ShadowrunAttribute> alreadyMaxed = getMaximizedAttributes();
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		// Only allow to max an attribute, if there isn't one already
		if ((per.getSum()+1)>=getMaximumValue(key) && key.isPrimary()) {
			if (logger.isTraceEnabled())
				logger.trace("Increasing "+key+" would reach maximum of "+getMaximumValue(key)+".  Is already one maxed = "+alreadyMaxed);
			return !alreadyMaxed.isEmpty();
		}
		return false;
	}

	//-------------------------------------------------------------------
	private void reset() {
		adjustmentPoints = 0;
		attributePoints  = 0;
		allowedAdjust.clear();
		for (AttributeValue tmp : getModel().getAttributes()) {
			tmp.clearModifications();
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
		if (logger.isTraceEnabled()) logger.trace("ENTER process");
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
					} else if (mod.getResolvedKey()==CreatePoints.ATTRIBUTES) {
						attributePoints = mod.getValue();
					} else {
						unprocessed.add(mod);
					}
				} else if (tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE) {
					ValueModification mod = (ValueModification)tmp;
					ShadowrunAttribute attr = mod.getResolvedKey();
					getModel().getAttribute(attr).addModification(mod);
					logger.info("Consume "+mod);
					if (mod.getSet()==ValueType.MAX && mod.getValue()>6)
						allowedAdjust.add(attr);
					// Update base
					if (mod.getSet()==ValueType.NATURAL) {
						logger.debug("Updated base of "+attr+" with +"+mod.getValue());
						prioSettings.perAttrib.get(attr).base += mod.getValue();
						logger.debug("Updated base of "+attr+" to +"+prioSettings.perAttrib.get(attr).base);
					}
				} else {
					unprocessed.add(tmp);
				}
			}
			
			logger.debug("Start with "+adjustmentPoints+" adjust and "+attributePoints+" attrib points");
			
			ensureMaximumSet();
			
			
			if (redistribute) {
				calculateDistribution();
			} else {

				// Reduce points
				Shadowrun6Character model = parent.getModel();
				for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
					PerAttributePoints per = prioSettings.perAttrib.get(key);
					if (per == null) {
						logger.warn("No data for " + key);
						continue;
					}
//				per.base = 1;
//				logger.debug("  pay for "+per);
					adjustmentPoints -= per.adjust;
					attributePoints -= per.regular;
					model.setKarmaFree(model.getKarmaFree() - per.getKarmaInvest());
					model.setKarmaInvested(model.getKarmaInvested() + per.getKarmaInvest());
				}
				logger.debug("Finish with " + adjustmentPoints + " adjust and " + attributePoints + " attrib points");
			}
			
			// Copy current setup 
			updateAttributeValues();
			
			return unprocessed;
		} finally {
			if (logger.isTraceEnabled()) logger.trace("LEAVE process");
		}
	}

}
