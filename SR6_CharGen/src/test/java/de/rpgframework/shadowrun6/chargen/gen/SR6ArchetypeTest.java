package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.character.CharacterIOException;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.SetItem;
import de.rpgframework.genericrpg.SetItemValue;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.CheckModification;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormValue;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunFlags;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IContactController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.charctrl.SINController;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;
import de.rpgframework.shadowrun6.chargen.gen.priority.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityComplexFormGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySpellGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunCheckInfluence;

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
			Possible poss = attribs.canBeIncreased(val);
			assertTrue("May not increase "+val.getModifyable()+" to "+(val.getModifiedValue()+1)+":"+poss, poss.get());
			assertTrue("Failed raising from "+val.getModifiedValue(), attribs.increase(val).wasSuccessful());
			System.out.println("Increased "+val.getModifyable()+" to "+val.getModifiedValue());
		}
		return true;
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test01Adept() throws Exception {
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
		assertEquals(24, attribs.getPointsLeft2());
		assertEquals(9, attribs.getPointsLeft());
		raiseAttributeTo(ShadowrunAttribute.BODY     , 5);
		assertEquals(20, attribs.getPointsLeft2());
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 6);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 5);
		assertEquals(11, attribs.getPointsLeft2());
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 5);
		assertEquals(7, attribs.getPointsLeft2());
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 4);
		assertEquals(4, attribs.getPointsLeft2());
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 2);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 3);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 2);
		assertEquals(0, attribs.getPointsLeft2());
		assertEquals(9, attribs.getPointsLeft());
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 7);
		assertEquals(3, attribs.getPointsLeft());
		assertEquals(3, model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue());
		raiseAttributeTo(ShadowrunAttribute.MAGIC    , 6);

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		Possible poss = qualities.canBeSelected(Shadowrun6Core.getItem(SR6Quality.class, "ar_vertigo"));
		assertTrue("Selecting failed:"+poss,poss.get());
		OperationResult<QualityValue> res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "ar_vertigo"));
		assertNotNull(res);
		assertTrue(res.wasSuccessful());
		assertEquals(60, model.getKarmaFree());
		// Honorbound
		poss = qualities.canBeSelected(Shadowrun6Core.getItem(SR6Quality.class, "honorbound"));
		assertNotNull(poss.getMostSevere());
		assertEquals(Possible.State.DECISIONS_MISSING, poss.getState());
		assertTrue(poss.toString(), poss.get()); // Non-stopper warnings should not lead to blocking
		poss = qualities.canBeSelected(Shadowrun6Core.getItem(SR6Quality.class, "honorbound"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "honorbound").getChoices().get(0).getUUID(),"Code Duello"));
		assertNull(poss.getMostSevere());
		assertTrue(poss.toString(), poss.get());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "honorbound"));
		assertFalse("Should have failed: "+res,res.wasSuccessful());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "honorbound"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "honorbound").getChoices().get(0).getUUID(),"Code Duello"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(70, model.getKarmaFree());
		// Adding another negative quality should not be possible, because it generates >20 Karma
		assertFalse("More than 20 netto Karma gain not detected" ,qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "sinner")).wasSuccessful());
		// Take "Quick Healer" first
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "quick_healer")).wasSuccessful());
		assertEquals(62, model.getKarmaFree());
		// Now another negative quality should work
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "sinner")).wasSuccessful());
		assertEquals(70, model.getKarmaFree());
		assertEquals(1, model.getSINs().size());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "toughness")).wasSuccessful());
		assertEquals(58, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "guts")).wasSuccessful());
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
		assertEquals(4, sVal.get().getModifiedValue());

		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("athletics"));
		skills.select(tmp, Shadowrun6Core.getSkill("athletics").getSpecialization("throwing"), false);
		assertEquals(21, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("biotech"));
		skills.select(tmp, Shadowrun6Core.getSkill("biotech").getSpecialization("first_aid"), false);
		assertEquals(16, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("close_combat"));
		skills.select(tmp, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed"), false);
		assertEquals(11, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("perception"));
		System.out.println("-----------------------\n\n");
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

		IAdeptPowerController adept = charGen.getAdeptPowerController();
		assertEquals(6.0f, adept.getUnsedPowerPoints(), 0f);
		// Combat Sense 2
		OperationResult<AdeptPowerValue> pVal =  adept.select(Shadowrun6Core.getItem(AdeptPower.class, "combat_sense"));
		assertTrue(pVal.toString(), pVal.wasSuccessful());
		assertEquals(5.5f, adept.getUnsedPowerPoints(), 0f);
		pVal =  adept.increase(pVal.get());
		assertTrue(pVal.toString(), pVal.wasSuccessful());
		assertEquals(5f, adept.getUnsedPowerPoints(), 0f);
		// Critical Strike 2
		pVal =  adept.select(Shadowrun6Core.getItem(AdeptPower.class, "critical_strike"));
		pVal =  adept.increase(pVal.get());
		assertTrue(pVal.toString(), pVal.wasSuccessful());
		assertEquals(3f, adept.getUnsedPowerPoints(), 0f);
		// Improved reflexes 2
		pVal =  adept.select(Shadowrun6Core.getItem(AdeptPower.class, "improved_reflexes"));
		pVal =  adept.increase(pVal.get());
		assertTrue(pVal.toString(), pVal.wasSuccessful());
		assertEquals(1f, adept.getUnsedPowerPoints(), 0f);
		// Killing hands
		pVal =  adept.select(Shadowrun6Core.getItem(AdeptPower.class, "killing_hands"));
		assertTrue(pVal.toString(), pVal.wasSuccessful());
		assertEquals(0.5f, adept.getUnsedPowerPoints(), 0f);

		// Equipment
		ISR6EquipmentController equip = charGen.getEquipmentController();
		poss = equip.canBeSelected(Shadowrun6Core.getItem(ItemTemplate.class, "armor_vest"));
		assertNotNull(poss);
		assertTrue( poss.toString(), poss.get());
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_vest")).wasSuccessful() );
		// Contacts
		OperationResult<CarriedItem<ItemTemplate>> contactsRes = equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "contacts"),
				new Decision(
						Shadowrun6Core.getItem(ItemTemplate.class, "contacts").getChoices().get(0).getUUID(),
						"3"));
		assertTrue( contactsRes.getError(), contactsRes.wasSuccessful() );
		assertTrue( equip.embed(contactsRes.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "flare_compensation"), null).wasSuccessful() );
		assertTrue( equip.embed(contactsRes.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "image_link"), null).wasSuccessful() );
		assertTrue( equip.embed(contactsRes.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "low_light_vision"), null).wasSuccessful() );
		assertEquals(1075, contactsRes.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		OperationResult<CarriedItem<ItemTemplate>> sonyRes = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "sony_emporer"));
		assertEquals(700, sonyRes.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "subvocal_microphone")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "trodes")).wasSuccessful() );
		assertTrue( contactsRes.getError(), contactsRes.wasSuccessful() );

		ItemTemplate bow = Shadowrun6Core.getItem(ItemTemplate.class, "bow");
		// Try to add bow without rating
		assertFalse(  equip.select(bow).wasSuccessful() );
		// Try to add bow with rating
		OperationResult<CarriedItem<ItemTemplate>> bowC = equip.select(bow, new Decision(bow.getChoices().get(0), "5"));
		assertTrue(  bowC.wasSuccessful() );
		bowC.get().addFlag(ShadowrunFlags.PRIMARY_RANGED_WEAPON);

		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "knife")).wasSuccessful() );
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "shock_gloves")).wasSuccessful() );
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "stun_baton")).wasSuccessful() );
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "sword")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> itemRes = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "throwing_knives"));
		CarriedItem<ItemTemplate> item = itemRes.get();
		assertTrue(  equip.increase(item).wasSuccessful() ); // Count 2
		assertTrue(  equip.increase(item).wasSuccessful() ); // Count 3
		assertTrue(  equip.increase(item).wasSuccessful() ); // Count 4
		assertTrue(  equip.increase(item).wasSuccessful() ); // Count 5
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "yamaha_growler")).wasSuccessful() );

		// Lifestyle
		OperationResult<SR6Lifestyle> lifeRes = charGen.getLifestyleController().select(Shadowrun6Core.getItem(LifestyleQuality.class, "low"));
		assertTrue(lifeRes.wasSuccessful());
		lifeRes.get().setPrimary(true);
		lifeRes.get().setName("Room in a shared appartment");
		lifeRes.get().setSIN(model.getSINs().get(0).getUniqueId());

		// Contacts
		IContactController contacts = charGen.getContactController();
		Contact ganger = contacts.createContact().get();
		assertNotNull(ganger);
		ganger.setName("Brian");
		ganger.setTypeName("First Nations Ganger");
		ganger.setType(ContactType.CRIMINAL);
		contacts.increaseLoyalty(ganger);
		Contact secretary = contacts.createContact().get();
		secretary.setName("Eno");
		secretary.setTypeName("Salish Government Secretary");
		contacts.increaseRating(secretary);
		contacts.increaseLoyalty(secretary);
		ganger.setType(ContactType.GOVERNMENT);
		Contact sensei = contacts.createContact().get();
		sensei.setName("Sensei");
		contacts.increaseLoyalty(sensei);
		Contact squatter = contacts.createContact().get();
		squatter.setName("Squatter");
		squatter.setType(ContactType.STREET);

		model.setName("Adept");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

		// Try to reload it again
		Shadowrun6Core.decode(raw);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test02CombatMage() throws Exception {
		model.setStrictness("core");
		charGen.getRuleController().setRuleValue(Shadowrun6Rules.CHARGEN_BUY_SPELLS_KARMA, Boolean.TRUE);
		charGen.runProcessors();
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.B);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.E);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType meta = Shadowrun6Core.getItem(SR6MetaType.class, "ork");
		charGen.getMetatypeController().canBeSelected(meta);
		charGen.getMetatypeController().select(meta);

		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician"));

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 3);
		raiseAttributeTo(ShadowrunAttribute.MAGIC    , 6);
		for (int i=1; i<=3; i++) assertTrue(attribs.increasePoints(model.getAttribute(ShadowrunAttribute.BODY)).wasSuccessful());
		for (int i=4; i<=6; i++) assertTrue(attribs.increasePoints2(model.getAttribute(ShadowrunAttribute.BODY)).wasSuccessful());
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 7);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 5);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 4);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 5);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 3);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 3);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 2);

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "allergy"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(0).getUUID(),"common"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(1).getUUID(),"mild"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(2).getUUID(),"Grass")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(61, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "focused_concentration"));
		assertTrue(res.wasSuccessful());
		assertEquals(49, model.getKarmaFree());
		assertTrue(qualities.increase(res.get()).wasSuccessful());
		assertEquals(37, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "ar_vertigo")).wasSuccessful());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "astral_beacon")).wasSuccessful());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "aptitude"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "aptitude").getChoices().get(0).getUUID(),"sorcery")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "spirit_bane"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "spirit_bane").getChoices().get(0).getUUID(),"spirit_of_water")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());

		assertEquals(57, model.getKarmaFree());

		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(16, skills.getPointsLeft());

		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("sorcery"));
		assertTrue("Could not select skill with maximum modifications",sVal.wasSuccessful());
		assertEquals(15, skills.getPointsLeft());
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		skills.increase(sVal.get()); // 5
		skills.increase(sVal.get()); // 6
		assertTrue(skills.canBeIncreasedPoints(sVal.get()).toString(),  skills.canBeIncreasedPoints(sVal.get()).get());
		assertTrue("Aptitude not detected", skills.increase(sVal.get()).wasSuccessful()); // 7

		sVal = skills.select(Shadowrun6Core.getSkill("close_combat"));
		sVal = skills.select(Shadowrun6Core.getSkill("conjuring"));
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		skills.increase(sVal.get()); // 5
		assertEquals(3, skills.getPointsLeft());
		assertEquals(3, skills.getPointsLeft2());

		sVal = skills.select(Shadowrun6Core.getSkill("perception"));
		assertEquals(2, skills.getPointsLeft());
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(1, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("stealth"));
		assertEquals(0, skills.getPointsLeft());
		assertEquals(57, model.getKarmaFree());

		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("sorcery"));
		skills.select(tmp, Shadowrun6Core.getSkill("sorcery").getSpecialization("spellcasting"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(52, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("conjuring"));
		skills.select(tmp, Shadowrun6Core.getSkill("conjuring").getSpecialization("summoning"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(47, model.getKarmaFree());
		assertEquals(3, skills.getPointsLeft2());

		assertEquals(1, model.getSkillValues(SkillType.LANGUAGE).size());
		assertEquals(4, model.getSkillValues(SkillType.LANGUAGE).get(0).getDistributed());
//		model.getSkillValues(SkillType.LANGUAGE).get(0).setName("English");
		assertTrue(skills.canBeSelected(Shadowrun6Core.getSkill("knowledge")).get());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Magical History")) );
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Wizzer Gangs") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Ork Culture") ));
		assertEquals(0, skills.getPointsLeft2());

		// Spells
		SR6SpellController spells = charGen.getSpellController();
		assertEquals(6, ((SR6PrioritySpellGenerator)spells).getFreeSpells());

		SR6Spell spell = Shadowrun6Core.getItem(SR6Spell.class, "armor");
		assertNotNull("Spell not found",spell);
		assertTrue( spells.select(spell).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "confusion")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "detect_enemies")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "flamestrike")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "heal")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "ice_storm")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "levitate")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "physical_barrier")).wasSuccessful() );

		ISR6EquipmentController equip = charGen.getEquipmentController();
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		Possible poss = equip.canBeSelected(Shadowrun6Core.getItem(ItemTemplate.class, "armor_jacket"));
		assertNotNull(poss);
		assertTrue( poss.toString(), poss.get());
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_jacket")).wasSuccessful() );
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "chrysler-nissan_jackrabbit")).wasSuccessful() );
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "renraku_sensei")).wasSuccessful() );

		SINController sinCtrl = charGen.getSINController();
		SIN[] sins =sinCtrl.createNewSIN(FakeRating.ANYONE, 3);
		assertNotNull("Failed creating multiple SINs",sins);

		// Lifestyle
		OperationResult<SR6Lifestyle> lifeRes = charGen.getLifestyleController().select(Shadowrun6Core.getItem(LifestyleQuality.class, "low"));
		assertTrue(lifeRes.wasSuccessful());
		lifeRes.get().setPrimary(true);
		lifeRes.get().setName("Appartment in decaying megahousing");
		lifeRes.get().setSIN(model.getSINs().get(0).getUniqueId());

		// Contacts
		IContactController contacts = charGen.getContactController();
		Contact c1 = contacts.createContact().get();
		c1.setTypeName("Corporate Wage Mage");
		contacts.increaseLoyalty(c1);
		contacts.increaseRating(c1);
		c1.setType("CORPORATE");
		Contact c2 = contacts.createContact().get();
		c2.setTypeName("Crimson Crush Ganger");
		contacts.increaseLoyalty(c2);
		contacts.increaseRating(c2);
		c2.setType("CRIMINAL");
		Contact c3 = contacts.createContact().get();
		c3.setTypeName("Talismonger");
		contacts.increaseLoyalty(c3);
		contacts.increaseRating(c3);
		c3.setType("MAGIC");

