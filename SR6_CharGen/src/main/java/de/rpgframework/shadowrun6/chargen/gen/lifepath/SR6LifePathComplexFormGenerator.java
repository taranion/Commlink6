package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6ComplexFormGenerator;

public class SR6LifePathComplexFormGenerator extends CommonSR6ComplexFormGenerator {

	//-------------------------------------------------------------------
	public SR6LifePathComplexFormGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeSelected(ComplexForm value, Decision... decisions) {
		if (getModel().getMagicOrResonanceType()==null || !getModel().getMagicOrResonanceType().usesResonance())
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);

		Possible poss = super.canBeSelected(value, decisions);
		if (!poss.get())
			return poss;
		if (getFree()<1)
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_POINTS);

		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>(super.process(previous));
		todos.clear();
		maxFree = 0;
		if (getModel().getMagicOrResonanceType()!=null && getModel().getMagicOrResonanceType().usesResonance())
			maxFree = getModel().getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue() * 2;
		free = maxFree - getModel().getComplexForms().size();

		if (free>0) {
			todos.add(new ToDoElement(Severity.WARNING, "Unused complex forms"));
		} else if (free<0) {
			todos.add(new ToDoElement(Severity.STOPPER, "Too many complex forms selected"));
		}

		return unprocessed;
	}
}
