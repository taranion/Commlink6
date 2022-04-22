package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.shadowrun6.SR6RuleFlag;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class RuleFlagConverter implements StringValueConverter<SR6RuleFlag> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SR6RuleFlag value) throws Exception {
		return value.name();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SR6RuleFlag read(String idref) throws Exception {
		try {
			SR6RuleFlag data = SR6RuleFlag.valueOf(idref);
			return data;
		} catch (Exception e) {
			throw new ReferenceException(ShadowrunReference.RULE, idref);
		}
	}

}