//		ItemTemplate bow = Shadowrun6Core.getItem(ItemTemplate.class, "bow");
//		// Try to add bow without rating
//		assertFalse(  equip.select(bow).wasSuccessful() );
//		// Try to add bow with rating
//		assertTrue(  equip.select(bow, new Decision(bow.getChoices().get(0), "5")).wasSuccessful() );
		model.setName("Combat Mage");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

		// Try to reload it again
		Shadowrun6Core.decode(raw);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test03CovertOps() throws Exception {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.D);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.B);
		prio.setPriority(PriorityType.RESOURCES, Priority.C);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "elf");
		charGen.getMetatypeController().select(human);

		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane"));

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 3);
		for (int i=1; i<=2; i++) assertTrue(attribs.increasePoints(model.getAttribute(ShadowrunAttribute.CHARISMA)).wasSuccessful());
		raiseAttributeTo(ShadowrunAttribute.BODY     , 3);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 5);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 4);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 2);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 4);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 7);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 3);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 5);

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "addiction"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "addiction").getChoices().get(0).getUUID(),"Nic-stick")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		qualities.increase(res.get());
		qualities.increase(res.get());
		assertEquals(56, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "aptitude"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "aptitude").getChoices().get(0).getUUID(),"stealth")
				);
		assertEquals(44, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "blandness"));
		assertEquals(36, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "honorbound"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "honorbound").getChoices().get(0).getUUID(),"White Hat")
				);
		assertEquals(46, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "photographic_memory"));
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "prejudiced"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "prejudiced").getChoices().get(0).getUUID(),"Technomancers")
				);
		assertEquals(42, model.getKarmaFree());

		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(24, skills.getPointsLeft());
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("athletics"));
		skills.increase(sVal.get()); // 2
		assertEquals(22, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("close_combat"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(20, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("con"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(18, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("stealth"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 5
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 6
		assertTrue("Aptitude not recognized",skills.increase(sVal.get()).wasSuccessful()); // 7
		assertEquals(11, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("engineering"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(9, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("perception"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(7, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("firearms"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(5, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("biotech"));
		assertEquals(4, skills.getPointsLeft());

		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("athletics"));
		skills.select(tmp, Shadowrun6Core.getSkill("athletics").getSpecialization("sprinting"), false);
		assertEquals(3, skills.getPointsLeft());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("close_combat"));
		skills.select(tmp, Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed"), false);
		assertEquals(2, skills.getPointsLeft());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("con"));
		skills.select(tmp, Shadowrun6Core.getSkill("con").getSpecialization("impersonation"), false);
		assertEquals(1, skills.getPointsLeft());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("engineering"));
		skills.select(tmp, Shadowrun6Core.getSkill("engineering").getSpecialization("lockpicking"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(42, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("perception"));
		skills.select(tmp, Shadowrun6Core.getSkill("perception").getSpecialization("urban"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(37, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("stealth"));
		skills.select(tmp, Shadowrun6Core.getSkill("stealth").getSpecialization("palming"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(32, model.getKarmaFree());
		assertEquals(3, skills.getPointsLeft2());

		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Seattle Johnsons")) );
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Seattle Downtown Geography") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Corporate Security Tactics") ));
		assertEquals(0, skills.getPointsLeft2());

		// Contacts
		IContactController contCtrl = charGen.getContactController();
		Contact c1 = contCtrl.createContact().get();
		c1.setTypeName("Beat Cop");
		contCtrl.increaseLoyalty(c1);
		contCtrl.increaseLoyalty(c1);
		contCtrl.increaseLoyalty(c1);
		contCtrl.increaseRating(c1);
		c1.setType(ContactType.GOVERNMENT);
		Contact c2 = contCtrl.createContact().get();
		c2.setTypeName("Corporate Secretary");
		contCtrl.increaseLoyalty(c2);
		contCtrl.increaseLoyalty(c2);
		contCtrl.increaseRating(c2);
		contCtrl.increaseRating(c2);
		c2.setType(ContactType.CORPORATE);
		Contact c3 = contCtrl.createContact().get();
		c3.setTypeName("Fixer");
		contCtrl.increaseLoyalty(c3);
		contCtrl.increaseLoyalty(c3);
		contCtrl.increaseLoyalty(c3);
		contCtrl.increaseRating(c3);
		contCtrl.increaseRating(c3);
		contCtrl.increaseRating(c3);
		contCtrl.increaseRating(c3);
		contCtrl.increaseRating(c3);
		c3.setType(ContactType.CRIMINAL);

		// Augmentations
		ISR6EquipmentController equip = charGen.getEquipmentController();
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		assertTrue(equip.increaseConversion());
		equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "datajack"));
//		equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "image_link"));
		OperationResult<CarriedItem<ItemTemplate>> toner = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "muscle_toner"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "muscle_toner").getChoices().get(0), "1"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(toner.wasSuccessful());
		OperationResult<CarriedItem<ItemTemplate>> enhan = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "reaction_enhancers"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "reaction_enhancers").getChoices().get(0), "1"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(enhan.wasSuccessful());
		OperationResult<CarriedItem<ItemTemplate>> wired = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "wired_reflexes"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "wired_reflexes").getChoices().get(0), "1"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(wired.wasSuccessful());

		Possible poss = equip.canBeSelected(Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm"));
		assertNotNull(poss);
		assertNotNull("Missing variant not detected" , poss.getMostSevere());
		assertTrue("Should be marked selectable, although variant not selected" , poss.get());
		poss = equip.canBeSelected(Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm"), "hand_obvious", CarryMode.IMPLANTED);
		assertTrue(poss.toString(), poss.get());
		OperationResult<CarriedItem<ItemTemplate>> hand = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm"), "hand_obvious", CarryMode.IMPLANTED, new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue (hand.getError(), hand.wasSuccessful());
		hand.get().setCustomName("Obvious left hand");
		OperationResult<CarriedItem<ItemTemplate>> shock = equip.embed(hand.get(), ItemHook.CYBERLIMB_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "shock_limb"), null,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				);
		assertTrue (shock.getError(), shock.wasSuccessful());
		shock.get().setCustomName("Shock Hand");
		OperationResult<CarriedItem<ItemTemplate>> imageLink = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "image_link"), "bodyware", CarryMode.IMPLANTED, new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue (imageLink.getError(), imageLink.wasSuccessful());
		OperationResult<CarriedItem<ItemTemplate>> smartLink = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "smartlink"), "bodyware", CarryMode.IMPLANTED, new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue (smartLink.getError(), smartLink.wasSuccessful());

		//Gear
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "actioneer_business")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "jammer"), "area", null,
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "jammer").getChoices().get(0), "6")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "autopicker")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "bug_scanner")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> cam = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "camera"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "camera").getChoices().get(0), "4"));
		assertTrue( equip.embed(cam.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "low_light_vision"), null).wasSuccessful() );
		assertTrue( equip.embed(cam.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "thermographic_vision"), null).wasSuccessful() );
		assertTrue( equip.embed(cam.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "vision_enhancement"), null).wasSuccessful() );
		assertTrue( equip.embed(cam.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "vision_magnification"), null).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "climbing_gear")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> contacts = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "contacts"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "contacts").getChoices().get(0), "3"));
		assertTrue( equip.embed(contacts.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "image_link"), null).wasSuccessful() );
		assertTrue( equip.embed(contacts.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "thermographic_vision"), null).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "jammer"), "direct", null,
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "jammer").getChoices().get(0), "6")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> micro = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "directional_microphone"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "directional_microphone").getChoices().get(0), "3"));
		assertTrue( equip.embed(micro.get(), ItemHook.AUDIO, Shadowrun6Core.getItem(ItemTemplate.class, "select_sound_filter"), null, new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "select_sound_filter").getChoices().get(0).getUUID(), "2")).wasSuccessful() );
		assertTrue( equip.embed(micro.get(), ItemHook.AUDIO, Shadowrun6Core.getItem(ItemTemplate.class, "audio_enhancement"), null).wasSuccessful() );

		SINController sinCtrl = charGen.getSINController();
		SIN sin1 =sinCtrl.createNewSIN("Someone",FakeRating.SUPERFICIALLY_PLAUSIBLE);
		sinCtrl.createNewLicense(sin1, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Concealed Carry License");
		sinCtrl.createNewLicense(sin1, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Cyberware License");
		sinCtrl.createNewLicense(sin1, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Driver's License");
		SIN sin2 =sinCtrl.createNewSIN("Someone",FakeRating.GOOD_MATCH);
		sinCtrl.createNewLicense(sin2, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Concealed Carry License");
		sinCtrl.createNewLicense(sin2, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Cyberware License");
		sinCtrl.createNewLicense(sin2, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Driver's License");
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "flashlight")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "gecko_tape_gloves")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> glasses = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "glasses"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "glasses").getChoices().get(0), "2"));
		assertTrue( equip.embed(glasses.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "image_link"), null).wasSuccessful() );
		assertTrue( equip.embed(glasses.get(), ItemHook.OPTICAL, Shadowrun6Core.getItem(ItemTemplate.class, "vision_enhancement"), null).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "glue_sprayer")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "honda_spirit")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "keycard_copier")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "lockpick_set")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "maglock_passkey"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "maglock_passkey").getChoices().get(0), "3")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> transc = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "micro-transceiver"));
		assertTrue (transc.wasSuccessful());
		equip.increase(transc.get());
		equip.increase(transc.get());
		equip.increase(transc.get());
		equip.increase(transc.get());
		equip.increase(transc.get());
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "monofilament_chainsaw")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "rappelling_gloves")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "respirator"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "respirator").getChoices().get(0), "6")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "sequencer"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "sequencer").getChoices().get(0), "4")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> stealth = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "security_tag"),"stealth_tag",null);
		assertTrue (transc.wasSuccessful());
		for (int i=2; i<=10; i++) equip.increase(stealth.get());
		OperationResult<CarriedItem<ItemTemplate>> stim = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "stim_patch"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "stim_patch").getChoices().get(0), "6"));
		assertTrue (stim.wasSuccessful());
		for (int i=2; i<=5; i++) equip.increase(stim.get());
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "tag_eraser")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> transsys = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "transys_avalon"));
		assertTrue( equip.embed(transsys.get(), ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "subvocal_microphone"), null).wasSuccessful() );
		assertTrue( equip.embed(transsys.get(), ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "trid_projector"), null).wasSuccessful() );
		assertTrue( equip.embed(transsys.get(), ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "trodes"), null).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> tranq = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "tranq_patch"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "tranq_patch").getChoices().get(0), "10"));
		assertTrue (tranq.wasSuccessful());
		for (int i=2; i<=5; i++) equip.increase(tranq.get());
		OperationResult<CarriedItem<ItemTemplate>> ultra = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "handheld_housing"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "handheld_housing").getChoices().get(0), "2"));
