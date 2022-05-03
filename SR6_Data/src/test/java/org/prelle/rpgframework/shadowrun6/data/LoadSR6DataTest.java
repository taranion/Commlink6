package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.OperationMode;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.WeaponData;

/**
 * @author prelle
 *
 */
public class LoadSR6DataTest {
	
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
	public void loadDataTest() {
		
		SR6Skill athlet = Shadowrun6Core.getSkill("athletics");
		assertNotNull(athlet);
//		assertEquals("Athletik", athlet.getName(Locale.GERMAN));
//		assertEquals("Athletics", athlet.getName(Locale.ENGLISH));
//		
//		ItemTemplate axe = Shadowrun6Core.getItem(ItemTemplate.class, "combat_axe");
//		assertNotNull(axe);
//		assertNotNull(axe.getAttribute(SR6ItemAttribute.PRICE));
////		assertEquals(500, axe.getAttribute(SR6ItemAttribute.PRICE).getModifiedValue());
//		assertNotNull(axe.getAttribute(SR6ItemAttribute.DAMAGE));
//		assertTrue(axe.getAttribute(SR6ItemAttribute.DAMAGE).getFormula().isResolved());
//		assertEquals("5P", axe.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue());
//		assertNotNull("Formula missing" ,   axe.getAttribute(SR6ItemAttribute.DAMAGE).getFormula());
//		System.out.println("loadDataTest: "+axe.getAttribute(SR6ItemAttribute.DAMAGE).getFormula());
//		assertTrue(   axe.getAttribute(SR6ItemAttribute.DAMAGE).getFormula().isResolved() );
//		assertNotNull(   (Damage)axe.getAttribute(SR6ItemAttribute.DAMAGE).getValue() );
//		assertEquals(5,  ((Damage)axe.getAttribute(SR6ItemAttribute.DAMAGE).getValue()).getModifiedValue() );
//		
//		ItemTemplate bow = Shadowrun6Core.getItem(ItemTemplate.class, "bow");
//		assertNotNull(bow);
		
		ItemTemplate riot = Shadowrun6Core.getItem(ItemTemplate.class, "riot_shield");
		assertNotNull(riot);
		assertEquals(1, riot.getAttacks().size());
	}

	//-------------------------------------------------------------------
//	@Test
	public void loadSingleWeapons() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "defiance_super_shock");
		assertNotNull(item);
		
		ItemAttributeDefinition def = item.getAttribute(SR6ItemAttribute.DAMAGE);
		assertNotNull(def);
		Damage dmg = def.getValue();
		assertNotNull(def);
		assertEquals(6,dmg.getModifiedValue());
		assertEquals(DamageType.STUN,dmg.getType());
		assertEquals(DamageElement.ELECTRICITY,dmg.getElement());
		
		def = item.getAttribute(SR6ItemAttribute.ATTACK_RATING);
		assertNotNull(def);
		assertTrue(int[].class.isAssignableFrom(def.getValue().getClass()));
		assertArrayEquals(new int[]{10,6,0,0,0}, def.getValue());
		
		assertNotNull(item.getAttribute(SR6ItemAttribute.FIREMODES));
		assertTrue ( ((List<FireMode>)item.getAttribute(SR6ItemAttribute.FIREMODES).getValue()).contains(FireMode.SINGLE_SHOT));
		assertFalse( ((List<FireMode>)item.getAttribute(SR6ItemAttribute.FIREMODES).getValue()).contains(FireMode.BURST_FIRE));
		assertEquals(340, (Integer)item.getAttribute(SR6ItemAttribute.PRICE).getValue(), 0);
		assertEquals(1, ((Availability)item.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getValue());
		assertEquals(Legality.LEGAL, ((Availability)item.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getLegality());
		assertEquals(Shadowrun6Core.getSkill("firearms"), (SR6Skill)item.getAttribute(SR6ItemAttribute.SKILL).getValue());
		assertEquals(Shadowrun6Core.getSkill("firearms").getSpecialization("pistols"), (SkillSpecialization)item.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION).getValue());
		
		assertEquals(1, item.getAlternates().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void detectModes() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "laser_sight");
		assertNotNull(item);
		
		List<OperationMode> modes = item.getOperationModes();
		assertNotNull(modes);
		assertEquals(2, modes.size());
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


	//-------------------------------------------------------------------
	@Test
	public void loadWithIntegerAvailability() {
		ItemTemplate temp = Shadowrun6Core.getItem(ItemTemplate.class, "image_link");
		ItemAttributeDefinition attrDef = temp.getAttribute(SR6ItemAttribute.AVAILABILITY);
		assertEquals(1, ((Availability)attrDef.getValue()).getValue());
		attrDef = temp.getAttribute(SR6ItemAttribute.PRICE);
		assertEquals(25, attrDef.getFormula().getAsInteger());
		assertEquals(25, attrDef.getDistributed());
		
		OperationResult<CarriedItem<ItemTemplate>> item = GearTool.buildItem(temp, temp.getVariant("bodyware"));
		assertTrue(item.wasSuccessful());
		item.get().getAsObject(SR6ItemAttribute.AVAILABILITY);
		item.get().getAsValue(SR6ItemAttribute.PRICE);
	}
	
	//-------------------------------------------------------------------
	public void exportSkillSpecializations() {
		StringBuffer buf = new StringBuffer();
		StringBuffer de = new StringBuffer();
		List<SR6Skill> skills = Shadowrun6Core.getItemList(SR6Skill.class);
		Collections.sort(skills, new Comparator<SR6Skill>() {

			@Override
			public int compare(SR6Skill arg0, SR6Skill arg1) {
				// TODO Auto-generated method stub
				return arg0.getId().compareTo(arg1.getId());
			}
		});
		for (SR6Skill skill : skills) {
			buf.append("   \""+skill.getId()+"\": {\n");
			de.append("   \""+skill.getId()+"\": {\n");
			for (SkillSpecialization spec : skill.getSpecializations()) {
				buf.append("      \""+spec.getId()+"\": \"shadowrun6.special."+skill.getId()+"."+spec.getId()+"\",\n");
				de.append("      \""+spec.getId()+"\": \""+spec.getName(Locale.ENGLISH)+"\",\n");
			}
			buf.append("   },\n");
			//
			de.append("   },\n");
		}
		System.out.println(buf.toString());
		System.out.println(de.toString());
	}

}
