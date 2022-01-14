package de.rpgframework.shadowrun6.chargen.jfx;

import de.rpgframework.shadowrun.AShadowrunSkill;
import de.rpgframework.shadowrun.AShadowrunSkillValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTable;
import javafx.scene.control.Skin;

/**
 * @author prelle
 *
 */
public class PointBuyAttributeTable<S extends AShadowrunSkill, V extends AShadowrunSkillValue<S>, C extends ShadowrunCharacter<S,V>> extends ShadowrunAttributeTable<S,V,C>  {

	//-------------------------------------------------------------------
	public PointBuyAttributeTable(IShadowrunCharacterController<S, V, C> ctrl) {
		super(ctrl);
		expertModeAvailable.set(false);
	}

	//-------------------------------------------------------------------
	public Skin<?> createDefaultSkin() {
		return new PointBuyAttributeTableSkin(this);
	}

}
