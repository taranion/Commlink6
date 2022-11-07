package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author prelle
 *
 */
public class PersonaTest {
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
//		System.setProperty("logdir", "C:\\Users\\stefa");
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );		
	}

	//-------------------------------------------------------------------
	@Test
	public void testProgram() {
		ItemTemplate prog1 = Shadowrun6Core.getItem(ItemTemplate.class, "armor");
		CarriedItem<ItemTemplate> prog1CI = SR6GearTool.buildItem(prog1, CarryMode.EMBEDDED, null, true).get();
		System.out.println("testProgram: operationModeOptions="+prog1CI.getOperationModes(true));
		assertNotNull(prog1CI.getModifications());
		assertTrue(prog1CI.getModifications().isEmpty());
		assertNotNull(prog1CI.getCharacterModifications());
		assertTrue("Mode not activated yet - should not have modifications",prog1CI.getCharacterModifications().isEmpty());
		
		prog1CI.setMode(prog1CI.getOperationModes(false).get(0).getModes().get(0), true);
		SR6GearTool.recalculate("", null, prog1CI);
		assertNotNull(prog1CI.getCharacterModifications());
		assertFalse("Mode activated now- should have modifications",prog1CI.getCharacterModifications().isEmpty());
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testPersona() {
		ItemTemplate prog1 = Shadowrun6Core.getItem(ItemTemplate.class, "armor");
		CarriedItem<ItemTemplate> prog1CI = SR6GearTool.buildItem(prog1, CarryMode.EMBEDDED, null, true).get();

		ItemTemplate deck = Shadowrun6Core.getItem(ItemTemplate.class, "shiawase_cyber6");
		CarriedItem<ItemTemplate> deckCI = SR6GearTool.buildItem(deck, CarryMode.CARRIED, null, true).get();
		deckCI.addFlag(SR6ItemFlag.PRIMARY);
		ItemTemplate jack = Shadowrun6Core.getItem(ItemTemplate.class, "cyberjack");
		CarriedItem<ItemTemplate> jackCI = SR6GearTool.buildItem(jack, CarryMode.IMPLANTED, null, true, new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"), new Decision(UUID.fromString("66b37416-6996-48b8-87f0-17b28dc44df1"), "6")).get();
		jackCI.addFlag(SR6ItemFlag.PRIMARY);
		deckCI.addAccessory(prog1CI, ItemHook.SOFTWARE);
		SR6GearTool.recalculate("", null, deckCI);
		
		Shadowrun6Character model = new Shadowrun6Character();
		model.addCarriedItem(jackCI);
		model.addCarriedItem(deckCI);
		
		
		System.out.println("testPersona: runProcessor----------------------");
		Shadowrun6Tools.runProcessors(model);
		
		assertNotNull(model.getPersona());
		assertEquals(8, model.getPersona().getAttack().getModifiedValue());
		assertEquals(7, model.getPersona().getSleaze().getModifiedValue());
		assertEquals(9, model.getPersona().getDataProcessing().getModifiedValue());
		assertEquals(8, model.getPersona().getFirewall().getModifiedValue());
		
		// Reorganize matrix attributes
		model.getMatrixAttribMap().setAttack(SR6ItemAttribute.DATA_PROCESSING);
		model.getMatrixAttribMap().setDataProcessing(SR6ItemAttribute.ATTACK);
		Shadowrun6Tools.runProcessors(model);
		assertEquals(9, model.getPersona().getAttack().getModifiedValue());
		assertEquals(7, model.getPersona().getSleaze().getModifiedValue());
		assertEquals(8, model.getPersona().getDataProcessing().getModifiedValue());
		assertEquals(8, model.getPersona().getFirewall().getModifiedValue());
	}

}
