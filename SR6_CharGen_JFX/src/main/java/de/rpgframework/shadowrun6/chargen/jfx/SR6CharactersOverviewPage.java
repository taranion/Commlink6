package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterProviderLoader;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.CharacterGenerator;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.jfx.pages.CharactersOverviewPage;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;

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
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterController(de.rpgframework.character.RuleSpecificCharacterObject)
	 */
	@Override
	protected CharacterController<?, ?> createCharacterController(RuleSpecificCharacterObject<?> model) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterAppLayout()
	 */
	@Override
	protected CharacterViewLayout createCharacterAppLayout(CharacterController<?, ?> control) {
		logger.log(Level.DEBUG, "ENTER: createCharacterViewLayout");
		try {
			return new SR6CharacterViewLayout();
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
	protected RuleSpecificCharacterObject<?> loadRuleSpecific(byte[] raw) {
		try {
			return Shadowrun6Core.load(raw);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed parsing XML for char",e);
			StringWriter mess = new StringWriter();
			mess.append("Failed loading character\n\n");
			e.printStackTrace(new PrintWriter(mess));
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, mess.toString());
		}
		return null;
	}

}
