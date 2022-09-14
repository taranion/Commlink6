package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathStep;
import de.rpgframework.shadowrun6.QualityPathStepValue;
import de.rpgframework.shadowrun6.QualityPathValue;

/**
 * @author prelle
 *
 */
public interface IQualityPathController extends ComplexDataItemController<QualityPath, QualityPathValue> {
	
	/**
	 * Get free karma that can be only spent here 
	 */
	public int getKarmaBalance();

	/**
	 * Can a step within a path be taken?
	 */
	public Possible canBeSelected(QualityPathValue path, QualityPathStep step, Decision...decisions);

	/**
	 * Take a step within a path
	 */
	public OperationResult<QualityPathStepValue> select(QualityPathValue path, QualityPathStep step, Decision...decisions);

	/**
	 * Can a step within a path be undone?
	 */
	public Possible canBeDeselected(QualityPathValue path, QualityPathStepValue step);
	
	public OperationResult<QualityPathStepValue> deselect(QualityPathValue path, QualityPathStepValue step);
	
}
