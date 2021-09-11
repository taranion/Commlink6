package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemTemplate;
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
		OperationResult<CarriedItem<ItemTemplate>> result = GearTool.buildItem(item, new Decision(choice, "7"));
		assertTrue(result.isPresent());
		CarriedItem<ItemTemplate> carried = result.get();
		assertNotNull("CarriedItem not created",carried);
		GearTool.recalculate("", carried);
		
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getDistributed());
		assertEquals(7, carried.getAsValue(SR6ItemAttribute.RATING).getModifiedValue());
		assertEquals(170, carried.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		
		int[] expected = new int[] {4,7,2,0,0};
		assertArrayEquals(expected, carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getValue());
		assertArrayEquals(expected, carried.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());
		
	}

}
