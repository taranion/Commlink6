package de.rpgframework.eden.roll20.sr6;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.reality.Player;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun6.SR6Ritual;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * @author prelle
 *
 */
public class Shadowrun6CompendiumFactory {

	private static Logger logger = System.getLogger("shadowrun6.compendium");

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final static String IMGROOT = "/home/data/shadowrun";

	private final static String VALIDCHARS2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final static String VALIDCHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
	private final static Random RANDOM = new Random();
	
	private final static String createRandomID() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<16; i++) {
			buf.append(VALIDCHARS.charAt(RANDOM.nextInt(VALIDCHARS.length())));
		}
		return buf.toString();
	}
	
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
	public static Workbook createCompendium(Player player, String hostport, Collection<DataSet> sets, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
		logger.log(Level.INFO,"createCompendium "+shallow);
		if (localeCallback==null)
			throw new NullPointerException("localeCallback");
		Workbook module = new XSSFWorkbook();
		
		createAdeptPowers(module, localeCallback);
//		createCritterPowers(module, zipOut, localeCallback, shallow);
		createQualities  (module, localeCallback);
		createSpells     (module, localeCallback);
		createRituals    (module, localeCallback);
		createMetamagic  (module, localeCallback);
		createComplexForms(module, localeCallback);
		createEchoes     (module, localeCallback);
		createWeapons    (module,localeCallback, "Melee", ItemSubType.BLADES, ItemSubType.CLUBS, ItemSubType.WHIPS, ItemSubType.UNARMED, ItemSubType.OTHER_CLOSE);
		createWeapons    (module,localeCallback, "Range", 
				ItemSubType.BOWS,
				ItemSubType.CROSSBOWS,
				ItemSubType.THROWING,
				ItemSubType.TASERS,
				ItemSubType.HOLDOUTS,
				ItemSubType.PISTOLS_LIGHT,
				ItemSubType.MACHINE_PISTOLS,
				ItemSubType.PISTOLS_HEAVY,
				ItemSubType.SUBMACHINE_GUNS,
				ItemSubType.SHOTGUNS,
				ItemSubType.RIFLE_ASSAULT,
				ItemSubType.RIFLE_HUNTING,
				ItemSubType.RIFLE_SNIPER,
				ItemSubType.LMG,
				ItemSubType.MMG,
				ItemSubType.HMG,
				ItemSubType.ASSAULT_CANNON,
				ItemSubType.LAUNCHERS,
				ItemSubType.THROWERS,
				ItemSubType.OTHER_SPECIAL
				);
		createVehicles   (module,localeCallback);
		createAugmentations(module,localeCallback);
		createOtherGear  (module,localeCallback);
//		createVehicle    (module, zipOut, localeCallback, shallow);
//		createGrunts     (module, localeCallback);
//		createCritter    (module, zipOut, localeCallback, shallow);
		
//		for (Language lang : module.getLanguages()) {
//        	ZipEntry zipEntry = new ZipEntry(lang.getPath());
//        	zipOut.putNextEntry(zipEntry);
//         	zipOut.write(gson.toJson(lang.keys).getBytes(Charset.forName("UTF-8")));
//		}
//
//        // module.json
//		if (hostport==null) hostport="localhost";
//		if (player!=null) {
//			module.setManifest("http://"+player.getLogin()+":"+player.getPassword()+"@"+hostport+"/api/foundry/shadowrun6/compendium/module.json");
//		} else {
//			module.setManifest("http://"+hostport+"/api/foundry/shadowrun6/compendium/module.json");
//		}
//		module.setDownload(module.getManifest().substring(0, module.getManifest().length()-12)+".zip");
//		logger.log(Level.INFO,"manifest = "+module.getManifest());
//		logger.log(Level.INFO,"Download = "+module.getDownload());
		return module;
	}

	//-------------------------------------------------------------------
	private static void createAdeptPowers(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Powers");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-activation");
		head.createCell(5, CellType.STRING).setCellValue("data-activation_text");
		head.createCell(6, CellType.STRING).setCellValue("data-cost");
		head.createCell(7, CellType.STRING).setCellValue("data-cost_text");
		
		List<AdeptPower> list = Shadowrun6Core.getItemList(AdeptPower.class);
		Collections.sort(list, new Comparator<AdeptPower>() {
			public int compare(AdeptPower o1, AdeptPower o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (AdeptPower item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertAdeptPower( item, locales[0], row);
		}
		
		for (int i=0; i<14; i++) {
			if (i==2 || i==4) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

//	//-------------------------------------------------------------------
//	private static void createCritterPowers(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
//		Pack pack = new Pack();
//		pack.setName("shadowrun6-critterpowers");
//		pack.setLabel("Critter Powers");
//		pack.setEntity("Item");
//		pack.setPath("packs/critterpowers.db");
//		pack.setSystem("shadowrun6-eden");
//		module.getPacks().add(pack);
//		
//		if (shallow)
//			return;
//		
//		StringBuffer buf = new StringBuffer();
//		Gson gson = new GsonBuilder().create();
//		for (CritterPower tmp : Shadowrun6Core.getItemList(CritterPower.class)) {
//			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
//			for (Locale loc : locales) {
//				module.addTranslation(loc.getLanguage(), "critterpower."+tmp.getId()+".desc", tmp.getDescription(loc));
//				module.addTranslation(loc.getLanguage(), "critterpower."+tmp.getId()+".name", tmp.getName(loc));
//				module.addTranslation(loc.getLanguage(), "critterpower."+tmp.getId()+".src", createSourceText(tmp, loc));
//			}
//			ItemData<FVTTCritterPower> entry = Converter.convert(tmp, locales[0]);
////			entry._id  = tmp.getId();
//			entry._id  = createRandomID();
//			buf.append(gson.toJson(entry));
//			buf.append('\n');
//		}
//		
//    	ZipEntry zipEntry = new ZipEntry("packs/critterpowers.db");
//    	zipOut.putNextEntry(zipEntry);
//    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
//		return;
//	}

	//-------------------------------------------------------------------
	private static void createSpells(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Spells");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-notes");

		head.createCell(5, CellType.STRING).setCellValue("data-type");
		head.createCell(6, CellType.STRING).setCellValue("data-range");
		head.createCell(7, CellType.STRING).setCellValue("data-duration");
		head.createCell(8, CellType.STRING).setCellValue("data-drain");
		head.createCell(9, CellType.STRING).setCellValue("data-category");
		head.createCell(10, CellType.STRING).setCellValue("data-effect_type");
		head.createCell(11, CellType.STRING).setCellValue("data-skill");
		head.createCell(12, CellType.STRING).setCellValue("data-damage");
		
		head.createCell(13, CellType.STRING).setCellValue("data-category_text");
		head.createCell(14, CellType.STRING).setCellValue("data-duration_text");
		head.createCell(15, CellType.STRING).setCellValue("data-drain_text");
		head.createCell(16, CellType.STRING).setCellValue("data-range_text");
		head.createCell(17, CellType.STRING).setCellValue("data-type_text");
		head.createCell(18, CellType.STRING).setCellValue("data-damage_text");
		head.createCell(19, CellType.STRING).setCellValue("data-features_text");
		
		List<SR6Spell> list = Shadowrun6Core.getItemList(SR6Spell.class);
		Collections.sort(list, new Comparator<SR6Spell>() {
			public int compare(SR6Spell o1, SR6Spell o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (SR6Spell item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());
			row.createCell(4, CellType.STRING).setCellValue(item.getDescription(locales[0]));

			Converter.convertSpell((SR6Spell) item, locales[0], row);
		}
		for (int i=0; i<20; i++) {
			if (i==2 || i==4) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createMetamagic(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Metamagic");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");

		head.createCell(4, CellType.STRING).setCellValue("data-rating");
//		head.createCell(6, CellType.STRING).setCellValue("duration");
//		head.createCell(7, CellType.STRING).setCellValue("data-fade");
//		head.createCell(8, CellType.STRING).setCellValue("Fade Value");
		
		
		List<MetamagicOrEcho> list = Shadowrun6Core.getItemList(MetamagicOrEcho.class);
		list = list.stream().filter(moe -> moe.getType()==Type.METAMAGIC || moe.getType()==Type.METAMAGIC_ADEPT)
				.sorted(new Comparator<MetamagicOrEcho>() {
			public int compare(MetamagicOrEcho o1, MetamagicOrEcho o2) {
				return o1.getName().compareTo(o2.getName());
			}
		}).toList();
		for (MetamagicOrEcho item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertEcho((MetamagicOrEcho) item, locales[0], row);
		}
		for (int i=0; i<20; i++) {
			if (i==2 || i==4) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createComplexForms(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Forms");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");

		head.createCell(4, CellType.STRING).setCellValue("data-duration");
		head.createCell(5, CellType.STRING).setCellValue("data-fade");
		
		
		List<ComplexForm> list = Shadowrun6Core.getItemList(ComplexForm.class);
		Collections.sort(list, new Comparator<ComplexForm>() {
			public int compare(ComplexForm o1, ComplexForm o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ComplexForm item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertComplexForm((ComplexForm) item, locales[0], row);
		}
		for (int i=0; i<20; i++) {
			if (i==2 || i==4) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createEchoes(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Echoes");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-rating");
		
		List<MetamagicOrEcho> list = Shadowrun6Core.getItemList(MetamagicOrEcho.class);
		list = list.stream().filter(moe -> moe.getType()==Type.ECHO)
				.sorted(new Comparator<MetamagicOrEcho>() {
			public int compare(MetamagicOrEcho o1, MetamagicOrEcho o2) {
				return o1.getName().compareTo(o2.getName());
			}
		}).toList();
		for (MetamagicOrEcho item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertEcho((MetamagicOrEcho) item, locales[0], row);
		}
		for (int i=0; i<20; i++) {
			if (i==2 || i==4) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	public static String pretty(String original) {
		original = original.replace("\u2022 ", "").trim();		
		if (original.startsWith("<br/>"))
			original = original.substring(5);
		if (original.endsWith("<br/>"))
			original = original.substring(0, original.length()-5);
		return original;
	}

	//-------------------------------------------------------------------
	private static String extractDescripton(String original) {
		int costIndex = original.indexOf("<br/><b>Cost");
		if (costIndex==0)
			costIndex = original.indexOf("<br/> <b>Cost");
		int bonusIndex = original.indexOf("<br/><b>Bonus");
		if (costIndex>0)
			return original.substring(0, costIndex);
		if (bonusIndex>0)
			return original.substring(0, bonusIndex);
		System.err.println("Fail for: "+original);
		return null;
	}

	//-------------------------------------------------------------------
	private static String extractGameEffect(String original) {
		String needle = "<b>Game Effect:</b> ";
		int costIndex = original.indexOf(needle);
		if (costIndex>0)
			return original.substring(costIndex+needle.length(), original.length());
		System.err.println("Fail for "+original);
		return original;
	}

	//-------------------------------------------------------------------
	private static String getCostText(Quality qual) {
		StringBuffer buf = new StringBuffer();
//		if (qual.isPositive()) {
//			buf.append("<b>Cost:</b> ");
//		} else {
//			buf.append("<b>Bonus:</b> ");
//		}
		buf.append(qual.getKarmaCost()+" Karma");
		if (qual.hasLevel())
			buf.append(" per level");
		return buf.toString();
	}

	//-------------------------------------------------------------------
	private static void createQualities(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Qualities");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-type");
		head.createCell(5, CellType.STRING).setCellValue("data-positive");
		head.createCell(6, CellType.STRING).setCellValue("data-maxLevel");
		head.createCell(7, CellType.STRING).setCellValue("data-karma");
		head.createCell(8, CellType.STRING).setCellValue("data-game_effect");
		head.createCell(9, CellType.STRING).setCellValue("data-cost_text");
		
		List<Quality> list = Shadowrun6Core.getItemList(Quality.class);
		Collections.sort(list, new Comparator<Quality>() {
			public int compare(Quality o1, Quality o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Quality item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(pretty(extractDescripton(pretty(item.getDescription(locales[0])))));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());
			row.createCell(3, CellType.STRING).setCellValue(item.getId());
			row.createCell(8, CellType.STRING).setCellValue(extractGameEffect(pretty(item.getDescription(locales[0]))));
			row.createCell(9, CellType.STRING).setCellValue(getCostText(item));
			Converter.convertQuality(item, locales[0], row);
		}
		
		for (int i=0; i<14; i++) {
			if (i==2 || i==4 || i==8) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createAugmentations(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Augmentations");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-variantID");
		head.createCell(5, CellType.STRING).setCellValue("data-itemtype");
		head.createCell(6, CellType.STRING).setCellValue("data-itemsubtype");
		head.createCell(7, CellType.STRING).setCellValue("data-availability");
		head.createCell(8, CellType.STRING).setCellValue("data-price");
		head.createCell(9, CellType.STRING).setCellValue("data-has_rating");
		head.createCell(10, CellType.STRING).setCellValue("data-capacity_cost");
		head.createCell(11, CellType.STRING).setCellValue("data-essence_cost");
		head.createCell(12, CellType.STRING).setCellValue("data-modification");
		
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		List<ItemType> subs = List.of(ItemType.bodytechTypes());
		for (ItemTemplate item : list) {
			if (!subs.contains(item.getItemType()))
				continue;
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			if (item.getVariants().isEmpty() || !item.requiresVariant()) {
				Row row = sheet.createRow(++rowNum);
				row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
				row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
				row.createCell(2, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
				row.createCell(3, CellType.STRING).setCellValue(item.getId());

				Converter.convertAugmentation(item, locales[0], row);
			} else {
				for (SR6PieceOfGearVariant variant : item.getVariants()) {
					CarryMode mode = variant.getUsages().isEmpty()
							?
							item.getUsages().get(0).getMode()
							:
							variant.getUsages().get(0).getMode();
					CarriedItem<ItemTemplate> carried = new CarriedItem<>(item, variant, mode);
					SR6GearTool.recalculate("", null, carried);
					Row row = sheet.createRow(++rowNum);
					row.createCell(0, CellType.STRING).setCellValue(carried.getNameWithoutRating(locales[0]));
					row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
					row.createCell(2, CellType.STRING).setCellValue(item.getDescription(locales[0]));
					row.createCell(3, CellType.STRING).setCellValue(item.getId());
					row.createCell(4, CellType.STRING).setCellValue(variant.getId());
					Converter.convertAugmentation(carried, locales[0], row);
				}
			}
		}
		
		for (int i=0; i<12; i++) {
			if (i==2) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createWeapons(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, String category, ItemSubType...subtypes) throws IOException {
		Sheet sheet = workbook.createSheet("!"+category);
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-itemtype");
		head.createCell(5, CellType.STRING).setCellValue("data-itemsubtype");
		head.createCell(6, CellType.STRING).setCellValue("data-availability");
		head.createCell(7, CellType.STRING).setCellValue("data-price");
		head.createCell(8, CellType.STRING).setCellValue("data-skill");
		head.createCell(9, CellType.STRING).setCellValue("data-skillspec");
		head.createCell(10, CellType.STRING).setCellValue("data-dv");
		head.createCell(11, CellType.STRING).setCellValue("data-close");
		head.createCell(12, CellType.STRING).setCellValue("data-near");
		head.createCell(13, CellType.STRING).setCellValue("data-medium");
		head.createCell(14, CellType.STRING).setCellValue("data-far");
		head.createCell(15, CellType.STRING).setCellValue("data-extreme");
		head.createCell(16, CellType.STRING).setCellValue("data-firing_modes");
		head.createCell(17, CellType.STRING).setCellValue("data-ammo");
		
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		List<ItemSubType> subs = List.of(subtypes);
		for (ItemTemplate item : list) {
			if (!ItemType.isWeapon(item.getItemType()))
					continue;
			if (!subs.contains(item.getItemSubtype()))
				continue;
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertWeapon(item, locales[0], row);
		}
		
		for (int i=0; i<11; i++) {
			if (i==2 || i==4) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createRituals(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Rituals");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-threshold");
		head.createCell(5, CellType.STRING).setCellValue("data-features");
		
		
		List<SR6Ritual> list = Shadowrun6Core.getItemList(SR6Ritual.class);
		Collections.sort(list, new Comparator<SR6Ritual>() {
			public int compare(SR6Ritual o1, SR6Ritual o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (SR6Ritual item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertRitual(item, locales[0], row);
		}
		
		for (int i=0; i<11; i++) {
			if (i==2) continue;
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createVehicles(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Vehicles");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-itemtype");
		head.createCell(5, CellType.STRING).setCellValue("data-type");
		head.createCell(6, CellType.STRING).setCellValue("data-availability");
		head.createCell(7, CellType.STRING).setCellValue("data-price");
		head.createCell(8, CellType.STRING).setCellValue("data-hand");
		head.createCell(9, CellType.STRING).setCellValue("data-acc");
		head.createCell(10, CellType.STRING).setCellValue("data-spdi");
		head.createCell(11, CellType.STRING).setCellValue("data-speed");
		head.createCell(12, CellType.STRING).setCellValue("data-body");
		head.createCell(13, CellType.STRING).setCellValue("data-armor");
		head.createCell(14, CellType.STRING).setCellValue("data-pilot");
		head.createCell(15, CellType.STRING).setCellValue("data-sensor");
		head.createCell(16, CellType.STRING).setCellValue("data-seat");
		
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ItemTemplate item : list) {
			if (!ItemType.isVehicle(item.getItemType()))
					continue;
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertVehicle(item, locales[0], row);
		}
		
		for (int i=0; i<11; i++) {
			if (i==2) continue;
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createArnor(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("!Gear");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-itemtype");
		head.createCell(5, CellType.STRING).setCellValue("data-type");
		head.createCell(6, CellType.STRING).setCellValue("data-availability");
		head.createCell(7, CellType.STRING).setCellValue("data-price");
		
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ItemTemplate item : list) {
			if (ItemType.isVehicle(item.getItemType()))
					continue;
			if (ItemType.isWeapon(item.getItemType()))
				continue;
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertOtherGear(item, locales[0], row);
		}
		
		for (int i=0; i<11; i++) {
			if (i==2) continue;
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createOtherGear(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("Gear");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(2, CellType.STRING).setCellValue("data-description");
		head.createCell(3, CellType.STRING).setCellValue("data-genesisID");
		head.createCell(4, CellType.STRING).setCellValue("data-itemtype");
		head.createCell(5, CellType.STRING).setCellValue("data-type");
		head.createCell(6, CellType.STRING).setCellValue("data-availability");
		head.createCell(7, CellType.STRING).setCellValue("data-price");
		
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ItemTemplate item : list) {
			if (ItemType.isVehicle(item.getItemType()))
					continue;
			if (ItemType.isWeapon(item.getItemType()))
				continue;
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			
			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(item.getName(locales[0]));
			row.createCell(1, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(2, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(item.getId());

			Converter.convertOtherGear(item, locales[0], row);
		}
		
		for (int i=0; i<11; i++) {
			if (i==2) continue;
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createGrunts(Module module, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
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
//				module.addTranslation(loc.getLanguage(), "npc."+tmp.getId()+".name", tmp.getName(loc));
//				module.addTranslation(loc.getLanguage(), "npc."+tmp.getId()+".src", createSourceText(tmp, loc));
//				if (!tmp.getDescription(loc).endsWith(".desc"))
//					module.addTranslation(loc.getLanguage(), "npc."+tmp.getId()+".desc", tmp.getDescription(loc));
//			}
//			
//			ActorData<? extends GeneralActor> entry = Converter.convertActor(tmp, locales[0]);
//			entry._id = createRandomID();
////			entry.flags.core.sheetClass="shadowrun6-eden.Shadowrun6ActorSheetVehicleCompendium";
//			
////			FVTTNPCActor data = new FVTTNPCActor();
////			data.attributes.bod.base = tmp.getAttribute(ShadowrunAttribute.BODY).getDistributed();
////			data.attributes.bod.mod  = tmp.getAttribute(ShadowrunAttribute.BODY).getModifier();
////			data.attributes.agi.base = tmp.getAttribute(ShadowrunAttribute.AGILITY).getDistributed();
////			data.attributes.agi.mod  = tmp.getAttribute(ShadowrunAttribute.AGILITY).getModifier();
////			data.attributes.rea.base = tmp.getAttribute(ShadowrunAttribute.REACTION).getDistributed();
////			data.attributes.rea.mod  = tmp.getAttribute(ShadowrunAttribute.REACTION).getModifier();
////			data.attributes.str.base = tmp.getAttribute(ShadowrunAttribute.STRENGTH).getDistributed();
////			data.attributes.str.mod  = tmp.getAttribute(ShadowrunAttribute.STRENGTH).getModifier();
////			data.attributes.wil.base = tmp.getAttribute(ShadowrunAttribute.WILLPOWER).getDistributed();
////			data.attributes.wil.mod  = tmp.getAttribute(ShadowrunAttribute.WILLPOWER).getModifier();
////			data.attributes.log.base = tmp.getAttribute(ShadowrunAttribute.LOGIC).getDistributed();
////			data.attributes.log.mod  = tmp.getAttribute(ShadowrunAttribute.LOGIC).getModifier();
////			data.attributes.inn.base = tmp.getAttribute(ShadowrunAttribute.INTUITION).getDistributed();
////			data.attributes.inn.mod  = tmp.getAttribute(ShadowrunAttribute.INTUITION).getModifier();
////			data.attributes.cha.base = tmp.getAttribute(ShadowrunAttribute.CHARISMA).getDistributed();
////			data.attributes.cha.mod  = tmp.getAttribute(ShadowrunAttribute.CHARISMA).getModifier();
////			data.attributes.mag.base = tmp.getAttribute(ShadowrunAttribute.MAGIC).getDistributed();
////			data.attributes.mag.mod  = tmp.getAttribute(ShadowrunAttribute.MAGIC).getModifier();
////			data.attributes.res.base = tmp.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed();
////			data.attributes.res.mod  = tmp.getAttribute(ShadowrunAttribute.RESONANCE).getModifier();
////			
////			for (SR6SkillValue val : tmp.getSkillValues()) {
////				if ((val.getModifyable().getId().equals("language") || val.getModifyable().getId().equals("knowledge"))) {
////					continue;
////				} else {
////					FVTTSkill fVal = new FVTTSkill();
////					fVal.genesisID = val.getModifyable().getId();
////					fVal.points    = val.getDistributed();
////					fVal.modifier  = val.getModifier();
////
////					Item<FVTTSkill> item = new Item<FVTTSkill>(val.getName(Locale.ENGLISH), "skill", fVal);
////					entry.addItems(item);
////				}
////			}
//////			data.accOff = spell.getAttribute(SR6ItemAttribute.)
//////			data.genesisID = spell.getId();
//////			data.category  = spell.getCategory().name();
//////			data.drain = spell.getDrain();
//////			data.type  = spell.getType().name();
//////			data.range = spell.getRange().name();
////			entry.data = data;
//			
//			buf.append(gson.toJson(entry));
//			buf.append('\n');
//		}
//		
//    	ZipEntry zipEntry = new ZipEntry("packs/grunts.db");
//    	zipOut.putNextEntry(zipEntry);
//    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
//		return;
	}

//	//-------------------------------------------------------------------
//	private static void createCritter(Module module, ZipOutputStream zipOut, Function<Collection<PageReference>,Locale[]> localeCallback, boolean shallow) throws IOException {
//		Pack pack = new Pack();
//		pack.setName("shadowrun6-critter");
//		pack.setLabel("Critter");
//		pack.setEntity("Actor");
//		pack.setPath("packs/critter.db");
//		pack.setSystem("shadowrun6-eden");
//		module.getPacks().add(pack);
//		
//		if (shallow)
//			return;
//		
//		StringBuffer buf = new StringBuffer();
//		Gson gson = new GsonBuilder().create();
//		for (SR6NPC tmp : Shadowrun6Core.getItemList(SR6NPC.class)) {
//			if (tmp.getType()!=NPCType.CRITTER && tmp.getType()!=NPCType.CRITTER_AWAKENED)
//				continue;
//			logger.log(Level.WARNING, "Critter "+tmp.getName()+" "+tmp.getType());
//			
//			Locale[] locales = localeCallback.apply(tmp.getPageReferences());
//			for (Locale loc : locales) {
//				module.addTranslation(loc.getLanguage(), "critter."+tmp.getId()+".name", tmp.getName(loc));
//				module.addTranslation(loc.getLanguage(), "critter."+tmp.getId()+".src", createSourceText(tmp, loc));
//				if (!tmp.getDescription(loc).endsWith(".desc"))
//					module.addTranslation(loc.getLanguage(), "critter."+tmp.getId()+".desc", tmp.getDescription(loc));
//			}
//			
//			ActorData<? extends GeneralActor> entry = Converter.convertActor(tmp, locales[0]);
//			entry._id = createRandomID();
//			
//			addImages(zipOut, "critter", tmp.getId(), entry);
//			buf.append(gson.toJson(entry));
//			buf.append('\n');
//		}
//		
//    	ZipEntry zipEntry = new ZipEntry("packs/critter.db");
//    	zipOut.putNextEntry(zipEntry);
//    	zipOut.write(buf.toString().getBytes(Charset.forName("UTF-8")));
//		return;
//	}

}
