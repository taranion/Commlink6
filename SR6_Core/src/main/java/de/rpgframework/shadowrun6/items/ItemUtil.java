package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.Usage;
import de.rpgframework.genericrpg.requirements.AnyRequirement;
import de.rpgframework.genericrpg.requirements.ExistenceRequirement;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
public class ItemUtil {
	
	private final static Logger logger = System.getLogger(ItemUtil.class.getPackageName());

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
		if (tmp.getApply()!=ApplyTo.DATA_ITEM) return true;
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
			default:
				System.err.println("ItemUtil: TODO: check existence of "+req.getType());
				logger.log(Level.ERROR, "TODO: check existence of "+req.getType());
			}
			return false;
		}
		logger.log(Level.INFO, "TODO: check "+tmp);
		return false;
	}

}
