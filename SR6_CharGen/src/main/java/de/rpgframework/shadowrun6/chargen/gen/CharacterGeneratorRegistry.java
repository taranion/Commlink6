package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
import de.rpgframework.shadowrun6.chargen.gen.free.FreeCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.karma.KarmaCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.karma.SR6KarmaSettings;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifepathCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
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

	private static Map<Class<? extends SR6CharacterGenerator>, String> generatorNames = new HashMap<>();
	
	//-------------------------------------------------------------------
	static {
		generators = new LinkedHashMap<String,Class<? extends SR6CharacterGenerator>>();
		addGenerator(PriorityCharacterGenerator.class);
		addGenerator(SumToTenCharacterGenerator.class);
		addGenerator(PointBuyCharacterGenerator.class);
		addGenerator(KarmaCharacterGenerator.class);
//		addGenerator(SR6LifepathCharacterGenerator.class);
		addGenerator(FreeCharacterGenerator.class);
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
		
		SR6CharacterGenerator charGen = clazz.getConstructor(Shadowrun6Character.class, CharacterHandle.class).newInstance(model, handle);
		generatorNames.put(clazz, charGen.getName());
		return charGen;
	}

	//---------------------------------------------------------
	private static String getGeneratorName(Shadowrun6Character model, Class<? extends SR6CharacterGenerator> clazz) {
		String generatorName = generatorNames.get(clazz);
		if (generatorName!=null)
			return generatorName;
		try {
			Method getStaticName = clazz.getMethod("getStaticName");
			generatorName = (String) getStaticName.invoke(null);
			if (generatorName!=null) {
				generatorNames.put(clazz, generatorName);
				return generatorName;
			}
			SR6CharacterGenerator charGen = CharacterGeneratorRegistry.getGenerator(model.getCharGenUsed(), model, null);
			generatorNames.put(clazz, charGen.getName());
			return charGen.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return clazz.getSimpleName();
		}
	}

	//---------------------------------------------------------
	public static List<String> getGenerationInfoStrings(Shadowrun6Character model, Locale loc) {
		List<String> ret = new ArrayList<>();

		Class<? extends SR6CharacterGenerator> clazz = null;
		if (model.getCharGenUsed()!=null) {
			try {
				clazz = generators.get(model.getCharGenUsed());
				String generatorName = getGeneratorName(model, clazz);
				ret.add(RES.format("chargeninfo.generator", generatorName));
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
		if (clazz==PointBuyCharacterGenerator.class) {
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			ret.add(RES.format("chargeninfo.pointbuy.adjust", loc, settings.cpBoughtSpecial, settings.cpBoughtSpecial/4));
			ret.add(RES.format("chargeninfo.pointbuy.attrib", loc, settings.cpBoughtAttrib, settings.cpBoughtAttrib/2));
			ret.add(RES.format("chargeninfo.pointbuy.skill", loc, settings.cpToSkills, settings.cpToSkills/2));
			ret.add(RES.format("chargeninfo.pointbuy.ppoints", loc, settings.ppCP , settings.ppKarma));
			int karma=0, cp=0;
			for (Boolean b : settings.perSpellPayedWithCP.values())
				if (b) cp++; else karma++;
			for (Boolean b : settings.perRitualPayedWithCP.values())
				if (b) cp++; else karma++;
			if (model.getMagicOrResonanceType().usesSpells()) {
				ret.add(RES.format("chargeninfo.pointbuy.spells", loc, cp, karma));
			}
			ret.add(RES.format("chargeninfo.pointbuy.resrc", loc, settings.cpToResources, settings.cpToResources*20000));
		}
		if (clazz==KarmaCharacterGenerator.class) {
			SR6KarmaSettings settings = model.getCharGenSettings(SR6KarmaSettings.class);
			ret.add(RES.format("chargeninfo.karma.meta", loc, settings.meta));
			ret.add(RES.format("chargeninfo.karma.mortype", loc, settings.morType));
			ret.add(RES.format("chargeninfo.karma.attrib", loc, settings.attrib));
			ret.add(RES.format("chargeninfo.karma.skill", loc, settings.skills));
			ret.add(RES.format("chargeninfo.karma.spell", loc, settings.spells));
			ret.add(RES.format("chargeninfo.karma.cform", loc, settings.cforms));
			//ret.add(RES.format("chargeninfo.karma.resrc", loc, settings.cpToResources, settings.cpToResources*20000));
		}

		// Karma to nuyen
		CommonSR6GeneratorSettings settings = model.getCharGenSettings(CommonSR6GeneratorSettings.class);
		if (settings!=null) {
			ret.add(RES.format("chargeninfo.common.init", loc, settings.getKarmaForInitiation()));
			ret.add(RES.format("chargeninfo.common.negqual", loc, settings.getKarmaForNegativeQualities()));
			ret.add( RES.format("chargeninfo.conversion.nuyen", loc, settings.getKarmaToNuyen()));
			ret.add( RES.format("chargeninfo.conversion.contacts", loc, settings.getBoughtContactPoints()));
		}

		return ret;
	}

}
