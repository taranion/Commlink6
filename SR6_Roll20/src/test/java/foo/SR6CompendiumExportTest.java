package foo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.eden.roll20.sr6.Shadowrun6CompendiumFactory;
import de.rpgframework.genericrpg.LicenseManager;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class SR6CompendiumExportTest {

	private static Workbook workbook;

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		LicenseManager.storeUserLicensedDatasets(List.of("SHADOWRUN6/CORE"));
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();
//		Shadowrun6Core.removeDataSet(Shadowrun6Core.getDataSets().get(3));
		Shadowrun6Core.removeDataSet(Shadowrun6Core.getDataSets().get(2));
		Shadowrun6Core.removeDataSet(Shadowrun6Core.getDataSets().get(1));

		workbook = new XSSFWorkbook();
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	//-------------------------------------------------------------------
	@Test
	public void testAllUS() throws IOException {
		Function<Collection<PageReference>,Locale[]> callback = (references) -> new Locale[] {Locale.ENGLISH};

		List<DataSet> sets = Shadowrun6Core.getDataSets();
//		sets.remove(1);
		Workbook modDeep = Shadowrun6CompendiumFactory.createCompendium(null, null, sets, callback, false);
		assertNotNull(modDeep);
		File file = new File("compendium.xlsx");
		FileOutputStream fos = new FileOutputStream(file);
		modDeep.write(fos);
		fos.close();
		System.out.println("Written to "+file.getAbsolutePath());

		Desktop.getDesktop().open(file);
		//System.exit(1);
	}

//	//-------------------------------------------------------------------
//	@Test
//	public void testAllDE() throws IOException {
//		Function<Collection<PageReference>,Locale[]> callback = (references) -> new Locale[] {Locale.GERMAN};
//
//		List<DataSet> sets = Shadowrun6Core.getDataSets();
//		Module modShallow = Shadowrun6CompendiumFactory.createCompendium(null, null, sets, callback, true);
//		assertNotNull(modShallow);
//		Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
////		System.out.println(gson.toJson(modShallow));
//		Module modDeep = Shadowrun6CompendiumFactory.createCompendium(null, null, sets, callback, false);
//		assertNotNull(modDeep);
//		byte[] data = modDeep.fos.toByteArray();
//		assertNotNull(data);
//		assertTrue(data.length>0);
//		assertEquals(2,data.length);
//	}

}
