package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.function.BiFunction;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.pane.DecisionDialog.DecisionDialogConfig;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * Checks whether an item with decisions can be selected or embedded.
 *
 * @author prelle
 */
public class SR6EquipmentVerifier implements BiFunction<DecisionDialogConfig<ItemTemplate>, Decision[], Possible> {

	private ISR6EquipmentController ctrl;
	private CarriedItem<ItemTemplate> container;
	private ItemHook hook;

	// ---
	public SR6EquipmentVerifier(ISR6EquipmentController ctrl) {
		this.ctrl = ctrl;
	}

	// ---
	public SR6EquipmentVerifier(ISR6EquipmentController ctrl, CarriedItem<ItemTemplate> container, ItemHook hook) {
		this.ctrl = ctrl;
		this.container = container;
		this.hook = hook;
	}

	// ---
	@Override
	public Possible apply(DecisionDialogConfig<ItemTemplate> item, Decision[] allDecisions) {
		Decision[] decisions = SR6SelectAndDecide.withoutVariant(allDecisions);
		if (container != null) {
			return ctrl.canBeEmbedded(container, hook, item.getItem(), item.getVariant(), decisions);
		} else if (item.getCarryMode() != null) {
			return ctrl.canBeSelected(item.getItem(), item.getVariant(), item.getCarryMode(), decisions);
		} else {
			return ctrl.canBeSelected(item.getItem(), decisions);
		}
	}

}
