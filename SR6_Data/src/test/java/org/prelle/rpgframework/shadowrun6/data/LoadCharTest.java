package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.OperationMode;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.SR6AlternateUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.items.SR6UsageMode;
import de.rpgframework.shadowrun6.items.WeaponData;

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

}
