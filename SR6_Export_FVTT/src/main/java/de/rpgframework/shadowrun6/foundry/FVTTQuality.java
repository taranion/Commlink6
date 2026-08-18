package de.rpgframework.shadowrun6.foundry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prelle
 *
 */
public class FVTTQuality extends GenericFVTT {

	public String category;
	public boolean level;
	public boolean positive;
	
	public int    value;
	public String explain;
	public List<Modifier> modifier = new ArrayList();
	
	//-------------------------------------------------------------------
	public FVTTQuality() {
		// TODO Auto-generated constructor stub
	}

}
