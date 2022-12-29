package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class EquipmentTests {

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	@BeforeClass
	public static void setupClass() {
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();

	}

	//-------------------------------------------------------------------
	@Before
	public void setup() {
		model = new Shadowrun6Character();
	}

	//-------------------------------------------------------------------
	@Test
	public void test01() {
		ItemTemplate ares = Shadowrun6Core.getItem(ItemTemplate.class, "ares_light_fire_70");
		assertNotNull(ares);

		CarriedItem<ItemTemplate> ref = new CarriedItem<ItemTemplate>(ares, null, CarryMode.CARRIED);
		List<ItemTemplate> list = ItemUtil.getEmbeddableIn(ref, ItemHook.BARREL);
		System.out.println("#########################\nEmbeddable in BARREL of "+ares+" are:");
		for (ItemTemplate tmp : list) {
			System.out.println("  * "+tmp);
		}
		assertTrue( list.contains(Shadowrun6Core.getItem(ItemTemplate.class, "ares_lf_silencer")));
		assertTrue( list.contains(Shadowrun6Core.getItem(ItemTemplate.class, "silencer")));

		ares = Shadowrun6Core.getItem(ItemTemplate.class, "ares_alpha");
		assertNotNull(ares);

		ref = new CarriedItem<ItemTemplate>(ares, null, CarryMode.EMBEDDED);
		list = ItemUtil.getEmbeddableIn(ref, ItemHook.BARREL);
		System.out.println("#########################\nEmbeddable in BARREL of "+ares+" are:");
		for (ItemTemplate tmp : list) {
			System.out.println("  * "+tmp);
		}
		assertFalse("Requirement not honored", list.contains(Shadowrun6Core.getItem(ItemTemplate.class, "ares_lf_silencer")));
		assertTrue( list.contains(Shadowrun6Core.getItem(ItemTemplate.class, "silencer")));
	}

	//-------------------------------------------------------------------
	@Test
	public void test02() {
		ItemTemplate tempContacts = Shadowrun6Core.getItem(ItemTemplate.class, "contacts");
		ItemTemplate tempLowLV = Shadowrun6Core.getItem(ItemTemplate.class, "low_light_vision");
		ItemTemplate tempFlare = Shadowrun6Core.getItem(ItemTemplate.class, "flare_compensation");
		ItemTemplate tempImage = Shadowrun6Core.getItem(ItemTemplate.class, "image_link");

		Decision dec = new Decision(tempContacts.getChoices().get(0).getUUID(), "3");
		CarriedItem<ItemTemplate> ref = new CarriedItem<ItemTemplate>(tempContacts, null, CarryMode.CARRIED);
		SR6GearTool.recalculate("", null, ref);
		ref.addDecision(dec);
		ref.addAccessory(new CarriedItem<ItemTemplate>(tempLowLV, null, CarryMode.EMBEDDED), ItemHook.OPTICAL);
		ref.addAccessory(new CarriedItem<ItemTemplate>(tempFlare, null, CarryMode.EMBEDDED), ItemHook.OPTICAL);
		ref.addAccessory(new CarriedItem<ItemTemplate>(tempImage, null, CarryMode.EMBEDDED), ItemHook.OPTICAL);

		OperationResult<List<Modification>> mods = SR6GearTool.recalculate("", null, ref);
		assertTrue(mods.wasSuccessful());
		List<Modification> list = mods.get();
		for (Modification val : list) {
			System.out.println("  = "+val);
		}

		System.out.println("DUMP\n"+ref.dump());

		ItemAttributeNumericalValue<SR6ItemAttribute> attr = ref.getAsValue(SR6ItemAttribute.PRICE);
		assertEquals(1075, attr.getModifiedValue());
	}

	//-------------------------------------------------------------------
	/**
	 * Ensure essence is calculated when cyberware is added
	 */
	@Test
	public void test03() {
		CarriedItem<ItemTemplate> ref = GearTool.buildItem(Shadowrun6Core.getItem(ItemTemplate.class,"datajack"), CarryMode.IMPLANTED, model, true, new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).get();
//		System.out.println("DUMP1\n"+ref.dump());

		OperationResult<List<Modification>> mods = SR6GearTool.recalculate("", null, ref);
		assertTrue(mods.wasSuccessful());
		List<Modification> list = mods.get();
//		for (Modification val : list) {
//			System.out.println("  = "+val);
//		}
//
//		System.out.println("DUMP2\n"+ref.dump());

		ItemAttributeNumericalValue<SR6ItemAttribute> attr = ref.getAsValue(SR6ItemAttribute.PRICE);
		assertEquals(1000, attr.getModifiedValue());
	}

	//-------------------------------------------------------------------
	/**
	 * Make sure there are modifications to the character
	 */
	@Test
	public void testBoneLacing() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class,"bone_lacing");
		CarriedItem<ItemTemplate> ref = GearTool.buildItem(item, CarryMode.IMPLANTED, item.getVariant("titanium"), model, true,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "ALPHA")).get();

		OperationResult<List<Modification>> mods = SR6GearTool.recalculate("", null, ref);
		assertTrue(mods.wasSuccessful());
		List<Modification> list = mods.get();
		for (Modification val : list) {
			System.out.println("  = "+val);
		}

