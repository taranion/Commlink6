package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/**
 * @author prelle
 *
 */
public class SoftwareLibrarySection extends GearSection {

	private final static Logger logger = System.getLogger(SoftwareLibrarySection.class.getPackageName());

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SoftwareLibrarySection.class.getPackageName()+".Section");
	
	static class ProgramRow {
		ItemSubType type;
		CarriedItem<ItemTemplate> program;
		Map<CarriedItem<ItemTemplate>, Boolean> installState = new LinkedHashMap<>();
		ToggleGroup group = new ToggleGroup();
		public ProgramRow(ItemSubType type) {this.type = type;}
		public ProgramRow(CarriedItem<ItemTemplate> prog) {
			if (prog==null) throw new NullPointerException();
			this.program = prog;
			group.setUserData(prog);
		}
		public String getName() {
			if (type!=null) return type.getName();
			if (program!=null) return program.getNameWithRating();
			return "?";
		}
	}
	static class ProgramRowDeviceCell extends TreeTableCell<ProgramRow, Boolean> {
		private ToggleButton button;
		private ImageView iView;
		private ProgramRow row;
		private SR6CharacterController control;
		
		public ProgramRowDeviceCell(TreeTableColumn<ProgramRow, Boolean> p, SR6CharacterController ctrl) {
			this.control = ctrl;
			button = new ToggleButton("x");
			button.getStyleClass().add("install-button");
			setAlignment(Pos.CENTER);
		}
		@SuppressWarnings("unchecked")
		@Override
		public void updateItem(Boolean item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setGraphic(null);
			} else {
				ProgramRow p = getTableRow().getItem();
				CarriedItem<ItemTemplate> dev = (CarriedItem<ItemTemplate>) getTableColumn().getUserData();
				// No buttons for devices without software slot
				if (dev.getSlot(ItemHook.SOFTWARE)==null) {
					setGraphic(null);
					return;
				}	
				
				if (p!=row && row!=null)
					row.group.getToggles().remove(button);
				if (p!=null && !p.group.getToggles().contains(button))
					p.group.getToggles().add(button);
				row = p;
				
				if (p==null || p.program==null) {
					setGraphic(null);
					return;
				}
				
				// Can it be installed here?
				CarriedItem<ItemTemplate> program = p.program;
				if (dev.getAccessories().contains(program)) {
					button.setDisable(false);
				} else {
					Possible possInstall = control.getEquipmentController().canBeEmbedded(dev, ItemHook.SOFTWARE,
							program.getModifyable(), program.getVariantID(),
							program.getDecisions().toArray(new Decision[program.getDecisions().size()]));
					button.setDisable(!possInstall.get());
					if (!possInstall.get())
						logger.log(Level.WARNING, "Cannot install {0} on {1} because {2}", program, dev,
								possInstall.toString());
				}
				
				setGraphic(button);
				if (dev!=null) {
					button.setUserData(dev);
					button.setSelected(item);
				}
			}
		}
	}

	private final static Predicate<ItemTemplate> SELECT_FILTER = (c) -> c.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue()==ItemType.SOFTWARE && c.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue()!=ItemSubType.SKILLSOFT ;
	private final static Predicate<CarriedItem<ItemTemplate>> SHOW_FILTER = new CarriedItemItemTypeFilter(CarryMode.EMBEDDED, ItemType.SOFTWARE);
		
	private List<CarriedItem<ItemTemplate>> currentlyShowing;
	private List<CarriedItem<ItemTemplate>> cacheDevices;
	
	private TreeTableView<ProgramRow> treeTable;
	private TreeItem<ProgramRow> root;
	
	private TreeTableColumn<ProgramRow, String> colName;
	private TreeTableColumn<ProgramRow, Boolean> colUnused;
	
	//-------------------------------------------------------------------
	public SoftwareLibrarySection() {
		super(ResourceI18N.get(RES, "section.software.title"), CarryMode.EMBEDDED, SELECT_FILTER, SHOW_FILTER);
		initTreeTable();
	}
	
	//-------------------------------------------------------------------
	private void initTreeTable() {
		currentlyShowing = new ArrayList<>();
		cacheDevices     = new ArrayList<>();
		treeTable = new TreeTableView<>();
		treeTable.setShowRoot(false);
		
		colName   = new TreeTableColumn<>("Name");
		colUnused = new TreeTableColumn<>("Unused");		
		treeTable.getColumns().add(colName);
		treeTable.getColumns().add(colUnused);
		colName.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().getName()));
		colUnused.setCellValueFactory(p -> new SimpleBooleanProperty(isUnused(p.getValue().getValue(), p.getTreeTableColumn())));
