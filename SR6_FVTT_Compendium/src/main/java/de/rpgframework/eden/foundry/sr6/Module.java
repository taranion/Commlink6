package de.rpgframework.eden.foundry.sr6;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author prelle
 *
 */
public class Module {
	
	public transient ByteArrayOutputStream fos;
	
	private String name;
	private String title;
	private String description;
	private String author = "RPGFramework";
	private String version;
	private List<String> systems;
	private String minimumCoreVersion;
	private String compatibleCoreVersion;
	private List<Pack> packs;
	private List<Dependency> dependencies;
	private List<Language> languages;
	private String socket;
	private String initiative;
	private String gridDistance;
	private String gridUnits;
	private String primaryTokenAttribute;
	private String secondaryTokenAttribute;
	private String url;
	private String manifest;
	private String download;

	//-------------------------------------------------------------------
	public Module() {
		packs = new ArrayList<>();
		languages = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	private Language getOrCreateLanguage(String key) {
		for (Language lang : languages) {
			if (lang.getLang().equalsIgnoreCase(key))
				return lang;
		}
		Language lang = new Language();
		lang.setLang(key);
		lang.setName(name+"-translation-"+key);
		lang.setPath("lang/"+name+"_"+key+".json");
		languages.add(lang);
		
		return lang;
	}

	//-------------------------------------------------------------------
	public void addTranslation(String lang, String key, String value) {
		Language tmp = getOrCreateLanguage(lang);
		tmp.addTranslation(key, value);
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	//-------------------------------------------------------------------
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	//-------------------------------------------------------------------
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	//-------------------------------------------------------------------
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	//-------------------------------------------------------------------
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the minimumCoreVersion
	 */
	public String getMinimumCoreVersion() {
		return minimumCoreVersion;
	}

	//-------------------------------------------------------------------
	/**
	 * @param minimumCoreVersion the minimumCoreVersion to set
	 */
	public void setMinimumCoreVersion(String minimumCoreVersion) {
		this.minimumCoreVersion = minimumCoreVersion;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the compatibleCoreVersion
	 */
	public String getCompatibleCoreVersion() {
		return compatibleCoreVersion;
	}

	//-------------------------------------------------------------------
	/**
	 * @param compatibleCoreVersion the compatibleCoreVersion to set
	 */
	public void setCompatibleCoreVersion(String compatibleCoreVersion) {
		this.compatibleCoreVersion = compatibleCoreVersion;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the packs
	 */
	public List<Pack> getPacks() {
		return packs;
	}

	//-------------------------------------------------------------------
	/**
	 * @param packs the packs to set
	 */
	public void setPacks(List<Pack> packs) {
		this.packs = packs;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	//-------------------------------------------------------------------
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the manifest
	 */
	public String getManifest() {
		return manifest;
	}

	//-------------------------------------------------------------------
	/**
	 * @param manifest the manifest to set
	 */
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the download
	 */
	public String getDownload() {
		return download;
	}

	//-------------------------------------------------------------------
	/**
	 * @param download the download to set
	 */
	public void setDownload(String download) {
		this.download = download;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the languages
	 */
	public List<Language> getLanguages() {
		return languages;
	}

	//-------------------------------------------------------------------
	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

}
