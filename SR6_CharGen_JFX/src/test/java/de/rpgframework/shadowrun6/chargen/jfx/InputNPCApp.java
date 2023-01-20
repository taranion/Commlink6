package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.prelle.simplepersist.Persister;

import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.chargen.jfx.NPCParser.Result;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class InputNPCApp extends Application {

	private Button btnClear;
	private TextArea taBuffer;
	private TextArea taXML;

	private Persister persist;

	//-------------------------------------------------------------------
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		System.setProperty("logdir","/tmp");
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		launch(args);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() {
		persist = new Persister();

		btnClear = new Button("Reset");
		taBuffer = new TextArea() {
			public void paste() {
				super.paste();
				System.out.println("Pasted "+taBuffer.getText());
				Result res = NPCParser.parse(taBuffer.getText());
				SR6NPC npc = res.npc;
				StringWriter out = new StringWriter();
				try {
					persist.write(npc, out);
					out.append("\n"+String.join("\n", res.errors)+"\n");

					taXML.setText(out.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		taBuffer.setPrefColumnCount(50);
		taBuffer.setPrefRowCount(20);

		taXML = new TextArea();
		taXML.setPrefColumnCount(50);
		taXML.setPrefRowCount(20);

		btnClear.setOnAction(ev -> {
			taBuffer.clear();
			taXML.clear();
		});

	}

    //-------------------------------------------------------------------
    /**
     * @throws IOException
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    public void start(Stage stage) throws Exception {
    	VBox layout = new VBox(20, btnClear, new HBox(20, taBuffer, taXML));
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.setMinWidth(1400);
		stage.setMinHeight(600);

		stage.show();
    }

}
