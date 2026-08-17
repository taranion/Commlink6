package de.rpgframework.shadowrun6.foundry;

/**
 * @author prelle
 *
 */
public class ActionSkills {
	
	public class ActionSkillValue {
		public int points;
		public int modifier;
		public String modString;
		public int augment;
		public String specialization;
		public String expertise;
	}

	public ActionSkillValue astral;
	public ActionSkillValue athletics;
	public ActionSkillValue biotech;
	public ActionSkillValue close_combat;
	public ActionSkillValue con;
	public ActionSkillValue conjuring;
	public ActionSkillValue cracking;
	public ActionSkillValue electronics;
	public ActionSkillValue enchanting;
	public ActionSkillValue engineering;
	public ActionSkillValue exotic_weapons;
	public ActionSkillValue firearms;
	public ActionSkillValue influence;
	public ActionSkillValue outdoors;
	public ActionSkillValue perception;
	public ActionSkillValue piloting;
	public ActionSkillValue sorcery;
	public ActionSkillValue stealth;
	public ActionSkillValue tasking;
	
	//-------------------------------------------------------------------
	/**
	 */
	public ActionSkills() {
		astral = new ActionSkillValue();
		athletics = new ActionSkillValue();
		biotech = new ActionSkillValue();
		close_combat = new ActionSkillValue();
		con = new ActionSkillValue();
		conjuring = new ActionSkillValue();
		cracking = new ActionSkillValue();
		electronics = new ActionSkillValue();
		enchanting = new ActionSkillValue();
		engineering = new ActionSkillValue();
		exotic_weapons = new ActionSkillValue();
		firearms   = new ActionSkillValue();
		influence  = new ActionSkillValue();
		outdoors   = new ActionSkillValue();
		perception = new ActionSkillValue();
		piloting   = new ActionSkillValue();
		sorcery    = new ActionSkillValue();
		stealth    = new ActionSkillValue();
		tasking    = new ActionSkillValue();
	}

}
