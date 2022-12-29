package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author prelle
 *
 */
public class VirtualItemTest {

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
	public void testImageLink() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "image_link");
		CarryMode carry = CarryMode.IMPLANTED;
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) item.getVariant(carry);

		ItemTemplate virtual = ItemUtil.calculateVirtualItem(item, variant, carry);
		assertNotNull(virtual);
		assertEquals(ItemType.CYBERWARE, virtual.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue());
		assertEquals(ItemSubType.VISION_ENHANCEMENT, virtual.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue());

		assertEquals(new Availability(2, false), virtual.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue());
		assertEquals(800, (int)virtual.getAttribute(SR6ItemAttribute.PRICE).getValue());
		assertEquals(0.1f, (float)virtual.getAttribute(SR6ItemAttribute.ESSENCECOST).getValue(), 0f);
		assertTrue(virtual.hasFlag(ItemTemplate.FLAG_AUGMENTATION));

		// Regular
		carry = CarryMode.EMBEDDED;
		variant = null;

		virtual = ItemUtil.calculateVirtualItem(item, variant, carry);
		assertNotNull(virtual);
		assertEquals(ItemType.ACCESSORY, virtual.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue());
		assertEquals(ItemSubType.VISION_ENHANCEMENT, virtual.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue());

		assertEquals(new Availability(1, false), virtual.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue());
		assertEquals(25, (int)virtual.getAttribute(SR6ItemAttribute.PRICE).getValue());
		assertNull(virtual.getAttribute(SR6ItemAttribute.ESSENCECOST));
		assertFalse(virtual.hasFlag(ItemTemplate.FLAG_AUGMENTATION));
	}

	//-------------------------------------------------------------------
	@Test
	public void testCyberarm() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm");
		CarryMode carry = CarryMode.IMPLANTED;
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) item.getVariant("forearm_synthetic");

		try {
			ItemUtil.calculateVirtualItem(item, variant, CarryMode.CARRIED);
			Assert.fail("Wrong carry mode not detected");
		} catch (Exception e) {
		}
		ItemTemplate virtual = ItemUtil.calculateVirtualItem(item, variant, carry);
		assertNotNull(virtual);
		assertEquals(ItemType.CYBERWARE, virtual.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue());
		assertEquals(ItemSubType.CYBER_LIMBS, virtual.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue());

		assertEquals(new Availability(3, false), virtual.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue());
		assertEquals(12000, (int)virtual.getAttribute(SR6ItemAttribute.PRICE).getValue());
		assertEquals(0.45f, (float)virtual.getAttribute(SR6ItemAttribute.ESSENCECOST).getValue(), 0f);
		assertTrue(virtual.hasFlag(ItemTemplate.FLAG_AUGMENTATION));
	}

	//-------------------------------------------------------------------
	@Test
	public void testSmugglingCompartment() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "smuggling_compartment");
		CarryMode carry = CarryMode.IMPLANTED;
		assertEquals(1, item.getPossibilities(carry).size());
		SR6PieceOfGearVariant variant = null;

		try {
			ItemUtil.calculateVirtualItem(item, variant, CarryMode.CARRIED);
			Assert.fail("Wrong carry mode not detected");
		} catch (Exception e) {
		}
		ItemTemplate virtual = ItemUtil.calculateVirtualItem(item, variant, carry);
		assertNotNull(virtual);
		assertEquals(ItemType.CYBERWARE, virtual.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue());
		assertEquals(ItemSubType.CYBER_BODYWARE, virtual.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue());

		assertEquals(new Availability(3, false), virtual.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue());
		assertEquals(7500, (int)virtual.getAttribute(SR6ItemAttribute.PRICE).getValue());
		assertEquals(0.2f, (float)virtual.getAttribute(SR6ItemAttribute.ESSENCECOST).getValue(), 0f);
		assertTrue(virtual.hasFlag(ItemTemplate.FLAG_AUGMENTATION));
	}

	//-------------------------------------------------------------------
	@Test
	public void testDerivedCapacity() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "cybertorso");
		CarryMode carry = CarryMode.IMPLANTED;
		assertEquals(2, item.getPossibilities(carry).size());
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) item.getVariant("synthetic");

		try {
			ItemUtil.calculateVirtualItem(item, variant, CarryMode.CARRIED);
			Assert.fail("Wrong carry mode not detected");
		} catch (Exception e) {
		}
		ItemTemplate virtual = ItemUtil.calculateVirtualItem(item, variant, carry);
		assertNotNull(virtual);

		assertEquals(25000, (int)virtual.getAttribute(SR6ItemAttribute.PRICE).getValue());
		assertEquals(1.5f, (float)virtual.getAttribute(SR6ItemAttribute.ESSENCECOST).getValue(), 0f);
		assertEquals(5, (int)virtual.getAttribute(SR6ItemAttribute.CAPACITY).getValue());
	}

}
