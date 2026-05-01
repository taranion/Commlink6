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
import de.rpgframework.genericrpg.SetItem;
import de.rpgframework.genericrpg.SetItemList;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.genericrpg.chargen.RuleInterpretationList;
import de.rpgframework.genericrpg.data.ASkillValue;
import de.rpgframework.genericrpg.data.CheckInfluence;
import de.rpgframework.genericrpg.data.CostType;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.DataSet.DataSetType;
import de.rpgframework.genericrpg.data.GenericCore;
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
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.ContactTypeList;
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
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureList;
import de.rpgframework.shadowrun6.DrakeType;
import de.rpgframework.shadowrun6.DrakeTypeList;
import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleList;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsList;
import de.rpgframework.shadowrun6.MetaTypeList;
import de.rpgframework.shadowrun6.NPCList;
import de.rpgframework.shadowrun6.QualityList;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathList;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.SR6SpellList;
import de.rpgframework.shadowrun6.Sense;
import de.rpgframework.shadowrun6.SenseList;
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
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import de.rpgframework.shadowrun6.items.SR6UsageMode;
import de.rpgframework.shadowrun6.items.SR6VariantMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunCheckInfluence;
import de.rpgframework.shadowrun6.modifications.ShadowrunCostType;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
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
import de.rpgframework.world.LocalWorldManager;

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
		alreadyInitialized = true;
//		DataSet core = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE", "core.i18n", Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.forLanguageTag("pt"));

		try {
			initCore();
			initFiringSquad();
			initStreetWyrd();
			initPowerPlays();
			initDoubleClutch();
			initHackNSlash();
			initCompanion();
			initLofwyrsLegions();
			initOtherUS();
			initKrime();
			initCollapsing();
			initSlip();
			initKechibi();
			initNoFuture();
			initDPAlpen();
			initBerlin();
			initDPFeuerläufer();
			initOtherDE();
			initDPPiraten();
			initRevierbericht();
			initDPSOTA2081();
			initDPSOTA2082();
			initDPWestphalen();
			initAstralWays();
			initBodyShop();
			initEasyCome();
			initEmeraldCity();
			initSmoothOperations();
			initDPBundeswehr();
			initBestialNature();
			initTarnishedStar();
			initDPSOTA2083();
			initDealersOfDeath();
			initShadowCast();
			//initDeadlyArts();
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
					logger.log(Level.DEBUG, "Failed on ''placeholder/{0}''", file);
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
			logger.log(Level.DEBUG, "Check "+file);
			URL ins = clazz.getResource("placeholder/"+file);
			if (ins!=null) {
				return ins;
			}
		}
		return null;
	}

	//-------------------------------------------------------------------
	private void initWorld() throws IOException {
		GenericCore core = new GenericCore() {
		};
		LocalWorldManager mgr = new LocalWorldManager(null);
	}

	//-------------------------------------------------------------------
	private void initCore() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -------------------------------Core-----------------------------------------------");
		DataSet core = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE", "core.i18n", Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.forLanguageTag("pt"));
		core.setType(DataSetType.RULES);
		core.setReleased(201908);
		ItemUtil.SOFTWARE_LIBRARY_ITEM.assignToDataSet(core);
		ItemUtil.UNARMED_ITEM.assignToDataSet(core);

