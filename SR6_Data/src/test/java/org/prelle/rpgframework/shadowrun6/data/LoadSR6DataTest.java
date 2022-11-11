package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.OperationMode;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.shadowrun.DamageElement;
import de.rpgframework.shadowrun.DamageType;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun.items.Legality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
import de.rpgframework.shadowrun6.items.OnRoadOffRoadValue;
import de.rpgframework.shadowrun6.items.SR6AlternateUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.items.SR6UsageMode;
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
		
//		ItemTemplate riot = Shadowrun6Core.getItem(ItemTemplate.class, "riot_shield");
//		assertNotNull(riot);
//		assertEquals(1, riot.getAttacks().size());
//		
//		ItemTemplate rig = Shadowrun6Core.getItem(ItemTemplate.class, "control_rig");
//		assertNotNull(rig);
//		assertEquals(4, ((Availability)rig.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getValue());
//		assertEquals(Legality.RESTRICTED, ((Availability)rig.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getLegality());
		
		ItemTemplate mount = Shadowrun6Core.getItem(ItemTemplate.class, "weapon_mount_heavy");
		assertNotNull(mount);
		ItemAttributeDefinition def = mount.getAttribute(SR6ItemAttribute.AVAILABILITY);
		System.out.println("LoadSR6DataTest: "+def);
		System.out.println("LoadSR6DataTest: "+def.getValue());
		assertEquals(5, ((Availability)def.getValue()).getValue());
		assertEquals(Legality.FORBIDDEN, ((Availability)mount.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getLegality());
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
		
		CarriedItem carried = new CarriedItem(item, null, CarryMode.CARRIED);
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
		
//		OperationResult<CarriedItem<ItemTemplate>> item = GearTool.buildItem(temp, temp.getVariant("bodyware"), null);
//		// Quality selection missing
//		assertFalse(item.wasSuccessful());
		OperationResult<CarriedItem<ItemTemplate>> item =  GearTool.buildItem(temp, CarryMode.IMPLANTED, temp.getVariant("bodyware"), null, true, new Decision(ItemTemplate.UUID_AUGMENTATION_QUALITY, "BETA"));
		// Quality selection missing
		assertTrue(item.wasSuccessful());
		item.get().getAsObject(SR6ItemAttribute.AVAILABILITY);
		item.get().getAsValue(SR6ItemAttribute.PRICE);
		assertEquals(0.07f, item.get().getAsFloat(SR6ItemAttribute.ESSENCECOST).getModifiedValue(), 0f);
		
		System.out.println(temp.dump());
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

	//-------------------------------------------------------------------
	@Test
	public void loadVehicles() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "mitsubishi_nightsky");
		assertNotNull(item);
		
		assertEquals(259000, (Integer)item.getAttribute(SR6ItemAttribute.PRICE).getValue(), 0);
		assertEquals(18, (Integer)item.getAttribute(SR6ItemAttribute.BODY).getValue(), 0);
		assertEquals(4, ((OnRoadOffRoadValue)item.getAttribute(SR6ItemAttribute.HANDLING).getValue()).getOnRoad(), 0);
	}

	//-------------------------------------------------------------------
	@Test
	public void augmentationSimple() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "datajack");
		assertNotNull(item);
		assertFalse(item.requiresVariant());
		
		assertEquals(1000, (Integer)item.getAttribute(SR6ItemAttribute.PRICE).getValue(), 0);
		assertEquals(2, ((Availability)item.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getValue());
		assertEquals(1, item.getUsages().size());
		Usage usage = item.getUsages().get(0);
		assertEquals(CarryMode.IMPLANTED, usage.getMode());
		assertEquals(0.1f, usage.getSize(), 0);
		assertTrue(item.hasFlag(ItemTemplate.FLAG_AUGMENTATION));
	}

	//-------------------------------------------------------------------
	@Test
	public void augmentationChoices() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "skilljack");
		assertNotNull(item);
		assertFalse(item.requiresVariant());
		
		assertEquals(4, ((Availability)item.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getValue());
		ItemAttributeDefinition priceDef = item.getAttribute(SR6ItemAttribute.PRICE);
		assertNotNull(priceDef);
		assertNotNull(priceDef.getFormula());
		assertFalse(priceDef.getFormula().isResolved());
		
		assertEquals(1, item.getUsages().size());
		Usage usage = item.getUsages().get(0);
		assertEquals(CarryMode.IMPLANTED, usage.getMode());
		assertFalse(usage.getFormula().isResolved());
		assertEquals(0.0f, usage.getSize(), 0);
		assertTrue(item.hasFlag(ItemTemplate.FLAG_AUGMENTATION));
	}

