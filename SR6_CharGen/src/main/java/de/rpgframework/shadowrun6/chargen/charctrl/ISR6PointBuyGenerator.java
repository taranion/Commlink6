package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.gen.PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PointBuySettings;

/**
 * @author prelle
 *
 */
public interface ISR6PointBuyGenerator {

	//-------------------------------------------------------------------
	public SR6PointBuySettings getSettings();

	//-------------------------------------------------------------------
	public PointBuyAttributeGenerator getPointBuyAttributeController();
	
}
