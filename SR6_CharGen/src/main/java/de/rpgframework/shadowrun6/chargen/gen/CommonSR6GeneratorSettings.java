package de.rpgframework.shadowrun6.chargen.gen;

import de.rpgframework.shadowrun.ACommonCharacterSettings;
import de.rpgframework.shadowrun6.PowerLevel;

/**
 * @author prelle
 *
 */
public class CommonSR6GeneratorSettings extends ACommonCharacterSettings {

	public PowerLevel variant;
	protected int mysticAdeptPowerPoints;
	private int toContacts;
	private int negQualities;
	public int initiation;

	/** Define how many points of the MAG rating have been spent for PP (for MysAds) */
	public void setMagicForPP(int val) { mysticAdeptPowerPoints = val; }
	/** How many points of the MAG rating have been spent for PP (for MysAds) */
	public int  getMagicForPP() { return mysticAdeptPowerPoints; }

	public int getBoughtContactPoints() { return toContacts; }
	public void setBoughtContactPoints(int val) { toContacts = val; }

	public int getKarmaForNegativeQualities() { return negQualities; }
	public void setKarmaForNegativeQualities(int val) { negQualities = val; }

	public void setKarmaForInitiation(int val) { initiation = val; }
	public int getKarmaForInitiation() { return initiation; }

}
