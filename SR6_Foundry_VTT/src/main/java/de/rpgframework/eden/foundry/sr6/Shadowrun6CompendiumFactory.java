package de.rpgframework.eden.foundry.sr6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.eden.foundry.Language;
import de.rpgframework.eden.foundry.Module;
import de.rpgframework.eden.foundry.Pack;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.reality.Player;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.NPCType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.foundry.ActorData;
import de.rpgframework.shadowrun6.foundry.FVTTAdeptPower;
import de.rpgframework.shadowrun6.foundry.FVTTGear;
import de.rpgframework.shadowrun6.foundry.FVTTNPCActor;
import de.rpgframework.shadowrun6.foundry.FVTTQuality;
import de.rpgframework.shadowrun6.foundry.FVTTRitual;
import de.rpgframework.shadowrun6.foundry.FVTTSkill;
import de.rpgframework.shadowrun6.foundry.FVTTSpell;
import de.rpgframework.shadowrun6.foundry.FVTTVehicleActor;
import de.rpgframework.shadowrun6.foundry.FVTTWeapon;
import de.rpgframework.foundry.ItemData;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class Shadowrun6CompendiumFactory {

	private static Logger logger = System.getLogger("shadowrun6.compendium");

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	//-------------------------------------------------------------------
	private static String createSourceText(DataItem item, Locale loc) {
		List<String> elements = new ArrayList<>();
		boolean shorted = item.getPageReferences().size()>2;
		String language = loc.getLanguage();
		for (PageReference ref : item.getPageReferences()) {
			if (!ref.getLanguage().equals(language))
				continue;
			if (shorted) {
				elements.add( ref.getProduct().getShortName(Locale.getDefault())+" "+ref.getPage() );				
			} else {
				elements.add( ref.getProduct().getName(Locale.getDefault())+" "+ref.getPage() );
			}
		}
		
		return String.join(", ", elements);
	}

	//-------------------------------------------------------------------
	public static Module createCompendium(Player player, String hostport, Collection<DataSet> sets, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		logger.log(Level.INFO,"createCompendium "+shallow);
		if (localeCallback==null)
			throw new NullPointerException("localeCallback");
		Module module = new Module();
		module.setName("shadowrun6-data");
		module.setTitle("Shadowrun6 Daten");
		if (shallow) {
			module.setVersion("0.0.3"); // For JSON
		} else {
			module.setVersion("0.0.2");  // For ZIP
		}
		module.setMinimumCoreVersion("0.8.0");
		module.setCompatibleCoreVersion("9.242");
		module.setAuthor("Stefan Prelle");
		
		module.fos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(module.fos);
		
		createAdeptPowers(module, zipOut, localeCallback, shallow);
		createQualities  (module, zipOut, sets, localeCallback, shallow);
		createSpells     (module, zipOut, localeCallback, shallow);
		createWeapons    (module, zipOut, localeCallback, shallow);
		createVehicle    (module, zipOut, localeCallback, shallow);
//		createNPCs       (module, zipOut, localeCallback, shallow);
		
		for (Language lang : module.getLanguages()) {
        	ZipEntry zipEntry = new ZipEntry(lang.getPath());
        	zipOut.putNextEntry(zipEntry);
         	zipOut.write(gson.toJson(lang.keys).getBytes(Charset.forName("UTF-8")));
		}

        // module.json
		if (hostport==null) hostport="localhost";
		if (player!=null) {
			module.setManifest("http://"+player.getLogin()+":"+player.getPassword()+"@"+hostport+"/api/foundry/shadowrun6/compendium/module.json");
		} else {
			module.setManifest("http://"+hostport+"/api/foundry/shadowrun6/compendium/module.json");
		}
		module.setDownload(module.getManifest().substring(0, module.getManifest().length()-12)+".zip");
//		logger.log(Level.INFO,"manifest = "+module.getManifest());
//		logger.log(Level.INFO,"Download = "+module.getDownload());
		String json = gson.toJson(module);
    	ZipEntry zipEntry = new ZipEntry("module.json");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(json.getBytes());

		zipOut.close();
    	module.fos.close();
		return module;
	}

	//-------------------------------------------------------------------
	private static void createAdeptPowers(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		Pack pack = new Pack();
		pack.setName("shadowrun6-powers");
		pack.setLabel("Adept Powers");
		pack.setEntity("Item");
		pack.setPath("packs/adeptpowers.db");
		pack.setSystem("shadowrun6-eden");
		module.getPacks().add(pack);
		
		if (shallow)
			return;
		
		StringBuffer buf = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		for (AdeptPower tmp : Shadowrun6Core.getItemList(AdeptPower.class)) {
			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
			for (Locale loc : locales) {
				module.addTranslation(loc.getLanguage(), "adeptpower."+tmp.getId()+".desc", tmp.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "adeptpower."+tmp.getId()+".name", tmp.getName(loc));
				module.addTranslation(loc.getLanguage(), "adeptpower."+tmp.getId()+".src", createSourceText(tmp, loc));
			}
			ItemData<FVTTAdeptPower> entry = Converter.convertAdeptPower(tmp, locales[0]);
			entry._id  = tmp.getId();
			buf.append(gson.toJson(entry));
			buf.append('\n');
		}
		
    	ZipEntry zipEntry = new ZipEntry("packs/adeptpowers.db");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
		return;
	}

	//-------------------------------------------------------------------
	private static void createSpells(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		Pack pack = new Pack();
		pack.setName("shadowrun6-spells");
		pack.setLabel("Spells");
		pack.setEntity("Item");
		pack.setPath("packs/spells.db");
		pack.setSystem("shadowrun6-eden");
		module.getPacks().add(pack);
		
		if (shallow)
			return;
		
		StringBuffer buf = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		for (SR6Spell spell : Shadowrun6Core.getItemList(SR6Spell.class)) {
			Locale[] locales = localeCallback.apply(spell.getPageReferences());
			logger.log(Level.INFO, "Locales = "+Arrays.toString(locales));
			for (Locale loc : locales) {
				module.addTranslation(loc.getLanguage(), "spell."+spell.getId()+".desc", spell.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "spell."+spell.getId()+".name", spell.getName(loc));
				module.addTranslation(loc.getLanguage(), "spell."+spell.getId()+".src", createSourceText(spell, loc));
			}
			
			ItemData<FVTTSpell> entry = Converter.convertSpell(spell, locales[0]);
			buf.append(gson.toJson(entry));
			buf.append('\n');
		}
		
    	ZipEntry zipEntry = new ZipEntry("packs/spells.db");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
		return;
	}

	//-------------------------------------------------------------------
	private static void createQualities(Module module, ZipOutputStream zipOut, Collection<DataSet> sets, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		Pack pack = new Pack();
		pack.setName("shadowrun6-qualities");
		pack.setLabel("Qualities");
		pack.setEntity("Item");
		pack.setPath("packs/qualities.db");
		pack.setSystem("shadowrun6-eden");
		module.getPacks().add(pack);
		
		if (shallow)
			return;
		
		StringBuffer buf = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		List<Quality> list = Shadowrun6Core.getItemList(Quality.class);
		list.sort(new Comparator<Quality>() {
			public int compare(Quality o1, Quality o2) {
				return o1.getName(Locale.ENGLISH).compareTo(o2.getName(Locale.ENGLISH));
			}
		});
		for (Quality tmp : list) {
			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
			if (!tmp.inDataSets(sets)) {
				continue;
			}
			for (Locale loc : locales) {
				module.addTranslation(loc.getLanguage(), "quality."+tmp.getId()+".desc", tmp.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "quality."+tmp.getId()+".name", tmp.getName(loc));
				module.addTranslation(loc.getLanguage(), "quality."+tmp.getId()+".src", createSourceText(tmp, loc));
			}
			ItemData<FVTTQuality> entry = Converter.convertQuality(tmp, locales[0]);
			entry._id  = tmp.getId();
			
			buf.append(gson.toJson(entry));
			buf.append('\n');
		}
		
    	ZipEntry zipEntry = new ZipEntry("packs/qualities.db");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
		return;
	}

	//-------------------------------------------------------------------
	private static void createWeapons(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		Pack pack = new Pack();
		pack.setName("shadowrun6-weapons");
		pack.setLabel("Weapons");
		pack.setEntity("Item");
		pack.setPath("packs/weapons.db");
		pack.setSystem("shadowrun6-eden");
		module.getPacks().add(pack);
		
		if (shallow)
			return;
		
		StringBuffer buf = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		for (ItemTemplate tmp : Shadowrun6Core.getItemList(ItemTemplate.class)) {
			if (!ItemType.isWeapon(tmp.getItemType()))
					continue;
			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
			for (Locale loc : locales) {
				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".desc", tmp.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".name", tmp.getName(loc));
				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".src", createSourceText(tmp, loc));
			}
			
			ItemData<FVTTGear> entry = Converter.convert(tmp, locales[0]);
			entry._id  = tmp.getId();
			buf.append(gson.toJson(entry));
			buf.append('\n');
		}
		
    	ZipEntry zipEntry = new ZipEntry("packs/weapons.db");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
		return;
	}

