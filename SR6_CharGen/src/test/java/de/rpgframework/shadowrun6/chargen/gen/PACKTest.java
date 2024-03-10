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
public class PACKTest {

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
	public void testDeckerPACK() throws CharacterIOException {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.A);
		assertEquals(50, model.getKarmaFree());

		OperationResult<CarriedItem<ItemTemplate>> result = charGen.getEquipmentController().select(Shadowrun6Core.getItem(ItemTemplate.class, "decker"));
		assertTrue(result.wasSuccessful());
		System.out.println("\n");
//		charGen.runProcessors();
//		charGen.runProcessors();
//		charGen.runProcessors();
//		charGen.runProcessors();
		System.out.println("testDeckerPACK: "+model.getCarriedItems().size());
		assertEquals(40, model.getCarriedItems().size());
		List<CarriedItem<ItemTemplate>> nonVirtual = model.getCarriedItems().stream().filter(ci -> (ci.getInjectedBy()==null && ci.getCarryMode()!=CarryMode.VIRTUAL)).toList();
		assertEquals(1, nonVirtual.size());

		charGen.finish();
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

	//-------------------------------------------------------------------
	@Test
	public void testHackerPACK() throws CharacterIOException {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		prio.setPriority(PriorityType.ATTRIBUTE, Priority.A);
		prio.setPriority(PriorityType.METATYPE, Priority.C);
		prio.setPriority(PriorityType.MAGIC, Priority.E);
		prio.setPriority(PriorityType.SKILLS, Priority.D);
		prio.setPriority(PriorityType.RESOURCES, Priority.A);
		assertEquals(50, model.getKarmaFree());

		OperationResult<CarriedItem<ItemTemplate>> result = charGen.getEquipmentController().select(Shadowrun6Core.getItem(ItemTemplate.class, "pack_hacker_f"));
		assertTrue(result.wasSuccessful());
		System.out.println("\n");
		charGen.runProcessors();
//		charGen.runProcessors();
//		charGen.runProcessors();
//		charGen.runProcessors();
		System.out.println("testHackerPACK: "+model.getCarriedItems().size());
		for (CarriedItem ci : model.getCarriedItems()) {
			System.out.println("--> "+ci +" = "+ci.getCarryMode()+" from "+ci.getInjectedBy()+" / "+ci.getIncomingModifications());
		}
		assertEquals(8, model.getCarriedItems().size());
		List<CarriedItem<ItemTemplate>> nonVirtual = model.getCarriedItems().stream().filter(ci -> (ci.getInjectedBy()==null && ci.getCarryMode()!=CarryMode.VIRTUAL)).toList();
		assertEquals(1, nonVirtual.size());
//		System.exit(1);

		charGen.finish();
		byte[] raw = Shadowrun6Core.encode(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

}
