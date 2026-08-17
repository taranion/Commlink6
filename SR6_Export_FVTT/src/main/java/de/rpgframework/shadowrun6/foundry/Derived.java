package de.rpgframework.shadowrun6.foundry;

/**
 * @author prelle
 *
 */
public class Derived {
	
	public class DerivedValue {
		public int base = 0;
		public int mod = 0;
		public int pool = 0;
	}
	
	public DerivedValue attack_rating;
	public DerivedValue defense_rating;
	public DerivedValue composure;
	public DerivedValue judge_intentions;
	public DerivedValue memory;
	public DerivedValue lift_carry;

	//-------------------------------------------------------------------
	/**
	 */
	public Derived() {
		attack_rating   = new DerivedValue();
		defense_rating  = new DerivedValue();
		composure       = new DerivedValue();
		judge_intentions= new DerivedValue();
		memory          = new DerivedValue();
		lift_carry      = new DerivedValue();
	}

}
