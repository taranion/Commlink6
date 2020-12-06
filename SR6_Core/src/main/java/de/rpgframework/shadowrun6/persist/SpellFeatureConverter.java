/**
 * 
 */
package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.SpellFeature;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

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
		SpellFeature skill = Shadowrun6Core.getSpellFeature(idref);
		if (skill==null)
			throw new ReferenceException(ShadowrunReference.SPELLFEATURE, idref);

		return skill;
	}

}