//	//-------------------------------------------------------------------
//	private static void createRituals(Module module, ZipOutputStream zipOut, boolean shallow) throws IOException {
//		Pack pack = new Pack();
//		pack.setName("shadowrun6-rituals");
//		pack.setLabel("Rituals");
//		pack.setEntity("Item");
//		pack.setPath("packs/rituals.db");
//		pack.setSystem("shadowrun6-eden");
//		module.getPacks().add(pack);
//		
//		if (shallow)
//			return;
//		
//		StringBuffer buf = new StringBuffer();
//		Gson gson = new GsonBuilder().create();
//		for (Ritual spell : Shadowrun6Core.getItemList(Ritual.class)) {
//			
//			module.addTranslation("de", "spell."+spell.getId()+".desc", spell.getDescription(Locale.GERMAN));
//			module.addTranslation("de", "spell."+spell.getId()+".name", spell.getName(Locale.GERMAN));
//			module.addTranslation("de", "spell."+spell.getId()+".src", createSourceText(spell, Locale.GERMAN));
//			module.addTranslation("en", "spell."+spell.getId()+".desc", spell.getDescription(Locale.ENGLISH));
//			module.addTranslation("en", "spell."+spell.getId()+".name", spell.getName(Locale.ENGLISH));
//			module.addTranslation("en", "spell."+spell.getId()+".src", createSourceText(spell, Locale.ENGLISH));
//			CompendiumEntry entry = new CompendiumEntry();
//			entry._id = spell.getId();
//			entry.name = spell.getName(Locale.ENGLISH);
//			entry.type = "spell";
//			
//			FVTTRitual data = new FVTTRitual();
//			data.genesisID = spell.getId();
//			data.category  = spell.getCategory().name();
//			data.drain = spell.getDrain();
//			data.type  = spell.getType().name();
//			data.range = spell.getRange().name();
//			entry.data = data;
//			
//			buf.append(gson.toJson(entry));
//			buf.append('\n');
//		}
//		
//    	ZipEntry zipEntry = new ZipEntry("packs/spells.db");
//    	zipOut.putNextEntry(zipEntry);
//    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
//		return;
//	}

	//-------------------------------------------------------------------
	private static void createVehicle(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		Pack pack = new Pack();
		pack.setName("shadowrun6-vehicles");
		pack.setLabel("Vehicles");
		pack.setEntity("Actor");
		pack.setPath("packs/vehicles.db");
		pack.setSystem("shadowrun6-eden");
		module.getPacks().add(pack);
		
		if (shallow)
			return;
		
		StringBuffer buf = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		for (ItemTemplate tmp : Shadowrun6Core.getItemList(ItemTemplate.class)) {
			if (tmp.getItemType()!=ItemType.VEHICLES)
				continue;
			
			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
			for (Locale loc : locales) {
				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".desc", tmp.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".name", tmp.getName(loc));
				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".src", createSourceText(tmp, loc));
			}
			ActorData entry = Converter.convertActor(tmp, locales[0]);
			entry._id  = tmp.getId();