//		System.out.println("DUMP2\n"+ref.dump());

		ItemAttributeNumericalValue<SR6ItemAttribute> attr = ref.getAsValue(SR6ItemAttribute.PRICE);
		assertEquals(36000, attr.getModifiedValue());
		assertEquals(1.2f, ref.getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue(), 0.0);
		assertEquals(ItemType.CYBERWARE, ref.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue());
		assertEquals(ItemSubType.CYBER_BODYWARE, ref.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue());
		assertEquals(7, ((Availability)ref.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getValue());
		assertEquals(Legality.RESTRICTED, ((Availability)ref.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getLegality());
		assertEquals(5, ref.getCharacterModifications().size());
		assertEquals(0, ref.getModifications().size());
	}

	//-------------------------------------------------------------------
	/**
	 * Make sure there are modifications to the character
	 */
	@Test
	public void testCyberjack() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class,"cyberjack");
		CarriedItem<ItemTemplate> ref = GearTool.buildItem(item, CarryMode.IMPLANTED, null, model, true,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "cyberjack").getChoices().get(0), "6")).get();

		OperationResult<List<Modification>> mods = SR6GearTool.recalculate("", null, ref);
		assertTrue(mods.wasSuccessful());
		List<Modification> list = mods.get();
		for (Modification val : list) {
			System.out.println("U  = "+val);
		}
		for (Modification val : ref.getCharacterModifications()) {
			System.out.println("C  = "+val);
		}
		for (Modification val : ref.getModifications()) {
			System.out.println("I  = "+val);
		}

//		System.out.println("DUMP2\n"+ref.dump());

		ItemAttributeNumericalValue<SR6ItemAttribute> attr = ref.getAsValue(SR6ItemAttribute.PRICE);
		assertEquals(210000, attr.getModifiedValue());
		assertEquals(3, ref.getCharacterModifications().size());
		assertEquals(0, ref.getModifications().size());
	}

	//-------------------------------------------------------------------
	/**
	 * Make sure there are modifications to the character
	 */
	@Test
	public void testMatrixDevice() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class,"transys_avalon");
		CarriedItem<ItemTemplate> ref = GearTool.buildItem(item, CarryMode.CARRIED, null, model, true).get();

		OperationResult<List<Modification>> mods = SR6GearTool.recalculate("", null, ref);
		assertTrue(mods.wasSuccessful());
		assertTrue(ref.hasFlag("MATRIX_DEVICE"));
//		assertTrue(ref.hasFlag(SR6ItemFlag.MATRIX_DEVICE));
	}

	//-------------------------------------------------------------------
	@Test
	public void testContacts() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class,"contacts");
		CarriedItem<ItemTemplate> ref = GearTool.buildItem(item, CarryMode.CARRIED, null, model, true,
				new Decision(
						Shadowrun6Core.getItem(ItemTemplate.class, "contacts").getChoices().get(0).getUUID(),
						"3")).get();

		OperationResult<List<Modification>> mods = SR6GearTool.recalculate("", null, ref);
		assertTrue(mods.wasSuccessful());
		List<Modification> list = mods.get();
		for (Modification val : list) {
			System.out.println("  = "+val);
		}

		AvailableSlot optical = ref.getSlot(ItemHook.OPTICAL);
		assertNotNull(optical);
		assertEquals("Expect capacity 3", 3, optical.getCapacity(), 0f);
		assertEquals("Expect free capacity 3", 3, optical.getFreeCapacity(), 0f);
	}
}
