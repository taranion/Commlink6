package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import de.rpgframework.genericrpg.chargen.CharacterControllerImpl;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityTableEntry;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 *
 */
public class SR6PriorityTableController extends PriorityTableController<Shadowrun6Character, SR6PrioritySettings> {

	private final static Map<PowerLevel, String> ALLOWED_BY_LEVEL = Map.of(
			PowerLevel.STANDARD    , "ABCDE",
			PowerLevel.STREET_LEVEL, "BCCDE",
			PowerLevel.ELITE       , "AABCD"
			);

	private PowerLevel oldLevel;
	private List<Priority> options;

	//--------------------------------------------------------------------
	public SR6PriorityTableController(CharacterControllerImpl<ShadowrunAttribute, Shadowrun6Character> parent, BiFunction<PriorityType, Priority, PriorityTableEntry> resolver) {
		super(parent, SR6PrioritySettings.class, resolver);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	private void updateOptions(PowerLevel level) {
		if (level==null) level = PowerLevel.STANDARD;
		String allowed = ALLOWED_BY_LEVEL.get(level);
		options = new ArrayList<>();
		for (char c : allowed.toCharArray()) {
			options.add( Priority.valueOf(String.valueOf(c)) );
		}
		logger.log(Level.INFO, "Options {0}",options);
	}

	//-------------------------------------------------------------------
	public void verifySettings() {
		SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
		if (oldLevel!=settings.variant) {
			updateOptions(settings.variant);
			oldLevel = settings.variant;
		}

		List<Priority> optionsLeft = new ArrayList<>(options);
		List<PriorityType> notFound = new ArrayList<>();
		for (Map.Entry<PriorityType, Priority> entry : settings.priorities.entrySet()) {
			boolean foundInOptions = optionsLeft.contains(entry.getValue());
			if (!foundInOptions) {
				logger.log(Level.WARNING, "Priority {0} for {1} not found in remaining options {2}", entry.getValue(), entry.getKey(), optionsLeft);
				notFound.add(entry.getKey());
			} else {
				optionsLeft.remove(entry.getValue());
			}
		}
		// For all options not found, pick the first option left
		for (PriorityType type : notFound) {
			Priority old  = settings.priorities.get(type);
			Priority prio = optionsLeft.get(0);
			settings.priorities.put(type, prio);
			logger.log(Level.INFO, "Replace {0}: {1} with {2}", type, old, prio);
		}
	}

	//--------------------------------------------------------------------
	public void setPriority(PriorityType option, Priority prio) {
		SR6PrioritySettings settings = parent.getModel().getCharGenSettings(SR6PrioritySettings.class);
		logger.log(Level.INFO, "Set priority {0} to {1}",option, prio);
		if (settings.variant==null) {
			super.setPriority(option, prio);
		} else {
			switch (settings.variant) {
			case STREET_LEVEL:
				if (prio==Priority.A) {
					logger.log(Level.ERROR, "Selected priority A, which is not allowed for Street Level");
					return;
				}
				break;
			case ELITE:
				if (prio==Priority.E) {
					logger.log(Level.ERROR, "Selected priority E, which is not allowed for Elite");
					return;
				}
				break;
			}
			super.setPriority(option, prio);
		}
	}

}
