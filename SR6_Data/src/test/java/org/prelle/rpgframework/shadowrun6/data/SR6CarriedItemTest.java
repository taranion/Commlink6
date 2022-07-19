package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author prelle
 *
 */
public class SR6CarriedItemTest {
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );		
	}

	//-------------------------------------------------------------------
	/**
	 * 	<item id="combat_axe" avail="4" price="500" type="WEAPON_CLOSE_COMBAT" subtype="BLADES">
	 *	  <equip mode="NORMAL"/>
	 *	  <weapon dmg="5P" attack="9,,,," skill="close_combat" spec="close_combat/blades" />
	 *  </item>
	 */
	@Test
	public void testSimpleItem() {
		ItemTemplate axe = Shadowrun6Core.getItem(ItemTemplate.class, "combat_axe");
		assertNotNull(axe);
		
		CarriedItem<ItemTemplate> item = new CarriedItem<ItemTemplate>(axe, null, CarryMode.CARRIED);
		SR6GearTool.recalculate("", null, item);
		assertNotNull(item);
		assertNotNull(item.getAsObject(SR6ItemAttribute.AVAILABILITY));
		assertEquals(4, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getValue());
		assertEquals(Legality.LEGAL, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getLegality());
		assertNotNull(item.getAsValue(SR6ItemAttribute.PRICE));
		assertEquals(500, item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.DAMAGE));
		assertEquals(5, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getValue());
		assertEquals(DamageType.PHYSICAL, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getType());
		assertNotNull(item.getAsObject(SR6ItemAttribute.ATTACK_RATING));
		assertArrayEquals(new int[]{9,0,0,0,0}, (int[])item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.SKILL));
		assertEquals(Shadowrun6Core.getSkill("close_combat"), (SR6Skill)item.getAsObject(SR6ItemAttribute.SKILL).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION));
		assertEquals(Shadowrun6Core.getSkill("close_combat").getSpecialization("blades"), (SkillSpecialization<SR6Skill>)item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION).getModifiedValue());
	}

	//-------------------------------------------------------------------
	/**
	 * <item id="bow" price="100" type="WEAPON_RANGED" subtype="BOWS">
	 * 	<choices>
	 * 		<choice uuid="adeb159c-6ca3-407b-8641-c76f9b29a49c" type="ITEM_ATTRIBUTE" ref="RATING" options="1,2,3,4,5,6,7,8,9,10,11,12,13,14"/> 
	 * 	</choices>
	 * 	<requires>
	 * 		<valuereq type="ATTRIBUTE" ref="STRENGTH" min="$RATING"/>
	 * 	</requires>
     *   <modifications>
     *      <itemmod type="SLOT" ref="TOP"/>
     *      <itemmod type="SLOT" ref="UNDER"/>
     *   </modifications>
	 * 	<attrdef id="PRICE"         value="$RATING*10 +100" />
	 * 	<attrdef id="DAMAGE"        value="$RATING/2 P" />
	 * 	<attrdef id="ATTACK_RATING" value="$RATING/2,$RATING,$RATING/4,," />
	 * 	<attrdef id="AVAILABILITY"  value="$RATING/3 L" />
	 * 	<weapon skill="athletics" spec="athletics/archery" />
	 * </item>
	 */
	@Test
	public void testItemWithRating() {
		ItemTemplate temp = Shadowrun6Core.getItem(ItemTemplate.class, "bow");
		assertNotNull(temp);
		
		CarriedItem<ItemTemplate> item = new CarriedItem<ItemTemplate>(temp, null, CarryMode.CARRIED);
		Decision decision = new Decision(UUID.fromString("adeb159c-6ca3-407b-8641-c76f9b29a49c"), "9");
		item.setDecisions(List.of(decision));
		SR6GearTool.recalculate("", null, item);
		
		assertNotNull(item.getAsObject(SR6ItemAttribute.AVAILABILITY));
		assertEquals(3, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getValue());
		assertEquals(Legality.RESTRICTED, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getLegality());
		assertNotNull("No PRICE set",item.getAsValue(SR6ItemAttribute.PRICE));
		assertEquals(190, item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.DAMAGE));
		assertEquals(5, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getValue());
		assertEquals(DamageType.PHYSICAL, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getType());
		assertNotNull(item.getAsObject(SR6ItemAttribute.ATTACK_RATING));
		assertArrayEquals(new int[]{5,9,2,0,0}, (int[])item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.SKILL));
		assertEquals(Shadowrun6Core.getSkill("athletics"), (SR6Skill)item.getAsObject(SR6ItemAttribute.SKILL).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION));
		assertEquals(Shadowrun6Core.getSkill("athletics").getSpecialization("archery"), (SkillSpecialization<SR6Skill>)item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION).getModifiedValue());
	}

	//-------------------------------------------------------------------
	/**
     * <item id="defiance_super_shock" avail="1" price="340" type="WEAPON_FIREARMS" subtype="TASERS">
     *    <modifications>
     *       <itemmod type="HOOK" ref="TOP"/>
     *    </modifications>
     *    <weapon dmg="6S(e)" attack="10,6,,," mode="SS" ammo="4(m)" skill="firearms" spec="firearms/pistols"/>
     * </item>
	 */
	@Test
	public void testItemWithSlots() {
		ItemTemplate temp = Shadowrun6Core.getItem(ItemTemplate.class, "defiance_super_shock");
		assertNotNull(temp);
		
		CarriedItem<ItemTemplate> item = new CarriedItem<ItemTemplate>(temp, null, CarryMode.CARRIED);
		SR6GearTool.recalculate("", null, item);
		assertNotNull(item);
		assertNotNull(item.getAsObject(SR6ItemAttribute.AVAILABILITY));
		assertEquals(1, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getValue());
		assertEquals(Legality.LEGAL, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getLegality());
		assertNotNull(item.getAsValue(SR6ItemAttribute.PRICE));
		assertEquals(340, item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.DAMAGE));
		assertEquals(6, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getValue());
		assertEquals(DamageType.STUN, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getType());
		assertEquals(DamageElement.ELECTRICITY, ((Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue()).getElement());
		assertNotNull(item.getAsObject(SR6ItemAttribute.ATTACK_RATING));
		assertArrayEquals(new int[]{10,6,0,0,0}, (int[])item.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.SKILL));
		assertEquals(Shadowrun6Core.getSkill("firearms"), (SR6Skill)item.getAsObject(SR6ItemAttribute.SKILL).getModifiedValue());
		assertNotNull(item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION));
		assertEquals(Shadowrun6Core.getSkill("firearms").getSpecialization("tasers"), (SkillSpecialization<SR6Skill>)item.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION).getModifiedValue());
		
		//item.get
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testCapacityFromSlots() {
		ItemTemplate temp = Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm");
		assertNotNull(temp);
		SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) temp.getVariant("forearm_obvious");
		
		OperationResult<CarriedItem<ItemTemplate>> res = SR6GearTool.buildItem(temp, CarryMode.IMPLANTED, variant, null, false, new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")); 
		assertNotNull(res);
		CarriedItem<ItemTemplate> item = res.get();
		assertNotNull(item);
		assertNotNull(item.getAsObject(SR6ItemAttribute.AVAILABILITY));
		assertEquals(3, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getValue());
		assertEquals(Legality.LEGAL, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getLegality());
		assertNotNull(item.getAsValue(SR6ItemAttribute.PRICE));
		assertEquals(10000, item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertNotNull("CAPACITY not calculated",item.getAsValue(SR6ItemAttribute.CAPACITY));
		assertEquals(10, item.getAsValue(SR6ItemAttribute.CAPACITY).getModifiedValue());
	}

	//-------------------------------------------------------------------
	/**
	 * 	<item id="combat_axe" avail="4" price="500" type="WEAPON_CLOSE_COMBAT" subtype="BLADES">
	 *	  <equip mode="NORMAL"/>
	 *	  <weapon dmg="5P" attack="9,,,," skill="close_combat" spec="close_combat/blades" />
	 *  </item>
	 */
	@Test
	public void testVehicle() {
		ItemTemplate honda = Shadowrun6Core.getItem(ItemTemplate.class, "honda_spirit");
		assertNotNull(honda);
		ItemAttributeDefinition def = honda.getAttribute(SR6ItemAttribute.BODY);
		assertNotNull(def);
		assertTrue("BODY should be an integer type",def.isInteger());
		
		CarriedItem<ItemTemplate> item = GearTool.buildItem(honda, CarryMode.CARRIED, null, true).get();
		assertNotNull(item);
		assertNotNull(item.getAsObject(SR6ItemAttribute.AVAILABILITY));
		assertEquals(2, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getValue());
		assertEquals(ItemType.VEHICLES, item.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue());
		assertEquals(ItemSubType.CARS, item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue());
		assertEquals(Legality.LEGAL, ((Availability)item.getAsObject(SR6ItemAttribute.AVAILABILITY).getModifiedValue()).getLegality());
		assertNotNull(item.getAsValue(SR6ItemAttribute.PRICE));
		assertEquals(13000, item.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertEquals(10, item.getAsValue(SR6ItemAttribute.BODY).getModifiedValue());
	}
	
	@Test
	public void testWithoutChoices() {
		ItemTemplate honda = Shadowrun6Core.getItem(ItemTemplate.class, "dermal_plating");
		assertNotNull(honda);
		
		CarriedItem<ItemTemplate> item = SR6GearTool.buildItem(honda, CarryMode.IMPLANTED, null, false).get();
		assertNotNull(item);
		assertNotNull(item.getAsObject(SR6ItemAttribute.AVAILABILITY));
		assertNotNull(item.getAsValue(SR6ItemAttribute.PRICE));
	}
}
