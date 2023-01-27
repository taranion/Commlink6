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
import de.rpgframework.shadowrun6.chargen.jfx.NPCParser.Result;
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
		Result result = NPCParser.parse(rawData);
		SR6NPC npc = result.npc;
		StringWriter out = new StringWriter();
		try {
			persist.write(npc, out);
		} catch (Exception e) {e.printStackTrace();}
		System.out.println(out.toString()+"\n"+String.join("\n", result.errors)+"\n");
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
				+ "Stun baton [Club, DV 5Se, Attack Ratings 6/—/—/—/— ]";
		Result result = NPCParser.parse(rawData);
		SR6NPC npc = result.npc;
		StringWriter out = new StringWriter();
		try {
			persist.write(npc, out);
		} catch (Exception e) {e.printStackTrace();}
		System.out.println(out.toString()+"\n"+String.join("\n", result.errors)+"\n");
	}

	@Test
	public void testParseAdept() {
		String rawData = "Yakuza Blademaster (Adept)\n"
				+ "B A R S W L I C M ESS\n"
				+ "3 5(6) 4(5) 3 3 2 3 2 4 6\n"
				+ "DR I/ID AC CM MOVE\n"
				+ "6 8/2 A1, I3 14 10/15/+1\n"
				+ "Skills: Astral 2, Athletics 4, Close Combat 5(6) (Blades +2), Outdoors 2\n"
				+ "(Tracking +2), Perception 4, Stealth 5\n"
				+ "Powers: Astral Perception, Improved Close Combat 1, Improved Agility 1,\n"
				+ "Improved Reflexes 1\n"
				+ "Gear: Armor vest (+3), commlink (Device Rating 4)\n"
				+ "Weapons:\n"
				+ "Katana [Blade, DV 4P, Attack Ratings 10/—/—/—/—]";
		Result result = NPCParser.parse(rawData);
		SR6NPC npc = result.npc;
		StringWriter out = new StringWriter();
		try {
			persist.write(npc, out);
		} catch (Exception e) {e.printStackTrace();}
		System.out.println(out.toString()+"\n"+String.join("\n", result.errors)+"\n");
	}

	@Test
	public void testParseSwatOfficer() {
		String rawData = "Lone Star SWAT Officer\n"
				+ "B A R S W L I C ESS\n"
				+ "4 3(4) 4(5) 3(4) 4 2 4 3 3.0\n"
				+ "DR I/ID AC CM MOVE\n"
				+ "14(18) 9/2 A1, I3 15 10/15/+1\n"
				+ "Skills: Athletics 3, Biotech 2, Close Combat 4 (Clubs +2), Con 2, Electronics\n"
				+ "2, Firearms 6, Influence 3 (Intimidation +2), Outdoors 2 (Tracking +2), Per-\n"
				+ "ception 4, Piloting 2, Stealth 4\n"
				+ "Augmentations: Cyberears (Rating 2 w/sound link, damper, audio en-\n"
				+ "hancement, select sound filter 2), cybereyes (Rating 2, w/image link, cam-\n"
				+ "era, smartlink), dermal plating 3, muscle replacement 1, wired reflexes 1\n"
				+ "Gear: Commlink (Device Rating 4), full body armor w/ helmet (+7)\n"
				+ "Weapons:\n"
				+ "Colt M23 [Rifle, DV 5P, SA/BF/FA Attack Ratings 7/10/10/10/6, w/\n"
				+ "smartgun, explosive ammo]\n"
				+ "Colt Manhunter [Heavy Pistol, DV 3P, SA, Attack Ratings 10/8/6/—/—,\n"
				+ "w/ smartgun]\n"
				+ "Stun baton [Club, DV 5Se, Attack Ratings 6/—/—/—/—]\n"
				+ "Riot shield [Unique, DV 4Se, Attack Ratings 4/—/—/—/—]";
		Result result = NPCParser.parse(rawData);
		SR6NPC npc = result.npc;
		StringWriter out = new StringWriter();
		try {
			persist.write(npc, out);
		} catch (Exception e) {e.printStackTrace();}
		System.out.println(out.toString()+"\n"+String.join("\n", result.errors)+"\n");
	}
}
