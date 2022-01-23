package de.rpgframework.shadowrun6;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.ExistenceRequirement;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.proc.GetModificationsFromMetaType;
import de.rpgframework.shadowrun6.log.Logging;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyQualityModifications;
import de.rpgframework.shadowrun6.proc.ResetModifications;

/**
 * @author prelle
 *
 */
public class Shadowrun6Tools {

	public final static List<Class<? extends ProcessingStep>> RECALCULATE_STEPS = Arrays.asList(
		ResetModifications.class,
//		new ResolveChoicesInReferences(),
		GetModificationsFromMetaType.class,
		ApplyQualityModifications.class
//		new GetModificationsFromMagicOrResonance(),
//		new GetModificationsFromQualities(),
//		new ApplyAdeptPowerModifications(),
////		new GetModificationsFromPowers(),
//		new RecalculateEquipment(),
////		new FixDeprecatedRecursiveAccessories(),
//		new FixOldWeaponType(),
//		new GetModificationsFromEquipment(),
//		new GetModificationsFromMetamagicOrEchoes(),
//		new GetModificationsFromFoci(),
//		new ApplyAdeptPowerModifications(),
//		new GetModificationsFromPowers(),
//		new GetModificationsFromTechniques(),
//		new ApplyCarriedItemModifications(),
//		new DistributeAccessoriesToContainers(),
//		new ApplyAttributeModifications(),
//		new ApplySkillModifications(),
//		new ApplyMemorizedUUIDModifications(),
////		new ApplySINModifications(),
//		new ConnectSignatureManeuvers(),
//		new ApplyRelevanceAndEdgeMods(),
//		new CalculateDerivedAttributes(),
//		new CalculateEssence(),
//		new CalculatePersona(),
	);

