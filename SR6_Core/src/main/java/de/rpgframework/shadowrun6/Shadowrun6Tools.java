package de.rpgframework.shadowrun6;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.ResourceI18N;
import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.PoolCalculation;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.CommonCharacter;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.DataErrorException;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.genericrpg.items.formula.FormulaImpl;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.items.formula.VariableResolver;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.AnyRequirement;
import de.rpgframework.genericrpg.requirements.ExistenceRequirement;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun.proc.GetModificationsFromFoci;
import de.rpgframework.shadowrun.proc.GetModificationsFromMetaType;
import de.rpgframework.shadowrun.proc.GetModificationsFromQualities;
import de.rpgframework.shadowrun6.items.AmmunitionType;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.items.SR6ResolveTemplatesStep;
import de.rpgframework.shadowrun6.log.Logging;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import de.rpgframework.shadowrun6.proc.ApplyModificationsGeneric;
import de.rpgframework.shadowrun6.proc.CalculateDerivedAttributes;
import de.rpgframework.shadowrun6.proc.CalculateEssence;
import de.rpgframework.shadowrun6.proc.CalculatePersona;
import de.rpgframework.shadowrun6.proc.EnsureAttributePresence;
import de.rpgframework.shadowrun6.proc.GetModificationsFromGear;
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
		EnsureAttributePresence.class,
//		new ResolveChoicesInReferences(),
		GetModificationsFromMetaType.class,
		ApplyModificationsGeneric.class,
//		new GetModificationsFromMagicOrResonance(),
		GetModificationsFromQualities.class,
//		new ApplyAdeptPowerModifications(),
////		new GetModificationsFromPowers(),
//		new RecalculateEquipment(),
////		new FixDeprecatedRecursiveAccessories(),
//		new FixOldWeaponType(),
		GetModificationsFromGear.class,
//		new GetModificationsFromMetamagicOrEchoes(),
		GetModificationsFromFoci.class,
