/**
 * 
 */
package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

/**
 * @author prelle
 *
 */
public class LoadDataTest {

	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() {
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		
		SR6Skill athlet = Shadowrun6Core.getSkill("athletics");
		assertNotNull(athlet);
		assertEquals("Athletik", athlet.getName(Locale.GERMAN));
		assertEquals("Athletics", athlet.getName(Locale.ENGLISH));
	}

}
