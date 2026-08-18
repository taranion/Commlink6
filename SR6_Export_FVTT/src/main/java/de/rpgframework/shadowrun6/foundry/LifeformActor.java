package de.rpgframework.shadowrun6.foundry;

/**
 * @author prelle
 *
 */
public class LifeformActor extends GeneralActor {
	
	public static enum Type {
		PC,
		NPC,
		CRITTER,
		SPIRIT,
		SPRITE
	}
	
	public static class Edge {
		public int value;
		public int max;
	}
	
	public Type type;
	public PrimaryAttributes attributes;
	public Initiatives initiative;
	public Derived derived;
	public DefenseRating defenserating;
	public Resistances resist;
	public ActionSkills skills;
	public String metatype;
	public String gender;
	public Movement movement;

	//-------------------------------------------------------------------
	/**
	 */
	public LifeformActor() {
		attributes = new PrimaryAttributes();
		skills     = new ActionSkills();
		initiative = new Initiatives();
		derived    = new Derived();
		resist     = new Resistances();
		movement   = new Movement();
	}

}
