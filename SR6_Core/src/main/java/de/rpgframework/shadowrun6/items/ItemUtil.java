package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
public class ItemUtil {

	//-------------------------------------------------------------------
	public static List<ItemTemplate> getEmbeddableIn(CarriedItem ref, ItemHook slot) {
		List<ItemTemplate> ret = new ArrayList<>();
//		for (ItemTemplate tmp : Shadowrun6Core.getItemList(ItemTemplate.class)) {
//			
//		}
		ret = Shadowrun6Core.getItemList(ItemTemplate.class)
			.stream()
			// Only those that can be embedded
//			.filter(t -> t.getEquipModes().stream().anyMatch(eq -> eq.getEquipMode()==SR6EquipMode.EMBEDDED))
			// and have a matching hook requirement
			.filter(t -> ItemUtil.hasHookRequirement(t, slot))
			.collect(Collectors.toList());
		return List.of();
	}
	
	//-------------------------------------------------------------------
	public static boolean hasHookRequirement(ItemTemplate item, ItemHook hook) {
		for (Requirement tmp : item.getRequirements()) {
			
			if (tmp.getType()==ShadowrunReference.HOOK && tmp.getType().resolve(tmp.getKey())==hook) {
				return true;
			}
		}
		return false;
	}

}
