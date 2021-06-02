package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.character.Attachment;
import de.rpgframework.character.Attachment.Format;
import de.rpgframework.character.Attachment.Type;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterProviderLoader;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.jfx.pages.CharactersOverviewPage;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class SR6CharactersOverviewPage extends CharactersOverviewPage {
	
	private final static Logger logger = LogManager.getLogger(SR6CharactersOverviewPage.class);

	//-------------------------------------------------------------------

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterAppLayout()
	 */
	@Override
	protected CharacterViewLayout createCharacterAppLayout() {
		logger.debug("##############createCharacterAppLayout");
		return new SR6CharacterViewLayout();
//		try {
//			return ScreenLoader.loadMainScreen();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(1);
//			return null;
//		}
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
			logger.error("Failed loading characters",e);
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
			logger.error("Failed parsing XML for char",e);
			StringWriter mess = new StringWriter();
			mess.append("Failed loading character\n\n");
			e.printStackTrace(new PrintWriter(mess));
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, mess.toString());
		}
		return null;
	}

}
