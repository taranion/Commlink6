package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.NavigButtonControl;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author prelle
 *
 */
public class SkillSelector extends Selector<SR6Skill,SR6SkillValue> {

	private final static Logger logger = System.getLogger(SkillSelector.class.getPackageName());

	public final static ResourceBundle RES = ResourceBundle.getBundle(SkillSelector.class.getPackageName()+".Selectors");

	private ComplexDataItemController<SR6Skill, SR6SkillValue> control;

	//-------------------------------------------------------------------
	public SkillSelector(SR6CharacterController ctrl, Predicate<SR6Skill> baseFilter) {
		super(ctrl.getSkillController(),
				baseFilter,
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				null);
		control = ctrl.getSkillController();
		logger.log(Level.INFO, "create SkillSelector");
		listPossible.setCellFactory( lv -> new ComplexDataItemListCell<SR6Skill>(
				() -> control,
				Shadowrun6Tools.requirementResolver(Locale.getDefault())));


		// Button control
    	listPossible.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
    		logger.log(Level.DEBUG, "Selected {0}", n);
    		logger.log(Level.DEBUG, "Selected2 {0}", n.getDescription());
    		Possible poss = ctrl.getSkillController().canBeSelected(n);
    		logger.log(Level.DEBUG, "Selection possible = {0}",poss);
    		if (btnCtrl!=null) {
    			btnCtrl.setDisabled(CloseType.OK, !poss.get());
    		}
    	});
	}

	//-------------------------------------------------------------------
	public void setButtonControl(NavigButtonControl btnCtrl) {
		super.setButtonControl(btnCtrl);

		btnCtrl.setCallback( (close) -> {
			if (close!=CloseType.OK) return true;
			SR6Skill select = getSelected();
			if (select==null) return false;
	   		Possible poss = control.canBeSelected(select);
    		logger.log(Level.DEBUG, "Selection possible = {0}",poss);
			return poss.get();
		});
	}

}
