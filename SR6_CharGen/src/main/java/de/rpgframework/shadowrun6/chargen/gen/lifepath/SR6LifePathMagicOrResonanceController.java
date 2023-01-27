package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.MagicOrResonanceOption;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.LifePathCharacterGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6LifePathMagicOrResonanceController extends MagicOrResonanceController {

	private MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(LifePathCharacterGenerator.class, Locale.ENGLISH, Locale.ENGLISH);

	private final static Logger logger = System.getLogger(SR6LifePathMagicOrResonanceController.class.getPackageName());

	protected List<MagicOrResonanceType> available;

	//-------------------------------------------------------------------
	/**
	 * @param parent
	 */
	public SR6LifePathMagicOrResonanceController(IShadowrunCharacterGenerator parent) {
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

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.MagicOrResonanceController#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		logger.log(Level.WARNING,"process()");
		List<Modification> unprocessed = new ArrayList<>(previous);


		MagicOrResonanceType type = model.getMagicOrResonanceType();
		if (type==null) {
			type = Shadowrun6Core.getItem(MagicOrResonanceType.class, "mundane");
			model.setMagicOrResonanceType(type);
		}

		logger.log(Level.INFO, "BORN THIS WAY: "+type);
		switch (type.getId()) {
		case "technomancer":
			unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.RESONANCE.name(), 1));
			break;
		case "magician":
		case "mysticadept":
		case "adept":
			unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 1));
			break;
		case "aspectedmagician":
			unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.MAGIC.name(), 2));
			break;
		case "mundane":
			unprocessed.add(new ValueModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.EDGE.name(), 1));
			break;
		}

		return unprocessed;
	}

	@Override
	public int getCost(MagicOrResonanceType morType) {
		// TODO Auto-generated method stub
		return 0;
	}

}
