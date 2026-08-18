package de.rpgframework.shadowrun6.foundry;

public class FVTTWeapon extends FVTTGear{
	
	public static class FireMode {
		public boolean BF ;
		public boolean FA ;
		public boolean SA ;
		public boolean SS ;
		public boolean RB ;
	}

	public String  dmgDef;
	public int     dmg;
	public boolean stun;
	public int[]   attackRating;
	public FireMode  modes;
}
