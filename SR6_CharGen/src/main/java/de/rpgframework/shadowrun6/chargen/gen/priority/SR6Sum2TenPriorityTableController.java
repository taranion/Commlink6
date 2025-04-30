package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.util.function.BiFunction;

import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityTableEntry;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.SumToTenPriorityTableController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Rules;

/**
 *
 */
public class SR6Sum2TenPriorityTableController extends SumToTenPriorityTableController<Shadowrun6Character, SR6PrioritySettings> {

	//--------------------------------------------------------------------
	public SR6Sum2TenPriorityTableController(CharacterControllerImpl<ShadowrunAttribute, Shadowrun6Character> parent, BiFunction<PriorityType, Priority, PriorityTableEntry> resolver) {
		super(parent, SR6PrioritySettings.class, resolver);
		// TODO Auto-generated constructor stub
	}

	//--------------------------------------------------------------------
	public void setPriority(PriorityType option, Priority prio) {
		SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
		if (settings.variant!=null) {
			switch (settings.variant) {
			case STANDARD: max=10; break;
			case STREET_LEVEL: max=8; break;
			case ELITE: 
				//max=13; // 13 is only for DE 
				max = parent.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_SUM_TO_TEN_ELITE);
				break;
			}
		}
		super.setPriority(option, prio);
	}

}
