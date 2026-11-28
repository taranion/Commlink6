package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.Locale;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.Focus;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IFocusController;
import de.rpgframework.shadowrun.chargen.jfx.section.FocusSection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6JFXTools;

/**
 * FocusSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6FocusSection extends FocusSection {

	private SR6CharacterController control;
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public SR6FocusSection() {
		super(Shadowrun6Tools.requirementResolver(Locale.getDefault()), Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		this.model = (ctrl != null) ? ctrl.getModel() : null;
		super.updateController(ctrl);
	}

	//-------------------------------------------------------------------
	@Override
	protected Decision[] requestUserDecisions(Focus value) {
		if (control != null && control.getFocusController() != null) {
			return SR6JFXTools.askDecisions(control.getFocusController(), value);
		}
		return new Decision[0];
	}

	//-------------------------------------------------------------------
	@Override
	public void refresh() {
		super.refresh();
		if (control != null && control.getFocusController() != null && model != null) {
			IFocusController ctrl = control.getFocusController();
			lbNum.setText(model.getFoci().size() + " / " + model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue(ValueType.NATURAL));
			lbSum.setText(ctrl.getFocusPointsSpent() + " / " + ctrl.getFocusPointsMax());
		}
	}

}
