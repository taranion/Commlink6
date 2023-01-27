package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.gen.PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;

/**
 * @author prelle
 *
 */
public interface ISR6PointBuyGenerator extends SR6CharacterGenerator {

	//-------------------------------------------------------------------
	public SR6PointBuySettings getSettings();

	//-------------------------------------------------------------------
	public PointBuyAttributeGenerator getPointBuyAttributeController();

}
