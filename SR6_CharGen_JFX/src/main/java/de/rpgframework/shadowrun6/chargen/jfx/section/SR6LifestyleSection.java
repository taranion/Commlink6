package de.rpgframework.shadowrun6.chargen.jfx.section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.chargen.jfx.section.LifestyleSection;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6LifestyleListCell;

/**
 * LifestyleSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6LifestyleSection extends LifestyleSection<SR6Lifestyle> {

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	public SR6LifestyleSection() {
		super(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.life.section.lifestyles"),
				() -> new SR6Lifestyle(Shadowrun6Core.getItem(LifestyleQuality.class, "low")));
		list.setCellFactory(lv -> new SR6LifestyleListCell(() -> control));
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		super.updateController(ctrl);
	}

}