//		PluginSkeleton CORE = new PluginSkeleton("CORE", "Splittermond Core Rules");
		list = Shadowrun6Core.loadDataItems(SenseList.class, Sense.class, core, clazz.getResourceAsStream("core/data/senses.xml"));
		logger.log(Level.DEBUG, "Loaded {0} senses", list.size());
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
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, core, clazz, "core/data/qualities.xml");
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
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz,"core/data/gear_firearms_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon accessories");
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, core, clazz,"core/data/weapon_modifications.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz, "core/data/gear_melee.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" items");
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
//		logger.log(Level.ERROR, "Stop here");
//		System.exit(1);
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

		ItemUtil.UNARMED_ITEM.setAttribute(SR6ItemAttribute.SKILL, Shadowrun6Core.getSkill("close_combat"));
		ItemUtil.UNARMED_ITEM.setAttribute(SR6ItemAttribute.SKILL_SPECIALIZATION, Shadowrun6Core.getSkill("close_combat"));

		// Seattle Edition
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE_SEATTLE", "core.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		core.setReleased(202109);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "core/data/qualities_seattle.xml");
		logger.log(Level.DEBUG, "Loaded {0} qualities from Seattle City Edition", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"core/data/gear_seattle.xml");
		logger.log(Level.DEBUG, "Loaded {0} armor from Seattle City Edition", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"core/data/gear_drones_seattle.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones from Seattle City Edition");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"core/data/gear_electronics_seattle.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" electronics from Seattle City Edition");

		// Berlin Edition
		set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE_BERLIN", "core.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		core.setReleased(202311);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"core/data/qualities_berlin.xml");
		logger.log(Level.INFO, "Loaded {0} qualities from Berlin City Edition", list.size());
	}

	//-------------------------------------------------------------------
	private void initFiringSquad() throws IOException {
		logger.log(Level.INFO, "START -------------------------------FIRING_SQUAD---------------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "FIRING_SQUAD", "firing_squad.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.RULES);
		set.setReleased(202005);
		ItemUtil.FIRING_SQUAD_MELEE_HARDNING.assignToDataSet(set);
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "firing_squad/data/actions_edge.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" edge actions");
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"firing_squad/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunitions");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"firing_squad/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(QualityPathList.class, QualityPath.class, set, clazz,"firing_squad/data/quality_paths.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" quality paths");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"firing_squad/data/gear_underbarrel_weapons.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" underbarrel weapons");
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
		list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, set, clazz.getResourceAsStream("firing_squad/data/npcs.xml"));
		logger.log(Level.DEBUG, "Loaded {0} NPCs", list.size());
		//System.exit(1);
	}

	//-------------------------------------------------------------------
	private void initStreetWyrd() throws IOException {
		logger.log(Level.INFO, "START -------------------------------STREET_WYRD------------------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "STREET_WYRD", "street_wyrd.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.RULES);
		set.setReleased(202101);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "street_wyrd/data/gear_magical.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" alchemical artifacts");
		list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, set, clazz, "street_wyrd/data/adeptpowers.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "street_wyrd/data/qualities1.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "street_wyrd/data/qualities2.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities (Adept Ways)");
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "street_wyrd/data/metamagics.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" metamagics for adepts");
		list = Shadowrun6Core.loadDataItems(TraditionList.class, Tradition.class, set, clazz,"street_wyrd/data/traditions.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" magic traditions");
		list = Shadowrun6Core.loadDataItems(SpellFeatureList.class, SpellFeature.class, set, clazz,"street_wyrd/data/spellfeatures.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" spell features");
		list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, set, clazz,"street_wyrd/data/spells.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" spells");
		list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "street_wyrd/data/critterpower.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" spirit powers");
		list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, set, clazz,"street_wyrd/data/spirits.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" spirits");
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,"street_wyrd/data/mentorspirits.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" mentor spirits");
		list = Shadowrun6Core.loadDataItems(FocusList.class, Focus.class, set, clazz, "street_wyrd/data/foci.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" foci");
	}

	//-------------------------------------------------------------------
	private void initPowerPlays() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------Power Plays-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "POWER_PLAYS", "power_plays.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		set.setReleased(202104);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "power_plays/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded {0} qualities", list.size());
	}

	//-------------------------------------------------------------------
	private void initDoubleClutch() throws IOException {
		logger.log(Level.INFO, "START -------------------------------Double Clutch------------------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DOUBLE_CLUTCH", "double_clutch.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.RULES);
		set.setReleased(202110);
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "double_clutch/data/actions_edge.xml");
		logger.log(Level.DEBUG, "Loaded {0} actions", list.size());
		list = Shadowrun6Core.loadDataItems(ChassisTypeList.class, ChassisType.class, set, clazz, "double_clutch/data/chassisTypes.xml");
		logger.log(Level.DEBUG, "Loaded {0} chassis types", list.size());
		list = Shadowrun6Core.loadDataItems(PowertrainList.class, Powertrain.class, set, clazz, "double_clutch/data/powertrains.xml");
		logger.log(Level.DEBUG, "Loaded {0} power trains", list.size());
		list = Shadowrun6Core.loadDataItems(ConsoleTypeList.class, ConsoleType.class, set, clazz, "double_clutch/data/consoleTypes.xml");
		logger.log(Level.DEBUG, "Loaded {0} Console Types", list.size());
		list = Shadowrun6Core.loadDataItems(DesignOptionList.class, DesignOption.class, set, clazz, "double_clutch/data/designOptions.xml");
		logger.log(Level.DEBUG, "Loaded {0} design options", list.size());
		list = Shadowrun6Core.loadDataItems(DesignModList.class, DesignMod.class, set, clazz, "double_clutch/data/designMods.xml");
		logger.log(Level.DEBUG, "Loaded {0} design mods", list.size());
		list = Shadowrun6Core.loadDataItems(QualityFactorList.class, QualityFactor.class, set, clazz, "double_clutch/data/qualityFactors.xml");
		logger.log(Level.DEBUG, "Loaded {0} quality factors", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"double_clutch/data/gear_electronics.xml");
		logger.log(Level.DEBUG, "Loaded {0} electronics", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"double_clutch/data/gear_vehicle_accessories.xml");
		logger.log(Level.DEBUG, "Loaded {0} vehicles accessories", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"double_clutch/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded {0} vehicles", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"double_clutch/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded {0} drones", list.size());
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "double_clutch/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded {0} qualities", list.size());
	}

	//-------------------------------------------------------------------
	private void initHackNSlash() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -------------------------------HACK&SLASH------------------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "HACK_SLASH", "hack_slash.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.RULES);
		set.setReleased(202210);
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "hack_slash/data/actions.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" actions");
		list = Shadowrun6Core.loadDataItems(ComplexFormList.class, ComplexForm.class, set, clazz, "hack_slash/data/complexforms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" complex forms");
		list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "hack_slash/data/critterpower.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" technocritter qualities");
		list = Shadowrun6Core.loadDataItems(NPCList.class, SR6NPC.class, set, clazz, "hack_slash/data/sprites.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" sprites");
		list = Shadowrun6Core.loadDataItems(DataStructureList.class, DataStructure.class, set, clazz, "hack_slash/data/datastructures.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" data structures");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "hack_slash/data/gear_electronics.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" electronic items");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "hack_slash/data/gear_software.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" software items");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"hack_slash/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"hack_slash/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "hack_slash/data/echoes.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" echoes");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "hack_slash/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "hack_slash/data/qualities_ai.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vitual lifeform qualities");
		list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "hack_slash/data/metatypes.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" AI types");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"hack_slash/data/gear_codemods.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" codemods");
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,"hack_slash/data/mentorspirits.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" paragons");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "hack_slash/data/qualities_streams.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" streams");
		list = Shadowrun6Core.loadDataItems(QualityPathList.class, QualityPath.class, set, clazz,"hack_slash/data/quality_paths.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" quality paths");
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz,"hack_slash/data/neuromorphism.xml");
		logger.log(Level.DEBUG, "Loaded {0} echoes", list.size());

	}

	//-------------------------------------------------------------------
	private void initCompanion() throws IOException {
		logger.log(Level.INFO, "START -------------------------------COMPANION------------------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "COMPANION", "companion.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.RULES);
		set.setReleased(202205);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "companion/data/qualities-metagenetic.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" metagenic qualities");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "companion/data/qualities-infected.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" infected qualities");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "companion/data/qualities.xml");
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
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/packs-vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicle PACKs");
		list = Shadowrun6Core.loadDataItems(LifepathModuleList.class, LifepathModule.class, set, clazz,"companion/data/lifepath.xml");
		logger.log(Level.WARNING, "Loaded "+list.size()+" lifepath modules");
		list = Shadowrun6Core.loadDataItems(ContactTypeList.class, ContactType.class, set, clazz, "companion/data/contact_types.xml");
		logger.log(Level.DEBUG, "Loaded {0} contact types", list.size());
		list = Shadowrun6Core.loadDataItems(SetItemList.class, SetItem.class, set, clazz, "companion/data/collectives.xml");
		logger.log(Level.DEBUG, "Loaded {0} collectives", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"companion/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initLofwyrsLegions() throws IOException {
		logger.log(Level.INFO, "START -------------------------------Lofwyrs Legions----------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "LOFWYR", "lofwyr.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		set.setReleased(202201);
		list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "lofwyr/data/critterpower.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" critter powers");
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "lofwyr/data/dracogenesis_powers.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" dracogenesis powers");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "lofwyr/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(TechniqueList.class, Technique.class, set, clazz,"lofwyr/data/techniques.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art techniques");
		list = Shadowrun6Core.loadDataItems(MartialArtsList.class, MartialArts.class, set, clazz,"lofwyr/data/martialarts.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art styles");
		list = Shadowrun6Core.loadDataItems(DrakeTypeList.class, DrakeType.class, set, clazz, "lofwyr/data/draketypes.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drake types");
	}

	//-------------------------------------------------------------------
	private void initKrime() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------Krime Katalog-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "KRIME", "krime.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"krime/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"krime/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"krime/data/gear_firearms_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms accessories");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"krime/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"krime/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"krime/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initCollapsing() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------Collapsing Now-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "COLLAPSING_NOW", "collapsing_now.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, set, clazz, "collapsing_now/data/adeptpowers.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"collapsing_now/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
	}

	//-------------------------------------------------------------------
	private void initSlip() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------Slip Streams-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "SLIP_STREAMS", "slip_streams.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "slip_streams/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
	}

	//-------------------------------------------------------------------
	private void initKechibi() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------Kechibi Code-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "KECHIBI", "kechibi.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"kechibi/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"kechibi/data/gear_vision.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vision systems");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "kechibi/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
	}

	//-------------------------------------------------------------------
	private void initNoFuture() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------No Future-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "NO_FUTURE", "no_future.i18n", Locale.ENGLISH);
		set.setType(DataSetType.BACKGROUND);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_bioware.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" bioware");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_clothing.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" clothing");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_cyberware.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" cyberware");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_instruments.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" instruments");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_melee.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" melee");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_tools.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" tools");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"no_future/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(LifepathModuleList.class, LifepathModule.class, set, clazz,"no_future/data/lifepath.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" lifepath modules");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "no_future/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
	}

	//-------------------------------------------------------------------
	private void initOtherUS() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------ Other US -----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "OTHER_US", "other_us.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"other_us/data/gear_various.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" various gear");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"other_us/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,("other_us/data/mentorspirits.xml"));
		logger.log(Level.DEBUG, "Loaded "+list.size()+" mentor spirits");
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"other_us/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,("other_us/data/qualities.xml"));
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "other_us/data/critterpower.xml");
		logger.log(Level.DEBUG, "Loaded {0} critter powers", list.size());
		list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "other_us/data/metatypes.xml");
		logger.log(Level.DEBUG, "Loaded {0} metatypes", list.size());
	}

	//-------------------------------------------------------------------
	private void initDPAlpen() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE Datapuls Alpen-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_ALPEN", "de_alpen.i18n", Locale.GERMAN);
		set.setType(DataSetType.LOCATION);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_alpen/data/gear_armor.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_alpen/data/gear_survival.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" survival");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_alpen/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz.getResourceAsStream("de_alpen/data/mentorspirits.xml"));
		logger.log(Level.DEBUG, "Loaded "+list.size()+" Mentor Spirit");
	}

	//-------------------------------------------------------------------
	private void initBerlin() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -------------------------------DE Berlin 2080-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_BERLIN2080", "de_berlin2080.i18n", Locale.GERMAN);
		set.setType(DataSetType.LOCATION);
		set.setReleased(201910);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_berlin2080/data/gear_electronics.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" electronics");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_berlin2080/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_berlin2080/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initDPFeuerläufer() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ----------------------------DE Datapuls Feuerlaeufer---------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_FEUERLAEUFER", "de_feuerlaeufer.i18n", Locale.GERMAN);
		set.setType(DataSetType.OTHER);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_vehicle_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicle accessories");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_armor.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_explosives.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_melee.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" melee");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_survival.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" survival");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_feuerlaeufer/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initOtherDE() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -----------------------------DE Other Sources--------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_OTHER", "de_other.i18n", Locale.GERMAN);
		set.setType(DataSetType.OTHER);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "de_other/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_electronics.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" electronics");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_software.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" software");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_chemicals.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_other/data/gear_tools.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initDPPiraten() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE Piraten-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_PIRATEN", "de_piraten.i18n", Locale.GERMAN);
		set.setType(DataSetType.OTHER);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_piraten/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initDPSOTA2081() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE SOTA 2081-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_SOTA2081", "de_sota2081.i18n", Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		set.setReleased(202005);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2081/data/gear_firearms_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" accessories");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2081/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2081/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2081/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz.getResourceAsStream("de_sota2081/data/mentorspirits.xml"));
		logger.log(Level.DEBUG, "Loaded "+list.size()+" Mentor Spirit");
	}

	//-------------------------------------------------------------------
	private void initDPSOTA2082() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE SOTA 2082-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_SOTA2082", "de_sota2082.i18n", Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		set.setReleased(202207);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2082/data/gear_ammo.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammo");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2082/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2082/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2082/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz.getResourceAsStream("de_sota2082/data/mentorspirits.xml"));
		logger.log(Level.DEBUG, "Loaded "+list.size()+" Mentor Spirit");
	}

	//-------------------------------------------------------------------
	private void initDPWestphalen() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE Westphalen-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_WESTPHALEN", "de_westphalen.i18n", Locale.GERMAN);
		set.setType(DataSetType.LOCATION);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_westphalen/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
	}

	//-------------------------------------------------------------------
	private void initRevierbericht() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE Revierbericht-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_REVIERBERICHT", "de_revierbericht.i18n", Locale.GERMAN);
		set.setType(DataSetType.LOCATION);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_revierbericht/data/gear_armor.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_revierbericht/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_revierbericht/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initAstralWays() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -------------------------------Astral Ways------------------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "ASTRAL_WAYS", "astral_ways.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		set.setReleased(202302);
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "astral_ways/data/actions_edge.xml");
		logger.log(Level.DEBUG, "Loaded {0} actions", list.size());
		list = Shadowrun6Core.loadDataItems(CritterPowerList.class, CritterPower.class, set, clazz, "astral_ways/data/critterpower.xml");
		logger.log(Level.DEBUG, "Loaded {0} critter powers", list.size());
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"astral_ways/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"astral_ways/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded {0} vehicles", list.size());
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz, "astral_ways/data/metamagics.xml");
		logger.log(Level.DEBUG, "Loaded {0} metamagics", list.size());
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "astral_ways/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded {0} qualities", list.size());
		list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, set, clazz, "astral_ways/data/metatypes.xml");
		logger.log(Level.DEBUG, "Loaded {0} metatypes", list.size());
		list = Shadowrun6Core.loadDataItems(RitualList.class, Ritual.class, set, clazz, "astral_ways/data/rituals.xml");
		logger.log(Level.DEBUG, "Loaded {0} rituals", list.size());
		list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, set, clazz,"astral_ways/data/spells.xml");
		logger.log(Level.DEBUG, "Loaded {0} spells", list.size());
		list = Shadowrun6Core.loadDataItems(FocusList.class, Focus.class, set, clazz, "astral_ways/data/foci.xml");
		logger.log(Level.DEBUG, "Loaded {0} foci", list.size());
		list = Shadowrun6Core.loadDataItems(ContactTypeList.class, ContactType.class, set, clazz, "astral_ways/data/contact_types.xml");
		logger.log(Level.DEBUG, "Loaded {0} contact types", list.size());
	}

	//-------------------------------------------------------------------
	private void initBodyShop() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -------------------------------BODY SHOP-------------------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "BODY_SHOP", "body_shop.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.RULES);
		set.setReleased(202306);
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "body_shop/data/actions.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" actions");
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "body_shop/data/actions_edge.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" edge actions");
		list = Shadowrun6Core.loadDataItems(TechniqueList.class, Technique.class, set, clazz,"body_shop/data/techniques.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art techniques");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"body_shop/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_cosmetic.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" cosmetic cyberware");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_earware.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_eyeware.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_headware.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_bodyware.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_cyberlimbs.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberlimbs", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_cyberweapons.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberweapons", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_bioware.xml");
		logger.log(Level.DEBUG, "Loaded {0} bioware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_biosenses.xml");
		logger.log(Level.DEBUG, "Loaded {0} bioware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_bioweapons.xml");
		logger.log(Level.DEBUG, "Loaded {0} bioweapons", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_symbionts.xml");
		logger.log(Level.DEBUG, "Loaded {0} symbionts", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_nanoware.xml");
		logger.log(Level.DEBUG, "Loaded {0} nanoware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_geneware.xml");
		logger.log(Level.DEBUG, "Loaded {0} geneware", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "body_shop/data/gear_drugs.xml");
		logger.log(Level.DEBUG, "Loaded {0} drugs", list.size());
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz, "body_shop/data/cyberware_enhancements.xml");
		logger.log(Level.DEBUG, "Loaded {0} cyberware enhancements", list.size());
	}

	//-------------------------------------------------------------------
	private void initEasyCome() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -----------------------------Easy Come, Easy Go--------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "easycome", "sif_new_orleans.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.LOCATION);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"sif_new_orleans/data/gear_easycome.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" gear from 'Easy Come'");
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz,"sif_new_orleans/data/modifications_easycome.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications from 'Easy Come'");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"sif_new_orleans/data/qualities_easycome.xml");
		logger.log(Level.DEBUG, "Loaded {0} qualities", list.size());
	}

	//-------------------------------------------------------------------
	private void initEmeraldCity() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -----------------------------Emerald_City------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "emerald", "emerald.i18n", Locale.ENGLISH);
		set.setType(DataSetType.LOCATION);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"emerald/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities from 'Emerald City'");

