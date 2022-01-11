package de.rpgframework.shadowrun6.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PointBuySR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class PointBuyGenTest {
	
	private Shadowrun6Character model;
	private PointBuyCharacterGenerator charGen;

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
		charGen = new PointBuyCharacterGenerator();
		charGen.setModel(model, null);
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test6WCp29() {
		assertEquals(50, model.getKarmaFree());
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		assertEquals(100, settings.characterPoints);
		
		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "human");
		assertNotNull("Metatype 'human' not found", human);
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);
		
		// Select Technomancer
		assertEquals(0, model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue());
		assertEquals(0, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue());
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "technomancer"));
		assertEquals(90, settings.characterPoints);
		assertEquals(0, model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue());
		
		/* 
		 * Attributes
		 */
		PointBuyAttributeGenerator attrib = (PointBuyAttributeGenerator) charGen.getAttributeController();
		// Invest Free Special point to raise EDGE
 		attrib.increasePoints(model.getAttribute(ShadowrunAttribute.EDGE));
		assertEquals(2, model.getAttribute(ShadowrunAttribute.EDGE).getModifiedValue());
		assertEquals(90, settings.characterPoints);

		assertTrue(attrib.increasePoints(model.getAttribute(ShadowrunAttribute.RESONANCE)).wasSuccessful());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue());
		assertTrue(attrib.increasePoints(model.getAttribute(ShadowrunAttribute.RESONANCE)).wasSuccessful());
 		assertTrue(attrib.increasePoints(model.getAttribute(ShadowrunAttribute.RESONANCE)).wasSuccessful());
 		assertTrue(attrib.increasePoints(model.getAttribute(ShadowrunAttribute.RESONANCE)).wasSuccessful());
 		assertTrue(attrib.increasePoints(model.getAttribute(ShadowrunAttribute.RESONANCE)).wasSuccessful());
		assertEquals(70, settings.characterPoints);
		assertTrue(attrib.increasePoints(model.getAttribute(ShadowrunAttribute.EDGE)).wasSuccessful());
		assertEquals(66, settings.characterPoints);
		
		/*
		 * For our attributes, we end up with Body 1, Agility 1, Reaction 4, Strength 1, 
		 * Willpower 5, Logic 6, Intuition 5, Charisma 5. This requires 20 attribute
		 * points. Since we get four for free, we only need to buy 16 attribute 
		 * points for 32 CPs. 
		 */
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.REACTION)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.REACTION)).wasSuccessful()); // 3
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.REACTION)).wasSuccessful()); // 4
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.WILLPOWER)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.WILLPOWER)).wasSuccessful()); // 3
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.WILLPOWER)).wasSuccessful()); // 4
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.WILLPOWER)).wasSuccessful()); // 5
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.LOGIC)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.LOGIC)).wasSuccessful()); // 3
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.LOGIC)).wasSuccessful()); // 4
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.LOGIC)).wasSuccessful()); // 5
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.LOGIC)).wasSuccessful()); // 6
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.INTUITION)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.INTUITION)).wasSuccessful()); // 3
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.INTUITION)).wasSuccessful()); // 4
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.INTUITION)).wasSuccessful()); // 5
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.CHARISMA)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.CHARISMA)).wasSuccessful()); // 3
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.CHARISMA)).wasSuccessful()); // 4
		assertTrue(attrib.increasePoints2(model.getAttribute(ShadowrunAttribute.CHARISMA)).wasSuccessful()); // 5
		assertEquals(34, settings.characterPoints);
		
		/*
		 *  We’ll spend 10 of our Karma to raise our Body to 2, and 25 more 
		 *  to raise our Agility up to 3.
		 */
		assertEquals(50, model.getKarmaFree());
		assertTrue(attrib.increasePoints3(model.getAttribute(ShadowrunAttribute.BODY)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints3(model.getAttribute(ShadowrunAttribute.AGILITY)).wasSuccessful()); // 2
		assertTrue(attrib.increasePoints3(model.getAttribute(ShadowrunAttribute.AGILITY)).wasSuccessful()); // 3
		assertEquals(15, model.getKarmaFree());
		assertEquals(34, settings.characterPoints);
		
		/* 
		 * Skills
		 */
		PointBuySR6SkillGenerator skill = (PointBuySR6SkillGenerator) charGen.getSkillController();
		assertEquals(12, skill.getPointsLeft());
		SR6Skill athletics = Shadowrun6Core.getItem(SR6Skill.class, "athletics");
		assertNotNull(athletics);
		assertTrue(skill.select(athletics).wasSuccessful());
		assertTrue(skill.increase(model.getSkillValue(athletics)).wasSuccessful()); // 2
		assertTrue(skill.increase(model.getSkillValue(athletics)).wasSuccessful()); // 3
		assertTrue(skill.increase(model.getSkillValue(athletics)).wasSuccessful()); // 4
		assertEquals( 8, skill.getPointsLeft());
		assertEquals(34, settings.characterPoints);
		// Cracking
		SR6Skill cracking = Shadowrun6Core.getItem(SR6Skill.class, "cracking");
		assertTrue(skill.select(cracking).wasSuccessful());
		assertTrue(skill.increase(model.getSkillValue(cracking)).wasSuccessful()); // 2
		assertTrue(skill.increase(model.getSkillValue(cracking)).wasSuccessful()); // 3
		assertTrue(skill.increase(model.getSkillValue(cracking)).wasSuccessful()); // 4
		assertTrue(skill.increase(model.getSkillValue(cracking)).wasSuccessful()); // 5
		assertTrue(skill.increase(model.getSkillValue(cracking)).wasSuccessful()); // 6
		assertEquals( 2, skill.getPointsLeft());
		assertEquals(34, settings.characterPoints);
		// Electronics
		SR6Skill electronics = Shadowrun6Core.getItem(SR6Skill.class, "electronics");
		assertTrue(skill.select(electronics).wasSuccessful());
		assertTrue(skill.increase(model.getSkillValue(electronics)).wasSuccessful()); // 2
		assertEquals(34, settings.characterPoints);
		assertTrue("Paying with autoconversion of CP doesn't work",skill.increase(model.getSkillValue(electronics)).wasSuccessful()); // 3
		assertEquals(32, settings.characterPoints);
		assertTrue(skill.increase(model.getSkillValue(electronics)).wasSuccessful()); // 4
		assertTrue(skill.increase(model.getSkillValue(electronics)).wasSuccessful()); // 5
		assertEquals(28, settings.characterPoints);
		// Perception
		SR6Skill perception = Shadowrun6Core.getItem(SR6Skill.class, "perception");
		assertNotNull(perception);
		assertTrue(skill.select(perception).wasSuccessful());
		assertTrue(skill.increase(model.getSkillValue(perception)).wasSuccessful()); // 2
		assertTrue(skill.increase(model.getSkillValue(perception)).wasSuccessful()); // 3
		assertEquals(22, settings.characterPoints);
		// Stealth
		SR6Skill stealth = Shadowrun6Core.getItem(SR6Skill.class, "stealth");
		assertTrue(skill.select(stealth).wasSuccessful());
		assertTrue(skill.increase(model.getSkillValue(stealth)).wasSuccessful()); // 2
		assertTrue(skill.increase(model.getSkillValue(stealth)).wasSuccessful()); // 3
		assertTrue(skill.increase(model.getSkillValue(stealth)).wasSuccessful()); // 4
		assertTrue(skill.increase(model.getSkillValue(stealth)).wasSuccessful()); // 5
		assertEquals(12, settings.characterPoints);
		// Tasking
		SR6Skill tasking = Shadowrun6Core.getItem(SR6Skill.class, "tasking");
		assertTrue(skill.select(tasking).wasSuccessful());
		assertTrue(skill.increase(model.getSkillValue(tasking)).wasSuccessful()); // 2
		assertTrue(skill.increase(model.getSkillValue(tasking)).wasSuccessful()); // 3
		assertTrue(skill.increase(model.getSkillValue(tasking)).wasSuccessful()); // 4
		assertTrue(skill.increase(model.getSkillValue(tasking)).wasSuccessful()); // 5
		assertEquals(2, settings.characterPoints);
	}
	
}