//		assertTrue( equip.embed(ultra.get(), ItemHook.SENSOR_HOUSING, Shadowrun6Core.getItem(ItemTemplate.class, "sensor_array"), null, new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "sensor_array").getChoices().get(0), "5")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "white_noise_generator"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "white_noise_generator").getChoices().get(0), "6")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ares_predator_vi")).wasSuccessful() );

		// Lifestyle
		OperationResult<SR6Lifestyle> lifeRes = charGen.getLifestyleController().select(Shadowrun6Core.getItem(LifestyleQuality.class, "middle"));
		assertTrue(lifeRes.wasSuccessful());
		lifeRes.get().setPrimary(true);
		lifeRes.get().setName("Small appartment");
		lifeRes.get().setSIN(model.getSINs().get(0).getUniqueId());

		model.setName("Covert-Ops Specialist");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		// Try to reload it again
		Shadowrun6Core.decode(raw);


		List<CarriedItem> list = model.getCarriedItems().stream()
			.filter(item -> ((ItemTemplate)item.getResolved()).getItemType()==ItemType.CYBERWARE)
			.collect(Collectors.toList());
		for (CarriedItem goo : list) {
			System.out.println("..."+goo.getKey());
		}

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test04Decker() throws Exception {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.C);
		prio.setPriority(PriorityType.METATYPE, Priority.D);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.B);
		prio.setPriority(PriorityType.RESOURCES, Priority.A);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "dwarf");
		charGen.getMetatypeController().select(human);

		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane"));

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "allergy"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(0).getUUID(),"common"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(1).getUUID(),"moderate"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(2).getUUID(),"dairy")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(64, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "analytical_mind"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(61, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "aptitude"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "aptitude").getChoices().get(0).getUUID(),"cracking")
				);
		assertEquals(49, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "dependents"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "dependents").getChoices().get(0).getUUID(),"Parent"));
		assertTrue(res.wasSuccessful());
		assertEquals(53, model.getKarmaFree());
		assertTrue(qualities.increase(res.get()).wasSuccessful());
		assertEquals(57, model.getKarmaFree());
		assertTrue(qualities.increase(res.get()).wasSuccessful());
		assertEquals(61, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "gearhead")).wasSuccessful());
		assertEquals(51, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "hardening")).wasSuccessful());
		assertEquals(41, model.getKarmaFree());

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		assertEquals(12, attribs.getPointsLeft2());
		assertEquals(4, attribs.getPointsLeft());
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 4);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 4);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 6);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 5);
		raiseAttributeTo(ShadowrunAttribute.BODY     , 3);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 2);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 2);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 1);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 1);

		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(24, skills.getPointsLeft());
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("cracking"));
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		skills.increase(sVal.get()); // 5
		skills.increase(sVal.get()); // 6
		skills.increase(sVal.get()); // 7
		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("cracking"));
		skills.select(tmp, Shadowrun6Core.getSkill("cracking").getSpecialization("cybercombat"), false);
		assertEquals(16, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("electronics"));
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		skills.increase(sVal.get()); // 5
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("electronics"));
		skills.select(tmp, Shadowrun6Core.getSkill("electronics").getSpecialization("computer"), false);
		assertEquals(10, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("firearms"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(8, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("perception"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertEquals(4, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("piloting"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertEquals(2, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("stealth"));
		assertEquals(1, skills.getPointsLeft());

		// Knowledge and language
		assertEquals(1, skills.getPointsLeft());
		assertEquals(6, skills.getPointsLeft2());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Data Havens")) );
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Decker Bars") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Hackers") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Matrix Clubs") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Node Security Design") ));
		assertEquals(1, skills.getPointsLeft());
		assertEquals(1, skills.getPointsLeft2());
		assertEquals(4, model.getSkillValues(SkillType.LANGUAGE).get(0).getDistributed());
		model.getSkillValues(SkillType.LANGUAGE).get(0).addDecision(new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), "English"));;
		assertNotNull( skills.select(Shadowrun6Core.getSkill("language"), new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), "Japanese") ));
		assertEquals(1, skills.getPointsLeft());
		assertEquals(0, skills.getPointsLeft2());

		// Contacts
		IContactController contacts = charGen.getContactController();
		assertEquals(12, contacts.getPointsLeft());
		Contact c1 = contacts.createContact().get();
		c1.setTypeName("Bartender");
