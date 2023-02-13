package de.rpgframework.shadowrun6.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.prelle.simplepersist.Persister;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.chargen.RuleInterpretationList;
import de.rpgframework.genericrpg.data.ASkillValue;
import de.rpgframework.genericrpg.data.CheckInfluence;
import de.rpgframework.genericrpg.data.CostType;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.IAttribute;
import de.rpgframework.genericrpg.items.AlternateUsage;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.Hook;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.IUsageMode;
import de.rpgframework.genericrpg.items.IVariantMode;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.ANPC;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerList;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.ComplexFormList;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerList;
import de.rpgframework.shadowrun.Focus;
import de.rpgframework.shadowrun.FocusList;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.LifestyleQualityList;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MagicOrResonanceTypeList;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.MentorSpiritList;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoList;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityList;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualFeature;
import de.rpgframework.shadowrun.RitualFeatureList;
import de.rpgframework.shadowrun.RitualList;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.SpellFeatureList;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.TraditionList;
import de.rpgframework.shadowrun6.ActionList;
import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleList;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsList;
import de.rpgframework.shadowrun6.MetaTypeList;
import de.rpgframework.shadowrun6.NPCList;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathList;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.SR6SpellList;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.SkillList;
import de.rpgframework.shadowrun6.Technique;
import de.rpgframework.shadowrun6.TechniqueList;
import de.rpgframework.shadowrun6.items.AmmunitionType;
import de.rpgframework.shadowrun6.items.AmmunitionTypeList;
import de.rpgframework.shadowrun6.items.ItemEnhancementList;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6AlternateUsage;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.items.SR6UsageMode;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunCheckInfluence;
import de.rpgframework.shadowrun6.modifications.ShadowrunCostType;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
public class Shadowrun6DataPlugin  {

	private static Logger logger = System.getLogger("de.rpgframework.shadowrun6.data");

	private static boolean alreadyInitialized = false;

