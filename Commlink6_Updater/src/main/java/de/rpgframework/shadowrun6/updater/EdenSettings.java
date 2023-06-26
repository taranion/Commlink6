package de.rpgframework.shadowrun6.updater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.Optional;
import java.util.prefs.Preferences;

import com.gluonhq.attach.settings.SettingsService;
import com.gluonhq.attach.storage.StorageService;

/**
 * @author prelle
 *
 */
public class EdenSettings {

	public static String PREF_BASEDIR = "eden.basedir";

	public static Path appDir;
	public static Path logDir;

	//-------------------------------------------------------------------
	public static Locale getPreferredLangauge(String appName) {
		String key = appName.toLowerCase()+".lang";
		// Tell generic packages which key to use
		System.setProperty("eden.langkey", key);
		System.out.println("a "+System.currentTimeMillis());
//		if (SettingsService.create().isPresent()) {
//			System.out.println("b "+System.currentTimeMillis());
//			System.out.println("Found SettingsService");
//			String val = SettingsService.create().get().retrieve(key);
//			if (val!=null) {
//				System.err.println("Language settings is "+val);
//				return Locale.forLanguageTag(val);
//			}
//			System.err.println("No language settings");
//			return Locale.getDefault();
//		} else {
//			System.out.println("c "+System.currentTimeMillis());
//			System.out.println("Did not find SettingsService");
//			Preferences pref = Preferences.userRoot().node(appName);
//			System.err.println("Language settings is "+pref.get(key,null));
//
//			return Locale.forLanguageTag(pref.get(key, Locale.getDefault().getLanguage()));
//		}
		Optional<SettingsService> storage = SettingsService.create();
		System.out.println("a2 "+System.currentTimeMillis());
		SettingsService settings = storage.orElse(null);
		if (settings!=null) {
			System.out.println("b "+System.currentTimeMillis());
			System.out.println("Found SettingsService");
			String val = settings.retrieve(key);
			if (val!=null) {
				System.out.println("Language settings is "+val);
				return Locale.forLanguageTag(val);
			}
			System.err.println("No language settings");
			return Locale.getDefault();
		} else {
			System.out.println("c "+System.currentTimeMillis());
			System.out.println("Did not find SettingsService");
			Preferences pref = Preferences.userRoot().node(appName);
			System.out.println("Language settings is "+pref.get(key,null));

			return Locale.forLanguageTag(pref.get(key, Locale.getDefault().getLanguage()));
		}
	}

	//-------------------------------------------------------------------
	private static File getParentDirectory() {
		Optional<StorageService> storage = StorageService.create();
		if (!storage.isPresent()) {
			System.err.println("No StorageService found");
			return new File(System.getProperty("user.home"));
		}
		StorageService service = storage.get();
//		System.out.println("isExternalStorageReadable: "+service.isExternalStorageReadable());
//		System.out.println("isExternalStorageWritable: "+service.isExternalStorageWritable());

		// Check main directory
		Optional<File> file = service.getPublicStorage("");
		if (file.isPresent()) {
			// Check if main directory can be used
			if (file.get().canWrite()) {
//				System.out.println("Use public storage root as parent directory: "+file.get());
				return file.get();
			}
			// Check for "Documents", "Dokumente" or "docs
			for (String dir : new String[]{ "Documents","Dokumente","docs"}) {
				Optional<File> sub = service.getPublicStorage(dir);
				if (sub.isPresent() && sub.get().canWrite()) {
					System.out.println("Use public storage subdirectory '"+sub.get()+"' as parent directory");
					return sub.get();
				}
			}

			// Search ANY writable directory
			for (File sub : file.get().listFiles()) {
				if (sub.canWrite()) {
					System.out.println("Oh boy - I can't help but pick the next best writeable directory as parent: "+file.get());
					return file.get();
				}
			}

		} else {
			System.err.println("No Public Storage found");
		}
		return new File(System.getProperty("user.home"));
	}

	//-------------------------------------------------------------------
	private static boolean setupUsingPrivateStorage(String appName) {
		File parent = getParentDirectory();
		appDir = parent.toPath().resolve(appName);
		try {
			if (!Files.exists(appDir))
				Files.createDirectory(appDir);
		} catch (IOException e) {
			System.out.println("  Error creating directory : "+e);
			return false;
		}
		return true;
	}

	//-------------------------------------------------------------------
	public static void setupDirectories(String appName) {
		setupUsingPrivateStorage(appName);

		System.out.println("Application directory: "+appDir+" is writeable="+Files.isWritable(appDir));
		System.setProperty(PREF_BASEDIR, appDir.toAbsolutePath().toString());

		// Setup log Dir
		logDir = appDir.resolve("logs");

		try {
//			System.out.println("Existing permissions: "+Files.getPosixFilePermissions(appDir));
			if (!Files.exists(appDir)) {
				Files.createDirectory(appDir, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx")));
			}
			if (!Files.exists(logDir)) {
				Files.createDirectory(logDir, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("setupDirectories() done");
		}
	}

}
