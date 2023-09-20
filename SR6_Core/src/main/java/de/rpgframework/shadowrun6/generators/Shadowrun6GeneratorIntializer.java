package de.rpgframework.shadowrun6.generators;

import java.util.List;

import de.rpgframework.random.GeneratorInitializer;
import de.rpgframework.random.RandomGenerator;

/**
 *
 */
public class Shadowrun6GeneratorIntializer implements GeneratorInitializer {

	private static List<RandomGenerator> generatorsToRegister;

	//-------------------------------------------------------------------
	public Shadowrun6GeneratorIntializer() {
		generatorsToRegister = List.of(
				new SR6NSCGenerator()
		);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.GeneratorInitializer#getGeneratorsToRegister()
	 */
	@Override
	public List<RandomGenerator> getGeneratorsToRegister() {
		return generatorsToRegister;
	}

}
