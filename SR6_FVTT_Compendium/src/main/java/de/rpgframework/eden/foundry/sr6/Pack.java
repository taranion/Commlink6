package de.rpgframework.eden.foundry.sr6;

/**
 * @author prelle
 *
 */
public class Pack {

	/** The compendium pack name - this should be a unique lower-case string with no special characters. */
	private String name;
	/** The compendium pack label - this should be a human readable string label which is displayed in the Compendium sidebar in-game */
	private String label;
	/** Since you are creating compendium content specifically for your system, be sure to reference that the content inside each compendium pack requires the system by providing the system name that you chose. */
	private String system;
	/** The module attribute of each compendium pack designates which content module provided the pack, since this pack is coming from the system itself we can once again provide the system name. */
	private String module;
	/** The path for each compendium pack should designate a database file with the .db extension. As a best practice, I recommend placing these database files within the packs subdirectory. You do not need to create these files yourself. If a system includes a compendium pack, the database file for that pack will be created automatically when the system is loaded, if it does not already exist. */
	private String path;
	/** Each compendium pack must designate a specific Entity type that it contains. Compendiums can contain any type of Entity, including: Actors, Items, Journal Entries, Macro Commands, Playlists, Rollable Tables or Scenes. */
	private String entity;
	
	//-------------------------------------------------------------------
	/**
	 */
	public Pack() {
		// TODO Auto-generated constructor stub
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	//-------------------------------------------------------------------
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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

	//-------------------------------------------------------------------
	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	//-------------------------------------------------------------------
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the system
	 */
	public String getSystem() {
		return system;
	}

	//-------------------------------------------------------------------
	/**
	 * @param system the system to set
	 */
	public void setSystem(String system) {
		this.system = system;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	//-------------------------------------------------------------------
	/**
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}

}