//		new ApplyAdeptPowerModifications(),
		ApplyModificationsGeneric.class,
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
		CalculateDerivedAttributes.class,
		CalculateEssence.class,
		CalculatePersona.class
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
	public static void runProcessors(Shadowrun6Character model) {
		List<ProcessingStep> processChain = getCharacterProcessingSteps(model);
		try {
			logger.log(Level.DEBUG, "\n\nSTART: runProcessors: "+processChain.size()+"-------------------------------------------------------");
			List<Modification> unprocessed = new ArrayList<>();
			for (ProcessingStep processor : processChain) {
				unprocessed = processor.process(unprocessed);
				logger.log(Level.DEBUG, "------ after "+processor.getClass().getSimpleName()+"     "+unprocessed);
			}
			logger.log(Level.DEBUG, "Remaining mods  = "+unprocessed);
			logger.log(Level.DEBUG, "STOP : runProcessors: "+processChain.size()+"-------------------------------------------------------");
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed calculating character",e);
		}
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
	public static String getModificationSourceString(Object source) {
		if (source==null)
			return "Unknown source";
		if (source instanceof ShadowrunAttribute) 
			return ((ShadowrunAttribute)source).getName();
		if (source instanceof SR6ItemAttribute) 
			return ((SR6ItemAttribute)source).getName();
		if (source instanceof SR6Skill) 
			return ((SR6Skill)source).getName();
//		if (source instanceof Technique) 
//			return ((Technique)source).getName();
		if (source instanceof CarriedItem) {
			return ((CarriedItem)source).getNameWithRating();
		} else if (source instanceof AdeptPower) {
			return RES.getString("label.adeptpower")+" "+((AdeptPower)source).getName();
		} else if (source instanceof ItemTemplate) {
			return ((ItemTemplate)source).getName();
		} else if (source instanceof String) {
			return (String)source;
//		} else if (source instanceof FocusValue) {
//			return ((FocusValue)source).getName();
		} else if (source instanceof DataItem) {
			return ((DataItem)source).getName();
		}
		logger.log(Level.WARNING,"Missing treatment for modification source: "+source.getClass());
		return source.getClass().getSimpleName();
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
				String prefix = (valMod.isRemove())?"- ":"";
				if (valMod.getValue()>0) {
					return prefix+quality.getName()+" +"+valMod.getValue();
				} else {
					return prefix+quality.getName()+" "+valMod.getValue();
				}
			default:
				return "Unknown value type "+type;
			}
		}

		else if (mod instanceof DataItemModification) {
			DataItemModification valMod = (DataItemModification)mod;
			String what = type.name();
			DataItem resolved = type.resolve(valMod.getKey());
			
			String prefix = valMod.isRemove()?"- ":"";
			if (resolved!=null) {
				if (valMod.getDecisions().isEmpty())				
					return prefix+resolved.getName(Locale.getDefault());
				return prefix+resolved.getName(Locale.getDefault())+"(..)";
			}
			
//			switch (type) {
//			case QUALITY:
//				Quality qual = Shadowrun6Core.getItem(Quality.class, valMod.getKey());
//				if (qual==null) {
//					return "Unknown quality '"+valMod.getKey()+"'";
//				}
//				if (valMod.getDecisions().isEmpty())				
//					return qual.getName(Locale.getDefault());
//				return qual.getName(Locale.getDefault())+"(..)";
//				
//			case CRITTER_POWER:
//			}
			logger.log(Level.ERROR, "Not supported yet: "+type);
			return "ToDo: "+type;
		}

		Logging.logger.log(Level.ERROR, "Missing string conversion for "+mod.getClass());
		return mod.toString();
	}

	//-------------------------------------------------------------------
	public static Function<Requirement, String> requirementResolver(Locale loc) {
		return (r) -> getRequirementString(r, loc);
	}
	
	//-------------------------------------------------------------------
	public static String getRequirementString(Requirement req, Locale loc) {
		if (req instanceof ExistenceRequirement) {
			ExistenceRequirement tmp = (ExistenceRequirement)req;
			String prefix = (tmp.isNegate())?(RES.getString("require.negate")+" "):"";
			switch ((ShadowrunReference)tmp.getType()) {
			case GEAR:
				ItemTemplate gear = ShadowrunReference.resolve((ShadowrunReference)tmp.getType(), tmp.getKey());
				if (gear==null)
					return "Unknown "+tmp.getKey();
				return prefix+gear.getName(loc);
			case ITEMSUBTYPE:
				ItemSubType subtype = ShadowrunReference.resolve((ShadowrunReference)tmp.getType(), tmp.getKey());
				return prefix+subtype.getName();
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
				System.err.println("Shadowrun6Tools: Making cleartext of "+tmp.getType()+" existance req. not supported");
				Logging.logger.log(Level.ERROR, "Making cleartext of "+tmp.getType()+" existance req. not supported");
			}
		} else if (req instanceof ValueRequirement) {
			ValueRequirement tmp = (ValueRequirement)req;
			ShadowrunReference type = (ShadowrunReference)tmp.getType();
			DataItem item = null;
			switch ((ShadowrunReference)tmp.getType()) {
			case ATTRIBUTE:
				if (tmp.getMaxValue()>0) {
					return ShadowrunAttribute.valueOf(req.getKey()).getName(loc)+" <="+tmp.getMaxValue();			
				}
				return ShadowrunAttribute.valueOf(req.getKey()).getName(loc)+" "+tmp.getValue()+"+";			
			case SKILL:
				item = ShadowrunReference.resolve(type, req.getKey());
				return item.getName(loc)+" "+tmp.getRawValue()+"+";			
			case QUALITY:
				DataItem qual = ShadowrunReference.resolve((ShadowrunReference)tmp.getType(), tmp.getKey());
				if (qual==null)
					return "Unknown "+tmp.getKey();
				return qual.getName(loc);
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

			logger.log(Level.DEBUG, "resolve quality paths");
			for (QualityPathValue tmp : model.getQualityPaths()) {
				QualityPath resolved = Shadowrun6Core.getItem(QualityPath.class, tmp.getKey());
				tmp.setResolved(resolved);
			}

			logger.log(Level.DEBUG, "resolve skills");
			for (SR6SkillValue tmp : model.getSkillValues()) {
				SR6Skill resolved = Shadowrun6Core.getItem(SR6Skill.class, tmp.getKey());
				if (resolved==null) logger.log(Level.ERROR, "Character {} contains unknown skill '{}'", model.getName(), tmp.getKey());
				tmp.setResolved(resolved);
				// Specs
				for (SkillSpecializationValue<SR6Skill> v : tmp.getSpecializations()) {
					if (v.getResolved()==null) {
						SkillSpecialization<SR6Skill> spec = resolved.getSpecialization(v.getKey());
						if (spec==null) {
							logger.log(Level.ERROR, "Character {} contains unknown skill specialization '{}'", model.getName(), v.getKey());
						} else {
							v.setResolved(spec);
						}
					}
				}
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
			SR6ResolveTemplatesStep resolver = new SR6ResolveTemplatesStep();
			logger.log(Level.DEBUG, "resolve lifestyles");
			for (SR6Lifestyle tmp : model.getLifestyles()) {
				LifestyleQuality resolved = Shadowrun6Core.getItem(LifestyleQuality.class, tmp.getKey());
				tmp.setResolved(resolved);
			}

			for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
				resolver.process("", ShadowrunReference.ITEM_ATTRIBUTE, model, tmp, List.of());
				SR6GearTool.recalculate("", model, tmp);
			}
		} catch (DataErrorException e) {
			logger.log(Level.ERROR, "Failed resolving reference {1} ''{2}'' in character {0}", model.getName(), e.getReferenceError().getType(), e.getReferenceError().getReference(), e);
		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed resolving references in character {0}", model.getName(),e);
		} finally {
			logger.log(Level.DEBUG, "LEAVE resolveChar");
		}
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
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
				return model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType()==item;
			case GEAR:
				// Character needs to have a specific gear
				for (CarriedItem<ItemTemplate> gear : model.getCarriedItems()) {
					if (gear.getModifyable().getId().equals(req.getKey())) {
						return true;
					}						
				}
				return false;
			default:
				System.err.println("Shadowrun6Tool: Todo: isRequirementMet for "+type+" = "+item);
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
			ShadowrunReference type = (ShadowrunReference)tmp.getType();			
			int min = -1;
			int max = Integer.MAX_VALUE;
			if (tmp.getFormula().isResolved()) {
				if (tmp.getRawValue()!=null) {
					min = tmp.getValue();
				} else {
					max = tmp.getMaxValue();
				}
			} else {
				logger.log(Level.WARNING, "ToDo: check unresolved requirement "+req.getKey()+":"+tmp.getFormula()+" for "+requiredFor.getClass());
				if (requiredFor.getClass()==ItemTemplate.class) {
					logger.log(Level.WARNING, "Special handling for ItemTemplates");
					CarryMode mode = ((ItemTemplate) requiredFor).getUsages().get(0).getMode();
					CarriedItem item = GearTool.buildItem((ItemTemplate) requiredFor, mode, model, false, decisions).get();
					VariableResolver resolver = new VariableResolver(item, model);
					logger.log(Level.WARNING, "ToDo: Resolve "+tmp.getFormula());
					SR6ItemAttribute itemAttr = SR6ItemAttribute.valueOf( ((FormulaImpl)tmp.getFormula()).getAsString().substring(1));
					String raw = FormulaTool.resolve(ShadowrunReference.ITEM_ATTRIBUTE, (FormulaImpl)tmp.getFormula(), resolver);
					min = Integer.valueOf(raw);
				}
			}
			Object item = ShadowrunReference.resolve(type, req.getKey());
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
				if (max!=Integer.MAX_VALUE && val>max) return false;
				if (min>0 && val<min) return false;
				//if (max>0 && val>min) return false;
				return true;
			case ATTRIBUTE:
				if ("CHOICE".equals(tmp.getKey())) {
					return true;
				}
				if (model.getAttribute((ShadowrunAttribute)item)==null) {
					return false;
				}
				val = model.getAttribute( (ShadowrunAttribute)item).getModifiedValue();
				if (max!=Integer.MAX_VALUE && val>max) return false;
				if (min>0 && val<min) return false;
				//if (max>0 && val>min) return false;
				return true;
			case QUALITY:
				if ("CHOICE".equals(tmp.getKey())) {
					return true;
				}
				if (model.getQuality(tmp.getKey())==null) {
					return false;
				}
				val = model.getQuality(tmp.getKey()).getModifiedValue();
				if (max!=Integer.MAX_VALUE && val>max) return false;
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
	public static Possible checkDecisionsAndRequirements(Shadowrun6Character model, ComplexDataItem data, Decision...decisions) {
		Possible p1 = areRequirementsMet(model, data, decisions);
		Possible p2 = GenericRPGTools.areAllDecisionsPresent(data, decisions);
		
		return new Possible(p1, p2);
	}
	
	//-------------------------------------------------------------------
	public static Modification instantiateModification(Modification tmp, ComplexDataItemValue<?> value, CommonCharacter<?, ?, ?,?> model) {
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
	
	//-------------------------------------------------------------------
	public static String getAttackRatingString(int[] attackRating) {
		if (attackRating==null) return "ERROR";
		String[] ratings = new String[attackRating.length];
		for (int i=0; i<ratings.length; i++) {
			int val = attackRating[i];
			if (val>0)
				ratings[i] = String.valueOf(val);
			else
				ratings[i] = "-";
		}
		return String.join("/", ratings);
	}

	//-------------------------------------------------------------------
	/**
	 * Give the advantages of this item and non-recursively walk through all
	 * embedded items and return their advantages as well
	 */
	public static Collection<String> getWiFiAdvantageStrings(CarriedItem<ItemTemplate> carried, Locale locale) {
		List<String> ret = new ArrayList<>();
		ret.addAll( carried.getResolved().getWiFiAdvantageStrings(carried.getCarryMode(), carried.getVariant(), locale) );
		return ret;
	}

	//-------------------------------------------------------------------
	public static boolean isType(CarriedItem<ItemTemplate> item, ItemType ... types) {
		ItemType found = item.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue();
		for (ItemType tmp : types) {
			if (tmp==found) return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	public static boolean isSubtype(CarriedItem<ItemTemplate> item, ItemSubType ... types) {
		ItemSubType found = item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
		for (ItemSubType tmp : types) {
			if (tmp==found) return true;
		}
		return false;
	}
	
	//-------------------------------------------------------------------
	public static List<CarriedItem<ItemTemplate>> getMatrixItems(Shadowrun6Character model) {
		return model.getCarriedItems()
				.stream()
				.filter(ci -> ci.hasFlag(ItemTemplate.FLAG_MATRIX_DEVICE))
				.collect(Collectors.toList());

	}

	//--------------------------------------------------------------------
	public static List<PoolCalculation<Integer>> getAttributeModifierCalculation(Shadowrun6Character model, ShadowrunAttribute attrib) {
		List<PoolCalculation<Integer>> ret = new ArrayList<>();
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(attrib);
		// Now add modifiers from the attribute
		int augAllowed = 4;
		if (attrib.name().startsWith("DEFENSIVE_POOL")) {
			augAllowed = 99;
		}
		for (Modification mod : aVal.getModifications()) {
			if (mod.getReferenceType()==ShadowrunReference.ATTRIBUTE && mod instanceof ValueModification) {
				ValueModification sMod = (ValueModification)mod;
				if (!sMod.isConditional() && sMod.getSet()!=ValueType.MAX && sMod.getSet()!=ValueType.ARTIFICAL) {
					int val = Math.min(augAllowed, sMod.getValue());
					if (sMod.getSet()==ValueType.NATURAL)
						val = sMod.getValue();
					// Mark modifiers being capped with augmentation limit
					PoolCalculation<Integer> calc = new PoolCalculation<Integer>(val, Shadowrun6Tools.getModificationSourceString(sMod.getSource()));
					// Augmentation limit is only valid if not NATURAL
					if (sMod.getSet()!=ValueType.NATURAL)
						calc.hitLimit = val<sMod.getValue();
					ret.add(calc);
					augAllowed -= val;
				}
			}
		}
		
		return ret;
	}

	//--------------------------------------------------------------------
	public static List<PoolCalculation<Integer>> getAttributePoolCalculation(Shadowrun6Character model, ShadowrunAttribute attrib) {
		List<PoolCalculation<Integer>> ret = new ArrayList<>();
		// Add the unmodified attribute
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(attrib);
		if (aVal.getDistributed()>0)
			ret.add(new PoolCalculation<Integer>(aVal.getDistributed(), aVal.getModifyable().getName()));
		// Now add modifiers from the attribute
		ret.addAll(getAttributeModifierCalculation(model, attrib));
		
		return ret;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @param skill
	 * @param useAttrib  Attribute to use 
	 * @param special IDs of specializations to use (only use highest)
	 * @return
	 */
	public static List<PoolCalculation<Integer>> getSkillPoolCalculationWithoutAttribute(Shadowrun6Character model, SR6Skill skill, String...special) {
		List<PoolCalculation<Integer>> ret = new ArrayList<>();
		
		// Add the unmodified skill
		SR6SkillValue     sVal = model.getSkillValue(skill);
		if (sVal==null) {
			// Skill not present
			if (!skill.isUseUntrained()) {
				RES.format("explain.skill_not_untrained", skill.getName());
				ret.add(new PoolCalculation<Integer>(0, RES.format("explain.skill_not_untrained", skill.getName())));
				return ret;
			} else {
				ret.add(new PoolCalculation<Integer>(-1, RES.format("explain.untrained_skill", skill.getName())));
			}
		} else {
			ret.add(new PoolCalculation<Integer>(sVal.getDistributed(), Shadowrun6Core.getI18nResources().format( "explain.skillpoints", skill.getName())));
//			if (sVal.getAlternativePoints()>sVal.getDistributed()) {
//				ret.clear();
//				ret.add(new PoolCalculation(sVal.getAlternativePoints(), Resource.format(ShadowrunCore.getI18nResources(), "explain.skillpoints.alternative", 
//						skill.getName(),
//						ShadowrunTools.getModificationSourceString(sVal.getAlternativeSource()))));
//			}
			// Now add modifiers from the skill
			int augAllowed = 4;
			for (Modification mod : sVal.getModifications()) {
				if (mod.getReferenceType()==ShadowrunReference.SKILL && mod instanceof ValueModification) {
					ValueModification sMod = (ValueModification)mod;
					if (sMod.getResolvedKey()==skill && !sMod.isConditional() && sMod.getSet()!=ValueType.ARTIFICAL && sMod.getSet()!=ValueType.MAX) {
						int val = Math.min(augAllowed, sMod.getValue());
						// Mark modifiers being capped with augmentation limit
						PoolCalculation calc = new PoolCalculation(val, Shadowrun6Tools.getModificationSourceString(sMod.getSource()));
						calc.hitLimit = val<sMod.getValue();
						ret.add(calc);
						augAllowed -= val;
					}
				}
			}
			
			// Now specializations
//			// DE: No +2 for skill specializations
//			if (skill.getId().equals("exotic_weapons") && Locale.getDefault().getLanguage().equals("de")) {
//				logger.debug("DE players don't get boni from exotic weapon specializations");
//			} else {
				SkillSpecializationValue bestSpec = null;
				for (SkillSpecializationValue spec : sVal.getSpecializations()) {
					// Test if specializ. matches requested specs
					if (!Arrays.asList(special).contains(spec.getResolved().getId()))
						continue;
					//if (bestSpec == null || spec.isExpertise())
						bestSpec = spec;
				}
//				if (bestSpec != null && !skill.getId().contains("exotic")) {
//					if (bestSpec.isExpertise()) {
//						ret.add(new PoolCalculation(3,
//								Resource.format(CORE, "explain.expertise", bestSpec.getSpecial().getName())));
//					} else {
//						ret.add(new PoolCalculation(2,
//								Resource.format(CORE, "explain.specialization", bestSpec.getSpecial().getName())));
//					}
//				}
//			}
		}
		
		return ret;
	}

	//--------------------------------------------------------------------
	/**
	 * @param skill
	 * @param useAttrib  Attribute to use 
	 * @param special IDs of specializations to use (only use highest)
	 * @return
	 */
	public static List<PoolCalculation<Integer>> getSkillPoolCalculation(Shadowrun6Character model, SR6Skill skill, ShadowrunAttribute useAttrib, String...special) {
		List<PoolCalculation<Integer>> ret = new ArrayList<>();
		ret.addAll(getSkillPoolCalculationWithoutAttribute(model, skill, special));
		// Add the attribute
		ret.addAll(getAttributePoolCalculation(model, useAttrib));
		
		
		return ret;
	}

	//--------------------------------------------------------------------
	public static int getSkillPool(Shadowrun6Character model, SR6Skill skill, String... special) {
		return (int)getSkillPoolCalculation(model, skill, skill.getAttribute(), special).stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
	}

	//--------------------------------------------------------------------
	public static int getSkillPool(Shadowrun6Character model, SR6Skill skill, ShadowrunAttribute useAttrib, String... special) {
		return (int)getSkillPoolCalculation(model, skill, useAttrib, special).stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
	}

	//--------------------------------------------------------------------
	public static int getSkillPoolWithoutAttribute(Shadowrun6Character model, SR6Skill skill, String... special) {
		return (int)getSkillPoolCalculationWithoutAttribute(model, skill, special).stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
	}

	//--------------------------------------------------------------------
	public static String getSkillPoolExplanation(Shadowrun6Character model, SR6Skill skill, String... special) {
		return String.join("\n",getSkillPoolCalculation(model, skill, skill.getAttribute(), special).stream().map(pool -> pool.value+" "+pool.source+(pool.hitLimit?"*":" ") ).collect(Collectors.toList()));
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("incomplete-switch")
	public static String getInitiativeString(Shadowrun6Character model, ShadowrunAttribute iniAttribute) {
		String base = model.getAttribute(iniAttribute).toString();
		switch (iniAttribute) {
		case INITIATIVE_PHYSICAL:
			return RES.format("label.ini", base, model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL).getModifiedValue());
		case INITIATIVE_MATRIX:
			return RES.format("label.ini", base, model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getModifiedValue());
//		case INITIATIVE_MATRIX_VR_COLD:
//			return RES.format("label.ini", base, model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD).getModifiedValue());
//		case INITIATIVE_MATRIX_VR_HOT:
//			return RES.format("label.ini", base, model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_HOT).getModifiedValue());
		case INITIATIVE_ASTRAL:
			return RES.format("label.ini", base, model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL).getModifiedValue());
		}
		return ""+base;
	}

	//-------------------------------------------------------------------
	public static String getDrainString(SR6Spell spell) {
		if (spell.getDrain()<0)
			return RES.getString("label.drain.short")+" "+spell.getDrain();
		if (spell.getDrain()>0)
			return RES.getString("label.drain.short")+" +"+spell.getDrain();
		return RES.getString("label.drain.short");
	}
	
	//-------------------------------------------------------------------
	public static int[] getMonitorArray(ShadowrunCharacter model, ShadowrunAttribute attr) {
		int add = 0;
//		int add = model.getAttribute(attr).getModifiedValue();
//		add = Math.round( (float)add / 2.0f);
		if (attr==ShadowrunAttribute.BODY && model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR)!=null)
			add+=model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getModifiedValue();
		if (attr==ShadowrunAttribute.WILLPOWER && model.getAttribute(ShadowrunAttribute.STUN_MONITOR)!=null)
			add+=model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getModifiedValue();
		int[] ret = new int[add];

		int start = 0;
		int every = 3;
		if (model.hasAdeptPower("pain_resistance")) {
			start+=model.getAdeptPower("pain_resistance").getModifiedValue();
		}

		for (int i=start; i<ret.length; i++) {
			ret[i] = - ((i+1-start)/every);
			if (attr==ShadowrunAttribute.BODY && model.hasQuality("high_pain_tolerance")) {
				if (ret[i]<0)
					ret[i]++;
			}
			if (attr==ShadowrunAttribute.BODY && model.hasQuality("low_pain_tolerance")) {
				ret[i]*=2;;
			}
		}
		logger.log(Level.DEBUG, "array for "+attr+": "+Arrays.toString(ret));

		return ret;
	}

	//--------------------------------------------------------------------
	/**
	 * Prepare a single section from a multicolumn table with sections
	 * @param <T> Data type
	 * @param <C> Column type. 
	 * @param <S> Section type. Should implement comparable
	 * @param data Data to sort
	 * @param section Section to return
	 * @param detectColumn Method to detect column
	 * @param detectSection Method to detect section
	 * @return
	 */
	public static <T,C,S> Map<C, List<T>> sortToColumns(List<T> data, S section, Function<T,C> detectColumn, Function<T,S> detectSection) {
		List<T> allSorted = new ArrayList<>(data);
		// Sort by sections
		Collections.sort(allSorted, new Comparator<T>() {
			@SuppressWarnings("unchecked")
			public int compare(T o1, T o2) {
				S section1 = detectSection.apply(o1);
				S section2 = detectSection.apply(o2);
				if (section1==null && section2==null) return 0;
				if (section1==null && section2!=null) return +1;
				if (section1!=null && section2==null) return -1;
				if (section instanceof Comparable) {
					return ((Comparable<S>)section1).compareTo(section2);
				} else
					return String.valueOf(section1).compareTo(String.valueOf(section2));
			}
		});
		
		Map<C, List<T>> ret = new HashMap<>();
		for (T item : data) {
			// Ignore data from unwanted section
			if (section!=detectSection.apply(item))
				continue;
			// Sort to matching column
			C column = detectColumn.apply(item);
			List<T> list = ret.get(column);
			if (list==null) {
				list = new ArrayList<>();
				ret.put(column, list);
			}
			list.add(item);
		}
		
		return ret;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @param <T>
	 * @param <C>
	 * @param data
	 * @param columns
	 * @param minRows
	 * @param detectCategory
	 * @param categoryCompare
	 * @return
	 */
	public static <T,C> List<Object>[] getAsBalancedCategoryTable(List<T> data, int columns, int minRows, Function<T, C> detectCategory, Comparator<C> categoryCompare) {
		Map<C, List<Object>> listsByCategory = new HashMap<>();
		// Sort all data into categorized lists
		for (T item : data) {
			C category = detectCategory.apply(item);
			List<Object> list = listsByCategory.get(category);
			if (list==null) {
				list = new ArrayList<>();
				list.add(category); // Add header to list
				listsByCategory.put(category, list);
			}
			list.add(item);
		}
		
		// Make a first guess for required rows
		int totalItems = data.size()+listsByCategory.size();
		int rowsFirstAssumption = totalItems/columns;
		if ((totalItems%columns)>0)
			rowsFirstAssumption++;
		int guessedRows = Math.max(minRows, rowsFirstAssumption);
		
		List<C> categories = new ArrayList<>(listsByCategory.keySet());
		Collections.sort(categories, categoryCompare);
		@SuppressWarnings("unchecked")
		Class<C> categoryClass = (Class<C>) categories.get(0).getClass();

		// Prepare result
		@SuppressWarnings("unchecked")
		List<Object>[] resultLists = new ArrayList[columns];
		for (int i=0; i<columns; i++)
			resultLists[i] = new ArrayList<>();

		/*
		 * Try several iterations
		 */
		int rows = guessedRows;
		outer:
		while ( (rows-guessedRows)<4 ) {
			int maxItems = rows*columns;
			// Build a combined list of all categories
			List<Object> all = new ArrayList<Object>();
			categories.forEach(cat -> all.addAll(listsByCategory.get(cat)));
			
			/*
			 * Ensure that there is no category at a column end.
			 * If so, fill an empty line there
			 */
			for (int col=0; col<columns; col++) {
				int colEnd = ((col+1)*rows)-1;
				Object item = (colEnd<all.size())?all.get(colEnd):null;
				if (item!=null && categoryClass.isInstance(item)) {
					// Last element in column was a category header
					// Inject an empty line here
					all.add(colEnd, null);
				}
			}
			// If after all eventually injects the number of items does
			// not exceed maximum, we are okay
			if (all.size()<=maxItems) {
				// Fill into result lists
				for (int i=0; i<columns; i++) {
					int to = Math.min( ((i+1)*rows), all.size());
					if (i*rows <= to)
						resultLists[i].addAll(all.subList(i*rows, to));
				}
				break outer;
			}
			// Otherwise try with a row more
			rows++;
		}
		
		return resultLists;
	}
	
	//--------------------------------------------------------------------
	/**
	 * @param <T>
	 * @param <C>
	 * @param data
	 * @param columns
	 * @param minRows
	 * @param detectCategory
	 * @param categoryCompare
	 * @return
	 */
	public static <T,C> List<Object> getAsBalancedCategoryList(List<T> data, int columns, int minRows, Function<T, C> detectCategory, Comparator<C> categoryCompare) {
		List<Object>[] raw = getAsBalancedCategoryTable(data, columns, minRows, detectCategory, categoryCompare);
		List<Object> ret = new ArrayList<>();
		while (true) {
			for (int i=0; i<columns; i++) {
				if (raw[i].isEmpty()) {
					if (i==0)
						return ret;
					else
						ret.add(null);
				} else {
					ret.add(raw[i].remove(0));
				}
			}
		}
	}
	//-------------------------------------------------------------------
	public static List<SR6SkillValue> getAllSkillValues(Shadowrun6Character model, SkillType... types) {
		List<SkillType> filter = Arrays.asList(types);
		if (filter.isEmpty())
			filter = Arrays.asList(SkillType.regularValues());

		List<SR6SkillValue> ret = new ArrayList<>();
		for (SR6Skill skill : Shadowrun6Core.getSkills()) {
			if (!filter.contains(skill.getType()))
				continue;
			switch (skill.getType()) {
			case COMBAT:
			case MAGIC:
			case PHYSICAL:
			case RESONANCE:
			case SOCIAL:
			case TECHNICAL:
			case VEHICLE:
				SR6SkillValue val = model.getSkillValue(skill);
				if (val==null) {
					if (skill.isUseUntrained()) {
						val = new SR6SkillValue(skill, 0);
					} else
						val = new SR6SkillValue(skill, -1);
				}
				ret.add(val);
				break;
			case LANGUAGE:
			case KNOWLEDGE:
				break;
			case ACTION:
			case NOT_SET:
				break;
			}
		}

		for (SR6SkillValue val : model.getSkillValues()) {
			SkillType tmpType = val.getModifyable().getType();
			if (!filter.contains(tmpType))
				continue;
			if (Arrays.asList(SkillType.individualValues()).contains(tmpType)) {
				ret.add(val);
			}
		}

		return ret;
	}

	//-------------------------------------------------------------------
	public static List<PoolCalculation<Integer>> getWeaponPoolCalculation(Shadowrun6Character model, CarriedItem item) {
//		if (item.getResolved().getWeaponData()==null) {
//			throw new IllegalArgumentException(item.getName()+" is not a weapon but a "+item.getItem().getTypes()+" and of type "+item.getItem().getClass());
//		}
		
		SR6Skill skill = (SR6Skill) item.getAsObject(SR6ItemAttribute.SKILL).getValue();
		SR6SkillValue sVal = model.getSkillValue(skill);
		
		List<PoolCalculation<Integer>> ret = new ArrayList<>();
		String special = null;
		logger.log(Level.WARNING, "ToDo: getWeaponPoolCalculation");
		
//		// Find the correct specialization
//		if (skill.getId().equals("exotic_weapons")) {
//			// Without skill, you cannot use the weapon
//			if (sVal==null) {
//				ret.add(new PoolCalculation(0, RES.format("explain.missing_exotic_skill", item.getResolved().getName())));
//				return ret;
//			}
//			// Find matching specialization
//			SkillSpecializationValue spec = null;
//			for (SkillSpecializationValue tmp : sVal.getSkillSpecializations()) {
//				if (tmp.getSpecial().getExoticItem()==item.getResolved() || tmp.getSpecial()==item.getResolved().getWeaponData().getSpecialization()) {
//					spec = tmp;
//					break;
//				}
//			}
//			// Without specialization, the weapon cannot be used
//			if (spec==null) {
//				ret.add(new PoolCalculation(0, RES.format( "explain.missing_exotic_specialization", item.getResolved().getName())+": "+sVal));				
//				return ret;
//			}
//			// Specialization found
//			special = spec.getSpecial().getId();
//		} else {		
//			SkillSpecialization required = item.getItem().getWeaponData().getSpecialization();
//			if (required!=null) {
//				special = required.getId();
//			}
//		}
		logger.log(Level.ERROR, "getWeaponPoolCalculation not finished yet");

		
		ret.addAll( getSkillPoolCalculation(model, skill, skill.getAttribute(), special) );		
		
		/*
		 * Add eventually existing focus
		 */
//		if (item.getUsedFocus()!=null) {
//			FocusValue focus = item.getUsedFocus();
//			if (focus.getModifyable().getChoice()==ChoiceType.MELEE_WEAPON) {
//				ret.add( new PoolCalculation(focus.getLevel(), focus.getName()));
//			}
//		}
//		
//		/*
//		 * Add eventually existing item attunement
//		 */
//		if (item.getItemAttunement()!=null) {
//			MetamagicOrEchoValue meta = item.getItemAttunement();
////			if (focus.getChoice()==item) {
//				ret.add( new PoolCalculation(model.getInitiateSubmersionLevel(), meta.getName()));
////			}
//		}
		
		return ret;
	}

	//--------------------------------------------------------------------
	public static int getWeaponPool(Shadowrun6Character model, CarriedItem item) {
		return (int)getWeaponPoolCalculation(model, item).stream().collect(Collectors.summarizingInt(pc -> pc.value)).getSum();
	}

	//--------------------------------------------------------------------
	public static String getWeaponPoolExplanation(Shadowrun6Character model, CarriedItem item) {
		return String.join("\n",getWeaponPoolCalculation(model, item).stream().map(pool -> pool.value+" "+pool.source+(pool.hitLimit?"*":" ") ).collect(Collectors.toList()));
	}

	//-------------------------------------------------------------------
	/*
	 * Called from Shadowrun6_Print
	 */
	public static Damage getWeaponDamage(ShadowrunCharacter model, CarriedItem item) {
//		if (item.getResolved().getWeaponData()==null) {
//			throw new IllegalArgumentException(item.getName()+" is not a weapon but a "+item.getItem().getTypes()+" and of type "+item.getItem().getClass());
//		}

//		return ((Damage)model.getItem("unarmed").getAsValue(ItemAttribute.DAMAGE));
		
		Damage damage = (Damage)item.getAsObject(SR6ItemAttribute.DAMAGE).getModifiedValue();
//		if (damage.isAddStrength()) {
//			AttributeValue val = model.getAttribute(Attribute.STRENGTH);
//			int strHalf = Math.round( val.getModifiedValue() / 2.0f);
//			Damage damage2 = new Damage();
//			damage2.setValue(damage.getValue() + strHalf);
//			damage2.setType(damage.getType());
//			damage2.setModifications(damage.getModifications());
//			return damage2;
//		}
		return damage;
	}

	//-------------------------------------------------------------------
	public static String getItemAttributeString(ShadowrunCharacter model, CarriedItem item, SR6ItemAttribute attr) {
		switch (attr) {
		case FIREMODES:
			return String.valueOf(item.getAsObject(attr));
		case SKILL:
			return ((SR6Skill)item.getAsObject(attr).getValue()).getName();
		case PRICE:
			return String.valueOf(item.getAsObject(attr));
		case AMMUNITION:
			return String.valueOf(item.getAsObject(attr).getValue());
		default:
			ItemAttributeNumericalValue val = item.getAsValue(attr);
			if (val.getModifier()==0)
				return String.valueOf(val.getDistributed());
			else
				return val.getDistributed()+" ("+val.getModifiedValue()+")";
		}
	}
	
	//--------------------------------------------------------------------
	/*
	 * Find the best commlink or cyber jack
	 */
	public static CarriedItem getBestMatrixDF(Shadowrun6Character model) {
		CarriedItem bestDF = null;
		int bestSum = 0;
		for (CarriedItem item : model.getCarriedItems(ItemType.ELECTRONICS)) {
			if (!item.hasAttribute(SR6ItemAttribute.DATA_PROCESSING))
				continue;
			logger.log(Level.INFO,"  consider for DF: "+item);
			int d = item.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue();
			int f = item.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue();
			int sum = d+f;
			if (sum>bestSum) {
				bestDF = item;
				bestSum= sum;
			}
		}
		return bestDF;
	}
	
	//--------------------------------------------------------------------
	/**
	 * Determine the most powerful RCC available
	 */
	public static CarriedItem getBestRCC(Shadowrun6Character model) {
		CarriedItem ret = null;
		for (CarriedItem item : model.getCarriedItems(ItemType.ELECTRONICS)) {
			// Only evaluate RIGGER_CONSOLEs
//			if (!item.getItem().isSubtype(ItemSubType.RIGGER_CONSOLE, ItemType.ELECTRONICS))
//				continue;
//			if (ret==null || ret.getAsValue(ItemAttribute.DEVICE_RATING).getModifiedValue()<item.getAsValue(ItemAttribute.DEVICE_RATING).getModifiedValue())
//				ret = item;
		}
		logger.log(Level.WARNING, "ToDo: getBestRCC");
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Get a list of all ammunitions from the inventory, that can be used
	 * with a given weapon.
	 * 
	 * @param model  Character
	 * @param weapon Weapon
	 * @return
	 */
	public static List<CarriedItem<ItemTemplate>> getAmmunitionsFor(Shadowrun6Character model, CarriedItem<ItemTemplate> weapon) {
		List<CarriedItem<ItemTemplate>> ret = new ArrayList<>();
		boolean caseless = weapon.hasFlag(SR6ItemFlag.USES_CASELESS);
		for (CarriedItem<ItemTemplate> ammo : model.getCarriedItems(ItemUtil.AMMUNITION_FILTER)) {
			boolean ammoIsCaseless = ammo.getVariantID()!=null && ammo.getVariantID().equals("caseless");
			// Determine if ammunition is allowed for this weapon
			for (Requirement req : ammo.getRequirements()) {
				boolean isMet = ItemUtil.isRequirementMet(weapon, ammo.getResolved(), req);
				logger.log(Level.INFO, "Ammo "+ammo+" mets requirement = "+isMet);
				if (isMet) {
					// Check cased vs. caseless
					if (caseless && ammoIsCaseless)
						ret.add(ammo);
					else if (!caseless && !ammoIsCaseless)
						ret.add(ammo);
				}
			}
		}
		
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * 
	 * @param model
	 * @param weapon
	 * @return
	 */
	public static TreeMap<AmmunitionType, Map<SR6ItemAttribute, ItemAttributeValue<SR6ItemAttribute>>> getAmmunitionTypes(Shadowrun6Character model, CarriedItem<ItemTemplate> weapon) {
		TreeMap<AmmunitionType, Map<SR6ItemAttribute, ItemAttributeValue<SR6ItemAttribute>>> ret = new TreeMap<>();
		for (CarriedItem<ItemTemplate> ammo : model.getCarriedItems(ItemUtil.AMMUNITION_FILTER)) {
			// Determine if ammunition is allowed for this weapon
		}
		
		return ret;
	}
	
	//-------------------------------------------------------------------
	public static ItemType getItemType(CarriedItem<ItemTemplate> model) {
		return model.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue();
	}
	
	//-------------------------------------------------------------------
	public static ItemSubType getItemSubType(CarriedItem<ItemTemplate> model) {
		return model.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue();
	}
	
	//---------------------------------------------------------
	public static String getAccessoryString(CarriedItem<ItemTemplate> item) {
		class Counted {
			CarriedItem inst;
			int count;
			public Counted(CarriedItem item) {
				inst = item;
				count=1;
			}
			public String toString() {
				if (count==1) return inst.getNameWithoutRating();
				return inst.getNameWithoutRating()+" ("+count+"x)";
			}
		}
		Map<ItemTemplate, Counted> map = new LinkedHashMap<>();
		List<String> list = new ArrayList<>();
		item.getEffectiveAccessories().forEach( ci -> {
			ItemSubType sub = ItemSubType.ACCESSORY;
			if (ci!=null &&  getItemSubType(ci)!=null)
				sub = getItemSubType(ci);
			else {
				if (getItemType(ci)!=null && getItemSubType(ci)!=null) {
					sub = getItemSubType(ci);
				} else
					logger.log(Level.WARNING, "No subtype set for "+ci+" / "+getItemType(ci)+" / "+getItemSubType(ci));
			}
//			switch (sub) {
//			case HACKING_PROGRAM:
//			case BASIC_PROGRAM:
//			case RIGGER_PROGRAM:
//			case AUTOSOFT:
//			case SKILLSOFT:
//				break;
//			default:
//				// Don't print hardpoints
//				if (ci.getItem().getId().startsWith("hardpoint"))
//					return;
//				if (ci.getItem().getId().startsWith("modslot_"))
//					return;
//				if (ci.getItem().getId().startsWith("improved_"))
//					return;
//				if (ci.getItem().getId().startsWith("enhanced_"))
//					return;
//				if (ci.getItem().getId().startsWith("weapon_mount"))
//					return;
				// Sum up
				if (map.containsKey(ci.getResolved())) {
					map.get(ci.getResolved()).count++;
				} else {
					map.put(ci.getResolved(), new Counted(ci));
				}
//			}
		});
		map.values().forEach(c-> list.add(c.toString()));
		
		String mods = String.join(", ", list);
		return mods;
	}

//	//---------------------------------------------------------
//	public static String getEnhancementString(CarriedItem item) {
//		List<String> list = new ArrayList<>();
//		item.getEnhancements().forEach( mod -> {
//			ItemEnhancement enh = mod.getModifyable();
//			list.add(enh.getName());
//		});
//		
//		String mods = String.join(", ", list);
//		return mods;
//	}
	
	// -------------------------------------------------------------------
	public static List<DataItem> getInfluences(ComplexDataItemValue<?> val) {
		List<DataItem> ret = new ArrayList<>();
		for (Modification mod : val.getModifications()) {
			if (mod.isConditional()) {
				if (mod.getSource() == null) {
					System.err.println("Shadowrun6Tools.getInfluences: No source for Modification " + mod);
				} else if (!(mod.getSource() instanceof DataItem)) {
					System.err.println("Shadowrun6Tools.getInfluences: Source of SkillModification " + mod
							+ " is of type " + mod.getSource().getClass());
				} else {
					ret.add((DataItem) mod.getSource());
				}
			}
		}
		return ret;
	}

	//---------------------------------------------------------
	public static CarriedItem<ItemTemplate> getPrimaryArmor(Shadowrun6Character model) {
		CarriedItem<ItemTemplate> bestArmor = null;
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
			if (!item.hasAttribute(SR6ItemAttribute.DEFENSE_PHYSICAL))
				continue;
			item.setPrimary(false);
			item.setAutoFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS, true);
			// If no previous selection or armor is better, use it
			if (bestArmor==null || item.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue()> bestArmor.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue() )
				bestArmor = item;
			// Gear pieces that add armor are also allowed
//			if (item.getItem().getArmorData()!=null && item.getItem().getArmorData().addsToMain())
//				item.setIgnoredForCalculations(false);
			logger.log(Level.DEBUG,"*  "+item.getNameWithRating()+" \t"+item.getAsValue(SR6ItemAttribute.DEFENSE_PHYSICAL).getModifiedValue()+": ignored="+item.hasAutoFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS));
		}
		if (bestArmor!=null) {
			bestArmor.setAutoFlag(SR6ItemFlag.IGNORE_FOR_CALCULATIONS, false);
			bestArmor.setPrimary(true);
			return bestArmor;
		}
		return null;
	}

	//---------------------------------------------------------
	public static CarriedItem<ItemTemplate> getPrimaryRangedWeapon(Shadowrun6Character model) {
		List<CarriedItem<ItemTemplate>> weapons = model.getCarriedItems(ItemType.WEAPON_RANGED, ItemType.WEAPON_FIREARMS, ItemType.WEAPON_FIREARMS);
		for (CarriedItem item : weapons) {
			if (item.isPrimary()) return item;
		}
		return weapons.isEmpty()?null:weapons.get(0);
	}

	//---------------------------------------------------------
	public static CarriedItem<ItemTemplate> getPrimaryMeleeWeapon(Shadowrun6Character model) {
		List<CarriedItem<ItemTemplate>> weapons = model.getCarriedItems(ItemType.WEAPON_CLOSE_COMBAT);
		for (CarriedItem item : weapons) {
			if (item.isPrimary()) return item;
		}
		return weapons.isEmpty()?null:weapons.get(0);
	}

}
