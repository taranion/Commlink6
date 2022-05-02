package de.rpgframework.shadowrun6.chargen;

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
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
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
		
		CarriedItem<ItemTemplate> ref = new CarriedItem<ItemTemplate>(ares, null);
		List<ItemTemplate> list = ItemUtil.getEmbeddableIn(ref, ItemHook.BARREL);
		System.out.println("#########################\nEmbeddable in BARREL of "+ares+" are:");
		for (ItemTemplate tmp : list) {
			System.out.println("  * "+tmp);
		}
		assertTrue( list.contains(Shadowrun6Core.getItem(ItemTemplate.class, "ares_lf_silencer")));
		assertTrue( list.contains(Shadowrun6Core.getItem(ItemTemplate.class, "silencer")));

		ares = Shadowrun6Core.getItem(ItemTemplate.class, "ares_alpha");
		assertNotNull(ares);
		
		ref = new CarriedItem<ItemTemplate>(ares, null);
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
		CarriedItem<ItemTemplate> ref = new CarriedItem<ItemTemplate>(tempContacts, null);
		ref.addDecision(dec);
		ref.addAccessory(new CarriedItem<ItemTemplate>(tempLowLV, null), ItemHook.OPTICAL);
		ref.addAccessory(new CarriedItem<ItemTemplate>(tempFlare, null), ItemHook.OPTICAL);
		ref.addAccessory(new CarriedItem<ItemTemplate>(tempImage, null), ItemHook.OPTICAL);
		
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
}
