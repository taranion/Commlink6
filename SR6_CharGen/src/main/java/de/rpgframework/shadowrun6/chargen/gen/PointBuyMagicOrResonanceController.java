package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceOption;
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
public class PointBuyMagicOrResonanceController extends MagicOrResonanceController {

	private final static Logger logger = System.getLogger(PointBuyMagicOrResonanceController.class.getPackageName());

	private Map<MagicOrResonanceType, Integer> available;

	// -------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public PointBuyMagicOrResonanceController(IShadowrunCharacterGenerator parent) {
		super(parent);
		available = new LinkedHashMap<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController#getAvailable()
	 */
	@Override
	public List<MagicOrResonanceType> getAvailable() {
		// TODO Auto-generated method stub
		return null;
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

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.DEBUG, "ENTER process()");
		List<Modification> unprocessed = previous;

		try {
			SR6PointBuySettings settings = (SR6PointBuySettings) model.getCharGenSettings(SR6PointBuySettings.class);
			MagicOrResonanceType type = model.getMagicOrResonanceType();
			if (type != null) {
				int cpCost = type.getCost();
				logger.log(Level.INFO, "Chose '"+type.getId()+"' for "+cpCost+" CP");
				settings.characterPoints -= cpCost;
				
				switch (type.getId()) {
				case "magician":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1));
					break;
				case "adept":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1));
					break;
				case "mysticadept":
					// Start with 1 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1));
					break;
				case "aspectdmagician":
					// Start with 2 point in MAGIC
					unprocessed.add(
							new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 2));
					break;
				case "technomancer":
					// Start with 1 point in RESONANCE
					unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE,
							ShadowrunAttribute.RESONANCE.name(), 1));
					break;
				}
				
				unprocessed.addAll(type.getModifications());
			}

			return unprocessed;
		} finally {
			logger.log(Level.DEBUG, "LEAVE process()");
		}
	}

}
