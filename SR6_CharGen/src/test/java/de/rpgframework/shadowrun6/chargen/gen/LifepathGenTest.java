package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.character.CharacterIOException;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathModuleGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifepathCharacterGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class LifepathGenTest {

	private Shadowrun6Character model;
	private SR6LifepathCharacterGenerator charGen;

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
		charGen = new SR6LifepathCharacterGenerator(model,null);
		charGen.setModel(model, null);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testComp48() throws CharacterIOException {
		charGen.runProcessors();
		assertEquals(50, model.getKarmaFree());
//		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
//		assertEquals(100, settings.characterPoints);
		model.setName("Lucifer Lifepath");

		// Magic/Resonance
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane"));
		assertEquals(50, model.getKarmaFree());
//		assertEquals(2, model.getAttribute(ShadowrunAttribute.EDGE    ).getModifiedValue());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "troll");
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);
		int expectedKarma = 50 - charGen.getMetatypeController().getKarmaCost(human);
		assertEquals(expectedKarma, model.getKarmaFree());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.AGILITY).getModifiedValue());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.EDGE    ).getModifiedValue());

		/*
		 * The nationality will be UCAS, and our native language will be English. The quality
		 * this character feels they were born with is Tough as Nails at level 2, which costs us 8 Karma.
		 */
		charGen.getBornThisWayGenerator().getQualityController().select(Shadowrun6Core.getItem(SR6Quality.class, "built_tough"));
		charGen.getBornThisWayGenerator().getQualityController().select(Shadowrun6Core.getItem(SR6Quality.class, "sinner"));
		charGen.getBornThisWayGenerator().selectNativeLanguage("English");
		expectedKarma -= Shadowrun6Core.getItem(SR6Quality.class, "built_tough").getKarmaCost();
		expectedKarma += Shadowrun6Core.getItem(SR6Quality.class, "sinner").getKarmaCost();
		assertEquals(expectedKarma, model.getKarmaFree());
		assertTrue(model.hasQuality("built_tough"));
		assertTrue(model.hasQuality("sinner"));
		assertEquals(2, charGen.getBornThisWayGenerator().getQualityController().getSelected().size());

		// Modules
		SR6LifePathModuleGenerator modules = charGen.getModuleGenerator();
		OperationResult<LifepathModuleValue> res = modules.select(Shadowrun6Core.getItem(LifepathModule.class, "gangmitglied"),
				new Decision("dd8bb000-fc65-4195-a8cf-1866fa01f2ed","REACTION"),
				new Decision("5e000fdb-8415-4a0c-9a1d-1e55f633ed34","firearms"),
				new Decision("72f10b55-0eb5-41b6-874a-00d3b738c905", "stealth")
				);
		assertNotNull(res);
		assertTrue(res.getError(),res.wasSuccessful());

		charGen.finish();
		assertEquals(expectedKarma, model.getKarmaFree());
		assertEquals(50000, model.getNuyen());
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

	//-------------------------------------------------------------------
	@Test
	public void testLifepathModuleQualityChoicePolarity() {
		SR6LifePathModuleGenerator modules = charGen.getModuleGenerator();
		LifepathModule money = Shadowrun6Core.getItem(LifepathModule.class, "an_eine_menge_geld_gekommen");
		assertNotNull(money);

		OperationResult<LifepathModuleValue> positive = modules.select(money,
				new Decision("eb8b1c35-686a-4251-a564-2fb29451a3a4", "built_tough")
				);
		assertFalse(positive.wasSuccessful());

		OperationResult<LifepathModuleValue> negative = modules.select(money,
				new Decision("eb8b1c35-686a-4251-a564-2fb29451a3a4", "sinner")
				);
		assertTrue(negative.getError(), negative.wasSuccessful());
	}

	//-------------------------------------------------------------------
	@Test
	public void testCannotDeselectRequiredLifeModule() {
		SR6LifePathModuleGenerator modules = charGen.getModuleGenerator();
		LifepathModule studies = Shadowrun6Core.getItem(LifepathModule.class, "studium");
		LifepathModule doctor = Shadowrun6Core.getItem(LifepathModule.class, "aerztin");
		assertNotNull(studies);
		assertNotNull(doctor);

		OperationResult<LifepathModuleValue> studiesResult = modules.select(studies,
				new Decision("4085c382-f35f-4cf4-a3a0-1f1de3b08e83", "LOGIC"),
				new Decision("d3782337-10d5-4be6-b7ee-d8f07098537b", "biotech"),
				new Decision("5e39d6f5-b06f-457c-8017-e2cf6e3e9c5d", "electronics")
				);
		assertTrue(studiesResult.getError(), studiesResult.wasSuccessful());

		OperationResult<LifepathModuleValue> doctorResult = modules.select(doctor,
				new Decision("3a2d5141-0787-4d99-b550-bf5d90a8ec74", "LOGIC"),
				new Decision("d6a6b973-7455-4d5f-877a-db1db4db542f", "biotech")
				);
		assertTrue(doctorResult.getError(), doctorResult.wasSuccessful());

		assertFalse(modules.canBeDeselected(studiesResult.get()).get());
		assertFalse(modules.deselect(studiesResult.get()));
		assertTrue(modules.getSelected().contains(studiesResult.get()));

		assertTrue(modules.deselect(doctorResult.get()));
		assertTrue(modules.canBeDeselected(studiesResult.get()).get());
		assertTrue(modules.deselect(studiesResult.get()));
	}

	//-------------------------------------------------------------------
	@Test
	public void testLifePathPowerLevelOnlyChangesModuleCount() {
		assertLifePathDefaults(PowerLevel.STREET_LEVEL, 6);
		assertLifePathDefaults(PowerLevel.STANDARD, 8);
		assertLifePathDefaults(PowerLevel.ELITE, 10);
	}

	//-------------------------------------------------------------------
	private void assertLifePathDefaults(PowerLevel level, int expectedModules) {
		charGen.applyPowerLevelDefaults(level);
		assertEquals(50, charGen.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_ADJUSTMENT_KARMA));
		assertEquals(expectedModules, charGen.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_MAX_MODULES));
		assertEquals(6, charGen.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_MAX_QUALITIES));
		assertEquals(20, charGen.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_NEGATIVE_KARMA_CAP));
		assertEquals(25000, charGen.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_START_NUYEN));
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testGaius() throws CharacterIOException {
		charGen.runProcessors();
		assertEquals(50, model.getKarmaFree());
//		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
//		assertEquals(100, settings.characterPoints);
		model.setName("Gaius");

		// Magic/Resonance
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mysticadept"));
		assertEquals(50, model.getKarmaFree());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.AGILITY).getModifiedValue());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.EDGE    ).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.MAGIC   ).getModifiedValue());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "elf");
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);
		assertEquals(50, model.getKarmaFree());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.AGILITY).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.EDGE    ).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.MAGIC   ).getModifiedValue());
		charGen.getBornThisWayGenerator().selectNativeLanguage("German");
		charGen.getBornThisWayGenerator().getQualityController().select(Shadowrun6Core.getItem(SR6Quality.class, "sinner"));

		charGen.getChildhoodGenerator().getSkillController().select(Shadowrun6Core.getItem(SR6Skill.class, "con"));
		charGen.getChildhoodGenerator().getSkillController().select(Shadowrun6Core.getItem(SR6Skill.class, "influence"));
		charGen.getChildhoodGenerator().getSkillController().select(Shadowrun6Core.getItem(SR6Skill.class, "perception"));
		charGen.getChildhoodGenerator().getSkillController().select(Shadowrun6Core.getItem(SR6Skill.class, "athletics"));
		charGen.getChildhoodGenerator().getQualityController().select(Shadowrun6Core.getItem(SR6Quality.class, "charismatic_defense"));
		charGen.getChildhoodGenerator().getQualityController().select(Shadowrun6Core.getItem(SR6Quality.class, "allergy"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(0).getUUID(),"seasonal"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(1).getUUID(),"mild"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(2).getUUID(),"Pollen")
				);

		charGen.getEarlyAdultGenerator().getSkillController().select(Shadowrun6Core.getItem(SR6Skill.class, "sorcery"));

		charGen.finish();
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}
}
