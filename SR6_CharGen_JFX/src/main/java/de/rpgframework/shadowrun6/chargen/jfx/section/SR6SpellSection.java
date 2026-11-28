package de.rpgframework.shadowrun6.chargen.jfx.section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.chargen.jfx.section.SpellSection;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.SR6SelectAndDecide;

/**
 * SpellSection for Shadowrun 6, usable directly from FXML.
 */
public class SR6SpellSection extends SpellSection<SR6Spell> {

	private SR6CharacterController control;
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	public SR6SpellSection() {
		setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.magic.section.spells"));
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		this.model = (ctrl != null) ? ctrl.getModel() : null;
		super.updateController(ctrl);
	}

	//-------------------------------------------------------------------
	@Override
	protected void onAdd() {
		if (control != null && control.getSpellController() != null) {
			SR6SelectAndDecide.of(control, control.getSpellController(), SR6Spell.class, SR6Spell.class)
					.title(ResourceI18N.get(SR6CharacterViewLayout.UI, "section.spell.selector.title"))
					.execute();
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void refresh() {
		super.refresh();
		if (cbTradition.getItems().isEmpty()) {
			cbTradition.getItems().setAll(Shadowrun6Core.getItemList(Tradition.class));
		}
		if (model != null && cbTradition.getValue() != model.getTradition()) {
			cbTradition.setValue(model.getTradition());
		}
	}

}
