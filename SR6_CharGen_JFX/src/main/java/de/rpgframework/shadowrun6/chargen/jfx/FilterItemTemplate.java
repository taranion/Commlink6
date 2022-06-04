package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.IRefreshableList;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ItemTemplateSelector;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class FilterItemTemplate extends AFilterInjector<ItemTemplate> {
	
	private final static Logger logger = System.getLogger(FilterItemTemplate.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(FilterItemTemplate.class.getPackageName()+".Filters");

	private ItemType[] allowed;
	
	private ChoiceBox<ItemType> cbType;
	private ChoiceBox<ItemSubType> cbSubtype;
	private TextField tfSearch;
	
	private ItemType lastType;

	//-------------------------------------------------------------------
	public FilterItemTemplate(ItemType...allowed) {
		this.allowed = allowed;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.AFilterInjector#addFilter(de.rpgframework.jfx.FilteredListPage, javafx.scene.layout.FlowPane)
	 */
	@Override
	public void addFilter(IRefreshableList page, Pane filterPane) {
		/*
		 * Item Type
		 */
		cbType = new ChoiceBox<ItemType>();
		cbType.getItems().add(null);
		cbType.getItems().addAll(allowed);
		Collections.sort(cbType.getItems(), new Comparator<ItemType>() {
			public int compare(ItemType o1, ItemType o2) {
				if (o1==null) return -1;
				if (o2==null) return  1;
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}
		});
		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> page.refreshList());
		cbType.setConverter(new StringConverter<ItemType>() {
			public String toString(ItemType val) {
				if (val==null) return ResourceI18N.get(RES, "filter.itemtemplate.type.all");
				return val.getName();
			}
			public ItemType fromString(String string) { return null; }
		});
		filterPane.getChildren().add(cbType);

		/*
		 * Item Sub Type
		 */
		cbSubtype = new ChoiceBox<ItemSubType>();
		cbSubtype.getItems().add(null);
//		cbType.getItems().addAll(allowed);
		Collections.sort(cbSubtype.getItems(), new Comparator<ItemSubType>() {
			public int compare(ItemSubType o1, ItemSubType o2) {
				if (o1==null) return -1;
				if (o2==null) return  1;
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}
		});
		cbSubtype.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> page.refreshList());
		cbSubtype.setConverter(new StringConverter<ItemSubType>() {
			public String toString(ItemSubType val) {
				if (val==null) return ResourceI18N.get(RES, "filter.itemtemplate.subtype.all");
				return val.getName();
			}
			public ItemSubType fromString(String string) { return null; }
		});
		filterPane.getChildren().add(cbSubtype);

		// Finally a keyword search
		tfSearch = new TextField();
		tfSearch.textProperty().addListener( (ov,o,n)-> page.refreshList());
		filterPane.getChildren().add(tfSearch);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.AFilterInjector#applyFilter(de.rpgframework.jfx.FilteredListPage, java.util.List)
	 */
	@Override
	public List<ItemTemplate> applyFilter(List<ItemTemplate> input) {
		// Match item type
		if (cbType.getValue()!=null) {
			input = input.stream()
					.filter(data -> data.getItemType()==cbType.getValue())
					.collect(Collectors.toList());
			logger.log(Level.INFO, "After filter cbType={0} remain {1} items", cbType, input.size());
			
			if (cbType.getValue()!=lastType || lastType==null) {
				List<ItemSubType> appear = new ArrayList<>();
				input.forEach(item -> {if (!appear.contains(item.getItemSubtype())) appear.add(item.getItemSubtype());});
				Collections.sort(appear);
				cbSubtype.getItems().setAll(appear);
			}
		} else {
			input = input.stream()
					.filter(data -> List.of(allowed).contains(data.getItemType()))
					.collect(Collectors.toList());
			logger.log(Level.INFO, "After filter cbType={0} remain {1} items", cbType, input.size());
			if (cbType.getValue()!=lastType || lastType==null) {
				List<ItemSubType> appear = new ArrayList<>();
				input.forEach(item -> {if (!appear.contains(item.getItemSubtype())) appear.add(item.getItemSubtype());});
				Collections.sort(appear);
				cbSubtype.getItems().setAll(appear);
			}
			
		}
		lastType = cbType.getValue();
		// Match item subtype
		if (cbSubtype.getValue()!=null) {
			input = input.stream()
					.filter(data -> data.getItemSubtype()==cbSubtype.getValue())
					.collect(Collectors.toList());
			logger.log(Level.INFO, "After filter cbSubType={0} remain {1} items", cbType, input.size());
		}
		// Match keyword
		if (tfSearch.getText()!=null && !tfSearch.getText().isBlank()) {
			String key = tfSearch.getText().toLowerCase().trim();
			input = input.stream()
					.filter(item -> item.getName().toLowerCase().contains(key))
					.collect(Collectors.toList());
			logger.log(Level.INFO, "After filter text={0} remain {1} items", key, input.size());
		}
		
		return input;
	}

}
