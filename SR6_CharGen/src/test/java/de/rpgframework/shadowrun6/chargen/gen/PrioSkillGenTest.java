package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendingController;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleValue;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.RuleController;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IContactController;
import de.rpgframework.shadowrun.chargen.charctrl.IFocusController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetamagicOrEchoController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IPANController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.charctrl.IRitualController;
import de.rpgframework.shadowrun.chargen.charctrl.SINController;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.IQualityPathController;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6LifestyleController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySkillGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ResetModifications;

/**
 * @author prelle
 *
 */
public class PrioSkillGenTest {

	private Shadowrun6Character model;
	private SR6PrioritySkillGenerator ctrl;
	private SR6CharacterGenerator charGen;
	private List<Modification> preMods = new ArrayList<>();

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
		model.setCharGenSettings(new SR6PrioritySettings());
		preMods.clear();
		charGen = new SR6TestGenerator(model) {
			public SR6SkillController getSkillController() {
				return ctrl;
			}
			public void runProcessors() {
				System.out.println("---------------");
				(new ResetModifications(model)).process(preMods);
				ctrl.process(preMods);
			}
		};
		ctrl  = new SR6PrioritySkillGenerator(charGen);
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(0, ctrl.getPointsLeft());
		assertEquals(model.getAttribute(ShadowrunAttribute.LOGIC).getDistributed(), ctrl.getPointsLeft2());
		assertEquals(0, ctrl.getPointsLeft3());
		assertEquals(0, model.getKarmaFree());

		assertEquals("There should be no skillvalues except native language", 1, model.getSkillValues().size() );
	}

	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill not present in the character
	 */
	@Test
	public void testNonExisting() {
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		// Increasing or decreasing should not be possible
		assertFalse(ctrl.canBeDecreased(val).get());
		assertFalse(ctrl.canBeIncreased(val).get());
		assertFalse(ctrl.canBeDecreasedPoints(val).get());
		assertFalse(ctrl.canBeIncreasedPoints(val).get());
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());
		assertFalse(ctrl.canBeDecreasedPoints3(val).get());
		assertFalse(ctrl.canBeIncreasedPoints3(val).get());
		// attempting it should fail
		assertFalse(ctrl.decrease(val).wasSuccessful());
		assertFalse(ctrl.increase(val).wasSuccessful());
		assertTrue(ctrl.decreasePoints(val).hasError());
		assertTrue(ctrl.increasePoints(val).hasError());
		assertTrue(ctrl.decreasePoints2(val).hasError());
		assertTrue(ctrl.increasePoints2(val).hasError());
		assertTrue(ctrl.decreasePoints3(val).hasError());
		assertTrue(ctrl.increasePoints3(val).hasError());
	}

	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testSelect() {
		// Inject 1 skill point
		ValueModification mod = new ValueModification(ShadowrunReference.CREATION_POINTS, "SKILLS", 1, ApplyTo.POINTS, null);
		preMods.add(mod);
		charGen.runProcessors();
		assertEquals(1, ctrl.getPointsLeft());

		OperationResult<SR6SkillValue> selected = ctrl.select(Shadowrun6Core.getSkill("athletics"));
		assertNotNull(selected);
		assertFalse(selected.hasError());
		assertTrue(selected.getError().isBlank());
		assertNotNull(selected.get());
		assertEquals(1,selected.get().getModifiedValue());
		assertEquals(1,selected.get().getDistributed());
		assertEquals(0, ctrl.getPointsLeft());
	}

	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill present in the character with a value
	 * of 0 (this should never happen)
	 */
	@Test
	public void testExistingValue0() {
		// Add points
		ValueModification mod = new ValueModification(ShadowrunReference.CREATION_POINTS, "SKILLS", 12, ApplyTo.POINTS, null);
		preMods.add(mod);
		charGen.runProcessors();

		OperationResult<SR6SkillValue> res = ctrl.select(Shadowrun6Core.getSkill("athletics"));
		assertTrue(res.getError(), res.wasSuccessful());
		SR6SkillValue val = res.get();
		assertEquals(1,val.getDistributed());

		assertEquals(11, ctrl.getPointsLeft());
		// Decreasing with points should be possible
		assertTrue(ctrl.canBeDecreasedPoints(val).get());
		// Since 11 points are left, increasing should be possible as well
		assertTrue(ctrl.canBeIncreasedPoints(val).get());

		// Increasing with skill points should work
		OperationResult<SR6SkillValue> result = ctrl.increasePoints(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(2,result.get().getDistributed());
		assertEquals(10, ctrl.getPointsLeft());

		// Decreasing again
		result = ctrl.decreasePoints(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(1,result.get().getDistributed());
		assertEquals(11, ctrl.getPointsLeft());

		// Reach limit of 6
		for (int i=2; i<=6; i++) {
			result = ctrl.increasePoints(val);
			assertNotNull(result);
			assertFalse(result.hasError());
			assertNotNull(result.get());
			assertEquals(i,result.get().getDistributed());
			assertEquals(12-i, ctrl.getPointsLeft());
		}

		// Increasing with skill points should not work anymore, since the maximum is reached
		assertFalse(ctrl.canBeIncreasedPoints(val).get());
		result = ctrl.increasePoints(val);
		assertNotNull(result);
		assertTrue(result.hasError());
		assertNull(result.get());
	}

	//-------------------------------------------------------------------
	@Test
	public void testRoll() {
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.SKILLS.name(), 12));
		charGen.runProcessors();

		ctrl.roll();
		assertEquals("Not all points spent",0, ctrl.getPointsLeft());
		assertEquals(0, ctrl.getPointsLeft3());
		assertEquals(0, model.getKarmaFree());
		assertEquals("Should have spent all skill points",0, ctrl.getPointsLeft2());

		assertTrue("There should be more skillvalues than native language", model.getSkillValues().size()>1 );
	}

}
