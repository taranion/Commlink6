package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.Page;
import org.prelle.javafx.WindowMode;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterIOException;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.page.BasicDataPage2;
import de.rpgframework.shadowrun6.chargen.jfx.wizard.GenerationWizard;

/**
 * @author prelle
 *
 */
public class SR6CharacterViewLayout extends CharacterViewLayout<ShadowrunAttribute, Shadowrun6Character> implements ControllerListener {
	
	private final static Logger logger = System.getLogger(SR6CharacterViewLayout.class.getPackageName());
	
	private BasicDataPage2 page;
	
	//-------------------------------------------------------------------
	/**
	 * @param ctrl Either a GeneratorWrapper or a CharacterLeveller
	 */
	public SR6CharacterViewLayout() {
		initPages();
		
		setOnBackAction(ev -> logger.log(Level.INFO, "BACK action"));
	}
	
	//-------------------------------------------------------------------
	public void initPages() {
		page = new BasicDataPage2();
		Page skillPage = new Page("Skills");
		getPages().addAll(page, skillPage);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#startCreation()
	 */
	@Override
	public void startCreation(CharacterGenerator<?,?> charGen) {
		logger.log(Level.WARNING, "ENTER: Start creation");
		GeneratorWrapper wrapper = (GeneratorWrapper) charGen; //new GeneratorWrapper(new Shadowrun6Character(), null);
		
		handleControllerEvent(BasicControllerEvents.GENERATOR_CHANGED, wrapper);
		
//		wrapper.setWrapped(new PriorityCharacterGenerator());
		logger.log(Level.WARNING, "Create wizard for "+wrapper);
		GenerationWizard wizard = new GenerationWizard(wrapper);
		while (true) {
			CloseType close = FlexibleApplication.getInstance().showAndWait(wizard);
			logger.log(Level.INFO, "Wizard closed via "+close);
			//		controller.refresh();
			if (close==CloseType.FINISH) {
				wrapper.finish();
				try {
					logger.log(Level.DEBUG, "Call save() on "+wrapper.getClass());
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
				logger.log(Level.INFO, "call historyBack()");
				getApplication().closeScreen(this);
				return;
			}
		}
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#continueCreation(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void continueCreation(Shadowrun6Character model, CharacterHandle handle) {
		logger.log(Level.INFO, "Continue creation");
		GeneratorWrapper wrapper = new GeneratorWrapper((Shadowrun6Character) model, handle);
		logger.log(Level.WARNING, "ToDo: Detect previously used generator");
		try {
			SR6CharacterGenerator charGen = CharacterGeneratorRegistry.getGenerator( model.getCharGenUsed() );
			wrapper.setWrapped(charGen);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Error creating generator '"+model.getCharGenUsed(),e);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Internal error creating character generator instance");
			return;
		}
		page.setController(wrapper);
		GenerationWizard wizard = new GenerationWizard(wrapper);
		CloseType close = FlexibleApplication.getInstance().showAndWait(wizard);
		logger.log(Level.INFO, "Wizard closed via "+close);
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
		logger.log(Level.INFO, "ToDo: Edit "+model);
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.DEBUG, "RCV "+type);
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			page.setController((SR6CharacterController) param[0]);
		
		}
//		Page page = getVisiblePage();
//		if (page!=null && page instanceof ControllerListener) {
//			((ControllerListener)page).handleControllerEvent(type, param);
//		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		logger.log(Level.WARNING, "Mode "+value);
	}

}
