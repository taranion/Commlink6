package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.StringValueConverter;

/**
 * @author prelle
 *
 */
public class MentorSpiritsEffectConverter implements StringValueConverter<String> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(java.lang.Object)
	 */
	@Override
	public String write(String value) throws Exception {
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(java.lang.String)
	 */
	@Override
	public String read(String v) throws Exception {
		return Shadowrun6Core.getI18nResources().getString("mentorspirit.choice."+v);
	}

}
