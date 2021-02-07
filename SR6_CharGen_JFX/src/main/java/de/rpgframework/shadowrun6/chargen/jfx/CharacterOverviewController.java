package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;

import org.prelle.javafx.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class CharacterOverviewController {
	
	private final static Logger logger = LoggerFactory.getLogger(CharacterOverviewController.class);

	private transient SR6CharacterViewLayout screen;
	
	private Page pgAttributes;

	//-------------------------------------------------------------------
	public CharacterOverviewController() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
	}

	//-------------------------------------------------------------------
	public void setComponent(SR6CharacterViewLayout screen) {
		this.screen = screen;
		screen.setController(this);
		logger.warn("TODO: set start page");
//		try {
//			if (pgAttributes==null) {
//				pgAttributes = ScreenLoader.loadAttributesPage();
//			}
//			screen.navigateTo(pgAttributes, true);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	//-------------------------------------------------------------------
	public void refresh() {
		pgAttributes.requestLayout();
	}

	//-------------------------------------------------------------------
	@FXML
	public void navigateAttributes(ActionEvent ev) throws IOException {
		logger.debug("Navigate Attributes");
//		if (pgAttributes==null) {
//			pgAttributes = ScreenLoader.loadAttributesPage();
//		}
//		screen.navigateTo(pgAttributes, true);
	}

}
