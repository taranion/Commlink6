package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun6.Persona;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;


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
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				// Technomancer
				calculateTechnomancer(model, persona);				

			} else {
				// Non-Technomancer
				calculateNonTechnomancer(model, persona);
			}
		} finally {
			logger.log(Level.INFO,"STOP : process() ends with "+unprocessed.size()+" modifications still to process");
		}
		return unprocessed;
	}

	//--------------------------------------------------------------------
	private void calculateTechnomancer(Shadowrun6Character model, Persona persona) {
		persona.setName(RES.getString("label.living_persona"));
		// Data Processing = LOGIC
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.DATA_PROCESSING, 
						model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue()));
		// Firewall = WILLOWER
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.FIREWALL, 
						model.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue()));
		// Attack = CHARISMA
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.ATTACK, 
						model.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue()));
		// Sleaze = INTUITION
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.SLEAZE, 
						model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()));

		// Device Rating = RESONANCE
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.DEVICE_RATING, 
						model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue()));
		// Attack rating
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.ATTACK_RATING,
				persona.getAttack().getModifiedValue() + 
				persona.getSleaze().getModifiedValue()));
		// Defense rating
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.DEFENSE_PHYSICAL,
				persona.getDataProcessing().getModifiedValue() + 
				persona.getFirewall().getModifiedValue()));

		// Initiative
		persona.setAttribute(new AttributeValue(ShadowrunAttribute.INITIATIVE_MATRIX, 
				model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue()+
				model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()));
		persona.setAttribute(new AttributeValue(ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD, 
				model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue()+
				model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()));
		persona.setAttribute(new AttributeValue(ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT, 
				model.getAttribute(ShadowrunAttribute.LOGIC).getModifiedValue()+
				model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()));
		persona.setAttribute(model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX));
		persona.setAttribute(model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD));
		persona.setAttribute(model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_HOT));
		
		// Matrix condition monitor
		persona.setMonitor(getTechnomancerMonitorArray(model));
	}

	//--------------------------------------------------------------------
	private void calculateNonTechnomancer(Shadowrun6Character model, Persona persona) {
		CarriedItem bestAccessDevice = null;

		/*
		 * Find the best cyberdeck
		 */
		CarriedItem<ItemTemplate> bestAS = null;
		int bestSum = 0;
		for (CarriedItem item : model.getCarriedItems()) {
			if (!item.hasAttribute(SR6ItemAttribute.ATTACK))
				continue;
//			if (logger.isTraceEnabled())
//				logger.trace("  consider for AS: "+item);
			int a = item.getAsValue(SR6ItemAttribute.ATTACK).getModifiedValue();
			int s = item.getAsValue(SR6ItemAttribute.SLEAZE).getModifiedValue();
			int sum = a+s;
			if (sum>bestSum) {
				// Previous best is not best anymor
				if (bestAS!=null) bestAS.setPrimary(false);
				bestAS = item;
				bestSum= sum;
				bestAS.setPrimary(true);
				persona.setAttribute(item.getAsValue(SR6ItemAttribute.ATTACK));
				persona.setAttribute(item.getAsValue(SR6ItemAttribute.SLEAZE));
			}
			// Device rating
			if (item.hasAttribute(SR6ItemAttribute.DEVICE_RATING)) {
				int dr = item.getAsValue(SR6ItemAttribute.DEVICE_RATING).getModifiedValue();
				if (bestAccessDevice==null || dr>bestAccessDevice.getAsValue(SR6ItemAttribute.DEVICE_RATING).getModifiedValue()) {
					bestAccessDevice = item;
				}
			}
		}
		if (bestAS!=null)
			bestAccessDevice = bestAS;
		logger.log(Level.INFO, "best device for AS: "+bestAS);

		/*
		 * Find the best commlink or cyber jack
		 */
		CarriedItem<ItemTemplate> bestDF = null;
		bestSum = 0;
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
			if (!item.hasAttribute(SR6ItemAttribute.DATA_PROCESSING))
				continue;
			logger.log(Level.INFO, "  consider for DF: "+item);
			int d = item.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue();
			int f = item.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue();
			int sum = d+f;
			if (sum>bestSum) {
				// Previous best DF is not primary anymore
				if (bestDF!=null) {
					bestDF.setPrimary(false);
				}
				bestDF = item;
				bestSum= sum;
				bestDF.setPrimary(true);
				persona.setAttribute(item.getAsValue(SR6ItemAttribute.DATA_PROCESSING));
				persona.setAttribute(item.getAsValue(SR6ItemAttribute.FIREWALL));
			}
			// Device rating
			if (item.hasAttribute(SR6ItemAttribute.DEVICE_RATING) && bestAS==null) {
				int dr = item.getAsValue(SR6ItemAttribute.DEVICE_RATING).getModifiedValue();
				if (bestAccessDevice==null || dr>bestAccessDevice.getAsValue(SR6ItemAttribute.DEVICE_RATING).getModifiedValue()) {
					bestAccessDevice = item;
				}
			}
		}
		if (bestAS!=null)
			bestAccessDevice = bestAS;
		logger.log(Level.INFO, "best device for DF: "+bestDF);
		logger.log(Level.INFO, "best access device: "+bestAccessDevice);

		// Device rating
		if (bestAccessDevice!=null)
			persona.setAttribute(bestAccessDevice.getAsValue(SR6ItemAttribute.DEVICE_RATING));
		else
			persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.DEVICE_RATING,0));
		// Attack rating
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.ATTACK_RATING,
				persona.getAttack().getModifiedValue() + 
				persona.getSleaze().getModifiedValue()));
		// Defense rating
		persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.DEFENSE_PHYSICAL,
				persona.getDataProcessing().getModifiedValue() + 
				persona.getFirewall().getModifiedValue()));
		// Active program slots
		if (bestAccessDevice!=null)
			persona.setAttribute(bestAccessDevice.getAsValue(SR6ItemAttribute.CONCURRENT_PROGRAMS));
		else
			persona.setAttribute(new ItemAttributeNumericalValue(SR6ItemAttribute.CONCURRENT_PROGRAMS,0));
		
		/*
		 * Initiative (CRB 179)
		 */
		persona.setAttribute(model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX));
		persona.setAttribute(model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX));
		persona.setAttribute(new AttributeValue(ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD, 
				model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()+
				persona.getDataProcessing().getModifiedValue()));
		persona.setAttribute(new AttributeValue(ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT, 
				model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()+
				persona.getDataProcessing().getModifiedValue()));
		
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
	public static int[] getTechnomancerMonitorArray(ShadowrunCharacter model) {
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

}
