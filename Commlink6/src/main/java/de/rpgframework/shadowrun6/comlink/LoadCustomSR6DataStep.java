package de.rpgframework.shadowrun6.comlink;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.StartupStep;
import de.rpgframework.genericrpg.data.CustomDataSetHandle;
import de.rpgframework.genericrpg.data.CustomDataSetHandle.DataSetEntry;
import de.rpgframework.genericrpg.data.CustomDataSetManager;
import de.rpgframework.genericrpg.data.CustomDataSetManagerLoader;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.GenericCore;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;

import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.ActionList;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerList;
import de.rpgframework.shadowrun6.items.AmmunitionType;
import de.rpgframework.shadowrun6.items.AmmunitionTypeList;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormList;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.NPCList;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerList;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoList;
import de.rpgframework.shadowrun.Focus;
import de.rpgframework.shadowrun.FocusList;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import de.rpgframework.shadowrun6.items.ItemEnhancementList;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.LifestyleQualityList;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MagicOrResonanceTypeList;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.MentorSpiritList;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.MetaTypeList;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.QualityList;
import de.rpgframework.shadowrun.RitualFeature;
import de.rpgframework.shadowrun.RitualFeatureList;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualList;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.chargen.RuleInterpretationList;
import de.rpgframework.shadowrun6.Sense;
import de.rpgframework.shadowrun6.SenseList;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SkillList;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.SpellFeatureList;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.SR6SpellList;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.TraditionList;

import de.rpgframework.shadowrun6.Technique;
import de.rpgframework.shadowrun6.TechniqueList;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsList;

import de.rpgframework.shadowrun6.vehicle.ChassisType;
import de.rpgframework.shadowrun6.vehicle.ChassisTypeList;
import de.rpgframework.shadowrun6.vehicle.ConsoleType;
import de.rpgframework.shadowrun6.vehicle.ConsoleTypeList;
import de.rpgframework.shadowrun6.vehicle.DesignMod;
import de.rpgframework.shadowrun6.vehicle.DesignModList;
import de.rpgframework.shadowrun6.vehicle.DesignOption;
import de.rpgframework.shadowrun6.vehicle.DesignOptionList;
import de.rpgframework.shadowrun6.vehicle.Powertrain;
import de.rpgframework.shadowrun6.vehicle.PowertrainList;
import de.rpgframework.shadowrun6.vehicle.QualityFactor;
import de.rpgframework.shadowrun6.vehicle.QualityFactorList;

import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureList;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathList;

import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.ContactTypeList;
import de.rpgframework.genericrpg.SetItem;
import de.rpgframework.genericrpg.SetItemList;

import de.rpgframework.shadowrun6.DrakeType;
import de.rpgframework.shadowrun6.DrakeTypeList;

import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleList;


/**
 * @author prelle
 *
 */
public class LoadCustomSR6DataStep implements StartupStep {

	private static Logger logger = System.getLogger("de.rpgframework.shadowrun6.data");

	//-------------------------------------------------------------------
	public LoadCustomSR6DataStep() {
	}

