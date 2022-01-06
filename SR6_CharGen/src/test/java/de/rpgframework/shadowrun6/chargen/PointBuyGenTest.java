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
import de.rpgframework.shadowrun6.chargen.gen.PointBuySkillGenerator;
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
 		attrib.increaseAdjust(ShadowrunAttribute.EDGE);
		assertEquals(2, model.getAttribute(ShadowrunAttribute.EDGE).getModifiedValue());
		assertEquals(90, settings.characterPoints);

		assertTrue(attrib.increaseAdjust(ShadowrunAttribute.RESONANCE));
		assertEquals(2, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue());
		assertTrue(attrib.increaseAdjust(ShadowrunAttribute.RESONANCE));
 		assertTrue(attrib.increaseAdjust(ShadowrunAttribute.RESONANCE));
 		assertTrue(attrib.increaseAdjust(ShadowrunAttribute.RESONANCE));
 		assertTrue(attrib.increaseAdjust(ShadowrunAttribute.RESONANCE));
		assertEquals(70, settings.characterPoints);
		assertTrue(attrib.increaseAdjust(ShadowrunAttribute.EDGE));
		assertEquals(66, settings.characterPoints);
		
		/*
		 * For our attributes, we end up with Body 1, Agility 1, Reaction 4, Strength 1, 
		 * Willpower 5, Logic 6, Intuition 5, Charisma 5. This requires 20 attribute
		 * points. Since we get four for free, we only need to buy 16 attribute 
		 * points for 32 CPs. 
		 */
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.REACTION)); // 2
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.REACTION)); // 3
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.REACTION)); // 4
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.WILLPOWER)); // 2
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.WILLPOWER)); // 3
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.WILLPOWER)); // 4
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.WILLPOWER)); // 5
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.LOGIC)); // 2
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.LOGIC)); // 3
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.LOGIC)); // 4
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.LOGIC)); // 5
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.LOGIC)); // 6
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.INTUITION)); // 2
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.INTUITION)); // 3
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.INTUITION)); // 4
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.INTUITION)); // 5
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.CHARISMA)); // 2
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.CHARISMA)); // 3
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.CHARISMA)); // 4
		assertTrue(attrib.increaseAttrib(ShadowrunAttribute.CHARISMA)); // 5
		assertEquals(34, settings.characterPoints);
		
		/*
		 *  We’ll spend 10 of our Karma to raise our Body to 2, and 25 more 
		 *  to raise our Agility up to 3.
		 */
		assertEquals(50, model.getKarmaFree());
		assertTrue(attrib.increaseKarma(ShadowrunAttribute.BODY)); // 2
		assertTrue(attrib.increaseKarma(ShadowrunAttribute.AGILITY)); // 2
		assertTrue(attrib.increaseKarma(ShadowrunAttribute.AGILITY)); // 3
		assertEquals(15, model.getKarmaFree());
		assertEquals(34, settings.characterPoints);
		
		/* 
		 * Attributes
		 */
		PointBuySkillGenerator skill = (PointBuySkillGenerator) charGen.getSkillController();
		assertNotNull(Shadowrun6Core.getItem(SR6Skill.class, "athletics"));
		assertTrue(skill.increase(model.getSkillValue(Shadowrun6Core.getItem(SR6Skill.class, "athletics"))));
	}
	
}
