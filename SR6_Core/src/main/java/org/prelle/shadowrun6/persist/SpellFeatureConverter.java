/**
 * 
 */
package org.prelle.shadowrun6.persist;

import org.prelle.shadowrun6.ShadowrunCore;
import org.prelle.shadowrun6.SpellFeature;
import org.prelle.shadowrun6.modifications.ShadowrunReference;
import org.prelle.simplepersist.StringValueConverter;

/**
 * @author prelle
 *
 */
public class SpellFeatureConverter implements StringValueConverter<SpellFeature> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SpellFeature value) throws Exception {
		return value.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SpellFeature read(String idref) throws Exception {
		SpellFeature skill = ShadowrunCore.getSpellFeature(idref);
		if (skill==null)
			throw new ReferenceException(ShadowrunReference.SPELLFEATURE, idref);

		return skill;
	}

}
