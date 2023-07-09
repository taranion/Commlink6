package de.rpgframework.shadowrun6.chargen.gen.karma;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyWhen;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.KarmaAttributeGenerator;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6KarmaAttributeGenerator extends CommonAttributeGenerator implements KarmaAttributeGenerator {

//	public final static String I18N_NOT_SPECIAL_OR_RACIAL = "attrib.adjust.noSpecialAttribute";

	private final static Logger logger = System.getLogger(SR6KarmaAttributeGenerator.class.getPackageName()+".attrib");

	//-------------------------------------------------------------------
	public SR6KarmaAttributeGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonAttributeGenerator#canBeIncreased(AttributeValue)
	 */
	@Override
	public Possible canBeIncreased(AttributeValue<ShadowrunAttribute> value) {
		// Check if raising to the new value is allowed
		Possible possible = super.canBeIncreased(value);
		if (!possible.get())
			return possible;

		int karmaNeeded = (value.getDistributed()+1)*5;
		if (parent.getModel().getKarmaFree()<karmaNeeded) return new Possible(IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA);
		return Possible.TRUE;
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

		return new Possible(value.getDistributed()>0);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> increase(AttributeValue<ShadowrunAttribute> value) {
		Possible poss = canBeIncreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to increase {0} though {1}", value, poss.toString());
			return new OperationResult<>(poss);
		}

		value.setDistributed(value.getDistributed()+1);
		int karmaNeeded = value.getDistributed()*5;
		getModel().setKarmaFree    ( getModel().getKarmaFree()     - karmaNeeded);
		getModel().setKarmaInvested( getModel().getKarmaInvested() + karmaNeeded);
		logger.log(Level.INFO, "Increased {0} for {1} Karma", value.getModifyable(), karmaNeeded);

		parent.runProcessors();

		return new OperationResult<AttributeValue<ShadowrunAttribute>>(value);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#decrease(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public OperationResult<AttributeValue<ShadowrunAttribute>> decrease(AttributeValue<ShadowrunAttribute> value) {
		Possible poss = canBeDecreased(value);
		if (!poss.get()) {
			logger.log(Level.WARNING, "Trying to decrease {0} though {1}", value, poss.toString());
			return new OperationResult<>(poss);
		}

		int karmaGranted = value.getDistributed()*5;
		value.setDistributed(value.getDistributed()-1);
		getModel().setKarmaFree    ( getModel().getKarmaFree()     + karmaGranted);
		getModel().setKarmaInvested( getModel().getKarmaInvested() - karmaGranted);
		logger.log(Level.INFO, "Decreased {0} for {1} Karma", value.getModifyable(), karmaGranted);

		parent.runProcessors();

		return new OperationResult<AttributeValue<ShadowrunAttribute>>(value);
	}

	//-------------------------------------------------------------------
	public int getMaximumValue(ShadowrunAttribute key) {
		return parent.getModel().getAttribute(key).getMaximum();
	}

	//-------------------------------------------------------------------
	protected List<ShadowrunAttribute> getMaximizedAttributes() {
		List<ShadowrunAttribute> maxed = new ArrayList<ShadowrunAttribute>();
		Shadowrun6Character model = parent.getModel();
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
			AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
			int current = val.getModifiedValue(ValueType.NATURAL);
			if (current >= model.getAttribute(key).getMaximum())
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
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = parent.getModel().getAttribute(key);
			int base = val.getModifier(ValueType.NATURAL);
			int current = val.getModifiedValue(ValueType.NATURAL);
			int max = getMaximumValue(key);
			if (current>max) {
				logger.log(Level.ERROR,"New value for "+key+":"+current+" would exceed maximum of "+max+" - reduce Karma invest");
				val.setDistributed( max - base);
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
			allowedAdjust.clear();
			todos.clear();
			SR6KarmaSettings settings = getModel().getCharGenSettings(SR6KarmaSettings.class);
			settings.attrib=0;

			// Walk modifications for creation points
			for (Modification tmp : previous) {
				if (tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE) {
					ValueModification mod = (ValueModification)tmp;
					ShadowrunAttribute attr = mod.getResolvedKey();
					logger.log(Level.INFO, "Consume {0}",mod);
					switch (mod.getSet()) {
					case MAX:
						// Optional: Allow adjustment points on lowered maximum
						if (mod.getValue()>6 || parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_ADJUSTMENT_ON_LOWERED_MAX)) {
							logger.log(Level.DEBUG, "Allow adjustment points for {0}",attr);
							allowedAdjust.add(attr);
						}
						getModel().getAttribute(attr).addModification(mod);
						break;
					case NATURAL:
						// Update base
						logger.log(Level.INFO, "Updated base of "+attr+" with +"+mod.getValue());
						getModel().getAttribute(attr).addModification(mod);
						break;
					default:
						getModel().getAttribute(attr).addModification(mod);
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

			ensureMaximumSet();
			ensureMaximumNotExceeded();

			logger.log(Level.INFO, "Start with {0} karma", getModel().getKarmaFree());
			// Reduce points
			Shadowrun6Character model = parent.getModel();
//			logger.log(Level.INFO, "MAGIC = "+model.getAttribute(ShadowrunAttribute.MAGIC));
			for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
				AttributeValue<ShadowrunAttribute> val = getModel().getAttribute(key);
				int karmaNeeded = 0;
				int upTo = val.getDistributed();
				for (int i=2; i<=upTo; i++) {
					int pay = i*5;
					karmaNeeded += pay;
				}
				logger.log(Level.DEBUG, "  Pay {0} Karma for {1} {2}", karmaNeeded, key, upTo);
				if (karmaNeeded > 0) {
					model.setKarmaFree(model.getKarmaFree() - karmaNeeded);
					model.setKarmaInvested(model.getKarmaInvested() + karmaNeeded);
					settings.attrib += karmaNeeded;
				}
			}

			logger.log(Level.INFO, "Leave with {0} Karma", model.getKarmaFree());

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#getValue(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public int getValue(AttributeValue<ShadowrunAttribute> key) {
		return key.getModifiedValue(ValueType.NATURAL);
	}

}
