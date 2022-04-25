package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.ReferenceException;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class LifestyleQualityConverter implements StringValueConverter<LifestyleQuality> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(LifestyleQuality value) throws Exception {
		return value.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public LifestyleQuality read(String idref) throws Exception {
		LifestyleQuality skill = Shadowrun6Core.getItem(LifestyleQuality.class, idref);
		if (skill==null)
			throw new ReferenceException(ShadowrunReference.LIFESTYLE, idref);

		return skill;
	}

}
