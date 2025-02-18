package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IQualityController;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.proc.ResetModifications;

/**
 * @author prelle
 *
 */
public class ShifterTest {

	private Shadowrun6Character model;
	private CommonQualityGenerator qual;
	private SR6ShifterGenerator ctrl;
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
			public void runProcessors() {
				System.out.println("---------------");
				(new ResetModifications(model)).process(List.of());
				(new ResetGenerator(charGen)).process(List.of());
				ctrl.process(new ArrayList<>(preMods));
			}
			public boolean save(byte[] data) throws IOException { return false; }
			public SR6ShifterGenerator getShifterGenerator() { return ctrl; }
			public IQualityController getQualityController() { return qual; }
		};
		ctrl  = new SR6ShifterGenerator(charGen);
		qual  = new CommonQualityGenerator(charGen);
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
//		assertEquals(0, ctrl.getKarmaGain());
//		assertEquals(0, ctrl.getNumberOfQualities());
		assertEquals(50, model.getKarmaFree());

		assertTrue("There should be no qualitiies", model.getQualities().isEmpty() );
	}

	//-------------------------------------------------------------------
	@Test
	public void test1() {
		SR6ShifterGenerator shiftGen = charGen.getShifterGenerator();
		assertNotNull(shiftGen);
		
		SR6Quality shifter = Shadowrun6Core.getItem(SR6Quality.class, "shifter"); 
		SR6Quality armor = Shadowrun6Core.getItem(SR6Quality.class, "shifter_armor"); 
		assertFalse("Should not be possible without shifter base mod", shiftGen.canBeSelected(armor).get() );
		
		OperationResult<QualityValue> result = charGen.getQualityController().select(shifter, new Decision("55a6dda7-7565-4108-9a04-65b7607081d3", "fins"));
		assertTrue(result.isPresent());
		assertTrue("Should not possible with shifter base mod", shiftGen.canBeSelected(armor).get() );
	}

}
