package de.rpgframework.shadowrun6.chargen.gen.pointbuy;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun.chargen.gen.IComplexFormGenerator;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6ComplexFormGenerator;

/**
 * @author prelle
 *
 */
public class SR6PointBuyComplexFormGenerator extends CommonSR6ComplexFormGenerator implements IComplexFormController, IComplexFormGenerator {

	//-------------------------------------------------------------------
	protected SR6PointBuyComplexFormGenerator(SR6CharacterController parent) {
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
		
		if (free>0)
			return Possible.TRUE;
		
		// Buy no more than RESONANCE*2
		int max = getModel().getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue()*2;
		int cnt = getModel().getComplexForms().size();
		if ( cnt > max) {
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
			free = 0;
			
			Shadowrun6Character model = getModel();
			if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesResonance()) {				
				SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
				free = settings.perAttrib.get(ShadowrunAttribute.RESONANCE).base;
				logger.log(Level.INFO, "Have {0} free complex forms", free);
			}
			maxFree = free;
			
			int byKarma = 0;
			for (ComplexFormValue val : model.getComplexForms()) {
				if (free>0)
					free--;
				else {
					byKarma++;
					model.setKarmaFree( model.getKarmaFree() -5 );
					logger.log(Level.INFO, "Pay complex form ''{0}'' with 5 Karma", val.getModifyable().getId());
				}
			}
			
			// Summary and eventually warn
			logger.log(Level.INFO, "Have {0} remaining free complex forms", free);
			if (free>0) {
				todos.add(new ToDoElement(Severity.WARNING, "Unused complex forms"));
			} else if (byKarma>0) {
				boolean karmaAllowed =  parent.getRuleController().getRuleValueAsBoolean(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA);
				if (!karmaAllowed) {
					todos.add(new ToDoElement(Severity.STOPPER, "Too many complex forms bought"));
				}
			}
			
			return unprocessed;
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
	}

}
