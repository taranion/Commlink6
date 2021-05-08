package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterProviderLoader;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.jfx.pages.CharacterViewLayout;
import de.rpgframework.jfx.pages.CharactersOverviewPage;

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

}
