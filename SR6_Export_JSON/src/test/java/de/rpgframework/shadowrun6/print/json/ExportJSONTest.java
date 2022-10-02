package de.rpgframework.shadowrun6.print.json;

import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.export.json.SR6JSONExportPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Desktop;
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
//		name = "CovertOps";
		name = "Decker";
//		name = "StreetSam";
//		name = "StreetShaman";
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
//		try {
//			Desktop.getDesktop().open(file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.exit(0);
	}


	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() throws IOException {
//		RPGFramework framework = RPGFrameworkLoader.getInstance();
//		framework.addBootStep(StandardBootSteps.FRAMEWORK_PLUGINS);
//		framework.addBootStep(StandardBootSteps.ROLEPLAYING_SYSTEMS);
//		framework.initialize(new DummyRPGFrameworkInitCallback());
//
//		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
//		plugin.init( (percent) -> {});
//		ShadowrunCore.initialize(null);
//		ConfigContainer parent = new ConfigContainerImpl(Preferences.userRoot(), "foo");
//		parent.createContainer("shadowrun6");
//		
//		
//		
//        ShadowrunCharacter character = ShadowrunCore.load(new FileInputStream("src/test/resources/testdata/Decker.xml"));
//        assertNotNull(character);
//        System.out.println("Converting "+character.getName());
//       
//        JSONSR6ExportPlugin jsonPlugin = new JSONSR6ExportPlugin();
//        jsonPlugin.attachConfigurationTree(parent);
//        // Configure to convert to original character
//        ((ConfigOption<String>)jsonPlugin.getConfiguration().get(0)).set(".");
////        jsonPlugin.OPTION_EXPORT_RESOLVED.set(false);
//       
//        CommandResult result = jsonPlugin.handleCommand(this, CommandType.PRINT, 
//        		null,
//        		character,
//        		null, // Scene
//        		null, // ScreenManager
//        		PrintType.JSON
//        		);
//        
//       assertNotNull(result);
//       assertTrue(result.wasSuccessful());
//       assertNotNull(result.getReturnValue());
//       System.out.println(result.getReturnValue());
	}

}