	//--------------------------------------------------------------------
	public Shadowrun6DataPlugin() {
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+ModifiedObjectType.class.getName(), ShadowrunReference.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+CostType.class.getName(), ShadowrunCostType.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+CheckInfluence.class.getName(), ShadowrunCheckInfluence.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IItemAttribute.class.getName(), SR6ItemAttribute.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IAttribute.class.getName(), ShadowrunAttribute.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+Hook.class.getName(), ItemHook.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IUsageMode.class.getName(), SR6UsageMode.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IVariantMode.class.getName(), SR6VariantMode.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+AlternateUsage.class.getName(), SR6AlternateUsage.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+PieceOfGearVariant.class.getName(), SR6PieceOfGearVariant.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+ANPC.class.getName(), SR6NPC.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+ASkillValue.class.getName(), SR6SkillValue.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+ASpell.class.getName(), SR6Spell.class);
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.RulePlugin#init()
	 */
//	@Override
	public synchronized void init() {
		if (alreadyInitialized)
			return;
		double totalPlugins = 23.0;
		double count = 0;
		alreadyInitialized = true;
		logger.log(Level.INFO, "START -------------------------------Core-----------------------------------------------");
		DataSet core = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE", "core.i18n", Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.forLanguageTag("pt"));
		ItemUtil.SOFTWARE_LIBRARY_ITEM.assignToDataSet(core);
		SR6GearTool.recalculate("", null, ItemUtil.SOFTWARE_LIBRARY);

//		PluginSkeleton CORE = new PluginSkeleton("CORE", "Splittermond Core Rules");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		try {
//			list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, core, clazz.getResourceAsStream("core/data/mentorspirits.xml"));
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" Mentor Spirit");
//			System.exit(1);
			list = Shadowrun6Core.loadDataItems(SkillList.class, SR6Skill.class, core, clazz,"core/data/skills.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" skills");
			list = Shadowrun6Core.loadDataItems(SpellFeatureList.class, SpellFeature.class, core, clazz, "core/data/spellfeatures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spell features");
			list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, core, clazz, "core/data/spells.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spells");
			list = Shadowrun6Core.loadDataItems(RitualFeatureList.class, RitualFeature.class, core, clazz, "core/data/ritualfeatures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ritual features");
			list = Shadowrun6Core.loadDataItems(RitualList.class, Ritual.class, core, clazz, "core/data/rituals.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" rituals");
			list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, core, clazz, "core/data/adeptpowers.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
			list = Shadowrun6Core.loadDataItems(FocusList.class, Focus.class, core, clazz, "core/data/foci.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" foci");
			list = Shadowrun6Core.loadDataItems(ComplexFormList.class, ComplexForm.class, core, clazz, "core/data/complexforms.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" complex forms");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, core, clazz, "core/data/qualities.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, core, clazz, "core/data/critterpower.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" critter power");
			list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, core, clazz, "core/data/metatypes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metatypes");
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, core, clazz, "core/data/actions_minor.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" minor actions");
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, core, clazz, "core/data/actions_major.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" major actions");
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, core, clazz, "core/data/actions_matrix.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" matrix actions");
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, core, clazz, "core/data/actions_edge.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" edge actions");
			list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, core, clazz,"core/data/weapon_modifications.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz, "core/data/gear_melee.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" items");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_firearms_accessories.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon accessories");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_firearms.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_armor.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_armor_accessories.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" armor accessories");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_ammunition.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
			list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, core, clazz,"core/data/ammunition_types.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_explosives.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" explosives");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_electronics.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" electronics");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_software.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" software");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_sensors_and_co.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" sensors & co");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_security_survival.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" security & survival gear");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_cyberware.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" cyberware");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_bioware.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" bioware");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_vehicles.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_drones.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_magical.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" magic gear");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, core, clazz,"core/data/metamagics.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metamagics");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, core, clazz,"core/data/echoes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" echoes");
			list = Shadowrun6Core.loadDataItems(LifestyleQualityList.class, LifestyleQuality.class, core, clazz,"core/data/lifestyles.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" lifestyle qualities");
			list = Shadowrun6Core.loadDataItems(MagicOrResonanceTypeList.class, MagicOrResonanceType.class, core, clazz,"core/data/magicOrResonance.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" magic or resonance entries");
			Shadowrun6Core.loadPriorityTableEntries(core, clazz.getResourceAsStream("core/data/priorities.xml"));
			list = Shadowrun6Core.loadDataItems(TraditionList.class, Tradition.class, core, clazz,"core/data/traditions.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" magic traditions");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz.getResourceAsStream("core/data/npcs.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" NPCs");

			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz.getResourceAsStream("core/data/critters_awakened.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" awakened critters");
//			logger.log(Level.ERROR, "Stop here");
//			System.exit(1);
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz.getResourceAsStream("core/data/contacts.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" Contacts");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz, "core/data/spirits.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spirits");
			list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, core, clazz, "core/data/spritepower.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" sprite powers");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz, "core/data/sprites.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" sprites");
			list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, core, clazz.getResourceAsStream("core/data/mentorspirits.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" Mentor Spirit");
			list = Shadowrun6Core.loadDataItems(RuleInterpretationList.class, RuleInterpretation.class, core, clazz.getResourceAsStream("core/data/rules.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" rule presets");
			//---------Seattle
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear-seattle.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" gear pieces for Seattle Edition");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, core, clazz, "core/data/qualities-seattle.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities for Seattle Edition");

			logger.log(Level.INFO, "START -------------------------------FIRING_SQUAD---------------------------------------");
			DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "FIRING_SQUAD", "firing_squad.i18n", Locale.ENGLISH, Locale.GERMAN);
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "firing_squad/data/actions_edge.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" edge actions");
			list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"firing_squad/data/ammunition_types.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_ammunition.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunitions");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz,"firing_squad/data/qualities.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(QualityPathList.class, QualityPath.class, set, clazz,"firing_squad/data/quality_paths.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" quality paths");
			//ShadowrunCore.loadEquipment     (FSQUAD, clazz.getResourceAsStream("firing_squad/data/gear_underbarrel_weapons.xml"), FSQUAD.getResources(), FSQUAD.getHelpResources());
			list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz,"firing_squad/data/weapon_modifications.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_melee.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" melee weapons");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_firearms.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_firearms_accessories.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms accessories");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_revolution_arms.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_armor_accessories.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" armor accessories");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_mems_accessories.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" MEMS accessories");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_armor.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_electronics.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" electronic items");
			list = Shadowrun6Core.loadDataItems(TechniqueList.class, Technique.class, set, clazz,"firing_squad/data/techniques.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art techniques");
			list = Shadowrun6Core.loadDataItems(MartialArtsList.class, MartialArts.class, set, clazz,"firing_squad/data/martialarts.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art styles");

			logger.log(Level.INFO, "START -------------------------------STREET_WYRD------------------------------------------");
			set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "STREET_WYRD", "street_wyrd.i18n", Locale.ENGLISH, Locale.GERMAN);
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "street_wyrd/data/gear_magical.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" alchemical artifacts");
			list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, set, clazz, "street_wyrd/data/adeptpowers.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "street_wyrd/data/qualities1.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "street_wyrd/data/qualities2.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities (Adept Ways)");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "street_wyrd/data/metamagics.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metamagics for adepts");
			list = Shadowrun6Core.loadDataItems(TraditionList.class, Tradition.class, set, clazz,"street_wyrd/data/traditions.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" magic traditions");
			list = Shadowrun6Core.loadDataItems(SpellFeatureList.class, SpellFeature.class, set, clazz,"street_wyrd/data/spellfeatures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spell features");
			list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, set, clazz,"street_wyrd/data/spells.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spells");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, set, clazz,"street_wyrd/data/spirits.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spirits");
			list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,"street_wyrd/data/mentorspirits.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" mentor spirits");
			list = Shadowrun6Core.loadDataItems(FocusList.class, Focus.class, set, clazz, "street_wyrd/data/foci.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" foci");

			logger.log(Level.INFO, "START -------------------------------COMPANION------------------------------------------");
			set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "COMPANION", "companion.i18n", Locale.ENGLISH, Locale.GERMAN);
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "companion/data/qualities-metagenetic.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metagenic qualities");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "companion/data/qualities-infected.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" infected qualities");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "companion/data/qualities.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(QualityPathList.class, QualityPath.class, set, clazz,"companion/data/quality_paths.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" quality paths");
			list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "companion/data/critterpower.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" critter power");
			list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "companion/data/metatypes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metatypes");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz,"companion/data/transhumanism.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" transhumanisms");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/packs-complete.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" complete PACKs");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/packs-weapons.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon PACKs");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/packs-other.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" other PACKs");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/packs-augments.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" augmentation PACKs");
			list = Shadowrun6Core.loadDataItems(LifepathModuleList.class, LifepathModule.class, set, clazz,"companion/data/lifepath.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" lifepath modules");

			logger.log(Level.INFO, "START -------------------------------HACK&SLASH------------------------------------------");
			set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "HACK_SLASH", "hack_slash.i18n", Locale.ENGLISH);
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "hack_slash/data/actions.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" actions");
//			list = Shadowrun6Core.loadDataItems(ComplexFormList.class, ComplexForm.class, set, clazz, "hack_slash/data/complexforms.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" complex forms");
//			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, set, clazz, "hack_slash/data/sprites.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" sprites");
//			list = Shadowrun6Core.loadDataItems(DataStructureList.class, DataStructure.class, set, clazz, "hack_slash/data/datastructures.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" data structures");
//			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "hack_slash/data/echoes.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" echoes");
////			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "hack_slash/data/qualities.xml");
////			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
//			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "hack_slash/data/gear_customcyber.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" custom cyberdeck items");
//			list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "hack_slash/data/critterpower.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" technocritter qualities");
//			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "hack_slash/data/qualities_ai.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" vitual lifeform qualities");
//			list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "hack_slash/data/metatypes.xml");
//			logger.log(Level.DEBUG, "Loaded "+list.size()+" AI types");
////			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"hack_slash/data/gear_codemods.xml");
////			logger.log(Level.DEBUG, "Loaded "+list.size()+" codemods");
////			list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,"hack_slash/data/mentorspirits.xml");
////			logger.log(Level.DEBUG, "Loaded "+list.size()+" paragons");
////			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "hack_slash/data/qualities_streams.xml");
////			logger.log(Level.DEBUG, "Loaded "+list.size()+" streams");

			logger.log(Level.INFO, "START -------------------------------BODY SHOP-------------------------------------------");
			set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "BODY_SHOP", "body_shop.i18n", Locale.ENGLISH);
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "body_shop/data/actions.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" actions");
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "body_shop/data/actions_edge.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" edge actions");
			list = Shadowrun6Core.loadDataItems(TechniqueList.class, Technique.class, set, clazz,"body_shop/data/techniques.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art techniques");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz,"body_shop/data/qualities.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_cosmetic.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" cosmetic cyberware");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_earware.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" cyberware");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_eyeware.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" cyberware");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_headware.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" cyberware");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_bodyware.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" cyberware");

		} catch (DataErrorException e) {
			logger.log(Level.ERROR, "Failed loading data. In dataset "+e.getDataset().getID()+"\n"+e.getMessage());
			System.err.println("Failed loading data. In dataset "+e.getDataset().getID()+"\n"+e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed loading data",e);
			System.exit(1);
		}
	}

	//-------------------------------------------------------------------
	public static byte[] getPlaceholderGraphic(CarriedItem<ItemTemplate> item) {
		List<String> filenames = new ArrayList<>();
		// Build possible filenames
		if (item.getResolved()!=null) {
			filenames.add(item.getResolved().getId()+".png");
			filenames.add(item.getResolved().getId()+".jpg");
		}
		ItemType type = Shadowrun6Tools.getItemType(item);
		ItemSubType subtype = Shadowrun6Tools.getItemSubType(item);
		filenames.add(type+"_"+subtype+".png");
		filenames.add(type+"_"+subtype+".jpg");
		filenames.add(type+".png");
		filenames.add(type+".jpg");
		// See which image is available
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		for (String file : filenames) {
			InputStream ins = clazz.getResourceAsStream("placeholder/"+file);
			if (ins!=null) {
				try {
					return ins.readAllBytes();
				} catch (IOException e) {
					logger.log(Level.ERROR ,"Failed accessing resource "+file+": "+e);
				}
			} else {
				if (filenames.indexOf(file)>=2)
					logger.log(Level.WARNING, "Failed on ''placeholder/{0}''", file);
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	public static URL getPlaceholderGraphicURL(CarriedItem<ItemTemplate> item) {
		List<String> filenames = new ArrayList<>();
		// Build possible filenames
		if (item.getResolved()!=null) {
			filenames.add(item.getResolved().getId()+".png");
			filenames.add(item.getResolved().getId()+".jpg");
		}
		ItemType type = Shadowrun6Tools.getItemType(item);
		ItemSubType subtype = Shadowrun6Tools.getItemSubType(item);
		filenames.add(type+"_"+subtype+".png");
		filenames.add(type+"_"+subtype+".jpg");
		filenames.add(type+".png");
		filenames.add(type+".jpg");
		// See which image is available
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		for (String file : filenames) {
			logger.log(Level.ERROR, "Check "+file);
			URL ins = clazz.getResource("placeholder/"+file);
			if (ins!=null) {
				return ins;
			}
		}
		return null;
	}

}
