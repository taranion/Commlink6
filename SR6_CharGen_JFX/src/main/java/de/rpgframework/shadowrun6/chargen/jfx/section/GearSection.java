package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.controlsfx.control.ToggleSwitch;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Mode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.section.ComplexDataItemListSection;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.CarriedItemListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class GearSection extends ComplexDataItemListSection<ItemTemplate, CarriedItem<ItemTemplate>> {

	private final static Logger logger = System.getLogger(GearSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private CarryMode carry = CarryMode.CARRIED;
	private Predicate<CarriedItem<ItemTemplate>> filter;
	private Predicate<ItemTemplate> templateFilter;

	protected SR6CharacterController control;
	protected Shadowrun6Character model;

	private ToggleSwitch cbRuleNegativeNuyen;
	private ToggleSwitch cbRulePayGear;

	/* To be used from child classes, E.g. ActiveProgramsSection */
	protected CarriedItem<ItemTemplate> addToContainer;
	protected ItemHook addToHook;

	//-------------------------------------------------------------------
	public GearSection(String title, CarryMode carry, Predicate<ItemTemplate> selectFilter, Predicate<CarriedItem<ItemTemplate>> showFilter) {
		super(title);
		this.carry = carry;
		list.setCellFactory(lv -> new CarriedItemListCell( control));
		this.filter = showFilter;
		this.templateFilter = selectFilter;

		initSecondaryContent();
		refresh();
	}

	//-------------------------------------------------------------------
	public GearSection(String title, Predicate<ItemTemplate> selectFilter, Predicate<CarriedItem<ItemTemplate>> showFilter) {
		this(title, CarryMode.CARRIED, selectFilter, showFilter);
	}

	//-------------------------------------------------------------------
	protected void setSelectFilter(Predicate<ItemTemplate> selectFilter) {
		logger.log(Level.INFO, "Change templateFilter to "+selectFilter);
		this.templateFilter = selectFilter;
	}

	//-------------------------------------------------------------------
	private void initSecondaryContent() {
		cbRuleNegativeNuyen = new ToggleSwitch();
		cbRuleNegativeNuyen.setGraphicTextGap(0);
		cbRulePayGear       = new ToggleSwitch();
		cbRulePayGear.setGraphicTextGap(0);

		cbRuleNegativeNuyen.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) control.getRuleController().setRuleValue(ShadowrunRules.CHARGEN_NEGATIVE_NUYEN, n);
		});
		cbRulePayGear.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) control.getRuleController().setRuleValue(ShadowrunRules.CAREER_PAY_GEAR, n);
		});

		setMode(Mode.BACKDROP);

		VBox bxRules = new VBox(10);
		bxRules.getChildren().add(makeLabel(cbRuleNegativeNuyen, ShadowrunRules.CHARGEN_NEGATIVE_NUYEN));
		bxRules.getChildren().add(makeLabel(cbRulePayGear      , ShadowrunRules.CAREER_PAY_GEAR));
		setSecondaryContent(bxRules);

	}

	//-------------------------------------------------------------------
	private Label makeLabel(ToggleSwitch ts, Rule rule) {
		Label lb = new Label(rule.getName(Locale.getDefault()), ts);
		lb.setWrapText(true);
		lb.setAlignment(Pos.TOP_LEFT);
		VBox.setMargin(lb, new Insets(0, 0, 0, -20));
		return lb;
	}

	//-------------------------------------------------------------------
	@Override
	protected void selectionChanged(CarriedItem<ItemTemplate> old, CarriedItem<ItemTemplate> neu) {
		logger.log(Level.ERROR, "Selection changed from "+old+" to "+neu);
		if (neu==null) {
			btnDel.setDisable(true);
		} else {
			Possible possible = (addToContainer!=null)
					?
					control.getEquipmentController().canBeRemoved(addToContainer, addToHook, neu)
							:
					control.getEquipmentController().canBeDeselected(neu);
			if (!possible.get()) logger.log(Level.WARNING, "Controller {0} says I cannot deselect/remove {1}", control.getEquipmentController(), neu);
			btnDel.setDisable( !possible.get() );
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.INFO, "ENTER: onAdd(carry={0}, container={1}, hook={2}, templateFilter={3}",carry,addToContainer, addToHook, templateFilter);

		ItemTemplateSelector selector = new ItemTemplateSelector(control, carry, templateFilter, addToContainer, addToHook);
//		if (templateFilter!=null)
//			selector.setBaseFilter(templateFilter);
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES, "section.gear.selector.title"), selector, CloseType.OK, CloseType.CANCEL);
		CloseType closed = FlexibleApplication.getInstance().showAndWait(dialog);
		if (closed==CloseType.OK) {
			ItemTemplate selected = selector.getSelected();
			SR6PieceOfGearVariant suggestedVariant = selector.getVariant();
			OperationResult<CarriedItem<ItemTemplate>> result = null;
			// Eventually show decision dialog
			boolean needToAsk = !selected.getChoices().isEmpty();
			needToAsk |= !selected.getVariants().isEmpty();
			if (  control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.ALWAYS_ASK_FOR_FLAGS))
				needToAsk |= !selected.getUserSelectableFlags(SR6ItemFlag.class).isEmpty();
			if (needToAsk) {
				logger.log(Level.DEBUG, "Select/Embed with choices or variants or flags");
				ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>> dia2 = new ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>>(control.getEquipmentController(), carry);
				dia2.setSuggestedVariant(suggestedVariant);
				Decision[] dec = dia2.apply(selected, selected.getChoices());
				if (dec!=null) {
					// Not cancelled
					String variantID = dia2.getSelectedVariant();
					logger.log(Level.DEBUG, "After dialog: variant   = "+variantID);
					logger.log(Level.DEBUG, "After dialog: decisions = "+Arrays.toString(dec));
					if (addToContainer==null) {
						logger.log(Level.WARNING, "Select with decisions");
						result = control.getEquipmentController().select(selector.getSelected(), variantID, carry, dec);
					} else {
						logger.log(Level.WARNING, "Embed with decisions");
						result = control.getEquipmentController().embed(addToContainer, addToHook, selector.getSelected(), variantID, dec);
						logger.log(Level.WARNING, "Embedding returned "+result);
					}
				}
			} else {
				if (addToContainer==null) {
					logger.log(Level.WARNING, "Select without decisions");
					result = control.getEquipmentController().select(selector.getSelected());
				} else {
					logger.log(Level.INFO, "Embed without decisions");
					result = control.getEquipmentController().embed(addToContainer, addToHook, selected, null);
				}
			}
			if (result != null) {
				if (result.wasSuccessful()) {
					logger.log(Level.WARNING, "Successful");
					refresh();
				} else {
					logger.log(Level.WARNING, "Failed: " + result.getError());
					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, result.getError());
				}
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(CarriedItem<ItemTemplate> item) {
		// TODO Auto-generated method stub

		if (addToContainer!=null) {
			logger.log(Level.INFO, "onDelete {0} from container {1}", item, addToContainer);
			Possible poss = control.getEquipmentController().removeEmbedded(addToContainer, addToHook, item);
			if (poss.get()) {
				refresh();
			} else {
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, poss.toString());
			}
		} else {
			logger.log(Level.WARNING, "onDelete {0} ", item);
			if (control.getEquipmentController().deselect(item)) {
				refresh();
			}
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
//		logger.log(Level.DEBUG, "updateController");
		this.control = ctrl;
		model = ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
//		logger.log(Level.DEBUG, "refresh");
//		logger.log(Level.WARNING, "GearSection("+getTitle()+": "+filter);
		if (model==null) return;

		// If  a model and a filter exists, update automatically
		if (filter!=null) {
			List<CarriedItem<ItemTemplate>> data = null;
			data = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItemsRecursive())
			.stream()
			.filter(filter)
			.collect(Collectors.toList());
			list.getItems().setAll(data);
		}

		// Secondary content
		cbRuleNegativeNuyen.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CHARGEN_NEGATIVE_NUYEN));
		cbRulePayGear.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR));

	}

}
