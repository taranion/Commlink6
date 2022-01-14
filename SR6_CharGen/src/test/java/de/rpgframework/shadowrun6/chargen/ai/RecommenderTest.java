package de.rpgframework.shadowrun6.chargen.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.chargen.ai.AITool;
import de.rpgframework.genericrpg.chargen.ai.Recommender;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.PointBuySR6AttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PointBuySR6SkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuySettings;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class RecommenderTest {

	//-------------------------------------------------------------------
	@BeforeClass
	public static void setupClass() {
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();
	}

	//-------------------------------------------------------------------
	@Before
	public void setup() {
		MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(RecommenderTest.class.getPackageName()+".Recommender", Locale.ENGLISH);
		AITool.loadProfiles(RecommenderTest.class.getResourceAsStream("profiles.xml"), RES);
	}
	
	//-------------------------------------------------------------------
	@Test
	public void test1() {
		Recommender recommender = new Recommender();
		recommender.addConfiguration( AITool.getProfile("pick_every_lock").getModifications() );
		recommender.addConfiguration( AITool.getProfile("gun_master").getModifications() );
		
		assertEquals(RecommendationState.RECOMMENDED, recommender.getRecommendationState(Shadowrun6Core.getSkill("engineering")));
		assertEquals(RecommendationState.RECOMMENDED, recommender.getRecommendationState(ShadowrunAttribute.LOGIC));
	}
	
}
