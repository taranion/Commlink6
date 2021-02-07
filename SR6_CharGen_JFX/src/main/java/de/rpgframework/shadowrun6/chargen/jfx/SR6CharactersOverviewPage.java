package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.prelle.rpgframework.jfx.pages.CharacterViewLayout;
import org.prelle.rpgframework.jfx.pages.CharactersOverviewPage;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterProviderLoader;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.shadowrun6.chargen.jfx.fxml.ScreenLoader;

/**
 * @author prelle
 *
 */
public class SR6CharactersOverviewPage extends CharactersOverviewPage {
	
	private final static Logger logger = LoggerFactory.getLogger(SR6CharactersOverviewPage.class);

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.pages.CharactersOverviewPage#createCharacterAppLayout()
	 */
	@Override
	protected CharacterViewLayout createCharacterAppLayout() {
		logger.debug("##############createCharacterAppLayout");
		try {
			return ScreenLoader.loadMainScreen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.rpgframework.jfx.pages.CharactersOverviewPage#loadCharacters()
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
