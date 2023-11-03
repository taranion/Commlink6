package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class CalculateCharTest {

	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
//		System.setProperty("logdir", "C:\\Users\\stefa");
		System.setProperty("logdir", "/home/prelle/commlink-logs");
		Locale.setDefault(Locale.GERMAN);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
	}

	//-------------------------------------------------------------------
	@Test
	public void closeCombatTest() throws IOException {
        Shadowrun6Character character = new Shadowrun6Character();

		ItemTemplate axe = Shadowrun6Core.getItem(ItemTemplate.class, "combat_axe");
		assertNotNull(axe);

		CarriedItem<ItemTemplate> item = SR6GearTool.buildItem(axe, CarryMode.CARRIED, character, true).get();
		assertNotNull(item);
		character.addCarriedItem(item);
        int[] ar = item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
        assertArrayEquals(new int[]{9,0,0,0,0}, ar);

        character.getAttribute(ShadowrunAttribute.STRENGTH).setDistributed(8);
        Shadowrun6Tools.runProcessors(character, Locale.getDefault());
        ar = item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
        assertArrayEquals(new int[]{17,0,0,0,0}, ar);

        Shadowrun6Tools.runProcessors(character, Locale.getDefault());
        ar = item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
        assertArrayEquals(new int[]{17,0,0,0,0}, ar);

        // Unarmed
		CarriedItem<ItemTemplate> unarmed = character.getCarriedItem(ItemTemplate.UUID_UNARMED);
		assertNotNull(unarmed);
		System.out.println("Unarmed AR = "+unarmed.getAsObject(SR6ItemAttribute.ATTACK_RATING));
        ar = unarmed.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
        assertArrayEquals(new int[]{2,0,0,0,0}, ar);
	}

	//-------------------------------------------------------------------
	@Test
	public void trollDamage() throws IOException {
        Shadowrun6Character model = new Shadowrun6Character();
        Shadowrun6Tools.runProcessors(model, Locale.getDefault());
        Shadowrun6Tools.runProcessors(model, Locale.getDefault());

		CarriedItem<ItemTemplate> unarmed = model.getCarriedItem(ItemTemplate.UUID_UNARMED);
		assertNotNull(unarmed);
		Damage dmg = unarmed.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue();
		assertNotNull(dmg);
        assertEquals(2, dmg.getValue());
        assertEquals(DamageType.STUN, dmg.getType());

        // Now make character into a troll (with dermal deposits)
        model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "troll"));
        Shadowrun6Tools.runProcessors(model, Locale.getDefault());
        Shadowrun6Tools.runProcessors(model, Locale.getDefault());
        unarmed = model.getCarriedItem(ItemTemplate.UUID_UNARMED);
		dmg = unarmed.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue();
		assertNotNull(dmg);
        assertEquals(3, dmg.getValue());
        assertEquals(DamageType.PHYSICAL, dmg.getType());

        // Add adept power critical strike
        AdeptPowerValue adept = new AdeptPowerValue(Shadowrun6Core.getItem(AdeptPower.class, "critical_strike"),2);
		model.addAdeptPower(adept);
		Shadowrun6Tools.runProcessors(model, Locale.getDefault());
        unarmed = model.getCarriedItem(ItemTemplate.UUID_UNARMED);
		dmg = unarmed.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue();
		assertNotNull(dmg);
        assertEquals(5, dmg.getValue());
        assertEquals(DamageType.PHYSICAL, dmg.getType());

	}

}
