package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.Locale;

import de.rpgframework.genericrpg.chargen.ChoiceCallback;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.pane.DecisionDialog;
import de.rpgframework.jfx.pane.DecisionDialog.DecisionDialogConfig;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.CarriedItemDescriptionPane;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;

/**
 * Utility methods for JavaFX dialogs and decision handling in SR6.
 *
 * @author prelle
 */
public class SR6JFXTools {

	// ---
	/**
	 * A DecisionDialog with the SR6 storage for any controller
	 */
	public static <T extends ComplexDataItem, V extends ComplexDataItemValue<T>> DecisionDialog<T, V> createDecisionDialog(
			ComplexDataItemController<T, V> ctrl) {
		return DecisionDialog.<T, V>builder()
				.setController(ctrl)
				.setCustomizer(new SR6DecisionDialogStorage())
				.setModificationResolver(m -> Shadowrun6Tools.getModificationString(m, Locale.getDefault()))
				.build();
	}

	// ---
	/**
	 * Configuration for the DecisionDialog - the character is taken from the controller
	 */
	public static <T extends ComplexDataItem> DecisionDialogConfig<T> createDecisionConfig(
			ComplexDataItemController<T, ?> ctrl, T item) {
		return DecisionDialogConfig.<T>builder()
				.item(item)
				.model((Shadowrun6Character) ctrl.getModel())
				.build();
	}

	// ---
	/**
	 * Let the user make the decisions for an item in a DecisionDialog
	 *
	 * @return Decisions, or NULL if cancelled
	 */
	public static <T extends ComplexDataItem, V extends ComplexDataItemValue<T>> Decision[] askDecisions(
			ComplexDataItemController<T, V> ctrl, T item) {
		DecisionDialogConfig<T> config = createDecisionConfig(ctrl, item);
		config.setChoices(ctrl.getChoicesToDecide(item));
		return createDecisionDialog(ctrl).apply(config);
	}

	// ---
	/**
	 * A DecisionDialog as option callback, e.g. for selection skins of wizard pages
	 */
	public static <T extends ComplexDataItem, V extends ComplexDataItemValue<T>> ChoiceCallback<T> decisionCallback(
			ComplexDataItemController<T, V> ctrl) {
		return createDecisionDialog(ctrl).asChoiceCallback(item -> createDecisionConfig(ctrl, item));
	}

	// ---
	/**
	 * Preset decision dialog for gear with interactive preview
	 */
	public static DecisionDialog<ItemTemplate, CarriedItem<ItemTemplate>> createItemChoiceDialog(
			SR6CharacterController control,
			ISR6EquipmentController eqCtrl,
			CarriedItem<ItemTemplate> container,
			ItemHook hook,
			CarryMode carry,
			String variantID) {
		SR6EquipmentVerifier verifier = (container != null)
				? new SR6EquipmentVerifier(eqCtrl, container, hook)
				: new SR6EquipmentVerifier(eqCtrl);
		CarriedItemDescriptionPane descPane = new CarriedItemDescriptionPane(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				control);
		SR6DecisionDialogStorage storage = new SR6DecisionDialogStorage();
		if (container != null) {
			storage.setContext(container);
		}
		return DecisionDialog.<ItemTemplate, CarriedItem<ItemTemplate>>builder()
				.setCustomizer(storage)
				.setSelectionVerifier(verifier)
				.setController(eqCtrl)
				.setDescriptionNode(descPane)
				.setOnUpdate((dia, decisions) -> {
					ItemTemplate tmpl = dia.getConfig().getItem();
					Shadowrun6Character model = (Shadowrun6Character) dia.getConfig().getModel();
					String varID = (dia.getConfig().getVariant() != null) ? dia.getConfig().getVariant() : variantID;
					SR6PieceOfGearVariant variant = (varID != null) ? (SR6PieceOfGearVariant) tmpl.getVariant(varID) : null;
					OperationResult<CarriedItem<ItemTemplate>> res = SR6GearTool.buildItem(tmpl, carry, variant, model, false, decisions);
					if (res != null && res.isPresent()) {
						CarriedItem<ItemTemplate> built = res.get();
						descPane.setData(built);
						if (dia.getOptional() != null) {
							dia.getOptional().setTitle(built.getNameWithRating());
						}
					} else {
						descPane.setData(tmpl);
						if (dia.getOptional() != null) {
							dia.getOptional().setTitle(tmpl.getName());
						}
					}
				})
				.build();
	}

}
