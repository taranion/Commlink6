package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;

import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.GearTool;

/**
 * @author prelle
 *
 */
public class SR6GearTool extends GearTool {
	
	public final static Logger logger = System.getLogger(ApplyStockModificationsStep.class.getPackageName());

	public static CarriedItemProcessor[] SR6_PHASE1_STEPS = new CarriedItemProcessor[] {
			new ApplyStockModificationsStep(),
			new SR6ResolveTemplatesStep()
	};

	public static CarriedItemProcessor[] SR6_PHASE2_STEPS = new CarriedItemProcessor[] {
			new HandleAugmentationGradeStep(),
			new AddUpPricesStep()
	};

}
