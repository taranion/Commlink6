package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SumToTenCharacterGenerator;

/**
 * @author prelle
 *
 */
public class CharacterGeneratorRegistry {

	private static Map<String,Class<? extends SR6CharacterGenerator>> generators;
	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(CharacterGeneratorRegistry.class.getName(), Locale.ENGLISH, Locale.GERMAN);;

	//-------------------------------------------------------------------
	static {
		generators = new LinkedHashMap<String,Class<? extends SR6CharacterGenerator>>();
		addGenerator(PriorityCharacterGenerator.class);
		addGenerator(SumToTenCharacterGenerator.class);
		addGenerator(PointBuyCharacterGenerator.class);
//		addGenerator(LifePathCharacterGenerator.class);
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
	public static SR6CharacterGenerator getGenerator(String id, Shadowrun6Character model, CharacterHandle handle) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException  {
		
		Class<? extends SR6CharacterGenerator> clazz = generators.get(id);
		if (clazz==null)
			throw new NoSuchElementException("Unknown generator: "+id);
		return clazz.getConstructor(Shadowrun6Character.class, CharacterHandle.class).newInstance(model, handle);
	}

	//---------------------------------------------------------
	public static List<String> getGenerationInfoStrings(Shadowrun6Character model, Locale loc) {
		List<String> ret = new ArrayList<>();
		
		// Character Generator
		SR6CharacterGenerator charGen = null;
		Class<? extends SR6CharacterGenerator> clazz = null;
		if (model.getCharGenUsed()!=null) {
			try {
				clazz = generators.get(model.getCharGenUsed());
				charGen = CharacterGeneratorRegistry.getGenerator(model.getCharGenUsed(), model, null);
				ret.add(RES.format("chargeninfo.generator", charGen.getName()));
			} catch (Exception e) {
				ret.add("Error instantiating "+generators.get(model.getCharGenUsed())+": "+e);
			}
		} else
			ret.add(RES.getString("chargeninfo.no_generator", loc));
		
		// Depending on rule system
		if (clazz==PriorityCharacterGenerator.class || clazz==SumToTenCharacterGenerator.class) {
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			List<String> prios = new ArrayList<>();
			prios.add(RES.format("chargeninfo.prio.metatype", loc, settings.priorities.get(PriorityType.METATYPE)));
			prios.add(RES.format("chargeninfo.prio.attribute", loc, settings.priorities.get(PriorityType.ATTRIBUTE)));
			prios.add(RES.format("chargeninfo.prio.magic"  , loc, settings.priorities.get(PriorityType.MAGIC)));
			prios.add(RES.format("chargeninfo.prio.skills", loc, settings.priorities.get(PriorityType.SKILLS)));
			prios.add(RES.format("chargeninfo.prio.resources", loc, settings.priorities.get(PriorityType.RESOURCES)));
			ret.add( RES.getString("chargeninfo.prio", loc)+": "+String.join(", ", prios));
			if (model.getMagicOrResonanceType().usesSpells() && model.getMagicOrResonanceType().usesPowers()) {
				ret.add( RES.format("chargeninfo.prio.mysadpp", loc, settings.getMagicForPP()));
			}
		}
		
		// Karma to nuyen
		CommonSR6GeneratorSettings settings = model.getCharGenSettings(CommonSR6GeneratorSettings.class);
		if (settings!=null) {
			ret.add( RES.format("chargeninfo.conversion.nuyen", loc, settings.getKarmaToNuyen()));
			ret.add( RES.format("chargeninfo.conversion.contacts", loc, settings.getBoughtContactPoints()));
		}
		
		return ret;
	}

}