//		colUnused.setCellFactory(p -> new RadioButton("? "))));
		colUnused.setCellFactory(p -> new ProgramRowDeviceCell(p, control));
		root = new TreeItem<>();
		treeTable.setRoot(root);
		setContent(treeTable);
		
		// Interactivity
		showHelpFor.unbind();
		treeTable.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n==null || n.getValue()==null)
				showHelpFor.set(null);
			else
				showHelpFor.set(n.getValue().program);
		});
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private boolean isUnused(ProgramRow row, TreeTableColumn<ProgramRow, Boolean> col) {
		if (row.program==null) return true;
		CarriedItem<ItemTemplate> device = (CarriedItem<ItemTemplate>) col.getUserData();
		if (device!=null && device.getAccessories().contains(row.program)) return true;
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		if (model==null) return;
		if (root==null) return;
		addToContainer = model.getCarriedItem(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE);
		addToHook = ItemHook.SOFTWARE;
		colUnused.setUserData(addToContainer);
		
		List<CarriedItem<ItemTemplate>> data = null;
		data = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItemsRecursive())
		.stream()
		.filter(SHOW_FILTER)
		.collect(Collectors.toList());
		List<CarriedItem<ItemTemplate>> devices = ((List<CarriedItem<ItemTemplate>>)model.getCarriedItemsRecursive())
				.stream()
				.filter(ItemUtil.MATRIXDEVICES_FILTER)
				.collect(Collectors.toList());
		
		if (programsChanged(data)) {
			refreshPrograms(data);
		}
		if (devicesChanged(devices)) {
			refreshDevices(devices);
		}
		treeTable.refresh();
	}

	//-------------------------------------------------------------------
	private boolean devicesChanged(List<CarriedItem<ItemTemplate>> data) {
		if (data.equals(cacheDevices)) return false;
		logger.log(Level.WARNING, "devicesChanged\n"+data+"\n"+cacheDevices);
		cacheDevices = data;
		return true;
	}

	//-------------------------------------------------------------------
	private boolean programsChanged(List<CarriedItem<ItemTemplate>> data) {
		List<CarriedItem<ItemTemplate>> clone = new ArrayList<>(data);
		clone.removeAll(currentlyShowing);
		if (clone.isEmpty()) return false;
		logger.log(Level.WARNING, "programsChanged\n"+data+"\n"+currentlyShowing);
		currentlyShowing = data;
		return true;
	}

	//-------------------------------------------------------------------
	private void refreshPrograms(List<CarriedItem<ItemTemplate>> data) {
		root.getChildren().clear();
		// Sort software by type
		Map<ItemSubType, List<CarriedItem<ItemTemplate>>> byType = new HashMap<>();
		for (CarriedItem<ItemTemplate> tmp : data) {
			ItemSubType key = tmp.getAsObject(SR6ItemAttribute.ITEMSUBTYPE).getModifiedValue();
			List<CarriedItem<ItemTemplate>> list = byType.get(key);
			if (list==null) {
				list = new ArrayList<>();
				byType.put(key, list);
			}
			list.add(tmp);			
		}
		List<ItemSubType> subtypes = new ArrayList<ItemSubType>(byType.keySet());
		Collections.sort(subtypes, (s1,s2) -> Integer.compare(s1.ordinal(), s2.ordinal()));
		
		for (ItemSubType key : subtypes) {
			TreeItem<ProgramRow> item = new TreeItem<ProgramRow>(new ProgramRow(key));
			treeTable.getRoot().getChildren().add(item);
			List<CarriedItem<ItemTemplate>> list = byType.get(key);
			Collections.sort(list, (c1,c2) -> c1.getNameWithRating().compareTo(c2.getNameWithRating()));
			list.forEach(ci -> {
				ProgramRow progRow = new ProgramRow(ci);
				progRow.group.selectedToggleProperty().addListener( (ov,o,n) -> installationDeviceChanged(progRow, o,n));
				item.getChildren().add(new TreeItem<ProgramRow>(progRow));});
		}
		currentlyShowing = data;
		
		root.setExpanded(true);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void installationDeviceChanged(ProgramRow progRow, Toggle from, Toggle to) {
		if (from==null) return;
		if (to==null) return;
		CarriedItem<ItemTemplate> fromDevice = (from!=null)?(CarriedItem<ItemTemplate>) from.getUserData():null;
		CarriedItem<ItemTemplate> toDevice   = (  to!=null)?(CarriedItem<ItemTemplate>)   to.getUserData():null;
		if (progRow==null || progRow.program==null) return;
		if (toDevice==fromDevice) return;
		CarriedItem<ItemTemplate> program = progRow.program;
		logger.log(Level.WARNING, "User wants to remove ''{0}'' from {1} and install it on {2}", program.getKey(), fromDevice, toDevice);
		
		Possible possRemove = control.getEquipmentController().canBeRemoved(fromDevice, ItemHook.SOFTWARE, program);
		Possible possInstall = control.getEquipmentController().canBeEmbedded(toDevice, ItemHook.SOFTWARE, program.getModifyable(), program.getVariantID(), program.getDecisions().toArray(new Decision[program.getDecisions().size()]));
		if (!possRemove.get()) {
			logger.log(Level.WARNING, "Trying to uninstall, but {0}", possRemove);
			control.runProcessors();
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, possRemove.toString());
			return;
		}
		if (!possInstall.get()) {
			logger.log(Level.WARNING, "Trying to install, but {0}", possInstall);
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, possInstall.toString());
			control.runProcessors();
			return;
		}
		
		Possible poss = control.getEquipmentController().removeEmbedded(fromDevice, ItemHook.SOFTWARE, program);
		if (poss.get()) {
			logger.log(Level.WARNING, "Uninstall successful - now add to "+toDevice);
			// Deinstallation successful - add to target device
			toDevice.addAccessory(program, ItemHook.SOFTWARE);			
		}
		control.runProcessors();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void refreshDevices(List<CarriedItem<ItemTemplate>> devices) {
		// Calculate list of matrix device columns
		List<TreeTableColumn<ProgramRow, Boolean>> newColumns = new ArrayList<>();
		for (CarriedItem<ItemTemplate> matrixDev : devices) {
			if (matrixDev.getUuid().equals(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE)) continue;
			if (matrixDev.getSlot(ItemHook.SOFTWARE)==null) continue;
			int capacity = (int) matrixDev.getSlot(ItemHook.SOFTWARE).getCapacity();
			TreeTableColumn<ProgramRow, Boolean> realColumn = new TreeTableColumn<>(String.valueOf(capacity));
			TreeTableColumn<ProgramRow, Boolean> columnDevName = new TreeTableColumn<>(matrixDev.getNameWithoutRating());
			realColumn.setMinWidth(85);
			columnDevName.getColumns().add(realColumn);
			
			realColumn.setUserData(matrixDev);
			realColumn.setCellValueFactory(p -> new SimpleBooleanProperty(isUnused(p.getValue().getValue(), realColumn)));
			realColumn.setCellFactory(p -> new ProgramRowDeviceCell(p, control));
			makeHeaderWrappable(columnDevName);				
			newColumns.add(columnDevName);
			
			logger.log(Level.WARNING, "Column {0} has data {1}", realColumn, matrixDev);
		}
		treeTable.getColumns().setAll(colName, colUnused);
		treeTable.getColumns().addAll(newColumns);
	}
	
	//-------------------------------------------------------------------
	private void makeHeaderWrappable(TreeTableColumn<?,?> col) {
	    Label label = new Label(col.getText());
	    label.setStyle("-fx-padding: 8px;");
	    label.setWrapText(true);
	    label.setAlignment(Pos.CENTER);
	    label.setTextAlignment(TextAlignment.CENTER);

	    StackPane stack = new StackPane();
	    stack.getChildren().add(label);
	    stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
	    label.prefWidthProperty().bind(stack.prefWidthProperty());
	    col.setText(null);
	    col.setGraphic(stack);
	  }
}
