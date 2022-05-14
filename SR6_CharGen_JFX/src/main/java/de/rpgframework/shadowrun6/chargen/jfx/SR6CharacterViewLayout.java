package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.WindowMode;

import com.google.gson.Gson;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterIOException;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SpliMoCharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
import de.rpgframework.shadowrun6.chargen.jfx.page.AugmentationPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.BasicDataPage2;
import de.rpgframework.shadowrun6.chargen.jfx.page.CombatPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.MagicPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.MatrixPage;
import de.rpgframework.shadowrun6.chargen.jfx.page.SkillPage;
import de.rpgframework.shadowrun6.chargen.jfx.wizard.GenerationWizard;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class SR6CharacterViewLayout extends CharacterViewLayout<ShadowrunAttribute, Shadowrun6Character, SpliMoCharacterController> implements ControllerListener {
	
	private final static ResourceBundle UI = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private final static Logger logger = System.getLogger(SR6CharacterViewLayout.class.getPackageName());
	
	private BasicDataPage2 pgBasic;
	private SkillPage pgSkills;
	private CombatPage pgCombat;
	private AugmentationPage pgAugment;
	private MagicPage pgMagic;
	private MatrixPage pgMatrix;
	
	//-------------------------------------------------------------------
	/**
	 * @param ctrl Either a GeneratorWrapper or a CharacterLeveller
	 */
	public SR6CharacterViewLayout() {
		super(RoleplayingSystem.SHADOWRUN6);
		initPages();
		
		setOnBackAction(ev -> closeRequested( ));
	}
	
	//-------------------------------------------------------------------
	public void initPages() {
		pgBasic  = new BasicDataPage2();
		pgSkills = new SkillPage();
		pgCombat = new CombatPage();
		pgAugment= new AugmentationPage();
		pgMagic  = new MagicPage();
		pgMatrix = new MatrixPage();
		getPages().addAll(pgBasic, pgSkills, pgCombat, pgAugment, pgMagic, pgMatrix);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#startCreation()
	 */
	@Override
	public void startCreation(CharacterGenerator<?,?> charGen) {
		logger.log(Level.WARNING, "ENTER: Start creation");
		Label tmp = new Label(charGen.getModel().getName());
		tmp.getStyleClass().add(JavaFXConstants.STYLE_HEADING2);
		super.pages.setHeader(tmp);
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
					wrapper.save(Shadowrun6Core.encode(wrapper.getModel()));
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
		logger.log(Level.INFO, "ENTER: Continue creation");
		this.handle = handle;

		try {
			Label tmp = new Label(model.getName());
			tmp.getStyleClass().add(JavaFXConstants.STYLE_HEADING2);
			super.pages.setHeader(tmp);

			GeneratorWrapper wrapper = new GeneratorWrapper((Shadowrun6Character) model, handle);
			logger.log(Level.INFO, "ToDo: Detect previously used generator: {0}", model.getCharGenUsed());
			try {
				logger.log(Level.DEBUG, "JSON = "+model.getChargenSettingsJSON());
//				switch (model.getCharGenUsed()) {
//				case "prio":
//					model.setCharGenSettings( (new Gson()).fromJson(model.getChargenSettingsJSON(), SR6PrioritySettings.class) );
//					break;
//				default:
//					logger.log(Level.ERROR, "Don't know how to read settings from "+model.getCharGenUsed());
//					System.exit(1);
//				}
				
				SR6CharacterGenerator charGen = CharacterGeneratorRegistry.getGenerator(model.getCharGenUsed(), model,
						handle);
				wrapper.setWrapped(charGen);
				super.control = wrapper;
				logger.log(Level.INFO, "Generator to continue with: {0}", charGen.getClass().getSimpleName());
				charGen.setModel(model, handle);
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error creating generator '" + model.getCharGenUsed(), e);
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2,
						"Internal error creating character generator instance");
				return;
			}
			refreshController();
		} finally {
			logger.log(Level.INFO, "LEAVE: Continue creation");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#edit(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	public void edit(Shadowrun6Character model, CharacterHandle handle) {
		logger.log(Level.INFO, "ToDo: Edit "+model);
		this.handle = handle;
		
	}

	//-------------------------------------------------------------------
	private void refreshController() {
		control.setAllowRunProcessor(false);
		pgBasic.setController(control);
		pgSkills.setController(control);
		pgCombat.setController(control);
		pgAugment.setController(control);
		pgMagic.setController(control);
		pgMatrix.setController(control);
		control.setAllowRunProcessor(true);
		control.runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.DEBUG, "RCV "+type);
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			control = (SpliMoCharacterController) param[0];
			refreshController();
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			pgBasic.refresh();
			pgSkills.refresh();
			pgCombat.refresh();
			pgAugment.refresh();
			pgMagic.refresh();
			pgMatrix.refresh();
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

	//-------------------------------------------------------------------
	/**
	 * This method is called, after closing the page has been confirmed -
	 * AND the character has already been saved, if requested.
	 * To enable reverting the character to the state from the disk, reload it here
	 */
	private void closeRequested() {
		logger.log(Level.DEBUG, "ENTER closeRequested");
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#encodeCharacter(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	protected byte[] encodeCharacter(Shadowrun6Character model) throws CharacterIOException {
		logger.log(Level.DEBUG, "START: encodeCharacter");
		return Shadowrun6Core.encode(model); 
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharacterViewLayout#decodeCharacter(byte[])
	 */
	@Override
	protected Shadowrun6Character decodeCharacter(byte[] encoded) throws CharacterIOException {
		logger.log(Level.DEBUG, "START: decodeCharacter");
		Shadowrun6Character model = Shadowrun6Core.decode(encoded);
		Shadowrun6Tools.resolveChar(model);
		return model;
	}

}
