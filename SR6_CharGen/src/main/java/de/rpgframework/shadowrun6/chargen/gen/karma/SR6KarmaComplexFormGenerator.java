package de.rpgframework.shadowrun6.chargen.gen.karma;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.IComplexFormGenerator;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6ComplexFormGenerator;

/**
 * @author prelle
 *
 */
public class SR6KarmaComplexFormGenerator extends CommonSR6ComplexFormGenerator implements IComplexFormController, IComplexFormGenerator {

	protected static Logger logger = System.getLogger(ControllerImpl.class.getPackageName()+".cforms");
	private static MultiLanguageResourceBundle RES = SR6CharacterGenerator.RES;

	//-------------------------------------------------------------------
	protected SR6KarmaComplexFormGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#canBeSelected(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public Possible canBeSelected(ComplexForm value, Decision... decisions) {
		Possible poss = super.canBeSelected(value, decisions);
		if (!poss.get())
			return poss;

		// Buy no more than RESONANCE*2
		int max = getModel().getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue(ValueType.NATURAL)*2;
		int cnt = getModel().getComplexForms().size();
		if ( cnt >= max) {
			return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_MAX_COMPLEX_FORMS, max);
		}

		// No more free complex forms. Evtl. buy with karma
		if (getModel().getKarmaFree()>=5)
			return Possible.TRUE;
		return new Possible(Severity.STOPPER, IRejectReasons.RES, IRejectReasons.IMPOSS_NOT_ENOUGH_KARMA, 5);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>(previous);

		try {
			todos.clear();

			Shadowrun6Character model = getModel();
			maxFree = 0;

			SR6KarmaSettings settings = parent.getModel().getCharGenSettings(SR6KarmaSettings.class);
			settings.cforms=0;
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {
				int reso = model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue();
				maxFree= reso*2;
			}
			logger.log(Level.INFO, "May buy up to {0} complex forms", maxFree);

			// Pay Karma
			int karmaNeeded = model.getComplexForms().size()*5;
			logger.log(Level.INFO, "Pay {0} Karma for complex forms", karmaNeeded);
			settings.cforms = karmaNeeded;
			model.setKarmaFree( model.getKarmaFree() - karmaNeeded);
			model.setKarmaInvested( model.getKarmaInvested() - karmaNeeded);

			free = maxFree - model.getComplexForms().size();
			// Ensure not too many complex forms
			if ((model.getComplexForms().size())>maxFree) {
				todos.add(new ToDoElement(Severity.STOPPER, RES, SR6RejectReasons.TODO_CFORMS_TOO_MANY));
			}

			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
