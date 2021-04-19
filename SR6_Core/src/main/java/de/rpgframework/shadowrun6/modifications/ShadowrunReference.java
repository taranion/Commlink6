package de.rpgframework.shadowrun6.modifications;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.persist.AttributeConverter;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.persist.ReferenceException;
import de.rpgframework.shadowrun6.persist.RuleConverter;
import de.rpgframework.shadowrun6.persist.SkillSpecializationConverter;
/**
 * @author prelle
 *
 */
public enum ShadowrunReference implements ModifiedObjectType {

	ALLERGY_ALLERGEN("Allergen"),
	ALLERGY_SEVERITY("Severity"),
	ATTRIBUTE(new AttributeConverter()),
	ELEMENT("Element"),
	GEAR(ItemTemplate.class),
	MAGIC_RESO("MagicOrResonance"),
	METATYPE(SR6MetaType.class),
	RULE(new RuleConverter()),
	QUALITY(Quality.class),
	SKILL_KNOWLEDGE(SR6Skill.class.getAnnotation(DataItemTypeKey.class).id()),
	SKILL(SR6Skill.class.getAnnotation(DataItemTypeKey.class).id()),
	SKILLSPECIALIZATION(new SkillSpecializationConverter()),
	SLOT("ItemHook"),
	SPELLFEATURE(SpellFeature.class.getAnnotation(DataItemTypeKey.class).id()),
	SPIRIT("Spirit"),
	SPRITE("Sprite"),
	TEXT("TEXT"),
	;
	
	Class<? extends DataItem> typeClass;
	String typeId;
	StringValueConverter<? extends Object> converter;
	
	//-------------------------------------------------------------------
	ShadowrunReference(StringValueConverter<? extends Object> conv) {
		converter = conv;
	}
	
	//-------------------------------------------------------------------
	ShadowrunReference(String type) {
		this.typeId = type;
	}
	
	//-------------------------------------------------------------------
	ShadowrunReference(Class<? extends DataItem> cls) {
		this.typeClass = cls;
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static <T> T resolve(ShadowrunReference type, String key) {
		if (type.typeClass!=null) {
			return (T) Shadowrun6Core.getItem(type.typeClass, key);
		} else {
			try {
				return (T) type.converter.read(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		throw new ReferenceException(type, key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.ModifiedObjectType#resolve(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T resolve(String key) {
		return (T)ShadowrunReference.resolve(this, key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.ModifiedObjectType#resolve(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataItem> T resolveAsDataItem(String key) {
		return (T)ShadowrunReference.resolve(this, key);
	}
}
