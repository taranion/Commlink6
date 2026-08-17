package de.rpgframework.foundry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prelle
 *
 */
public class ActorData<T> extends DocumentData {
	
	public String type;
	public String img;
	public T system;
	public String generatorName, generatorVersion;
	public String exportVersion = "13.0";
	public TokenData token;
	private List<ItemData<?>> items;
	private List<Object> effects;
	
		
	//-------------------------------------------------------------------
	public ActorData(String name, String type, T data) {
		this.name = name;
		this.type = type;
		this.system = data;
		items = new ArrayList<>();
		token = new TokenData();
		token.name = name;
		effects = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public void setItems(List<ItemData<?>> value) {
		items = value;
	}

	//-------------------------------------------------------------------
	public void addItem(ItemData<?> value) {
		if (value!=null)
			items.add(value);
	}

}
