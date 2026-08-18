package de.rpgframework.shadowrun6.cards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.rpgframework.genericrpg.LicenseManager;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

public class ActionCardPrinter {


	public static void main(String[] args) throws IOException, DocumentException {
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.GERMAN);
		LicenseManager.storeGlobalLicenses(List.of("SHADOWRUN6/CORE","SHADOWRUN6/COMPANION","SHADOWRUN6/FIRING_SQUAD"));
		LicenseManager.storeUserLicensedDatasets(List.of("SHADOWRUN6/CORE"));
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();
		createPdf("Actions.pdf");
	}

	   public static void createPdf(String filename) throws IOException, DocumentException {
			Image image = Image.getInstance("src/main/resources/ChapterIntroBG_small.jpg");

	        Document document = new Document(PageSize.A4);
	        document.setMargins(20, 20, 10, 20);
	        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
	        document.open();
	        document.addTitle("Handlungskarten");
	        document.addAuthor("Stefan Prelle");
	        document.addKeywords("Shadowrun 6");
	        document.addSubject("Zauber- und Meisterschaftskarten für Splittermond - Infos zu Splittermond http://www.splittermond.de");
	        document.addCreator("Erstellt mit Genesis by Stefan Prelle und Anja Prelle");

	        PdfPTable table = new PdfPTable(5);
	        table.setWidthPercentage(100);
	        table.setSplitRows(false);
	        table.setWidths(new int[] {32,2,32,2,32});

	        List<Shadowrun6Action> list = Shadowrun6Core.getItemList(Shadowrun6Action.class);
	        int num=0;
	        for (Shadowrun6Action act : list) {
	        	System.out.println("Add "+num+" = "+act);
	        	table.addCell(getCellFromElement(act));
	        	if (num<2) table.addCell(new PdfPCell());
	        	num = (num+1)%3;
	        }
	        document.add(table);

	        document.close();

	        writer.flush();
	        writer.close();
	    }

	    private static ActionCell getCellFromElement(Shadowrun6Action action) throws BadElementException, MalformedURLException, IOException {
	    	ActionCell cell = new ActionCell(action);
	        return cell;
	    }

}
