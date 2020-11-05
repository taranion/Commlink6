package org.prelle.shadowrun6.modifications;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

import org.prelle.shadowrun6.*;
import org.prelle.shadowrun6.persist.*;
/**
 * @author prelle
 *
 */
public enum ShadowrunReference implements ModifiedObjectType {

	ATTRIBUTE(new AttributeConverter()),
	SKILL(Skill.class.getAnnotation(DataItemTypeKey.class).id()),
	SPELLFEATURE(SpellFeature.class.getAnnotation(DataItemTypeKey.class).id())
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
	public static Object resolve(ShadowrunReference type, String key) {
		if (type.typeClass!=null) {
			return ShadowrunCore.getItem(type.typeClass, key);
		} else {
			try {
				return type.converter.read(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ReferenceException(type, key);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.modification.ModifiedObjectType#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String key) {
		return ShadowrunReference.resolve(this, key);
	}
}
