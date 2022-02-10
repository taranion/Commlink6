package de.rpgframework.eden.foundry.sr6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
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
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.foundry.Actor;
import de.rpgframework.shadowrun6.foundry.FVTTAdeptPower;
import de.rpgframework.shadowrun6.foundry.FVTTQuality;
import de.rpgframework.shadowrun6.foundry.FVTTRitual;
import de.rpgframework.shadowrun6.foundry.FVTTSpell;
import de.rpgframework.shadowrun6.foundry.FVTTVehicleActor;
import de.rpgframework.shadowrun6.foundry.FVTTWeapon;
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
	public static String createSourceText(DataItem item, Locale loc) {
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
			CompendiumEntry entry = new CompendiumEntry();
			entry._id  = tmp.getId();
			entry.name = tmp.getName(locales[0]);
			entry.type = "adeptpower";
			
			FVTTAdeptPower data = new FVTTAdeptPower();
			data.genesisID   = tmp.getId();
			data.activation	 = tmp.getActivation().name().toLowerCase();
			data.cost        = tmp.getCostForLevel(1);
			entry.data = data;
			
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
		pack.setLabel("Zauber");
		pack.setEntity("Item");
		pack.setPath("packs/spells.db");
		pack.setSystem("shadowrun6-eden");
		module.getPacks().add(pack);
		
		if (shallow)
			return;
		
		StringBuffer buf = new StringBuffer();
		Gson gson = new GsonBuilder().create();
		for (ASpell spell : Shadowrun6Core.getItemList(ASpell.class)) {
			Locale[] locales = localeCallback.apply(spell.getPageReferences());
			for (Locale loc : locales) {
				module.addTranslation(loc.getLanguage(), "spell."+spell.getId()+".desc", spell.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "spell."+spell.getId()+".name", spell.getName(loc));
				module.addTranslation(loc.getLanguage(), "spell."+spell.getId()+".src", createSourceText(spell, loc));
			}
			CompendiumEntry entry = new CompendiumEntry();
			entry._id = spell.getId();
			entry.name = spell.getName(locales[0]);
			entry.type = "spell";
			
			FVTTSpell data = new FVTTSpell();
			data.genesisID = spell.getId();
			data.category  = spell.getCategory().name();
			data.drain = spell.getDrain();
			data.type  = spell.getType().name();
			data.range = spell.getRange().name();
			entry.data = data;
			
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
		for (Quality tmp : Shadowrun6Core.getItemList(Quality.class)) {
			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
			if (!tmp.inDataSets(sets)) {
				logger.log(Level.DEBUG, "Ignore "+tmp);
				continue;
			}
			for (Locale loc : locales) {
				logger.log(Level.DEBUG, "Quality "+tmp+" in locale "+loc);
				module.addTranslation(loc.getLanguage(), "quality."+tmp.getId()+".desc", tmp.getDescription(loc));
				module.addTranslation(loc.getLanguage(), "quality."+tmp.getId()+".name", tmp.getName(loc));
				module.addTranslation(loc.getLanguage(), "quality."+tmp.getId()+".src", createSourceText(tmp, loc));
			}
			CompendiumEntry entry = new CompendiumEntry();
			entry._id = tmp.getId();
			entry.name = tmp.getName(locales[0]);
			entry.type = "quality";
			
			FVTTQuality data = new FVTTQuality();
			data.genesisID   = tmp.getId();
			data.category    = tmp.getType().name();
			data.level       = tmp.hasLevel();
			entry.data = data;
			
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
			
			CompendiumEntry entry = new CompendiumEntry();
			entry._id  = tmp.getId();
			entry.name = tmp.getName(locales[0]);
			entry.type = "gear";
			
			FVTTWeapon data = new FVTTWeapon();
			data.genesisID  = tmp.getId();
			if (tmp.getItemType()!=null)
				data.type       = tmp.getItemType().name();
			if (tmp.getItemSubtype()!=null)
				data.subtype    = tmp.getItemSubtype().name();
			if (tmp.getAttribute(SR6ItemAttribute.AVAILABILITY)!=null)
				data.availDef   = tmp.getAttribute(SR6ItemAttribute.AVAILABILITY).getRawValue();
			if (tmp.getAttribute(SR6ItemAttribute.SKILL)!=null)
				data.skill      = tmp.getAttribute(SR6ItemAttribute.SKILL).getRawValue();
			if (tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION)!=null)
				data.skillSpec  = tmp.getAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION).getRawValue();

			if (tmp.getAttribute(SR6ItemAttribute.DAMAGE)!=null)			
				data.dmgDef     = tmp.getAttribute(SR6ItemAttribute.DAMAGE).getRawValue();
			entry.data = data;
			
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
		pack.setLabel("Fahrzeuge");
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
			CompendiumEntry entry = new CompendiumEntry();
			entry._id = tmp.getId();
			entry.name = tmp.getName(locales[0]);
			entry.type = "Vehicle";
			entry.flags.core.sheetClass="shadowrun6-eden.Shadowrun6ActorSheetVehicleCompendium";
			
			FVTTVehicleActor data = new FVTTVehicleActor();
//			data.accOff = spell.getAttribute(SR6ItemAttribute.)
//			data.genesisID = spell.getId();
//			data.category  = spell.getCategory().name();
//			data.drain = spell.getDrain();
//			data.type  = spell.getType().name();
//			data.range = spell.getRange().name();
			entry.data = data;
			
			buf.append(gson.toJson(entry));
			buf.append('\n');
		}
		
    	ZipEntry zipEntry = new ZipEntry("packs/vehicles.db");
    	zipOut.putNextEntry(zipEntry);
    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
		return;
	}

}
