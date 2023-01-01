package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemEnhancementValue;
import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import javafx.scene.layout.Pane;

/**
 * @author prelle
 *
 */
public class ItemEnhancementSelector extends Selector<SR6ItemEnhancement, ItemEnhancementValue<SR6ItemEnhancement>> {

	private final static Logger logger = System.getLogger(ItemEnhancementSelector.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(ItemEnhancementSelector.class.getPackageName()+".Selectors");

	private ComplexDataItemController<SR6ItemEnhancement, ItemEnhancementValue<SR6ItemEnhancement>> control;

	//-------------------------------------------------------------------
	/**
	 * @param ctrl
	 * @param resolver
	 * @param filter
	 */
	public ItemEnhancementSelector(ISR6EquipmentController ctrl, CarriedItem<ItemTemplate> toModify,
			AFilterInjector<SR6ItemEnhancement> filter) {
		super(ctrl.getItemEnhancementController(toModify),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				filter);
		control = ctrl.getItemEnhancementController(toModify);
		logger.log(Level.INFO, "create ItemEnhancementSelector (toModify={0}, filterInject={1})", toModify, filter);
		listPossible.setCellFactory( lv -> new ComplexDataItemListCell<SR6ItemEnhancement>(
				() -> control,
				Shadowrun6Tools.requirementResolver(Locale.getDefault())));
	}

	//-------------------------------------------------------------------
	/**
	 * @param ctrl
	 * @param baseFilter
	 * @param resolver
	 * @param filter
	 */
	public ItemEnhancementSelector(ISR6EquipmentController ctrl, CarriedItem<ItemTemplate> toModify,
			Predicate<SR6ItemEnhancement> baseFilter, AFilterInjector<SR6ItemEnhancement> filter) {
		super(ctrl.getItemEnhancementController(toModify), baseFilter,
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				filter);
		control = ctrl.getItemEnhancementController(toModify);
		logger.log(Level.INFO, "create ItemEnhancementSelector (toModify={0}, baseFilter={1}, filterInject={2})", toModify, baseFilter, filter);
		listPossible.setCellFactory( lv -> new ComplexDataItemListCell<SR6ItemEnhancement>(
				() -> control,
				Shadowrun6Tools.requirementResolver(Locale.getDefault())));
	}

	//-------------------------------------------------------------------
	protected Pane getDescriptionNode(SR6ItemEnhancement selected) {
		genericDescr.setData(selected);
		return genericDescr;
	}

}
