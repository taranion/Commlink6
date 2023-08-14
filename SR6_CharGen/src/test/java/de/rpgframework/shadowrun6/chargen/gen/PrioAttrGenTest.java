package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ApplyWhen;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ResetModifications;

/**
 * @author prelle
 *
 */
public class PrioAttrGenTest {

	private Shadowrun6Character model;
	private SR6PriorityAttributeGenerator ctrl;
	private SR6CharacterGenerator charGen;
	private List<Modification> preMods = new ArrayList<>();

	private transient int karma = 0;

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
		karma = 0;
		charGen = new SR6TestGenerator(model) {
			public void runProcessors() {
				System.getLogger(PrioAttrGenTest.class.getPackageName()).log(Level.DEBUG,"---------------");
				model.setKarmaFree(karma);
				(new ResetModifications(model)).process(preMods);
				ctrl.process(preMods);
			}
		};
		ctrl  = new SR6PriorityAttributeGenerator(charGen);
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(0, ctrl.getPointsLeft());
		assertEquals(0, ctrl.getPointsLeft2());
		assertEquals(0, ctrl.getPointsLeft3());
		assertEquals(0, model.getKarmaFree());
	}

	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill not present in the character
	 */
	@Test
	public void testNonExisting() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.REACTION);

		assertEquals(0, ctrl.getPointsLeft());
		assertEquals(0, ctrl.getPointsLeft2());
		assertEquals(0, ctrl.getPointsLeft3());

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

		assertEquals(1, model.getAttribute(ShadowrunAttribute.BODY    ).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.AGILITY ).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.REACTION).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.STRENGTH).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.LOGIC    ).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.CHARISMA ).getModifiedValue());
		assertEquals(1, model.getAttribute(ShadowrunAttribute.EDGE     ).getModifiedValue());
		assertEquals(0, model.getAttribute(ShadowrunAttribute.MAGIC    ).getModifiedValue());
		assertEquals(0, model.getAttribute(ShadowrunAttribute.RESONANCE).getModifiedValue());
	}

	//-------------------------------------------------------------------
	@Test
	public void testSpecialAdjustmentPoints() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.EDGE);
		assertFalse(ctrl.canBeDecreasedPoints(val).get());
		assertFalse(ctrl.canBeIncreasedPoints(val).get());

		// Add points
		ValueModification mod = new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), 1);
		preMods.add(mod);
		charGen.runProcessors();

		// By default the only special attribute available is EDGE
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.BODY     )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.AGILITY  )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.REACTION )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.STRENGTH )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.WILLPOWER)).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.LOGIC    )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.INTUITION)).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.CHARISMA )).get());
		assertTrue (ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.EDGE     )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.MAGIC    )).get());
		assertFalse(ctrl.canBeIncreasedPoints(model.getAttribute(ShadowrunAttribute.RESONANCE)).get());

		// Increasing MAGIC should not work
		OperationResult<AttributeValue<ShadowrunAttribute>> result = ctrl.increasePoints(model.getAttribute(ShadowrunAttribute.MAGIC));
		assertNotNull(result);
		assertTrue(result.hasError());
		assertEquals(1, ctrl.getPointsLeft());

		// Increasing EDGE should work
		result = ctrl.increasePoints(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(2,result.get().getDistributed());
		assertEquals(0, ctrl.getPointsLeft());

		// Increasing another one should fail (No more free adjustment points)
		result = ctrl.increasePoints(val);
		assertNotNull(result);
		assertTrue(result.hasError());

		// Decrease EDGE again
		result = ctrl.decreasePoints(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(1,result.get().getDistributed());
		assertEquals(1, ctrl.getPointsLeft());

	}

	//-------------------------------------------------------------------
	@Test
	public void testAttributePoints() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.BODY);
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());

		// Add points
		ValueModification mod = new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ATTRIBUTES.name(), 1);
		preMods.add(mod);
		charGen.runProcessors();

		// All primary attributes should be raisable
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.BODY     )).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.AGILITY  )).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.REACTION )).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.STRENGTH )).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.WILLPOWER)).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.LOGIC    )).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.INTUITION)).get());
		assertTrue (ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.CHARISMA )).get());
		assertFalse(ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.EDGE     )).get());
		assertFalse(ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.MAGIC    )).get());
		assertFalse(ctrl.canBeIncreasedPoints2(model.getAttribute(ShadowrunAttribute.RESONANCE)).get());

		// Increasing MAGIC should not work
		OperationResult<AttributeValue<ShadowrunAttribute>> result = ctrl.increasePoints2(model.getAttribute(ShadowrunAttribute.MAGIC));
		assertNotNull(result);
		assertTrue(result.hasError());
		assertEquals(1, ctrl.getPointsLeft2());

		// Increasing EDGE should not work
		result = ctrl.increasePoints2(model.getAttribute(ShadowrunAttribute.EDGE));
		assertNotNull(result);
		assertTrue(result.hasError());
		assertEquals(1, ctrl.getPointsLeft2());

		// Increasing BODY should work
		result = ctrl.increasePoints2(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(2,result.get().getDistributed());
		assertEquals(0, ctrl.getPointsLeft2());

		// Increasing another one should fail (No more free attribute points)
		result = ctrl.increasePoints2(val);
		assertNotNull(result);
		assertTrue(result.hasError());

		// Decrease BODY again
		result = ctrl.decreasePoints2(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(1,result.get().getDistributed());
		assertEquals(1, ctrl.getPointsLeft2());
	}

	//-------------------------------------------------------------------
	@Test
	public void testKarma() {
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.BODY);
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());
		assertEquals(0, ctrl.getPointsLeft3());
		assertEquals(0, model.getKarmaFree());

		// Add points
		karma = 50;
		charGen.runProcessors();
		assertEquals(50, ctrl.getPointsLeft3());
		assertEquals(50, model.getKarmaFree());

		// All primary attributes should be raisable
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.BODY     )).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.AGILITY  )).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.REACTION )).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.STRENGTH )).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.WILLPOWER)).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.LOGIC    )).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.INTUITION)).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.CHARISMA )).get());
		assertTrue (ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.EDGE     )).get());
		assertFalse(ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.MAGIC    )).get());
		assertFalse(ctrl.canBeIncreasedPoints3(model.getAttribute(ShadowrunAttribute.RESONANCE)).get());

		// Increasing MAGIC should not work
		OperationResult<AttributeValue<ShadowrunAttribute>> result = ctrl.increasePoints3(model.getAttribute(ShadowrunAttribute.MAGIC));
		assertNotNull(result);
		assertTrue(result.hasError());
		assertEquals(50, ctrl.getPointsLeft3());

		// Increasing EDGE should work (from 1 to 2 for 10 Karma)
		result = ctrl.increasePoints3(model.getAttribute(ShadowrunAttribute.EDGE));
		assertNotNull(result);
		assertFalse(result.hasError());
		assertEquals(40, ctrl.getPointsLeft3());

		// Increasing BODY should work
		result = ctrl.increasePoints3(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(2,result.get().getDistributed());
		assertEquals(0, ctrl.getPointsLeft2());
		assertEquals(30, ctrl.getPointsLeft3());

		// Increasing BODY again
		result = ctrl.increasePoints3(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertEquals(3,result.get().getDistributed());
		assertEquals(15, ctrl.getPointsLeft3());

		// Decrease BODY again
		result = ctrl.decreasePoints3(val);
		assertNotNull(result);
		assertFalse(result.hasError());
		assertNotNull(result.get());
		assertEquals(2,result.get().getDistributed());
		assertEquals(30, ctrl.getPointsLeft3());
	}

	//-------------------------------------------------------------------
	@Test
	public void testReductionOnExceed() {
		Shadowrun6Character model = charGen.getModel();
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		settings.perAttrib.put(ShadowrunAttribute.BODY, new PerAttributePoints(2,2,1));
		karma = 50;
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), 10));
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ATTRIBUTES.name(), 10));
		preMods.add(new ValueModification(ShadowrunReference.ATTRIBUTE, "BODY", 4, ApplyWhen.ALLCREATE, ValueType.MAX));
		charGen.runProcessors();
		// Although 2 adjustment points are spent, they should not be taken into account
		// since only EGDE may be used for adjustmentpoints on humans
		assertEquals(10, ctrl.getPointsLeft());
		assertEquals(8, ctrl.getPointsLeft2());
		assertEquals(30, ctrl.getPointsLeft3());// Pay 20 karma to raise from 3 to 4, leaves 30
		assertEquals(4, model.getAttribute(ShadowrunAttribute.BODY).getDistributed());

		// One more adjustment point, should exceed maximum
		settings.perAttrib.put(ShadowrunAttribute.BODY, new PerAttributePoints(3,2,1));
		charGen.runProcessors();
		assertEquals(10, ctrl.getPointsLeft());
		assertEquals(8, ctrl.getPointsLeft2());
		assertEquals(30, ctrl.getPointsLeft3());
		assertEquals(4, model.getAttribute(ShadowrunAttribute.BODY).getDistributed());
	}

	//-------------------------------------------------------------------
	@Test
	public void testComplex() {
		Shadowrun6Character model = charGen.getModel();
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		settings.perAttrib.put(ShadowrunAttribute.BODY     , new PerAttributePoints(0,0,1));
		settings.perAttrib.put(ShadowrunAttribute.AGILITY  , new PerAttributePoints(3,2,1));
		settings.perAttrib.put(ShadowrunAttribute.REACTION , new PerAttributePoints(0,2,0));
		settings.perAttrib.put(ShadowrunAttribute.STRENGTH , new PerAttributePoints(0,2,1));
		settings.perAttrib.put(ShadowrunAttribute.WILLPOWER, new PerAttributePoints(0,2,0));
		settings.perAttrib.put(ShadowrunAttribute.LOGIC    , new PerAttributePoints(0,2,0));
		settings.perAttrib.put(ShadowrunAttribute.INTUITION, new PerAttributePoints(0,2,0));
		settings.perAttrib.put(ShadowrunAttribute.CHARISMA , new PerAttributePoints(2,4,1));
		settings.perAttrib.put(ShadowrunAttribute.MAGIC    , new PerAttributePoints(2,0,0));
		settings.perAttrib.put(ShadowrunAttribute.EDGE     , new PerAttributePoints(4,0,0));
		karma = 50;
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), 11));
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ATTRIBUTES.name(), 16));
		preMods.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 4, ApplyWhen.ALLCREATE, ValueType.NATURAL));
		preMods.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.AGILITY.name(), 7, ApplyWhen.ALLCREATE, ValueType.MAX));
		preMods.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.CHARISMA.name(), 8, ApplyWhen.ALLCREATE, ValueType.MAX));
		model.setMagicOrResonanceType(Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician"));
		charGen.runProcessors();
		assertEquals(0, ctrl.getPointsLeft());
		assertEquals(0, ctrl.getPointsLeft2());
		assertEquals(-55, ctrl.getPointsLeft3());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.BODY     ).getDistributed());
		assertEquals(7, model.getAttribute(ShadowrunAttribute.AGILITY  ).getDistributed());
		assertEquals(3, model.getAttribute(ShadowrunAttribute.REACTION ).getDistributed());
		assertEquals(4, model.getAttribute(ShadowrunAttribute.STRENGTH ).getDistributed());
		assertEquals(3, model.getAttribute(ShadowrunAttribute.WILLPOWER).getDistributed());
		assertEquals(3, model.getAttribute(ShadowrunAttribute.LOGIC    ).getDistributed());
		assertEquals(3, model.getAttribute(ShadowrunAttribute.INTUITION).getDistributed());
		assertEquals(8, model.getAttribute(ShadowrunAttribute.CHARISMA ).getDistributed());
		assertEquals(2, model.getAttribute(ShadowrunAttribute.MAGIC    ).getDistributed());
		assertEquals(6, model.getAttribute(ShadowrunAttribute.MAGIC    ).getModifiedValue());
		assertEquals(5, model.getAttribute(ShadowrunAttribute.EDGE     ).getDistributed());
	}

	//-------------------------------------------------------------------
	@Test
	public void testIncreaseEdge6Times() {
		Shadowrun6Character model = charGen.getModel();
		SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
		karma = 50;
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), 10));
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ATTRIBUTES.name(), 10));
		preMods.add(new ValueModification(ShadowrunReference.ATTRIBUTE, "EDGE", 7, ApplyWhen.ALLCREATE, ValueType.MAX));
		charGen.runProcessors();
		OperationResult<AttributeValue<ShadowrunAttribute>> result = ctrl.increase(model.getAttribute(ShadowrunAttribute.EDGE));
		ctrl.increase(model.getAttribute(ShadowrunAttribute.EDGE));
		ctrl.increase(model.getAttribute(ShadowrunAttribute.EDGE));
		ctrl.increase(model.getAttribute(ShadowrunAttribute.EDGE));
		ctrl.increase(model.getAttribute(ShadowrunAttribute.EDGE));
		ctrl.increase(model.getAttribute(ShadowrunAttribute.EDGE));
		assertEquals(7, model.getAttribute(ShadowrunAttribute.EDGE).getDistributed());
	}

	//-------------------------------------------------------------------
	@Test
	public void testReducedRacialMax() {
		Shadowrun6Character model = charGen.getModel();
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ADJUST.name(), 10));
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.ATTRIBUTES.name(), 10));
		charGen.runProcessors();
		assertTrue( ctrl.canBeIncreased(model.getAttribute(ShadowrunAttribute.BODY)).get() );
		assertEquals(6, model.getAttribute(ShadowrunAttribute.BODY).getMaximum());
		preMods.add(new ValueModification(ShadowrunReference.ATTRIBUTE, "BODY", 4, ApplyWhen.ALLCREATE, ValueType.MAX));
		charGen.runProcessors();
		charGen.runProcessors();
		assertEquals(4, model.getAttribute(ShadowrunAttribute.BODY).getMaximum());
		assertTrue( ctrl.canBeIncreased(model.getAttribute(ShadowrunAttribute.BODY)).get() );
		OperationResult<AttributeValue<ShadowrunAttribute>> result = ctrl.increase(model.getAttribute(ShadowrunAttribute.BODY));
		assertTrue(result.wasSuccessful());
		assertEquals(4, model.getAttribute(ShadowrunAttribute.BODY).getMaximum());
		assertTrue( ctrl.canBeIncreased(model.getAttribute(ShadowrunAttribute.BODY)).get() );
		result = ctrl.increase(model.getAttribute(ShadowrunAttribute.BODY));
		assertTrue(result.wasSuccessful());
	}

}
