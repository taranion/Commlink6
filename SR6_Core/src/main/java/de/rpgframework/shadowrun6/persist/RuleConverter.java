package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.shadowrun6.SR6Rule;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class RuleConverter implements StringValueConverter<SR6Rule> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SR6Rule value) throws Exception {
		return value.name();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SR6Rule read(String idref) throws Exception {
		try {
			SR6Rule data = SR6Rule.valueOf(idref);
			return data;
		} catch (Exception e) {
			throw new ReferenceException(ShadowrunReference.RULE, idref);
		}
	}

}
