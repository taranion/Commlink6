package de.rpgframework.eden.foundry.sr6;

/**
 * @author prelle
 *
 */
public class TokenEntry {
	
	public static class AttributeBar {
		public String attribute;
		public AttributeBar() {}
		public AttributeBar(String attrib) {this.attribute = attrib;}
	}
	
	
	public String name;
	public String img;
	public int displayName;
	public boolean vision;
	public int disposition = 0;
	public int displayBars = 20;
	public AttributeBar bar1;
	public AttributeBar bar2;

	//-------------------------------------------------------------------
	/**
	 */
	public TokenEntry() {
	}

}
