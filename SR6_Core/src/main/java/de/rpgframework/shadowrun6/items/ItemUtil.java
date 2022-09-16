package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.AnyRequirement;
import de.rpgframework.genericrpg.requirements.ExistenceRequirement;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.VehicleData.VehicleType;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
public class ItemUtil {
	
	private final static Logger logger = System.getLogger(ItemUtil.class.getPackageName());

	public static Predicate<CarriedItem<ItemTemplate>> AMMUNITION_FILTER = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.AMMUNITION); 
	
	//-------------------------------------------------------------------
	public static List<ItemTemplate> getEmbeddableIn(CarriedItem ref, ItemHook slot) {
		List<ItemTemplate> ret = new ArrayList<>();
		ret = Shadowrun6Core.getItemList(ItemTemplate.class)
			.stream()
			// Filter those EMBEDDED into the slot in any way
			.filter(t -> ItemUtil.hasHookRequirement(t, slot))
//			.map( t -> {
//				if ()
//				return t;})
			.filter(t -> ItemUtil.areRequirementsMet( ref, t))
			.collect(Collectors.toList());
		return ret;
	}
	
	//-------------------------------------------------------------------
	public static boolean hasHookRequirement(ItemTemplate item, ItemHook hook) {
		// Test main item
		for (Usage usage : item.getUsages()) {
			if (usage.getMode()==CarryMode.EMBEDDED && usage.getSlot()==hook) {
				return true;
			}
		}
		// Check variants
		for (SR6PieceOfGearVariant var : item.getVariants()) {
			for (Usage usage : var.getUsages()) {
				if (usage.getMode()==CarryMode.EMBEDDED && usage.getSlot()==hook) {
					return true;
				}
			}
		}
		return false;
	}
	
	//-------------------------------------------------------------------
	public static boolean areRequirementsMet(CarriedItem<ItemTemplate> container, ItemTemplate item) {
		for (Requirement tmp : item.getRequirements()) {
//			switch (tmp.getApply()) {
//			case 
//			}
			if (!isRequirementMet(container, item, tmp))
				return false;
		}
		return true;
	}
	
	//-------------------------------------------------------------------
	public static boolean isRequirementMet(CarriedItem<ItemTemplate> container, ItemTemplate item, Requirement tmp) {
		if (tmp.getApply()!=ApplyTo.DATA_ITEM && !(tmp instanceof AnyRequirement)) return true;
		if (tmp instanceof AnyRequirement) {
			AnyRequirement req = (AnyRequirement)tmp;
			for (Requirement part : req.getOptionList()) {
				if (isRequirementMet(container, item, part)) return true;
			}
			return false;
		}
		if (tmp instanceof ExistenceRequirement) {
			ExistenceRequirement req = (ExistenceRequirement)tmp;
			ShadowrunReference type = (ShadowrunReference) req.getType();
			String key = req.getKey();
			switch (type) {
			case GEAR:
				return container.getModifyable().getId().equals(key);
			case ITEMTYPE:
				return container.getAsObject(SR6ItemAttribute.ITEMTYPE).getValue()==type.resolve(key);
			case ITEMSUBTYPE:
				return container.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getValue()==type.resolve(key);
			default:
				System.err.println("ItemUtil: TODO: check existence of "+req.getType());
				logger.log(Level.ERROR, "TODO: check existence of "+req.getType());
			}
			return false;
		}
		logger.log(Level.INFO, "TODO: check "+tmp);
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * Calculate all effective attributes of an item, depending on the picked
	 * variant and carry mode.
	 * @param item
	 * @param variant
	 * @param carry
	 * @return
	 */
	public static ItemTemplate calculateVirtualItem(ItemTemplate item, SR6PieceOfGearVariant variant, CarryMode carry) {
		if (item.requiresVariant() && variant==null) throw new NullPointerException("Variant must be chosen");
		
		ItemTemplate virtual = (variant!=null)?(new VariantItemTemplate(variant)):(new ItemTemplate());
		virtual.setId(item.getId());
		virtual.assignToDataSet(item.getAssignedDataSets().iterator().next());
		for (ItemAttributeDefinition attr : item.getAttributes()) {
			virtual.setAttribute(attr);
		}
		virtual.addModifications( item.getModifications() );
		virtual.addFlags(item.getFlags());
		
		Usage usage = item.getUsage(carry);
		Usage variantUsage = (variant!=null)?variant.getUsage(carry):null;
		if (variantUsage!=null)
			virtual.addUsage(variantUsage);
		else if (usage!=null) 
			virtual.addUsage(usage);
		else
			throw new IllegalArgumentException("No usage "+carry+" for "+item.getId()+"/"+variant);
		
		Usage actual = virtual.getUsage(carry);		
		if (carry==CarryMode.IMPLANTED) {
			if (actual.getFormula()!=null)
				virtual.setAttribute(SR6ItemAttribute.ESSENCECOST, actual.getFormula());
		}
		
		// Copy variant attributes
		if (variant!=null) {
			virtual.setParentItem(item);
			virtual.setId(variant.getId());
			for (ItemAttributeDefinition attr : variant.getAttributes()) {
				virtual.setAttribute(attr);
			}
			virtual.addModifications( variant.getModifications() );
			virtual.addFlags(variant.getFlags());
		}
		
		// If only one hook is present, use it as CAPACITY
		long hooks = virtual.getModifications().stream()
			.filter(m -> m instanceof ValueModification)
			.filter(m -> ((ValueModification)m).getReferenceType()==ShadowrunReference.HOOK)
			.count();
		if (hooks==1) {
			ValueModification vMod = (ValueModification) virtual.getModifications().stream()
					.filter(m -> m instanceof ValueModification)
					.filter(m -> ((ValueModification)m).getReferenceType()==ShadowrunReference.HOOK)
					.findFirst().get();
			ItemAttributeDefinition attr = new ItemAttributeDefinition(SR6ItemAttribute.CAPACITY, FormulaTool.tokenize(vMod.getRawValue()));
			virtual.setAttribute(attr);
		}
		
		return virtual;
	}
	
	//-------------------------------------------------------------------
	/**	 * 
	 * @param item
	 * @return Value >0, if a decision has been made
	 */
	public static int getRating(CarriedItem<ItemTemplate> item) {
		Decision dec = item.getDecision(ItemTemplate.UUID_RATING); 
		if (dec!=null) {
			return Integer.parseInt( dec.getValue() );
		}
		return 0;
	}
	
	//-------------------------------------------------------------------
	public static AugmentationQuality getBodytechQuality(CarriedItem<ItemTemplate> item) {
		Decision dec = item.getDecision(ItemTemplate.UUID_AUGMENTATION_QUALITY); 
		if (dec!=null) {
			return AugmentationQuality.valueOf( dec.getValue() );
		}
		return null;
	}

}
