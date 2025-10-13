package de.rpgframework.shadowrun6.comlink;

import java.util.Locale;
import java.util.logging.LogManager;

import de.rpgframework.eden.client.jfx.EdenSettings;
import de.rpgframework.eden.client.jfx.EdenSettingsPage;
import de.rpgframework.eden.client.jfx.UISettingsSection;

public class ComLinkStarter {

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		EdenSettings.setupDirectories("CommLink6");
		System.out.println("Locale = "+EdenSettings.getPreferredLangauge("CommLink6"));
		Locale.setDefault(EdenSettings.getPreferredLangauge("CommLink6"));
		//System.setProperty("section.nontransparent", "true");
		ComLinkMain.main(args);
	}

}
