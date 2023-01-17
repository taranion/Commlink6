package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.NPCType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class NPCParser {

	private final static Logger logger = System.getLogger(NPCParser.class.getPackageName());

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
		TYPED_LINE
	}

	static class ParsingState {
		public State state = State.NAME;
		public String lineType = null;
		public StringBuffer lineBuf = new StringBuffer();
		public Map<String, Object> memory = new LinkedHashMap<>();
	}

	//-------------------------------------------------------------------
	public static SR6NPC parse(String rawData) {
		StringBuffer buf = new StringBuffer(rawData);
		BufferedReader read= new BufferedReader(new StringReader(rawData));

		SR6NPC ret = new SR6NPC();
		ret.setType(NPCType.GRUNT);
		ret.getType();
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
		case DERIVED_ATTRIBUTE_NAMES:
//			parseAttributeValues(npc, memory, line);
			memory.state = State.DERIVED_ATTRIBUTE_VALUES;
			return;
		case DERIVED_ATTRIBUTE_VALUES:
			//parseAttributeValues(npc, memory, line);
			memory.state = State.TYPED_LINE;
			return;
//		case SKILLS:
//			if (line.contains(":")) {
//				parseHeading(npc, memory, line);
//				return;
//			}
//			parseSkillLine(npc, memory, line);
//			return;
//		case GEAR:
//			if (line.contains(":")) {
//				parseHeading(npc, memory, line);
//				return;
//			}
//			parseGearLine(npc, memory, line);
//			return;
		default:
			if (line.contains(":")) {
				if (memory.lineType!=null) {
					parseLineType(npc, memory);
				}
				memory.lineBuf = new StringBuffer();
				String[] pair = parseFirstLine(line);
				memory.lineType = pair[0];
				memory.lineBuf.append(pair[1]);
				return;
			} else {
				// Add to existing line
				memory.lineBuf.append(" "+line);
			}
		}
		return;
	}

	//-------------------------------------------------------------------
	private static String[] parseFirstLine(String line) {
		String type = line.substring(0, line.indexOf(":")).toLowerCase().trim();
		String cont = line.substring(line.indexOf(":")+1).trim();
		return new String[] {type,cont};
	}

	//-------------------------------------------------------------------
	private static String[] splitWithBrackets(String line) {
		List<String> ret = new ArrayList<>();
		int bracketLevel = 0;
		StringBuffer current = new StringBuffer();
		for (int i=0; i<line.length(); i++) {
			char c = line.charAt(i);
			if (c=='(' || c=='[') {
				bracketLevel++;
				current.append(c);
			} else if (c==')' || c==']') {
				bracketLevel--;
				if (bracketLevel<0) bracketLevel=0;
				current.append(c);
			} else if (c==',' && bracketLevel==0) {
				ret.add(current.toString().trim());
				current = new StringBuffer();
			} else {
				current.append(c);
			}
		}
		if (!current.toString().isBlank())
			ret.add(current.toString());

		return ret.toArray(new String[ret.size()]);
	}

	//-------------------------------------------------------------------
	private static void parseLineType(SR6NPC npc, ParsingState memory) {
		logger.log(Level.ERROR, "parseLineType for "+memory.lineBuf+" of type "+memory.lineType);

		//TODO: line my contain commas between "(" and ")"

		//String[] elements = memory.lineBuf.toString().split(",");
		String[] elements = splitWithBrackets(memory.lineBuf.toString());
		logger.log(Level.ERROR, Arrays.toString(elements));
		switch(memory.lineType) {
		case "skills": parseSkills(npc, memory, elements); break;
		case "gear"  : parseGearLine(npc, memory, elements); break;
		case "powers":
			logger.log(Level.WARNING, "Don't know how to parse: '"+memory.lineType+"'");
			break;
		default:
			logger.log(Level.WARNING, "Don't know how to parse: '"+memory.lineType+"'");
			System.exit(1);
		}
	}

	//-------------------------------------------------------------------
	private static void updateState(ParsingState runner, String category) {
//		switch (category) {
//		case "skills": runner.state = State.SKILLS; return;
//		case "gear": runner.state = State.GEAR; return;
//		case "weapons": runner.state = State.WEAPONS; return;
//		default:
			System.err.println("updateState: unknown '"+category+"'");
//		}
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
			int p1 = t.indexOf("(");
			int p2 = t.indexOf(")");
			if (p1>0) {
				aVal.setDistributed( Integer.parseInt(t.substring(0, p1)));
				aVal.addModification(new ValueModification(
						ShadowrunReference.ATTRIBUTE,
						attr.name(),
						Integer.parseInt(t.substring( p1+1, p2))
						));
			} else {
				aVal.setDistributed( Integer.parseInt(t));
			}
		}
	}

	//-------------------------------------------------------------------
	private static void parseSkills(SR6NPC npc, ParsingState runner, String[] splitted) {
		logger.log(Level.DEBUG, "Array: "+Arrays.toString(splitted));
		List<String> unsuccessful = new ArrayList<>();
		for (String perSkill : splitted) {
			logger.log(Level.DEBUG, "parseSkillLine: "+perSkill);
			boolean successful = parseSkill(npc, runner, perSkill);
			if (!successful) {
				unsuccessful.add(perSkill);
			}
		}
		if (unsuccessful.isEmpty()) {
			runner.memory.remove(VAR_SKILL_LINE);
		} else {
			logger.log(Level.WARNING, "parseSkillLine: Unsuccessful on "+unsuccessful);
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
			logger.log(Level.INFO,"Find spec: "+specName);
			SkillSpecialization<SR6Skill> spec = findSkillSpecNamed(sVal.getResolved(), specName.toString(), Locale.ENGLISH);
			if (spec!=null) {
				sVal.getSpecializations().add(new SkillSpecializationValue<>(spec, specVal));
			}
		}

		return foundName && foundValue;
	}

	//-------------------------------------------------------------------
	private static void parseGearLine(SR6NPC npc, ParsingState runner, String[] splitted) {
		List<String> unsuccessful = new ArrayList<>();
		for (String perGear : splitted) {
			String key = perGear;
//			if (perGear.indexOf("(")>0) {
//				key = perGear.substring(0, perGear.indexOf("("));
//			}
			logger.log(Level.WARNING, "Parse gear: ''{0}''", key);
			boolean successful = parseGear(npc, runner, perGear);
			if (!successful) {
				unsuccessful.add(perGear);
			}
		}
		if (unsuccessful.isEmpty()) {
			runner.memory.remove(VAR_GEAR_LINE);
		} else {
			logger.log(Level.WARNING, "Unsuccessful in gear: ''{0}''", unsuccessful);
			runner.memory.put(VAR_GEAR_LINE, String.join(",", unsuccessful));
		}
	}


	//-------------------------------------------------------------------
	private static ItemTemplate findGearNamed(String name, Locale loc, String extra) {
		logger.log(Level.DEBUG, "Search gear "+name);

		if ("commlink".equals(name) && extra.contains("Device Rating")) {
			int pos = extra.indexOf("Device Rating") + "Device Rating".length();
			int rating = Integer.parseInt(extra.substring(pos).trim());
			for (ItemTemplate raw : Shadowrun6Core.getItemList(ItemTemplate.class)) {
				if (raw.getItemSubtype()==ItemSubType.COMMLINK && raw.getAttribute(SR6ItemAttribute.DEVICE_RATING).getDistributed()==rating) {
					logger.log(Level.INFO, "Substituted ''{0}'' with "+raw);
					return raw;
				}
			}
		}


		for (ItemTemplate raw : Shadowrun6Core.getItemList(ItemTemplate.class)) {
			if (raw.getName(loc).equalsIgnoreCase(name)) {
				logger.log(Level.INFO, "Found gear "+raw);
				return raw;
			}
		}
		logger.log(Level.WARNING, "Searching gear ''{0}'' failed", name);
		return null;
	}

	//-------------------------------------------------------------------
	private static boolean parseGear(SR6NPC npc, ParsingState runner, String line) {
		String key = line;
		String extra = "";
		if (line.contains("(")) {
			int pos = line.indexOf("(");
			key = line.substring(0, pos).trim();
			extra = line.substring(pos+1, line.indexOf(")"));
		}

		CarriedItem<ItemTemplate> cooked = null;
			ItemTemplate raw = findGearNamed(key, Locale.ENGLISH, extra);
			if (raw!=null) {
				cooked = new CarriedItem<ItemTemplate>(raw, null, CarryMode.CARRIED);
				logger.log(Level.WARNING, "Found gear: ''{0}'' for {1} and extra ''{2}''", raw, line, extra);
				npc.getGear().add(cooked);
				return true;
			}

		return false;
	}

}
