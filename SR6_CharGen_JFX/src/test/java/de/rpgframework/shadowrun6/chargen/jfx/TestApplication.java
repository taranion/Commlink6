package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;

import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.NavigationPane;
import org.prelle.javafx.Page;

import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class TestApplication extends FlexibleApplication {
	
	private MenuItem item;

	//-------------------------------------------------------------------
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		launch(args);
	}

	@Override
	public void populateNavigationPane(NavigationPane drawer) {
		item = new MenuItem("Pick me");
		drawer.getItems().add(item);
	}

	@Override
	public Page createPage(MenuItem menuItem) {
		// TODO Auto-generated method stub
		return new SR6CharacterSheet();
	}
	
    //-------------------------------------------------------------------
    /**
     * @throws IOException 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    public void start(Stage stage) throws Exception {
		stage.setMaxWidth(1400);
//		stage.setMaxHeight(900);
		stage.setMinWidth(360);
		stage.setMinHeight(560);
		super.start(stage);
		
		Scene scene = stage.getScene();
//		setStyle(scene, DARK_STYLE);
		
//        stage.getScene().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
     }

}
