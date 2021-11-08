package de.rpgframework.shadowrun6.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.chargen.RuleValue;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.gen.PrioritySkillGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PrioSkillGenTest {
	
	private Shadowrun6Character model;
	private PrioritySkillGenerator ctrl;
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
			public IMetatypeController getMetatypeController() {return null;}
			public IAttributeController getAttributeController() {return null;}
			public IQualityController getQualityController() { return null;}
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
		};
		ctrl  = new PrioritySkillGenerator(charGen);
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill not present in the character
	 */
	@Test
	public void testNonExisting() {
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		// Increasing or decreasing should not be possible
		assertFalse(ctrl.canBeDecreased(val));
		assertFalse(ctrl.canBeIncreased(val));
		// attempting it should fail
		assertFalse(ctrl.decrease(val));
		assertFalse(ctrl.increase(val));
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill present in the character with a value
	 * of 0 (this should never happen)
	 */
	@Test
	public void testExistingValue0() {
		SR6SkillValue val = new SR6SkillValue(Shadowrun6Core.getSkill("athletics"), 0);
		model.addSkillValue(val);
		assertEquals(0, ctrl.getPointsLeftSkills());
		assertEquals(0, ctrl.getPointsLeftInKnowledgeAndLanguage());
		// Increasing or descreasing should not be possible
		// without karma or points
		assertFalse(ctrl.canBeDecreased(val));
		assertFalse(ctrl.canBeIncreased(val));
		// attempting it should fail
		assertFalse(ctrl.decrease(val));
		assertFalse(ctrl.increase(val));
		
		// Add points
		ValueModification mod = new ValueModification(ShadowrunReference.SKILL, "ath", 2, ApplyTo.POINTS, null);
		preMods.add(mod);
		charGen.runProcessors();
		// Should be possible
		assertEquals(2, ctrl.getPointsLeftSkills());
		assertEquals(0, ctrl.getPointsLeftInKnowledgeAndLanguage());
		assertFalse(ctrl.canBeDecreased(val));
		assertTrue(ctrl.canBeIncreased(val));
		// attempting it should fail
		assertFalse(ctrl.decrease(val));
		assertTrue(ctrl.increase(val));
		assertEquals(1, val.getDistributed());
//		assertEquals(1, ctrl.getPointsLeftSkills());
//		assertEquals(0, ctrl.getPointsLeftInKnowledgeAndLanguage());
	}

}
