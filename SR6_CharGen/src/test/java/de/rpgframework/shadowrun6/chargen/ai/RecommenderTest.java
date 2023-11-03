package de.rpgframework.shadowrun6.chargen.ai;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.chargen.ai.AITool;
import de.rpgframework.genericrpg.chargen.ai.LevellingProfile;
import de.rpgframework.genericrpg.chargen.ai.Recommender;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Core;
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
		SR6AITool.initialize();
	}

	//-------------------------------------------------------------------
	@Test
	public void test1() {
		Recommender recommender = new Recommender();
		recommender.addConfiguration( Shadowrun6Core.getItem(LevellingProfile.class,"pick_every_lock").getOutgoingModifications() );
		recommender.addConfiguration( Shadowrun6Core.getItem(LevellingProfile.class,"gun_master").getOutgoingModifications() );

		assertEquals(RecommendationState.RECOMMENDED, recommender.getRecommendationState(Shadowrun6Core.getSkill("engineering")));
		assertEquals(RecommendationState.RECOMMENDED, recommender.getRecommendationState(ShadowrunAttribute.LOGIC));
	}

}
