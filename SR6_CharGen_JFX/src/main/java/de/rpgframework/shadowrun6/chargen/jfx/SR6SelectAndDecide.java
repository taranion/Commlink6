package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.jfx.SelectAndDecide;
import de.rpgframework.jfx.pane.DecisionDialog;
import de.rpgframework.jfx.pane.DecisionDialog.DecisionDialogConfig;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.chargen.jfx.selector.SR6SelectorBuilder;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * SR6 presets for the flow "select, decide, add" ({@link SelectAndDecide}):
 * SR6 selector builder with resolvers and search filter, a DecisionDialog with
 * the SR6 storage and a configuration that knows the character.
 *
 * @author prelle
 */
public class SR6SelectAndDecide<T extends ComplexDataItem, V extends ComplexDataItemValue<T>> extends SelectAndDecide<T, V> {

	private final static ResourceBundle RES = SR6CharacterViewLayout.UI;

	// ---
	protected SR6SelectAndDecide(ComplexDataItemController<T, V> controller) {
		super(controller);
	}

	// ---
	/**
	 * Preset for any type of data item.
	 *
	 * @param control   Character controller - provides the character for decisions
	 * @param ctrl      Controller for the data item type
	 * @param type      Type of the data item
	 * @param valueType Type of the value
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ComplexDataItem, V extends ComplexDataItemValue<T>> SR6SelectAndDecide<T, V> of(
			IShadowrunCharacterController control,
			ComplexDataItemController<T, V> ctrl,
			Class<T> type,
			Class<?> valueType) {
		SR6SelectAndDecide<T, V> ret = new SR6SelectAndDecide<>(ctrl);
		ret.selectorBuilder(SR6SelectorBuilder.builder(type, (Class<V>) (Class<?>) valueType).withController(ctrl));
		ret.configurator(DecisionDialog.<T, V>builder()
				.setController(ctrl)
				.setCustomizer(new SR6DecisionDialogStorage())
				.setModificationResolver(m -> Shadowrun6Tools.getModificationString(m, Locale.getDefault()))
				.build());
		ret.configFactory(item -> DecisionDialogConfig.<T>builder()
				.item(item)
				.model(control != null ? control.getModel() : null)
				.build());
		return ret;
	}

	// ---
	public Optional<V> run() {
		return execute();
	}

	// ---
	/**
	 * @return The variant decided in the DecisionDialog, or NULL
	 */
	public static String variant(Decision[] decisions) {
		return Arrays.stream(decisions)
				.filter(d -> PieceOfGear.VARIANT.equals(d.getChoiceUUID()))
				.map(Decision::getValue)
				.findFirst().orElse(null);
	}

	// ---
	/**
	 * @return The decisions without the variant - the equipment controller
	 *    expects the variant as own parameter
	 */
	public static Decision[] withoutVariant(Decision[] decisions) {
		return Arrays.stream(decisions)
				.filter(d -> !PieceOfGear.VARIANT.equals(d.getChoiceUUID()))
				.toArray(Decision[]::new);
	}

	// ---
	/**
	 * Preset for adding gear to the character (carried or implanted).
	 * Asks for decisions if the item has choices or variants.
	 */
	public static SR6SelectAndDecide<ItemTemplate, CarriedItem<ItemTemplate>> gear(SR6CharacterController control, CarryMode carry) {
		return gear(control, carry, null);
	}

	// ---
	/**
	 * Preset for adding gear to the character (carried or implanted) with filter.
	 * Asks for decisions if the item has choices or variants.
	 */
	public static SR6SelectAndDecide<ItemTemplate, CarriedItem<ItemTemplate>> gear(SR6CharacterController control, CarryMode carry,
			java.util.function.Predicate<ItemTemplate> templateFilter) {
		ISR6EquipmentController eqCtrl = control.getEquipmentController();
		SR6SelectAndDecide<ItemTemplate, CarriedItem<ItemTemplate>> ret = new SR6SelectAndDecide<>(eqCtrl);
		ret.title(ResourceI18N.get(RES, "section.gear.selector.title"));
		ret.selector(new ItemTemplateSelector(control, carry, templateFilter, null, null));
		ret.configurator(SR6JFXTools.createItemChoiceDialog(control, eqCtrl, null, null, carry, null));
		ret.configFactory(item -> DecisionDialogConfig.<ItemTemplate>builder()
				.item(item)
				.model(control.getModel())
				.carryMode(carry)
				.build());
		ret.needToAsk(item -> !eqCtrl.getChoicesToDecide(item).isEmpty() || !item.getVariants().isEmpty());
		ret.selectAction((item, decisions) -> eqCtrl.select(item, variant(decisions), carry, withoutVariant(decisions)));
		return ret;
	}

	// ---
	/**
	 * Preset for embedding gear into a slot of another item.
	 * Asks for decisions if the item has choices or variants.
	 */
	public static SR6SelectAndDecide<ItemTemplate, CarriedItem<ItemTemplate>> gearForEmbedding(SR6CharacterController control,
			CarriedItem<ItemTemplate> container, ItemHook hook) {
		return gearForEmbedding(control, container, hook, null);
	}

	// ---
	/**
	 * Preset for embedding gear into a slot of another item with filter.
	 * Asks for decisions if the item has choices or variants.
	 */
	public static SR6SelectAndDecide<ItemTemplate, CarriedItem<ItemTemplate>> gearForEmbedding(SR6CharacterController control,
			CarriedItem<ItemTemplate> container, ItemHook hook, java.util.function.Predicate<ItemTemplate> templateFilter) {
		ISR6EquipmentController eqCtrl = control.getEquipmentController();
		SR6SelectAndDecide<ItemTemplate, CarriedItem<ItemTemplate>> ret = new SR6SelectAndDecide<>(eqCtrl);
		ret.title(ResourceI18N.get(RES, "section.gear.selector.title"));
		ret.selector(new ItemTemplateSelector(control, CarryMode.EMBEDDED, templateFilter, container, hook));
		ret.configurator(SR6JFXTools.createItemChoiceDialog(control, eqCtrl, container, hook, CarryMode.EMBEDDED, null));
		ret.configFactory(item -> DecisionDialogConfig.<ItemTemplate>builder()
				.item(item)
				.model(control.getModel())
				.carryMode(CarryMode.EMBEDDED)
				.build());
		ret.needToAsk(item -> !eqCtrl.getChoicesToDecide(item).isEmpty() || !item.getVariants().isEmpty());
		ret.selectAction((item, decisions) -> eqCtrl.embed(container, hook, item, variant(decisions), withoutVariant(decisions)));
		return ret;
	}

}
