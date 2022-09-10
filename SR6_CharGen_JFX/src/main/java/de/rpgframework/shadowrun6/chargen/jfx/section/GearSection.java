package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;
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
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.section.ComplexDataItemListSection;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.ShadowrunRules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.CarriedItemListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.control.ContentDisplay;
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
	protected ShadowrunCharacter model;
	
	private ToggleSwitch cbRuleNegativeNuyen;
	private ToggleSwitch cbRulePayGear;

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
	private void initSecondaryContent() {
		cbRuleNegativeNuyen = new ToggleSwitch("Allow negative Nuyen during chargen");
		cbRulePayGear       = new ToggleSwitch("Pay gear in career");
		cbRuleNegativeNuyen.setContentDisplay(ContentDisplay.LEFT);
		cbRulePayGear.setContentDisplay(ContentDisplay.RIGHT);
		
		cbRuleNegativeNuyen.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) model.setRuleValue(ShadowrunRules.CHARGEN_NEGATIVE_NUYEN, String.valueOf(n));
		});
		cbRulePayGear.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) model.setRuleValue(ShadowrunRules.CAREER_PAY_GEAR, String.valueOf(n));
		});
		
		setMode(Mode.BACKDROP);
		
		VBox bxRules = new VBox(10);
		bxRules.getChildren().add(cbRuleNegativeNuyen);
		bxRules.getChildren().add(cbRulePayGear);
		setSecondaryContent(bxRules);

	}

	//-------------------------------------------------------------------
	@Override
	protected void selectionChanged(CarriedItem<ItemTemplate> old, CarriedItem<ItemTemplate> neu) {
		if (neu==null) {
			btnDel.setDisable(true);
		} else {
			Possible possible = control.getEquipmentController().canBeDeselected(neu);
			btnDel.setDisable( !possible.get() );
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.WARNING, "ToDo: onAdd "+carry);
		
		ItemTemplateSelector selector = new ItemTemplateSelector(control, carry, templateFilter);
		if (templateFilter!=null)
			selector.setBaseFilter(templateFilter);
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES, "section.gear.selector.title"), selector, CloseType.OK, CloseType.CANCEL);
		CloseType closed = FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.WARNING, "closed "+closed);
		if (closed==CloseType.OK) {
			ItemTemplate selected = selector.getSelected();
			OperationResult<CarriedItem<ItemTemplate>> result = null;
			// Eventually show decision dialog
			if (!selected.getChoices().isEmpty() || !selected.getVariants().isEmpty()) {
				logger.log(Level.WARNING, "Select with choices or variants");
				ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>> dia2 = new ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>>(FlexibleApplication.getInstance(), control.getEquipmentController(), carry);
				Decision[] dec = dia2.apply(selected, selected.getChoices());
				if (dec!=null) {
					// Not cancelled
					String variantID = dia2.getSelectedVariant();
					logger.log(Level.DEBUG, "After dialog: variant   = "+variantID);
					logger.log(Level.DEBUG, "After dialog: decisions = "+Arrays.toString(dec));
					result = control.getEquipmentController().select(selector.getSelected(), variantID, carry, dec);
				}
			} else {
				logger.log(Level.WARNING, "Select without decisions");
				result = control.getEquipmentController().select(selector.getSelected());
			}
			if (result != null) {
				if (result.wasSuccessful()) {
					logger.log(Level.WARNING, "Successful");
					refresh();
				} else {
					logger.log(Level.WARNING, "Failed: " + result.getError());
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
		logger.log(Level.WARNING, "ToDo: onDelete");
		
		if (control.getEquipmentController().deselect(item)) {
			refresh();
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		logger.log(Level.DEBUG, "updateController");
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
		logger.log(Level.WARNING, "GearSection("+getTitle()+": "+filter);
		if (model==null) return;
		
		// If  a model and a filter exists, update automatically
		if (filter!=null) {
			List<CarriedItem<ItemTemplate>> data = null;
			data = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItems())
			.stream()
			.filter(filter)
			.collect(Collectors.toList());
			list.getItems().setAll(data);
		}
		
		// Secondary content
		cbRuleNegativeNuyen.setSelected(model.getRuleValueAsBoolean(ShadowrunRules.CHARGEN_NEGATIVE_NUYEN));
		cbRulePayGear.setSelected(model.getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR));

	}

}
