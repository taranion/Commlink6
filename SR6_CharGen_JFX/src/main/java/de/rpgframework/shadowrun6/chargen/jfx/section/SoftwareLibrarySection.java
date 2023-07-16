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
import de.rpgframework.genericrpg.items.GearTool;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.filter.CarriedItemItemTypeFilter;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.ItemUtil;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
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
		CarriedItem<ItemTemplate> installedOn;
		Map<CarriedItem<ItemTemplate>, ToggleButton> buttonByDevice = new LinkedHashMap<>();
		ToggleGroup group = new ToggleGroup();
		TreeTableView<ProgramRow> treeTable;
		public ProgramRow(ItemSubType type) {this.type = type;}
		public ProgramRow(CarriedItem<ItemTemplate> prog, TreeTableView<ProgramRow> treeTable) {
			if (prog==null) throw new NullPointerException();
			this.program = prog;
			group.setUserData(prog);
			this.treeTable = treeTable;
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
		private SR6CharacterController control;

		public ProgramRowDeviceCell(TreeTableColumn<ProgramRow, Boolean> p, SR6CharacterController ctrl) {
			this.control = ctrl;
			button = new ToggleButton("x");
			button.getStyleClass().add("install-button");
			setAlignment(Pos.CENTER);
//			System.out.println("ProgramRowDeviceCell.<init>: "+this);
			tableRowProperty().addListener( (ov,o,n) -> {
				if (n==null) return;
				ProgramRow row = n.getItem();
				if (row==null) return;
				CarriedItem<ItemTemplate> device  = (CarriedItem<ItemTemplate>) getTableColumn().getUserData();
//				logger.log(Level.WARNING, "now "+row.program+" x "+device);
			});
		}
		@SuppressWarnings("unchecked")
		@Override
		public void updateItem(Boolean item, boolean empty) {
			super.updateItem(item, empty);
			ProgramRow row = getTableRow().getItem();
			if (empty || row==null) {
				setGraphic(null);
				return;
			}
			CarriedItem<ItemTemplate> program = (row==null)?null:row.program;
			CarriedItem<ItemTemplate> device  = (CarriedItem<ItemTemplate>) getTableColumn().getUserData();

//			logger.log(Level.WARNING, "updateItem() for "+program+" x "+device.getKey()+"  item="+item);
			if (!row.group.getToggles().contains(button))
				row.group.getToggles().add(button);
			row.buttonByDevice.put(device, button);
				CarriedItem<ItemTemplate> dev = (CarriedItem<ItemTemplate>) getTableColumn().getUserData();
				getTableRow().getItem().buttonByDevice.put(dev, button);
				// No buttons for devices without software slot
				if (dev==null || dev.getSlot(ItemHook.SOFTWARE)==null) {
					logger.log(Level.WARNING, "No SOFTWARE slot for {0}",dev);
					setGraphic(null);
					return;
				}

				// Can it be installed here?
				if (program==null) {
					return;
				}
				if (dev.getAccessories().contains(program)) {
					button.setDisable(false);
				} else {
					Possible possInstall = control.getEquipmentController().canBeEmbedded(dev, ItemHook.SOFTWARE,
							program.getModifyable(), program.getVariantID(),
							program.getDecisions().toArray(new Decision[program.getDecisions().size()]));
					button.setDisable(!possInstall.get());
				}

				setGraphic(button);
				if (dev!=null) {
					button.setUserData(dev);
					if (item)
						row.group.selectToggle(button);
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

	private Map<CarriedItem<ItemTemplate>, ProgramRow> rowBySoftware;

	private static boolean initializeDone;

	//-------------------------------------------------------------------
	public SoftwareLibrarySection() {
		super(ResourceI18N.get(RES, "section.software.title"), CarryMode.EMBEDDED, SELECT_FILTER, SHOW_FILTER);
		initTreeTable();
	}

	//-------------------------------------------------------------------
	private void initTreeTable() {
		currentlyShowing = new ArrayList<>();
		cacheDevices     = new ArrayList<>();
		rowBySoftware    = new HashMap<>();
		treeTable = new TreeTableView<>();
		treeTable.setShowRoot(false);

		colName   = new TreeTableColumn<>("Name");
		colUnused = new TreeTableColumn<>(ResourceI18N.get(RES, "section.software.unused"));
		treeTable.getColumns().add(colName);
		treeTable.getColumns().add(colUnused);
		colName.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().getName()));
		colUnused.setCellValueFactory(p -> new SimpleBooleanProperty(isUsed(p.getValue().getValue(), p.getTreeTableColumn())));
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
	private boolean isUsed(ProgramRow row, TreeTableColumn<ProgramRow, Boolean> col) {
		if (row.program==null) return true;
		CarriedItem<ItemTemplate> device = (CarriedItem<ItemTemplate>) col.getUserData();
		boolean used = device!=null && device.getAccessories().contains(row.program);
		return used;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		if (model==null) return;
		if (root==null) return;
		addToContainer = model.getSoftwareLibrary();
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

		initializeDone = false;
		if (programsChanged(data)) {
			refreshPrograms(data);
		}
		if (devicesChanged(devices)) {
			refreshDevices(devices);
		}
		treeTable.refresh();
		initializeDone = true;
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

		rowBySoftware.clear();
		for (ItemSubType key : subtypes) {
			TreeItem<ProgramRow> item = new TreeItem<ProgramRow>(new ProgramRow(key));
			treeTable.getRoot().getChildren().add(item);
			List<CarriedItem<ItemTemplate>> list = byType.get(key);
			Collections.sort(list, (c1,c2) -> c1.getNameWithRating().compareTo(c2.getNameWithRating()));
			list.forEach(ci -> {
				ProgramRow progRow = new ProgramRow(ci, treeTable);
				rowBySoftware.put(ci, progRow);
				progRow.group.selectedToggleProperty().addListener( (ov,o,n) -> {
					if (n==null)
						o.setSelected(true);
					else if (SoftwareLibrarySection.initializeDone)
						installationDeviceChanged(progRow, o,n);});
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
		logger.log(Level.DEBUG, "User wants to remove ''{0}'' from {1} and install it on {2}", program.getKey(), fromDevice, toDevice);

		Possible possRemove = control.getEquipmentController().canBeRemoved(fromDevice, ItemHook.SOFTWARE, program);
		Possible possInstall = control.getEquipmentController().canBeEmbedded(toDevice, ItemHook.SOFTWARE, program.getModifyable(), program.getVariantID(), program.getDecisions().toArray(new Decision[program.getDecisions().size()]));
		if (!possRemove.get()) {
			logger.log(Level.WARNING, "Trying to uninstall {1} from {2}, but {0}", possRemove, program, fromDevice);
			return;
		}
		if (!possInstall.get()) {
			logger.log(Level.WARNING, "Trying to install, but {0}", possInstall);
			return;
		}

		logger.log(Level.INFO, "Should move {0} from {1} to {2}", program.getModifyable(), fromDevice, toDevice);
		if (fromDevice.getAccessories().contains(program)) {
			fromDevice.removeAccessory(program, ItemHook.SOFTWARE);
			GearTool.recalculate("", ShadowrunReference.ITEM_ATTRIBUTE, model, fromDevice);
		}
		if (toDevice.getAccessories().contains(program))
			return;
		else {
			toDevice.addAccessory(program, ItemHook.SOFTWARE);
			GearTool.recalculate("", ShadowrunReference.ITEM_ATTRIBUTE, model, toDevice);
		}
		control.runProcessors();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void refreshDevices(List<CarriedItem<ItemTemplate>> devices) {
		List<CarriedItem<ItemTemplate>> devices2 = new ArrayList<>(devices);
		devices2.add(0,model.getSoftwareLibrary());

		// Calculate list of matrix device columns
		List<TreeTableColumn<ProgramRow, Boolean>> newColumns = new ArrayList<>();
		for (CarriedItem<ItemTemplate> matrixDev : devices2) {
			if (matrixDev.getUuid().equals(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE)) continue;
			if (matrixDev.getSlot(ItemHook.SOFTWARE)==null) continue;
			int capacity = (int) matrixDev.getSlot(ItemHook.SOFTWARE).getCapacity();
			TreeTableColumn<ProgramRow, Boolean> realColumn = new TreeTableColumn<>(String.valueOf(capacity));
			TreeTableColumn<ProgramRow, Boolean> columnDevName = new TreeTableColumn<>(matrixDev.getNameWithoutRating());
			realColumn.setMinWidth(85);
			columnDevName.getColumns().add(realColumn);

			realColumn.setUserData(matrixDev);
			realColumn.setCellValueFactory(p -> new SimpleBooleanProperty(isUsed(p.getValue().getValue(), realColumn)));
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
