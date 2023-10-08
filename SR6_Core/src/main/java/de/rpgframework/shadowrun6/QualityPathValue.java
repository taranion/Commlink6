package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.ComplexDataItemValue;

/**
 * @author prelle
 *
 */
public class QualityPathValue extends ComplexDataItemValue<QualityPath> {

	@ElementList(type = QualityPathStepValue.class, entry = "step", inline=true)
	private List<QualityPathStepValue> stepsTaken;

	//-------------------------------------------------------------------
	public QualityPathValue() {
		stepsTaken = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public QualityPathValue(QualityPath data) {
		super(data);
		stepsTaken = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public void addStepTaken(QualityPathStepValue step) {
		stepsTaken.add(step);
	}

	//-------------------------------------------------------------------
	public void removeStepTaken(QualityPathStepValue step) {
		stepsTaken.remove(step);
	}

	//-------------------------------------------------------------------
	public List<QualityPathStepValue> getStepsTaken() {
		return stepsTaken;
	}

	//-------------------------------------------------------------------
	public QualityPathStepValue getStepTaken(QualityPathStep step) {
		for (QualityPathStepValue val : stepsTaken) {
			if (val.getResolved()==step) return val;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public boolean hasStepTaken(QualityPathStep step) {
		return stepsTaken.stream().map(sv -> sv.getResolved()).anyMatch(s -> s==step);
	}

}
