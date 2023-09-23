package de.rpgframework.shadowrun6.chargen.ai;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.prelle.simplepersist.Persister;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.chargen.ai.LevellingProfile;
import de.rpgframework.genericrpg.chargen.ai.LevellingProfileList;
import de.rpgframework.genericrpg.data.DataSet;
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

}
