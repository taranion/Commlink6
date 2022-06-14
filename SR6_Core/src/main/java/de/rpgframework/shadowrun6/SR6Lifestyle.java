package de.rpgframework.shadowrun6;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;

import de.rpgframework.shadowrun.Lifestyle;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun6.persist.LifestyleQualityConverter;

/**
 * @author prelle
 *
 */
public class SR6Lifestyle extends Lifestyle {
	
	@Attribute(name = "neigh")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality neighborhood;
	@Attribute(name = "necess")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality necessities;
	@Attribute(name = "comfort")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality comforts;
	@Attribute(name = "security")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality security;
	@Attribute(name = "entert")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality entertainment;
	@Attribute(name = "space")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality space;
	
	@ElementList(entry = "quality", type = String.class )
	private List<String> qualities;

	//-------------------------------------------------------------------
	public SR6Lifestyle() {
		qualities = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public SR6Lifestyle(LifestyleQuality value) {
		super(value);
		qualities = new ArrayList<>();
		neighborhood = value;
		necessities  = value;
		comforts     = value;
		security     = value;
		entertainment= value;
		space        = value;
	}
	
	//-------------------------------------------------------------------
	public LifestyleQuality getNeighborHood() { return (neighborhood!=null)?neighborhood:getModifyable(); }
	public LifestyleQuality getNecessities() {return (necessities!=null)?necessities:getModifyable(); }
	public LifestyleQuality getComforts() {return (comforts!=null)?comforts:getModifyable(); }
	public LifestyleQuality getSecurity() {return (security!=null)?security:getModifyable();}
	public LifestyleQuality getEntertainment() {return (entertainment!=null)?entertainment:getModifyable();}
	public LifestyleQuality getSpace() {return (space!=null)?space:getModifyable();}

	//-------------------------------------------------------------------
	public int getLifestylePoints() {
		int sum = 0;
		try {
			sum = getNeighborHood().getLifestylePoints()
				+ getNecessities().getLifestylePoints()
				+ getComforts().getLifestylePoints()
				+ getSecurity().getLifestylePoints()
				+ getEntertainment().getLifestylePoints()
				+ getSpace().getLifestylePoints();
		} catch (Exception e) {
			System.getLogger(getClass().getPackageName()).log(Level.ERROR, "Lifestyle broken: "+super.getKey(),e);
		}
		
		for (String id : qualities) {
			Quality qual = Shadowrun6Core.getItem(Quality.class, id);
			if (qual!=null) {
				sum += qual.getKarmaCost();
			}
		}
		
		return sum;
	}

}
