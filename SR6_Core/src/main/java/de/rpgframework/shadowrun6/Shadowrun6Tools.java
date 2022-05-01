package de.rpgframework.shadowrun6;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.CommonCharacter;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.AnyRequirement;
import de.rpgframework.genericrpg.requirements.ExistenceRequirement;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.proc.GetModificationsFromMetaType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.log.Logging;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyQualityModifications;
import de.rpgframework.shadowrun6.proc.ResetModifications;

/**
 * @author prelle
 *
 */
public class Shadowrun6Tools {
	
	private final static Logger logger = System.getLogger("de.rpgframework.shadowrun6");

	private static MultiLanguageResourceBundle RES;

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
	static {
		RES = new MultiLanguageResourceBundle(Shadowrun6Tools.class.getName(), Locale.ENGLISH, Locale.GERMAN);
	}

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
//					Logging.logger.log(Level.WARNING, "Unknown skill reference '"+key+"' in choice "+choice.getUUID()+" from "+data);
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
					Logging.logger.log(Level.WARNING, "Found unknown skill '"+valMod.getKey()+"' in valuemod of "+data);
					return "Unknown skill '"+valMod.getKey()+"'";
				}
				if (valMod.getValue()>0) {
					return skill.getName()+" +"+valMod.getValue();
				} else {
					return skill.getName()+" "+valMod.getValue();
				}
			case QUALITY:
				if (valMod.getConnectedChoice()!=null) {
					Logging.logger.log(Level.WARNING, "TODO: value modification for quality with choice: "+valMod);
				}
				
				Quality quality = Shadowrun6Core.getItem(Quality.class, valMod.getKey());
				if (quality==null) {
					Logging.logger.log(Level.WARNING, "Found unknown quality '"+valMod.getKey()+"' in valuemod of "+data);
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
//					Logging.logger.log(Level.WARNING, "Found unknown cultlore '"+valMod.getKey()+"' in valuemod of "+data);
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
//					Logging.logger.log(Level.WARNING, "Found unknown skill '"+valMod.getKey()+"' in valuemod of "+data);
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

		Logging.logger.log(Level.ERROR, "Missing string conversion for "+mod.getClass());
		return mod.toString();
	}

	//-------------------------------------------------------------------
	public static String getRequirementString(Requirement req, Locale loc) {
		if (req instanceof ExistenceRequirement) {
			ExistenceRequirement tmp = (ExistenceRequirement)req;
			String prefix = (tmp.isNegate())?(RES.getString("require.negate")+" "):"";
			switch ((ShadowrunReference)tmp.getType()) {
			case MAGIC_RESO:
				MagicOrResonanceType morType = Shadowrun6Core.getItem(MagicOrResonanceType.class, tmp.getKey());
				if (morType==null)
					return "Unknown "+tmp.getKey();
				return prefix+morType.getName(loc);
			case METATYPE:
				SR6MetaType meta = Shadowrun6Core.getItem(SR6MetaType.class, tmp.getKey());
				if ( meta==null)
					return "Unknown "+tmp.getKey();
				return prefix+ meta.getName(loc);
			case QUALITY:
				Quality qual = Shadowrun6Core.getItem(Quality.class, tmp.getKey());
				if (qual==null)
					return "Unknown "+tmp.getKey();
				return prefix+qual.getName(loc);
			case SKILL:
				String value = (req instanceof ValueRequirement)?((ValueRequirement)req).getRawValue():"";
				if ("CHOICE".equals(tmp.getKey())) {
					if (tmp.isNegate()) 
						return ResourceI18N.format(RES, loc, "skill.chosen.not", value);
					return ResourceI18N.format(RES, loc, "skill.chosen", value);
				}
				SR6Skill skill = Shadowrun6Core.getItem(SR6Skill.class, tmp.getKey());
				if (skill==null)
					return "Unknown "+tmp.getKey();
				return prefix+skill.getName(loc);
				
//			case MASTERSHIP:
//				Mastership master =  SplitterMondCore.getItem(Mastership.class, tmp.getKey());
//				if (master==null) {
//					return "Unknown "+SplitterMondCore.getI18nResources().getString("label.mastership")+" "+tmp.getKey();
//				}
//				return master.getName(loc);
			default:
				Logging.logger.log(Level.ERROR, "Making cleartext of "+tmp.getType()+" existance req. not supported");
			}
		} else if (req instanceof ValueRequirement) {
			ValueRequirement tmp = (ValueRequirement)req;
			ShadowrunReference type = (ShadowrunReference)tmp.getType();			
			DataItem item = ShadowrunReference.resolve(type, req.getKey());
			switch ((ShadowrunReference)tmp.getType()) {
			case SKILL:
				return item.getName(loc)+" "+tmp.getRawValue()+"+";			
			}
		} else if (req instanceof AnyRequirement) {
			AnyRequirement any = (AnyRequirement)req;
			List<String> names = any.getOptionList().stream().map(r -> getRequirementString(r,loc)).collect(Collectors.toList());
			return "("+String.join(", ", names)+")";
		}

		Logging.logger.log(Level.ERROR, "Missing string conversion for "+req.getClass()+" and "+req.getType());
		System.err.println("Shadowrun6Tool: Missing string conversion for "+req.getClass()+" and "+req.getType());
		return req.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * Walk through all items in the character and resolve them
	 * @param rawChar
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void resolveChar(Shadowrun6Character model) {
		logger.log(Level.INFO, "ENTER resolveChar");
		try {
			logger.log(Level.DEBUG, "resolve qualities");
			for (QualityValue tmp : model.getQualities()) {
				Quality resolved = Shadowrun6Core.getItem(Quality.class, tmp.getKey());
				tmp.setResolved(resolved);
			}

			logger.log(Level.DEBUG, "resolve skills");
			for (SR6SkillValue tmp : model.getSkillValues()) {
				SR6Skill resolved = Shadowrun6Core.getItem(SR6Skill.class, tmp.getKey());
				if (resolved==null) logger.log(Level.ERROR, "Character {} contains unknown skill '{}'", model.getName(), tmp.getKey());
				tmp.setResolved(resolved);
			}
			logger.log(Level.DEBUG, "resolve adept powers");
			for (AdeptPowerValue tmp : model.getAdeptPowers()) {
				AdeptPower resolved = Shadowrun6Core.getItem(AdeptPower.class, tmp.getKey());
				tmp.setResolved(resolved);
			}
			logger.log(Level.DEBUG, "resolve spells");
			for (SpellValue tmp : model.getSpells()) {
				SR6Spell resolved = Shadowrun6Core.getItem(SR6Spell.class, tmp.getKey());
				tmp.setResolved(resolved);
			}
			logger.log(Level.DEBUG, "resolve gear");
			for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
				ItemTemplate resolved = Shadowrun6Core.getItem(ItemTemplate.class, tmp.getKey());
				tmp.setResolved(resolved);
				GearTool.recalculate("", model, tmp);
			}

		} finally {
			logger.log(Level.DEBUG, "LEAVE resolveChar");
		}
	}

	//-------------------------------------------------------------------
	public static boolean isRequirementMet(Shadowrun6Character model, ComplexDataItem requiredFor, Requirement req, Decision[] decisions) {
		if (req.getApply()!=null && req.getApply()!=ApplyTo.CHARACTER) return true;
		
		if (req instanceof ExistenceRequirement) {
			ExistenceRequirement tmp = (ExistenceRequirement)req;
			boolean negated = tmp.isNegate();
			ShadowrunReference type = (ShadowrunReference)tmp.getType();			
			DataItem item = ShadowrunReference.resolve(type, req.getKey());
			switch (type) {
			case QUALITY:
				if (negated) return !model.hasQuality(req.getKey());
				return model.hasQuality(req.getKey());
			case METATYPE:
				if (model.getMetatype()==null) return false;
				if (negated) return !model.getMetatype().getId().equals(req.getKey());
				return model.getMetatype().getId().equals(req.getKey());
			case MAGIC_RESO:
				logger.log(Level.WARNING, "Check for magic/Reso "+item);
				return model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType()==item;
			default:
				logger.log(Level.WARNING, "Todo: isRequirementMet for "+type+" = "+item);
			}			
		} else if (req instanceof AnyRequirement) {
			AnyRequirement any = (AnyRequirement)req;
			for (Requirement tmp : any.getOptionList()) {
				if (isRequirementMet(model, requiredFor, tmp, decisions))
					return true;
			}
			return false;
		} else if (req instanceof ValueRequirement) {
			ValueRequirement tmp = (ValueRequirement)req;
			int min = tmp.getValue();
			ShadowrunReference type = (ShadowrunReference)tmp.getType();			
			DataItem item = ShadowrunReference.resolve(type, req.getKey());
			if (item==null && !("CHOICE".equals(req.getKey()))) {
				logger.log(Level.ERROR, "Cannot find item for key ''{0}''", tmp.getType()+":"+tmp.getKey());
				return false;
			}
			switch (type) {
			case SKILL:
				if ("CHOICE".equals(tmp.getKey())) {
					return true;
				}
				if (model.getSkillValue((SR6Skill)item)==null) {
					return false;
				}
				int val = model.getSkillValue( (SR6Skill)item).getModifiedValue();
				if (min>0 && val<min) return false;
				//if (max>0 && val>min) return false;
				return true;
			default:
				logger.log(Level.WARNING, "Todo: isRequirementMet for "+type);
			}			
		}
		System.err.println("Shadowrun6Tool: Requirement checking not supported for "+req.getClass()+" and "+req.getType());
		logger.log(Level.WARNING,"ToDo: Requirement checking not supported for "+req.getClass()+" and "+req.getType());
		return false;
	}

	//-------------------------------------------------------------------
	public static Possible areRequirementsMet(Shadowrun6Character model, ComplexDataItem data, Decision[] decisions) {
		List<Requirement> list = new ArrayList<>();
		for (Requirement req : data.getRequirements()) {
			if (req.getApply()!=null && req.getApply()!=ApplyTo.CHARACTER)
				continue;
			if (!isRequirementMet(model, data, req, decisions)) {
				list.add(req);
			}
		}
		
		if (list.isEmpty())
			return Possible.TRUE;
		System.err.println("Shadowrun6Tools.areReqMet: Not met for "+data+": "+list);
		return new Possible(list.toArray(new Requirement[list.size()]));
	}

	//-------------------------------------------------------------------
	public static Possible areAllDecisionsPresent(ComplexDataItem model, Decision...decisions) {
		for (Choice choice : model.getChoices()) {
			boolean decisionMissing = true;
			for (Decision dec : decisions) {
				if (dec==null) continue;
				if (choice.getUUID().equals(dec.getChoiceUUID())) {
					decisionMissing = false;
					break;
				}
			}
			if (decisionMissing) {
				return new Possible(Severity.INFO, RES, "impossible.choiceMissing");
			}
		}
		
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	public static Possible checkDecisionsAndRequirements(Shadowrun6Character model, ComplexDataItem data, Decision...decisions) {
		Possible p1 = areRequirementsMet(model, data, decisions);
		Possible p2 = areAllDecisionsPresent(data, decisions);
		
		return new Possible(p1, p2);
	}
	
	//-------------------------------------------------------------------
	public static Modification instantiateModification(Modification tmp, ComplexDataItemValue<?> value, CommonCharacter<?, ?, ?> model) {
		if (tmp instanceof ValueModification) {
			ValueModification clone = ((ValueModification)tmp).clone();
			if ("CHOICE".equals( clone.getKey() )) {
				UUID uuid =  ((ValueModification) tmp).getConnectedChoice();
				Decision dec = value.getDecision(uuid);
				if (dec!=null) {
					clone.setKey( dec.getValue());
				} else {
					logger.log(Level.ERROR, "No decision for {0} found in {1}", uuid, value);
				}
			}
			
			return clone;
		}
		if (tmp instanceof DataItemModification) {
			DataItemModification clone = ((DataItemModification)tmp).clone();
			if ("CHOICE".equals( clone.getKey() )) {
				UUID uuid =  ((ValueModification) tmp).getConnectedChoice();
				Decision dec = value.getDecision(uuid);
				if (dec!=null) {
					clone.setKey( dec.getValue());
				} else {
					logger.log(Level.ERROR, "No decision for {0} found in {1}", uuid, value);
				}
			}
			
			return clone;
		}
		
		throw new IllegalArgumentException("Cannot instantiate "+tmp.getClass());
	}
	
}
