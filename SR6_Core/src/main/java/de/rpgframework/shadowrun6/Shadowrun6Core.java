package de.rpgframework.shadowrun6;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterIOException;
import de.rpgframework.character.CharacterIOException.ErrorCode;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.GenericCore;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityTable;
import de.rpgframework.shadowrun.PriorityTableEntry;
import de.rpgframework.shadowrun.PriorityTableEntryList;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.SpellFeature;

/**
 * @author prelle
 *
 */
public class Shadowrun6Core extends GenericCore {

	private static Logger logger = System.getLogger("shadowrun6");

	private static MultiLanguageResourceBundle i18NResources;
	
	private static PriorityTable prioTable;
	
	//-------------------------------------------------------------------
	static {
		i18NResources = new MultiLanguageResourceBundle(Shadowrun6Core.class.getPackageName()+".i18n.core", Locale.ENGLISH, Locale.GERMAN);
		prioTable   = new PriorityTable();
	}

	//-------------------------------------------------------------------
	public static MultiLanguageResourceBundle getI18nResources() {
		return i18NResources;
	}

	//-------------------------------------------------------------------
	public static void loadPriorityTableEntries(DataSet plugin, InputStream in) {
		logger.log(Level.DEBUG, "Load priority table entries (Plugin="+plugin.getID()+")");
		try {
			PriorityTableEntryList toAdd = (PriorityTableEntryList)serializer.read(PriorityTableEntryList.class, in);
			logger.log(Level.INFO, "Successfully loaded "+toAdd.size()+" priority table entries");

			// Set translation
			for (PriorityTableEntry tmp : toAdd) {
//				tmp.setResourceBundle(resrc);
//				tmp.setHelpResourceBundle(helpResources);
//				tmp.setPlugin(plugin);
//				if (logger.isDebugEnabled())
//					logger.log(Level.DEBUG, "* "+tmp.getName());

				PriorityTableEntry mergeTo = prioTable.get(tmp.getType()).get(tmp.getPriority());
				mergeTo.mergeFrom(tmp);
			}

		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed deserializing priority table entries",e);
			return;
		}
	}

	//-------------------------------------------------------------------
	public static PriorityTableEntry getPriorityTableEntry(PriorityType type, Priority prio) {
		return prioTable.get(type).get(prio);
	}

	
	//
//	//-------------------------------------------------------------------
//	public static <E extends DataItem> void loadDataItems(Class<? extends List<E>> cls, DataSet plugin, InputStream in) {
//		logger.log(Level.DEBUG, "Load skills (Plugin="+plugin.getID()+")");
//		try {
//			List<E> addSkills = serializer.read(cls, in);
//			logger.log(Level.INFO, "Successfully loaded "+addSkills.size()+" skills");
//			addSkills.forEach(skill -> skill.assignToDataSet(plugin));
//			addSkills.forEach(skill -> skills.add((Skill) skill));
//		} catch (Exception e) {
//			logger.log(Level.ERROR, "Failed deserializing skills",e);
//			System.exit(0);
//			return;
//		}
//	}
//
//	//-------------------------------------------------------------------
//	public static void loadSkills(DataSet plugin, InputStream in) {
//		loadDataItems(SkillList.class, plugin, in);
//		logger.log(Level.DEBUG, "Load skills (Plugin="+plugin.getID()+")");
//		try {
//			SkillList addSkills = serializer.read(SkillList.class, in);
//			logger.log(Level.INFO, "Successfully loaded "+addSkills.size()+" skills");
//			addSkills.forEach(skill -> skill.assignToDataSet(plugin));
//			addSkills.forEach(skill -> skills.add(skill));
//		} catch (Exception e) {
//			logger.log(Level.ERROR, "Failed deserializing skills",e);
//			System.exit(0);
//			return;
//		}
//	}

	//-------------------------------------------------------------------
	public static SR6Skill getSkill(String key) {
		return getItem(SR6Skill.class, key);
	}

	//-------------------------------------------------------------------
	public static List<SR6Skill> getSkills(SkillType... types) {
		List<SkillType> allowed = List.of(types);
		List<SR6Skill> ret = getItemList(SR6Skill.class).stream().filter(sk -> allowed.contains(sk.getType())).collect(Collectors.toList());
		Collections.sort(ret, new Comparator<SR6Skill>() {
			public int compare(SR6Skill arg0, SR6Skill arg1) {
				return Collator.getInstance().compare(arg0.getName(),  arg1.getName());
			}
		});
		return ret;
	}

	//-------------------------------------------------------------------
	public static SpellFeature getSpellFeature(String key) {
		return getItem(SpellFeature.class, key);
	}

	//-------------------------------------------------------------------
	public static List<SR6Spell> getSpells() {
		List<SR6Spell> ret = new ArrayList<>();
		getItemList(ASpell.class).forEach(s -> ret.add((SR6Spell)s));
		Collections.sort(ret, new Comparator<SR6Spell>() {
			public int compare(SR6Spell arg0, SR6Spell arg1) {
				int cmp = arg0.getCategory().compareTo(arg1.getCategory());
				if (cmp!=0) return cmp;
				return Collator.getInstance().compare(arg0.getName(),  arg1.getName());
			}
		});
		return ret;
	}

	//-------------------------------------------------------------------
	public static byte[] encode(Shadowrun6Character character) throws CharacterIOException {
		try {
			String json = (new Gson()).toJson(character.getCharGenSettings(Object.class));
			character.setChargenSettingsJSON(json);
			logger.log(Level.INFO, json);
			StringWriter out = new StringWriter();
			serializer.write(character, out);
			return out.toString().getBytes(Charset.forName("UTF-8"));
		} catch (IOException e) {
			logger.log(Level.ERROR, "Failed generating XML for char",e);
			throw new CharacterIOException(ErrorCode.ENCODING_FAILED, "Failed generating XML", e);
		}
	}

	//-------------------------------------------------------------------
	public static Shadowrun6Character decode(byte[] rawData) throws Exception {
		String data = new String(rawData, Charset.forName("UTF-8"));
		return serializer.read(Shadowrun6Character.class, data);
	}

}
