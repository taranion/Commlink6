/**
 * 
 */
package de.rpgframework.shadowrun6.chargen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.gen.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

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
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();
//		SpliMoConfigOptions.attachConfigurationTree(new ConfigContainerImpl(Preferences.userNodeForPackage(ArchetypeTest.class), "unittest"));
	}

	//-------------------------------------------------------------------
	@Before
	public void setup() {
		model = new Shadowrun6Character();
		charGen = new PriorityCharacterGenerator();
		charGen.setModel(model);
	}
	
	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		PriorityTableController<Shadowrun6Character,SR6PrioritySettings> prio = charGen.getPriorityController();
		assertEquals(50, model.getKarmaFree());
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
		
		SR6SkillController skills = charGen.getSkillController();
		OperationResult<SR6SkillValue> sVal = skills.select(Shadowrun6Core.getSkill("athletics"));
		assertNotNull(sVal);
		assertTrue(sVal.getError(), sVal.isPresent());
		skills.increase(sVal.get()); // 2
		skills.increase(sVal.get()); // 3
		assertEquals(3,model.getSkillValue(Shadowrun6Core.getSkill("athletics")).getDistributed());
		
		byte[] raw = Shadowrun6Core.save(model);
		String xml = new String(raw);
		System.out.println(xml);
	}

}
