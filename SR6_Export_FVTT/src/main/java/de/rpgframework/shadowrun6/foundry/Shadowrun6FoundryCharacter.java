package de.rpgframework.shadowrun6.foundry;

import com.google.gson.annotations.SerializedName;

/**
 * @author prelle
 *
 */
public class Shadowrun6FoundryCharacter extends LifeformActor {
	
	public static class SpecialTraits {
		public int initiation;
		public int submersion;
	}
	

	public Monitor overflow;
	public int nuyen;
	public String mortype;
	@SerializedName("special-traits")
	public SpecialTraits specialTraits;

	//-------------------------------------------------------------------
	/**
	 */
	public Shadowrun6FoundryCharacter() {
		overflow = new Monitor();
		specialTraits = new SpecialTraits();
	}

}
