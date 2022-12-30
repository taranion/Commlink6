package de.rpgframework.shadowrun6.export.json;

import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

/**
 * @author prelle
 *
 */
public class ExportJSONTest {

	@BeforeClass
	public static void beforeClass() {
		//System.setProperty("logdir", "C:\\Users\\anja");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
	}

	//-------------------------------------------------------------------
	/**
	 * @throws IOException
	 */
	@Test
	public void printTest() throws IOException {
		SR6JSONExportPlugin plugin = new SR6JSONExportPlugin();


		String name = "Adept";
//		name = "CombatMage";
		name = "CovertOps";
		name = "Decker";
		name = "StreetSam";
		name = "StreetShaman";
		FileInputStream fis = new FileInputStream("src/test/resources/testdata/"+ name + ".xml");
		byte[] data = fis.readAllBytes();
		Shadowrun6Character character = Shadowrun6Core.decode(data);
		System.out.println("Loaded "+character.getName());
		Shadowrun6Tools.resolveChar(character);
		Shadowrun6Tools.runProcessors(character);

		byte[] pdfData = plugin.createExport(character);
		System.out.println("Exported JSON with "+pdfData.length+" bytes");

		File file = new File(name+".xml");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(pdfData);
		fos.flush();
		fos.close();
		System.out.println(Files.readString(file.toPath()));
	}

}