//	//-------------------------------------------------------------------
//	@Test
//	public void softwareTypes() {
//		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "transys_avalon");
//		assertNotNull(item);
//		assertFalse(item.requiresVariant());		
//		
//		assertEquals(2, item.getUsages().size());
//		assertTrue("no software types set",item.getAttribute(SR6ItemAttribute.SOFTWARE_TYPES)!=null);
////		assertEquals(2, item.getAttribute(SR6ItemAttribute.SOFTWARE_TYPES).getValue());
//	}

	//-------------------------------------------------------------------
	@Test
	public void augmentationVariants() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "bone_lacing");
		assertNotNull(item);
		assertTrue(item.requiresVariant());
		// Some attributes should not be present without variant
		assertNull( item.getAttribute(SR6ItemAttribute.AVAILABILITY) );
		assertEquals(3, item.getVariants().size());
		
		SR6PieceOfGearVariant variant = item.getVariants().iterator().next();
		assertEquals("plastic",variant.getId());
		assertEquals(3, ((Availability)variant.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getValue());
		assertEquals(Legality.RESTRICTED, ((Availability)variant.getAttribute(SR6ItemAttribute.AVAILABILITY).getValue()).getLegality());
	}

	//-------------------------------------------------------------------
	@Test
	public void augmentationAlternate() {
		ItemTemplate item = Shadowrun6Core.getItem(ItemTemplate.class, "cyberskull");
		assertNotNull(item);
		assertTrue(item.requiresVariant());
		
		assertEquals(1, item.getAlternates().size());
		SR6AlternateUsage alt = item.getAlternates().get(0);
		assertEquals("cyberjaw",alt.getId());
		assertEquals(SR6UsageMode.WEAPON, alt.getUsageMode());
		assertEquals(ItemType.WEAPON_CLOSE_COMBAT, alt.getType());
		assertEquals(ItemSubType.UNARMED, alt.getSubtype());
	}

	//-------------------------------------------------------------------
	@Test
	public void testImageLink() {
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
//		System.out.println("Before filter: "+list.size()+" items");
		int b = list.size();
		list = list.stream().filter(new ItemTypeFilter(CarryMode.IMPLANTED)).collect(Collectors.toList());
//		System.out.println("After filter: "+list.size()+" items (mode IMPLANTED)");
		int a1 = list.size();
		list = list.stream().filter(new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.CYBERWARE)).collect(Collectors.toList());
//		System.out.println("After filter:2 "+list.size()+" items (mode IMPLANTED)");
		int a2 = list.size();
		assertTrue( a1 < b);
		assertTrue( a2 < a1);
		
//		for (ItemTemplate temp : list) {
//			if (temp.getUsage(CarryMode.IMPLANTED)!=null) {
//				System.out.println(temp.getId()+" : "+temp.getItemType());
//			} else {
//				SR6PieceOfGearVariant var = (SR6PieceOfGearVariant) temp.getVariant(CarryMode.IMPLANTED);
//				System.out.println(temp.getId()+"/"+var.getId()+" : "+var.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue());
//			}
//		}
		
		ItemTemplate tmp = Shadowrun6Core.getItem(ItemTemplate.class, "image_link");
		assertEquals(ItemType.ACCESSORY, tmp.getItemType());
		assertNull(tmp.getItemType(CarryMode.CARRIED));
		assertNotNull(tmp.getItemType(CarryMode.EMBEDDED));
		assertEquals(ItemType.ACCESSORY, tmp.getItemType(CarryMode.EMBEDDED));
		assertEquals(ItemType.CYBERWARE, tmp.getItemType(CarryMode.IMPLANTED));
	}

	//-------------------------------------------------------------------
	@Test
	public void testItemTemplateFilter() {
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		list = list.stream().filter(new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.CYBERWARE)).collect(Collectors.toList());
		
		Optional<ItemTemplate> result = list.stream().filter(item -> item.getId().equals("cyberarm")).findFirst();
		assertTrue("Cyberarm not found",result.isPresent());
		assertNotNull("Cyberarm not found",result.get());
		
	}

}
