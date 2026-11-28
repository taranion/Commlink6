package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.Locale;

import org.prelle.javafx.Mode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.chargen.jfx.section.ContactSection;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import javafx.scene.control.CheckBox;

/**
 * ContactSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6ContactSection extends ContactSection {

	private CheckBox cbExtended;

	//-------------------------------------------------------------------
	public SR6ContactSection() {
		super(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.life.section.contacts"));
		initExtended();
	}

	//-------------------------------------------------------------------
	public SR6ContactSection(String title) {
		super(title);
		initExtended();
	}

	//-------------------------------------------------------------------
	private void initExtended() {
		cbExtended = new CheckBox(Shadowrun6Rules.CHARGEN_EXTENDED_CONTACT.getName(Locale.getDefault()));
		setSecondaryContent(cbExtended);
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		super.updateController(ctrl);
		if (ctrl != null) {
			if (ctrl.getClass().getSimpleName().contains("Generator")) {
				setMode(Mode.BACKDROP);
			} else {
				setMode(Mode.REGULAR);
			}
		}
	}

}
