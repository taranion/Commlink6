package de.rpgframework.shadowrun6.foundry;

/**
 * @author prelle
 *
 */
public class Initiatives {
	
	public class IniPerType {
		public int mod;
		public int dice = 1;
	}
	
	public IniPerType physical;
	public IniPerType astral;

	//-------------------------------------------------------------------
	/**
	 */
	public Initiatives() {
		physical = new IniPerType();
		astral   = new IniPerType();
	}

}
