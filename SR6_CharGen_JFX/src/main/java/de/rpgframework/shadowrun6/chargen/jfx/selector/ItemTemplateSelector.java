package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.OptionalNodePane;

import de.rpgframework.genericrpg.Possible;
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
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.control.CheckBox;

/**
 * @author prelle
 *
 */
public class ItemTemplateSelector extends Selector<ItemTemplate, CarriedItem<ItemTemplate>> {
	
	private final static Logger logger = System.getLogger(ItemTemplateSelector.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(ItemTemplateSelector.class.getPackageName()+".Selectors");

	protected SR6CharacterController charGen;
	protected CarryMode carry;
	
	// Shall character requirements be ignored
	private CheckBox cbIgnoreRequirements;

	//-------------------------------------------------------------------
	public ItemTemplateSelector(SR6CharacterController charGen, CarryMode mode, Predicate<ItemTemplate> templateFilter, CarriedItem<ItemTemplate> container, ItemHook hook) {
		super(charGen.getEquipmentController(),
				templateFilter,
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()),
				new FilterItemTemplate(mode));
		logger.log(Level.INFO, "create ItemTemplateSelector (container={0}, hook={1}, carry={2})", container, hook, mode);
		this.carry = mode;
		this.charGen = charGen;
		if (container!=null) {
			listPossible.setCellFactory( lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController(), container, hook));
		} else {
			listPossible.setCellFactory( lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController(), carry));	
		}
		
		// Button control
    	listPossible.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
    		logger.log(Level.DEBUG, "Selected {0}", n);
    		Possible poss = null;
    		if (carry==CarryMode.EMBEDDED) {
    			poss = charGen.getEquipmentController().canBeEmbedded(container, hook, n, null);
    		} else {
       			poss = charGen.getEquipmentController().canBeSelected(n);
    		}
    		logger.log(Level.DEBUG, "Selection possible = {0}",poss);
    		if (btnCtrl!=null) {
    			btnCtrl.setDisabled(CloseType.OK, !poss.get());
    		}
    	});

		
		genericDescr= new ItemTemplatePane(r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()),carry);
		
		logger.log(Level.WARNING, "Show filter for item types");
	}

}
