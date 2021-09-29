package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.Page;

import de.rpgframework.character.CharacterIOException;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.page.BasicDataPage;
import de.rpgframework.shadowrun6.chargen.jfx.wizard.GenerationWizard;

/**
 * @author prelle
 *
 */
public class SR6CharacterViewLayout extends CharacterViewLayout<ShadowrunAttribute, Shadowrun6Character> implements ControllerListener {
	
	private final static Logger logger = LogManager.getLogger(SR6CharacterViewLayout.class);
	
	private BasicDataPage page;
	
	//-------------------------------------------------------------------
	/**
	 * @param ctrl Either a GeneratorWrapper or a CharacterLeveller
	 */
	public SR6CharacterViewLayout() {
		initPages();
	}
	
	//-------------------------------------------------------------------
	public void initPages() {
		page = new BasicDataPage();
		getPages().add(page);
		
		Page skillPage = new Page("Skills");
		getPages().add(skillPage);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#startCreation()
	 */
	@Override
	public void startCreation() {
		logger.warn("ENTER: Start creation");
		GeneratorWrapper wrapper = new GeneratorWrapper(new Shadowrun6Character());
		page.setController(wrapper);
		//skillPage.setController(wrapper);
		logger.warn("Create wizard for "+wrapper);
		GenerationWizard wizard = new GenerationWizard(wrapper);
		while (true) {
			CloseType close = FlexibleApplication.getInstance().showAndWait(wizard);
			logger.info("Wizard closed via "+close);
			//		controller.refresh();
			if (close==CloseType.FINISH) {
				wrapper.finish();
				try {
					logger.debug("Call save() on "+wrapper.getClass());
					wrapper.save(Shadowrun6Core.save(wrapper.getModel()));
					return;
				} catch (CharacterIOException e) {
					super.showCharacterIOException(e, wrapper.getModel());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (close==CloseType.CANCEL) {
				//			getApplication().closeAppLayout();
				logger.info("call historyBack() on "+getAppLayout().getBreadcrumb());
				getAppLayout().historyBack();
				return;
			}
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#continueCreation(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void continueCreation(Shadowrun6Character model) {
		logger.info("Continue creation");
		GeneratorWrapper wrapper = new GeneratorWrapper((Shadowrun6Character) model);
		logger.warn("ToDo: Detect previously used generator");
		try {
			SR6CharacterGenerator charGen = CharacterGeneratorRegistry.getGenerator( model.getCharGenUsed() );
			wrapper.setWrapped(charGen);
		} catch (Exception e) {
			logger.fatal("Error creating generator '"+model.getCharGenUsed(),e);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Internal error creating character generator instance");
			return;
		}
		page.setController(wrapper);
		GenerationWizard wizard = new GenerationWizard(wrapper);
		CloseType close = FlexibleApplication.getInstance().showAndWait(wizard);
		logger.info("Wizard closed via "+close);
//		controller.refresh();
		if (close==CloseType.FINISH) {
			wrapper.finish();
			try {
				wrapper.save(Shadowrun6Core.save((Shadowrun6Character) model));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#edit(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void edit(Shadowrun6Character model) {
		logger.info("ToDo: Edit "+model);
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.debug("RCV "+type);
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			page.setController(null);
		
		}
//		Page page = getVisiblePage();
//		if (page!=null && page instanceof ControllerListener) {
//			((ControllerListener)page).handleControllerEvent(type, param);
//		}
	}

}
