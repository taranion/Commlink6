package de.rpgframework.shadowrun6;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.data.GenericCore;
import de.rpgframework.shadowrun.SpellFeature;

/**
 * @author prelle
 *
 */
public class Shadowrun6Core extends GenericCore {

	private static Logger logger = LoggerFactory.getLogger("shadowrun6");

	private static MultiLanguageResourceBundle i18NResources;
	
	
	//-------------------------------------------------------------------
	static {
		i18NResources = new MultiLanguageResourceBundle(Shadowrun6Core.class.getPackageName()+".i18n.core", Locale.ENGLISH, Locale.GERMAN);
	}
	
	//-------------------------------------------------------------------
	/**
	 */
	public Shadowrun6Core() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	public static MultiLanguageResourceBundle getI18nResources() {
		return i18NResources;
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
	public static SR6Skill getSkill(String key) {
		return getItem(SR6Skill.class, key);
	}

	//-------------------------------------------------------------------
	public static SpellFeature getSpellFeature(String key) {
		return getItem(SpellFeature.class, key);
	}

	//-------------------------------------------------------------------
	public static byte[] save(Shadowrun6Character character) {
		try {
			StringWriter out = new StringWriter();
			serializer.write(character, out);
			return out.toString().getBytes(Charset.forName("UTF-8"));
		} catch (IOException e) {
			logger.error("Failed generating XML for char",e);
			StringWriter mess = new StringWriter();
			mess.append("Failed saving character\n\n");
			e.printStackTrace(new PrintWriter(mess));
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, mess.toString());
		}
		return null;
	}

}
