package de.rpgframework.shadowrun6.chargen.jfx;

import org.prelle.javafx.Page;
import org.prelle.javafx.PagePile;

import de.rpgframework.shadowrun6.chargen.jfx.page.BasicDataPage;

/**
 * @author prelle
 *
 */
public class SR6CharacterSheet extends PagePile {

	//-------------------------------------------------------------------
	public SR6CharacterSheet() {
		setChangeBackdropWithPage(false);
		initPages();
	}
	
	//-------------------------------------------------------------------
	public void initPages() {
		BasicDataPage page = new BasicDataPage();
		getPages().add(page);
		
		Page skillPage = new Page("Skills");
		getPages().add(skillPage);
	}
	
}
