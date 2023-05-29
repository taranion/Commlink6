package de.rpgframework.shadowrun6.chargen.gen.free;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class FreeMagicOrResonanceController extends MagicOrResonanceController {

	private final static Logger logger = System.getLogger(FreeMagicOrResonanceController.class.getPackageName());

	private List<MagicOrResonanceType> available;

	// -------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public FreeMagicOrResonanceController(IShadowrunCharacterGenerator parent) {
		super(parent);
		// Build available
		available  = new ArrayList<>(Shadowrun6Core.getItemList(MagicOrResonanceType.class));
		Collections.sort(available, new Comparator<MagicOrResonanceType>() {
			public int compare(MagicOrResonanceType arg0, MagicOrResonanceType arg1) {
				return Collator.getInstance().compare(arg0.getName(), arg1.getName());
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#getAvailable()
	 */
	@Override
	public List<MagicOrResonanceType> getAvailable() {
		return available;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#selectTradition(de.rpgframework.shadowrun.Tradition)
	 */
	@Override
	public void selectTradition(Tradition value) {
		logger.log(Level.INFO, "select magic tradition: {0}", value);
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

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.DEBUG, "ENTER process()");
		List<Modification> unprocessed = previous;

		try {
			MagicOrResonanceType type = model.getMagicOrResonanceType();
			if (type != null) {
				logger.log(Level.INFO, "Chose '"+type.getId());

				switch (type.getId()) {
				case "magician":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1, type, ValueType.NATURAL));
					break;
				case "adept":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1, type, ValueType.NATURAL));
					break;
				case "mysticadept":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1, type, ValueType.NATURAL));
					break;
				case "aspectedmagician":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1, type, ValueType.NATURAL));
					SR6Skill skill =  ((Shadowrun6Character)model).getAspectSkill();
					if (skill!=null) {
						AllowModification allow = new AllowModification(ShadowrunReference.SKILL, model.getAspectSkillId());
						allow.setSource(type);
						unprocessed.add(allow);
					}
					break;
				case "technomancer":
					// Start with 1 point in RESONANCE
					unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), 1, type, ValueType.NATURAL));
					break;
				}

				//unprocessed.addAll(type.getModifications());
			}

			return unprocessed;
		} finally {
			logger.log(Level.DEBUG, "LEAVE process()");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#getCost(de.rpgframework.shadowrun.MagicOrResonanceType)
	 */
	@Override
	public int getCost(MagicOrResonanceType morType) {
		return morType.getCost();
	}

}
