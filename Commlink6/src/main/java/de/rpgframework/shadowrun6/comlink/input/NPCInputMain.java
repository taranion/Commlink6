package de.rpgframework.shadowrun6.comlink.input;

import java.util.Locale;

import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class NPCInputMain extends Application {

	//-------------------------------------------------------------------
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("logdir", "/home/prelle/commlink-logs");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		launch(args);
	}

	//-------------------------------------------------------------------
	/**
	 */
	public NPCInputMain() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		SR6NPC npc = new SR6NPC();
		NPCInputPane input = new NPCInputPane();
		input.setModel(npc);
		Scene scene = new Scene(input);
		stage.setScene(scene);
		stage.show();
	}

}
