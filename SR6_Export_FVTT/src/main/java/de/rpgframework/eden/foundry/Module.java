package de.rpgframework.eden.foundry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author prelle
 *
 */
public class Module {
	public static class Compatibility {
		public String minimum;
		public String verified;
	}
	public static class PackFolder {
		public String name;
		public String sorting;
		public List<String> packs;
		public String color;
		public List<PackFolder> folders;
	}

	public static class Relationship {
		public String id;
		public String type;
		public String manifest;
		public Compatibility compatibility;
	}
	public static class Relationships {
		public List<Relationship> systems;
		public List<Relationship> requires;
	}

	public static class Author {
		public String name;
		public String discord;
		public String email;
		public String url;
	}

	public transient ByteArrayOutputStream fos;

	// Basic required attributes
	private String id;
	private String title;
	private String description;
	private String version;

	// Content description
	private Compatibility compatibility;
	private List<String> esmodules;
	private List<String> styles;
	private List<Pack> packs;
	private List<PackFolder> packFolders;
	private Relationships relationships;
	private List<Language> languages;

	// Other metadata
	private List<Author> authors;
	private String socket;
	private String url;
	private String manifest;
	private String download;
	private String license;
	private String readme;
	private String bugs;
	private String changelog;

	// Old attributes, probably not used by FVTT
	private String initiative;
	private String gridDistance;
	private String gridUnits;
	private String primaryTokenAttribute;
	private String secondaryTokenAttribute;

	//-------------------------------------------------------------------
	public Module() {
		compatibility = new Compatibility();
		languages = new ArrayList<>();
		packs = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	private Language getOrCreateLanguage(String key) {
		for (Language lang : languages) {
			if (lang.getLang().equalsIgnoreCase(key))
				return lang;
		}
		Language lang = new Language();
		lang.setLang(key);
		lang.setName(id+"-translation-"+key);
		lang.setPath("lang/"+id+"_"+key+".json");
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	//-------------------------------------------------------------------
	/**
	 * Legacy helpers to keep backward compatibility
	 */
	public String getName() {
		return id;
	}
	public void setName(String name) {
		this.id = name;
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
	 * @return the authors list
	 */
	public List<Author> getAuthors() {
		return authors;
	}

	//-------------------------------------------------------------------
	/**
	 * @param authors the authors list to set
	 */
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	//-------------------------------------------------------------------
	/**
	 * @param author the author to add to authors list
	 */
	public void addAuthor(Author author) {
		if(authors == null) {
			authors = new ArrayList<>();
		}
		this.authors.add(author);
	}

	//-------------------------------------------------------------------
	/**
	 * Legacy helpers to keep backward compatibility
	 */
	public String getAuthor() {
		if(authors == null)
			return "";
		for (Author author : authors) {
            return author.name;
		}
		return "";
	}
	public void setAuthor(String author) {
		if(authors == null) {
			authors = new ArrayList<>();
		}
		Author tmp = new Author();
		tmp.name = author;
		authors.add(tmp);
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
		return compatibility.minimum;
	}

	//-------------------------------------------------------------------
	/**
	 * @param minimumCoreVersion the minimumCoreVersion to set
	 */
	public void setMinimumCoreVersion(String minimumCoreVersion) {
		this.compatibility.minimum = minimumCoreVersion;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the compatibleCoreVersion
	 */
	public String getCompatibleCoreVersion() {
		return compatibility.verified;
	}

	//-------------------------------------------------------------------
	/**
	 * @param compatibleCoreVersion the compatibleCoreVersion to set
	 */
	public void setCompatibleCoreVersion(String compatibleCoreVersion) {
		this.compatibility.verified = compatibleCoreVersion;
	}

	//-------------------------------------------------------------------
	/**
	 * @param rel relations to add
	 */
	public void addRelationshipSystem(Relationship rel) {
		if(relationships == null) {
			relationships = new Relationships();
		}
		if(relationships.systems == null) {
			relationships.systems = new ArrayList<>();
		}
		this.relationships.systems.add(rel);
	}

	//-------------------------------------------------------------------
	/**
	 * @param rel relations to add
	 */
	public void addRelationshipRequire(Relationship rel) {
		if(relationships == null) {
			relationships = new Relationships();
		}
		if(relationships.requires == null) {
			relationships.requires = new ArrayList<>();
		}
		this.relationships.requires.add(rel);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the relationships
	 */
	public Relationships getRelationships() {
		return relationships;
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
