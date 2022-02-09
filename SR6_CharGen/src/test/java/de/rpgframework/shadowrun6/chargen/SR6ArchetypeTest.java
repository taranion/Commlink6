package de.rpgframework.shadowrun6.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
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
	public void testAdept() {
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
		
//		SR6SkillController skills = charGen.getSkillController();
//		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("athletics"));
//		assertNotNull(sVal);
//		assertTrue(sVal.getError(), sVal.isPresent());
//		skills.increase(sVal.get()); // 2
//		skills.increase(sVal.get()); // 3
//		assertEquals(3,model.getSkillValue(Shadowrun6Core.getSkill("athletics")).getDistributed());
		
		CarriedItem item = new CarriedItem(Shadowrun6Core.getItem(ItemTemplate.class, "bow"), null);
		model.addCarriedItem(item);
		
		
		byte[] raw = Shadowrun6Core.save(model);
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
