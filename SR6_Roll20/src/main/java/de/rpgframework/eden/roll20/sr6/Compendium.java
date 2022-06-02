package de.rpgframework.eden.roll20.sr6;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Compendium {

	public transient ByteArrayOutputStream fos;

	private String name;
	private String title;
	private String description;
	private String author = "RPGFramework";
	private String version;
	private List<String> systems;

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

}
