package de.rpgframework.shadowrun6.data;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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
import de.rpgframework.shadowrun.Spirit;
import de.rpgframework.shadowrun.SpiritList;
import de.rpgframework.shadowrun.Sprite;
import de.rpgframework.shadowrun.SpriteList;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.TraditionList;
import de.rpgframework.shadowrun6.ActionList;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureList;
import de.rpgframework.shadowrun6.MetaTypeList;
import de.rpgframework.shadowrun6.NPCList;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Ritual;
import de.rpgframework.shadowrun6.SR6RitualList;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.SR6SpellList;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.SkillList;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;
import de.rpgframework.shadowrun6.items.SR6AlternateUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
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

	private static Logger logger = System.getLogger("shadowrun6.data");

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
	public void init() {
		if (alreadyInitialized)
			return;
		double totalPlugins = 23.0;
		double count = 0;
		alreadyInitialized = true;
		logger.log(Level.INFO, "START -------------------------------Core-----------------------------------------------");
		DataSet core = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE", "core.i18n", Locale.ENGLISH, Locale.GERMAN);
//		PluginSkeleton CORE = new PluginSkeleton("CORE", "Splittermond Core Rules");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		try {
			list = Shadowrun6Core.loadDataItems(SkillList.class, SR6Skill.class, core, clazz,"core/data/skills.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" skills");
			list = Shadowrun6Core.loadDataItems(SpellFeatureList.class, SpellFeature.class, core, clazz, "core/data/spellfeatures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spell features");
			list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, core, clazz, "core/data/spells.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spells");
			list = Shadowrun6Core.loadDataItems(RitualFeatureList.class, RitualFeature.class, core, clazz, "core/data/ritualfeatures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ritual features");
			list = Shadowrun6Core.loadDataItems(SR6RitualList.class, SR6Ritual.class, core, clazz, "core/data/rituals.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" rituals");
			list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, core, clazz, "core/data/adeptpowers.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
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
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, core, clazz,"core/data/metamagics.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metamagics");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, core, clazz,"core/data/echoes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" echoes");
			list = Shadowrun6Core.loadDataItems(LifestyleQualityList.class, LifestyleQuality.class, core, clazz,"core/data/lifestyles.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" lifestyle qualities");
			list = Shadowrun6Core.loadDataItems(RitualFeatureList.class, RitualFeature.class, core, clazz,"core/data/ritualfeatures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" ritual features");
			list = Shadowrun6Core.loadDataItems(RitualList.class, Ritual.class, core, clazz,"core/data/rituals.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" rituals");
//			logger.log(Level.ERROR, "Stop here");
//			System.exit(1);
			list = Shadowrun6Core.loadDataItems(MagicOrResonanceTypeList.class, MagicOrResonanceType.class, core, clazz,"core/data/magicOrResonance.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" magic or resonance entries");
			Shadowrun6Core.loadPriorityTableEntries(core, clazz.getResourceAsStream("core/data/priorities.xml"));
			list = Shadowrun6Core.loadDataItems(TraditionList.class, Tradition.class, core, clazz,"core/data/traditions.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" magic traditions");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz, "core/data/critterweapons.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" critter weapons");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz.getResourceAsStream("core/data/npcs.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" NPCs");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz.getResourceAsStream("core/data/critters_awakened.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" awakened critters");
			list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, core, clazz.getResourceAsStream("core/data/contacts.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" NPCs");
			list = Shadowrun6Core.loadDataItems(SpiritList.class, Spirit.class, core, clazz, "core/data/spirits.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" spirits");
			list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, core, clazz.getResourceAsStream("core/data/mentorspirits.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" Mentor Spirit");
			
			list = Shadowrun6Core.loadDataItems(RuleInterpretationList.class, RuleInterpretation.class, core, clazz.getResourceAsStream("core/data/rules.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" rule presets");

			logger.log(Level.INFO, "START -------------------------------COMPANION------------------------------------------");
			DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "COMPANION", "companion.i18n", Locale.ENGLISH);
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "companion/data/qualities-metagenetic.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metagenic qualities");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "companion/data/qualities-infected.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" infected qualities");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "companion/data/qualities.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "companion/data/critterpower.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" critter power");
			list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "companion/data/metatypes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" metatypes");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, core, clazz,"companion/data/transhumanism.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" transhumanisms");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/packs-complete.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" complete PACKs");

			logger.log(Level.INFO, "START -------------------------------HACK&SLASH------------------------------------------");
			set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "HACK_SLASH", "hack_slash.i18n", Locale.ENGLISH);
			list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "hack_slash/data/actions.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" actions");
			list = Shadowrun6Core.loadDataItems(ComplexFormList.class, ComplexForm.class, set, clazz, "hack_slash/data/complexforms.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" complex forms");
			list = Shadowrun6Core.loadDataItems(SpriteList.class, Sprite.class, set, clazz, "hack_slash/data/sprites.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" sprites");
			list = Shadowrun6Core.loadDataItems(DataStructureList.class, DataStructure.class, set, clazz, "hack_slash/data/datastructures.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" data structures");
			list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "hack_slash/data/echoes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" echoes");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "hack_slash/data/qualities.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "hack_slash/data/gear_customcyber.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" custom cyberdeck items");
			list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "hack_slash/data/critterpower.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" technocritter qualities");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "hack_slash/data/qualities_ai.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" vitual lifeform qualities");
			list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "hack_slash/data/metatypes.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" AI types");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"hack_slash/data/gear_codemods.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" codemods");
			list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,"hack_slash/data/mentorspirits.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" paragons");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, set, clazz, "hack_slash/data/qualities_streams.xml");
			logger.log(Level.DEBUG, "Loaded "+list.size()+" streams");

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
	
}
