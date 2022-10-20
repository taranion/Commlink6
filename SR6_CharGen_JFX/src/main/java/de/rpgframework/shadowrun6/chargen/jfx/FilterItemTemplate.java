package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.IRefreshableList;
import de.rpgframework.shadowrun6.items.ItemSubType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6PieceOfGearVariant;
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

	private CarryMode carry;
	private ItemType[] allowed;
	
	private ChoiceBox<ItemType> cbType;
	private ChoiceBox<ItemSubType> cbSubtype;
	private TextField tfSearch;
	
	private ItemType lastType;

	//-------------------------------------------------------------------
	public FilterItemTemplate(CarryMode carry, ItemType...allowed) {
		this.carry   = carry;
		this.allowed = allowed;
		logger.log(Level.INFO, "FilterItemTemplate for "+carry);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.AFilterInjector#updateChoices(java.util.List)
	 */
	@Override
	public void updateChoices(List<ItemTemplate> available) {
		List<ItemType> existing = new ArrayList<>();
		List<ItemSubType> existingSub = new ArrayList<>();
		for (ItemTemplate item : available) {
			SR6PieceOfGearVariant variant = (SR6PieceOfGearVariant) item.getVariant(carry);
			if (item.getUsage(carry)!=null) {
				ItemType type = (item.getAttribute(SR6ItemAttribute.ITEMTYPE)!=null)?item.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue(): item.getItemType();
				ItemSubType subtype = (item.getAttribute(SR6ItemAttribute.ITEMSUBTYPE)!=null)?item.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue(): item.getItemSubtype();
				if (!existing.contains(type))
					existing.add(type);
				if (!existingSub.contains(subtype))
					existingSub.add(subtype);
			} else if (variant!=null && variant.getUsage(carry)!=null) {
				// Inspect variant
				ItemType type = (variant.getAttribute(SR6ItemAttribute.ITEMTYPE)!=null)?variant.getAttribute(SR6ItemAttribute.ITEMTYPE).getValue(): item.getItemType();
				ItemSubType subtype = (variant.getAttribute(SR6ItemAttribute.ITEMSUBTYPE)!=null)?variant.getAttribute(SR6ItemAttribute.ITEMSUBTYPE).getValue(): item.getItemSubtype();
				if (!existing.contains(type)) 
					existing.add(type);
				if (!existingSub.contains(subtype))
					existingSub.add(subtype);
			} else {
				logger.log(Level.TRACE, "Item {0} from available list has no carry mode {1}", item.getId(), carry);
			}
		}
		// Sort them by ordinal
		Collections.sort(existing, new Comparator<ItemType>() {
			public int compare(ItemType t1, ItemType t2) {
				return Integer.compare(t1.ordinal(), t2.ordinal());
			}
		});
		// Sort them by ordinal
		Collections.sort(existingSub, new Comparator<ItemSubType>() {
			public int compare(ItemSubType t1, ItemSubType t2) {
				return Integer.compare(t1.ordinal(), t2.ordinal());
			}
		});
		// Overwrite allowed types
		allowed = existing.toArray(new ItemType[existing.size()]);
		// Allow empty selection
		existing.add(0, null);
		cbType.getItems().setAll(existing);
		
		cbSubtype.getItems().setAll(existingSub);
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
		if (allowed!=null)
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
		cbSubtype.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Subtype changed");
			page.refreshList();
			});
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
				input.forEach(item -> {if (item.getItemSubtype(carry)!=null &&!appear.contains(item.getItemSubtype(carry))) appear.add(item.getItemSubtype(carry));});
				Collections.sort(appear);
				cbSubtype.getItems().setAll(appear);
			}
		} else {
			if (allowed!=null) {
				input = input.stream()
					.filter(data -> data.getItemType(carry)!=null && List.of(allowed).contains(data.getItemType(carry)))
					.collect(Collectors.toList());
			}
			logger.log(Level.INFO, "After filter cbType={0} remain {1} items", cbType, input.size());
			if (cbType.getValue()!=lastType) {
				List<ItemSubType> appear = new ArrayList<>();
				input.forEach(item -> {if (item.getItemSubtype(carry)!=null &&!appear.contains(item.getItemSubtype(carry))) appear.add(item.getItemSubtype(carry));});
				Collections.sort(appear);
				cbSubtype.getItems().setAll(appear);
			}
			
		}
		lastType = cbType.getValue();
		// Match item subtype
		if (cbSubtype.getValue()!=null) {
			input = input.stream()
					.filter(data -> data.getItemSubtype(carry)==cbSubtype.getValue())
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
