package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.character.CharacterIOException;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.ISkillController;
import de.rpgframework.shadowrun.chargen.charctrl.ISpellController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.gen.karma.KarmaCharacterGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class KarmaGenTest {

	private Shadowrun6Character model;
	private KarmaCharacterGenerator charGen;

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
		charGen = new KarmaCharacterGenerator();
		charGen.setModel(model, null);
	}

	//-------------------------------------------------------------------
	private int increaseAttrib(ShadowrunAttribute key, int target) {
		int cost = 0;
		IAttributeController attrib = charGen.getAttributeController();
		AttributeValue<ShadowrunAttribute> val = charGen.getModel().getAttribute(key);
		while (val.getModifiedValue(ValueType.NATURAL)<target) {
			int oldKarma = charGen.getModel().getKarmaFree();
			Possible poss = attrib.canBeIncreased(val);
			assertTrue(poss.toString(), poss.get());
			OperationResult<AttributeValue<ShadowrunAttribute>> res = attrib.increase(val);
			assertTrue(res.getError(),res.wasSuccessful());
			val = res.get();
			int newKarma = charGen.getModel().getKarmaFree();
			if (oldKarma==newKarma)
				fail("Raising attribute to "+val.getDistributed()+" did not cost anything");
			cost+= (oldKarma-newKarma);
		}
		return cost;
	}

	//-------------------------------------------------------------------
	private int increaseSkill(String skillID, int target) {
		SR6Skill skill = Shadowrun6Core.getSkill(skillID);
		assertNotNull(skill);
		int cost = 0;
		ISkillController ctrl = charGen.getSkillController();
		SR6SkillValue val = charGen.getModel().getSkillValue(skill);
		if (val==null) {
			OperationResult<SR6SkillValue> res = ctrl.select(skill);
			assertTrue("Failed selecting "+skillID+":" +res.getError(),res.wasSuccessful());
			val = res.get();
			cost=5;
		}
		while (val.getModifiedValue(ValueType.NATURAL)<target) {
			int oldKarma = charGen.getModel().getKarmaFree();
			Possible poss = ctrl.canBeIncreased(val);
			assertTrue(poss.toString(), poss.get());
			OperationResult<SR6SkillValue> res = ctrl.increase(val);
			assertTrue(res.getError(),res.wasSuccessful());
			val = res.get();
			int newKarma = charGen.getModel().getKarmaFree();
			if (oldKarma==newKarma)
				fail("Raising skill to "+val.getDistributed()+" did not cost anything");
			cost+= (oldKarma-newKarma);
		}
		return cost;
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testKomp156() throws CharacterIOException {
		charGen.runProcessors();
		assertEquals(1000, model.getKarmaFree());
//		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
//		assertEquals(100, settings.characterPoints);

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "fomorian");
		assertNotNull("Metatype 'fomorian' not found", human);
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);
		assertEquals(990, model.getKarmaFree());

		// Attributes
		assertEquals(45, increaseAttrib(ShadowrunAttribute.BODY, 4));
		assertEquals(45, increaseAttrib(ShadowrunAttribute.AGILITY, 4));
		assertEquals(25, increaseAttrib(ShadowrunAttribute.REACTION, 3));
		assertEquals(45, increaseAttrib(ShadowrunAttribute.STRENGTH, 4));
		assertEquals(70, increaseAttrib(ShadowrunAttribute.WILLPOWER, 5));
		assertEquals(45, increaseAttrib(ShadowrunAttribute.LOGIC, 4));
		assertEquals(70, increaseAttrib(ShadowrunAttribute.INTUITION, 5));
		assertEquals(45, increaseAttrib(ShadowrunAttribute.CHARISMA, 4));
		assertEquals(45, increaseAttrib(ShadowrunAttribute.EDGE, 4));
		assertEquals(555, model.getKarmaFree());

		// Magic/Resonance
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "aspectedmagician"));
		model.setAspectSkill(Shadowrun6Core.getSkill("sorcery"));
		assertEquals(510, model.getKarmaFree());
		assertEquals(100, increaseAttrib(ShadowrunAttribute.MAGIC, 6));
		assertEquals(410, model.getKarmaFree());

		//Spells
		ISpellController<SR6Spell> spells = charGen.getSpellController();
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "increase_attribute")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "treat")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "stunbolt")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "diagnose")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "antidote")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "heal")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "detect_life")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "physical_barrier")).wasSuccessful());
		assertTrue(spells.select(Shadowrun6Core.getItem(SR6Spell.class, "stabilize")).wasSuccessful());
		assertEquals(365, model.getKarmaFree());

		// Skills
		SR6SkillController skills = charGen.getSkillController();
		assertEquals(30,increaseSkill("astral", 3));
		assertTrue(skills.select(model.getSkillValue(Shadowrun6Core.getSkill("astral")),
				Shadowrun6Core.getSkill("astral").getSpecialization("astral_signatures"), false).wasSuccessful());
		assertEquals(330, model.getKarmaFree());
		assertEquals(15,increaseSkill("athletics", 2));
		assertEquals(50,increaseSkill("biotech", 4));
		assertTrue(skills.select(model.getSkillValue(Shadowrun6Core.getSkill("biotech")),
				Shadowrun6Core.getSkill("biotech").getSpecialization("first_aid"), false).wasSuccessful());
		assertEquals(5,increaseSkill("electronics", 1));
		assertEquals(30,increaseSkill("firearms", 3));
		assertEquals(30,increaseSkill("stealth", 3));
		assertEquals(105,increaseSkill("sorcery", 6));
		assertTrue(skills.select(model.getSkillValue(Shadowrun6Core.getSkill("sorcery")),
				Shadowrun6Core.getSkill("sorcery").getSpecialization("spellcasting"), false).wasSuccessful());
		assertEquals(15,increaseSkill("close_combat", 2));
		assertEquals(15,increaseSkill("piloting", 2));
		assertEquals(50,increaseSkill("perception", 4));
		assertEquals(5, model.getKarmaFree());

		// Qualities
		assertTrue(charGen.getQualityController().select(Shadowrun6Core.getItem(Quality.class, "non_lethal")).wasSuccessful()); // 5
		assertEquals(10, model.getKarmaFree());
		assertTrue(charGen.getQualityController().select(Shadowrun6Core.getItem(Quality.class, "team_player")).wasSuccessful()); // 10
		assertEquals(0, model.getKarmaFree());
		assertTrue(charGen.getQualityController().select(Shadowrun6Core.getItem(Quality.class, "sinner")).wasSuccessful()); // 8
		assertEquals(8, model.getKarmaFree());
		OperationResult<QualityValue>  res = charGen.getQualityController().select(Shadowrun6Core.getItem(Quality.class, "stim_patch_allergy"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(18, model.getKarmaFree());

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

}