	//-------------------------------------------------------------------
	private static Class<? extends DataItem> guessItemType(String name) {
		if (name.equals("hello") || name.startsWith("gear")|| name.startsWith("packs")|| name.startsWith("item")|| name.startsWith("equipment"))
			return ItemTemplate.class;
		if (name.startsWith("actions"))
			return Shadowrun6Action.class;
		if (name.contains("adeptpower"))
			return AdeptPower.class;
		if (name.startsWith("ammunition_types"))
			return AmmunitionType.class;
		if (name.startsWith("complex"))
			return ComplexForm.class;
		if (name.startsWith("contacts")|| name.startsWith("critters")|| name.startsWith("npcs")|| name.startsWith("spirits")|| name.startsWith("sprites"))
			return SR6NPC.class;
		if (name.startsWith("critterpower")|| name.startsWith("spritepower"))
			return CritterPower.class;
		if (name.startsWith("echoes")|| name.startsWith("metamagic")|| name.startsWith("neuromorphism")|| name.startsWith("transhumanism")|| name.startsWith("dracogenesis"))
			return MetamagicOrEcho.class;
		if (name.startsWith("foci"))
			return Focus.class;
		if (name.contains("enhancement")|| name.contains("modification"))
			return SR6ItemEnhancement.class;
		if (name.startsWith("lifestyles"))
			return LifestyleQuality.class;
		if (name.startsWith("magicOrResonance"))
			return MagicOrResonanceType.class;
		if (name.startsWith("mentor"))
			return MentorSpirit.class;
		if (name.startsWith("metatype"))
			return SR6MetaType.class;
		if (name.startsWith("qualities"))
			return SR6Quality.class;
		if (name.startsWith("ritualfeatures"))
			return RitualFeature.class;
		if (name.startsWith("ritual"))
			return Ritual.class;
		if (name.startsWith("rules"))
			return RuleInterpretation.class;
		if (name.startsWith("senses"))
			return Sense.class;
		if (name.startsWith("skills"))
			return SR6Skill.class;
		if (name.startsWith("spellfeature"))
			return SpellFeature.class;
		if (name.startsWith("spells"))
			return SR6Spell.class;
		if (name.startsWith("tradition"))
			return Tradition.class;

		if (name.startsWith("techniques"))
			return Technique.class;
		if (name.startsWith("martialarts"))
			return MartialArts.class;

		if (name.startsWith("chassisTypes"))
			return ChassisType.class;
		if (name.startsWith("powertrains"))
			return Powertrain.class;
		if (name.startsWith("consoleTypes"))
			return ConsoleType.class;
		if (name.startsWith("designOptions"))
			return DesignOption.class;
		if (name.startsWith("designMods"))
			return DesignMod.class;
		if (name.startsWith("qualityFactors"))
			return QualityFactor.class;

		if (name.startsWith("datastructures"))
			return DataStructure.class;
		if (name.startsWith("quality_paths"))
			return QualityPath.class;

		if (name.startsWith("contact_types"))
			return ContactType.class;
		if (name.startsWith("collectives"))
			return SetItem.class;

		if (name.startsWith("draketypes"))
			return DrakeType.class;

		if (name.startsWith("lifepath"))
			return LifepathModule.class;

		return null;
	}

