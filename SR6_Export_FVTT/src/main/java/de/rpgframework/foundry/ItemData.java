package de.rpgframework.foundry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prelle
 *
 */
public class ItemData<T> extends DocumentData {
	
	public String type;
	public String img;
	private T data;
	private List<Object> effects;

	//-------------------------------------------------------------------
	public ItemData(String name, String type, T data) {
		this.name = name;
		this.type = type;
		this.data = data;
		effects = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public T getData() { return data; }

}
