/**
 * 
 */
package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;
import org.prelle.shadowrun6.ShadowrunCore;
import org.prelle.shadowrun6.Skill;

/**
 * @author prelle
 *
 */
public class LoadDataTest {

	//-------------------------------------------------------------------
	@Test
	public void loadDataTest() {
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		
		Skill athlet = ShadowrunCore.getSkill("athletics");
		assertNotNull(athlet);
		assertEquals("Athletik", athlet.getName(Locale.GERMAN));
		assertEquals("Athletics", athlet.getName(Locale.ENGLISH));
	}

}