	//-------------------------------------------------------------------
	private static Class<? extends List> guessListType(String name) {
		if (name.equals("hello") || name.startsWith("gear")|| name.startsWith("packs")|| name.startsWith("item")|| name.startsWith("equipment"))
			return ItemTemplateList.class;
		if (name.startsWith("actions"))
			return ActionList.class;
		if (name.contains("adeptpower"))
			return AdeptPowerList.class;
		if (name.startsWith("ammunition_types"))
			return AmmunitionTypeList.class;
		if (name.startsWith("complex"))
			return ComplexFormList.class;
		if (name.startsWith("contacts")|| name.startsWith("critters")|| name.startsWith("npcs")|| name.startsWith("spirits")|| name.startsWith("sprites"))
			return NPCList.class;
		if (name.startsWith("critterpower")|| name.startsWith("spritepower"))
			return CritterPowerList.class;
		if (name.startsWith("echoes")|| name.startsWith("metamagic")|| name.startsWith("neuromorphism")|| name.startsWith("transhumanism")|| name.startsWith("dracogenesis"))
			return MetamagicOrEchoList.class;
		if (name.startsWith("foci"))
			return FocusList.class;
		if (name.contains("enhancement")|| name.contains("modification"))
			return ItemEnhancementList.class;
		if (name.startsWith("lifestyles"))
			return LifestyleQualityList.class;
		if (name.startsWith("magicOrResonance"))
			return MagicOrResonanceTypeList.class;
		if (name.startsWith("mentor"))
			return MentorSpiritList.class;
		if (name.startsWith("metatype"))
			return MetaTypeList.class;
		if (name.startsWith("qualities"))
			return QualityList.class;
		if (name.startsWith("ritualfeatures"))
			return RitualFeatureList.class;
		if (name.startsWith("ritual"))
			return RitualList.class;
		if (name.startsWith("rules"))
			return RuleInterpretationList.class;
		if (name.startsWith("senses"))
			return SenseList.class;
		if (name.startsWith("skills"))
			return SkillList.class;
		if (name.startsWith("spellfeature"))
			return SpellFeatureList.class;
		if (name.startsWith("spells"))
			return SR6SpellList.class;
		if (name.startsWith("tradition"))
			return TraditionList.class;

		if (name.startsWith("techniques"))
			return TechniqueList.class;
		if (name.startsWith("martialarts"))
			return MartialArtsList.class;

		if (name.startsWith("chassisTypes"))
			return ChassisTypeList.class;
		if (name.startsWith("powertrains"))
			return PowertrainList.class;
		if (name.startsWith("consoleTypes"))
			return ConsoleTypeList.class;
		if (name.startsWith("designOptions"))
			return DesignOptionList.class;
		if (name.startsWith("designMods"))
			return DesignModList.class;
		if (name.startsWith("qualityFactors"))
			return QualityFactorList.class;

		if (name.startsWith("datastructures"))
			return DataStructureList.class;
		if (name.startsWith("quality_paths"))
			return QualityPathList.class;

		if (name.startsWith("contact_types"))
			return ContactTypeList.class;
		if (name.startsWith("collectives"))
			return SetItemList.class;

		if (name.startsWith("draketypes"))
			return DrakeTypeList.class;

		if (name.startsWith("lifepath"))
			return LifepathModuleList.class;

		return null;
	}

	//-------------------------------------------------------------------
	private <E extends DataItem> List<E> loadList(DataSetEntry<E> file, DataSet set, CustomDataSetHandle handle) {
		CustomDataSetManager manager = CustomDataSetManagerLoader.getInstance();
		InputStream data = manager.getDataFile(handle, file.key());
		if (data==null) {
			logger.log(Level.ERROR, "Did not get inputstream for custom data {0}", file.key());
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Error accessing custom data for "+file.key());
			return null;
		}
		Class<E> clsItem = file.clazz();
		Class<? extends List<E>> clsList = file.listClazz();
		if (clsItem==null) clsItem = (Class<E>) guessItemType(file.key());
		if (clsList==null) clsList = (Class<? extends List<E>>) guessListType(file.key());
		if (clsItem==null) {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Missing class type for custom data "+file.key());
			return null;
		}
		if (clsList==null) {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Missing list class type for custom data "+file.key());
			return null;
		}
		try {
			List<E> dataList = GenericCore.loadDataItems(
					clsList,
					clsItem, set, data);
			logger.log(Level.INFO, "  Custom Data: Loaded {0} elements of {1}", dataList.size(), file.clazz());
			return dataList;
		} catch (IOException e) {
			logger.log(Level.ERROR, "Error loading "+file.key(),e);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Error loading custom data "+file.key()+" as "+clsList.getSimpleName()+"\n"+e.getMessage(),e);
		}
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		CustomDataSetManager manager = CustomDataSetManagerLoader.getInstance();
		if (manager==null) {
			logger.log(Level.WARNING, "No custom data handler found");
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "No custom data handler found - if you make use of inofficial datasets you won't be able to load them.");
			return;
		}
		logger.log(Level.INFO, "Got instance of Custom Data handler: "+manager);
		List<CustomDataSetHandle> list = manager.getCustomDataProducts();
		for (CustomDataSetHandle handle : list) {
			DataSet set = handle.getName();
			logger.log(Level.INFO, "Custom Data: Load {0}", set.getID(),set.getLocales());
			for (DataSetEntry<?> file : handle.getOrderedFileKeys()) {
				loadList(file, set, handle);
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.StartupStep#canRun()
	 */
	@Override
	public boolean canRun() {
		return true;
	}

}
