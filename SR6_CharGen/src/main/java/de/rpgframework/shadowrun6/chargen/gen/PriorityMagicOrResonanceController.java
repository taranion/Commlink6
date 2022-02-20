package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PriorityMagicOrResonanceController extends MagicOrResonanceController {
	
	private final static Logger logger = System.getLogger(PriorityMagicOrResonanceController.class.getPackageName());

	protected Map<MagicOrResonanceType, Integer> available;
	
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
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#getAvailable()
	 */
	public List<MagicOrResonanceType> getAvailable() {
		return new ArrayList<>(available.keySet());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#selectTradition(de.rpgframework.shadowrun.Tradition)
	 */
	@Override
	public void selectTradition(Tradition value) {
		logger.log(Level.INFO, "select magic tradition: {}", value);
		model.setTradition(value);
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(Tradition item) {
		// TODO Auto-generated method stub
		return RecommendationState.NEUTRAL;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.WARNING,"process()");
		List<Modification> unprocessed = new ArrayList<>();
		
		// Clear old available information
		available.clear();
		
		// Check for options
		for (Modification tmp : previous) {
			if (tmp instanceof ValueModification) {
				ValueModification mod = (ValueModification)tmp;
				if (mod.getReferenceType()==ShadowrunReference.MAGIC_RESO) {
					available.put(mod.getResolvedKey(), mod.getValue());
					logger.log(Level.INFO, "Allow "+mod.getKey()+" with "+mod.getValue()+" points in attribute");
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
			if (points==null) points=0;
			// Grant MAGIC or Resonance points
			if (type.usesMagic()) {
				logger.log(Level.INFO, "Selected "+type.getId()+" grants "+points+" MAGIC");
				unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), points));
			} else if (type.usesResonance()) {
				logger.log(Level.INFO, "Selected "+type.getId()+" grants "+points+" RESONANCE");
				unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), points));
			}
			// For mystic adepts
			SR6PrioritySettings sett = (SR6PrioritySettings)model.getCharGenSettings(SR6PrioritySettings.class);
			sett.mysticAdeptMaxPoints = points;
			sett.mysticAdeptPowerPoints = Math.max(points, sett.mysticAdeptPowerPoints);
		}
		
		return unprocessed;
	}

}
