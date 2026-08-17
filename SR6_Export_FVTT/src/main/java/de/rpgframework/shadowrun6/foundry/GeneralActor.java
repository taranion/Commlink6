package de.rpgframework.shadowrun6.foundry;

import de.rpgframework.shadowrun6.foundry.LifeformActor.Edge;

/**
 * @author prelle
 *
 */
public abstract class GeneralActor {

	public String genesisID;
	public String description;
	public Monitor physical;
	public Monitor stun;
	public Edge edge;

	//-------------------------------------------------------------------
	protected GeneralActor() {
		physical   = new Monitor();
		stun       = new Monitor();
		edge       = new Edge();
	}

}
