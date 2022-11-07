package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class ASDFMapping {
	
	@Attribute(name="a")
	private SR6ItemAttribute attack;
	@Attribute(name="s")
	private SR6ItemAttribute sleaze;
	@Attribute(name="d")
	private SR6ItemAttribute dataProcessing;
	@Attribute(name="f")
	private SR6ItemAttribute firewall;

	//-------------------------------------------------------------------
	public ASDFMapping() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @return the attack
	 */
	public SR6ItemAttribute getAttack() {
		return attack;
	}

	//-------------------------------------------------------------------
	/**
	 * @param attack the attack to set
	 */
	public void setAttack(SR6ItemAttribute attack) {
		this.attack = attack;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the sleaze
	 */
	public SR6ItemAttribute getSleaze() {
		return sleaze;
	}

	//-------------------------------------------------------------------
	/**
	 * @param sleaze the sleaze to set
	 */
	public void setSleaze(SR6ItemAttribute sleaze) {
		this.sleaze = sleaze;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the dataProcessing
	 */
	public SR6ItemAttribute getDataProcessing() {
		return dataProcessing;
	}

	//-------------------------------------------------------------------
	/**
	 * @param dataProcessing the dataProcessing to set
	 */
	public void setDataProcessing(SR6ItemAttribute dataProcessing) {
		this.dataProcessing = dataProcessing;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the firewall
	 */
	public SR6ItemAttribute getFirewall() {
		return firewall;
	}

	//-------------------------------------------------------------------
	/**
	 * @param firewall the firewall to set
	 */
	public void setFirewall(SR6ItemAttribute firewall) {
		this.firewall = firewall;
	}

}
