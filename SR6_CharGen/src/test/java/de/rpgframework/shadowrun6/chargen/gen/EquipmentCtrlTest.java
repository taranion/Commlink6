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

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class EquipmentCtrlTest {
	
	private Shadowrun6Character model;
	private ISR6EquipmentController ctrl;
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
			public ISR6EquipmentController ISR6EquipmentController() {
				return ctrl;
			}
			public void runProcessors() {
				System.out.println("---------------");
				ctrl.process(preMods);
			}
		};
		ctrl  = new SR6EquipmentGenerator(charGen);
		charGen.runProcessors();
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testSimple() {
		ItemTemplate jacket = Shadowrun6Core.getItem(ItemTemplate.class, "synthleather_jacket");
		assertNotNull(jacket);
		
		Possible poss = ctrl.canBeSelected(jacket); 
		// Should not be possible due to not enough nuyen
		assertFalse(poss.toString(), poss.get());
		assertEquals(IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, poss.getI18NKey().get(0).getKey());
		
		// Set enough nuyen
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.NUYEN.name(), 300));
		charGen.runProcessors();
		poss = ctrl.canBeSelected(jacket); 
		assertTrue(poss.toString(), poss.get());
		OperationResult<CarriedItem<ItemTemplate>> res = ctrl.select(jacket);
		assertTrue(res.wasSuccessful());
		assertNotNull(res.get());
		// Nuyen should be 0
		assertEquals("Wrong nuyen paid", 0, model.getNuyen());
		assertEquals(0, res.get().getCount());
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testCountable() {
		ItemTemplate jacket = Shadowrun6Core.getItem(ItemTemplate.class, "metal_restraints");
		assertNotNull(jacket);
		
		// Set enough nuyen
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.NUYEN.name(), 60));
		charGen.runProcessors();
		OperationResult<CarriedItem<ItemTemplate>> res = ctrl.select(jacket);
		assertTrue(res.wasSuccessful());
		assertNotNull(res.get());
		// Nuyen should be 40
		assertEquals("Wrong nuyen paid", 40, model.getNuyen());
		assertEquals("Countable not detected",1, res.get().getCount());
		
		CarriedItem<ItemTemplate> countable = res.get();
		Possible poss = ctrl.canBeIncreased(countable); 
		assertTrue(poss.get());		
		poss = ctrl.canBeDecreased(countable);
		assertFalse(poss.get());
//		assertEquals(IRejectReasons.IMPOSS_MIN_LEVEL_REACHED, poss.getI18NKey().get(0).getKey());
		ctrl.increase(countable);
		assertEquals(2, res.get().getCount());
		assertEquals("Wrong nuyen paid", 20, model.getNuyen());
		ctrl.increase(countable);
		assertEquals("Wrong nuyen paid", 0, model.getNuyen());
		assertEquals(3, res.get().getCount());

		poss = ctrl.canBeIncreased(countable); 
		// Should not be possible due to not enough nuyen
		assertFalse("Increasing above Nuyen limit not detected", poss.get());
		assertEquals(IRejectReasons.IMPOSS_NOT_ENOUGH_NUYEN, poss.getI18NKey().get(0).getKey());
		poss = ctrl.canBeDecreased(countable);
		assertTrue(poss.get());
		ctrl.decrease(countable);
		assertEquals(2, res.get().getCount());
		assertEquals("Wrong nuyen paid", 20, model.getNuyen());
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testEmbedding() {
		// Set enough nuyen
		preMods.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.NUYEN.name(), 600));
		charGen.runProcessors();
		OperationResult<CarriedItem<ItemTemplate>> res = ctrl.select(Shadowrun6Core.getItem(ItemTemplate.class, "ares_light_fire_70"));
		assertTrue(res.wasSuccessful());
		CarriedItem<ItemTemplate> container = res.get();
		assertEquals("Wrong nuyen paid", 250, model.getNuyen());
		
		assertNotNull(container.getSlot(ItemHook.TOP));
		assertNull(container.getSlot(ItemHook.UNDER));
		assertTrue(container.getSlot(ItemHook.TOP).getAllEmbeddedItems().isEmpty());
		
		ItemTemplate peri = Shadowrun6Core.getItem(ItemTemplate.class, "periscope");
		ItemTemplate bipod = Shadowrun6Core.getItem(ItemTemplate.class, "bipod");
		assertNotNull(peri);
		
		Possible poss = ctrl.canBeSelected(peri); 
		// Should not be possible due to no slot
		assertFalse(poss.toString(), poss.get());
		assertEquals(IRejectReasons.IMPOSS_INVALID_CARRYMODE, poss.getI18NKey().get(0).getKey());

		// Try embedding
		poss = ctrl.canBeEmbedded(container, ItemHook.TOP, peri, null);
		assertTrue(poss.toString(), poss.get());
		// Embedding in a non existing slot
		poss = ctrl.canBeEmbedded(container, ItemHook.UNDER, peri, null);
		assertFalse(poss.toString(), poss.get());
		assertEquals(IRejectReasons.IMPOSS_NOT_EMBEDDABLE, poss.getI18NKey().get(0).getKey());
		// Embedding something in an existing slot, not wanted by embedded item
		poss = ctrl.canBeEmbedded(container, ItemHook.TOP, bipod, null);
		assertFalse(poss.toString(), poss.get());
		assertEquals(IRejectReasons.IMPOSS_NOT_EMBEDDABLE, poss.getI18NKey().get(0).getKey());
		
		res = ctrl.embed(container, ItemHook.TOP, peri, null);
		assertTrue(res.wasSuccessful());
		assertNotNull(res.get());
		// Nuyen should be 250 - 70
		assertEquals("Wrong nuyen paid", 180, model.getNuyen());
		
		assertFalse("Item not in slot after embedding", container.getSlot(ItemHook.TOP).getAllEmbeddedItems().isEmpty());
		
		
	}
}
