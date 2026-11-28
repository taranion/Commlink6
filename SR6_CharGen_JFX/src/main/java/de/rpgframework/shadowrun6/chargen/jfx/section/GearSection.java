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
import org.prelle.javafx.AlertType;
import javafx.scene.control.ButtonType;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Mode;
import org.prelle.javafx.SymbolIcon;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController.RemoveMode;
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
import de.rpgframework.shadowrun6.chargen.jfx.SR6SelectAndDecide;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.CarriedItemListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class GearSection extends ComplexDataItemListSection<ItemTemplate, CarriedItem<ItemTemplate>> {

	protected final static Logger logger = System.getLogger(GearSection.class.getPackageName());

	protected static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(GearSection.class.getPackageName()+".Section");

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
	public GearSection() {
		this("Gear", CarryMode.CARRIED, null, null);
	}

	//-------------------------------------------------------------------
	public GearSection(String title) {
		this(title, CarryMode.CARRIED, null, null);
	}

	//-------------------------------------------------------------------
	public void setFilter(CarryMode carry, Predicate<ItemTemplate> selectFilter, Predicate<CarriedItem<ItemTemplate>> showFilter) {
		this.carry = carry;
		this.templateFilter = selectFilter;
		this.filter = showFilter;
		refresh();
	}

	//-------------------------------------------------------------------
	public void setFilter(Predicate<ItemTemplate> selectFilter, Predicate<CarriedItem<ItemTemplate>> showFilter) {
		setFilter(CarryMode.CARRIED, selectFilter, showFilter);
	}

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
		logger.log(Level.INFO, "ENTER: onAdd(carry={0}, container={1}, hook={2}, templateFilter={3}", carry, addToContainer, addToHook, templateFilter);
		var action = (addToContainer != null)
				? SR6SelectAndDecide.gearForEmbedding(control, addToContainer, addToHook, templateFilter)
				: SR6SelectAndDecide.gear(control, carry, templateFilter);
		action.execute();
		refresh();
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
			RemoveMode mode = RemoveMode.UNDO;
			if (model.isInCareerMode()) {
				mode = askRemoveMode(item);
			}
			if (control.getEquipmentController().deselect(item, mode)) {
				refresh();
			}
		}
	}

	//-------------------------------------------------------------------
	private RemoveMode askRemoveMode(CarriedItem<ItemTemplate> item) {
		Label lblQuest = new Label(ResourceI18N.get(RES, "section.gear.delete.question"));
		ButtonType closed = FlexibleApplication.getInstance().showAlertAndCall(AlertType.QUESTION,
				ResourceI18N.get(RES, "section.gear.delete.title"), lblQuest, null, JavaFXConstants.UNDO, JavaFXConstants.TRASH);
		if (closed==JavaFXConstants.UNDO) return RemoveMode.UNDO;
		if (closed==JavaFXConstants.TRASH) return RemoveMode.REMOVE_LATE;

		return RemoveMode.UNDO;
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
	public void refresh() {
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
