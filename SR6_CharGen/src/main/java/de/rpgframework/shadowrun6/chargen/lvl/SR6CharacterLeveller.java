package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.RecommendingController;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.CommonQualityPathController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MartialArtsController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6MetamagicOrEchoController;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.chargen.gen.SR6DataStructureController;
import de.rpgframework.shadowrun6.proc.CalculateDerivedAttributes;
import de.rpgframework.shadowrun6.proc.GetModificationsFromGear;

/**
 * @author prelle
 *
 */
public class SR6CharacterLeveller extends SR6CharacterControllerImpl {

	private final static Logger logger = System.getLogger(SR6CharacterLeveller.class.getPackageName());

	private boolean setupDone;

//	//-------------------------------------------------------------------
//	/**
//	 */
//	public SR6CharacterLeveller() {
//		// TODO Auto-generated constructor stub
//	}

	//-------------------------------------------------------------------
	/**
	 * @param model
	 * @param handle
	 * @param charGenSettingsClazz
	 */
	public SR6CharacterLeveller(Shadowrun6Character model, CharacterHandle handle, Class<? extends CommonSR6GeneratorSettings> charGenSettingsClazz) {
		super(model, handle, charGenSettingsClazz);
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#getRecommendingControllerFor(java.lang.Object)
	 */
	@Override
	public <T> RecommendingController<T> getRecommendingControllerFor(T item) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl#createPartialController()
	 */
	@Override
	protected void createPartialController() {
		attributes= new SR6AttributeLeveller(this);
		skills    = new SR6SkillLeveller(this);
		qualities = new SR6QualityLeveller(this);
		qPaths    = new CommonQualityPathController(this);
		equipment = new SR6EquipmentLeveller(this);
		spells    = new SR6SpellLeveller(this);
		rituals   = new SR6RitualLeveller(this);
		adeptPowers = new SR6AdeptPowerLeveller(this);
		complex   = new SR6ComplexFormLeveller(this);
		metaEcho  = new SR6MetamagicOrEchoController(this, false);
		sins      = new SR6SINLeveller(this);
		lifestyles= new SR6LifestyleLeveller(this);
		contacts  = new SR6ContactLeveller(this);
		dataStructures = new SR6DataStructureController(this);
		martial   = new SR6MartialArtsController(this);

		setupProcessChain();
	}

	// --------------------------------------------------------------------
	private void setupProcessChain() {
		if (logger.isLoggable(Level.DEBUG))
			logger.log(Level.DEBUG, "ENTER: setupProcessChain()");
		try {
			if (setupDone) {
				return;
			}

			processChain.addAll(Shadowrun6Tools.getCharacterProcessingSteps(model, locale));
			processChain.add(qualities);
			processChain.add(attributes);
			processChain.add(skills);
			processChain.add(spells);
			processChain.add(rituals);
			processChain.add(adeptPowers);
			processChain.add(martial);
			processChain.add(new GetModificationsFromGear(model));
			processChain.add(equipment);
			processChain.add(complex);
			processChain.add(metaEcho);
			processChain.add(dataStructures);
			processChain.add(sins);
			processChain.add(lifestyles);
			processChain.add(contacts);
//			processChain.add(new RemainingKarmaNuyenController(this));

			setupDone = true;
		} finally {
			if (logger.isLoggable(Level.DEBUG))
				logger.log(Level.DEBUG, "LEAVE: setupProcessChain()");
		}
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterControllerImpl#updateEffectiveRules()
//	 */
//	@Override
//	protected void updateEffectiveRules() {
//		// TODO Auto-generated method stub
//
//	}

}
