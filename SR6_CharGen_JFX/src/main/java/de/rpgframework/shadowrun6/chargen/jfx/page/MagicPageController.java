package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.util.Locale;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.chargen.jfx.section.RitualSection;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.WorldType;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.AdeptPowerSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.CombatSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6FocusSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6MetamagicSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6SpellSection;
import javafx.fxml.FXML;

/**
 * Controller for MagicPage.fxml.
 */
public class MagicPageController {

	private final static Logger logger = System.getLogger(MagicPageController.class.getPackageName());

	@FXML private CombatSection secCombat;
	@FXML private SR6FocusSection secFoci;
	@FXML private SR6SpellSection secSpells;
	@FXML private AdeptPowerSection secAdeptPowers;
	@FXML private RitualSection secRituals;
	@FXML private SR6MetamagicSection secMeta;

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		if (secCombat != null) {
			secCombat.setWorldType(WorldType.ASTRAL);
		}
		if (secRituals != null) {
			secRituals.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.magic.section.rituals"));
			secRituals.initialize(
					Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault())
			);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		if (secCombat != null) secCombat.updateController(ctrl);
		if (secFoci != null) secFoci.updateController(ctrl);
		if (secSpells != null) secSpells.updateController(ctrl);
		if (secAdeptPowers != null) secAdeptPowers.updateController(ctrl);
		if (secRituals != null) secRituals.updateController(ctrl);
		if (secMeta != null) secMeta.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secCombat != null) secCombat.refresh();
		if (secFoci != null) secFoci.refresh();
		if (secSpells != null) secSpells.refresh();
		if (secAdeptPowers != null) secAdeptPowers.refresh();
		if (secRituals != null) secRituals.refresh();
		if (secMeta != null) secMeta.refresh();

		if (control != null && control.getModel() != null) {
			MagicOrResonanceType mor = control.getModel().getMagicOrResonanceType();
			boolean hasPowers = mor != null && mor.usesPowers();
			if (secAdeptPowers != null) {
				secAdeptPowers.setVisible(hasPowers);
				secAdeptPowers.setManaged(hasPowers);
			}
			boolean hasSpells = mor != null && mor.usesSpells();
			if (secSpells != null) {
				secSpells.setVisible(hasSpells);
				secSpells.setManaged(hasSpells);
			}
		}
	}

}
