package de.rpgframework.shadowrun6.chargen;

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
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.IEquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SpellController;
import de.rpgframework.shadowrun6.chargen.gen.PointBuySR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class PointBuySkillGenTest {
	
	private Shadowrun6Character model;
	private PointBuySR6SkillGenerator ctrl;
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
		charGen = new SR6CharacterGenerator() {
			public String getId() { return "dummy";}
			public WizardPageType[] getWizardPages() { return null;}
			public SR6SkillController getSkillController() {
				return ctrl;
			}
			public Shadowrun6Character getModel() {return model;}
			public void addListener(ControllerListener callback) {}
			public void removeListener(ControllerListener callback) {}
			public Collection<ControllerListener> getListener() {
				return null;
			}
			public void fireEvent(ControllerEvent type, Object... param) {}
			public List<ToDoElement> getToDos() {
				return null;
			}
			public void runProcessors() {
				System.out.println("---------------");
				ctrl.process(preMods);
			}
			public boolean save(byte[] data) throws IOException {
				// TODO Auto-generated method stub
				return false;
			}
			public boolean canBeFinished() {return false;}
			public void setModel(Shadowrun6Character model, CharacterHandle handle) {}
			public void finish() {}
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public IMetatypeController getMetatypeController() {return null;}
			public IAttributeController getAttributeController() {return null;}
			public IQualityController getQualityController() { return null;}
			public IEquipmentController getEquipmentController() { return null;}
			public IAdeptPowerController getAdeptPowerController() { return null;}
			public SR6SpellController getSpellController() { return null;}
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
			public <T> T getRule(Rule rule) {return null;}
			public List<RuleValue> getRules() { return new ArrayList<>(); }
			@Override
			public MagicOrResonanceController getMagicOrResonanceController() {return null;}
			@Override
			public <T> RecommendingController<T> getRecommendingControllerFor(T item) {return null;}
		};
		ctrl  = new PointBuySR6SkillGenerator(charGen);
		charGen.runProcessors();
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill not present in the character
	 */
	@Test
	public void testNonExisting() {
		assertEquals(12, ctrl.getPointsLeft());
		assertEquals(0, ctrl.getPointsLeft2());
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		// Increasing or decreasing should not be possible
		assertFalse(ctrl.canBeDecreased(val).get());
		assertFalse(ctrl.canBeIncreased(val).get());
		assertFalse(ctrl.canBeDecreasedPoints(val).get());
		assertFalse(ctrl.canBeIncreasedPoints(val).get());
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());
		// attempting it should fail
		assertFalse(ctrl.decrease(val).wasSuccessful());
		assertFalse(ctrl.increase(val).wasSuccessful());
		assertTrue(ctrl.decreasePoints(val).hasError());
		assertTrue(ctrl.increasePoints(val).hasError());
		assertTrue(ctrl.decreasePoints2(val).hasError());
		assertTrue(ctrl.increasePoints2(val).hasError());
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testSelect() {
		assertEquals(12, ctrl.getPointsLeft());
		OperationResult<SR6SkillValue> selected = ctrl.select(Shadowrun6Core.getSkill("athletics"));
		assertNotNull(selected);
		assertFalse(selected.hasError());
		assertTrue(selected.getError().isBlank());
		assertNotNull(selected.get());
		assertEquals(1,selected.get().getModifiedValue());
		assertEquals(1,selected.get().getDistributed());
		assertEquals(11, ctrl.getPointsLeft());
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
		assertEquals(0, ctrl.getPointsLeft2());
		// Returning Karma by decreasing should not be possible
		assertFalse(ctrl.canBeDecreasedPoints2(val).get());
		// Increasing for Karma should not be possible, since there still are skillpoints left
		assertFalse(ctrl.canBeIncreasedPoints2(val).get());
		
		// attempting it should fail
		assertTrue(ctrl.decreasePoints2(val).hasError());
		assertTrue(ctrl.increasePoints2(val).hasError());
		
		// Now grant Karma
		model.setKarmaFree(50);
		// decreasing should fail, increasing too, since skill points need to be spent first
		assertTrue(ctrl.decreasePoints2(val).hasError());
		assertTrue(ctrl.increasePoints2(val).hasError());
	}

}
