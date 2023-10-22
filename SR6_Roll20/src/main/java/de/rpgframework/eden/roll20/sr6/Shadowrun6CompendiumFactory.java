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
import java.util.stream.Collectors;

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
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEcho.Type;
import de.rpgframework.shadowrun.NPCType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Quality;
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

	//-------------------------------------------------------------------
	private static String capitalize(String name) {
		name = name.replace(',', ' ');

		char[] chars = name.toCharArray();
		  boolean found = false;
		  for (int i = 0; i < chars.length; i++) {
		    if (!found && Character.isLetter(chars[i])) {
		      chars[i] = Character.toUpperCase(chars[i]);
		      found = true;
		    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
		      found = false;
		    }
		  }
		  return String.valueOf(chars);
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

		createGrunts     (module, localeCallback, false);
		createGrunts     (module, localeCallback, true);
		createAdeptPowers(module, localeCallback);
		createAdeptPowersBlob(module, localeCallback);
//		createCritterPowers(module, zipOut, localeCallback, shallow);
		createQualities  (module, localeCallback);
		createQualitiesBlobs(module, localeCallback);
		createSpells     (module, localeCallback, false);
		createSpells     (module, localeCallback, true);
		createRituals    (module, localeCallback, false);
		createRituals    (module, localeCallback, true);
		createMetamagic  (module, localeCallback, false);
		createMetamagic  (module, localeCallback, true);
		createComplexForms(module, localeCallback, false);
		createComplexForms(module, localeCallback, true);
		createEchoes     (module, localeCallback, false);
		createEchoes     (module, localeCallback, true);
		createWeapons    (module,localeCallback, false, "Melee", ItemSubType.BLADES, ItemSubType.CLUBS, ItemSubType.WHIPS, ItemSubType.UNARMED, ItemSubType.OTHER_CLOSE);
		createWeapons    (module,localeCallback, true, "Melee", ItemSubType.BLADES, ItemSubType.CLUBS, ItemSubType.WHIPS, ItemSubType.UNARMED, ItemSubType.OTHER_CLOSE);
		createWeapons    (module,localeCallback, false, "Ranged",
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
				ItemSubType.DMSO,
				ItemSubType.DART,
				ItemSubType.OTHER_SPECIAL
				);
		createWeapons    (module,localeCallback, true, "Ranged",
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
				ItemSubType.DMSO,
				ItemSubType.DART,
				ItemSubType.OTHER_SPECIAL
				);
		createVehicles   (module,localeCallback, false);
		createVehicles   (module,localeCallback, true);
		createAugmentations(module,localeCallback, false);
		createAugmentations(module,localeCallback, true);
		createArmor      (module,localeCallback, false);
		createArmor      (module,localeCallback, true);
		createDevices    (module,localeCallback, false);
		createDevices    (module,localeCallback, true);
		createOtherGear  (module,localeCallback, false);
		createOtherGear  (module,localeCallback, true);
//		createVehicle    (module, zipOut, localeCallback, shallow);
		createCritterPowers(module, localeCallback, false);
		createCritterPowers(module, localeCallback, true);
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
		Sheet sheet = workbook.createSheet("Powers");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("data-name");
		head.createCell(2, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3, CellType.STRING).setCellValue("data-description");
		head.createCell(4, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(5, CellType.STRING).setCellValue("data-activation");
		head.createCell(6, CellType.STRING).setCellValue("data-activation_text");
		head.createCell(7, CellType.STRING).setCellValue("data-cost");
		head.createCell(8, CellType.STRING).setCellValue("data-cost_text");

		List<AdeptPower> list = Shadowrun6Core.getItemList(AdeptPower.class);
		Collections.sort(list, new Comparator<AdeptPower>() {
			public int compare(AdeptPower o1, AdeptPower o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (AdeptPower item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(1, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(4, CellType.STRING).setCellValue(item.getId());

			Converter.convertAdeptPower( item, locales[0], row, 5);
		}

		for (int i=0; i<14; i++) {
			if (i==3 ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createAdeptPowersBlob(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("Blobs|Powers");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		head.createCell(2, CellType.STRING).setCellValue("data-name");
		head.createCell(3, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(4, CellType.STRING).setCellValue("data-description");
		head.createCell(5, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(6, CellType.STRING).setCellValue("data-activation");
		head.createCell(7, CellType.STRING).setCellValue("data-activation_text");
		head.createCell(8, CellType.STRING).setCellValue("data-cost");
		head.createCell(9, CellType.STRING).setCellValue("data-cost_text");

		List<AdeptPower> list = Shadowrun6Core.getItemList(AdeptPower.class);
		Collections.sort(list, new Comparator<AdeptPower>() {
			public int compare(AdeptPower o1, AdeptPower o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (AdeptPower item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(1, CellType.STRING).setCellValue("Powers:"+capitalize(item.getName(locales[0])));
			row.createCell(2, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(4, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(5, CellType.STRING).setCellValue(item.getId());

			Converter.convertAdeptPower( item, locales[0], row, 6);
		}

		for (int i=0; i<14; i++) {
			if (i==4) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createCritterPowers(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Critterpower");
		int rowNum =0;
		int blobOffset = blob?1:0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");

		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-action");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-range");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-duration");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-level");

		List<CritterPower> list = Shadowrun6Core.getItemList(CritterPower.class);
		list = list.stream().filter(p -> p.getType()!=ASpell.Type.RESONANCE).collect(Collectors.toList());
		Collections.sort(list, new Comparator<CritterPower>() {
			public int compare(CritterPower o1, CritterPower o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (CritterPower item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue("Spells:"+capitalize(item.getName(locales[0])));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertCritterPower((CritterPower) item, locales[0], row, 5+blobOffset);
		}
		for (int i=0; i<21; i++) {
			if (i==(3+blobOffset)) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createSpells(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		Sheet sheet = workbook.createSheet(blob?("Blobs|Spells"):"Spells");
		int rowNum =0;
		int blobOffset = blob?1:0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-notes");

		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-range");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-duration");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-drain");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-category");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-effect_type");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-skill");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-damage");

		head.createCell(14+blobOffset, CellType.STRING).setCellValue("data-category_text");
		head.createCell(15+blobOffset, CellType.STRING).setCellValue("data-duration_text");
		head.createCell(16+blobOffset, CellType.STRING).setCellValue("data-drain_text");
		head.createCell(17+blobOffset, CellType.STRING).setCellValue("data-range_text");
		head.createCell(18+blobOffset, CellType.STRING).setCellValue("data-type_text");
		head.createCell(19+blobOffset, CellType.STRING).setCellValue("data-damage_text");
		head.createCell(20+blobOffset, CellType.STRING).setCellValue("data-features_text");

		List<SR6Spell> list = Shadowrun6Core.getItemList(SR6Spell.class);
		Collections.sort(list, new Comparator<SR6Spell>() {
			public int compare(SR6Spell o1, SR6Spell o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (SR6Spell item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo("Spells:"+capitalize(item.getName(locales[0])), item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(item.getId());
			row.createCell(5+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));

			Converter.convertSpell((SR6Spell) item, locales[0], row, 6+blobOffset);
		}
		for (int i=0; i<21; i++) {
			if (i==(3+blobOffset) || i==(5+blobOffset)) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createMetamagic(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Metamagic");
		int rowNum =0;
		Row head = sheet.createRow(0);
		int blobOffset = blob?1:0;
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");

		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-rating");

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
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue("Metamagic:"+capitalize(item.getName(locales[0])));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertEcho((MetamagicOrEcho) item, locales[0], row, 5+blobOffset);
		}
		for (int i=0; i<20; i++) {
			if (i==(3+blobOffset) ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createComplexForms(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		int blobOffset = blob?1:0;
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+Converter.getCategoryName(ComplexForm.class));
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");

		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-duration");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-fade");

		List<ComplexForm> list = Shadowrun6Core.getItemList(ComplexForm.class);
		Collections.sort(list, new Comparator<ComplexForm>() {
			public int compare(ComplexForm o1, ComplexForm o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ComplexForm item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue("Spells:"+capitalize(item.getName(locales[0])));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertComplexForm((ComplexForm) item, locales[0], row, 5);
		}
		for (int i=0; i<20; i++) {
			if (i==(3+blobOffset) ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createEchoes(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Echoes");
		int rowNum =0;
		Row head = sheet.createRow(0);
		int blobOffset = blob?1:0;
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(5, CellType.STRING).setCellValue("data-rating");

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
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue("Echoes:"+capitalize(item.getName(locales[0])));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(item.getId());
			Converter.convertEcho((MetamagicOrEcho) item, locales[0], row, 5);
		}
		for (int i=0; i<20; i++) {
			if (i==(3+blobOffset) ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	public static String pretty(String original) {
		if (original==null) return null;
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
//		System.err.println("Fail for: "+original);
		return "";
	}

	//-------------------------------------------------------------------
	static String extractGameEffect(String original) {
		String needle = "<b>Game Effect:</b> ";
		int costIndex = original.indexOf(needle);
		if (costIndex>0)
			return original.substring(costIndex+needle.length(), original.length());
		System.err.println("Fail for "+original);
		return original;
	}

	//-------------------------------------------------------------------
	static String getCostText(Quality qual) {
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
		head.createCell(1, CellType.STRING).setCellValue("data-name");
		head.createCell(2, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3, CellType.STRING).setCellValue("data-description");
		head.createCell(4, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(5, CellType.STRING).setCellValue("data-type");
		head.createCell(6, CellType.STRING).setCellValue("data-positive");
		head.createCell(7, CellType.STRING).setCellValue("data-maxLevel");
		head.createCell(8, CellType.STRING).setCellValue("data-karma");
		head.createCell(9, CellType.STRING).setCellValue("data-game_effect");
		head.createCell(10, CellType.STRING).setCellValue("data-cost_text");

		List<SR6Quality> list = Shadowrun6Core.getItemList(SR6Quality.class);
		Collections.sort(list, new Comparator<SR6Quality>() {
			public int compare(SR6Quality o1, SR6Quality o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Quality item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());
			if (item.getDescription(locales[0])==null) {
				logger.log(Level.ERROR, "No description for "+item);
			}

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(1, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3, CellType.STRING).setCellValue(pretty(extractDescripton(pretty(item.getDescription(locales[0])))));
			row.createCell(4, CellType.STRING).setCellValue(item.getId());
			Converter.convertQuality(item, locales[0], row, 5);
		}

		for (int i=0; i<14; i++) {
			if (i==3 || i==9) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createQualitiesBlobs(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback) throws IOException {
		Sheet sheet = workbook.createSheet("Blobs|Qualities");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		head.createCell(2, CellType.STRING).setCellValue("data-name");
		head.createCell(3, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(4, CellType.STRING).setCellValue("data-description");
		head.createCell(5, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(6, CellType.STRING).setCellValue("data-type");
		head.createCell(7, CellType.STRING).setCellValue("data-positive");
		head.createCell(8, CellType.STRING).setCellValue("data-maxLevel");
		head.createCell(9, CellType.STRING).setCellValue("data-karma");
		head.createCell(10, CellType.STRING).setCellValue("data-game_effect");
		head.createCell(11, CellType.STRING).setCellValue("data-cost_text");

		List<SR6Quality> list = Shadowrun6Core.getItemList(SR6Quality.class);
		Collections.sort(list, new Comparator<SR6Quality>() {
			public int compare(SR6Quality o1, SR6Quality o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Quality item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(1, CellType.STRING).setCellValue("Qualities:"+capitalize(item.getName(locales[0])));
			row.createCell(2, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(3, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(4, CellType.STRING).setCellValue(pretty(extractDescripton(pretty(item.getDescription(locales[0])))));
			row.createCell(5, CellType.STRING).setCellValue(item.getId());
			Converter.convertQuality(item, locales[0], row, 6);
		}

		for (int i=0; i<14; i++) {
			if (i==4 || i==10) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createAugmentations(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		int blobOffset = blob?1:0;
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Augmentations");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-wifibonus");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-overdrive");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemtype");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemsubtype");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-app_commlinkVariantID");

		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-availability");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-cost");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-cost_text");
		head.createCell(14+blobOffset, CellType.STRING).setCellValue("data-has_rating");
		head.createCell(15+blobOffset, CellType.STRING).setCellValue("data-capacity_cost");
		head.createCell(16+blobOffset, CellType.STRING).setCellValue("data-essence_cost");
		head.createCell(17+blobOffset, CellType.STRING).setCellValue("data-modification");

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
				row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
				if (blob) {
					String first = Converter.getCategoryPage(item,locales[0]);
					row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo(first, item, locales[0]));
				}
				row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
				row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
				row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-augment");
				row.createCell(5+blobOffset, CellType.STRING).setCellValue(pretty(item.getWiFi(locales[0])));
				row.createCell(6+blobOffset, CellType.STRING).setCellValue(pretty(item.getOverdrive(locales[0])));
				row.createCell(7+blobOffset, CellType.STRING).setCellValue(item.getId());

				Converter.convertAugmentation(item, locales[0], row, 8+blobOffset);
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
					if (blob) {
						String first = Converter.getCategoryPage(item,locales[0]);
						row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo(first, item, locales[0]));
					}
					row.createCell(1+blobOffset, CellType.STRING).setCellValue(carried.getNameWithoutRating(locales[0]));
					row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
					row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
					row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-augment");
					row.createCell(5+blobOffset, CellType.STRING).setCellValue(pretty(item.getWiFi(locales[0])));
					row.createCell(6+blobOffset, CellType.STRING).setCellValue(pretty(item.getOverdrive(locales[0])));
					row.createCell(7+blobOffset, CellType.STRING).setCellValue(item.getId());
					Converter.convertAugmentation(carried, locales[0], row, 8+blobOffset);
				}
			}
		}

		for (int i=0; i<20; i++) {
			if (i==3+blobOffset || i==5+blobOffset|| i==6+blobOffset) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createWeapons(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob, String category, ItemSubType...subtypes) throws IOException {
		int blobOffset = blob?1:0;
			Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+category);
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-Category");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-wifibonus");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemtype");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemsubtype");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-availability");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-cost");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-cost_text");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-skill");
		head.createCell(14+blobOffset, CellType.STRING).setCellValue("data-skillspec");
		head.createCell(15+blobOffset, CellType.STRING).setCellValue("data-dv");
		head.createCell(16+blobOffset, CellType.STRING).setCellValue("data-close");
		head.createCell(17+blobOffset, CellType.STRING).setCellValue("data-near");
		head.createCell(18+blobOffset, CellType.STRING).setCellValue("data-medium");
		head.createCell(19+blobOffset, CellType.STRING).setCellValue("data-far");
		head.createCell(20+blobOffset, CellType.STRING).setCellValue("data-extreme");
		head.createCell(21+blobOffset, CellType.STRING).setCellValue("data-firing_modes");
		head.createCell(22+blobOffset, CellType.STRING).setCellValue("data-ammo");

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
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				String first = Converter.getCategoryPage(item,locales[0]);
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo(first, item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue( (item.getItemType()==ItemType.WEAPON_CLOSE_COMBAT)?"weapon-melee":"weapon-ranged");
			row.createCell(5+blobOffset, CellType.STRING).setCellValue( (item.getItemType()==ItemType.WEAPON_CLOSE_COMBAT)?"Melee":"Ranged");
			row.createCell(6+blobOffset, CellType.STRING).setCellValue(pretty(item.getWiFi(locales[0])));
			row.createCell(7+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertWeapon(item, locales[0], row,8+blobOffset);
		}

		for (int i=0; i<20; i++) {
			if (i==(3+blobOffset) || i==(6+blobOffset) || (blob && i==1) ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createRituals(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Rituals");
		int rowNum =0;
		Row head = sheet.createRow(0);
		int blobOffset = blob?1:0;
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-threshold");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-features");


		List<Ritual> list = Shadowrun6Core.getItemList(Ritual.class);
		Collections.sort(list, new Comparator<Ritual>() {
			public int compare(Ritual o1, Ritual o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Ritual item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue("Rituals:"+capitalize(item.getName(locales[0])));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertRitual(item, locales[0], row, 5+blobOffset);
		}

		for (int i=0; i<11; i++) {
			if (i==3+blobOffset) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void createVehicles(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		int blobOffset = blob?1:0;
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Vehicles");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemtype");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemsubtype");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-Category");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-availability");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-cost");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-cost_text");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-drone_type");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-handling_onroad");
		head.createCell(14+blobOffset, CellType.STRING).setCellValue("data-handling_offroad");
		head.createCell(15+blobOffset, CellType.STRING).setCellValue("data-acceleration");
		head.createCell(16+blobOffset, CellType.STRING).setCellValue("data-speed_base");
		head.createCell(17+blobOffset, CellType.STRING).setCellValue("data-speed_base_offroad");
		head.createCell(18+blobOffset, CellType.STRING).setCellValue("data-speed_max");
		head.createCell(19+blobOffset, CellType.STRING).setCellValue("data-speed_max_offroad");
		head.createCell(20+blobOffset, CellType.STRING).setCellValue("data-body_base");
		head.createCell(21+blobOffset, CellType.STRING).setCellValue("data-armor_base");
		head.createCell(22+blobOffset, CellType.STRING).setCellValue("data-pilot_base");
		head.createCell(23+blobOffset, CellType.STRING).setCellValue("data-sensor_base");
		head.createCell(24+blobOffset, CellType.STRING).setCellValue("data-seats");
		head.createCell(25+blobOffset, CellType.STRING).setCellValue("data-physical");
		head.createCell(26+blobOffset, CellType.STRING).setCellValue("data-matrix");

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
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				String first = Converter.getCategoryPage(item,locales[0]);
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo(first, item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue( "drone");
			row.createCell(5+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertVehicle(item, locales[0], row, 6+blobOffset);
		}

		for (int i=0; i<28; i++) {
			if (i==(3+blobOffset) || (blob && i==1) ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createArmor(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		int blobOffset = blob?1:0;
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Armor");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-wifibonus");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemtype");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemsubtype");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-availability");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-cost");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-cost_text");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-defense");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-social");

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
			if (item.getItemType()!=ItemType.ARMOR && item.getItemType()!=ItemType.ARMOR_ADDITION  &&item.getItemSubtype()!=ItemSubType.ARMOR_BODY)
				continue;
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				String first = Converter.getCategoryPage(item,locales[0]);
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo(first, item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-armor");
			row.createCell(5+blobOffset, CellType.STRING).setCellValue(pretty(item.getWiFi(locales[0])));
			row.createCell(6+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertArmor(item, locales[0], row, 7+blobOffset);
		}

		for (int i=0; i<11; i++) {
			if ((i==1 && blob) || i==(3+blobOffset)|| i==(5+blobOffset)) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createOtherGear(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		int blobOffset = blob?1:0;
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Gear");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-wifibonus");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemtype");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemsubtype");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-availability");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-cost");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-cost_text");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-rating");

		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				int cmp = Integer.compare(o1.getItemType().ordinal(), o2.getItemType().ordinal());
				if (cmp!=0) return cmp;
				cmp = Integer.compare(o1.getItemSubtype().ordinal(), o2.getItemSubtype().ordinal());
				if (cmp!=0) return cmp;
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ItemTemplate item : list) {
			if (ItemType.isVehicle(item.getItemType()))
					continue;
			if (ItemType.isWeapon(item.getItemType()))
				continue;
			if (item.getItemType()==ItemType.ARMOR || item.getItemType()==ItemType.ARMOR_ADDITION  || item.getItemSubtype()==ItemSubType.ARMOR_BODY )
				continue;
			if (item.getItemType()==ItemType.SOFTWARE)
				continue;
			if (item.getItemType()==ItemType.CYBERWARE || item.getItemType()==ItemType.BIOWARE)
				continue;
			switch (item.getItemSubtype()) {
			case COMMLINK:
			case CYBERDECK:
			case DATATERM:
			case CYBERTERM:
			case RIGGER_CONSOLE:
			case TAC_NET:
				continue;
			default:
				if ("cyberjack".equals(item.getId()))
					continue;
				if ("control_rig".equals(item.getId()))
					continue;
			}
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				String first = Converter.getCategoryPage(item,locales[0]);
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo(first, item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-misc");
			row.createCell(5+blobOffset, CellType.STRING).setCellValue(pretty(item.getWiFi(locales[0])));
			row.createCell(6+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertOtherGear(item, locales[0], row, 7+blobOffset);
		}

		for (int i=0; i<11; i++) {
			if (i==(3+blobOffset) || i==(5+blobOffset)) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createDevices(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		int blobOffset = blob?1:0;
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"Devices");
		int rowNum =0;
		Row head = sheet.createRow(0);
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("data-type");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemtype");
		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-app_commlinkItemsubtype");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-Category");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-availability");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-cost");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-cost_text");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-device_type");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-rating");
		head.createCell(14+blobOffset, CellType.STRING).setCellValue("data-device_rating");
		head.createCell(15+blobOffset, CellType.STRING).setCellValue("data-dattack");
		head.createCell(16+blobOffset, CellType.STRING).setCellValue("data-sleaze");
		head.createCell(17+blobOffset, CellType.STRING).setCellValue("data-data_processing");
		head.createCell(18+blobOffset, CellType.STRING).setCellValue("data-firewall");
		head.createCell(19+blobOffset, CellType.STRING).setCellValue("data-active_program_slots");

		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				int cmp = Integer.compare(o1.getItemType().ordinal(), o2.getItemType().ordinal());
				if (cmp!=0) return cmp;
				cmp = Integer.compare(o1.getItemSubtype().ordinal(), o2.getItemSubtype().ordinal());
				if (cmp!=0) return cmp;
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ItemTemplate item : list) {
			switch (item.getItemSubtype()) {
			case COMMLINK:
			case CYBERDECK:
			case DATATERM:
			case CYBERTERM:
			case RIGGER_CONSOLE:
			case TAC_NET:
				break;
			default:
				if ("cyberjack".equals(item.getId()))
					break;
				if ("control_rig".equals(item.getId()))
					break;
				continue;
			}
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo("Devices:"+capitalize(item.getName(locales[0])), item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(pretty(item.getDescription(locales[0])));
			switch (item.getItemSubtype()) {
			case COMMLINK:
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-commlink"); break;
			case CYBERDECK:
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-cyberdeck"); break;
			case DATATERM:
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-dataterm"); break;
			case CYBERTERM:
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-cyberterm"); break;
			case RIGGER_CONSOLE:
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-command_console"); break;
			case TAC_NET:
				row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-mtoc"); break;
			default:
				if ("cyberjack".equals(item.getId())) {
					row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-cyberjack");
				} else if ("control_rig".equals(item.getId())) {
					row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-control_rig");
				}
			}
			row.createCell(4+blobOffset, CellType.STRING).setCellValue( "gear-misc");
			row.createCell(5+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertDevice(item, locales[0], row, 6+blobOffset);
		}

		for (int i=0; i<20; i++) {
			if (i==(2+blobOffset) || i==(3+blobOffset)) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
	}

	//-------------------------------------------------------------------
	private static void createGrunts(Workbook workbook, Function<Collection<PageReference>,Locale[]> localeCallback, boolean blob) throws IOException {
		Sheet sheet = workbook.createSheet((blob?"Blobs|":"")+"NPCs");
		int rowNum =0;
		Row head = sheet.createRow(0);
		int blobOffset = blob?1:0;
		head.createCell(0, CellType.STRING).setCellValue("Name");
		if (blob) {
			head.createCell(1, CellType.STRING).setCellValue("belongs_to");
		}
		head.createCell(1+blobOffset, CellType.STRING).setCellValue("data-Category");
		head.createCell(2+blobOffset, CellType.STRING).setCellValue("data-sub_type");
		head.createCell(3+blobOffset, CellType.STRING).setCellValue("data-name");
		head.createCell(4+blobOffset, CellType.STRING).setCellValue("Sourcebook");
		head.createCell(5+blobOffset, CellType.STRING).setCellValue("data-description");
		head.createCell(6+blobOffset, CellType.STRING).setCellValue("data-app_commlinkID");

		head.createCell(7+blobOffset, CellType.STRING).setCellValue("data-pr");
		head.createCell(8+blobOffset, CellType.STRING).setCellValue("data-body_base");
		head.createCell(9+blobOffset, CellType.STRING).setCellValue("data-body_modified");
		head.createCell(10+blobOffset, CellType.STRING).setCellValue("data-agility_base");
		head.createCell(11+blobOffset, CellType.STRING).setCellValue("data-agility_modified");
		head.createCell(12+blobOffset, CellType.STRING).setCellValue("data-reaction_base");
		head.createCell(13+blobOffset, CellType.STRING).setCellValue("data-reaction_modified");
		head.createCell(14+blobOffset, CellType.STRING).setCellValue("data-strength_base");
		head.createCell(15+blobOffset, CellType.STRING).setCellValue("data-strength_modified");
		head.createCell(16+blobOffset, CellType.STRING).setCellValue("data-willpower_base");
		head.createCell(17+blobOffset, CellType.STRING).setCellValue("data-willpower_modified");
		head.createCell(18+blobOffset, CellType.STRING).setCellValue("data-logic_base");
		head.createCell(19+blobOffset, CellType.STRING).setCellValue("data-logic_modified");
		head.createCell(20+blobOffset, CellType.STRING).setCellValue("data-intuition_base");
		head.createCell(21+blobOffset, CellType.STRING).setCellValue("data-intuition_modified");
		head.createCell(22+blobOffset, CellType.STRING).setCellValue("data-charisma_base");
		head.createCell(23+blobOffset, CellType.STRING).setCellValue("data-charisma_modified");

		head.createCell(24+blobOffset, CellType.STRING).setCellValue("data-edge");
		head.createCell(25+blobOffset, CellType.STRING).setCellValue("data-essence");
		head.createCell(26+blobOffset, CellType.STRING).setCellValue("data-magic");

		List<SR6NPC> list = Shadowrun6Core.getItemList(SR6NPC.class);
		list = list.stream().filter(n -> n.getType()==NPCType.GRUNT).toList();
//		Collections.sort(list, new Comparator<SR6NPC>() {
//			public int compare(SR6NPC o1, SR6NPC o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
		for (SR6NPC item : list) {
			Locale[] locales = localeCallback.apply(item.getPageReferences());

			Row row = sheet.createRow(++rowNum);
			row.createCell(0, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			if (blob) {
				row.createCell(1, CellType.STRING).setCellValue(Converter.makeBelongsTo("NPCs:"+capitalize(item.getName(locales[0])), item, locales[0]));
			}
			row.createCell(1+blobOffset, CellType.STRING).setCellValue("NPCs");
			row.createCell(2+blobOffset, CellType.STRING).setCellValue(item.getType().name().toLowerCase());
			row.createCell(3+blobOffset, CellType.STRING).setCellValue(capitalize(item.getName(locales[0])));
			row.createCell(4+blobOffset, CellType.STRING).setCellValue(createSourceText(item, locales[0]));
			row.createCell(5+blobOffset, CellType.STRING).setCellValue(item.getDescription(locales[0]));
			row.createCell(6+blobOffset, CellType.STRING).setCellValue(item.getId());

			Converter.convertGrunt(item, locales[0], row, 7+blobOffset);
			if (!blob) {
				Converter.convertGruntBlobs(item, locales[0]);
			}
		}

		for (int i=0; i<11; i++) {
			if (i==(5+blobOffset) ) {
				sheet.setColumnWidth(i, 5000);
				continue;
			};
			sheet.autoSizeColumn(i);
		}
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
