package de.rpgframework.shadowrun6.foundry;

/**
 * @author prelle
 *
 */
public class Resistances {
	
	public class ResistValue {
		public int base = 0;
		public int mod = 0;
		public int pool = 0;
	}
	
	public ResistValue attacks;
	public ResistValue damage;
	public ResistValue astral_direct;
	public ResistValue astral_indirect;
	public ResistValue toxin;

	//-------------------------------------------------------------------
	/**
	 */
	public Resistances() {
		attacks   = new ResistValue();
		astral_direct  = new ResistValue();
		astral_indirect= new ResistValue();
		damage   = new ResistValue();
		toxin    = new ResistValue();
	}

}
