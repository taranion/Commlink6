package de.rpgframework.shadowrun6.chargen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemUtil;

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
}
