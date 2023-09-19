package de.rpgframework.shadowrun6.chargen.ai;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.prelle.simplepersist.Persister;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.chargen.IRecommender;
import de.rpgframework.genericrpg.chargen.ai.LevellingProfile;
import de.rpgframework.genericrpg.chargen.ai.LevellingProfileList;
import de.rpgframework.genericrpg.chargen.ai.WeighedModification;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class SR6AITool {

	private final static Logger logger = System.getLogger("splittermond.ai");

	private final static Random random = new Random();
	private final static Persister serializer = new Persister();

	private static List<LevellingProfile> profiles = new ArrayList<LevellingProfile>();

	//-------------------------------------------------------------------
	public static void initialize() {
		DataSet core = new DataSet(new SR6AITool(), RoleplayingSystem.SPLITTERMOND, "profiles", null, Locale.GERMAN);
		Class<SR6AITool> clazz = SR6AITool.class;
		try {
			List list = Shadowrun6Core.loadDataItems(LevellingProfileList.class, LevellingProfile.class, core, clazz.getResourceAsStream("profiles.xml"));
			logger.log(Level.DEBUG, "Loaded "+list.size()+" profiles");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------
	public static List<LevellingProfile> getProfiles() {
		return new ArrayList<LevellingProfile>(profiles);
	}

	//-------------------------------------------------------------------
	public static LevellingProfile getProfile(String id) {
		for (LevellingProfile prof : profiles) {
			if (prof.getId().equals(id))
				return prof;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public static List<WeighedModification> convertToWeighedModification(IRecommender recommender, Modification choice) {
		// Weigh all modifications
		List<WeighedModification> weighed = new ArrayList<>();
//		for (Modification tmp2 : choice.getOptionList()) {
//			if (tmp2 instanceof SkillModification) {
//				SkillModification mod = (SkillModification)tmp2;
//				if (recommender.isRecommended(mod.getSkill())) {
//					weighed.add(new WeighedModification(mod, mod.getValue() * recommender.getRecommendation(mod.getSkill()).level.ordinal()));
//				} else
//					weighed.add(new WeighedModification(mod, 0));
//			} else
//				if (tmp2 instanceof ValueModification) {
//					ValueModification mod = (ValueModification)tmp2;
//					switch ( (SplittermondReference)mod.getReferenceType()) {
//					case ATTRIBUTE:
//						Attribute attribute = Attribute.valueOf(mod.getKey());
//						if (recommender.isRecommended(attribute)) {
//							weighed.add(new WeighedModification(mod, mod.getValue() * recommender.getRecommendation(attribute).level.ordinal()));
//						} else
//							weighed.add(new WeighedModification(mod, 0));
//						break;
//					default:
//						logger.warn("Unsupported for recommendation: "+mod);
//					}
//				}
//		}					

		return weighed;
	}

	//-------------------------------------------------------------------
	public static List<Modification> makeDecision(IRecommender recommender, Modification choice) {
		List<WeighedModification> weighed = convertToWeighedModification(recommender, choice);
		Collections.sort(weighed);
		Collections.reverse(weighed);

		List<Modification> decided = new ArrayList<Modification>();
//		if (choice.getValues()!=null && choice.getValues().length>1) {
//			// Spent fix values on possible skills
//			if (choice.getOptions()[0] instanceof SkillModification) {
//				// Sort all options by recommendation value
//				List<Skill> chooseFrom = new ArrayList<Skill>();
//				for (Modification mod : choice.getOptionList()) {
//					chooseFrom.add(  ((SkillModification)mod).getSkill() );
//				}
//				Collections.sort(chooseFrom, recommender);
//
//				logger.warn("Sorted: "+chooseFrom);
//				for (Integer toSet : choice.getValues()) {
//					logger.warn("TO spend "+toSet+" on "+chooseFrom.get(0));
//					decided.add(new SkillModification(chooseFrom.remove(0), toSet));
//				}
//				logger.log(Level.INFO, "Decided for "+decided+" from choice "+choice);
//			} else {
//				logger.warn("Don't know how to spend points for "+choice.getOptions()[0].getClass());
//			}
//		} else {
//			if (choice.getOptionList().size()==0) {
//				logger.log(Level.ERROR, "Cannot select from empty option list: "+choice);
//			} else {
//				for (int i=0; i<choice.getNumberOfChoices(); i++) {
//					if (i<weighed.size()) {
//						// Still have a recommended option left
//						decided.add(weighed.get(i).mod);
//					} else {
//						// No recommendation - pick any
//						Modification randMod = null;
//						int maxLoop = 5;
//						do {
//							maxLoop--;
//							randMod = choice.getOptionList().get(random.nextInt(choice.getOptionList().size()));
//						} while (decided.contains(randMod) && maxLoop>0); // Not previously selected
//						if (!decided.contains(randMod)) {
//							logger.log(Level.INFO, "Random pick: "+randMod+" from "+choice);
//							decided.add(randMod);
//						} else {
//							logger.warn("Cannot make a random pick from "+choice+" because already picked "+decided);
//						}
//					}
//				}
//			}
//		}
		return decided;
	}


}
