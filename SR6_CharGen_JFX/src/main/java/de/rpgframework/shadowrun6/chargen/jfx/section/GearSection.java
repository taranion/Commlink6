package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.ComplexDataItemListSection;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.PriorityAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.PrioritySR6AttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.PointBuyAttributeTable;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.CarriedItemListCell;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;

/**
 * @author prelle
 *
 */
public class GearSection extends ComplexDataItemListSection<ItemTemplate, CarriedItem<ItemTemplate>> {

	private final static Logger logger = System.getLogger(GearSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private List<ItemType> allowedTypes;

	private SR6CharacterController control;
	private ShadowrunCharacter model;

	//-------------------------------------------------------------------
	/**
	 * @param title
	 */
	public GearSection(String title, ItemType...types) {
		super(title);
		allowedTypes = List.of(types);
		list.setCellFactory(lv -> new CarriedItemListCell( () -> control.getEquipmentController()));
		
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		logger.log(Level.WARNING, "ToDo: onAdd");
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
				continue;
			}
			if (tmp.getResolved().getItemType()==null) {
				System.err.println("No item type for item "+tmp.getKey()+" UUID "+tmp.getUuid());
				System.exit(1);
			}
		}
		
		List<CarriedItem<ItemTemplate>> data = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItems())
			.stream()
			.filter(item -> allowedTypes.contains(item.getResolved().getItemType()))
			.collect(Collectors.toList());
		list.getItems().setAll(data);
	}

}
