package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.RecommendingController;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CritterPowerController;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public abstract class CommonSR6CharacterGenerator extends SR6CharacterControllerImpl
		implements SR6CharacterGenerator {

	protected static final Logger logger = System.getLogger(CommonSR6CharacterGenerator.class.getPackageName());

	protected IMetatypeController meta;
	protected MagicOrResonanceController magicReso;

	// -------------------------------------------------------------------
	protected CommonSR6CharacterGenerator() {
//		updateEffectiveRules();
		super.critter = new SR6CritterPowerController(this);
	}

	//-------------------------------------------------------------------
	public CommonSR6CharacterGenerator(Shadowrun6Character model, CharacterHandle handle, Class<?> charGenSettingsClazz) {
		super(model, handle, charGenSettingsClazz);
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
//		updateEffectiveRules();
		createPartialController();
	}

	// --------------------------------------------------------------------
	protected abstract void setupProcessChain();

	protected abstract void initializeModel();

	//-------------------------------------------------------------------
	@Override
	public void setModel(Shadowrun6Character data) {
		super.setModel(data);
		initializeModel();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#setModel(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void setModel(Shadowrun6Character model, CharacterHandle handle) {
		super.model = model;
		super.handle= handle;
		if (model.getMetatype() == null)
			model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
		ruleCtrl = new RuleController(model, Shadowrun6Core.getItemList(RuleInterpretation.class), Shadowrun6Rules.values());
		setupProcessChain();
		runProcessors();
	}

//	// -------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
//	 */
//	@Override
//	public void start(Shadowrun6Character model) {
//		super.model = model;
//		if (model.getMetatype() == null)
//			model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
//		setupProcessChain();
//		runProcessors();
//	}
//
//	// -------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
//	 */
//	@Override
//	public void continueCreation(Shadowrun6Character model) {
//		super.model = model;
//		setupProcessChain();
//		runProcessors();
//	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#canBeFinished()
	 */
	@Override
	public boolean canBeFinished() {
		for (ToDoElement todo : getToDos()) {
			if (todo.getSeverity()==Severity.STOPPER)
				return false;
		}
		return true;
	}

	//-------------------------------------------------------------------
	public static ItemType getItemType(CarriedItem<ItemTemplate> model) {
		if (model.hasAttribute(SR6ItemAttribute.ITEMTYPE))
			return model.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
		return null;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		logger.log(Level.WARNING, "TODO: finish");
		model.setInCareerMode(true);
		// Reduce Karma
		int maxKarma =  getRuleController().getRuleValueAsInteger(ShadowrunRules.CHARGEN_MAX_KARMA_REMAIN);
		if (model.getKarmaFree()>maxKarma) {
			logger.log(Level.WARNING, "Needed to reduce free Karma from {0} to {1}", model.getKarmaFree(), maxKarma);
			model.setKarmaFree( Math.min(model.getKarmaFree(), maxKarma));
		}
		// Reduce Nuyen
		int maxNuyen =  getRuleController().getRuleValueAsInteger(ShadowrunRules.CHARGEN_MAX_NUYEN_REMAIN);
		if (model.getNuyen()>maxNuyen) {
			logger.log(Level.WARNING, "Needed to reduce free Karma from {0} to {1}", model.getNuyen(), maxNuyen);
			model.setNuyen( Math.min(model.getNuyen(), maxNuyen));
		}

		/* Expand PACKs */
		for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
			if (getItemType(tmp)==ItemType.PACK) {
				logger.log(Level.WARNING, "ToDo: handle PACK "+tmp);
				System.err.println("ToDo: Expand PACK "+tmp);
				((CommonEquipmentGenerator)getEquipmentController()).expandPACK(tmp);
				model.removeCarriedItem(tmp);
			}
		}

	}

	//-------------------------------------------------------------------
	public <T> RecommendingController<T> getRecommendingControllerFor(T item) {
//		if (item instanceof ShadowrunAttribute)
//			return getAttributeController();
		return null;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getMetatypeController()
	 */
	@Override
	public IMetatypeController getMetatypeController() {
		return meta;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getMagicOrResonanceController()
	 */
	@Override
	public IMagicOrResonanceController getMagicOrResonanceController() {
		return magicReso;
	}

}
