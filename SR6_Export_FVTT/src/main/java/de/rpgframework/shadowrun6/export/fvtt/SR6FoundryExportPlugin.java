package de.rpgframework.shadowrun6.export.fvtt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import de.rpgframework.ConfigOption;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.export.CharacterExportPlugin;
import de.rpgframework.shadowrun6.Shadowrun6Character;

public class SR6FoundryExportPlugin implements CharacterExportPlugin<Shadowrun6Character> {

    /**
     */
    public SR6FoundryExportPlugin() {
    }

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.export.CharacterExportPlugin#getFileType()
	 */
	public String getFileType() {
		return ".json";
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
        return "Foundry FVTT Export";
    }

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.export.CharacterExportPlugin#getIcon()
	 */
	public byte[] getIcon() {
		InputStream is = SR6FoundryExportPlugin.class.getResourceAsStream("/Commlink_Export_FVTT.png");
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
    	return null;
    }
}
