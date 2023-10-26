package de.rpgframework.shadowrun6.chargen.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.chargen.DataSetMode;
import de.rpgframework.genericrpg.data.CommonCharacter.DataSetControl;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.karma.KarmaCharacterGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class FilteredDatasetTest {

	private Shadowrun6Character model;
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
		charGen = new KarmaCharacterGenerator(model,null) ;
		charGen.runProcessors();
	}

	//-------------------------------------------------------------------
	@Test
	public void testMetatypes() {
		int full = Shadowrun6Core.getItemList(SR6MetaType.class).size();

		DataSetControl control = model.getDataSets();

		control.mode = DataSetMode.ALL;
		charGen.runProcessors();
		assertEquals(full, charGen.getMetatypeController().getAvailable().size());

		control.mode = DataSetMode.SELECTED;
		//control.selected.add("CORE");
		charGen.runProcessors();
		assertEquals(5, charGen.getMetatypeController().getAvailable().size());
	}

	//-------------------------------------------------------------------
	@Test
	public void testQualities() {
		int full = Shadowrun6Core.getItemList(SR6Quality.class).size();

		DataSetControl control = model.getDataSets();

		control.mode = DataSetMode.ALL;
		charGen.runProcessors();
//		assertEquals(full, charGen.getQualityController().getAvailable().size());

		control.mode = DataSetMode.SELECTED;
		//control.selected.add("CORE");
		charGen.runProcessors();
		int reduced = charGen.getQualityController().getAvailable().size();
		System.out.println("reduced = "+reduced);
		assertTrue(reduced <full);

		control.mode = DataSetMode.SELECTED;
		control.selected.add("COMPANION");
		charGen.runProcessors();
		System.out.println("reduced2 = "+charGen.getQualityController().getAvailable().size());
		assertTrue(charGen.getQualityController().getAvailable().size() <full);
		assertTrue(charGen.getQualityController().getAvailable().size() >reduced);
	}

}
