package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger;
import java.util.List;

import de.rpgframework.genericrpg.items.CarriedItemProcessor;
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.items.Legality;

/**
 * @author prelle
 *
 */
public class SR6GearTool extends GearTool {
	
	public final static Logger logger = System.getLogger(ApplyStockModificationsStep.class.getPackageName());

	public static CarriedItemProcessor[] SR6_PHASE1_STEPS = new CarriedItemProcessor[] {
			new SR6ResolveFormulasStep(),
			new ApplyStockModificationsStep(),
			new SR6ResolveTemplatesStep()
	};

	public static CarriedItemProcessor[] SR6_PHASE2_STEPS = new CarriedItemProcessor[] {
//			new DetermineStandardEssenceStep(),
			new DeriveCapacityAttributeStep(),
			new HandleAugmentationGradeStep(),
			new AddUpPricesStep()
	};

	public static Availability calculateModifiedValue(Availability base, List<Modification> mods) {
		Availability ret = new Availability(base.getValue(), base.getLegality(), false);
		for (Modification tmp : mods) {
			if (tmp instanceof ValueModification) {
				ValueModification mod = (ValueModification)tmp;
				ret.setValue(ret.getValue() + mod.getValue());
			}
		}
		
		return ret;
	}
}
