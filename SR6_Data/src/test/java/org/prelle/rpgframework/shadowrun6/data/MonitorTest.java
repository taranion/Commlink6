package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class MonitorTest {

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
//		System.setProperty("logdir", "C:\\Users\\stefa");
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
	}

	//-------------------------------------------------------------------
	@Before
	public void setup() {
		model = new Shadowrun6Character();
		model.getAttribute(ShadowrunAttribute.BODY).setDistributed(5);
//		model.setAttribute(new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.BODY, 5));

		Shadowrun6Tools.runProcessors(model);
	}

	//-------------------------------------------------------------------
	@Test
	public void testIdle() {
		assertEquals(5, model.getAttribute(ShadowrunAttribute.BODY).getModifiedValue());
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		assertNotNull(val);
		assertEquals(11, val.getModifiedValue());
		System.out.println(val.getPool().toExplainString());
	}

	//-------------------------------------------------------------------
	@Test
	public void testQualitiesByMeta() {
		// Being a troll grants "Built tough 2"
		model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "troll"));
		Shadowrun6Tools.runProcessors(model);

		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		assertNotNull(val);
		System.out.println(val.getPool().toExplainString());
		assertEquals(13, val.getModifiedValue());

		int[] expect = new int[] {0,0,-1,-1,-1,-2,-2,-2,-3,-3,-3,-4,-5};
		int[] calc   = Shadowrun6Tools.getMonitorArray(model, ShadowrunAttribute.BODY);
		assertEquals("Last element in row rule not applied",-5, calc[12]);
		assertArrayEquals(expect, calc);
	}

	//-------------------------------------------------------------------
	@Test
	public void testQualitiesByMetaWithAddedLevel() {
		// Being a troll grants "Built tough 2"
		model.setMetatype(Shadowrun6Core.getItem(SR6MetaType.class, "troll"));
		Shadowrun6Tools.runProcessors(model);
		QualityValue qVal = model.getQuality("built_tough");
		qVal.setDistributed(2);

		Shadowrun6Tools.runProcessors(model);

		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		assertNotNull(val);
		System.out.println(val.getPool().toExplainString());
		assertEquals(15, val.getModifiedValue());
	}

}
