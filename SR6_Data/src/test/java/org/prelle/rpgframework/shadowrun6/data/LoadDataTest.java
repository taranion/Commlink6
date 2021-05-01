package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.genericrpg.items.PieceOfGearUsage;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6UsageMode;
import de.rpgframework.shadowrun6.items.WeaponData;

/**
 * @author prelle
 *
 */
public class LoadDataTest {
	
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
	public void loadDataTest() {
		
		SR6Skill athlet = Shadowrun6Core.getSkill("athletics");
		assertNotNull(athlet);
		assertEquals("Athletik", athlet.getName(Locale.GERMAN));
		assertEquals("Athletics", athlet.getName(Locale.ENGLISH));
		
		ItemTemplate axe = Shadowrun6Core.getItem(ItemTemplate.class, "combat_axe");
		assertNotNull(axe);
		assertNotNull(axe.getAttribute(SR6ItemAttribute.PRICE));
//		assertEquals(500, axe.getAttribute(SR6ItemAttribute.PRICE).getModifiedValue());
		assertNotNull(axe.getAttribute(SR6ItemAttribute.DAMAGE));
		assertFalse(axe.getAttribute(SR6ItemAttribute.DAMAGE).isFormula());
		assertEquals("5P", axe.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue());
		assertNotNull(   (Damage)axe.getAttribute(SR6ItemAttribute.DAMAGE).getValue() );
		assertEquals(5,  ((Damage)axe.getAttribute(SR6ItemAttribute.DAMAGE).getValue()).getModifiedValue() );
		
		ItemTemplate bow = Shadowrun6Core.getItem(ItemTemplate.class, "bow");
		assertNotNull(bow);
	}

	//-------------------------------------------------------------------
	@Test
	public void loadSingleWeapons() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "defiance_super_shock");
		assertNotNull(item);
		
		assertEquals(1, item.getUsages().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void loadDualWeapons() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "yamaha_pulsar_2");
		assertNotNull(item);
		
		List<WeaponData> usages = item.getAttacks();
		assertNotNull(usages);
		assertEquals(2, usages.size());
		assertTrue(usages.stream().map( p -> p.getSkill()).anyMatch( sk -> (sk.getId().equals("firearms"))));
		assertTrue(usages.stream().map( p -> p.getSkill()).anyMatch( sk -> (sk.getId().equals("close_combat"))));
		
		CarriedItem carried = new CarriedItem(item, null);
		ItemAttributeObjectValue<SR6ItemAttribute> dmg = carried.getAsObject(SR6ItemAttribute.DAMAGE);
	}

}
