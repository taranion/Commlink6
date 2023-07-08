package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.prelle.javafx.CloseType;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.Possible.State;
import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.Selector;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6EquipmentController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.FilterItemTemplate;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.ItemTemplateListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.ItemTemplatePane;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				new FilterItemTemplate(mode));
		logger.log(Level.INFO, "create ItemTemplateSelector (container={0}, hook={1}, carry={2})", container, hook, mode);
		this.carry = mode;
		this.charGen = charGen;
		if (container!=null) {
			listPossible.setCellFactory( lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController(), container, hook));
		} else {
			if (mode==CarryMode.EMBEDDED)
				throw new NullPointerException("CarryMode is EMBEDDED, but container is NULL");
			listPossible.setCellFactory( lv -> new ItemTemplateListCell( () -> charGen.getEquipmentController(), carry));
		}

		// Button control
    	listPossible.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
    		logger.log(Level.DEBUG, "Selected {0}", n);
    		Possible poss = null;
    		if (carry==CarryMode.EMBEDDED) {
    			poss = charGen.getEquipmentController().canBeEmbedded(container, hook, n, null);
    		} else if (n!=null) {
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

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.Selector#selectionChanged(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	protected void selectionChanged(ItemTemplate n) {
		Pane box = getDescriptionNode(n);
		logger.log(Level.DEBUG, "Selected {0} and show description node {1}", n, box);
		col2.getChildren().setAll(box, lbNotPossible);
		VBox.setVgrow(box, Priority.ALWAYS);
		help.getChildren().setAll(col1, col2);

		ISR6EquipmentController sr6Control = (ISR6EquipmentController) control;
		AGearData relevant = n.getMainOrVariant(carry);
		String variantID = (relevant instanceof SR6PieceOfGearVariant)?((SR6PieceOfGearVariant)relevant).getId():null;
		Possible poss = sr6Control.canBeSelected(n, variantID, carry);
		//logger.log(Level.TRACE, "  poss= {0}  btnCtrl={1}", poss, btnCtrl);
		if (btnCtrl!=null) {
			btnCtrl.setDisabled(CloseType.OK, !poss.get());
		}
		if (poss.getState()==State.REQUIREMENTS_NOT_MET) {
			List<String> problems = poss.getUnfulfilledRequirements().stream().map(r -> resolver.apply(r)).collect(Collectors.toList());
			lbNotPossible.setText(String.join(", ", problems));
		} else if (!poss.get())
			lbNotPossible.setText(poss.getMostSevere().getMessage());
		else
			lbNotPossible.setText(null);
	}

}
