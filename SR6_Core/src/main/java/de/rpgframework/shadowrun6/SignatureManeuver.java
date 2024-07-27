package de.rpgframework.shadowrun6;

import java.util.UUID;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Element;

import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAction;

/**
 * @author prelle
 *
 */
public class SignatureManeuver {

	@Attribute
	private String skill;

	@Attribute
	private String action1;
	@Attribute
	private String action2;

	@Element
	private UUID uuid;

	@Element
	private String name;

	private transient QualityValue quality;

	//---------------------------------------------------------
	public SignatureManeuver() {
		uuid = UUID.randomUUID();
	}

	//---------------------------------------------------------
	/**
	 * @return the skill
	 */
	public SR6Skill getSkill() {
		return Shadowrun6Core.getSkill(skill);
	}

	//---------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(SR6Skill skill) {
		this.skill = skill.getId();
	}

	//---------------------------------------------------------
	/**
	 * @return the action1
	 */
	public Shadowrun6Action getAction1() {
		return Shadowrun6Core.getItem(Shadowrun6Action.class, action1);
	}

	//---------------------------------------------------------
	/**
	 * @param action1 the action1 to set
	 */
	public void setAction1(ShadowrunAction action1) {
		this.action1 = action1.getId();
	}

	//---------------------------------------------------------
	/**
	 * @return the action2
	 */
	public Shadowrun6Action getAction2() {
		return Shadowrun6Core.getItem(Shadowrun6Action.class, action2);
	}

	//---------------------------------------------------------
	/**
	 * @param action2 the action2 to set
	 */
	public void setAction2(ShadowrunAction action2) {
		this.action2 = action2.getId();
	}

	//---------------------------------------------------------
	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	//---------------------------------------------------------
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	//---------------------------------------------------------
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	//---------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	//---------------------------------------------------------
	/**
	 * @return the quality
	 */
	public QualityValue getQuality() {
		return quality;
	}

	//---------------------------------------------------------
	/**
	 * @param quality the quality to set
	 */
	public void setQuality(QualityValue quality) {
		this.quality = quality;
	}

}
