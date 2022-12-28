package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ProblemItemTest {
	
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
	public void loadBow() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "bow");
		assertNotNull(item);
		assertEquals(1, item.getChoices().size());
		Choice choice = item.getChoices().get(0);
		assertNotNull(choice);
		
		assertEquals(ShadowrunReference.ITEM_ATTRIBUTE,choice.getChooseFrom());
		assertEquals("RATING",choice.getTypeReference());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.CARRIED, null, true, new Decision(choice, "7"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
		SR6GearTool.recalculate("", null, carried);
		
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getDistributed());
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getModifiedValue());
		assertEquals(170, carried.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		
		int[] expected = new int[] {4,7,2,0,0};
		assertArrayEquals(expected, carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getValue());
		assertArrayEquals(expected, carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());		
	}

	//-------------------------------------------------------------------
	/** Archetype for a weapon with accessories included in the stats */
	@Test
	public void loadAresLightFire() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "ares_light_fire_70");
		assertNotNull(item);
		assertEquals(0, item.getChoices().size());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.CARRIED, null, true);
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
//		GearTool.recalculate("", carried);
//		
//		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getDistributed());
//		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getModifiedValue());
		assertEquals(350, carried.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		
		int[] expected = new int[] {10,7,6,0,0};
		assertArrayEquals(expected, carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getValue());
		assertArrayEquals(expected, carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());	
	}

	//-------------------------------------------------------------------
	@Test
	public void loadItemWithRatingMultiplied() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "chemical_protection");
		assertNotNull(item);
		assertEquals(1, item.getChoices().size());
		Choice choice = item.getChoices().get(0);
		assertNotNull(choice);
		
		assertEquals(ShadowrunReference.ITEM_ATTRIBUTE,choice.getChooseFrom());
		assertEquals("RATING",choice.getTypeReference());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.EMBEDDED, null, true, new Decision(choice, "7"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
		SR6GearTool.recalculate("", null, carried);
		
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getDistributed());
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getModifiedValue());
		assertEquals(1750, carried.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.SIZE).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void loadItemWithTables() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "cyberjack");
		assertNotNull(item);
		assertEquals(2, item.getChoices().size());
		Choice choice = item.getChoices().get(0);
		assertNotNull(choice);
		
		assertEquals(ShadowrunReference.ITEM_ATTRIBUTE,choice.getChooseFrom());
		assertEquals("RATING",choice.getTypeReference());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.IMPLANTED, null, true, new Decision(choice, "4"), new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
		SR6GearTool.recalculate("", null, carried);
		
		assertEquals(4, carried.getAsValue(SR6ItemAttribute.RATING).getModifiedValue());
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue());
		assertEquals(6, carried.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue());
		assertEquals(95000, carried.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertEquals(new Availability(4, Legality.RESTRICTED, false), carried.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue());

	}

	//-------------------------------------------------------------------
	@Test
	public void loadImplantedWithEssence() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "datalock");
		assertNotNull(item);
		assertEquals(2, item.getChoices().size());

		Choice choice = item.getChoices().get(0);
		assertNotNull(choice);
		assertEquals(ShadowrunReference.ITEM_ATTRIBUTE,choice.getChooseFrom());
		assertEquals("RATING",choice.getTypeReference());

		Choice choice2 = item.getChoices().get(1);
		assertNotNull(choice2);
		assertEquals(ShadowrunReference.AUGMENTATION_QUALITY,choice2.getChooseFrom());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.IMPLANTED, null, true, new Decision(choice, "4"), new Decision(choice2, "BETA"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
		
		assertEquals(4, carried.getAsValue(SR6ItemAttribute.RATING).getModifiedValue());
		assertEquals(AugmentationQuality.BETA, carried.getAsObject(SR6ItemAttribute.QUALITY).getModifiedValue());
		assertEquals(0.07f, carried.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue(), 0.0);
	}

	//-------------------------------------------------------------------
	@Test
	public void loadAmmunition() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "ammo_holdout_light_machine");

		Choice choice = item.getChoices().get(0);
		assertNotNull(choice);
		assertEquals(ShadowrunReference.AMMUNITION_TYPE,choice.getChooseFrom());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.CARRIED, null, true, new Decision(choice, "apds"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
		
		Damage expect = new Damage();
		expect.setValue(-1);
		assertEquals(expect.getValue(), ((Damage)carried.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getValue());
		int[] modAR = (int[])carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
		int[] expAR = new int[] {2,2,2,2,2};		
		assertTrue("Expected "+Arrays.toString(expAR)+" but got "+Arrays.toString(modAR),Arrays.equals(expAR, expAR));
		
//		assertEquals(-1, carried.getAsValue(SR6ItemAttribute.DAMAGE).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void loadArmorWithComplexChoices() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "av_rapid_transit");

		Choice choice = item.getChoice(UUID.fromString("dbb18bb4-89a4-4535-bede-3077ee600bc1"));
		assertNotNull(choice);
		assertEquals(ShadowrunReference.GEAR,choice.getChooseFrom());
		assertNotNull(choice.getChoiceOptions());
		assertEquals(3,choice.getChoiceOptions().length);

		Choice choice2 = item.getChoice(UUID.fromString("dbb18bb4-89a4-4435-bede-3077ee600bc5"));
		assertNotNull(choice2);
		assertEquals(ShadowrunReference.SUBSELECT,choice2.getChooseFrom());
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.CARRIED, null, true, 
				new Decision(choice, "fire_resistance"),
				new Decision(choice2, "platinum"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
	}

}