//		contacts.increaseRating(c1);
		contacts.increaseLoyalty(c1);
		c1.setType(ContactType.STREET);
		Contact c2 = contacts.createContact().get();
		c2.setTypeName("Info Broker");
		contacts.increaseLoyalty(c2);
		contacts.increaseRating(c2);
		c2.setType(ContactType.CRIMINAL);
		Contact c3 = contacts.createContact().get();
		c3.setTypeName("Matrix Ganger");
		contacts.increaseRating(c3);
		c3.setType(ContactType.MATRIX);
		Contact c4 = contacts.createContact().get();
		c4.setTypeName("Electronics Shop Owner");
		contacts.increaseRating(c4);
		c4.setType(ContactType.STREET);
		assertEquals(0, contacts.getPointsLeft());

		// Augmentations
		ISR6EquipmentController equip = charGen.getEquipmentController();
		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
//		assertTrue(equip.increaseConversion());
		OperationResult<CarriedItem<ItemTemplate>> ears = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberears"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "cyberears").getChoices().get(0), "2")
				);
		assertTrue(ears.wasSuccessful());
		assertTrue(equip.embed(ears.get(), ItemHook.CYBEREAR_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "audio_enhancement"), "cyberear",new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());
		assertTrue(equip.embed(ears.get(), ItemHook.CYBEREAR_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "damper"),null,new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());
//		assertTrue(equip.embed(ears.get(), ItemHook.CYBEREAR_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "select_sound_filter"),"cyberear",
//				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
//				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "select_sound_filter").getVariant("cyberear").getChoices().get(0), "2")).wasSuccessful());
//		CarriedItem<ItemTemplate> carrEar = ears.get();
//		assertEquals(3000+4000+500+2250, carrEar.getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());

		OperationResult<CarriedItem<ItemTemplate>> eye = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cybereye"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "cybereye").getChoices().get(0), "4")
				);
		assertTrue(eye.wasSuccessful());
		assertTrue(equip.embed(eye.get(), ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "flare_compensation"), "cybereye",new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());
		assertTrue(equip.embed(eye.get(), ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "low_light_vision"), "cybereye",new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());
		assertTrue(equip.embed(eye.get(), ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "smartlink"), "cybereye",new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());
		assertTrue(equip.embed(eye.get(), ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "thermographic_vision"), "cybereye",new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());
//		assertTrue(equip.embed(eye.get(), ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "vision_enhancement"), "cybereye",new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")).wasSuccessful());

		OperationResult<CarriedItem<ItemTemplate>> jack = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberjack"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "cyberjack").getChoices().get(0), "6")
				);
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "jammer"), "area", null,
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "jammer").getChoices().get(0), "6")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_jacket")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "data_tap")).wasSuccessful() );

		SINController sinCtrl = charGen.getSINController();
		SIN sin =sinCtrl.createNewSIN("Name it", FakeRating.SUPERFICIALLY_PLAUSIBLE);
		sinCtrl.createNewLicense(sin, FakeRating.HIGHLY_PLAUSIBLE, "Concealed Carry");
		sinCtrl.createNewLicense(sin, FakeRating.HIGHLY_PLAUSIBLE, "Cyberdeck");
		sinCtrl.createNewLicense(sin, FakeRating.HIGHLY_PLAUSIBLE, "Cyberware");
		sinCtrl.createNewSIN("Name it", FakeRating.ROUGH_MATCH);
		sinCtrl.createNewSIN("Name it", FakeRating.ANYONE);

		OperationResult<CarriedItem<ItemTemplate>> deck = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "shiawase_cyber6"));
		assertTrue(deck.wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "armor"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "browse"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "configurator"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "edit"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "exploit"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "overclock"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "signal_scrubber"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "stealth"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "toolbox"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "trace"), null).wasSuccessful());
		assertTrue(equip.embed(deck.get(), ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "biometric_reader"), null).wasSuccessful());

		OperationResult<CarriedItem<ItemTemplate>> comm = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "transys_avalon"));
		assertTrue(comm.wasSuccessful());
		assertTrue(equip.embed(comm.get(), ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "biometric_reader"), null).wasSuccessful());

		// To be sure: try ti find matrix devices
		List<CarriedItem<ItemTemplate>> matrixDevices = model.getCarriedItems()
				.stream()
				.filter(ci -> ci.hasFlag(ItemTemplate.FLAG_MATRIX_DEVICE))
				.collect(Collectors.toList());
		matrixDevices.forEach(ci -> System.out.println("SR6ArchetypeTest.test04Decker: Matrix device is "+ci));
		assertTrue("Cyberjack not detected as matrix device",matrixDevices.contains(jack.get()));
		assertTrue("Cyberdeck not detected as matrix device",matrixDevices.contains(deck.get()));
		assertTrue("Commlink not detected as matrix device",matrixDevices.contains(comm.get()));

		assertEquals("Did not correctly find matrix devices", 3, matrixDevices.size());

		OperationResult<CarriedItem<ItemTemplate>> ares = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ares_light_fire_75"));
		assertTrue(ares.wasSuccessful());
		OperationResult<CarriedItem<ItemTemplate>> ammo = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ammo_holdout_light_machine"), null, CarryMode.CARRIED, new Decision(UUID.fromString("b015341d-24dc-42bb-a46b-781a5340e0b3"), "regular") );
		assertTrue(ammo.wasSuccessful());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		ammo = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ammo_heavy_smg"), "cased", CarryMode.CARRIED, new Decision(UUID.fromString("b015341d-24dc-42bb-a46b-781a5340e0b3"), "regular") );

		Shadowrun6Tools.getAmmunitionsFor(model, ares.get());

		//		equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "datajack"));
////		equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "image_link"));
//		OperationResult<CarriedItem<ItemTemplate>> toner = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "muscle_toner"),
//				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "muscle_toner").getChoices().get(0), "1"));
//		OperationResult<CarriedItem<ItemTemplate>> enhan = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "reaction_enhancers"),
//				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "reaction_enhancers").getChoices().get(0), "1"));


		// Lifestyle
		OperationResult<SR6Lifestyle> lifeRes = charGen.getLifestyleController().select(Shadowrun6Core.getItem(LifestyleQuality.class, "middle"));
		assertTrue(lifeRes.wasSuccessful());
		lifeRes.get().setPrimary(true);
		lifeRes.get().setName("To define");
		//lifeRes.get().setSIN(model.getSINs().get(0).getUniqueId());



		model.setName("Decker");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

		// Try to reload it again
		Shadowrun6Core.decode(raw);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test06Rigger() throws Exception {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.C);
		prio.setPriority(PriorityType.METATYPE, Priority.D);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.B);
		prio.setPriority(PriorityType.RESOURCES, Priority.A);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "human");
		charGen.getMetatypeController().select(human);

		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane"));

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "aptitude"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "aptitude").getChoices().get(0).getUUID(),"piloting")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertTrue( qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "bad_rep")).wasSuccessful() );
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "dependents"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "dependents").getChoices().get(0).getUUID(),"Estranged Child"));
		assertTrue(res.wasSuccessful());
		assertTrue( qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "elf_poser")).wasSuccessful() );
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "in_debt"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "dependents").getChoices().get(0).getUUID(),"Estranged Child"));

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		assertEquals(12, attribs.getPointsLeft2());
		assertEquals(4, attribs.getPointsLeft());
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 6);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 4);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 4);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 3);
		raiseAttributeTo(ShadowrunAttribute.BODY     , 2);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 2);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 2);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 1);
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 4);


		// Augmentations
		ISR6EquipmentController equip = charGen.getEquipmentController();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
