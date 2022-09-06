package de.rpgframework.shadowrun6;

import java.util.HashMap;
import java.util.Map;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author stefa
 *
 */
public class Persona {
	
	private final static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();
	
	private Map<SR6ItemAttribute, ItemAttributeValue> attributes;
	private Map<ShadowrunAttribute, AttributeValue> attributes2;
	private String name;
	private int[] monitor;
	
	
	//--------------------------------------------------------------------
	public Persona() {
		attributes = new HashMap<>();
		attributes2= new HashMap<>();
		clear();
	}

	//--------------------------------------------------------------------
	public void setAttribute(ItemAttributeValue<SR6ItemAttribute> value) {
		attributes.put(value.getModifyable(), value);
	}

//--------------------------------------------------------------------
	public void setAttribute(AttributeValue<ShadowrunAttribute> value) {
		attributes2.put(value.getModifyable(), value);
	}

	//--------------------------------------------------------------------
	public void setName(String value) { this.name = value; }
	public String getName() { return name; }

	//--------------------------------------------------------------------
	public void clear() {
		attributes.clear();
		attributes.put(SR6ItemAttribute.ATTACK, new ItemAttributeNumericalValue(SR6ItemAttribute.ATTACK, 0));
		attributes.put(SR6ItemAttribute.SLEAZE, new ItemAttributeNumericalValue(SR6ItemAttribute.SLEAZE, 0));
		attributes.put(SR6ItemAttribute.DATA_PROCESSING, new ItemAttributeNumericalValue(SR6ItemAttribute.DATA_PROCESSING, 0));
		attributes.put(SR6ItemAttribute.FIREWALL, new ItemAttributeNumericalValue(SR6ItemAttribute.FIREWALL, 0));
		attributes.put(SR6ItemAttribute.DEVICE_RATING, new ItemAttributeNumericalValue(SR6ItemAttribute.DEVICE_RATING, 0));
		attributes.put(SR6ItemAttribute.ATTACK_RATING, new ItemAttributeNumericalValue(SR6ItemAttribute.ATTACK_RATING, 0));
		attributes.put(SR6ItemAttribute.DEFENSE_PHYSICAL, new ItemAttributeNumericalValue(SR6ItemAttribute.DEFENSE_PHYSICAL, 0));
		attributes.put(SR6ItemAttribute.CONCURRENT_PROGRAMS, new ItemAttributeNumericalValue(SR6ItemAttribute.CONCURRENT_PROGRAMS, 0));

		attributes2.clear();
		attributes2.put(ShadowrunAttribute.INITIATIVE_DICE_MATRIX, new AttributeValue(ShadowrunAttribute.INITIATIVE_DICE_MATRIX, 0));
		attributes2.put(ShadowrunAttribute.INITIATIVE_MATRIX, new AttributeValue(ShadowrunAttribute.INITIATIVE_MATRIX, 0));
	}

	public String dump() {
		StringBuffer buf = new StringBuffer();
		for (SR6ItemAttribute key : SR6ItemAttribute.values()) {
			if (attributes.containsKey(key)) 
				buf.append(String.format("%15s: %s\n", key.name(), attributes.get(key).toString()));
		}
		
		return buf.toString();
	}
	
	//--------------------------------------------------------------------
	public ItemAttributeNumericalValue getAttack() {
		return (ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.ATTACK);
	}

	//--------------------------------------------------------------------
	public ItemAttributeNumericalValue getSleaze() {
		return (ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.SLEAZE);
	}

	//--------------------------------------------------------------------
	public ItemAttributeNumericalValue getDataProcessing() {
		return (ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.DATA_PROCESSING);
	}

	//--------------------------------------------------------------------
	public ItemAttributeNumericalValue getFirewall() {
		return (ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.FIREWALL);
	}

	//--------------------------------------------------------------------
	public int getDeviceRating() {
		return ((ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.DEVICE_RATING)).getModifiedValue();
	}

	//--------------------------------------------------------------------
	public int getAttackRating() {
		return ((ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.ATTACK_RATING)).getModifiedValue();
	}

	//--------------------------------------------------------------------
	public int getDefenseRating() {
		return ((ItemAttributeNumericalValue) attributes.get(SR6ItemAttribute.DEFENSE_PHYSICAL)).getModifiedValue();
	}

	//--------------------------------------------------------------------
	public String getInitiativeStringAR() {		
		return RES.format("label.ini", 
				attributes2.get(ShadowrunAttribute.INITIATIVE_MATRIX).getModifiedValue(), 
				attributes2.get(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue());
	}

	//--------------------------------------------------------------------
	public String getInitiativeStringVR() {		
		System.out.println("Attributes2 = "+attributes2);
		return RES.format("label.ini", 
//				attributes2.get(ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD).getModifiedValue(), 
				attributes2.get(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue()+1);
	}

	//--------------------------------------------------------------------
	public String getInitiativeStringVRHot() {		
		return RES.format("label.ini", 
//				attributes2.get(ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT).getModifiedValue(), 
				attributes2.get(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue()+2);
	}

	//--------------------------------------------------------------------
	/**
	 * @return the monitor
	 */
	public int[] getMonitor() {
		return monitor;
	}

	//--------------------------------------------------------------------
	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(int[] monitor) {
		this.monitor = monitor;
	}

}
