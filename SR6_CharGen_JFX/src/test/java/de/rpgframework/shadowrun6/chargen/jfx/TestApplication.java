package de.rpgframework.shadowrun6.chargen.jfx;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Predicate;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigationPane;
import org.prelle.javafx.Page;
import org.prelle.javafx.ResponsiveControlManager;

import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.priority.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemTypeFilter;
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
		Locale.setDefault(Locale.ENGLISH);
		System.setProperty("logdir","/tmp");
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
		ResponsiveControlManager.manageResponsiveControls(scene);
		ResponsiveControlManager.initialize((Region) scene.getRoot());
		stage.setMinWidth(1400);
		stage.setMinHeight(860);
		
		Predicate<ItemTemplate> selectFilter = new ItemTypeFilter(CarryMode.IMPLANTED, ItemType.CYBERWARE); 
		ItemTemplateSelector selector = new ItemTemplateSelector(chargen, CarryMode.IMPLANTED, selectFilter, null, null);
		ManagedDialog dialog = new ManagedDialog("Select an item", selector, CloseType.OK, CloseType.CANCEL);
		CloseType closed = FlexibleApplication.getInstance().showAndWait(dialog);
		if (closed==CloseType.CANCEL)
			System.exit(0);
		
		ItemTemplate item = selector.getSelected();
		ChoiceSelectorDialog<ItemTemplate, CarriedItem<ItemTemplate>> dialog2 = new ChoiceSelectorDialog<>(chargen.getEquipmentController(), CarryMode.IMPLANTED);
		dialog2.apply(item, item.getChoices());
		closed = FlexibleApplication.getInstance().showAndWait(dialog2);
		if (closed==CloseType.CANCEL)
			System.exit(0);
		
    }

}