//		equip.increaseConversion();
		OperationResult<CarriedItem<ItemTemplate>> rig = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "control_rig"),null, CarryMode.IMPLANTED,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"), new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "2"));
		assertTrue(rig.wasSuccessful());
		assertNull("Should not have imagelink", model.getCarriedItem("image_link"));
		OperationResult<CarriedItem<ItemTemplate>> eyesR = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cybereye"),null, CarryMode.IMPLANTED,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"), new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"));
		assertTrue(eyesR.wasSuccessful());
		assertNull("Should not have imagelink as non-auto accessory", model.getCarriedItem("image_link"));
		CarriedItem<ItemTemplate> container = eyesR.get();
		OperationResult<CarriedItem<ItemTemplate>> flare = equip.embed(container, ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "flare_compensation"), "cybereye",
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(flare.wasSuccessful());
		flare = equip.embed(container, ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "low_light_vision"), "cybereye",
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(flare.wasSuccessful());
		flare = equip.embed(container, ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "smartlink"), "cybereye",
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(flare.wasSuccessful());
		flare = equip.embed(container, ItemHook.CYBEREYE_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "thermographic_vision"), "cybereye",
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(flare.wasSuccessful());

		OperationResult<CarriedItem<ItemTemplate>> armR = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm"),"fullarm_obvious", CarryMode.IMPLANTED,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(armR.wasSuccessful());
		container = armR.get();
		flare = equip.embed(container, ItemHook.CYBERLIMB_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "attribute_increase"), null,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "4"),
				new Decision(UUID.fromString("d5c88f1f-eb6f-4057-b9d0-b65c212747e6"), "STRENGTH")
				);
		assertTrue(flare.wasSuccessful());
		flare = equip.embed(container, ItemHook.CYBERLIMB_IMPLANT, Shadowrun6Core.getItem(ItemTemplate.class, "cyber_smg"), null,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertTrue(flare.wasSuccessful());
		flare = equip.embed(flare.get(), ItemHook.IMPLANT_SMG, Shadowrun6Core.getItem(ItemTemplate.class, "uzi_iv"), null);
		assertTrue(flare.wasSuccessful());

		// Gear
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "shop"), new Decision(UUID.fromString("8d33356c-f3d4-4387-a6a2-a9575c449ae7"), "engineering")).wasSuccessful());
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_jacket")).wasSuccessful());
		SINController sinCtrl = charGen.getSINController();
		SIN sin =sinCtrl.createNewSIN("Name it", FakeRating.SUPERFICIALLY_PLAUSIBLE);
		assertNotNull(sin);
		sinCtrl.createNewLicense(sin, FakeRating.SUPERFICIALLY_PLAUSIBLE, "Cyberware license");
		sinCtrl.createNewLicense(sin, FakeRating.SUPERFICIALLY_PLAUSIBLE, "RCC license");
		sin =sinCtrl.createNewSIN("Name it", FakeRating.ROUGH_MATCH);
		assertNotNull(sin);
		sinCtrl.createNewLicense(sin, FakeRating.ROUGH_MATCH, "Cyberware license");
		sinCtrl.createNewLicense(sin, FakeRating.ROUGH_MATCH, "RCC license");
		OperationResult<CarriedItem<ItemTemplate>> rcc = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "proteus_poseidon"),null, CarryMode.CARRIED);
		assertTrue(rcc.wasSuccessful());
		container = rcc.get();
		assertTrue( equip.embed(container, ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "biometric_reader"), null).wasSuccessful() );
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "clearsight"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5")).wasSuccessful() );
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "electronic_warfare"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5")).wasSuccessful() );
		// Commlink
		OperationResult<CarriedItem<ItemTemplate>> comm = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "transys_avalon"),null, CarryMode.CARRIED);
		assertTrue(comm.wasSuccessful());
		container = comm.get();
		assertTrue( equip.embed(container, ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "biometric_reader"), null).wasSuccessful() );

		// Vehicles
		// Drone 1
		OperationResult<CarriedItem<ItemTemplate>> drone1 = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "chrysler-nissan_pursuit_v"),null, CarryMode.CARRIED);
		assertTrue(drone1.wasSuccessful());
		container = drone1.get();
		Possible poss = equip.canBeEmbedded(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "evasion"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "chrysler-nissan_pursuit_v")
				);
		assertTrue(poss.toString(), poss.get());
		OperationResult<CarriedItem<ItemTemplate>> embed =  equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "evasion"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "chrysler-nissan_pursuit_v")
				);
		assertTrue(embed.getError(), embed.wasSuccessful() );
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "maneuvering"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "chrysler-nissan_pursuit_v")
				).wasSuccessful() );
		// Drone 2
		OperationResult<CarriedItem<ItemTemplate>> drone2 = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberspace_designs_dalmatian"),null, CarryMode.CARRIED);
		assertTrue(drone2.wasSuccessful());
		assertTrue( equip.canBeIncreased(drone2.get()).get());
		assertTrue( equip.increase(drone2.get()).wasSuccessful() );
		container = drone2.get();
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "evasion"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "cyberspace_designs_dalmatian")).wasSuccessful() );
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "maneuvering"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "cyberspace_designs_dalmatian")
				).wasSuccessful() );
		// Drone 3
		OperationResult<CarriedItem<ItemTemplate>> drone3 = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberspace_designs_quadrotor"),null, CarryMode.CARRIED);
		assertTrue( equip.increase(drone3.get()).wasSuccessful() );
		assertTrue( equip.increase(drone3.get()).wasSuccessful() );
		assertTrue( equip.increase(drone3.get()).wasSuccessful() );
		container = drone3.get();
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "evasion"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "cyberspace_designs_dalmatian")).wasSuccessful() );
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "maneuvering"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "cyberspace_designs_dalmatian")
				).wasSuccessful() );
		// Van
		assertTrue (equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "gmc_bulldog"),null, CarryMode.CARRIED).wasSuccessful() );
		// Drone 4
		OperationResult<CarriedItem<ItemTemplate>> drone4 = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "gmc_micromachine"),null, CarryMode.CARRIED);
		assertTrue( equip.increase(drone4.get()).wasSuccessful() );
		assertTrue( equip.increase(drone4.get()).wasSuccessful() );
		assertTrue( equip.increase(drone4.get()).wasSuccessful() );
		assertTrue( equip.increase(drone4.get()).wasSuccessful() );
		container = drone4.get();
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "maneuvering"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "gmc_micromachine")
				).wasSuccessful() );
		// Drone 5
		OperationResult<CarriedItem<ItemTemplate>> drone5 = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "gm-nissan_dobermann"),null, CarryMode.CARRIED);
		assertTrue( equip.increase(drone5.get()).wasSuccessful() );
		container = drone5.get();
		assertTrue( equip.embed(container, ItemHook.SOFTWARE, Shadowrun6Core.getItem(ItemTemplate.class, "maneuvering"), null,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "5"),
				new Decision(UUID.fromString("355a3a45-39fc-4376-8667-661c9873dfdb"), "gm-nissan_dobermann")
				).wasSuccessful() );
		// Mirage
		assertTrue (equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "suzuki_mirage"),null, CarryMode.CARRIED).wasSuccessful() );

		model.setName("Rigger");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

		// Try to reload it again
		Shadowrun6Core.decode(raw);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test07StreetSamurai() throws Exception {
		charGen.getRuleController().setRuleValue(Shadowrun6Rules.CHARGEN_NEGATIVE_NUYEN, true);
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.B);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.A);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType troll = Shadowrun6Core.getItem(SR6MetaType.class, "troll");
		charGen.getMetatypeController().canBeSelected(troll);
		charGen.getMetatypeController().select(troll);

		model.setName("Street Samurai");
		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane"));

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 5);
		raiseAttributeTo(ShadowrunAttribute.BODY     , 7);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 3);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 3);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 7);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 1);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 2);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 4);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 2);

		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue> res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "ambidextrous"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(46, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "aptitude"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "aptitude").getChoices().get(0).getUUID(),"close_combat")
				);
		assertEquals(34, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "dependents"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "dependents").getChoices().get(0).getUUID(),"Siblings"));
		assertTrue(res.wasSuccessful());
		assertTrue(qualities.increase(res.get()).wasSuccessful());
		assertEquals(42, model.getKarmaFree());
		assertTrue( qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "high_pain_tolerance")).wasSuccessful() );
		assertEquals(35, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "honorbound"), new Decision(Shadowrun6Core.getItem(SR6Quality.class, "honorbound").getChoices().get(0).getUUID(),"Bushido"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(45, model.getKarmaFree());

		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(16, skills.getPointsLeft());
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("close_combat"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 5
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 6
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 7
		assertEquals(9, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());

		sVal = skills.select(Shadowrun6Core.getSkill("firearms"));
		assertNotNull(sVal);
		assertTrue(sVal.getError(), sVal.isPresent());
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		assertEquals(5, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("perception"));
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		assertEquals(2, skills.getPointsLeft());
		assertEquals(2, skills.getPointsLeft2());

		sVal = skills.select(Shadowrun6Core.getSkill("stealth"));
		skills.increase(sVal.get()); // 2
		assertEquals(45, model.getKarmaFree());
		skills.increase(sVal.get()); // 3
		assertEquals(0, skills.getPointsLeft());
		assertEquals(30, model.getKarmaFree());

		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("close_combat"));
		assertTrue( skills.select(tmp, Shadowrun6Core.getSkill("close_combat").getSpecialization("blades"), false).wasSuccessful() );
		assertEquals(25, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("firearms"));
		assertTrue( skills.select(tmp, Shadowrun6Core.getSkill("firearms").getSpecialization("pistols_heavy"), false).wasSuccessful() );
		assertEquals(20, model.getKarmaFree());

		// Knowledge and language
		assertEquals(2, skills.getPointsLeft2());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Urban Brawl")) );
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Japanese History") ));
		assertEquals(0, skills.getPointsLeft2());
		assertEquals(20, model.getKarmaFree());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Bushido") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("language"), new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), "Or'Zet") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("language"), new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), "Japanese") ));
		assertEquals(11, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft2());
//		assertEquals(4, model.getSkillValues(SkillType.LANGUAGE).get(0).getDistributed());
//		model.getSkillValues(SkillType.LANGUAGE).get(0).addDecision(new Decision(Shadowrun6Core.getSkill("language").getChoices().get(0).getUUID(), "English"));;
		assertEquals(0, skills.getPointsLeft());
		assertEquals(0, skills.getPointsLeft2());

		// Contacts
		IContactController contacts = charGen.getContactController();
		assertEquals(12, contacts.getPointsLeft());
		Contact c1 = contacts.createContact().get();
		c1.setTypeName("Corporate Executive");
		contacts.increaseRating(c1);
//		contacts.increaseLoyalty(c1);
		c1.setType(ContactType.CORPORATE);
		Contact c2 = contacts.createContact().get();
		c2.setTypeName("Martial Arts Instructor");
		//contacts.increaseLoyalty(c2);
		contacts.increaseRating(c2);
		c2.setType(ContactType.STREET);
		Contact c3 = contacts.createContact().get();
		c3.setTypeName("Squatter");
		c3.setType(ContactType.STREET);
		Contact c4 = contacts.createContact().get();
		c4.setTypeName("Urban Brawler");
		contacts.increaseRating(c4);
		contacts.increaseLoyalty(c4);
		c4.setType(ContactType.STREET);
		assertEquals(0, contacts.getPointsLeft());

		// Augmentations
		assertEquals(450000, model.getNuyen());
		ISR6EquipmentController equip = charGen.getEquipmentController();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		assertEquals(472000, model.getNuyen());
		OperationResult<CarriedItem<ItemTemplate>> lacing = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "bone_lacing"), "titanium", CarryMode.IMPLANTED,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		// Ensure cost is correct
		assertEquals("Without troll tax incorrect", 30000, lacing.get().getAsValue(SR6ItemAttribute.PRICE).getDistributed());
		assertEquals("Troll tax incorrect", 33000, lacing.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertEquals("Payed different than expected",439000, model.getNuyen());
		charGen.runProcessors();
		assertEquals("Payed different than expected",439000, model.getNuyen());
		OperationResult<CarriedItem<ItemTemplate>> arm = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "cyberarm"), "fullarm_obvious", CarryMode.IMPLANTED,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"));
		assertEquals("Without troll tax incorrect", 15000, arm.get().getAsValue(SR6ItemAttribute.PRICE).getDistributed());
		assertEquals("Troll tax incorrect", 16500, arm.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertEquals("Payed different than expected",422500, model.getNuyen());
		assertEquals(15, arm.get().getSlot(ItemHook.CYBERLIMB_IMPLANT).getCapacity(), 0);
		assertEquals(15, arm.get().getSlot(ItemHook.CYBERLIMB_IMPLANT).getFreeCapacity(), 0);
		assertTrue(equip.embed(
				arm.get(),
				ItemHook.CYBERLIMB_IMPLANT,
				Shadowrun6Core.getItem(ItemTemplate.class, "attribute_increase"),
				null,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(UUID.fromString("d5c88f1f-eb6f-4057-b9d0-b65c212747e6"), "AGILITY"),
				new Decision(ItemTemplate.UUID_RATING, "3")
				).wasSuccessful()
				);
		assertEquals(12, arm.get().getSlot(ItemHook.CYBERLIMB_IMPLANT).getFreeCapacity(), 0);
		assertTrue(equip.embed(
				arm.get(),
				ItemHook.CYBERLIMB_IMPLANT,
				Shadowrun6Core.getItem(ItemTemplate.class, "attribute_increase"),
				null,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD"),
				new Decision(UUID.fromString("d5c88f1f-eb6f-4057-b9d0-b65c212747e6"), "STRENGTH"),
				new Decision(ItemTemplate.UUID_RATING, "8")
				).wasSuccessful()
				);
		assertEquals(4, arm.get().getSlot(ItemHook.CYBERLIMB_IMPLANT).getFreeCapacity(), 0);
//		assertTrue(equip.embed(
//				arm.get(),
//				ItemHook.CYBERLIMB_IMPLANT,
//				Shadowrun6Core.getItem(ItemTemplate.class, "cyber_holster"),
//				null,
//				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
//				).wasSuccessful()
//				);
		assertTrue(equip.embed(
				arm.get(),
				ItemHook.CYBERLIMB_IMPLANT,
				Shadowrun6Core.getItem(ItemTemplate.class, "cyber_slide"),
				null,
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.embed(
				arm.get(),
				ItemHook.CYBERLIMB_IMPLANT,
				Shadowrun6Core.getItem(ItemTemplate.class, "spurs"),
				"retractable",
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
//		assertTrue(equip.embed(
//				arm.get(),
//				ItemHook.CYBERLIMB_IMPLANT,
//				Shadowrun6Core.getItem(ItemTemplate.class, "smuggling_compartment"),
//				"cyberlimb",
//				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
//				).wasSuccessful()
//				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "dermal_plating"), null, CarryMode.IMPLANTED,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "3"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "muscle_augmentation"), null, CarryMode.IMPLANTED,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "3"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "muscle_toner"), null, CarryMode.IMPLANTED,
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "2"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "platelet_factories"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "reflex_recorder"),
				new Decision(UUID.fromString("3cf6d9ac-dc8c-4d51-8058-b769eac72f50"), "close_combat"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "reflex_recorder"),
				new Decision(UUID.fromString("3cf6d9ac-dc8c-4d51-8058-b769eac72f50"), "firearms"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		assertTrue(equip.select(
				Shadowrun6Core.getItem(ItemTemplate.class, "sleep_regulator"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				).wasSuccessful()
				);
		OperationResult<CarriedItem<ItemTemplate>> wired = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "wired_reflexes"),
				new Decision(UUID.fromString("c2d17c87-1cfe-4355-9877-a20fe09c170d"), "1"),
				new Decision(ItemTemplate.CHOICE_AUGMENTATION_QUALITY, "STANDARD")
				);
		assertEquals("Without troll tax incorrect", 40000, wired.get().getAsValue(SR6ItemAttribute.PRICE).getDistributed());
		assertEquals("Troll tax incorrect", 44000, wired.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		assertEquals("Payed different than expected",129900, model.getNuyen());
		assertTrue(equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ares_roadmaster")).wasSuccessful());
		assertTrue(equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_jacket")).wasSuccessful());

		SINController sinCtrl = charGen.getSINController();
		SIN sin =sinCtrl.createNewSIN("Name it", FakeRating.SUPERFICIALLY_PLAUSIBLE);
		sinCtrl.createNewLicense(sin, FakeRating.HIGHLY_PLAUSIBLE, "Concealed Carry");
		sinCtrl.createNewLicense(sin, FakeRating.HIGHLY_PLAUSIBLE, "Driver");
		sinCtrl.createNewLicense(sin, FakeRating.HIGHLY_PLAUSIBLE, "Cyberware");
		OperationResult<CarriedItem<ItemTemplate>> comm = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "transys_avalon") );
		assertTrue(comm.wasSuccessful());
		assertTrue(equip.embed(comm.get(), ItemHook.ELECTRONIC_ACCESSORY, Shadowrun6Core.getItem(ItemTemplate.class, "biometric_reader"), null).wasSuccessful());
		assertTrue(equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "katana")).wasSuccessful());
//		assertTrue(equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ares_predator_v")).wasSuccessful());

		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.STRENGTH);
		System.out.println("Strength = "+aVal.getDisplayString());
		System.out.println("Strength = "+aVal.getIncomingModifications());
		System.out.println("Strength NAT = "+aVal.getPool().getCalculation(ValueType.NATURAL));
		System.out.println("Strength ART = "+aVal.getPool().getCalculation(ValueType.ARTIFICIAL));
		System.out.println("Strength ALL = "+aVal.getPool().toString());

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

		// Try to reload it again
		Shadowrun6Core.decode(raw);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test08StreetShaman() throws Exception {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.B);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.A);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.E);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "human");
		assertNotNull("Metatype 'human' not found", human);
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);

		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician"));

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		raiseAttributeTo(ShadowrunAttribute.BODY     , 2);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 3);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 2);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 1);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 5);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 2);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 3);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 5);
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 7);
		raiseAttributeTo(ShadowrunAttribute.MAGIC    , 6);

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "allergy"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(0).getUUID(),"common"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(1).getUUID(),"mild"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(2).getUUID(),"Insect Stings")
				);
		assertNotNull(res);
		assertTrue(res.wasSuccessful());
		assertEquals(61, model.getKarmaFree());
		// Honorbound
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "distinctive_style"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(67, model.getKarmaFree());
		assertTrue(qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "focused_concentration")).wasSuccessful());
		assertEquals(55, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "mentor_spirit"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "mentor_spirit").getChoices().get(0).getUUID(),"cat"),
				new Decision(Shadowrun6Core.getItem(MentorSpirit.class, "cat").getChoices().get(0).getUUID(),"athletics")
				);
		assertEquals(45, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "spirit_affinity"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "spirit_affinity").getChoices().get(0).getUUID(),"beast")
				);
		assertEquals(31, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "spirit_bane"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "spirit_bane").getChoices().get(0).getUUID(),"fire")
				);
		assertEquals(43, model.getKarmaFree());

		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(16, skills.getPointsLeft());
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("athletics"));
		sVal = skills.select(Shadowrun6Core.getSkill("close_combat"));
		skills.increase(sVal.get()); // 2
		sVal = skills.select(Shadowrun6Core.getSkill("conjuring"));
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		skills.increase(sVal.get()); // 5
//		assertEquals(11, skills.getPointsLeft());
//		assertEquals(2, skills.getPointsLeft2());

		sVal = skills.select(Shadowrun6Core.getSkill("perception"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2

		sVal = skills.select(Shadowrun6Core.getSkill("sorcery"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 5
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 6
//		assertEquals(5, skills.getPointsLeft());
//		assertEquals(2, skills.getPointsLeft2());

		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("conjuring"));
		skills.select(tmp, Shadowrun6Core.getSkill("conjuring").getSpecialization("summoning"), false);
//		assertEquals(21, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("sorcery"));
		skills.select(tmp, Shadowrun6Core.getSkill("sorcery").getSpecialization("spellcasting"), false);
		assertEquals(33, model.getKarmaFree());
//		assertEquals(6, model.getKarmaFree());
//		assertEquals(0, skills.getPointsLeft());
//		assertEquals(2, skills.getPointsLeft2());

		assertEquals(1, model.getSkillValues(SkillType.LANGUAGE).size());
		assertEquals(4, model.getSkillValues(SkillType.LANGUAGE).get(0).getDistributed());
//		model.getSkillValues(SkillType.LANGUAGE).get(0).setName("English");
		assertTrue(skills.canBeSelected(Shadowrun6Core.getSkill("knowledge")).get());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Dive Bars")) );
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Seattle Shamans") ));
		assertEquals(0, skills.getPointsLeft2());

		// Spells
		SR6SpellController spells = charGen.getSpellController();
		assertEquals(8, ((SR6PrioritySpellGenerator)spells).getFreeSpells());
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "chaos")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "clairaudience")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "darkness")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "fireball")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "heal")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "levitate")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "stunbolt")).wasSuccessful() );
		assertTrue( spells.select(Shadowrun6Core.getItem(SR6Spell.class, "trid_phantasm")).wasSuccessful() );

		// Gear
		ISR6EquipmentController equip = charGen.getEquipmentController();
		Possible poss = equip.canBeSelected(Shadowrun6Core.getItem(ItemTemplate.class, "armor_vest"));
		assertNotNull(poss);
		assertTrue( poss.toString(), poss.get());
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_vest")).wasSuccessful() );
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "meta_link")).wasSuccessful() );
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		equip.increaseConversion();
		assertTrue(  equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "suzuki_mirage")).wasSuccessful() );

		model.setName("Street Shaman");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

		// Try to reload it again
		Shadowrun6Core.decode(raw);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void test09Technomancer() throws Exception {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.B);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.E);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "dwarf");
		charGen.getMetatypeController().canBeSelected(human);
		charGen.getMetatypeController().select(human);

		// Select adept
		charGen.getMagicOrResonanceController().select(Shadowrun6Core.getItem(MagicOrResonanceType.class, "technomancer"));

		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		raiseAttributeTo(ShadowrunAttribute.EDGE     , 3);
		raiseAttributeTo(ShadowrunAttribute.RESONANCE, 6);
		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 5);
		raiseAttributeTo(ShadowrunAttribute.WILLPOWER, 7);
		raiseAttributeTo(ShadowrunAttribute.LOGIC    , 5);
		raiseAttributeTo(ShadowrunAttribute.INTUITION, 5);
		raiseAttributeTo(ShadowrunAttribute.CHARISMA , 5);
		raiseAttributeTo(ShadowrunAttribute.BODY     , 5);
		raiseAttributeTo(ShadowrunAttribute.AGILITY  , 2);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 2);

		// Qualitites
		assertEquals(50, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "allergy"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(0).getUUID(),"common"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(1).getUUID(),"moderate"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "allergy").getChoices().get(2).getUUID(),"Grass")
				);
		assertNotNull(res);
		assertTrue(res.wasSuccessful());
		assertEquals(64, model.getKarmaFree());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "analytical_mind"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "social_stress"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "social_stress").getChoices().get(0).getUUID(),"Social gatherings")
				);
		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "focused_concentration"));
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		res = qualities.increase(res.get());
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertEquals(45, model.getKarmaFree());

		// Skills -------------------------------------------
		SR6SkillGenerator skills = (SR6SkillGenerator) charGen.getSkillController();
		assertEquals(16, skills.getPointsLeft());
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("cracking"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		assertEquals(12, skills.getPointsLeft());
		sVal = skills.select(Shadowrun6Core.getSkill("electronics"));
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		skills.increase(sVal.get()); // 4
		assertEquals(8, skills.getPointsLeft());
		sVal = skills.select(Shadowrun6Core.getSkill("tasking"));
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 2
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 3
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 4
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 5
		assertTrue(skills.increase(sVal.get()).wasSuccessful()); // 6
		assertEquals(2, skills.getPointsLeft());

		sVal = skills.select(Shadowrun6Core.getSkill("con"));
		skills.increase(sVal.get()); // 2
		assertEquals(0, skills.getPointsLeft());
		assertEquals(45, model.getKarmaFree());
		sVal = skills.select(Shadowrun6Core.getSkill("piloting"));
		skills.increase(sVal.get()); // 2
		assertEquals(0, skills.getPointsLeft());
		assertEquals(30, model.getKarmaFree());
		sVal = skills.select(Shadowrun6Core.getSkill("firearms"));
		assertEquals(0, skills.getPointsLeft());
		assertEquals(25, model.getKarmaFree());
		sVal = skills.select(Shadowrun6Core.getSkill("stealth"));
		assertEquals(0, skills.getPointsLeft());
		assertEquals(20, model.getKarmaFree());

		SR6SkillValue tmp = model.getSkillValue(Shadowrun6Core.getSkill("tasking"));
		skills.select(tmp, Shadowrun6Core.getSkill("tasking").getSpecialization("compiling"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(15, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("electronics"));
		skills.select(tmp, Shadowrun6Core.getSkill("electronics").getSpecialization("computer"), false);
		assertEquals(0, skills.getPointsLeft());
		assertEquals(10, model.getKarmaFree());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("piloting"));
		skills.select(tmp, Shadowrun6Core.getSkill("piloting").getSpecialization("ground_craft"), false);
		assertEquals(5, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft());
		tmp = model.getSkillValue(Shadowrun6Core.getSkill("con"));
		skills.select(tmp, Shadowrun6Core.getSkill("con").getSpecialization("acting"), false);
		assertEquals(0, model.getKarmaFree());
		assertEquals(0, skills.getPointsLeft());

		assertEquals(5, skills.getPointsLeft2());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Commlink Design")) );
		assertEquals(4, skills.getPointsLeft2());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Dragons") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Host Design") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Seattle Gangs") ));
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Tacoma Geography") ));
		assertEquals(0, skills.getPointsLeft2());
		assertNotNull( skills.select(Shadowrun6Core.getSkill("knowledge"), new Decision(Shadowrun6Core.getSkill("knowledge").getChoices().get(0).getUUID(), "Technomancer Hangouts") ));

		// Complex Forms-------------------------
		IComplexFormController cforms = charGen.getComplexFormController();
		assertEquals(6, ((SR6PriorityComplexFormGenerator)cforms).getFree());

		ComplexForm cform = Shadowrun6Core.getItem(ComplexForm.class, "diffusion");
		OperationResult<ComplexFormValue> cRes = cforms.select(cform, new Decision(cform.getChoices().get(0).getUUID(), "FIREWALL"));
		assertTrue(cRes.wasSuccessful());
		cform = Shadowrun6Core.getItem(ComplexForm.class, "infusion");
		cRes = cforms.select(cform, new Decision(cform.getChoices().get(0).getUUID(), "ATTACK"));
		assertTrue(cRes.wasSuccessful());
		cRes = cforms.select(cform, new Decision(cform.getChoices().get(0).getUUID(), "SLEAZE"));
		assertTrue(cRes.wasSuccessful());
		cRes = cforms.select(Shadowrun6Core.getItem(ComplexForm.class, "pulse_storm"));
		assertTrue(cRes.wasSuccessful());
		cRes = cforms.select(Shadowrun6Core.getItem(ComplexForm.class, "resonance_spike"));
		assertTrue(cRes.wasSuccessful());
		cRes = cforms.select(Shadowrun6Core.getItem(ComplexForm.class, "stitches"));
		assertTrue(cRes.wasSuccessful());

		// Contacts--------------------------------------
		IContactController contCtrl = charGen.getContactController();
		Contact c1 = contCtrl.createContact().get();
		c1.setTypeName("Bartender");
		contCtrl.increaseLoyalty(c1);
		contCtrl.increaseLoyalty(c1);
		c1.setType(ContactType.STREET);
		Contact c2 = contCtrl.createContact().get();
		c2.setTypeName("Bountry Hunter");
		contCtrl.increaseRating(c2);
		contCtrl.increaseLoyalty(c2);
		contCtrl.increaseLoyalty(c2);
		contCtrl.increaseLoyalty(c2);
		contCtrl.increaseLoyalty(c2);
		c2.setType(ContactType.CORPORATE);
		Contact c3 = contCtrl.createContact().get();
		c3.setTypeName("Cab Driver");
		contCtrl.increaseRating(c3);
		c3.setType(ContactType.STREET);
		Contact c4 = contCtrl.createContact().get();
		c4.setTypeName("Corporate Executive");
		contCtrl.increaseLoyalty(c4);
		contCtrl.increaseLoyalty(c4);
		contCtrl.increaseLoyalty(c4);
		contCtrl.increaseRating(c4);
		contCtrl.increaseRating(c4);
		contCtrl.increaseRating(c4);
		c4.setType(ContactType.CORPORATE);
		Contact c5 = contCtrl.createContact().get();
		c5.setTypeName("Decker");
		contCtrl.increaseRating(c5);
		contCtrl.increaseLoyalty(c5);
		contCtrl.increaseLoyalty(c5);
		c5.setType(ContactType.MATRIX);
		Contact c6 = contCtrl.createContact().get();
		c6.setTypeName("Squatter");
		contCtrl.increaseLoyalty(c6);
		c6.setType(ContactType.CRIMINAL);

		// Lifestyle
		OperationResult<SR6Lifestyle> lifeRes = charGen.getLifestyleController().select(Shadowrun6Core.getItem(LifestyleQuality.class, "low"));
		assertTrue(lifeRes.wasSuccessful());
		lifeRes.get().setPrimary(true);
		lifeRes.get().setName("Room in a shared appartment");
//		lifeRes.get().setSIN(model.getSINs().get(0).getUniqueId());

		// Gear------------------------------
		ISR6EquipmentController equip = charGen.getEquipmentController();
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "jammer"), "area", null,
				new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "jammer").getChoices().get(0), "4")).wasSuccessful() );
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "walther_palm_pistol")).wasSuccessful() );
		OperationResult<CarriedItem<ItemTemplate>> eRes = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "armor_jacket"));
		assertTrue(eRes.wasSuccessful());
		equip.embed(eRes.get(), ItemHook.ARMOR, Shadowrun6Core.getItem(ItemTemplate.class, "electricity_resistance"), null, new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "electricity_resistance").getChoices().get(0), "4"));
		assertTrue( equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "meta_link")).wasSuccessful() );
		eRes = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "stim_patch"),new Decision(Shadowrun6Core.getItem(ItemTemplate.class, "stim_patch").getChoices().get(0), "4"));
		assertTrue(eRes.wasSuccessful());
		assertEquals(100, eRes.get().getAsValue(SR6ItemAttribute.PRICE).getModifiedValue());
		int priceBefore = model.getNuyen();
		equip.increase(eRes.get());
		assertEquals("Increasing count failed",2, eRes.get().getCount());
		int priceAfter = priceBefore - 100;
		assertEquals("Count does not multiply price",priceAfter, model.getNuyen());
		equip.increase(eRes.get());
		assertEquals(3, eRes.get().getCount());

		// Ammo
		priceBefore = model.getNuyen();
		OperationResult<CarriedItem<ItemTemplate>> ammo = equip.select(Shadowrun6Core.getItem(ItemTemplate.class, "ammo_holdout_light_machine"), null, CarryMode.CARRIED, new Decision(UUID.fromString("b015341d-24dc-42bb-a46b-781a5340e0b3"), "regular") );
		assertTrue(ammo.wasSuccessful());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		equip.increase(ammo.get());
		priceAfter = priceBefore - 25;
		assertEquals("Ammo price wrong",priceAfter, model.getNuyen());

		model.setName("Technomancer");

		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);

		List<CheckModification> edgeMods = model.getEdgeModifications(ShadowrunCheckInfluence.values());
		if (!edgeMods.isEmpty()) {
			System.out.println("Edge Generators");
			edgeMods.forEach(mod -> System.out.println("-> "+mod));
		}

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

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testPACK() throws CharacterIOException {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.B);
		prio.setPriority(PriorityType.SKILLS, Priority.E);
		prio.setPriority(PriorityType.RESOURCES, Priority.D);
		assertEquals(50, model.getKarmaFree());

		charGen.getEquipmentController().select(Shadowrun6Core.getItem(ItemTemplate.class, "starterpack"));
		charGen.runProcessors();
		charGen.runProcessors();
		System.out.println("testPACK.finish");

		charGen.finish();
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testNagaKaliAni() throws CharacterIOException {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.D);
		prio.setPriority(PriorityType.SKILLS, Priority.B);
		prio.setPriority(PriorityType.RESOURCES, Priority.E);
		assertEquals(50, model.getKarmaFree());

		SR6MetaType human = Shadowrun6Core.getItem(SR6MetaType.class, "naga");
		assertNotNull("Metatype 'naga' not found", human);
		assertNotNull("No metatype controller found", charGen.getMetatypeController());
		charGen.runProcessors();
		assertTrue("Naga should be selectable",charGen.getMetatypeController().canBeSelected(human));
		System.out.println("----Now selecting Naga--------");
		charGen.getMetatypeController().select(human);
		assertEquals("Naga has only 4 adjustment points on Prio C",4, ((PriorityAttributeGenerator)charGen.getAttributeController()).getPointsLeft());

		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.BODY);
		assertEquals("Expect 8 maximum from Naga", 8, aVal.getMaximum());

		SetItemValue kali = new SetItemValue(Shadowrun6Core.getItem(SetItem.class, "kali_ani"));
		model.setSurgeCollective(kali);
		charGen.runProcessors();
		assertEquals("Expect 8 from Naga and 3 from Shiva Arms", 11, aVal.getMaximum());

		// Qualities
		aVal = model.getAttribute(ShadowrunAttribute.LOGIC);
		PriorityAttributeGenerator attribs = (PriorityAttributeGenerator) charGen.getAttributeController();
		assertTrue(attribs.canBeIncreasedPoints2(aVal).get());
		assertEquals(20, model.getKarmaFree());
		IQualityController qualities = charGen.getQualityController();
		OperationResult<QualityValue>  res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "impaired"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "impaired").getChoices().get(0).getUUID(),"LOGIC")
				);
		assertTrue("Should not fail: "+res,res.wasSuccessful());
		assertTrue(attribs.canBeIncreasedPoints2(aVal).get());

		res = qualities.select(Shadowrun6Core.getItem(SR6Quality.class, "functional_wings"),
				new Decision(Shadowrun6Core.getItem(SR6Quality.class, "functional_wings").getChoices().get(0).getUUID(),"type2")
				);


		raiseAttributeTo(ShadowrunAttribute.STRENGTH , 10);
		raiseAttributeTo(ShadowrunAttribute.BODY , 10);
		raiseAttributeTo(ShadowrunAttribute.AGILITY , 6);
		raiseAttributeTo(ShadowrunAttribute.REACTION , 5);
		raiseAttributeTo(ShadowrunAttribute.INTUITION , 2);

		// Add melee weapon
		charGen.getEquipmentController().select(Shadowrun6Core.getItem(ItemTemplate.class, "katana"));
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testGnomeAttributes() {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.B);
		prio.setPriority(PriorityType.RESOURCES, Priority.D);
		assertEquals(50, model.getKarmaFree());
		charGen.runProcessors();

		SR6MetaType gnome = Shadowrun6Core.getItem(SR6MetaType.class, "gnome");
		assertTrue("Failed selecting metatype",charGen.getMetatypeController().select(gnome));

		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.BODY);
		assertEquals("Expect 4 maximum from Gnome (Not extra reduced by Neoteny)", 4, aVal.getMaximum());
		aVal = model.getAttribute(ShadowrunAttribute.AGILITY);
		assertEquals("Expect 6 maximum from Gnome (Not extra reduced by Neoteny)", 6, aVal.getMaximum());

		assertTrue(charGen.getAttributeController().increase(aVal).wasSuccessful()); // 2
		assertTrue(charGen.getAttributeController().increase(aVal).wasSuccessful()); // 3
	}

}