//		System.exit(1);
	}

	//-------------------------------------------------------------------
	private void initSmoothOperations() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START -----------------------------Smooth_Operations------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "smooth_operations", "smooth_operations.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"smooth_operations/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded {0} qualities from 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, set, clazz,"smooth_operations/data/spells.xml");
		logger.log(Level.DEBUG, "Loaded {0} spells from 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(RitualList.class, Ritual.class, set, clazz,"smooth_operations/data/rituals.xml");
		logger.log(Level.DEBUG, "Loaded {0} rituals from 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, set, clazz,"smooth_operations/data/adeptpowers.xml");
		logger.log(Level.DEBUG, "Loaded {0} adept powers from 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz,"smooth_operations/data/metamagics.xml");
		logger.log(Level.DEBUG, "Loaded {0} metamagic from 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(MentorSpiritList.class, MentorSpirit.class, set, clazz,"smooth_operations/data/mentorspirits.xml");
		logger.log(Level.DEBUG, "Loaded {0} mentor spirits from 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "smooth_operations/data/gear_magical.xml");
		logger.log(Level.DEBUG, "Loaded {0} alchemical preparations 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "smooth_operations/data/gear_clothing.xml");
		logger.log(Level.DEBUG, "Loaded {0} gear items 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "smooth_operations/data/gear_software.xml");
		logger.log(Level.DEBUG, "Loaded {0} gear items 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz, "smooth_operations/data/gear_espionage.xml");
		logger.log(Level.DEBUG, "Loaded {0} gear items 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "smooth_operations/data/actions_edge.xml");
		logger.log(Level.DEBUG, "Loaded {0} edge actions 'Smooth Operations'", list.size());
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz,"smooth_operations/data/weapon_modifications.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");

//		System.exit(1);
	}

	//-------------------------------------------------------------------
	private void initDPBundeswehr() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ----------------------------DE Datapuls Bundeswehr---------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DE_BUNDESWEHR", "de_bundeswehr.i18n", Locale.GERMAN);
		set.setType(DataSetType.OTHER);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_bundeswehr/data/gear_weapons.xml");
		logger.log(Level.DEBUG, "Loaded {0} weapons", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_bundeswehr/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded {0} vehicles", list.size());
	}

	//-------------------------------------------------------------------
	private void initBestialNature() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ----------------------------Bestial Nature---------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "bestial_nature", "bestial_nature.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		set.setReleased(202306);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"bestial_nature/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded {0} shifter qualities", list.size());
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz,"bestial_nature/data/metamagics.xml");
		logger.log(Level.DEBUG, "Loaded {0} animalisms", list.size());
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"bestial_nature/data/gear_items.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" items");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"bestial_nature/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"bestial_nature/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
	}

	//-------------------------------------------------------------------
	private void initTarnishedStar() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ----------------------------Tarnished Star---------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "tarnished_star", "tarnished_star.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.BACKGROUND);
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"tarnished_star/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"tarnished_star/data/gear_weapons.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapons");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"tarnished_star/data/gear_armor.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"tarnished_star/data/gear_tools.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" tools");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"tarnished_star/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"tarnished_star/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
	}

	//-------------------------------------------------------------------
	private void initDPSOTA2083() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------DE SOTA 2083-----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "de_sota2083", "de_sota2083.i18n", Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2083/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz,"de_sota2083/data/weapon_modifications.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2083/data/gear_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" accessories");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"de_sota2083/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
	}

	//-------------------------------------------------------------------
	private void initDealersOfDeath() throws IOException {
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		logger.log(Level.INFO, "START ------------------------------Dealers of Death -----------------------------------");
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "dealers_of_death", "dealers_of_death.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "dealers_of_death/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(QualityPathList.class, QualityPath.class, set, clazz,"dealers_of_death/data/quality_paths.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" quality paths");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"dealers_of_death/data/gear_melee.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" melee weapons");
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz,"dealers_of_death/data/weapon_modifications.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"dealers_of_death/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"dealers_of_death/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition");
		list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, set, clazz, "dealers_of_death/data/adeptpowers.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
		list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, set, clazz,"dealers_of_death/data/spells.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" spells");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"dealers_of_death/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"dealers_of_death/data/gear_toxins.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" toxins");
	}

	//-------------------------------------------------------------------
	private void initShadowCast() throws IOException {
		logger.log(Level.INFO, "START -------------------------------Shadow Cast------------------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "SHADOW_CAST", "shadow_cast.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		set.setReleased(202210);
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz, "shadow_cast/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(QualityPathList.class, QualityPath.class, set, clazz,"shadow_cast/data/quality_paths.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" quality paths");
	}

	//-------------------------------------------------------------------
	private void initDeadlyArts() throws IOException {
		logger.log(Level.INFO, "START -------------------------------Deadly Arts---------------------------------------");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		DataSet set = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "DEADLY_ARTS", "deadly_arts.i18n", Locale.ENGLISH, Locale.GERMAN);
		set.setType(DataSetType.OPT_RULES);
		set.setReleased(202504);
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_melee.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" melee weapons");
		list = Shadowrun6Core.loadDataItems(ItemEnhancementList.class, SR6ItemEnhancement.class, set, clazz,"deadly_arts/data/weapon_modifications.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" weapon modifications");
		list = Shadowrun6Core.loadDataItems(ActionList.class, Shadowrun6Action.class, set, clazz, "deadly_arts/data/actions_edge.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" edge actions");
		list = Shadowrun6Core.loadDataItems(TechniqueList.class, Technique.class, set, clazz,"deadly_arts/data/techniques.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art techniques");
		list = Shadowrun6Core.loadDataItems(MartialArtsList.class, MartialArts.class, set, clazz,"deadly_arts/data/martialarts.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" martial art styles");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_firearms_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms accessories");
		list = Shadowrun6Core.loadDataItems(AmmunitionTypeList.class, AmmunitionType.class, set, clazz,"deadly_arts/data/ammunition_types.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunition types");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_ammunition.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" ammunitions");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_firearms.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" firearms");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_electronics.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" electronic items");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_vehicle_mods.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicle mods");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_drones.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" drones");
		list = Shadowrun6Core.loadDataItems(QualityList.class, SR6Quality.class, set, clazz,"deadly_arts/data/qualities.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" qualities");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_vehicles.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" vehicles");
		list = Shadowrun6Core.loadDataItems(DesignModList.class, DesignMod.class, set, clazz, "deadly_arts/data/designMods.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" design mods");
		list = Shadowrun6Core.loadDataItems(FocusList.class, Focus.class, set, clazz, "deadly_arts/data/foci.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" foci");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_magical.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" magic gear");
		list = Shadowrun6Core.loadDataItems(SR6SpellList.class, SR6Spell.class, set, clazz, "deadly_arts/data/spells.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" spells");
		list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, set, clazz, "deadly_arts/data/adeptpowers.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" adept powers");
		list = Shadowrun6Core.loadDataItems(MetamagicOrEchoList.class, MetamagicOrEcho.class, set, clazz,"deadly_arts/data/metamagics.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" metamagics");
		list = Shadowrun6Core.loadDataItems(RitualList.class, Ritual.class, set, clazz, "deadly_arts/data/rituals.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" rituals");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_armor_accessories.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" armor accessories");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_armor.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" armor");
		list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, set, clazz,"deadly_arts/data/gear_biotech.xml");
		logger.log(Level.DEBUG, "Loaded "+list.size()+" biotech");
	}

}
