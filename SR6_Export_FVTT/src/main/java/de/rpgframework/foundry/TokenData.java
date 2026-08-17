package de.rpgframework.foundry;

import java.util.List;

/**
 * @author prelle
 *
 */
public class TokenData {
	
	public static class TokenBarData {
		/** The attribute path within the Token's Actor data which should be displayed */
		public String attribute;
		public TokenBarData() {}
		public TokenBarData(String attrib) {this.attribute = attrib;}
	}
	
	public static class LightAnimation {
		public float speed;
		public float intensity;
	}
	
	/** The name used to describe the Token */
	public String name;
	/** The display mode of the Token nameplate, from CONST.TOKEN_DISPLAY_MODES */
	public int displayName;
	/** Does this Token uniquely represent a singular Actor, or is it one of many? */
	public boolean actorLink;
	/** A file path to an image or video file used to depict the Token */
	public String img;
	/** The width of the Token in grid units */
	public int width  =1;
	public int height =1;
	public int scale  =1;
	public boolean mirrorX;
	public boolean mirrorY;
	public boolean lockRotation;
	public int rotation = 0;
	/** An array of effect icon paths which are displayed on the Token */
	public List<Object> effects;
	/** A single icon path which is displayed as an overlay on the Token */
	public String overlayEffect;
	
	/** A single icon path which is displayed as an overlay on the Token */
	public float alpha = 1.0f;
	public boolean hidden;
	
	/** Is this Token a source of vision? */
	public boolean vision;
	public int dimSight = 0;
	public int brightSight = 0;
	public int dimLight = 0;
	public int brightLight = 0;
	
	public float sightAngle = 0;
	public float lightAngle = 0;
	public float lightAlpha = 0;
	public LightAnimation lightAnimation;
	public int disposition = 0;
	public int displayBars = 20;
	public TokenBarData bar1;
	public TokenBarData bar2;
	public List<?> flags;
	public boolean randomImg;

	//-------------------------------------------------------------------
	public TokenData() {
	}

}
