package de.rpgframework.shadowrun6.comlink;

import java.util.Locale;
import java.util.logging.LogManager;

import de.rpgframework.eden.client.jfx.EdenSettings;

public class ComLinkStarter {

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		EdenSettings.setupDirectories("CommLink6");
		System.out.println("Locale = "+EdenSettings.getPreferredLangauge("CommLink6"));
		Locale.setDefault(EdenSettings.getPreferredLangauge("CommLink6"));
		Locale.setDefault(Locale.GERMAN);
		ComLinkMain.main(args);
	}

}
