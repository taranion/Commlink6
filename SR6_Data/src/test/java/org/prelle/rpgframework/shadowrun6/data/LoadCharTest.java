package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class LoadCharTest {

	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
//		System.setProperty("logdir", "C:\\Users\\stefa");
		System.setProperty("logdir", "/home/prelle/commlink-logs");
		Locale.setDefault(Locale.GERMAN);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
	}

	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() throws IOException {
		String name = "Impaired";
		FileInputStream fis = new FileInputStream("src/test/resources/testdata/"+ name + ".xml");
		byte[] data = fis.readAllBytes();
        Shadowrun6Character character = Shadowrun6Core.decode(data);

        Shadowrun6Tools.resolveChar(character);
        assertTrue("Lost IMPAIRED",character.hasQuality("impaired"));

        System.out.println("\nNow process----");
        assertTrue("Lost IMPAIRED",character.hasQuality("impaired"));
        Shadowrun6Tools.runProcessors(character, Locale.getDefault());
        assertTrue("Lost IMPAIRED",character.hasQuality("impaired"));

        character.getQualities().forEach(System.out::println);
	}

	//-------------------------------------------------------------------
	@Test
	public void loadDrake() throws IOException {
		String name = "Draconius";
		FileInputStream fis = new FileInputStream("src/test/resources/testdata/"+ name + ".xml");
		byte[] data = fis.readAllBytes();
        Shadowrun6Character character = Shadowrun6Core.decode(data);

        Shadowrun6Tools.resolveChar(character);
        Shadowrun6Tools.runProcessors(character, Locale.getDefault());

        assertFalse(character.getBodyForms().isEmpty());

        for (BodyForm form : character.getBodyForms()) {
        	System.out.println("## "+form.getType());
			form.getMovements().forEach(System.out::println);
			form.getQualities().forEach(System.out::println);
			form.getCritterPowers().forEach(System.out::println);
        }
	}

	//-------------------------------------------------------------------
	@Test
	public void loadStreetSam() throws IOException {
		String name = "Takeda";
		FileInputStream fis = new FileInputStream("src/test/resources/testdata/"+ name + ".xml");
		byte[] data = fis.readAllBytes();
        Shadowrun6Character character = Shadowrun6Core.decode(data);

        Shadowrun6Tools.resolveChar(character);
        Shadowrun6Tools.runProcessors(character, Locale.getDefault());

        for (CarriedItem<ItemTemplate> item : character.getCarriedItems()) {
			System.out.println("## "+item+" \t= "+item.getOutgoingModifications());
		}
	}

}
