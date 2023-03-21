package de.rpgframework.shadowrun6.comlink;

import java.util.Locale;
import java.util.logging.LogManager;

import de.rpgframework.eden.client.jfx.EdenSettings;

public class ComLinkUpdate4JStarter {

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		EdenSettings.setupDirectories("CommLink6");
		Locale.setDefault(EdenSettings.getPreferredLangauge());
		ComLinkMain main = new ComLinkMain();
		main.init();
	}

}
