package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public class NPCParser {

	private final static String VAR_EXPECTED_ATTRIBUTES = "ATTRIBUTES";
	private final static String VAR_SKILL_LINE = "SKILL";
	private final static String VAR_GEAR_LINE = "GEAR";

	/** What is expected next */
	private enum State {
		NAME,
		ATTRIBUTE_NAMES,
		ATTRIBUTE_VALUES,
		DERIVED_ATTRIBUTE_NAMES,
		DERIVED_ATTRIBUTE_VALUES,
		SKILLS,
		GEAR,
		WEAPONS
	}

	static class ParsingState {
		public State state = State.NAME;
		public Map<String, Object> memory = new LinkedHashMap<>();
	}

	//-------------------------------------------------------------------
	public static SR6NPC parse(String rawData) {
		StringBuffer buf = new StringBuffer(rawData);
		BufferedReader read= new BufferedReader(new StringReader(rawData));

		SR6NPC ret = new SR6NPC();
		ParsingState state = new ParsingState();
		try {
			while (true) {
				String line = read.readLine();
				if (line==null)
					break;
				line = line.trim();
				if (line.isBlank()) continue;
				parseLine(ret, state, line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	//-------------------------------------------------------------------
	private static void parseLine(SR6NPC npc, ParsingState memory, String line) {
		if (line.isBlank()) return;
		switch (memory.state) {
		case NAME:
			npc.setId(convertNameToID(line));
			memory.state = State.ATTRIBUTE_NAMES;
			return;
		case ATTRIBUTE_NAMES:
			if (line.startsWith("B A R S W")) {
				// English
				parseAttributeNamesEN(npc, memory, line);
			}
			memory.state = State.ATTRIBUTE_VALUES;
			return;
		case ATTRIBUTE_VALUES:
			parseAttributeValues(npc, memory, line);
			memory.state = State.DERIVED_ATTRIBUTE_NAMES;
			return;
		case SKILLS:
			if (line.contains(":")) {
				parseHeading(npc, memory, line);
				return;
			}
			parseSkillLine(npc, memory, line);
			return;
		case GEAR:
			if (line.contains(":")) {
				parseHeading(npc, memory, line);
				return;
			}
			parseGearLine(npc, memory, line);
			return;
		default:
			if (line.contains(":")) {
				parseHeading(npc, memory, line);
				return;
			}
			System.err.println("NPCParser.parseLine: Don't know what to do in state "+memory.state+" with "+line);
		}
		return;
	}

	//-------------------------------------------------------------------
	private static void parseHeading(SR6NPC npc, ParsingState memory, String line) {
		State oldState = memory.state;
		updateState(memory, line.substring(0, line.indexOf(":")).toLowerCase().trim());
		State newState = memory.state;
		if (newState!=oldState) {
			parseLine(npc, memory, line.substring(line.indexOf(":")+1));
			return;
		} else {
			System.err.println("State did not change");
		}
	}

	//-------------------------------------------------------------------
	private static void updateState(ParsingState runner, String category) {
		switch (category) {
		case "skills": runner.state = State.SKILLS; return;
		case "gear": runner.state = State.GEAR; return;
		case "weapons": runner.state = State.WEAPONS; return;
		default:
			System.err.println("updateState: unknown '"+category+"'");
		}
	}

	//-------------------------------------------------------------------
	private static String convertNameToID(String name) {
		return name.toLowerCase().replace(' ', '_');
	}

	//-------------------------------------------------------------------
	private static void parseAttributeNamesEN(SR6NPC npc, ParsingState runner, String line) {
		List<ShadowrunAttribute> attrib = new ArrayList<>();
		StringTokenizer tok = new StringTokenizer(line);
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			switch (t) {
 			case "B": attrib.add(ShadowrunAttribute.BODY); break;
 			case "A": attrib.add(ShadowrunAttribute.AGILITY); break;
 			case "R": attrib.add(ShadowrunAttribute.REACTION); break;
 			case "S": attrib.add(ShadowrunAttribute.STRENGTH); break;
 			case "W": attrib.add(ShadowrunAttribute.WILLPOWER); break;
 			case "L": attrib.add(ShadowrunAttribute.LOGIC); break;
 			case "I": attrib.add(ShadowrunAttribute.INTUITION); break;
 			case "C": attrib.add(ShadowrunAttribute.CHARISMA); break;
 			case "M": attrib.add(ShadowrunAttribute.MAGIC); break;
 			case "ESS": attrib.add(ShadowrunAttribute.ESSENCE); break;
			}
		}
		runner.memory.put(VAR_EXPECTED_ATTRIBUTES, attrib);
	}

	//-------------------------------------------------------------------
	private static void parseAttributeValues(SR6NPC npc, ParsingState runner, String line) {
		Iterator<ShadowrunAttribute> it = ((List<ShadowrunAttribute>)runner.memory.get(VAR_EXPECTED_ATTRIBUTES)).iterator();
		StringTokenizer tok = new StringTokenizer(line);
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			ShadowrunAttribute attr = it.next();

			AttributeValue<ShadowrunAttribute> aVal = npc.getAttribute(attr);
			if (aVal==null) {
				aVal = new AttributeValue<ShadowrunAttribute>(attr);
				npc.addAttribute(aVal);
			}
			aVal.setDistributed( Integer.parseInt(t));
		}
	}

	//-------------------------------------------------------------------
	private static void parseSkillLine(SR6NPC npc, ParsingState runner, String line) {
		String fullLine = (runner.memory.containsKey(VAR_SKILL_LINE))?(runner.memory.get(VAR_SKILL_LINE)+" "+line):line;

		String[] splitted = fullLine.split(",");
		System.out.println("PCParser.parseSkillLine: Array: "+Arrays.toString(splitted));
		List<String> unsuccessful = new ArrayList<>();
		for (String perSkill : splitted) {
			System.out.println("PCParser.parseSkillLine: "+perSkill);
			boolean successful = parseSkill(npc, runner, perSkill);
			if (!successful) {
				unsuccessful.add(perSkill);
			}
		}
		if (unsuccessful.isEmpty()) {
			runner.memory.remove(VAR_SKILL_LINE);
		} else {
			System.out.println("NPCParser.parseSkillLine: Unsuccessful on "+unsuccessful);
			runner.memory.put(VAR_SKILL_LINE, String.join(",", unsuccessful));
		}
	}

	//-------------------------------------------------------------------
	private static SR6Skill findSkillNamed(String name, Locale loc) {
		for (SR6Skill skill : Shadowrun6Core.getItemList(SR6Skill.class)) {
			if (skill.getName(loc).equalsIgnoreCase(name))
				return skill;
		}
		System.err.println("NPCParser.findSkillNamed("+name+", "+loc+") failed");
		return null;
	}

	//-------------------------------------------------------------------
	private static SkillSpecialization<SR6Skill> findSkillSpecNamed(SR6Skill skill, String name, Locale loc) {
		for (SkillSpecialization<?> spec : skill.getSpecializations()) {
			if (spec.getName(loc).equalsIgnoreCase(name))
				return (SkillSpecialization<SR6Skill>) spec;
		}
		System.err.println("NPCParser.findSkillSpecNamed("+skill+","+name+", "+loc+") failed");
		return null;
	}

	//-------------------------------------------------------------------
	private static boolean parseSkill(SR6NPC npc, ParsingState runner, String line) {
		if (line.contains("(") && !line.contains(")")) {
			// End of specialization missing
			return false;
		}

		StringTokenizer tok = new StringTokenizer(line);
		StringBuffer name = new StringBuffer();
		StringBuffer specName = new StringBuffer();
		int value = -1;
		int specVal = -1;
		boolean foundName = false;
		boolean foundValue = false;
		boolean foundSpecName = false;
		boolean foundSpecValue = false;
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			if (!foundName) {
				name.append(t);
				foundName = true;
			} else if (foundName && !foundValue) {
				// Either another part of skill name or an integer value
				try {
					value = Integer.parseInt(t);
					foundValue = true;
				} catch (NumberFormatException e) {
					name.append(" "+t);
				}
			} else if (foundName && foundValue) {
//				System.err.println("NPCParser.parseSkill: Ignored token: "+t);
				if (t.startsWith("(")) t=t.substring(1);
				if (t.endsWith(")")) t=t.substring(0, t.length()-1);
				if (!foundSpecName) {
					specName.append(t);
					foundSpecName = true;
				} else if (foundSpecName && !foundSpecValue) {
					// Either another part of skill name or an integer value
					try {
						specVal = Integer.parseInt(t);
						foundSpecValue = true;
					} catch (NumberFormatException e) {
						name.append(" "+t);
					}
				}			}
		}

		SR6SkillValue sVal = null;
		if (foundName && foundValue) {
			SR6Skill skill = findSkillNamed(name.toString(), Locale.ENGLISH);
			if (skill!=null) {
				sVal = new SR6SkillValue(skill, value);
				npc.addSkillValue(sVal);
			}
		}
		if (foundSpecName && foundSpecValue) {
			System.err.println("NPCParser.parseSkill: Find spec: "+specName);
			SkillSpecialization<SR6Skill> spec = findSkillSpecNamed(sVal.getResolved(), specName.toString(), Locale.ENGLISH);
			if (spec!=null) {
				sVal.getSpecializations().add(new SkillSpecializationValue<>(spec, specVal));
			}
		}

		return foundName && foundValue;
	}

	//-------------------------------------------------------------------
	private static void parseGearLine(SR6NPC npc, ParsingState runner, String line) {
		String fullLine = (runner.memory.containsKey(VAR_SKILL_LINE))?(runner.memory.get(VAR_SKILL_LINE)+" "+line):line;

		String[] splitted = fullLine.split(",");
		System.out.println("PCParser.parseGearLine: Array: "+Arrays.toString(splitted));
		List<String> unsuccessful = new ArrayList<>();
		for (String perSkill : splitted) {
			System.out.println("PCParser.parseGearLine: "+perSkill);
			boolean successful = parseSkill(npc, runner, perSkill);
			if (!successful) {
				unsuccessful.add(perSkill);
			}
		}
		if (unsuccessful.isEmpty()) {
			runner.memory.remove(VAR_SKILL_LINE);
		} else {
			System.out.println("NPCParser.parseSkillLine: Unsuccessful on "+unsuccessful);
			runner.memory.put(VAR_SKILL_LINE, String.join(",", unsuccessful));
		}
	}


	//-------------------------------------------------------------------
	private static ItemTemplate findGearNamed(String name, Locale loc) {
		for (ItemTemplate skill : Shadowrun6Core.getItemList(ItemTemplate.class)) {
			if (skill.getName(loc).equalsIgnoreCase(name))
				return skill;
		}
		System.err.println("NPCParser.findGearNamed("+name+", "+loc+") failed");
		return null;
	}

}
