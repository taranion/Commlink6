package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class PointBuySkillGenTest {

	private Shadowrun6Character model;
	private SR6PointBuySkillGenerator ctrl;
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
		model.setCharGenSettings(new SR6PointBuySettings());
		preMods.clear();
		charGen = new SR6TestGenerator(model) {
			public SR6SkillController getSkillController() {
				return ctrl;
			}
			public void runProcessors() {
				System.out.println("---------------");
				model.getCharGenSettings(SR6PointBuySettings.class).characterPoints = 100;
				ctrl.process(preMods);
			}
		};
		ctrl  = new SR6PointBuySkillGenerator(charGen);
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(12, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		assertEquals(0, model.getKarmaFree());
	}

	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill not present in the character
	 */
	@Test
	public void testNonExisting() {
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		// Decreasing should not be possible
		assertFalse(ctrl.canBeDecreased(val).get());
		assertFalse(ctrl.canBeDecreasedPoints(val).get());
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		// Increasing should select
		assertTrue(ctrl.canBeIncreased(val).get());
		assertTrue(ctrl.canBeIncreasedPoints(val).get());
		// But not with Karma, since there is none
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());
		// attempting it should fail
		assertFalse(ctrl.decrease(val).wasSuccessful());
		assertTrue(ctrl.decreasePoints(val).hasError());
		assertTrue(ctrl.decreasePoints2(val).hasError());
//		assertTrue(ctrl.increase(val).wasSuccessful());
//		assertFalse(ctrl.increasePoints(val).hasError());
//		assertFalse(ctrl.increasePoints2(val).hasError());
	}

	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testSelect() {
		model.setKarmaFree(50);
		assertEquals(12, ctrl.getPointsLeft());
		OperationResult<SR6SkillValue> selected = ctrl.select(Shadowrun6Core.getSkill("athletics"));
		assertNotNull(selected);
		assertFalse(selected.hasError());
		assertTrue(selected.getError().isBlank());
		assertNotNull(selected.get());
		assertEquals(1,selected.get().getModifiedValue());
		assertEquals(1,selected.get().getDistributed());

		assertEquals(11, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		assertEquals(50, model.getKarmaFree());
		assertEquals(1, ctrl.getPoints(selected.get()));
		assertEquals(0, ctrl.getPoints2(selected.get()));
	}

	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testDeselect() {
		OperationResult<SR6SkillValue> selected = ctrl.select(Shadowrun6Core.getSkill("athletics"));
		assertTrue(selected.wasSuccessful());

		assertTrue( ctrl.canBeDeselected(selected.get()).get() );
		assertTrue( ctrl.deselect(selected.get()) );
	}

	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testIncreaseSelect1() {
		model.setKarmaFree(50);
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		OperationResult<SR6SkillValue> selected = ctrl.increasePoints(val);
		assertNotNull(selected);
		assertFalse(selected.hasError());
		assertTrue(selected.getError().isBlank());
		assertNotNull(selected.get());
		assertEquals(1,selected.get().getModifiedValue());
		assertEquals(1,selected.get().getDistributed());
		assertEquals(11, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		assertEquals(50, model.getKarmaFree());
		assertEquals(1, ctrl.getPoints(val));
		assertEquals(0, ctrl.getPoints2(val));
	}

	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testDecreaseSelect1() {
		model.setKarmaFree(50);
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		OperationResult<SR6SkillValue> selected = ctrl.increasePoints(val);
		assertEquals(11, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		assertEquals(50, model.getKarmaFree());
		assertEquals(1, ctrl.getPoints(val));
		assertEquals(0, ctrl.getPoints2(val));

		val = selected.get();
		assertTrue ( ctrl.canBeDecreased(val).get() );
		assertTrue ( ctrl.canBeDecreasedPoints(val).get() );
		assertFalse( ctrl.canBeDecreasedPoints2(val).get() );

		assertFalse( ctrl.decreasePoints2(val).wasSuccessful() );
		selected = ctrl.decreasePoints(val);
		assertTrue (selected.wasSuccessful());
		assertEquals(12, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		assertEquals(50, model.getKarmaFree());
		assertEquals(0, ctrl.getPoints(val));
		assertEquals(0, ctrl.getPoints2(val));
	}

	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough Karma is present
	 */
	@Test
	public void testIncreaseSelect2() {
		model.setKarmaFree(50);
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		OperationResult<SR6SkillValue> selected = ctrl.increasePoints2(val);
		assertNotNull(selected);
		assertFalse(selected.hasError());
		assertTrue(selected.getError().isBlank());
		assertNotNull(selected.get());
		assertEquals(1,selected.get().getModifiedValue());
		assertEquals(1,selected.get().getDistributed());
		assertEquals(12, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		assertEquals(45, model.getKarmaFree());
		assertEquals(0, ctrl.getPoints(val));
		assertEquals(1, ctrl.getPoints2(val));
	}

	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill present in the character with a value
	 * of 0 (this should never happen)
	 */
	@Test
	public void testExistingWithSkillPoints() {
		SR6SkillValue val = ctrl.select(Shadowrun6Core.getSkill("athletics")).get();
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
			assertEquals(List.of(), result.getMessages());
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
	/**
	 * Test to increase or decrease a skill present in the character with a value
	 * of 0 (this should never happen)
	 */
	@Test
	public void testExistingWithKarma() {
		SR6SkillValue val = ctrl.select(Shadowrun6Core.getSkill("athletics")).get();

		assertEquals(11, ctrl.getPointsLeft());
		assertEquals(20, ctrl.getPointsLeft2());
		// Returning Karma by decreasing should not be possible
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		// Increasing for Karma should not be possible without Karma
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());

		// attempting it should fail
		assertTrue(ctrl.decreasePoints2(val).hasError());
		assertTrue(ctrl.increasePoints2(val).hasError());

		// Now grant Karma
		model.setKarmaFree(50);
		assertTrue(ctrl.canBeIncreasedPoints2(val).get());
		// decreasing should fail, increasing too, since skill points need to be spent first
		assertTrue(ctrl.decreasePoints2(val).hasError());
		assertFalse(ctrl.increasePoints2(val).hasError());
	}

}
