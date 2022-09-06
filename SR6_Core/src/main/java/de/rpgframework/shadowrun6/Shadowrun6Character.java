package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.classification.Gender;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;

/**
 * @author prelle
 *
 */
@Root(name="sr6char")
public class Shadowrun6Character extends ShadowrunCharacter<SR6Skill, SR6SkillValue, ItemTemplate, SR6Spell> implements RuleSpecificCharacterObject<ShadowrunAttribute, SR6Skill, SR6SkillValue, ItemTemplate> {

	@Element
	private PowerLevel powerLevel;
	@Element
	private int heat;
	@ElementList(entry="lifestyle", type = SR6Lifestyle.class, inline = false)
	private List<SR6Lifestyle> lifestyles;
	
	private transient Persona persona;
	
	//-------------------------------------------------------------------
	public Shadowrun6Character() {
		gender = Gender.MALE;
		lifestyles = new ArrayList<>();
		
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValuesPlusEdge()) {
			attributes.add(new AttributeValue<ShadowrunAttribute>(key, 1));
		}
		setAttribute(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.MAGIC, 0));
		setAttribute(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.RESONANCE, 0));
		setAttribute(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.ESSENCE_HOLE, 0));
	}

	//-------------------------------------------------------------------
	public SR6SkillValue addSkillValue(SR6SkillValue value) {
		// You can have multiple times knowledge or language
		// but other skills are unique
		SkillType type = value.getModifyable().getType();
		if (skills.contains(value) && !(type==SkillType.KNOWLEDGE || type==SkillType.LANGUAGE)) {
			throw new RuntimeException("Hab ich schon");
			//return value;
		}
		
		return super.addSkillValue(value);
//		skills.add(value);
//		return value;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RuleSpecificCharacterObject#getRules()
	 */
	@Override
	public RoleplayingSystem getRules() {
		return RoleplayingSystem.SHADOWRUN6;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ShadowrunCharacter#getMetatype()
	 */
	@SuppressWarnings("unchecked")
	public SR6MetaType getMetatype() {
		return Shadowrun6Core.getItem(SR6MetaType.class, metatype);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.RuleSpecificCharacterObject#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		SR6MetaType meta = getMetatype();
		StringBuffer buf = new StringBuffer();
		if (gender!=null) {
			buf.append(gender+" ");
		}
		if (meta!=null) {
			buf.append(meta.getName(Locale.getDefault())+" ");
		}
		if (getMagicOrResonanceType()!=null) {
			buf.append(getMagicOrResonanceType().getName(Locale.getDefault())+"");
		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the heat
	 */
	public int getHeat() {
		return heat;
	}

	//-------------------------------------------------------------------
	/**
	 * @param heat the heat to set
	 */
	public void setHeat(int heat) {
		this.heat = Math.max(0,heat);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ShadowrunCharacter#getMagicOrResonanceType()
	 */
	@Override
	public MagicOrResonanceType getMagicOrResonanceType() {
		return Shadowrun6Core.getItem(MagicOrResonanceType.class, magicOrResonance);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.ShadowrunCharacter#getTradition()
	 */
	@Override
	public Tradition getTradition() {
		return Shadowrun6Core.getItem(Tradition.class, tradition);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the powerLevel
	 */
	public PowerLevel getPowerLevel() {
		return powerLevel;
	}

	//-------------------------------------------------------------------
	/**
	 * @param powerLevel the powerLevel to set
	 */
	public void setPowerLevel(PowerLevel powerLevel) {
		this.powerLevel = powerLevel;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the qualities
	 */
	public List<SR6Lifestyle> getLifestyles() {
		return lifestyles;
	}

	//-------------------------------------------------------------------
	public void addLifestyle(SR6Lifestyle value) {
		if (!lifestyles.contains(value))
			lifestyles.add(value);
	}

	//-------------------------------------------------------------------
	public void removeLifestyle(SR6Lifestyle value) {
		lifestyles.remove(value);
	}

	//-------------------------------------------------------------------
	public List<CarriedItem<ItemTemplate>> getCarriedItems(ItemType... types) {
		CarriedItemItemTypeFilter filter = new CarriedItemItemTypeFilter(CarryMode.CARRIED, types);
		return getCarriedItems().stream()
			.filter(filter)
			.collect(Collectors.toList())
			;
		
	}

	//-------------------------------------------------------------------
	/**
	 * @return the persona
	 */
	public Persona getPersona() {
		return persona;
	}

	//-------------------------------------------------------------------
	/**
	 * @param persona the persona to set
	 */
	public void setPersona(Persona persona) {
		this.persona = persona;
	}

}
