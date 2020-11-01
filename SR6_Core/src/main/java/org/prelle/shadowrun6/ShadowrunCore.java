package org.prelle.shadowrun6;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.data.GenericCore;

/**
 * @author prelle
 *
 */
public class ShadowrunCore extends GenericCore {

	private static Logger logger = LogManager.getLogger("shadowrun6");

	private static MultiLanguageResourceBundle i18NResources;
	private static MultiLanguageResourceBundle i18NHelpResources;
	
	
	//-------------------------------------------------------------------
	static {
		i18NResources = new MultiLanguageResourceBundle(ShadowrunCore.class.getPackageName()+".i18n.core", Locale.ENGLISH, Locale.GERMAN);
		i18NHelpResources = new MultiLanguageResourceBundle(ShadowrunCore.class.getPackageName()+".i18n.core-help", Locale.ENGLISH, Locale.GERMAN);
	}
	
	//-------------------------------------------------------------------
	/**
	 */
	public ShadowrunCore() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public static MultiLanguageResourceBundle getI18nResources() {
		return i18NResources;
	}

	//-------------------------------------------------------------------
	public static MultiLanguageResourceBundle getI18nHelpResources() {
		return i18NHelpResources;
	}

	//
//	//-------------------------------------------------------------------
//	public static <E extends DataItem> void loadDataItems(Class<? extends List<E>> cls, DataSet plugin, InputStream in) {
//		logger.debug("Load skills (Plugin="+plugin.getID()+")");
//		try {
//			List<E> addSkills = serializer.read(cls, in);
//			logger.info("Successfully loaded "+addSkills.size()+" skills");
//			addSkills.forEach(skill -> skill.assignToDataSet(plugin));
//			addSkills.forEach(skill -> skills.add((Skill) skill));
//		} catch (Exception e) {
//			logger.fatal("Failed deserializing skills",e);
//			System.exit(0);
//			return;
//		}
//	}
//
//	//-------------------------------------------------------------------
//	public static void loadSkills(DataSet plugin, InputStream in) {
//		loadDataItems(SkillList.class, plugin, in);
//		logger.debug("Load skills (Plugin="+plugin.getID()+")");
//		try {
//			SkillList addSkills = serializer.read(SkillList.class, in);
//			logger.info("Successfully loaded "+addSkills.size()+" skills");
//			addSkills.forEach(skill -> skill.assignToDataSet(plugin));
//			addSkills.forEach(skill -> skills.add(skill));
//		} catch (Exception e) {
//			logger.fatal("Failed deserializing skills",e);
//			System.exit(0);
//			return;
//		}
//	}

	//-------------------------------------------------------------------
	public static Skill getSkill(String key) {
		return getItem(Skill.class, key);
	}

	//-------------------------------------------------------------------
	public static SpellFeature getSpellFeature(String key) {
		return getItem(SpellFeature.class, key);
	}

}
