package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.items.ItemAttributeObjectValue;
import de.rpgframework.genericrpg.items.ItemAttributeValue;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.items.formula.FormulaTool;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.AnyRequirement;
import de.rpgframework.genericrpg.requirements.ExistenceRequirement;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
public class ItemUtil {

	private final static Logger logger = System.getLogger(ItemUtil.class.getPackageName());

	public static Predicate<CarriedItem<ItemTemplate>> AMMUNITION_FILTER = new CarriedItemItemTypeFilter(CarryMode.CARRIED, ItemType.AMMUNITION);
	public static Predicate<CarriedItem<ItemTemplate>> MATRIXDEVICES_FILTER = item ->
			item.hasFlag(SR6ItemFlag.MATRIX_DEVICE)
			||
			(List.of( ItemSubType.matrixDevices()).contains(item.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue())
				||
				item.getResolved()==ItemUtil.SOFTWARE_LIBRARY_ITEM
				);

	public static ItemTemplate SOFTWARE_LIBRARY_ITEM = new ItemTemplate();
	public static CarriedItem<ItemTemplate> SOFTWARE_LIBRARY;


	static {
		ItemAttributeDefinition def = new ItemAttributeDefinition(SR6ItemAttribute.SOFTWARE_TYPES);
		String tmp = Arrays.toString(SoftwareTypes.values());
		def.setRawValue(tmp.substring(1, tmp.length()-1));
		SOFTWARE_LIBRARY_ITEM.setAttribute(def);
		SOFTWARE_LIBRARY_ITEM.setId("software_library");
		SOFTWARE_LIBRARY_ITEM.addModifications(List.of(new ValueModification(ShadowrunReference.HOOK, ItemHook.SOFTWARE.name(), "99")));

		SOFTWARE_LIBRARY =  new CarriedItem<ItemTemplate>(SOFTWARE_LIBRARY_ITEM, null, CarryMode.VIRTUAL);
		SOFTWARE_LIBRARY.addSlot(new AvailableSlot(ItemHook.SOFTWARE, 99));
	}

	//-------------------------------------------------------------------
	public static void addOrSetItemAttribute(CarriedItem<ItemTemplate> ref, ValueModification mod) {
		if (mod.getReferenceType()!=ShadowrunReference.ITEM_ATTRIBUTE)
			throw new IllegalArgumentException("Only for ITEM_ATTRIBUTE modifications, but got "+mod.getReferenceType());
		SR6ItemAttribute attr = mod.getResolvedKey();
		ItemAttributeValue<?> val = ref.getAttributeRaw(attr);
		if (val!=null) {
			logger.log(Level.DEBUG, "Add modification to existing attribute: {0}",mod);
			val.addModification(mod);
			return;
		}

		ItemAttributeObjectValue<SR6ItemAttribute> objVal;
		switch (attr) {
		case SOFTWARE_TYPES:
			objVal = new ItemAttributeObjectValue<>(attr, List.of(mod.getValueAsKeys()));
			ref.setAttribute(attr, objVal);
			return;
		case ATTACK_RATING:
			try {
				objVal = new ItemAttributeObjectValue<>(attr, SR6ItemAttribute.ATTACK_RATING.getConverter().read(mod.getRawValue()));
				ref.setAttribute(attr, objVal);
			} catch (Exception e) {
				logger.log(Level.ERROR, "Error converting {0} to ATTACK_RATING int[] (from {1})", mod.getRawValue(), mod);
				e.printStackTrace();
			}
			return;
		default:
			logger.log(Level.ERROR, "Unsuported addOrSetItemAttribute {0} with {1}  val={2}", attr, mod.getRawValue(), val);
		}
	}

	//-------------------------------------------------------------------
	public static List<ItemTemplate> getEmbeddableIn(CarriedItem<ItemTemplate> ref, ItemHook slot) {
		logger.log(Level.INFO, "getEmbeddableIn({0}, {1})", ref, slot);
		if (ref==null) throw new NullPointerException("ref");
		List<ItemTemplate> ret = new ArrayList<>();
		ret = Shadowrun6Core.getItemList(ItemTemplate.class)
			.stream()
			// Filter those EMBEDDED into the slot in any way
			.filter(t -> ItemUtil.hasHookRequirement(t, slot))
//			.map( t -> {
//				if ()
//				return t;})
			.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
			.filter(t -> ItemUtil.areRequirementsMet( ref, t, (SR6PieceOfGearVariant) ref.getVariant())==null)
			.collect(Collectors.toList());

//		for (ItemTemplate t : ret) {
//			logger.log(Level.INFO, "Embeddable "+t);
//		}
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
	/**
	 * @return Requirement not met
	 */
	public static Requirement areRequirementsMet(CarriedItem<ItemTemplate> container, ItemTemplate item, SR6PieceOfGearVariant variant) {
		for (Requirement tmp : item.getRequirements()) {
			if (!isRequirementMet(container, item, tmp))
				return tmp;
		}
		if (variant!=null) {
			for (Requirement tmp : variant.getRequirements()) {
				if (!isRequirementMet(container, item, tmp))
					return tmp;
			}
		}

		return null;
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
		ShadowrunReference type = (ShadowrunReference) tmp.getType();
		String key = tmp.getKey();
		if (tmp instanceof ExistenceRequirement) {
			ExistenceRequirement req = (ExistenceRequirement)tmp;
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
		if (tmp instanceof ValueRequirement) {
			ValueRequirement req = (ValueRequirement)tmp;
			switch (type) {
			case ITEM_ATTRIBUTE:
				SR6ItemAttribute iAttr = SR6ItemAttribute.valueOf(req.getKey());
				if (!container.hasAttribute(iAttr))
					return false;
				ItemAttributeValue<?> rawAttr = container.getAttributeRaw(iAttr);
				if (rawAttr==null)
					return false;
				switch (iAttr) {
				case SOFTWARE_TYPES:
					Object x = ((ItemAttributeObjectValue)rawAttr).getValue();
					if (x instanceof List)
						return ((List<SoftwareTypes>)x).contains( SoftwareTypes.valueOf( req.getRawValue() ));
					else
						return ((SoftwareTypes)x)==SoftwareTypes.valueOf( req.getRawValue() );
				default:
					logger.log(Level.ERROR, "TODO: support requirement check for item attribut {0}", iAttr);
				}
				return false;
			}
		}
		logger.log(Level.ERROR, "TODO: check "+tmp+"/"+tmp.getClass());
		System.err.println("TODO: support requirement check for "+tmp+"/"+tmp.getClass());
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
