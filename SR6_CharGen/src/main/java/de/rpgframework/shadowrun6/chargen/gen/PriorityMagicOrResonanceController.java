package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceOption;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PriorityMagicOrResonanceController extends MagicOrResonanceController {
	
	private final static Logger logger = LogManager.getLogger(PriorityMagicOrResonanceController.class.getPackageName());

	private Map<MagicOrResonanceType, Integer> available;
	
	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public PriorityMagicOrResonanceController(IShadowrunCharacterGenerator parent) {
		super(parent);
		available = new LinkedHashMap<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.info("process()");
		List<Modification> unprocessed = new ArrayList<>();
		
		// Clear old available information
		available.clear();
		
		// Check for options
		for (Modification tmp : previous) {
			if (tmp instanceof ValueModification) {
				ValueModification mod = (ValueModification)tmp;
				if (mod.getReferenceType()==ShadowrunReference.MAGIC_RESO) {
					MagicOrResonanceType opt = mod.getResolvedKey();
					available.put(mod.getResolvedKey(), mod.getValue());
					logger.debug("Allow "+mod.getKey()+" with "+mod.getValue()+" points in attribute");
				} else {
					unprocessed.add(mod);
				}
			} else {
				unprocessed.add(tmp);
			}
		}
		
		
		
		MagicOrResonanceType type = model.getMagicOrResonanceType();
		if (type!=null) {
			Integer points = available.get(type);
			// Grant MAGIC or Resonance points
			if (type.usesMagic()) {
				logger.info("Selected "+type.getId()+" grants "+points+" MAGIC");
				unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), points));
			} else if (type.usesResonance()) {
				logger.info("Selected "+type.getId()+" grants "+points+" RESONANCE");
				unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), points));
			}
		}
		
		return unprocessed;
	}

}
