package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.ResourceI18N;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.Attachment.Format;
import de.rpgframework.character.Attachment.Type;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.character.CharacterProviderLoader;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.jfx.CharacterHandleBox;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.jfx.pages.CharactersOverviewPage;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6GeneratorSettings;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.chargen.lvl.SR6CharacterLeveller;
import javafx.scene.image.Image;

/**
 * @author prelle
 *
 */
public class SR6CharactersOverviewPage extends CharactersOverviewPage {

	private final static Logger logger = System.getLogger(SR6CharactersOverviewPage.class.getPackageName());

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterGenerator()
	 */
	@Override
	protected CharacterGenerator<?, ?> createCharacterGenerator() {
		logger.log(Level.DEBUG, "ENTER createCharacterGenerator");
		Shadowrun6Character model = new Shadowrun6Character();
		@SuppressWarnings("unchecked")
		Class<SR6CharacterGenerator> clazz = (Class<SR6CharacterGenerator>) CharacterGeneratorRegistry.getGenerators().get(0);
		try {
			logger.log(Level.INFO, "creating "+clazz);
			SR6CharacterGenerator wrapped = (SR6CharacterGenerator) clazz
					.getConstructor(Shadowrun6Character.class, CharacterHandle.class)
					.newInstance(model, null);
			GeneratorWrapper ret = new GeneratorWrapper(model, null);
			ret.setWrapped(wrapped);
			return ret;
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed creating CharacterGenerator "+clazz,e);
			System.exit(1);
			return null;
		} finally {
			logger.log(Level.TRACE, "LEAVE createCharacterGenerator");
		}
	}