	//-------------------------------------------------------------------
	public static List<ProcessingStep> getCharacterProcessingSteps(Shadowrun6Character model) {
		List<ProcessingStep> steps = new ArrayList<>();
		for (Class<? extends ProcessingStep> cls : RECALCULATE_STEPS) {
			Constructor<? extends ProcessingStep> cons =  null;
			try {
				cons = cls.getConstructor(ShadowrunCharacter.class);
			} catch (Exception e) {
			}
			if (cons==null) {
				try {
					cons = cls.getConstructor(Shadowrun6Character.class);
				} catch (Exception e) {
				}
			}
			
			if (cons!=null) {
				try {
					steps.add( cons.newInstance(model));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return steps;
	}

	//-------------------------------------------------------------------
	public static String getChoiceString(ComplexDataItem data, Choice choice) {
//		switch ((SplittermondReference)choice.getChooseFrom()) {
//		case ATTRIBUTE:
//			if (choice.getChoiceOptions()==null) {
//				if (choice.getDistribute()!=null) {
//					int sum = Arrays.asList(choice.getDistribute()).stream().reduce(0, Integer::sum);
//					return "Verteile "+sum+" Punkte auf "+choice.getDistribute().length+" verschiedene Attribute";
//				}
//				return "Wähle ein beliebiges Attribut";
//			}
//			List<String> attribs = Arrays.asList( choice.getChoiceOptions() );
//			List<String> except  = new ArrayList<>();
//			for (Attribute tmp : Attribute.primaryValues()) {
//				if (!attribs.contains(tmp.name()))
//					except.add(tmp.getName());
//			}
//			return "Wähle ein Attribut außer "+String.join(", ", except);
//		case SKILL:
//			if (choice.getChoiceOptions()==null) {
//				if (choice.getDistribute()!=null) {
//					int sum = Arrays.asList(choice.getDistribute()).stream().reduce(0, Integer::sum);
//					return "Verteile "+sum+" Punkte auf "+choice.getDistribute().length+" verschiedene Fertigkeiten";
//				}
//				return "Wähle ein beliebiges Fertigkeit";
//			}
//			attribs = Arrays.asList( choice.getChoiceOptions() );
//			List<String> positive  = new ArrayList<>();
//			except  = new ArrayList<>();
//			for (String key : choice.getChoiceOptions()) {
//				if ("MAGIC".equals(key)) {
//					positive.add("eine Magieschule");
//					break;
//				}
//				if ("COMBAT".equals(key)) {
//					positive.add("eine Kampffertigkeit");
//					break;
//				}
//				SMSkill skill = SplitterMondCore.getSkill(key);
//				if (skill!=null)
//					positive.add(skill.getName());
//				else {
//					Logging.logger.warn("Unknown skill reference '"+key+"' in choice "+choice.getUUID()+" from "+data);
//				}
//			}
//			for (SMSkill tmp : SplitterMondCore.getItemList(SMSkill.class)) {
//				if (!attribs.contains(tmp.getId()))
//					except.add(tmp.getName());
//			}
//			if (except.size()<positive.size())
//				return "Wähle eine Fertigkeit außer "+String.join(", ", except);
//			return String.join(" oder ", positive);
//		}
		return "SplitterTools.getChoiceString("+choice.getChooseFrom()+")";
	}

	//-------------------------------------------------------------------
	public static String getModificationString(ComplexDataItem data, Modification mod) {
		ShadowrunReference type = (ShadowrunReference) mod.getReferenceType();
		if (mod instanceof ValueModification) {
			ValueModification valMod = (ValueModification)mod;
			String what = type.name();
			switch (type) {
			case ATTRIBUTE:
				if (valMod.getConnectedChoice()!=null) {
					Choice choice = data.getChoice(valMod.getConnectedChoice());
					if (choice==null) {
						return "Unknown choice "+valMod.getConnectedChoice();
					}
					if (ShadowrunReference.ATTRIBUTE==choice.getChooseFrom()) {
						if (valMod.getValue()>0) {
							return "ein beliebiges Attribut +"+valMod.getValue();
						} else {
							return getChoiceString(data, choice);
						}
					}
					return "???"+choice.getChooseFrom()+"???";
				}
				
				if (valMod.getValue()>0) {
					if (valMod.getSet()==ValueType.MAX)
						return ShadowrunAttribute.valueOf(valMod.getKey()).getName()+" "+valMod.getValue();
					return ShadowrunAttribute.valueOf(valMod.getKey()).getName()+" +"+valMod.getValue();
				} else {
					return ShadowrunAttribute.valueOf(valMod.getKey()).getName()+" "+valMod.getValue();
				}
			case SKILL:
				if (valMod.getConnectedChoice()!=null) {
					Choice choice = data.getChoice(valMod.getConnectedChoice());
					if (choice==null) {
						return "Unknown choice "+valMod.getConnectedChoice();
					}
					if (ShadowrunReference.SKILL==choice.getChooseFrom()) {
						if (valMod.getAsKeys().length>4) {
							return "ein gewählte Fertigkeit +"+valMod.getValue();
						} else {
							return getChoiceString(data, choice)+" +"+valMod.getValue();
						}
					}
					return "???"+choice.getChooseFrom()+"???";
				}
				
				SR6Skill skill = Shadowrun6Core.getSkill(valMod.getKey());
				if (skill==null) {
					Logging.logger.warn("Found unknown skill '"+valMod.getKey()+"' in valuemod of "+data);
					return "Unknown skill '"+valMod.getKey()+"'";
				}
				if (valMod.getValue()>0) {
					return skill.getName()+" +"+valMod.getValue();
				} else {
					return skill.getName()+" "+valMod.getValue();
				}
			case QUALITY:
				if (valMod.getConnectedChoice()!=null) {
					Logging.logger.warn("TODO: value modification for quality with choice: "+valMod);
				}
				
				Quality quality = Shadowrun6Core.getItem(Quality.class, valMod.getKey());
				if (quality==null) {
					Logging.logger.warn("Found unknown quality '"+valMod.getKey()+"' in valuemod of "+data);
					return "Unknown quality '"+valMod.getKey()+"'";
				}
				if (valMod.getValue()>0) {
					return quality.getName()+" +"+valMod.getValue();
				} else {
					return quality.getName()+" "+valMod.getValue();
				}
			default:
				return "Unknown value type "+type;
			}
		}

		else if (mod instanceof DataItemModification) {
			DataItemModification valMod = (DataItemModification)mod;
			String what = type.name();
			DataItem resolved = type.resolve(valMod.getKey());
			switch (type) {
			case QUALITY:
				Quality qual = Shadowrun6Core.getItem(Quality.class, valMod.getKey());
				if (qual==null) {
					return "Unknown quality '"+valMod.getKey()+"'";
				}
				return qual.getName(Locale.getDefault());
//			case CULTURE_LORE:
//				if (valMod.getConnectedChoice()!=null) {
//					Choice choice = data.getChoice(valMod.getConnectedChoice());
//					if (choice==null) {
//						return "Unknown choice "+valMod.getConnectedChoice();
//					}
//					return "???"+choice.getChooseFrom()+"???";
//				}
//				
//				CultureLore cultlore = type.resolve(valMod.getKey());
//				if (cultlore==null) {
//					Logging.logger.warn("Found unknown cultlore '"+valMod.getKey()+"' in valuemod of "+data);
//					return "Unknown cultlore '"+valMod.getKey()+"'";
//				}
//				return "Kulturkunde "+cultlore.getName();
//			case LANGUAGE:
//				if (valMod.getConnectedChoice()!=null) {
//					Choice choice = data.getChoice(valMod.getConnectedChoice());
//					if (choice==null) {
//						return "Unknown choice "+valMod.getConnectedChoice();
//					}
//					return "???"+choice.getChooseFrom()+"???";
//				}
//				
//				Language lang = type.resolve(valMod.getKey());
//				if (lang==null) {
//					Logging.logger.warn("Found unknown skill '"+valMod.getKey()+"' in valuemod of "+data);
//					return "Unknown language '"+valMod.getKey()+"'";
//				}
//				return "Sprache "+lang.getName();
//			case POWER:
//				Power power = SplitterMondCore.getItem(Power.class, valMod.getKey());
//				if (power==null) {
//					return "Unbekannte Kraft '"+valMod.getKey()+"'";
//				}
//				return power.getName(Locale.getDefault());
			}
			return "ToDo: "+type;
		}

		Logging.logger.error("Missing string conversion for "+mod.getClass());
		return mod.toString();
	}

	//-------------------------------------------------------------------
	public static String getRequirementString(Requirement req, Locale loc) {
		if (req instanceof ExistenceRequirement) {
			ExistenceRequirement tmp = (ExistenceRequirement)req;
//			switch ((ShadowrunReference)tmp.getType()) {
//			case MASTERSHIP:
//				Mastership master =  SplitterMondCore.getItem(Mastership.class, tmp.getKey());
//				if (master==null) {
//					return "Unknown "+SplitterMondCore.getI18nResources().getString("label.mastership")+" "+tmp.getKey();
//				}
//				return master.getName(loc);
//			}
		}

		Logging.logger.error("Missing string conversion for "+req.getClass());
		return req.toString();
	}
}
