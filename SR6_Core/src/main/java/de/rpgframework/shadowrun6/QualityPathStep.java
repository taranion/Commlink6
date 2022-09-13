package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="step")
public class QualityPathStep extends ComplexDataItem {
	
	@ElementList(type = String.class, entry = "next")
	private List<String> nextSteps;
	
	@Attribute
	private int level;

	//-------------------------------------------------------------------
	public QualityPathStep() {
		nextSteps = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the nextSteps
	 */
	public List<String> getNextSteps() {
		return nextSteps;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

}
