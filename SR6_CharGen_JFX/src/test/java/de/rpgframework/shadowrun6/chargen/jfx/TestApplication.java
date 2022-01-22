package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;

import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.NavigationPane;
import org.prelle.javafx.Page;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.ResponsiveControlManager;

import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
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
		return null; //new SR6CharacterViewLayout();
	}
	
    //-------------------------------------------------------------------
    /**
     * @throws IOException 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    public void start(Stage stage) throws Exception {
		stage.setMaxWidth(2400);
//		stage.setMaxHeight(900);
		stage.setMinWidth(400);
		stage.setMinHeight(560);
		super.start(stage);
		
		Scene scene = stage.getScene();
		setStyle(scene, DARK_STYLE);
        stage.getScene().getStylesheets().add(getClass().getResource("sr6test.css").toExternalForm());
		
        Shadowrun6Character model = new Shadowrun6Character();
        model.setName("Unnamed");
        SR6CharacterViewLayout screen = new SR6CharacterViewLayout();
        PriorityCharacterGenerator chargen = new PriorityCharacterGenerator();
        chargen.setModel(model, null);
        //KarmaCharacterGenerator karma = new KarmaCharacterGenerator(model, null);
        screen.handleControllerEvent(BasicControllerEvents.GENERATOR_CHANGED, chargen);
		openScreen(screen);
		ResponsiveControlManager.setBreakpoints(800, 1000);
		ResponsiveControlManager.manageResponsiveControls((Region) scene.getRoot());
		ResponsiveControlManager.initialize((Region) scene.getRoot());
     }

}
