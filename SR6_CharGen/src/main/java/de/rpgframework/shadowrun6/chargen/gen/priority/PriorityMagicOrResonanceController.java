package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PriorityMagicOrResonanceController extends MagicOrResonanceController {

	private final static Logger logger = System
			.getLogger(PriorityMagicOrResonanceController.class.getPackageName() + ".mor");

	protected Map<MagicOrResonanceType, Integer> available;

	// -------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public PriorityMagicOrResonanceController(IShadowrunCharacterGenerator<?, ?, ?,?> parent) {
		super(parent);
		available = new LinkedHashMap<>();
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#getAvailable()
	 */
	public List<MagicOrResonanceType> getAvailable() {
		return new ArrayList<>(available.keySet());
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#selectTradition(de.rpgframework.shadowrun.Tradition)
	 */
	@Override
	public void selectTradition(Tradition value) {
		logger.log(Level.INFO, "select magic tradition: {0}", value);
		model.setTradition(value);
		parent.runProcessors();
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.RecommendingController#getRecommendationState(java.lang.Object)
	 */
	@Override
	public RecommendationState getRecommendationState(Tradition item) {
		// TODO Auto-generated method stub
		return RecommendationState.NEUTRAL;
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.DEBUG, "process()");
		try {
			List<Modification> unprocessed = new ArrayList<>();

			// Clear old available information
			available.clear();

			Shadowrun6Character model = (Shadowrun6Character)getModel();
			boolean isAI = false;
			if (model.getMetatype()!=null) {
				isAI = model.getMetatype().isAI();
				logger.log(Level.ERROR, "Is "+model.getMetatype()+" an AI = "+isAI);
			}

			List<MagicOrResonanceType> forbidden = new ArrayList<>();
			// Check for options
			for (Modification tmp : previous) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification) tmp;
					if (mod.getReferenceType() == ShadowrunReference.MAGIC_RESO) {
						MagicOrResonanceType type = mod.getResolvedKey();
						if (forbidden.contains(type)) {
							logger.log(Level.ERROR, "Would allow {0}, but it has been forbidden",mod.getKey() );
						} else {
							available.put(type, mod.getValue());
							logger.log(Level.DEBUG, "Allow {0} with {1} points in attribute",mod.getKey(),mod.getValue() );
						}
					} else {
						unprocessed.add(mod);
					}
				} else if (tmp instanceof AllowModification) {
					AllowModification mod = (AllowModification)tmp;
					if (mod.getReferenceType() == ShadowrunReference.MAGIC_RESO) {
						MagicOrResonanceType type = mod.getResolvedKey();
						if (mod.isNegate()) {
							logger.log(Level.DEBUG, "Forbid {0} from {1}",mod.getKey(), available.keySet() );
							forbidden.add(type);
							available.remove(type);
						}
					} else {
						unprocessed.add(mod);
					}
				} else {
					unprocessed.add(tmp);
				}
			}
			logger.log(Level.ERROR, "Available = " + available.keySet());

			MagicOrResonanceType type = model.getMagicOrResonanceType();
			if (type == null || !available.containsKey(type)) {
				if (available.keySet().isEmpty()) {
					logger.log(Level.ERROR, "No resonance type set");
					return unprocessed;
				}
				type = available.keySet().iterator().next();
				logger.log(Level.WARNING, "Auto-select MOR type " + type);
				model.setMagicOrResonanceType(type);
			}

			Integer points = available.get(type);
			if (points == null)
				points = 0;
			// Grant MAGIC or Resonance points
			if (type.usesMagic()) {
				logger.log(Level.INFO, "Selected " + type.getId() + " grants " + points + " MAGIC");
				ValueModification val = new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), points);
				val.setSet(ValueType.NATURAL);
				val.setSource(type);
				unprocessed.add(val);
				// Only for regular adepts - get power points
			} else if (type.usesResonance()) {
				logger.log(Level.INFO, "Selected " + type.getId() + " grants " + points + " RESONANCE");
				ValueModification val = new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), points);
				val.setSet(ValueType.NATURAL);
				val.setSource(type);
				unprocessed.add(val);
			}
			// For mystic adepts
			SR6PrioritySettings sett = (SR6PrioritySettings) model.getCharGenSettings(SR6PrioritySettings.class);
			sett.mysticAdeptMaxPoints = points;
			if (type.usesMagic()) {
				sett.perAttrib.get(ShadowrunAttribute.MAGIC).base=points;
			} else {
				sett.perAttrib.get(ShadowrunAttribute.MAGIC).base=0;
			}
			if (type.usesResonance()) {
				sett.perAttrib.get(ShadowrunAttribute.RESONANCE).base=points;
			} else {
				sett.perAttrib.get(ShadowrunAttribute.RESONANCE).base=0;
			}
			sett.setMagicForPP( Math.min(points, sett.getMagicForPP()) );

			// For aspected magicians
			if ("aspectedmagician".equals(type.getId())) {
				SR6Skill skill =  ((Shadowrun6Character)model).getAspectSkill();
				if (skill!=null) {
					AllowModification allow = new AllowModification(ShadowrunReference.SKILL, model.getAspectSkillId());
					allow.setSource(type);
					unprocessed.add(allow);
				}
			}

			// Add modifications from choice
//			unprocessed.addAll(type.getModifications());

			return unprocessed;
		} finally {

		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#getCost(de.rpgframework.shadowrun.MagicOrResonanceType)
	 */
	@Override
	public int getCost(MagicOrResonanceType morType) {
		return 0;
	}

}
