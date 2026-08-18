package de.rpgframework.shadowrun6.foundry;

public class FVTTRitual extends GenericFVTT {
	
	public static class RitualFeatures {
		public boolean anchored;
		public boolean material_link;
		public boolean minion;
		public boolean spell;
		public boolean spotter;
	}

	public int threshold;
	public RitualFeatures features = new RitualFeatures();

}
