package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.prelle.javafx.SymbolIcon;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.ComplexDataItemListFilter;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class ItemTemplateFilterNode extends ComplexDataItemListFilter<ItemTemplate,CarriedItem<ItemTemplate>> {
	
	private final static Logger logger = System.getLogger(ItemTemplateFilterNode.class.getPackageName());
	
	private enum Sort {
		NAME,
		KARMA
	}
	
	private ResourceBundle RES;
	
	private List<ItemType> allowedTypes;
	private ChoiceBox<ItemType> cbTypes;
	private ChoiceBox<ItemSubType> cbSubTypes;
	private Button btnSort;
	private TextField tfSearch;
	
	private Comparator<ItemTemplate> compareByName = new Comparator<ItemTemplate>() {
		public int compare(ItemTemplate q1, ItemTemplate q2) {
			return Collator.getInstance().compare(q1.getName(), q2.getName());
		}
	};
	private Comparator<ItemTemplate> compareByKarma = new Comparator<ItemTemplate>() {
		public int compare(ItemTemplate q1, ItemTemplate q2) {
			int c = Integer.compare(q1.getAttribute(SR6ItemAttribute.PRICE).getDistributed(), q2.getAttribute(SR6ItemAttribute.PRICE).getDistributed());
			if (c==0)
				c = Collator.getInstance().compare(q1.getName(), q2.getName());
			return c;
		}
	};
	
	private Sort currentSort = Sort.NAME;

	//-------------------------------------------------------------------
	public ItemTemplateFilterNode(ResourceBundle RES, ComplexDataItemControllerNode<ItemTemplate, CarriedItem<ItemTemplate>> parent, ItemType...types) {
		super(parent);
		this.RES = RES;
		allowedTypes = List.of(types);
		initComponents();
		initLayout();
		initInteractivity();
		refreshAvailable();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		cbTypes = new ChoiceBox<ItemType>();
		cbTypes.getItems().addAll(ItemType.values());
//		cbTypes.setValue(What.ALL);
		cbTypes.setConverter(new StringConverter<ItemType>() {
			public String toString(ItemType what) { return (what!=null)?ResourceI18N.get(RES, "itemtype."+what.name().toLowerCase()):"";}
			public ItemType fromString(String arg0) {return null;}
		});
		cbSubTypes = new ChoiceBox<ItemSubType>();
		
		btnSort = new Button(null,new SymbolIcon("sort"));
		btnSort.setTooltip(new Tooltip(ResourceI18N.get(RES, "quality.sort.tooltip")));
		
		tfSearch = new TextField();
		tfSearch.setPromptText(ResourceI18N.get(RES, "quality.search.prompt"));
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		HBox line = new HBox(cbTypes, cbSubTypes, btnSort);
		HBox.setHgrow(line, Priority.ALWAYS);
		line.setMaxWidth(Double.MAX_VALUE);
		cbTypes.setMaxWidth(Double.MAX_VALUE);
		cbSubTypes.setMaxWidth(Double.MAX_VALUE);
		getChildren().addAll(line, tfSearch);
		
		VBox.setMargin(tfSearch, new Insets(5,0,5,0));
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnSort.setOnAction(ev -> {
			if (currentSort==Sort.NAME)
				currentSort=Sort.KARMA;
			else
				currentSort=Sort.NAME;
			logger.log(Level.INFO, "Sort changed to "+currentSort);

			refreshAvailable();
		});
		
		cbTypes.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "Selection changed");
			refreshAvailable();
		});
		
		tfSearch.textProperty().addListener( (ov,o,n) -> refreshAvailable());
	}
	
	//-------------------------------------------------------------------
	private void refreshAvailable() {
		ItemType n = cbTypes.getValue();
		final String search = tfSearch.getText().toLowerCase();
		List<ItemTemplate> unfiltered = parent.getController().getAvailable();
		List<ItemTemplate> filtered = unfiltered.stream()
//			.filter(q -> allowedTypes.contains(q.getItemType()))
			.filter(q -> (n==q.getItemType()))
			.filter(q -> (search==null || search.isBlank() || q.getName().toLowerCase().contains(search)))
			.collect(Collectors.toList());
		logger.log(Level.INFO, "{0} items now", filtered.size());
		logger.log(Level.INFO, "byName ="+compareByName);
		logger.log(Level.INFO, "byKarma="+compareByKarma);
		
		switch (currentSort) {
		case NAME : Collections.sort(filtered, compareByName); break;
		case KARMA: Collections.sort(filtered, compareByKarma); break;
		}
		parent.availableProperty().get().setAll(filtered);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ComplexDataItemListFilter#applyFilter()
	 */
	@Override
	public void applyFilter() {
		refreshAvailable();
	}

}
