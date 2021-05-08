package de.rpgframework.shadowrun6.chargen.gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.IGeneratorWrapper;
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

	private static Logger logger = LoggerFactory.getLogger(GeneratorWrapper.class);
	
	private SR6CharacterGenerator wrapped;

	//-------------------------------------------------------------------
	public GeneratorWrapper(SR6CharacterGenerator wrapped) {
		this.wrapped = wrapped;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getId()
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getWizardPages()
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
		return wrapped.getModel();
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
		for (ControllerListener callback : new ArrayList<>(wrapped.getListener())) {
			newCtrl.addListener(callback);
			wrapped.removeListener(callback);
		}
		newCtrl.continueCreation(wrapped.getModel());
		wrapped = newCtrl;
		wrapped.fireEvent(BasicControllerEvents.GENERATOR_CHANGED);
	}

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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterGenerator#getMetatypeController()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public IMetatypeController getMetatypeController() {
		return wrapped.getMetatypeController();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#start(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void start(Shadowrun6Character model) {
		wrapped.start(model);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#continueCreation(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void continueCreation(Shadowrun6Character model) {
		wrapped.continueCreation(model);
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

}
