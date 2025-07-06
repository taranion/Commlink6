package de.rpgframework.shadowrun6.cards;

import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;

import de.rpgframework.shadowrun6.Shadowrun6Action;

/**
 * @author prelle
 *
 */
public class ActionCell extends PdfPCell {

	//-------------------------------------------------------------------
	public ActionCell(Shadowrun6Action action) throws BadElementException, MalformedURLException, IOException {
		super(Image.getInstance("Test.jpg"),true);
		Phrase when = new Phrase(action.getTime().getNameLong());
		Phrase name = new Phrase(action.getName());
//		addElement(when);
//		addElement(name);
		// TODO Auto-generated constructor stub
	}

}
