package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.controlsfx.control.ToggleSwitch;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Mode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.Rule;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.section.ListSection;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.ShadowrunRules;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.MartialArtsSelector;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MartialArtsSection extends ListSection<MartialArtsValue> {

	private final static Logger logger = System.getLogger(MartialArtsSection.class.getPackageName());

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CombatSection.class.getPackageName()+".Section");

	protected SR6CharacterController control;
	protected Shadowrun6Character model;

	private ToggleSwitch cbRuleUndoChargen;
	private ToggleSwitch cbRuleUndoCareer;
	private ToggleSwitch cbRulePayGear;

	//-------------------------------------------------------------------
	public MartialArtsSection() {
		super(ResourceI18N.get(RES, "section.martialarts.title"));
		initSecondaryContent();
	}

	//-------------------------------------------------------------------
	private void initSecondaryContent() {
		cbRuleUndoChargen = new ToggleSwitch();
		cbRuleUndoChargen.setGraphicTextGap(0);
		cbRuleUndoCareer = new ToggleSwitch();
		cbRuleUndoCareer.setGraphicTextGap(0);
		cbRulePayGear       = new ToggleSwitch();
		cbRulePayGear.setGraphicTextGap(0);

		cbRuleUndoChargen.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) model.setRuleValue(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN, String.valueOf(n));
		});
		cbRuleUndoCareer.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) model.setRuleValue(ShadowrunRules.CAREER_UNDO_FROM_CAREER, String.valueOf(n));
		});
		cbRulePayGear.selectedProperty().addListener( (ov,o,n) -> {
			if (model!=null) model.setRuleValue(ShadowrunRules.CAREER_PAY_GEAR, String.valueOf(n));
		});

		setMode(Mode.BACKDROP);

		VBox bxRules = new VBox(10);
		bxRules.getChildren().add(makeLabel(cbRuleUndoChargen, ShadowrunRules.CAREER_UNDO_FROM_CHARGEN));
		bxRules.getChildren().add(makeLabel(cbRuleUndoCareer , ShadowrunRules.CAREER_UNDO_FROM_CAREER));
		bxRules.getChildren().add(makeLabel(cbRulePayGear    , ShadowrunRules.CAREER_PAY_GEAR));
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
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.INFO, "ENTER: onAdd()");

		MartialArtsSelector selector = new MartialArtsSelector(control.getMartialArtsController(), null);
//		if (templateFilter!=null)
//			selector.setBaseFilter(templateFilter);
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES, "section.martialarts.selector.title"), selector, CloseType.OK, CloseType.CANCEL);
		CloseType closed = FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.WARNING, "closed "+closed);
		if (closed==CloseType.OK) {
			logger.log(Level.INFO, "Trying to select {0}", selector.getSelected());
			OperationResult<MartialArtsValue> ret =  control.getMartialArtsController().select(selector.getSelected());
			if (ret.wasSuccessful()) {
				refresh();
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(MartialArtsValue item) {
		MartialArtsValue selected = list.getSelectionModel().getSelectedItem();
		logger.log(Level.DEBUG,"Martial art to deselect: "+selected);
		control.getMartialArtsController().deselect(selected);
		refresh();
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
//		logger.log(Level.WARNING, "GearSection("+getTitle()+": "+filter);
		if (model==null) return;

		list.getItems().setAll(model.getMartialArts());

		// Secondary content
		cbRuleUndoChargen.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CHARGEN));
		cbRuleUndoCareer.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_UNDO_FROM_CAREER));
		cbRulePayGear.setSelected( control.getRuleController().getRuleValueAsBoolean(ShadowrunRules.CAREER_PAY_GEAR));

	}

}
