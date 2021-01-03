package de.rpgframework.shadowrun6.chargen.jfx.fxml;

import java.io.IOException;
import java.util.ResourceBundle;

import org.prelle.javafx.ExtendedComponentBuilderFactory;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.Page;

import de.rpgframework.shadowrun6.Spell;
import de.rpgframework.shadowrun6.chargen.jfx.CharacterOverviewController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.SpellDescriptionPageController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class ScreenLoader {

	//-------------------------------------------------------------------
	public static SR6CharacterViewLayout loadMainScreen() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				ScreenLoader.class.getResource("CharacterOverview.fxml"),
				ResourceBundle.getBundle("de.rpgframework.shadowrun6.chargen.jfx.CharacterOverview")
				);
		FXMLLoader.setDefaultClassLoader(FlexibleApplication.class.getClassLoader());
		loader.setBuilderFactory(new ExtendedComponentBuilderFactory());
		SR6CharacterViewLayout ret = loader.load();
		((CharacterOverviewController)loader.getController()).setComponent(ret);
		return ret;
	}

//	//-------------------------------------------------------------------
//	public static Page loadAttributesPage() throws IOException {
//		FXMLLoader loader = new FXMLLoader(
//				ScreenLoader.class.getResource("AttributesPage.fxml"),
//				ResourceBundle.getBundle("org.prelle.splittermond.chargen.jfx.AttributesPage")
//				);
//		FXMLLoader.setDefaultClassLoader(ScreenLoader.class.getClassLoader());
//		loader.setBuilderFactory(new ExtendedComponentBuilderFactory());
//		Page ret = loader.load();
//		((AttributesPageController)loader.getController()).setComponent(ret);
//		return ret;
//	}

	//-------------------------------------------------------------------
	public static VBox loadSpellDescriptionVBox(Spell data) throws IOException {
		FXMLLoader loader = new FXMLLoader(
				ScreenLoader.class.getResource("SpellDescription.fxml"),
				ResourceBundle.getBundle("org.prelle.splittermond.chargen.jfx.SpellDescriptionPage")
				);
		FXMLLoader.setDefaultClassLoader(ScreenLoader.class.getClassLoader());
		loader.setBuilderFactory(new ExtendedComponentBuilderFactory());
		VBox ret = loader.load();
		((SpellDescriptionPageController)loader.getController()).setData(data);
		return ret;
	}

//	//-------------------------------------------------------------------
//	public static Page loadSpellDescriptionPage(Spell data) throws IOException {
//		FXMLLoader loader = new FXMLLoader(
//				ScreenLoader.class.getResource("SpellDescriptionPage.fxml"),
//				ResourceBundle.getBundle("org.prelle.splittermond.chargen.jfx.SpellDescriptionPage")
//				);
//		FXMLLoader.setDefaultClassLoader(ScreenLoader.class.getClassLoader());
//		loader.setBuilderFactory(new ExtendedComponentBuilderFactory());
//		Page ret = loader.load();
//		ret.setTitle(data.getName());
//		((SpellDescriptionPageController)loader.getController()).setData(data);
//		return ret;
//	}

//	//-------------------------------------------------------------------
//	public static Page loadMastershipDescriptionPage(Mastership data) throws IOException {
//		FXMLLoader loader = new FXMLLoader(
//				ScreenLoader.class.getResource("MastershipDescriptionPage.fxml"),
//				ResourceBundle.getBundle("org.prelle.splittermond.chargen.jfx.MastershipDescriptionPage")
//				);
//		FXMLLoader.setDefaultClassLoader(ScreenLoader.class.getClassLoader());
//		loader.setBuilderFactory(new ExtendedComponentBuilderFactory());
//		Page ret = loader.load();
//		((MastershipDescriptionPageController)loader.getController()).setData(data);
//		return ret;
//	}

}
