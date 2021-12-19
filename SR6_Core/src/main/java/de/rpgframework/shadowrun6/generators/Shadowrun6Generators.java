package de.rpgframework.shadowrun6.generators;

import de.rpgframework.random.RandomGeneratorRegistry;

/**
 * @author prelle
 *
 */
public class Shadowrun6Generators {
	
	//-------------------------------------------------------------------
	public static void initialize() {
		RandomGeneratorRegistry.register(new SR6NSCGenerator());
	}

}
