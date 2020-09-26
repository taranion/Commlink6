package org.prelle.shadowrun6.persist;

import org.prelle.shadowrun6.Attribute;
import org.prelle.simplepersist.StringValueConverter;

/**
 * @author prelle
 *
 */
public class AttributeConverter implements StringValueConverter<Attribute> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(Attribute value) throws Exception {
		return value.name();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public Attribute read(String idref) throws Exception {
		return Attribute.valueOf(idref.toUpperCase());
	}

}
