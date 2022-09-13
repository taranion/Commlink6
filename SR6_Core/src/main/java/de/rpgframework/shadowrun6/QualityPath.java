package de.rpgframework.shadowrun6;

import java.util.List;

import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.DataSet;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "qpath")
public class QualityPath extends ComplexDataItem {

	@ElementList(entry = "step", type = QualityPathStep.class, inline=true)
	private List<QualityPathStep> steps;
	
	//-------------------------------------------------------------------
	/**
	 */
	public QualityPath() {
		// TODO Auto-generated constructor stub
	}
	
	//-------------------------------------------------------------------
	public List<QualityPathStep> getSteps() {
		return steps;
	}
	
	//-------------------------------------------------------------------
	public void assignToDataSet(DataSet set) {
		super.assignToDataSet(set);
		for (QualityPathStep step : steps) {
			step.setParentItem(this);
			step.assignToDataSet(set);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.ComplexDataItem#validate()
	 */
	@Override
	public void validate() {
		super.validate();
		
		for (QualityPathStep step : steps) {
			step.validate();
		}
	}
}
