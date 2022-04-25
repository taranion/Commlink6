package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Element;
import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.Lifestyle;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun6.persist.LifestyleQualityConverter;

/**
 * @author prelle
 *
 */
public class SR6Lifestyle extends Lifestyle {
	
	@Element(name = "neigh")
	@AttribConvert(LifestyleQualityConverter.class)
	private LifestyleQuality neighborhood;
	private LifestyleQuality necessities;
	private LifestyleQuality comforts;
	private LifestyleQuality security;
	private LifestyleQuality entertainment;
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
		int sum 
			= getNeighborHood().getLifestylePoints()
			+ getNecessities().getLifestylePoints()
			+ getComforts().getLifestylePoints()
			+ getSecurity().getLifestylePoints()
			+ getEntertainment().getLifestylePoints()
			+ getSpace().getLifestylePoints();
		
		for (String id : qualities) {
			Quality qual = Shadowrun6Core.getItem(Quality.class, id);
			if (qual!=null) {
				sum += qual.getKarmaCost();
			}
		}
		
		return sum;
	}

}
