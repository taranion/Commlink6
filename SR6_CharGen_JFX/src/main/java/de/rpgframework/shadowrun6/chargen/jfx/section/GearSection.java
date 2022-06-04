package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.ComplexDataItemListSection;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.PriorityAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.PrioritySR6AttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.PointBuyAttributeTable;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.CarriedItemListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;

/**
 * @author prelle
 *
 */
public class GearSection extends ComplexDataItemListSection<ItemTemplate, CarriedItem<ItemTemplate>> {

	private final static Logger logger = System.getLogger(GearSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private ItemType[] allowedTypes;
	private Predicate<CarriedItem<ItemTemplate>> filter;

	private SR6CharacterController control;
	private ShadowrunCharacter model;

	//-------------------------------------------------------------------
	public GearSection(String title, ItemType...types) {
		super(title);
		allowedTypes = types;
		list.setCellFactory(lv -> new CarriedItemListCell( control));
		filter = item -> List.of(allowedTypes).contains(item.getResolved().getItemType());
		
		refresh();
	}

	//-------------------------------------------------------------------
	public GearSection(String title, Predicate<CarriedItem<ItemTemplate>> filter) {
		super(title);
		this.filter = filter;
		list.setCellFactory(lv -> new CarriedItemListCell( control));
		
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.WARNING, "ToDo: onAdd");
		
		ItemTemplateSelector selector = new ItemTemplateSelector(control, allowedTypes);
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES, "section.gear.selector.title"), selector, CloseType.OK, CloseType.CANCEL);
		CloseType closed = FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.WARNING, "closed "+closed);
		if (closed==CloseType.OK) {
			logger.log(Level.WARNING, "Select with decisions");
			ItemTemplate selected = selector.getSelected();
			OperationResult<CarriedItem<ItemTemplate>> result = null;
			if (!selected.getChoices().isEmpty()) {
				ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>> dia2 = new ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>>(FlexibleApplication.getInstance(), control.getEquipmentController());
				Decision[] dec = dia2.apply(selected, selected.getChoices());
				result = control.getEquipmentController().select(selector.getSelected(), dec);
			} else {
				logger.log(Level.WARNING, "Select without decisions");
				result = control.getEquipmentController().select(selector.getSelected());
			}
			if (result.wasSuccessful()) {
				logger.log(Level.WARNING, "Successful");
				refresh();
			} else {
				logger.log(Level.WARNING, "Failed: "+result.getError());

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
		logger.log(Level.DEBUG, "refresh");
		
		if (model==null) return;
		List<CarriedItem<ItemTemplate>> data2 = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItems());
		for (CarriedItem<ItemTemplate> tmp :data2) {
			if (tmp.getResolved()==null) {
				System.err.println("No resolved item '"+tmp.getKey()+"' for item "+tmp.getUuid());
				logger.log(Level.ERROR, "No resolved item '"+tmp.getKey()+"' for item "+tmp.getUuid());
				continue;
			}
			if (tmp.getResolved().getItemType()==null) {
				System.err.println("No item type for item "+tmp.getKey()+" UUID "+tmp.getUuid());
				System.exit(1);
			}
		}
		
		List<CarriedItem<ItemTemplate>> data = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItems())
			.stream()
			.filter(filter)
			.collect(Collectors.toList());
		for (CarriedItem goo : data) {
			System.out.println("..."+goo.getKey());
		}
		list.getItems().setAll(data);
	}

}