	//-------------------------------------------------------------------
	protected void styleCharacterHandleBox(CharacterHandle charac, CharacterHandleBox card) {
		CharacterProvider prov = CharacterProviderLoader.getCharacterProvider();
		byte[] imageBytes = null;
		try {
			Attachment attach = prov.getFirstAttachment(charac, Type.CHARACTER, Format.IMAGE);
			if (attach!=null) {
				imageBytes = attach.getData();
			}
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed loading image attachment",e);
		}
		if (imageBytes==null) {
			card.setImage(new Image(SR6CharactersOverviewPage.class.getResourceAsStream("Person.png")));
		} else {
			card.setImage(new Image(new ByteArrayInputStream(imageBytes)));
		}

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterController(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	protected CharacterController<?, ?> createCharacterController(RuleSpecificCharacterObject<?,?,?,?> rawModel, CharacterHandle handle) {
		logger.log(Level.INFO, "ENTER: create Character Controller");
		try {
		Shadowrun6Character model = (Shadowrun6Character) rawModel;

		if (model.isInCareerMode()) {
			logger.log(Level.ERROR, "ToDo: Create Karma Leveller");
			Class<? extends CommonSR6GeneratorSettings> settings = null;
			if (model.getCharGenUsed() != null) {
				if (model.getCharGenUsed().equals("prio")) {
					settings = SR6PrioritySettings.class;
				}
			}
			if (settings== null) {
				logger.log(Level.ERROR, "Don't know how to get settings class for '{0}'",model.getCharGenUsed());
				throw new NullPointerException("Don't know how to get settings class for '"+model.getCharGenUsed()+"'");
			}

			SR6CharacterLeveller leveller = new SR6CharacterLeveller(model, handle, settings);
			//System.exit(1);
			return leveller;
		} else {

			GeneratorWrapper wrapper = new GeneratorWrapper((Shadowrun6Character) model, handle);
			logger.log(Level.INFO, "ToDo: Detect previously used generator: {0}", model.getCharGenUsed());
			try {
				String json = model.getChargenSettingsJSON();
				logger.log(Level.ERROR, "JSON = {0}",json);
				if (model.getCharGenUsed() == null && json!=null) {
					if (json.startsWith("{\"priorities\"")) {
						logger.log(Level.ERROR, "Guessing PriorityGenerator as previously used generator");
						model.setCharGenUsed("prio");
						BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, ResourceI18N.get(RES, "error.no_chargen_in_character")+"\nThere are hints that the priority generator has been used, so I assume this now.");
					} else if (json.startsWith("{\"characterPoints\"")) {
						logger.log(Level.ERROR, "Guessing Point Buy as previously used generator");
						model.setCharGenUsed("pointbuy");
						BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, ResourceI18N.get(RES, "error.no_chargen_in_character")+"\nThere are hints that the point buy generator has been used, so I assume this now.");
					}
				}
				if (model.getCharGenUsed() == null) {
					throw new NullPointerException(ResourceI18N.get(RES, "error.no_chargen_in_character"));
				}
//			switch (model.getCharGenUsed()) {
//			case "prio":
//				model.setCharGenSettings( (new Gson()).fromJson(model.getChargenSettingsJSON(), SR6PrioritySettings.class) );
//				break;
//			default:
//				logger.log(Level.ERROR, "Don't know how to read settings from "+model.getCharGenUsed());
//				System.exit(1);
//			}

				SR6CharacterGenerator charGen = CharacterGeneratorRegistry.getGenerator(model.getCharGenUsed(), model,
						handle);
				wrapper.setWrapped(charGen);
				logger.log(Level.INFO, "Return "+wrapper);
				return wrapper;
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error creating generator '" + model.getCharGenUsed(), e);
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2,
						"Internal error creating character generator instance", e);
				return null;
			}
		}
		} finally {
			logger.log(Level.INFO, "LEAVE: create Character Controller");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterAppLayout()
	 */
	@Override
	protected CharacterViewLayout createCharacterAppLayout(CharacterController<?, ?> control) {
		logger.log(Level.DEBUG, "ENTER: createCharacterViewLayout");
		try {
			SR6CharacterViewLayout ret = new SR6CharacterViewLayout();
			if (control!=null)
				ret.setController((SR6CharacterController) control);
			return ret;
		} finally {
			logger.log(Level.DEBUG, "LEAVE: createCharacterViewLayout");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#loadCharacters()
	 */
	@Override
	protected List<CharacterHandle> loadCharacters() {
		try {
			return CharacterProviderLoader.getCharacterProvider().getMyCharacters(RoleplayingSystem.SHADOWRUN6);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed loading characters",e);
		}
		return new ArrayList<CharacterHandle>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#loadRuleSpecific(de.rpgframework.character.CharacterHandle)
	 */
	@Override
	protected RuleSpecificCharacterObject<?,?,?,?> loadRuleSpecific(byte[] raw) throws Exception {
		logger.log(Level.INFO, "ENTER loadRuleSpecific");

		try {
			Shadowrun6Character rawChar = Shadowrun6Core.decode(raw);
			Shadowrun6Tools.resolveChar(rawChar);
			return rawChar;
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed parsing XML for char",e);
			StringWriter mess = new StringWriter();
			mess.append("Failed loading character\n\n");
			e.printStackTrace(new PrintWriter(mess));
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, mess.toString());
			return null;
		} finally {
			logger.log(Level.INFO, "LEAVE loadRuleSpecific");
		}
	}

	//-------------------------------------------------------------------
	protected void exportClicked(CharacterHandle handle) {
		Shadowrun6Character charac = (Shadowrun6Character) handle.getCharacter();
		System.err.println("SR6CharactersOverviewPage: "+charac.getName()+" with "+charac.getAttribute(ShadowrunAttribute.ESSENCE));
		super.exportClicked(handle);
	}

	//-------------------------------------------------------------------
	public boolean needsCreationModeWarning(CharacterHandle charac) {
		Shadowrun6Character model = (Shadowrun6Character) charac.getCharacter();
		if (model.isInCareerMode()) return false;

		return "prio".equals(model.getCharGenUsed());
	}

	//-------------------------------------------------------------------
//	public String getCreationModeWarning(CharacterHandle charac) {
//		return ResourceI18N.get(CharacterExportPluginConfigPane.RES, "dialog.exportcreationmodewarning.message");
//	}

}
