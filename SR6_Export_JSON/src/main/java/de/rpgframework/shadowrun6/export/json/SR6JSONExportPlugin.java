package de.rpgframework.shadowrun6.export.json;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import de.rpgframework.ConfigOption;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.export.CharacterExportPlugin;
import de.rpgframework.shadowrun6.Shadowrun6Character;

public class SR6JSONExportPlugin implements CharacterExportPlugin<Shadowrun6Character> {

    /**
     */
    public SR6JSONExportPlugin() {
    }

    //-------------------------------------------------------------------
    /**
     * @see de.rpgframework.genericrpg}.export.ExportPlugin#getRoleplayingSystem()
     */
    public RoleplayingSystem getRoleplayingSystem() {
        return RoleplayingSystem.SHADOWRUN6;
    }

    //-------------------------------------------------------------------
    /**
     * @see de.rpgframework.genericrpg.export.ExportPlugin#getName(java.util.Locale)
     */
    public String getName(Locale loc) {
        return "Generic JSON and Roll20 Export";
    }

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.export.CharacterExportPlugin#getIcon()
	 */
	public byte[] getIcon() {
		InputStream is = SR6JSONExportPlugin.class.getResourceAsStream("/Commlink_Export_Roll20.png");
		if (is!=null) {
			try {
				return is.readAllBytes();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

    //-------------------------------------------------------------------
    /**
     * @see de.rpgframework.genericrpg.export.CharacterExportPlugin#getConfiguration()
     */
    @Override
    public List<ConfigOption<?>> getConfiguration() {
        return List.of();
    }

    //-------------------------------------------------------------------
    /**
     * @see de.rpgframework.genericrpg.export.CharacterExportPlugin#createExport(de.rpgframework.character.RuleSpecificCharacterObject)
     */
    @Override
    public byte[] createExport(Shadowrun6Character charac) {
    	JSONSR6ExportService service = new JSONSR6ExportService();
    	String json = service.exportCharacter(charac);
    	return json.getBytes(Charset.forName("UTF-8"));
    }
}
