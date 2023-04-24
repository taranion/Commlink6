package de.rpgframework.shadowrun6.chargen.jfx;

import java.util.Locale;

import org.prelle.javafx.FlexibleApplication;

import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.chargen.jfx.pane.ModSlotConfigPane;
import de.rpgframework.shadowrun6.chargen.jfx.pane.ModSlotConfigPane.ConversionEvent;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author prelle
 *
 */
public class ModSlotConfigPaneTest extends Application {

	//-------------------------------------------------------------------
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		ModSlotConfigPaneTest test = new ModSlotConfigPaneTest();
		launch(args);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		DataSet set = new DataSet(null, RoleplayingSystem.SHADOWRUN6, "dummy", null, Locale.ENGLISH);
		ItemTemplate vehicle = new ItemTemplate();
		vehicle.setId("test");
		vehicle.assignToDataSet(set);
		vehicle.setAttribute(SR6ItemAttribute.VEHICLE_MODSLOTS, new int[] {18,5,9});

		CarriedItem<ItemTemplate> carried = new CarriedItem<ItemTemplate>(vehicle, null, CarryMode.CARRIED);
		carried.setModificationSlotChanges(new int[] {4,0,-2});
		SR6GearTool.recalculate("", null, carried);
		ModSlotConfigPane pane = new ModSlotConfigPane();
		pane.setConfig(carried);
		pane.setOnConversion(new ModSlotConfigPane.ModSlotDefaultHandler(pane) {
			public void handle(ConversionEvent event) {
				super.handle(event);
				System.err.println("ModSlotConfigPaneTest: item changed");
			}
		});
//		pane.setOnConversion( cv -> {
//			System.err.println("Handled "+cv.getEventType());
//		});

		Scene scene = new Scene(pane, 240,240);
//        scene.getStylesheets().add(FlexibleApplication.DARK_STYLE);
////        scene.getStylesheets().add(de.rpgframework.jfx.Constants.class.getResource("css/rpgframework.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
