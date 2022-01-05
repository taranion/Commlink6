package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.management.RuntimeErrorException;

import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;

/**
 * @author prelle
 *
 */
public class CharacterGeneratorRegistry {

	private static Map<String,Class<? extends SR6CharacterGenerator>> generators;

	//-------------------------------------------------------------------
	static {
		generators = new HashMap<String,Class<? extends SR6CharacterGenerator>>();
		addGenerator(PriorityCharacterGenerator.class);
		addGenerator(SumToTenCharacterGenerator.class);
		addGenerator(PointBuyCharacterGenerator.class);
		addGenerator(LifeModulesCharacterGenerator.class);
	}

	//-------------------------------------------------------------------
	private static void addGenerator(Class<? extends SR6CharacterGenerator> clazz) {
		GeneratorId anno = clazz.getAnnotation(GeneratorId.class);
		if (anno==null) 
			throw new RuntimeException(clazz+" needs a @GeneratorId");
		generators.put(anno.value(), clazz);
	}

	//-------------------------------------------------------------------
	public static List<Class<? extends SR6CharacterGenerator>> getGenerators() {
		return new ArrayList<>(generators.values());
	}

	//-------------------------------------------------------------------
	public static SR6CharacterGenerator getGenerator(String id) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException  {
		
		Class<? extends SR6CharacterGenerator> clazz = generators.get(id);
		if (clazz==null)
			throw new NoSuchElementException("Unknown generator: "+id);
		return clazz.getConstructor().newInstance();
	}

}
