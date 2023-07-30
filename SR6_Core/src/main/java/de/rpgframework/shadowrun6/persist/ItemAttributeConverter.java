package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ItemAttributeConverter implements StringValueConverter<SR6ItemAttribute> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SR6ItemAttribute value) throws Exception {
		return value.name();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SR6ItemAttribute read(String idref) throws Exception {
		try {
			return SR6ItemAttribute.valueOf(idref.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ReferenceException(ShadowrunReference.ITEM_ATTRIBUTE, idref);
		}
	}

}
