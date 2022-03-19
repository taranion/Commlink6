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
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IAdeptPowerController;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IComplexFormController;
import de.rpgframework.shadowrun.chargen.charctrl.IContactController;
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
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PrioritySR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.ResetGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PrioQualGenTest {
	
	private Shadowrun6Character model;
	private CommonQualityGenerator ctrl;
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
		charGen = new SR6CharacterGenerator() {
			public String getId() { return "dummy";}
			public WizardPageType[] getWizardPages() { return null;}
			public SR6SkillController getSkillController() {
				return null;
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
				(new ResetGenerator(charGen)).process(List.of());
				ctrl.process(new ArrayList<>(preMods));
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
			public IQualityController getQualityController() { return ctrl;}
			public IEquipmentController getEquipmentController() { return null;}
			public IAdeptPowerController getAdeptPowerController() { return null;}
			public SR6SpellController getSpellController() { return null;}
			public IComplexFormController getComplexFormController() { return null;}
			public IContactController getContactController() { return null;}
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
			public RuleValue getRule(Rule rule) {return null;}
			public List<RuleValue> getRules() { return new ArrayList<>(); }
			@Override
			public MagicOrResonanceController getMagicOrResonanceController() {return null;}
			@Override
			public <T> RecommendingController<T> getRecommendingControllerFor(T item) {return null;}
		};
		ctrl  = new CommonQualityGenerator(charGen);
		charGen.runProcessors();
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(0, ctrl.getKarmaGain());
		assertEquals(0, ctrl.getNumberOfQualities());
		assertEquals(50, model.getKarmaFree());
		
		assertTrue("There should be no qualitiies", model.getQualities().isEmpty() );
	}

	//-------------------------------------------------------------------
	/**
	 * Test to increase or decrease a skill not present in the character
	 */
	@Test
	public void testNonExisting() {
		QualityValue val = new QualityValue(Shadowrun6Core.getItem(Quality.class,"built_tough"), 0);
		// Increasing or decreasing should not be possible
		assertFalse(ctrl.canBeDecreased(val).get());
		assertFalse(ctrl.canBeIncreased(val).get());
		// attempting it should fail
		assertFalse(ctrl.decrease(val).wasSuccessful());
		assertFalse(ctrl.increase(val).wasSuccessful());
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testSelect() {
		// Inject 1 skill point
//		ValueModification mod = new ValueModification(ShadowrunReference.QUALITY, "XXXX", 1, ApplyTo.POINTS, null);
//		preMods.add(mod);
//		charGen.runProcessors();
//		assertEquals(1, ctrl.getPointsLeft());
		
		OperationResult<QualityValue> selected = ctrl.select(Shadowrun6Core.getItem(Quality.class,"built_tough"));
		assertNotNull(selected);
		assertFalse(selected.hasError());
		assertTrue(selected.getError().isBlank());
		assertNotNull(selected);
		assertEquals("Levels not detected",1,selected.get().getModifiedValue());
		assertEquals(1,selected.get().getDistributed());
		assertEquals(46, model.getKarmaFree());
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testSelectDetectMax6() {
		charGen.runProcessors();
		
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"sinner"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"ork_poser"));
		assertEquals(2,ctrl.getNumberOfQualities());
		assertEquals(64, model.getKarmaFree());
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"built_tough"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"dermal_deposits"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"high_pain_tolerance"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"analytical_mind"));
		assertEquals(6,ctrl.getNumberOfQualities());
		OperationResult<QualityValue> selected = ctrl.select(Shadowrun6Core.getItem(Quality.class,"catlike"));
		assertNotNull(selected);
		assertFalse(selected.toString(), selected.wasSuccessful());
		assertFalse(selected.getError().isBlank());
	}
	
	//-------------------------------------------------------------------
	/**
	 * Test if a selection is successful when enough skill points are present
	 */
	@Test
	public void testSelectDetectMax6WithMods() {
		preMods.add( new ValueModification(ShadowrunReference.QUALITY, "built_tough", 2) );
		charGen.runProcessors();
		
		assertEquals(0,ctrl.getNumberOfQualities());
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"sinner"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"ork_poser"));
		assertEquals(2,ctrl.getNumberOfQualities());
		assertEquals(64, model.getKarmaFree());
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"dermal_deposits"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"high_pain_tolerance"));
		ctrl.select(Shadowrun6Core.getItem(Quality.class,"analytical_mind"));
		assertEquals(5,ctrl.getNumberOfQualities());
		OperationResult<QualityValue> selected = ctrl.increase(model.getQuality("built_tough"));
		assertNotNull(selected);
		assertTrue(selected.toString(), selected.wasSuccessful());
		assertTrue(selected.getError().isBlank());
		assertEquals("Did not detect quality that has also points distributed",6,ctrl.getNumberOfQualities());
	}

}
