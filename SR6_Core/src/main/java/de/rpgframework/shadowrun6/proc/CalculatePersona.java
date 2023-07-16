package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Persona;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;


/**
 * @author prelle
 *
 */
public class CalculatePersona implements ProcessingStep {

	private MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

	private final static Logger logger = System.getLogger(CalculatePersona.class.getPackageName());

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public CalculatePersona(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(previous);

		logger.log(Level.INFO,"START: process");
		try {
			// Ensure a persona is present
			Persona persona = model.getPersona();
			if (persona==null) {
				persona = new Persona();
				model.setPersona(persona);
			} else
				persona.clear();

			/*
			 * Normally a persona is build from the used commlink or cyber jack
			 * plus the cyberdeck. Technomancers use their living persona.
			 */
			if (model.getMetatype()!=null && model.getMetatype().isAI()) {
				// Virtual Life
				calculateAI(model, persona);
			} else if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				// Technomancer
				calculateTechnomancer(model, persona);
			} else {
				// Non-Technomancer
				calculateNonTechnomancer(model, persona);
			}

			logger.log(Level.INFO, "ASDF: {0}  {1}  {2}  {3}", persona.getAttack(), persona.getSleaze(), persona.getDataProcessing(), persona.getFirewall());
		} finally {
			logger.log(Level.INFO,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//--------------------------------------------------------------------
	private void calculateTechnomancer(Shadowrun6Character model, Persona persona) {
		persona.setName(RES.getString("label.living_persona"));

		ItemAttributeNumericalValue<SR6ItemAttribute> valD = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.DATA_PROCESSING);
		ItemAttributeNumericalValue<SR6ItemAttribute> valF = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.FIREWALL);
		ItemAttributeNumericalValue<SR6ItemAttribute> valA = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.ATTACK);
		ItemAttributeNumericalValue<SR6ItemAttribute> valS = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.SLEAZE);
		ItemAttributeNumericalValue<SR6ItemAttribute> valR = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.DEVICE_RATING);

		// Data Processing = LOGIC
		addNaturalModifier(valD, model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue(), ShadowrunAttribute.LOGIC);
		// Firewall = WILLOWER
		addNaturalModifier(valF, model.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue(), ShadowrunAttribute.WILLPOWER);
		// Attack = CHARISMA
		addNaturalModifier(valA, model.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue(), ShadowrunAttribute.CHARISMA);
		// Sleaze = INTUITION
		addNaturalModifier(valS, model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue(), ShadowrunAttribute.INTUITION);

		// Device Rating = RESONANCE
		addNaturalModifier(valR, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue(), ShadowrunAttribute.RESONANCE);

		// Matrix condition monitor
		persona.setMonitor(getTechnomancerMonitorArray(model));

		persona.setAttribute(valA);
		persona.setAttribute(valS);
		persona.setAttribute(valD);
		persona.setAttribute(valF);
	}

	//--------------------------------------------------------------------
	private void calculateAI(Shadowrun6Character model, Persona persona) {
		persona.setName(RES.getString("label.living_persona"));

		ItemAttributeNumericalValue<SR6ItemAttribute> valD = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.DATA_PROCESSING);
		ItemAttributeNumericalValue<SR6ItemAttribute> valF = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.FIREWALL);
		ItemAttributeNumericalValue<SR6ItemAttribute> valA = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.ATTACK);
		ItemAttributeNumericalValue<SR6ItemAttribute> valS = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.SLEAZE);
		ItemAttributeNumericalValue<SR6ItemAttribute> valR = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.DEVICE_RATING);

		// Data Processing = AGILITY
		addNaturalModifier(valD, model.getAttribute(ShadowrunAttribute.AGILITY).getModifiedValue(), SR6ItemAttribute.DATA_PROCESSING);
		// Firewall = BODY
		addNaturalModifier(valF, model.getAttribute(ShadowrunAttribute.BODY).getModifiedValue(), SR6ItemAttribute.FIREWALL);
		// Attack = STRENGTH
		addNaturalModifier(valA, model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue(), SR6ItemAttribute.ATTACK);
		// Sleaze = REACTION
		addNaturalModifier(valS, model.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue(), SR6ItemAttribute.SLEAZE);

		// Device Rating = RESONANCE
		addNaturalModifier(valR, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue(), ShadowrunAttribute.RESONANCE);

		// Matrix condition monitor
		persona.setMonitor(getNormalMonitorArray(persona.getDeviceRating()));

		persona.setAttribute(valA);
		persona.setAttribute(valS);
		persona.setAttribute(valD);
		persona.setAttribute(valF);
	}

	//--------------------------------------------------------------------
	private void calculateNonTechnomancer(Shadowrun6Character model, Persona persona) {
		CarriedItem<ItemTemplate> bestDF = Shadowrun6Tools.getPrimaryMatrixDF(model);
		CarriedItem<ItemTemplate> bestAS = Shadowrun6Tools.getPrimaryMatrixAS(model);
		logger.log(Level.INFO, "best device for DF: "+bestDF);
		logger.log(Level.INFO, "best access device: "+bestAS);

		ItemAttributeNumericalValue<SR6ItemAttribute> valD = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.DATA_PROCESSING);
		ItemAttributeNumericalValue<SR6ItemAttribute> valF = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.FIREWALL);
		ItemAttributeNumericalValue<SR6ItemAttribute> valA = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.ATTACK);
		ItemAttributeNumericalValue<SR6ItemAttribute> valS = new ItemAttributeNumericalValue<SR6ItemAttribute>(SR6ItemAttribute.SLEAZE);

		// If both devices are selected, check for current attribute distribution
		// otherwise only do standard
		if (bestDF==null && bestAS==null) {
		} else if (bestDF!=null && bestAS==null) {
			addNaturalModifier(valA, bestDF.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getDistributed(), bestDF);
			addNaturalModifier(valA, bestDF.getAsValue(SR6ItemAttribute.FIREWALL).getDistributed(), bestDF);
		} else if (bestDF==null && bestAS!=null) {
			addNaturalModifier(valA, bestAS.getAsValue(SR6ItemAttribute.ATTACK).getDistributed(), bestAS);
			addNaturalModifier(valA, bestAS.getAsValue(SR6ItemAttribute.SLEAZE).getDistributed(), bestAS);
		} else {
			SR6ItemAttribute attrD = model.getMatrixAttributeMapping(SR6ItemAttribute.DATA_PROCESSING);
			SR6ItemAttribute attrF = model.getMatrixAttributeMapping(SR6ItemAttribute.FIREWALL);
			SR6ItemAttribute attrA = model.getMatrixAttributeMapping(SR6ItemAttribute.ATTACK);
			SR6ItemAttribute attrS = model.getMatrixAttributeMapping(SR6ItemAttribute.SLEAZE);

			if (attrD==SR6ItemAttribute.SLEAZE || attrD==SR6ItemAttribute.ATTACK)
				addNaturalModifier(valD, bestAS.getAsValue(attrD).getDistributed(), bestAS);
			else
				addNaturalModifier(valD, bestDF.getAsValue(attrD).getDistributed(), bestDF);

			if (attrF==SR6ItemAttribute.SLEAZE || attrF==SR6ItemAttribute.ATTACK)
				addNaturalModifier(valF, bestAS.getAsValue(attrF).getDistributed(), bestAS);
			else
				addNaturalModifier(valF, bestDF.getAsValue(attrF).getDistributed(), bestDF);

			if (attrA==SR6ItemAttribute.SLEAZE || attrA==SR6ItemAttribute.ATTACK)
				addNaturalModifier(valA, bestAS.getAsValue(attrA).getDistributed(), bestAS);
			else
				addNaturalModifier(valA, bestDF.getAsValue(attrA).getDistributed(), bestDF);

			if (attrS==SR6ItemAttribute.SLEAZE || attrS==SR6ItemAttribute.ATTACK)
				addNaturalModifier(valS, bestAS.getAsValue(attrS).getDistributed(), bestAS);
			else
				addNaturalModifier(valS, bestDF.getAsValue(attrS).getDistributed(), bestDF);

		}
		persona.setAttribute(valA);
		persona.setAttribute(valS);
		persona.setAttribute(valD);
		persona.setAttribute(valF);

		List<Modification> allItemMods = new ArrayList<>();
		if (bestAS!=null) allItemMods.addAll(bestAS.getModifications());
		if (bestDF!=null) allItemMods.addAll(bestDF.getModifications());
		for (Modification mod : allItemMods) {
			logger.log(Level.INFO, "TODO: item mod "+mod);
		}
		AttributeValue<ShadowrunAttribute> val = null;

		// Device rating
		if (bestAS!=null)
			persona.setAttribute(bestAS.getAsValue(SR6ItemAttribute.DEVICE_RATING));
		else
			persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.DEVICE_RATING,0));


		// Active program slots
		if (bestAS!=null && bestAS.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS)!=null)
			persona.setAttribute(bestAS.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS));
		else
			persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.CONCURRENT_PROGRAMS,0));


		// Matrix condition monitor
		persona.setMonitor(getNormalMonitorArray(persona.getDeviceRating()));

		if (bestAS==null && bestDF!=null)
			persona.setName(bestDF.getNameWithoutRating());
		else if (bestAS!=null && bestDF!=null)
			persona.setName(bestDF.getNameWithoutRating()+" + "+bestAS.getNameWithoutRating());
		else {
			persona.setName("-None-");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * CRB 189: Their Matrix Condition Monitor is (Logic/2, rounded up) + 8.
	 */
	public static int[] getTechnomancerMonitorArray(Shadowrun6Character model) {
		return Shadowrun6Tools.getMonitorArray(model, ShadowrunAttribute.WILLPOWER);
	}

	//-------------------------------------------------------------------
	/**
	 * CRB 179: Devices have a Matrix Condition Monitor equal to
	 * [(Device Rating / 2) + 8].
	 */
	public static int[] getNormalMonitorArray(int devRating) {
		int stun = Math.round(devRating/2.0f) + 8;
		int[] ret = new int[stun];

		int start = 0;
		int every = 3;

		for (int i=start; i<ret.length; i++) {
			ret[i] = - ((i+1-start)/every);
		}

		return ret;
	}

	//-------------------------------------------------------------------
	private void addNaturalModifier(ItemAttributeNumericalValue<SR6ItemAttribute> val, int value, Object source) {
		ValueModification valMod = new ValueModification(ShadowrunReference.ITEM_ATTRIBUTE, val.getModifyable().name(), value, source);
		valMod.setSet(ValueType.NATURAL);
		val.addModification( valMod );
	}

	//-------------------------------------------------------------------
	static void addNaturalModifier(AttributeValue<ShadowrunAttribute> val, CarriedItem<ItemTemplate> item, SR6ItemAttribute attr) {
		if (item==null) return;
		if (!item.hasAttribute(attr)) {
			logger.log(Level.ERROR, "Item {0} does not have attribute {1}", item.getKey(), attr);
			return;
		}
		ValueModification valMod = new ValueModification(ShadowrunReference.ATTRIBUTE, val.getModifyable().name(), item.getAsValue(attr).getModifiedValue(), attr);
		valMod.setSet(ValueType.NATURAL);
		valMod.setSource(attr);
		val.addModification( valMod );
	}

}
