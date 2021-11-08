package de.rpgframework.shadowrun6.chargen.gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.IGeneratorWrapper;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleValue;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;

/**
 * @author stefa
 *
 */
public class GeneratorWrapper implements SR6CharacterGenerator, IGeneratorWrapper<Shadowrun6Character, SR6CharacterGenerator> {

	private static Logger logger = LogManager.getLogger(GeneratorWrapper.class);
	
	private Shadowrun6Character cached;
	private CharacterHandle cachedHandle;
	private SR6CharacterGenerator wrapped;

	//-------------------------------------------------------------------
	public GeneratorWrapper(Shadowrun6Character model, CharacterHandle handle) {
		this.cached = model;
		this.cachedHandle = handle;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return wrapped.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getName()
	 */
	@Override
	public String getName() {
		return wrapped.getName();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return wrapped.getDescription();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getWizardPages()
	 */
	@Override
	public WizardPageType[] getWizardPages() {
		return wrapped.getWizardPages();
	}
	
	//-------------------------------------------------------------------
	public boolean canBeFinished() {
		if (wrapped instanceof CharacterGenerator)
			return ((CharacterGenerator<?>)wrapped).canBeFinished();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#getModel()
	 */
	@Override
	public Shadowrun6Character getModel() {
		if (wrapped!=null)
			return wrapped.getModel();
		return cached;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#addListener(de.rpgframework.genericrpg.chargen.ControllerListener)
	 */
	@Override
	public void addListener(ControllerListener listener) {
		wrapped.addListener(listener);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#removeListener(de.rpgframework.genericrpg.chargen.ControllerListener)
	 */
	@Override
	public void removeListener(ControllerListener listener) {
		wrapped.removeListener(listener);
	}
	
	//-------------------------------------------------------------------
	public SR6CharacterGenerator getWrapped() {
		return wrapped;
	}
	
	//-------------------------------------------------------------------
	public void setWrapped(SR6CharacterGenerator newCtrl) {
		logger.info("#################Generator changed to "+newCtrl+"\n\n\n");
		// Move all existing listener to new controller
		if (wrapped!=null) {
			for (ControllerListener callback : new ArrayList<>(wrapped.getListener())) {
				newCtrl.addListener(callback);
				wrapped.removeListener(callback);
			}
		}
		newCtrl.setModel(cached, cachedHandle);
		wrapped = newCtrl;
		wrapped.fireEvent(BasicControllerEvents.GENERATOR_CHANGED, newCtrl);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#getListener()
	 */
	@Override
	public Collection<ControllerListener> getListener() {
		return wrapped.getListener();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#fireEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void fireEvent(ControllerEvent type, Object...param) {
		wrapped.fireEvent(type, param);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#runProcessors()
	 */
	@Override
	public void runProcessors() {
		wrapped.runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return wrapped.getToDos();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#save(byte[])
	 */
	@Override
	public boolean save(byte[] data) throws IOException {
		return wrapped.save(data);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getMetatypeController()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public IMetatypeController getMetatypeController() {
		return wrapped.getMetatypeController();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#setModel(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void setModel(Shadowrun6Character model, CharacterHandle handle) {
		wrapped.setModel(model, handle);
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
//	 */
//	@Override
//	public void start(Shadowrun6Character model) {
//		wrapped.start(model);
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#continueCreation(de.rpgframework.character.RuleSpecificCharacterObject)
//	 */
//	@Override
//	public void continueCreation(Shadowrun6Character model) {
//		wrapped.continueCreation(model);
//	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getAttributeController()
	 */
	@Override
	public IAttributeController getAttributeController() {
		return wrapped.getAttributeController();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController#getSkillController()
	 */
	@Override
	public SR6SkillController getSkillController() {
		return wrapped.getSkillController();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController#getQualityController()
	 */
	@Override
	public IQualityController getQualityController() {
		return wrapped.getQualityController();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#getRule(de.rpgframework.genericrpg.chargen.Rule)
	 */
	@Override
	public <T> T getRule(Rule rule) {
		return wrapped.getRule(rule);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterController#getRules()
	 */
	@Override
	public List<RuleValue> getRules() {
		return wrapped.getRules();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getMagicOrResonanceController()
	 */
	@Override
	public IMagicOrResonanceController getMagicOrResonanceController() {
		return wrapped.getMagicOrResonanceController();
	}

}
