package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.section.IconGridCell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.AvailableSlot;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author stefa
 *
 */
public class ActiveProgramsSection extends GearSection {

	private final static Logger logger = System.getLogger(ActiveProgramsSection.class.getPackageName());

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(ActiveProgramsSection.class.getPackageName()+".Section");
	private final static int SIZE = 64;
	
	private ChoiceBox<CarriedItem<ItemTemplate>> cbDevice;
	private GridView<CarriedItem<ItemTemplate>> grid;
	private int programSlots;

	private transient boolean refreshing;
	private SelectionModel<CarriedItem<ItemTemplate>> selectionModel;
	private List<IconGridCell<ItemTemplate,CarriedItem<ItemTemplate>>> allCells = new ArrayList<>();
	
	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param selectFilter
	 * @param showFilter
	 */
	public ActiveProgramsSection(String title, Predicate<ItemTemplate> selectFilter,
			Predicate<CarriedItem<ItemTemplate>> showFilter) {
		super(title, CarryMode.EMBEDDED, selectFilter, showFilter);
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<CarriedItem<ItemTemplate>> selectedDeviceProperty() {
		return cbDevice.getSelectionModel().selectedItemProperty();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		selectionModel = new SingleSelectionModel<CarriedItem<ItemTemplate>>() {

			@Override
			protected CarriedItem<ItemTemplate> getModelItem(int index) {
				return grid.getItems().get(index);
			}

			@Override
			protected int getItemCount() {
				return grid.getItems().size();
			}
		};
		
		cbDevice = new ChoiceBox<>();
		cbDevice.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			@Override
			public String toString(CarriedItem<ItemTemplate> item) {
				return (item==null)?"?": item.getNameWithoutRating(Locale.getDefault());
			}
			@Override
			public CarriedItem<ItemTemplate> fromString(String arg0) { return null;}
		});
		
		grid = new GridView<>();
		grid.setHorizontalCellSpacing(10);
		grid.setVerticalCellSpacing(10);
		grid.setCellWidth(SIZE+4);
		grid.setCellHeight(SIZE+16);
		grid.setCellFactory( gv -> {
			IconGridCell<ItemTemplate,CarriedItem<ItemTemplate>> cell = new IconGridCell<ItemTemplate,CarriedItem<ItemTemplate>>(
				selectionModel,
				temp -> resolveIcon(temp));
			allCells.add(cell);
			return cell;
		});

	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label hdDevice = new Label(ResourceI18N.get(RES, "section.active_programs.device"));
		hdDevice.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		HBox bxDevice = new HBox(10, hdDevice, cbDevice);
		bxDevice.setAlignment(Pos.CENTER_LEFT);
		
		setHeaderNode(bxDevice);
		
		VBox box = new VBox(5, bxDevice, grid);
		box.setMaxHeight(Double.MAX_VALUE);
		list.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(list, Priority.ALWAYS);
		setContent(box);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbDevice.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> refreshPrograms());
		showHelpFor.unbind();
		selectionModel.selectedIndexProperty().addListener( (ov,o,n) -> {
			for (IconGridCell<ItemTemplate,CarriedItem<ItemTemplate>> cell : allCells) {
				if (cell.getIndex()==(int)o) cell.pseudoClassStateChanged(IconGridCell.PSEUDO_CLASS_SELECTED, false);
			}
//			list.getSelectionModel().select((int)n);
			showHelpFor.set(grid.getItems().get((int)n));
		});
	}

	//-------------------------------------------------------------------
	public void refresh() {
		System.err.println("ActivePrograms.refresh: model="+model);
		if (model==null) return;
		if (refreshing) return;
		
		refreshing = true;

		try {
			CarriedItem<ItemTemplate> prevSelected = cbDevice.getValue();
			List<CarriedItem<ItemTemplate>> devices = ((Shadowrun6Character)model).getCarriedItems()
				.stream()
				.filter(d -> d.hasFlag(SR6ItemFlag.MATRIX_DEVICE.name()))
				.collect(Collectors.toList());
			cbDevice.getItems().setAll(devices);
			if (prevSelected!=null && devices.contains(prevSelected)) {
				cbDevice.setValue(prevSelected);
			} else {
				if (devices.isEmpty()) cbDevice.setValue(null);
				else cbDevice.setValue(devices.get(0));
			}
			
			// Update programs
			refreshPrograms();
			
//			super.refresh();
		} finally {
			refreshing = false;
		}
	}

	//-------------------------------------------------------------------
	public void refreshPrograms() {
		programSlots = 0;
		List<CarriedItem<ItemTemplate>> slotted = new ArrayList<>();
		// Update programs
		CarriedItem<ItemTemplate> selected = cbDevice.getValue();
		if (selected==null) {
			grid.getItems().clear();			
			list.getItems().clear();
		} else {
			AvailableSlot slot = selected.getSlot(ItemHook.SOFTWARE);
			logger.log(Level.DEBUG, "Slot = " + slot);

			if (slot != null) {
//				secPrograms.setSlots((int)slot.getCapacity());
				logger.log(Level.WARNING, "Embedded in slot = " + slot.getAllEmbeddedItems());
				slotted.addAll(slot.getAllEmbeddedItems());
			} else {
				logger.log(Level.ERROR, "Item {0} has program slots, but no SOFTWARE slot", selected.getKey());
			}
		}
		
		while (slotted.size()<programSlots) slotted.add(null);
		grid.getItems().setAll(slotted);
		list.getItems().setAll(slotted);

		super.refresh();
	}

	//-------------------------------------------------------------------
	private Image resolveIcon(ItemTemplate item) {
		String file = "icons/"+item.getId()+".png";
//		logger.log(Level.INFO, "Resolve "+file);
		InputStream is = Shadowrun6DataPlugin.class.getResourceAsStream(file);
//		logger.log(Level.INFO, "Stream = "+is);
		if (is==null) {
			logger.log(Level.ERROR, "Missing icon for program: "+file);
			return null;
		} else {
			return new Image(is);
		}
	}

}
