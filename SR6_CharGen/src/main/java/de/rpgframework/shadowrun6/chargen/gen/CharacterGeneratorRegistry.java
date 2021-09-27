package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;

/**
 * @author prelle
 *
 */
public class CharacterGeneratorRegistry {

	private static List<Class<? extends SR6CharacterGenerator>> generators;

	//-------------------------------------------------------------------
	static {
		generators = new ArrayList<Class<? extends SR6CharacterGenerator>>();
		generators.add(PriorityCharacterGenerator.class);
		generators.add(SumToTenCharacterGenerator.class);
		generators.add(KarmaCharacterGenerator.class);
		generators.add(LifeModulesCharacterGenerator.class);
	}

	//-------------------------------------------------------------------
	public static List<Class<? extends SR6CharacterGenerator>> getGenerators() {
		return new ArrayList<>(generators);
	}

}
