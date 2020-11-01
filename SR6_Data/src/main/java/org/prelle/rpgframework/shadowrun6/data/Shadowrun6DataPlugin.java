package org.prelle.rpgframework.shadowrun6.data;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.shadowrun6.ShadowrunCore;
import org.prelle.shadowrun6.Skill;
import org.prelle.shadowrun6.SkillList;
import org.prelle.shadowrun6.Spell;
import org.prelle.shadowrun6.SpellFeature;
import org.prelle.shadowrun6.SpellFeatureList;
import org.prelle.shadowrun6.SpellList;
import org.prelle.shadowrun6.modifications.ShadowrunCheckInfluence;
import org.prelle.shadowrun6.modifications.ShadowrunChoiceType;
import org.prelle.shadowrun6.modifications.ShadowrunCostType;
import org.prelle.shadowrun6.modifications.ShadowrunReference;
import org.prelle.simplepersist.Persister;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.data.CheckInfluence;
import de.rpgframework.genericrpg.data.ChoiceType;
import de.rpgframework.genericrpg.data.CostType;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityList;

/**
 * @author Stefan
 *
 */
public class Shadowrun6DataPlugin  {

	private static Logger logger = LogManager.getLogger("shadowrun6.data");

	private static boolean alreadyInitialized = false;

	//--------------------------------------------------------------------
	public Shadowrun6DataPlugin() {
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+ModifiedObjectType.class.getName(), ShadowrunReference.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+ChoiceType.class.getName(), ShadowrunChoiceType.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+CostType.class.getName(), ShadowrunCostType.class);
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+CheckInfluence.class.getName(), ShadowrunCheckInfluence.class);
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
		DataSet core = new DataSet(RoleplayingSystem.SHADOWRUN6, "CORE", "org.prelle.rpgframework.shadowrun6.data", Locale.GERMAN, Locale.ENGLISH);
//		PluginSkeleton CORE = new PluginSkeleton("CORE", "Splittermond Core Rules");
		Class<Shadowrun6DataPlugin> clazz = Shadowrun6DataPlugin.class;
		List<? extends DataItem> list = null;
		try {
			list = ShadowrunCore.loadDataItems(SkillList.class, Skill.class, core, clazz.getResourceAsStream("core/data/skills.xml"));
			logger.debug("Loaded "+list.size()+" skills");
			list = ShadowrunCore.loadDataItems(SpellFeatureList.class, SpellFeature.class, core, clazz.getResourceAsStream("core/data/spellfeatures.xml"));
			logger.debug("Loaded "+list.size()+" spell features");
			list = ShadowrunCore.loadDataItems(SpellList.class, Spell.class, core, clazz.getResourceAsStream("core/data/spells.xml"));
			logger.debug("Loaded "+list.size()+" spells");
			list = ShadowrunCore.loadDataItems(QualityList.class, Quality.class, core, clazz.getResourceAsStream("core/data/qualities.xml"));
			logger.debug("Loaded "+list.size()+" qualities");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
