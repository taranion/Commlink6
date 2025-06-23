package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.Date;
import java.util.List;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.Focus;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 *
 */
public class SR6FocusLeveller extends SR6CommonFocusController {

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6FocusLeveller(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ComplexDataItemController#select(de.rpgframework.genericrpg.data.DataItem, de.rpgframework.genericrpg.data.Decision[])
	 */
	@Override
	public OperationResult<FocusValue> select(Focus value, Decision... decisions) {
		OperationResult<FocusValue> ret = super.select(value, decisions);
		if (ret.wasSuccessful()) {
			Shadowrun6Character model = parent.getModel();

			FocusValue val = ret.get();
			int nuyenCost = val.getCostNuyen();
			int karmaCost = val.getCostKarma();

			ValueModification logMod = new ValueModification(ShadowrunReference.FOCUS, value.getId(), 0);
			logMod.setId(val.getUuid());
			logMod.setDate(new Date());
			logMod.setValue(karmaCost);

			boolean payGear = parent.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR);
			if (payGear) {
				logger.log(Level.INFO, "Pay {0} nuyen for focus {1}", nuyenCost, val);
				model.setNuyen( model.getNuyen() - nuyenCost );
			}
			logger.log(Level.INFO, "Pay {0} Karma for focus {1}", karmaCost, val);
			model.setKarmaFree( model.getKarmaFree() - karmaCost );
			model.addToHistory(logMod);

			parent.runProcessors();
		}
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		unprocessed = super.process(unprocessed);

		logger.log(Level.INFO, "Remaining Force pool is "+forcePool);

		return unprocessed;
	}

}
