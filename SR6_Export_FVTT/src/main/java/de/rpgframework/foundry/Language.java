package de.rpgframework.foundry;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author prelle
 *
 */
public class Language {
	
	private String lang;
	private String name;
	private String path;
	public transient SortedMap<String,String> keys;

	//-------------------------------------------------------------------
	public Language() {
		keys = new TreeMap<String,String>();
	}

	//-------------------------------------------------------------------
	public void addTranslation(String key, String value) {
		keys.put(key, value);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	//-------------------------------------------------------------------
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	//-------------------------------------------------------------------
	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	//-------------------------------------------------------------------
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
