package de.rpgframework.shadowrun6.foundry;

import com.google.gson.annotations.SerializedName;

/**
 * @author prelle
 *
 */
public class PrimaryAttributes {
	
	public class AttributeValue {
		public int base;
		public int mod;
		public int pool;
		public String modString;
	}

	public AttributeValue bod;
	public AttributeValue agi;
	public AttributeValue rea;
	public AttributeValue str;
	public AttributeValue wil;
	public AttributeValue log;
	@SerializedName(value="int")
	public AttributeValue inn;
	public AttributeValue cha;
//	public AttributeValue edg;
	public AttributeValue mag;
	public AttributeValue res;
	
	//-------------------------------------------------------------------
	/**
	 */
	public PrimaryAttributes() {
		bod = new AttributeValue();
		agi = new AttributeValue();
		rea = new AttributeValue();
		str = new AttributeValue();
		wil = new AttributeValue();
		log = new AttributeValue();
		inn = new AttributeValue();
		cha = new AttributeValue();
		
//		edg = new AttributeValue();
		mag = new AttributeValue();
		res = new AttributeValue();
	}

}
