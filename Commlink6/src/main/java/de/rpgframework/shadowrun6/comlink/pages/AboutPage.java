package de.rpgframework.shadowrun6.comlink.pages;

import java.io.File;
import java.lang.System.Logger.Level;
import java.util.Locale;

import org.prelle.javafx.PagePile;

import com.gluonhq.attach.util.Platform;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.eden.client.jfx.EdenClientApplication;
import de.rpgframework.eden.client.jfx.EdenDebugPage;
import de.rpgframework.eden.client.jfx.EdenSettingsPage;
import de.rpgframework.eden.client.jfx.PDFPage;
import de.rpgframework.jfx.attach.PDFViewerServiceFactory;

/**
 * @author prelle
 *
 */
public class AboutPage extends PagePile {

	private EdenSettingsPage pgSettings;
	private PDFPage pgPDF;
	private InfoPage pgInfo;
	private CopyrightPage pgCopy;
	private EdenDebugPage pgDebug;

	//-------------------------------------------------------------------
	public AboutPage(File[] directories, EdenClientApplication app, RoleplayingSystem rules) {
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage<init>");
		pgSettings = new EdenSettingsPage(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.forLanguageTag("pt"));
//		pgSettings.addSettingsOneLine(ComLinkMain.RES ,"page.about.settings.style", cbStyle);

		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage, calling PDFage<init>");
		pgPDF = new PDFPage(app, rules);
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage, calling InfoPage<init>");
		pgInfo = new InfoPage();
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage, calling CopyrightPage<init>");
		pgCopy  = new CopyrightPage();
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage, calling EdenDebugPage<init>");
		pgDebug = new EdenDebugPage(directories);
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage, after creating tabs");

		getPages().addAll(pgSettings, pgPDF, pgInfo, pgCopy, pgDebug);
		if (!Platform.isDesktop() || !PDFViewerServiceFactory.create().isPresent()) {
			getPages().remove(pgPDF);
		}
//		setStartPage(pgInfo);
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage, calling EdenDebugPage.refrsh");
		pgDebug.refresh();

		visibleProperty().addListener( (ov,o,n) -> pgDebug.refresh());
		System.getLogger(AboutPage.class.getPackageName()).log(Level.INFO, "AboutPage<init> done");
	}

}
