package de.rpgframework.shadowrun6;


import org.prelle.simplepersist.StringValueConverter;

/**
 * @author prelle
 *
 */
public class ReturnIdAsResultConverter implements StringValueConverter<String> {

	@Override
	public String write(String value) throws Exception {
		return value;
	}

	@Override
	public String read(String v) throws Exception {
		return v;
	}

}