//			entry.flags.core.sheetClass="shadowrun6-eden.Shadowrun6ActorSheetVehicleCompendium";
			
			buf.append(gson.toJson(entry));
			buf.append('\n');
		}
		
    	ZipEntry zipEntry = new ZipEntry("packs/vehicles.db");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
		return;
	}

//	//-------------------------------------------------------------------
//	private static void createNPCs(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
//		Pack pack = new Pack();
//		pack.setName("shadowrun6-grunts");
//		pack.setLabel("Grunts");
//		pack.setEntity("Actor");
//		pack.setPath("packs/grunts.db");
//		pack.setSystem("shadowrun6-eden");
//		module.getPacks().add(pack);
//		
//		if (shallow)
//			return;
//		
//		StringBuffer buf = new StringBuffer();
//		Gson gson = new GsonBuilder().create();
//		for (SR6NPC tmp : Shadowrun6Core.getItemList(SR6NPC.class)) {
//			if (tmp.getType()!=NPCType.GRUNT)
//				continue;
//			
//			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
//			for (Locale loc : locales) {
//				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".desc", tmp.getDescription(loc));
//				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".name", tmp.getName(loc));
//				module.addTranslation(loc.getLanguage(), "item."+tmp.getId()+".src", createSourceText(tmp, loc));
//			}
//			CompendiumEntry entry = new CompendiumEntry();
//			entry._id = tmp.getId();
//			entry.name = tmp.getName(locales[0]);
//			entry.type = "NPC";
////			entry.flags.core.sheetClass="shadowrun6-eden.Shadowrun6ActorSheetVehicleCompendium";
//			
//			FVTTNPCActor data = new FVTTNPCActor();
//			data.attributes.bod.base = tmp.getAttribute(ShadowrunAttribute.BODY).getDistributed();
//			data.attributes.bod.mod  = tmp.getAttribute(ShadowrunAttribute.BODY).getModifier();
//			data.attributes.agi.base = tmp.getAttribute(ShadowrunAttribute.AGILITY).getDistributed();
//			data.attributes.agi.mod  = tmp.getAttribute(ShadowrunAttribute.AGILITY).getModifier();
//			data.attributes.rea.base = tmp.getAttribute(ShadowrunAttribute.REACTION).getDistributed();
//			data.attributes.rea.mod  = tmp.getAttribute(ShadowrunAttribute.REACTION).getModifier();
//			data.attributes.str.base = tmp.getAttribute(ShadowrunAttribute.STRENGTH).getDistributed();
//			data.attributes.str.mod  = tmp.getAttribute(ShadowrunAttribute.STRENGTH).getModifier();
//			data.attributes.wil.base = tmp.getAttribute(ShadowrunAttribute.WILLPOWER).getDistributed();
//			data.attributes.wil.mod  = tmp.getAttribute(ShadowrunAttribute.WILLPOWER).getModifier();
//			data.attributes.log.base = tmp.getAttribute(ShadowrunAttribute.LOGIC).getDistributed();
//			data.attributes.log.mod  = tmp.getAttribute(ShadowrunAttribute.LOGIC).getModifier();
//			data.attributes.inn.base = tmp.getAttribute(ShadowrunAttribute.INTUITION).getDistributed();
//			data.attributes.inn.mod  = tmp.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
//			data.attributes.cha.base = tmp.getAttribute(ShadowrunAttribute.CHARISMA).getDistributed();
//			data.attributes.cha.mod  = tmp.getAttribute(ShadowrunAttribute.CHARISMA).getModifier();
//			data.attributes.mag.base = tmp.getAttribute(ShadowrunAttribute.MAGIC).getDistributed();
//			data.attributes.mag.mod  = tmp.getAttribute(ShadowrunAttribute.MAGIC).getModifier();
//			data.attributes.res.base = tmp.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed();
//			data.attributes.res.mod  = tmp.getAttribute(ShadowrunAttribute.RESONANCE).getModifier();
//			
//			for (SR6SkillValue val : tmp.getSkillValues()) {
//				if ((val.getModifyable().getId().equals("language") || val.getModifyable().getId().equals("knowledge"))) {
//					continue;
//				} else {
//					FVTTSkill fVal = new FVTTSkill();
//					fVal.genesisID = val.getModifyable().getId();
//					fVal.points    = val.getDistributed();
//					fVal.modifier  = val.getModifier();
//
//					Item<FVTTSkill> item = new Item<FVTTSkill>(val.getName(Locale.ENGLISH), "skill", fVal);
//					entry.addItems(item);
//				}
//			}
////			data.accOff = spell.getAttribute(SR6ItemAttribute.)
////			data.genesisID = spell.getId();
////			data.category  = spell.getCategory().name();
////			data.drain = spell.getDrain();
////			data.type  = spell.getType().name();
////			data.range = spell.getRange().name();
//			entry.data = data;
//			
//			buf.append(gson.toJson(entry));
//			buf.append('\n');
//		}
//		
//    	ZipEntry zipEntry = new ZipEntry("packs/grunts.db");
//    	zipOut.putNextEntry(zipEntry);
//    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
//		return;
//	}

}
