package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.SerializationException;

import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

public class NPCParserTest {

	private static Persister persist;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		System.setProperty("logdir","/tmp");
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );

		persist = new Persister();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSonsOfSauron() {
		String rawData = "Sons of Sauron Brute\n"
//				+ "(Ork Adjustments Applied)\n"
				+ "B A R S W L I C ESS\n"
				+ "3 2 2 4 2 1 2 1 6\n"
				+ "DR I/ID AC CM MOVE\n"
				+ "4 4/1 A1, I2 10 10/15/+1\n"
				+ "Skills: Athletics 2, Close Combat 4, Firearms 3, Influence 5 (Intimidation\n"
				+ "+2), Perception 3\n"
				+ "Gear: Armor clothing (+2), commlink (Device Rating 2)\n"
				+ "Weapons:\n"
				+ "Beretta 101T [Light Pistol, DV 2P, SA, Attack Ratings 9/8/6/—/—]\n"
				+ "Knucks [Unarmed, DV 3P, Attack Ratings 6/—/—/—/—]";
		SR6NPC npc = NPCParser.parse(rawData);
		StringWriter out = new StringWriter();
		try {
			persist.write(npc, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(out.toString());
	}

	@Test
	public void testParse() {
		String rawData = "Lone Star Patrolman\n"
				+ "B A R S W L I C ESS\n"
				+ "3 3 3 3 3 2 3 2 6\n"
				+ "DR I/ID AC CM MOVE\n"
				+ "6 6/1 A1, I2 10 10/15/+1\n"
				+ "Skills: Athletics 1, Biotech 1, Close Combat 4, Con 1, Electronics 1, Firearms\n"
				+ "4, Influence 2, Perception 4, Piloting 2\n"
				+ "Gear: Armor vest (+3), commlink (Device Rating 3), 2 x jazz inhalers (*+1\n"
				+ "Reaction, +2I, +2 ID)\n"
				+ "Weapons:\n"
				+ "Colt America L36 [Light Pistol, DV 2P, SA, Attack Ratings 8/8/6/—/—]\n"
				+ "Stun baton [Club, DV 5Se, Attack Ratings 6/—/—/—/— ";
		SR6NPC npc = NPCParser.parse(rawData);
		StringWriter out = new StringWriter();
		try {
			persist.write(npc, out);
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(out.toString());
	}

}
