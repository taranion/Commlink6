package de.rpgframework.shadowrun6.data;

import java.util.List;
import java.util.Locale;

import org.prelle.simplepersist.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.data.CheckInfluence;
import de.rpgframework.genericrpg.data.CostType;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.items.IEquipMode;
import de.rpgframework.genericrpg.items.IItemAttribute;
import de.rpgframework.genericrpg.items.IUsageMode;
import de.rpgframework.genericrpg.items.PieceOfGearEquip;
import de.rpgframework.genericrpg.items.PieceOfGearUsage;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerList;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityList;
import de.rpgframework.shadowrun.Spell;
import de.rpgframework.shadowrun.SpellFeature;
import de.rpgframework.shadowrun.SpellFeatureList;
import de.rpgframework.shadowrun.SpellList;
import de.rpgframework.shadowrun6.MetaTypeList;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.SkillList;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;
import de.rpgframework.shadowrun6.items.SR6EquipMode;
import de.rpgframework.shadowrun6.items.SR6GearEquip;
import de.rpgframework.shadowrun6.items.SR6GearUsage;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6UsageMode;
import de.rpgframework.shadowrun6.modifications.ShadowrunCheckInfluence;
import de.rpgframework.shadowrun6.modifications.ShadowrunCostType;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
public class Shadowrun6DataPlugin  {

	private static Logger logger = LoggerFactory.getLogger("shadowrun6.data");

	private static boolean alreadyInitialized = false;

	//--------------------------------------------------------------------
	public Shadowrun6DataPlugin() {
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+ModifiedObjectType.class.getName(), ShadowrunReference.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+CostType.class.getName(), ShadowrunCostType.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+CheckInfluence.class.getName(), ShadowrunCheckInfluence.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IItemAttribute.class.getName(), SR6ItemAttribute.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+PieceOfGearEquip.class.getName(), SR6GearEquip.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IEquipMode.class.getName(), SR6EquipMode.class);
		Persister.putContext(Persister.PREFIX_KEY_ABSTRACT+"."+PieceOfGearUsage.class.getName(), SR6GearUsage.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+IUsageMode.class.getName(), SR6UsageMode.class);
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
		logger.info("START -------------------------------Core-----------------------------------------------");
		DataSet core = new DataSet(this, RoleplayingSystem.SHADOWRUN6, "CORE", "core.i18n", Locale.ENGLISH, Locale.GERMAN);
//		PluginSkeleton CORE = new PluginSkeleton("CORE", "Splittermond Core Rules");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		try {
			list = Shadowrun6Core.loadDataItems(SkillList.class, SR6Skill.class, core, clazz.getResourceAsStream("core/data/skills.xml"));
			logger.debug("Loaded "+list.size()+" skills");
			list = Shadowrun6Core.loadDataItems(SpellFeatureList.class, SpellFeature.class, core, clazz.getResourceAsStream("core/data/spellfeatures.xml"));
			logger.debug("Loaded "+list.size()+" spell features");
			list = Shadowrun6Core.loadDataItems(SpellList.class, Spell.class, core, clazz.getResourceAsStream("core/data/spells.xml"));
			logger.debug("Loaded "+list.size()+" spells");
			list = Shadowrun6Core.loadDataItems(AdeptPowerList.class, AdeptPower.class, core, clazz.getResourceAsStream("core/data/adeptpowers.xml"));
			logger.debug("Loaded "+list.size()+" adept powers");
			list = Shadowrun6Core.loadDataItems(QualityList.class, Quality.class, core, clazz.getResourceAsStream("core/data/qualities.xml"));
			logger.debug("Loaded "+list.size()+" qualities");
			list = Shadowrun6Core.loadDataItems(MetaTypeList.class, SR6MetaType.class, core, clazz.getResourceAsStream("core/data/metatypes.xml"));
			logger.debug("Loaded "+list.size()+" metatypes");
			list = Shadowrun6Core.loadDataItems(ItemTemplateList.class, ItemTemplate.class, core, clazz.getResourceAsStream("core/data/gear.xml"));
			logger.debug("Loaded "+list.size()+" items");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
