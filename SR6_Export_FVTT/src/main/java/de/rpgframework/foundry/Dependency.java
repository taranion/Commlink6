package de.rpgframework.foundry;

/**
 * @author prelle
 *
 */
public class Dependency {
	
	private String name;
	private String type;
	private String manifest;

	//-------------------------------------------------------------------
	/**
	 */
	public Dependency() {
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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

}
