package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.ApplyWhen;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PointBuyAttributeGenerator extends CommonAttributeGenerator implements IAttributeController {
	
	public final static String I18N_NOT_SPECIAL_OR_RACIAL = "attrib.adjust.noSpecialAttribute";

	private static MultiLanguageResourceBundle RES = PointBuyCharacterGenerator.RES;
	
	private final static Logger logger = System.getLogger(PointBuyAttributeGenerator.class.getPackageName()+".attrib");
	
	private int specialFromCP;
	private int attribFromCP;
	private int special;
	private int attrib;
	private List<ShadowrunAttribute> allowedAdjust = new ArrayList<>();
	private boolean redistribute;
	
	//-------------------------------------------------------------------
	public PointBuyAttributeGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	public static String getHumanReadable(Possible result, Locale loc) {
		return RES.getString(result.getI18NKey(), loc);
	}

	//-------------------------------------------------------------------
	public Possible canBeDecreasedPoints(AttributeValue<ShadowrunAttribute> value) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(value.getModifyable());
		return (per.points1>0)?Possible.TRUE:Possible.FALSE;
	}

	//-------------------------------------------------------------------
	public Possible canBeIncreasedPoints(AttributeValue<ShadowrunAttribute> value) {
		if (value.getModifyable().isPrimary() && !allowedAdjust.contains(value.getModifyable()))
			return new Possible(I18N_NOT_SPECIAL_OR_RACIAL);
		
		if (special>0) return Possible.TRUE;
		// You may purchase up to 12 additional adjustment points at 4 CP each
		if (specialFromCP>=12) return new Possible("attrib.adjust.already12CP");
		if (parent.getModel().getCharGenSettings(SR6PointBuySettings.class).characterPoints<4) return new Possible("attrib.adjust.notEnoughCP");
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	public OperationResult<AttributeValue<ShadowrunAttribute>> increasePoints(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		Possible poss = canBeIncreasedPoints(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to increase attribute "+key+" with adjustment points, although not possible: "+poss);
			return new OperationResult<>(poss);
		}
		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		per.points1++;
		logger.log(Level.INFO, "Increased {} to {}", key, per.getSum());
		
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	public OperationResult<AttributeValue<ShadowrunAttribute>> decreasePoints(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		logger.log(Level.INFO, "decreasePoints("+key+")");
		Possible poss = canBeDecreasedPoints(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to decrease attribute "+key+" with adjustment points, although not possible: "+poss);
			return new OperationResult<>(poss);
		}

		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		per.points1--;		
		logger.log(Level.INFO, "Decreased {} to {}", key, per.getSum());
		
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeIncreased(AttributeValue)
	 */
	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		Possible allowed = canBeIncreased(value); 
		if (!allowed.get())
			return allowed;
		ShadowrunAttribute key = value.getModifyable();
		return Possible.TRUE;
//		return canIncreaseAttrib(key) || canIncreasePoints3(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeDecreased(AttributeValue)
	 */
	@Override
	public Possible canBeDecreased(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		return Possible.TRUE;
//		return canDecreaseAttrib(key) || canDecreasePoints3(key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increase(AttributeValue<ShadowrunAttribute> value) {
//		OperationResult<AttributeValue<ShadowrunAttribute>> result = super.increase(value);
//		logger.log(Level.INFO, "Increased "+value.getModifyable()+" to "+value.getModifiedValue());
//		int t1 = value.getModifiedValue();
//		
//		parent.runProcessors();
//		int t2 = value.getModifiedValue();
//		logger.log(Level.INFO, "Increased2 "+value.getModifyable()+" to "+value.getModifiedValue());
//		if (t1!=t2) {
//			logger.log(Level.ERROR, "Increasing "+value.getModifyable()+" failed");
//			return new OperationResult<>();
//		}
//
//		return result;
		return new OperationResult<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decrease(AttributeValue<ShadowrunAttribute> value) {
//		OperationResult<AttributeValue<ShadowrunAttribute>> decreased = super.decrease(value);
//		logger.log(Level.INFO, "Decreased "+value.getModifyable()+" to "+value.getModifiedValue());
//		
//		parent.runProcessors();
//
//		return decreased;
		return new OperationResult<>();
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
		SR6PointBuySettings settings = parent.getModel().getCharGenSettings(SR6PointBuySettings.class);
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
		
//		// Pay MAGIC, RESONANCE and EDGE with adjustment points
//		payAdjustment(settings, ShadowrunAttribute.EDGE );
//		if (parent.getModel().getMagicOrResonanceType()!=null && parent.getModel().getMagicOrResonanceType().usesMagic())
//			payAdjustment(settings, ShadowrunAttribute.MAGIC);
//		if (parent.getModel().getMagicOrResonanceType()!=null && parent.getModel().getMagicOrResonanceType().usesResonance())
//			payAdjustment(settings, ShadowrunAttribute.RESONANCE);
//		
//		// Invest remaining adjustment points
//		for (AttributeValue<ShadowrunAttribute> aVal : sorted) {
//			ShadowrunAttribute key = aVal.getModifyable();
//			if (key==ShadowrunAttribute.EDGE || key==ShadowrunAttribute.MAGIC || key==ShadowrunAttribute.RESONANCE)
//				continue;
//			
//			int toPay = aVal.getModifiedValue() - settings.perAttrib.get(key).getSum();
//			logger.log(Level.DEBUG, "  "+key+" is at "+settings.perAttrib.get(key).getSum()+" - need "+toPay+" more");
//			if (toPay>0 && allowedAdjust.contains(aVal.getModifyable())) {
//				int payed = Math.min(toPay, adjustmentPoints);
//				settings.perAttrib.get(key).adjust = payed;
//				adjustmentPoints -= payed;
//				logger.log(Level.DEBUG, "Payed "+payed+" adjustment points for "+key);
//			}
//		}
//		logger.log(Level.DEBUG, "After distributing adjustment points, there are "+adjustmentPoints+" left");
//		
//		// Invest attribute points
//		for (AttributeValue<ShadowrunAttribute> aVal : sorted) {
//			ShadowrunAttribute key = aVal.getModifyable();
//			PerAttributePoints per = settings.perAttrib.get(key);
//			int toPay = aVal.getDistributed()-1 - per.adjust;
//			if (toPay>0) {
//				int payed = Math.min(toPay, attributePoints);
//				per.regular = payed;
//				attributePoints -= payed;
//				logger.log(Level.DEBUG, "Payed "+payed+" attribute points for "+key);
//			}
//		}
//		logger.log(Level.DEBUG, "After distributing attribute points, there are "+attributePoints+" left");
		
		redistribute = false;
	}

	//-------------------------------------------------------------------
	public Possible canBeDecreasedPoints2(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		return (per.points2>0)?Possible.TRUE:Possible.FALSE;
	}

	// -------------------------------------------------------------------
	public Possible canBeIncreasedPoints2(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		if (!key.isPrimary())
			return new Possible("attrib.attrib.noCharacterAttribute");
		
		// Only 20 attribute points may be generated from CP
		if (attribFromCP >= 20)
			return new Possible("attrib.attrib.already20CP");
		// Every conversion costs 2 CP
		if (parent.getModel().getCharGenSettings(SR6PointBuySettings.class).characterPoints < 2)
			return new Possible("attrib.attrib.notEnoughCP");

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	public OperationResult<AttributeValue<ShadowrunAttribute>> increasePoints2(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		logger.log(Level.INFO, "increasePoints2("+key+")");
		Possible poss = canBeIncreasedPoints2(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to increase attribute "+key+" as regular attribute with CP, although not possible");
			return new OperationResult<>(poss);
		}
		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		per.points2++;
		logger.log(Level.INFO, "Increased attribute points for "+key+" to "+per.points2+" - sum is now "+per.getSum());
		
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	public OperationResult<AttributeValue<ShadowrunAttribute>> decreasePoints2(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		logger.log(Level.INFO, "decreasePoints2("+key+")");
		Possible allowed = canBeDecreasedPoints2(value);
		if (!allowed.get()) {
			logger.log(Level.WARNING, "Trying to decrease attribute "+key+" with attribute points, although not possible");
			return new OperationResult<>(allowed);
		}
		
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		per.points2--;
		logger.log(Level.INFO, "Decreased attribute points for "+key+" to "+per.points2+" - sum is now "+per.getSum());
		
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	public Possible canBeDecreasedPoints3(AttributeValue<ShadowrunAttribute> value) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(value.getModifyable());
		return new Possible(per.points3>0);
	}

	//-------------------------------------------------------------------
	public Possible canBeIncreasedPoints3(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		Shadowrun6Character model = parent.getModel();
		if (key==ShadowrunAttribute.RESONANCE && model.getMagicOrResonanceType()!=null && !model.getMagicOrResonanceType().usesResonance())
			return Possible.FALSE;
		if (key==ShadowrunAttribute.MAGIC && model.getMagicOrResonanceType()!=null && !model.getMagicOrResonanceType().usesMagic()) {
			return Possible.FALSE;
		}

		PerAttributePoints per = model.getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		if (per.getSum()>=getMaximumValue(key))
			return Possible.FALSE;

		if (isAnotherAttributeAlreadyMaxed(key))
			return Possible.FALSE;
		
		int requiredKarma = (per.getSum()+1)*5;
		if (model.getKarmaFree()<requiredKarma) {
			return Possible.FALSE;
		}
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	public OperationResult<AttributeValue<ShadowrunAttribute>> increasePoints3(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		logger.log(Level.INFO, "increasePoints3("+key+")");
		if (!canBeIncreasedPoints3(value).get()) {
			logger.log(Level.WARNING, "Trying to increase attribute "+key+" with Karma, although not possible");
			return new OperationResult<>();
		}

		parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key).points3++;
		logger.log(Level.INFO, "Increased "+key+" with karma");
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//-------------------------------------------------------------------
	public OperationResult<AttributeValue<ShadowrunAttribute>> decreasePoints3(AttributeValue<ShadowrunAttribute> value) {
		ShadowrunAttribute key = value.getModifyable();
		if (!canBeDecreasedPoints3(value).get())
			return new OperationResult<>();

		parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key).points3--;
		logger.log(Level.INFO, "Decreased "+key+" with karma");
		parent.runProcessors();
		return new OperationResult<>(value);
	}

	//--------------------------------------------------------------------
	private void updateAttributeValues() {
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
			AttributeValue<ShadowrunAttribute> val = parent.getModel().getAttribute(key);
			if (per.getSum()>7 || ( val.getMaximum()>0 && per.getSum()>val.getMaximum())) {
				logger.log(Level.ERROR, "New value for "+key+":"+per.getSum()+" would exceed maximum of "+val.getMaximum());
				System.exit(1);
			}
			if (logger.isLoggable(Level.DEBUG))
				logger.log(Level.DEBUG, key+" = "+per.getSum()+" - "+val.getModifier() + " = "+(per.getSum()-val.getModifier()));
			parent.getModel().getAttribute(key).setDistributed(per.getSum() - val.getModifier());
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
			PerAttributePoints per = model.getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
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
			PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
			if (per!=null) {
				while (per.points3>0 && per.getSum()>getMaximumValue(key))
					per.points3--;
				while (per.points2>0 && per.getSum()>getMaximumValue(key))
					per.points2--;
				while (per.points1>0 && per.getSum()>getMaximumValue(key))
					per.points1--;
			} 
		}
	}
	
	//-------------------------------------------------------------------
	private boolean isAnotherAttributeAlreadyMaxed(ShadowrunAttribute key) {
		if (key.isSpecial())
			return false;

		Collection<ShadowrunAttribute> alreadyMaxed = getMaximizedAttributes();
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perAttrib.get(key);
		// Only allow to max an attribute, if there isn't one already
		if ((per.getSum()+1)>=getMaximumValue(key) && key.isPrimary()) {
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, "Increasing "+key+" would reach maximum of "+getMaximumValue(key)+".  Is already one maxed = "+alreadyMaxed);
			return !alreadyMaxed.isEmpty();
		}
		return false;
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
			allowedAdjust.clear();
			todos.clear();
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			settings.perAttrib.get(ShadowrunAttribute.MAGIC).base=0;
			settings.perAttrib.get(ShadowrunAttribute.RESONANCE).base=0;
			settings.perAttrib.get(ShadowrunAttribute.EDGE).base=1;
			special = 1;
			attrib  = 4;
			
			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE) {
					ValueModification mod = (ValueModification)tmp;
					ShadowrunAttribute attr = mod.getResolvedKey();
					getModel().getAttribute(attr).addModification(mod);
					logger.log(Level.DEBUG, "Consume "+mod);
					if (mod.getSet()==ValueType.MAX && mod.getValue()>6)
						allowedAdjust.add(attr);
					// Update base
					if (mod.getSet()==ValueType.NATURAL) {
						logger.log(Level.DEBUG, "Updated base of "+attr+" with +"+mod.getValue());
						settings.perAttrib.get(attr).base += mod.getValue();
						logger.log(Level.DEBUG, "Updated base of "+attr+" to "+settings.perAttrib.get(attr).base);
					}
				} else {
					unprocessed.add(tmp);
				}
			}
			logger.log(Level.DEBUG, "Attributes allowed for adjustment points: "+allowedAdjust);
			
			ensureMaximumSet();
			
			
			if (redistribute) {
				calculateDistribution();
			} else {

				// Reduce points
				Shadowrun6Character model = parent.getModel();
				for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
					PerAttributePoints per = settings.perAttrib.get(key);
					if (per == null) {
						logger.log(Level.WARNING, "No data for " + key);
						continue;
					}
					/* 
					 * Pay special points 
					 */
					int requiredSpecial = per.points1;
					if (requiredSpecial>0) {
						if (special>0) {
							int pay = Math.min(special, requiredSpecial);
							logger.log(Level.DEBUG, "Pay {} free CP for {}", pay, key);
							special -= pay;
							requiredSpecial -= pay;
						}
						// If not enough, convert
						if (requiredSpecial>0) {
							int pay = requiredSpecial*4;
							logger.log(Level.DEBUG, "Convert {} CP to {} special points for {}", pay, requiredSpecial, key);
							settings.characterPoints -= pay;
							settings.cpBoughtSpecial += requiredSpecial;
							requiredSpecial -= pay;
						}
					}
					/* 
					 * Pay attribute points 
					 */
					int requiredAttrib = per.points2;
					if (requiredAttrib>0) {
						if (attrib>0) {
							int pay = Math.min(attrib, requiredAttrib);
							logger.log(Level.DEBUG, "Pay {} free CP for {}", pay, key);
							attrib -= pay;
							requiredAttrib -= pay;
						}
						// If not enough, convert
						if (requiredAttrib>0) {
							int pay = requiredAttrib*2;
							logger.log(Level.DEBUG, "Convert {} CP to {} special points for {}", pay, requiredAttrib, key);
							settings.characterPoints -= pay;
							settings.cpBoughtAttrib += requiredAttrib;
							requiredAttrib -= pay;
						}
					}
					/* 
					 * Pay Karma
					 */
					if (per.getKarmaInvest() > 0) {
						logger.log(Level.INFO, "Pay {} Karma for {}", per.getKarmaInvest(), key);
						model.setKarmaFree(model.getKarmaFree() - per.getKarmaInvest());
						model.setKarmaInvested(model.getKarmaInvested() + per.getKarmaInvest());
					}
				}
				logger.log(Level.DEBUG, "Finish with " + special + " special and " + attrib + " attrib points");
				if (logger.isLoggable(Level.TRACE))
					logger.log(Level.TRACE, settings.toAttributeString());
			}
			
			// Copy current setup 
			updateAttributeValues();

			
			logger.log(Level.INFO, "{} CP converted to {} special attributes", settings.cpBoughtSpecial*4, settings.cpBoughtSpecial);
			logger.log(Level.INFO, "{} CP converted to {} regular attributes", settings.cpBoughtAttrib*2, settings.cpBoughtAttrib);
			
			/*
			 * Ensure limits of 12 / 20 are in kept
			 */
			if (settings.cpBoughtAttrib>20) {
				todos.add(new ToDoElement(Severity.STOPPER, "Too many CP converted to regular attributes"));
			}
			if (settings.cpBoughtSpecial>12) {
				todos.add(new ToDoElement(Severity.STOPPER, "Too many CP converted to special attributes"));
			}
			
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
