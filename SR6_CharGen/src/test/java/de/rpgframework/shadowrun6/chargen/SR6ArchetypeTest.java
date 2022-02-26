package de.rpgframework.shadowrun6.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.character.CharacterIOException;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class SR6ArchetypeTest {
	
	private Shadowrun6Character model;
	private PriorityCharacterGenerator charGen;

	//-------------------------------------------------------------------
	@BeforeClass
	public static void setupClass() {
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();
//		SpliMoConfigOptions.attachConfigurationTree(new ConfigContainerImpl(Preferences.userNodeForPackage(ArchetypeTest.class), "unittest"));
	}

	//-------------------------------------------------------------------
	@Before
	public void setup() {
		model = new Shadowrun6Character();
		charGen = new PriorityCharacterGenerator();
		charGen.setModel(model, null);
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		assertEquals(50, model.getKarmaFree());
	}
	
	//-------------------------------------------------------------------
	private boolean raiseAttributeTo(ShadowrunAttribute key, int target) {
		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(key);
		while (val.getModifiedValue()<target) {
			assertTrue("May not increase "+val.getModifyable(), attribs.canBeIncreased(val).get());
			assertTrue("Failed raising from "+val.getModifiedValue(), attribs.increase(val).wasSuccessful());
		}
		return true;
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testAdept() throws CharacterIOException {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.B);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.E);
		assertEquals(50, model.getKarmaFree());
		
		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "human");
		assertNotNull("Metatype 'human' not found", human);
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);
		
		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "adept"));
		
		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		raiseAttributeTo(ShadowrunAttribute.BODY     , 5);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 6);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 5);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 5);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 4);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 2);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 3);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 2);
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 7);
		raiseAttributeTo(ShadowrunAttribute.MAGIC    , 6);
		
		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		Possible poss = qualities.canBeSelected(Shadowrun6Core.getItem(Quality.class, "ar_vertigo")); 
		assertTrue("Selecting failed:"+poss,poss.get());
		OperationResult<QualityValue> res = qualities.select(Shadowrun6Core.getItem(Quality.class, "ar_vertigo"));
		assertNotNull(res);
		assertTrue(res.wasSuccessful());
		assertEquals(60, model.getKarmaFree());
		// Honorbound
		poss = qualities.canBeSelected(Shadowrun6Core.getItem(Quality.class, "honorbound")); 
		assertNotNull(poss.getMostSevere());
		assertTrue(poss.get());
		res = qualities.select(Shadowrun6Core.getItem(Quality.class, "honorbound"));
		assertFalse("Should have failed: "+res,res.wasSuccessful());
		res = qualities.select(Shadowrun6Core.getItem(Quality.class, "honorbound"), new Decision(Shadowrun6Core.getItem(Quality.class, "honorbound").getChoices().get(0).getUUID(),"Code Duello"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(70, model.getKarmaFree());
		// Adding another negative quality should not be possible, because it generates >20 Karma
		assertFalse("More than 20 netto Karma gain not detected" ,qualities.select(Shadowrun6Core.getItem(Quality.class, "sinner")).wasSuccessful());
		// Take "Quick Healer" first
		assertTrue(qualities.select(Shadowrun6Core.getItem(Quality.class, "quick_healer")).wasSuccessful());
		assertEquals(62, model.getKarmaFree());
		// Now another negative quality should work
		assertTrue(qualities.select(Shadowrun6Core.getItem(Quality.class, "sinner")).wasSuccessful());
		assertEquals(70, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(Quality.class, "toughness")).wasSuccessful());
		assertEquals(58, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(Quality.class, "guts")).wasSuccessful());
		assertEquals(46, model.getKarmaFree());
		
		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(16, skills.getPointsLeft());
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("athletics"));
		assertNotNull(sVal);
		assertTrue(sVal.getError(), sVal.isPresent());
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		assertEquals(3,model.getSkillValue(Shadowrun6Core.getSkill("athletics")).getDistributed());
		assertEquals(13, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		
		sVal = skills.select(Shadowrun6Core.getSkill("biotech"));
		skills.increase(sVal.get()); // 2
		assertEquals(11, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		
		sVal = skills.select(Shadowrun6Core.getSkill("close_combat"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 5
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 6
		assertEquals(5, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());

		sVal = skills.select(Shadowrun6Core.getSkill("outdoors"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(3, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		assertEquals(46, model.getKarmaFree());

		sVal = skills.select(Shadowrun6Core.getSkill("perception"));
		assertNotNull(sVal);
		assertTrue(sVal.getError(), sVal.isPresent());
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertEquals(0, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		assertEquals(46, model.getKarmaFree());
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertEquals(26, model.getKarmaFree());


		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("athletics"));
		skills.select(tmp, Shadowrun6Core.getSkill("athletics").getSpecialization("throwing"), false);
		assertEquals(21, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("biotech"));
		skills.select(tmp, Shadowrun6Core.getSkill("biotech").getSpecialization("first_aid"), false);
		assertEquals(16, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("close_combat"));
		skills.select(tmp, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed"), false);
		assertEquals(11, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("perception"));
		skills.select(tmp, Shadowrun6Core.getSkill("perception").getSpecialization("visual"), false);
		assertEquals(6, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		
		assertEquals(1, model.getSkillValues(SkillType.LANGUAGE).size());
		assertEquals(4, model.getSkillValues(SkillType.LANGUAGE).get(0).getDistributed());
//		model.getSkillValues(SkillType.LANGUAGE).get(0).setName("English");
		assertTrue(skills.canBeSelected(Shadowrun6Core.getSkill("knowledge")).get());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Fight Clubs")) );
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Fort Lewis Geography") ));
		assertEquals(0, skills.getPointsLeft2());
		
		CarriedItem item = new CarriedItem(Shadowrun6Core.getItem(ItemTemplate.class, "bow"), null);
		model.addCarriedItem(item);
		
		
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testNartaki() {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.B);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.E);
		assertEquals(50, model.getKarmaFree());
		
		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "nartaki");
		assertNotNull("Metatype 'nartaki' not found", human);
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);
	}
}
