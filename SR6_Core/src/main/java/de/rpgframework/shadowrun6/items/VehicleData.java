/**
 * 
 */
package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.shadowrun6.persist.OnRoadOffRoadConverter;

/**
 * @author prelle
 *
 */
@Root(name="vehicle")
public class VehicleData implements IGearTypeData {
	
	public enum VehicleType {
		GROUND("ground_craft"),
		WATER("watercraft"),
		AIR("aircraft")
		;
		String specializationId;
		private VehicleType(String specialID) {
			specializationId = specialID;
		}
		public String getSpecializationID() { return specializationId; }
	}

	@Attribute(name="han")
	@AttribConvert(OnRoadOffRoadConverter.class)
	private OnRoadOffRoadValue handling;
	@Attribute(name="acc")
	@AttribConvert(OnRoadOffRoadConverter.class)
	private OnRoadOffRoadValue acceleration;
	@Attribute(name="spdi")
	@AttribConvert(OnRoadOffRoadConverter.class)
	private OnRoadOffRoadValue speedInterval;
	@Attribute(name="tspd")
	private Integer topSpeed;
	@Attribute(name="bod")
	private Integer body;
	@Attribute(name="arm")
	private Integer armor;
	@Attribute(name="pil")
	private Integer pilot;
	@Attribute(name="sen")
	private Integer sensor;
	@Attribute(name="sea")
	private Integer seats;
	@Attribute(name="type")
	private VehicleType type;
	@Attribute(name="cf")
	private Integer cargoFactor;
	
	private transient int size;
	private transient float hardpoints;
	
	//-------------------------------------------------------------------
	public VehicleData() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public void clear() {
		handling = new OnRoadOffRoadValue();
		acceleration = new OnRoadOffRoadValue();
		speedInterval = new OnRoadOffRoadValue();
		topSpeed = 0;
		body = 0;
		armor = 0;
		pilot = 0;
		sensor= 0;
		seats = 0;
		type  = null;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @return the handling
	 */
	public OnRoadOffRoadValue getHandling() {
		return handling;
	}

	//-------------------------------------------------------------------
	/**
	 * @param handling the handling to set
	 */
	public void setHandling(OnRoadOffRoadValue handling) {
		this.handling = handling;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the acceleration
	 */
	public OnRoadOffRoadValue getAcceleration() {
		return acceleration;
	}

	//-------------------------------------------------------------------
	/**
	 * @param acceleration the acceleration to set
	 */
	public void setAcceleration(OnRoadOffRoadValue acceleration) {
		this.acceleration = acceleration;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the body
	 */
	public int getBody() {
		return body;
	}

	//-------------------------------------------------------------------
	/**
	 * @param body the body to set
	 */
	public void setBody(int body) {
		this.body = body;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the armor
	 */
	public int getArmor() {
		return armor;
	}

	//-------------------------------------------------------------------
	/**
	 * @param armor the armor to set
	 */
	public void setArmor(int armor) {
		this.armor = armor;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the pilot
	 */
	public int getPilot() {
		return pilot;
	}

	//-------------------------------------------------------------------
	/**
	 * @param pilot the pilot to set
	 */
	public void setPilot(int pilot) {
		this.pilot = pilot;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the sensor
	 */
	public int getSensor() {
		return sensor;
	}

	//-------------------------------------------------------------------
	/**
	 * @param sensor the sensor to set
	 */
	public void setSensor(int sensor) {
		this.sensor = sensor;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the seats
	 */
	public int getSeats() {
		return seats;
	}

	//-------------------------------------------------------------------
	/**
	 * @param seats the seats to set
	 */
	public void setSeats(int seats) {
		this.seats = seats;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the speedInterval
	 */
	public OnRoadOffRoadValue getSpeedInterval() {
		return speedInterval;
	}

	//-------------------------------------------------------------------
	/**
	 * @param speedInterval the speedInterval to set
	 */
	public void setSpeedInterval(OnRoadOffRoadValue speedInterval) {
		this.speedInterval = speedInterval;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the topSpeed
	 */
	public int getTopSpeed() {
		return topSpeed;
	}

	//-------------------------------------------------------------------
	/**
	 * @param topSpeed the topSpeed to set
	 */
	public void setTopSpeed(int topSpeed) {
		this.topSpeed = topSpeed;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public VehicleType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(VehicleType type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	//-------------------------------------------------------------------
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the cargoFactor
	 */
	public int getCargoFactor() {
		return cargoFactor;
	}

	//-------------------------------------------------------------------
	/**
	 * @param cargoFactor the cargoFactor to set
	 */
	public void setCargoFactor(int cargoFactor) {
		this.cargoFactor = cargoFactor;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the hardpoints
	 */
	public float getHardpoints() {
		return hardpoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @param hardpoints the hardpoints to set
	 */
	public void setHardpoints(float hardpoints) {
		this.hardpoints = hardpoints;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.IGearTypeData#copyToAttributes(de.rpgframework.genericrpg.items.AGearData)
	 */
	@Override
	public void copyToAttributes(AGearData copyTo) {
//		if (handling!=null)
//			copyTo.setAttribute(SR6ItemAttribute.HANDLING, handling);
//		if (sensor!=null)
//			copyTo.setAttribute(SR6ItemAttribute.SENSOR, sensor);
//		if (damage!=null)
//			copyTo.setAttribute(SR6ItemAttribute.DAMAGE, damage);
//		if (mode!=null)
//			copyTo.setAttribute(SR6ItemAttribute.FIREMODES, mode);
//		if (skill!=null)
//			copyTo.setAttribute(SR6ItemAttribute.SKILL, skill);
//		if (spec!=null)
//			copyTo.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, spec);
		System.err.println("ToDo: VehicleData.copyToAttributes");
	}

}
