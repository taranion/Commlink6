package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.Page;
import org.prelle.rpgframework.jfx.pages.CharacterViewLayout;

import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.wizard.GenerationWizard;

/**
 * @author stefa
 *
 */
public class SR6CharacterViewLayout extends CharacterViewLayout<ShadowrunAttribute, Shadowrun6Character> implements ControllerListener {
	
	private final static Logger logger = LogManager.getLogger(SR6CharacterViewLayout.class);

	private CharacterOverviewController controller;
	
	//-------------------------------------------------------------------
	public void setController(CharacterOverviewController ctrl) {
		this.controller = ctrl;		
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.pages.CharacterViewLayout#startCreation()
	 */
	@Override
	public void startCreation() {
		logger.info("Start creation");
		PriorityCharacterGenerator gen = new PriorityCharacterGenerator();
		GeneratorWrapper wrapper = new GeneratorWrapper(gen);
		gen.start(new Shadowrun6Character());
		logger.info("Create wizard for "+wrapper);
		GenerationWizard wizard = new GenerationWizard(wrapper);
		CloseType close = getApplication().showAndWait(wizard);
		logger.info("Wizard closed via "+close);
		controller.refresh();
		if (close==CloseType.FINISH) {
			gen.finish();
			try {
				gen.save(Shadowrun6Core.save(gen.getModel()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (close==CloseType.CANCEL) {
			getApplication().closeAppLayout();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.pages.CharacterViewLayout#continueCreation(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void continueCreation(Shadowrun6Character model) {
		logger.info("Continue creation");
		logger.warn("ToDo: Detect previously used generator");
		PriorityCharacterGenerator gen = new PriorityCharacterGenerator();
		GeneratorWrapper wrapper = new GeneratorWrapper(gen);
		wrapper.continueCreation((Shadowrun6Character) model);
		GenerationWizard wizard = new GenerationWizard(wrapper);
		CloseType close = getApplication().showAndWait(wizard);
		logger.info("Wizard closed via "+close);
		controller.refresh();
		if (close==CloseType.FINISH) {
			gen.finish();
			try {
				gen.save(Shadowrun6Core.save((Shadowrun6Character) model));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.pages.CharacterViewLayout#edit(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void edit(Shadowrun6Character model) {
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.debug("RCV "+type);
		Page page = getVisiblePage();
		if (page!=null && page instanceof ControllerListener) {
			((ControllerListener)page).handleControllerEvent(type, param);
		}
	}

}
