package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemAttributeValue;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class LoadDataTest {

	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() {
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		
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

}
