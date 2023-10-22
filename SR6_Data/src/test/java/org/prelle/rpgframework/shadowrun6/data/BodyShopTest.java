package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author prelle
 *
 */
public class BodyShopTest {

	private Shadowrun6Character model;

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
	@Before
	public void beforeEach() {
		model = new Shadowrun6Character();
	}

	//-------------------------------------------------------------------
	@Test
	public void testCyberlimbCount() {
		Shadowrun6Tools.runProcessors(model, Locale.ENGLISH);
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		assertEquals(1, aVal.getModifier());

		// Left Arm 1
		ItemTemplate arm = Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm");
		SR6PieceOfGearVariant armVariant = arm.getVariant("fullarm_synthetic");
		CarriedItem<ItemTemplate> arm1L = SR6GearTool.buildItem(arm, CarryMode.IMPLANTED, armVariant, model, true,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "ALPHA")).get();
		arm1L.setCustomName("Left Arm 1");
		model.addCarriedItem(arm1L);

		// Right Arm 1
		armVariant = arm.getVariant("fullarm_obvious");
		CarriedItem<ItemTemplate> arm1R = SR6GearTool.buildItem(arm, CarryMode.IMPLANTED, armVariant, model, true,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "EXO")).get();
		arm1R.setCustomName("Right Arm 1");
		model.addCarriedItem(arm1R);

		// Left Arm 2
		armVariant = arm.getVariant("fullarm_synthetic");
		CarriedItem<ItemTemplate> arm2L = SR6GearTool.buildItem(arm, CarryMode.IMPLANTED, armVariant, model, true,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "ALPHA")).get();
		arm2L.setCustomName("Left Arm 2");
		model.addCarriedItem(arm2L);

		// Right Arm 1
		armVariant = arm.getVariant("forearm_obvious");
		CarriedItem<ItemTemplate> arm2R = SR6GearTool.buildItem(arm, CarryMode.IMPLANTED, armVariant, model, true,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "EXO")).get();
		arm2R.setCustomName("Right Arm 2");
		model.addCarriedItem(arm2R);

		Shadowrun6Tools.runProcessors(model, Locale.ENGLISH);

		List<CarriedItem<ItemTemplate>> limbs = model.getCarriedItems(item -> item.hasAutoFlag(SR6ItemFlag.CYBERLIMB));
		assertEquals("Wrong number of cyberlimbs found", 3, limbs.size());

		// Physical monitor should be +3.5
		aVal = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		System.out.println("PHYSICAL_MONITOR "+aVal);
		assertEquals(5, aVal.getModifier());
	}

	//-------------------------------------------------------------------
	@Test
	public void testCyberSingularitySeeker() {
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.WILLPOWER);
		aVal.setDistributed(1);
		testCyberlimbCount();
		aVal = model.getAttribute(ShadowrunAttribute.WILLPOWER);
		assertEquals(0, aVal.getModifier());
		assertEquals(1, aVal.getModifiedValue());

		model.addQuality(new QualityValue(Shadowrun6Core.getItem(SR6Quality.class, "cyber_singularity_seeker"), 0));
		Shadowrun6Tools.runProcessors(model, Locale.ENGLISH);
		assertEquals(1, aVal.getModifier());
		assertEquals(2, aVal.getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testRedliner() {
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.AGILITY);
		aVal.setDistributed(1);
		testCyberlimbCount();
		aVal = model.getAttribute(ShadowrunAttribute.AGILITY);
		assertEquals(0, aVal.getModifier());
		assertEquals(1, aVal.getModifiedValue());

		model.addQuality(new QualityValue(Shadowrun6Core.getItem(SR6Quality.class, "redliner"), 0));
		Shadowrun6Tools.runProcessors(model, Locale.ENGLISH);
		assertEquals(1, aVal.getModifier());
		assertEquals(2, aVal.getModifiedValue());

		List<CarriedItem<ItemTemplate>> limbs = model.getCarriedItems(item -> item.hasAutoFlag(SR6ItemFlag.CYBERLIMB));
		for (CarriedItem<?> limb : limbs) {
			assertTrue(limb.hasFlag(SR6ItemFlag.CANNOT_OVERCLOCK));
		}
	}

}
