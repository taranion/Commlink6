package de.rpgframework.shadowrun6.modifications;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.persist.AttributeConverter;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.persist.ItemAttributeConverter;
import de.rpgframework.shadowrun6.persist.ReferenceException;
import de.rpgframework.shadowrun6.persist.RuleConverter;
import de.rpgframework.shadowrun6.persist.SkillSpecializationConverter;
/**
 * @author prelle
 *
 */
public enum ShadowrunReference implements ModifiedObjectType {

	ACTION(Shadowrun6Action.class),
	ACTION_BONUS(Shadowrun6Action.Type.class, 0),
	ALLERGY_ALLERGEN("Allergen"),
	ALLERGY_SEVERITY("Severity"),
	ATTRIBUTE(new AttributeConverter()),
	ELEMENT("Element"),
	GEAR(ItemTemplate.class),
	HOOK(ItemHook.class, 0),
	ITEM_ATTRIBUTE(new ItemAttributeConverter()),
	MAGIC_RESO("MagicOrResonance"),
	MENTOR_SPIRIT("Mentorspirit"),
	METATYPE(SR6MetaType.class),
	POOL("Pool"), // Derived values like defense pool
	RULE(new RuleConverter()),
	QUALITY(Quality.class),
	SENSE("Sense"),
	SKILL_KNOWLEDGE(SR6Skill.class.getAnnotation(DataItemTypeKey.class).id()),
	SKILL(SR6Skill.class.getAnnotation(DataItemTypeKey.class).id()),
	SKILLSPECIALIZATION(new SkillSpecializationConverter()),
	SLOT("ItemHook"),
	SPELL(ASpell.class),
	SPELLFEATURE(SpellFeature.class.getAnnotation(DataItemTypeKey.class).id()),
	SPIRIT("Spirit"),
	SPRITE("Sprite"),
	TEXT("TEXT"),
	;
	
	Class<? extends DataItem> typeClass;
	String typeId;
	Class<? extends Enum<?>> enumType;
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
	ShadowrunReference(Class<? extends Enum<?>> enumType, int x) {
		this.enumType = enumType;
	}
	
	//-------------------------------------------------------------------
	private static StringValueConverter getConverter(Class cls, String enumName) {
		try {
			Field field = cls.getDeclaredField(enumName);
			if (field==null)
				return null;
			AttribConvert attrib = field.getAnnotation(AttribConvert.class);
			if (attrib!=null && attrib.value()!=null) {
				StringValueConverter ret = attrib.value().getDeclaredConstructor().newInstance();
				return ret;
			}
		} catch (NoSuchFieldException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static <T> T resolve(ShadowrunReference type, String key) {
		
		if (type.typeClass!=null) {
			return (T) Shadowrun6Core.getItem(type.typeClass, key);
		} else if (type.enumType!=null) {
			try {
				Method valueOf = type.enumType.getMethod("valueOf", String.class);
				return (T) valueOf.invoke(null, key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (type.converter==null)
				throw new RuntimeException("No type class nor StringConverter set for type "+type);
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
