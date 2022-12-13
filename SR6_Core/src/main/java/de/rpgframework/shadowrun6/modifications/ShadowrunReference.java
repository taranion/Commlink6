package de.rpgframework.shadowrun6.modifications;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.CommonCharacter;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Resistance;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun.persist.AttributeConverter;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.items.AmmunitionType;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.persist.ItemAttributeConverter;
import de.rpgframework.shadowrun6.persist.LifestyleQualityConverter;
import de.rpgframework.shadowrun6.persist.RuleFlagConverter;
import de.rpgframework.shadowrun6.persist.SkillSpecializationConverter;
/**
 * @author prelle
 *
 */
public enum ShadowrunReference implements ModifiedObjectType {

	ACTION(Shadowrun6Action.class),
	ACTION_BONUS(Shadowrun6Action.Type.class, 0),
	ADEPT_POWER(AdeptPower.class),
	ALLERGY_ALLERGEN("Allergen"),
	ALLERGY_SEVERITY("Severity"),
	AMMUNITION_TYPE(AmmunitionType.class),
	ATTRIBUTE(new AttributeConverter()),
	AUGMENTATION_QUALITY(AugmentationQuality.class,0),
	COMPLEX_FORM(ComplexForm.class),
	CREATION_POINTS(CreatePoints.class,0),
	CRITTER_POWER(CritterPower.class),
	DATA_STRUCTURE(DataStructure.class),
	ELEMENT("Element"),
	GEAR(ItemTemplate.class),
	GEARMOD(SR6ItemEnhancement.class),
	HOOK(ItemHook.class, 0),
	ITEM_ATTRIBUTE(new ItemAttributeConverter()),
	ITEMFLAG(SR6ItemFlag.class,0),
	ITEMTYPE(ItemType.class,0),
	ITEMSUBTYPE(ItemSubType.class,0),
	LIFESTYLE(new LifestyleQualityConverter()),
	MAGIC_RESO(MagicOrResonanceType.class),
	MATRIX_ATTRIBUTE(new ItemAttributeConverter()),
	MENTOR_SPIRIT(MentorSpirit.class),
	METAECHO(MetamagicOrEcho.class),
	METATYPE(SR6MetaType.class),
	PARAGON(MentorSpirit.class),
	POOL("Pool"), // Derived values like defense pool
	PROGRAM(ItemTemplate.class), // 
	// All Resistance tests
	RESISTANCE(Resistance.class,0),
	RULE(new RuleFlagConverter()),
	QUALITY(Quality.class),
	SENSE("Sense"),
	SIN(FakeRating.class,0),
	SKILL_KNOWLEDGE(SR6Skill.class.getAnnotation(DataItemTypeKey.class).id()),
	SKILL(key -> Shadowrun6Core.getSkill(key)),
	SKILLSPECIALIZATION(new SkillSpecializationConverter()),
	SLOT(ItemHook.class,0),
	SPELL(SR6Spell.class),
	SPELLFEATURE(SpellFeature.class.getAnnotation(DataItemTypeKey.class).id()),
	SPELL_CATEGORY(ASpell.Category.class.getAnnotation(DataItemTypeKey.class).id()),
	SPIRIT(SR6NPC.class),
	SPRITE(SR6NPC.class),
	SPRITE_POWER("SpritePower"),
	SUBSELECT("Subselect"), // Subselect
	TEXT("TEXT"),
	;
	
	Class<? extends DataItem> typeClass;
	String typeId;
	Class<? extends Enum<?>> enumType;
	StringValueConverter<? extends Object> converter;
	Function<String, ? extends DataItem> resolver;
	
	//-------------------------------------------------------------------
	ShadowrunReference(StringValueConverter<? extends Object> conv) {
		converter = conv;
	}
	
	//-------------------------------------------------------------------
	ShadowrunReference(Function<String, ? extends DataItem> resolv) {
		resolver = resolv;
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
	@SuppressWarnings("rawtypes")
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
		if (key==null) throw new NullPointerException(type+" not set");
		if (type.typeClass!=null) {
			return (T) Shadowrun6Core.getItem(type.typeClass, key);
		} else if (type.enumType!=null) {
			try {
				Method valueOf = type.enumType.getMethod("valueOf", String.class);
				return (T) valueOf.invoke(null, key);
			} catch (InvocationTargetException ivte) {
				Throwable ee = ivte.getTargetException();
				if (ee instanceof IllegalArgumentException) {
					throw new ReferenceException(type, key);
				}
				System.err.println(ShadowrunReference.class.getSimpleName()+".resolve()-1:");
				ivte.printStackTrace();
			} catch (Exception e) {
				System.err.println(ShadowrunReference.class.getSimpleName()+".resolve()-2:");
				e.printStackTrace();
			}
		} else if (type.resolver!=null) {
			return (T)type.resolver.apply(key);
		} else {
			if (type==ShadowrunReference.TEXT)
				return (T)key;
			if (type.converter==null)
				throw new RuntimeException("Neither class, nor enumType nor converter  class nor StringConverter set for type "+type);
			try {
				return (T) type.converter.read(key);
			} catch (ReferenceException e) {
				throw new ReferenceException(type, key);
			} catch (Exception e) {
				System.err.println(ShadowrunReference.class.getSimpleName()+".resolve()-3:");
				e.printStackTrace();
			}
		}
		throw new ReferenceException(type, key);
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static <T> T[] resolveAll(ShadowrunReference type) {
		switch (type) {
		case ATTRIBUTE:
			return (T[]) ShadowrunAttribute.primaryValues();
		default:
			throw new IllegalArgumentException("ALL not supported for "+type);
		}
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static <T> T[] resolveVariable(ShadowrunReference type, String varName) {
		switch (type) {
		case ATTRIBUTE:
			switch (varName) {
			case "PHYSICAL": return (T[]) new ShadowrunAttribute[] {ShadowrunAttribute.BODY, ShadowrunAttribute.AGILITY, ShadowrunAttribute.REACTION, ShadowrunAttribute.STRENGTH};
			case "MENTAL": return (T[]) new ShadowrunAttribute[] {ShadowrunAttribute.WILLPOWER, ShadowrunAttribute.LOGIC, ShadowrunAttribute.INTUITION, ShadowrunAttribute.CHARISMA};
			}
			throw new IllegalArgumentException("Unknown variable "+varName+" for "+type);
		default:
			throw new IllegalArgumentException("Variables of type "+type+" not supported");
		}
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
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.ModifiedObjectType#resolveAny()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] resolveAny() {
		return (T[])ShadowrunReference.resolveAll(this);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.ModifiedObjectType#resolveAny()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] resolveVariable(String varName) {
		return (T[])ShadowrunReference.resolveVariable(this, varName);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.ModifiedObjectType#instantiateModification(de.rpgframework.genericrpg.modification.Modification, de.rpgframework.genericrpg.data.ComplexDataItemValue, de.rpgframework.genericrpg.data.CommonCharacter)
	 */
	@Override
	public Modification instantiateModification(Modification tmp, ComplexDataItemValue<?> value, CommonCharacter<?, ?, ?,?> model) {
		return Shadowrun6Tools.instantiateModification(tmp, value, (Shadowrun6Character) model );
	}
	
}
