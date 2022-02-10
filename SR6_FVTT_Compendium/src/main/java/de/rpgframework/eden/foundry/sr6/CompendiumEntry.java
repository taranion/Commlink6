package de.rpgframework.eden.foundry.sr6;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.shadowrun6.foundry.Item;

/**
 * @author prelle
 *
 */
public class CompendiumEntry {
	
	public static class CoreFlags {
		public String sheetClass;
	}
	
	public static class Flags {
		public CoreFlags core;
	}
	
	
	public String _id;
	public String name;
	public String type;
	public String img;
	public Object data;
	public TokenEntry token;
	public List<Item> items;
	public Flags flags;

	//-------------------------------------------------------------------
	/**
	 */
	public CompendiumEntry() {
		items = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public void addItems(Item value) {
		items.add(value);
	}

}
