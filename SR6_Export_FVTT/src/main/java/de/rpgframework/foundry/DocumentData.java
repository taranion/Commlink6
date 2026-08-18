package de.rpgframework.foundry;

import java.util.List;
import java.util.Map;

/**
 * @author prelle
 *
 */
public class DocumentData {

	public String _id;
	public String name;
	/** The _id of a Folder which contains this Item */
	public String folder;
	/** The numeric sort value which orders this Item relative to its siblings */
	public int sort = 0;
	private Object permission;
	private Map<String,?> flags;

	//-------------------------------------------------------------------
	/**
	 */
	public DocumentData() {
		// TODO Auto-generated constructor stub
	}

}
