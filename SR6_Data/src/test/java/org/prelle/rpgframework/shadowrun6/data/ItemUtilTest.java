package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

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
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class ItemUtilTest {
	
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
	public void loadSoftware() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "mct_360");
		ItemTemplate needle = Shadowrun6Core.getItem(ItemTemplate.class, "biofeedback");
		
		// New create an item
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, CarryMode.CARRIED, null, true);
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);

		List<ItemTemplate> possible = ItemUtil.getEmbeddableIn(carried, ItemHook.SOFTWARE);
		assertNotNull(possible);
		assertFalse(possible.isEmpty());
		for (ItemTemplate tmp : possible)
			System.out.println("..."+tmp);
		assertTrue(possible.contains(needle));		
	}

}
