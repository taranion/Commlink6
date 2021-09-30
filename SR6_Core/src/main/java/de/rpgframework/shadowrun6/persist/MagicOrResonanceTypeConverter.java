package de.rpgframework.shadowrun6.persist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun6.Shadowrun6Core;

public class MagicOrResonanceTypeConverter implements StringValueConverter<MagicOrResonanceType> {
	
	private final static Logger logger = LogManager.getLogger("shadowrun.persist");

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public MagicOrResonanceType read(String v) throws Exception {
		MagicOrResonanceType data = Shadowrun6Core.getItem(MagicOrResonanceType.class ,v);
		if (data==null) {
			logger.error("No such magic or resonance type: "+v);
			throw new IllegalArgumentException("No such magic or resonance type: "+v);
		}
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(MagicOrResonanceType v) throws Exception {
		return v.getId();
	}
	
}