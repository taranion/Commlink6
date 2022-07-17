package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.OptionalNodePane;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.Selector;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.FilterItemTemplate;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.ItemTemplateListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.ItemTemplatePane;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import javafx.scene.control.CheckBox;

/**
 * @author prelle
 *
 */
public class ItemTemplateSelector extends Selector<ItemTemplate, CarriedItem<ItemTemplate>> {
	
	private final static Logger logger = System.getLogger(ItemTemplateSelector.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(ItemTemplateSelector.class.getPackageName()+".Selectors");

	protected SR6CharacterController charGen;
	
	protected ComplexDataItemControllerNode<ItemTemplate, CarriedItem<ItemTemplate>> selection;
	protected GenericDescriptionVBox<ItemTemplate> bxDescription;
	protected OptionalNodePane layout;
	
	// Shall character requirements be ignored
	private CheckBox cbIgnoreRequirements;

	//-------------------------------------------------------------------
	public ItemTemplateSelector(SR6CharacterController charGen, CarryMode carry, Predicate<ItemTemplate> templateFilter) {
		super(charGen.getEquipmentController(),
				templateFilter,
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()),
				new FilterItemTemplate(carry, null));
		this.charGen = charGen;
		listPossible.setCellFactory( lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController()));
		
		genericDescr= new ItemTemplatePane(r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()),carry);
		
		logger.log(Level.WARNING, "Show filter for item types");
	}

}
