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

import org.prelle.javafx.SymbolIcon;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.ComplexDataItemListFilter;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityCategory;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
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
public class QualityFilterNode extends ComplexDataItemListFilter<Quality,QualityValue> {

	private final static Logger logger = System.getLogger(QualityFilterNode.class.getPackageName());

	private enum What {
		ALL,
		POSITIVE,
		NEGATIVE
	}

	private enum Sort {
		NAME,
		KARMA
	}

	private ResourceBundle RES;
	private List<QualityType> allowedTypes;
	private List<QualityCategory> allowedCategories;

	private ChoiceBox<What> cbWhat;
	private ChoiceBox<QualityType> cbType;
	private ChoiceBox<QualityCategory> cbCategory;
	private Button btnSort;
	private TextField tfSearch;

	private Comparator<Quality> compareByName = new Comparator<Quality>() {
		public int compare(Quality q1, Quality q2) {
			return Collator.getInstance().compare(q1.getName(), q2.getName());
		}
	};
	private Comparator<Quality> compareByKarma = new Comparator<Quality>() {
		public int compare(Quality q1, Quality q2) {
			int c = Integer.compare(q1.getKarmaCost(), q2.getKarmaCost());
			if (c==0)
				c = Collator.getInstance().compare(q1.getName(), q2.getName());
			return c;
		}
	};

	private Sort currentSort = Sort.NAME;

	//-------------------------------------------------------------------
	public QualityFilterNode(ResourceBundle RES, ComplexDataItemControllerNode<Quality, QualityValue> parent, QualityType...types) {
		super(parent);
		this.RES = RES;
		allowedTypes = List.of(types);
		allowedCategories = new ArrayList<>();
		initComponents();
		initLayout();
		initInteractivity();
		refreshAvailable();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		cbWhat = new ChoiceBox<What>();
		cbWhat.getItems().addAll(What.values());
		cbWhat.setValue(What.ALL);
		cbWhat.setConverter(new StringConverter<QualityFilterNode.What>() {
			public String toString(What what) { return ResourceI18N.get(RES, "quality.what."+what.name().toLowerCase());}
			public What fromString(String arg0) {return null;}
		});

		cbType = new ChoiceBox<QualityType>();
		cbType.getItems().add(null);
		cbType.getItems().addAll(allowedTypes);
		cbType.setValue(null);
		cbType.setConverter(new StringConverter<QualityType>() {
			public String toString(QualityType type) { return (type==null)?QualityType.getAllName(Locale.getDefault()):type.getName();}
			public QualityType fromString(String arg0) {return null;}
		});

		cbCategory = new ChoiceBox<QualityCategory>();
		cbCategory.getItems().add(null);
		cbCategory.getItems().addAll(QualityCategory.values());
		cbCategory.setValue(null);
		cbCategory.setConverter(new StringConverter<QualityCategory>() {
			public String toString(QualityCategory cat) { return (cat!=null)?cat.getName(Locale.getDefault()):QualityCategory.getAllName(Locale.getDefault()) ;}
			public QualityCategory fromString(String arg0) {return null;}
		});

		btnSort = new Button(null,new SymbolIcon("sort"));
		btnSort.setTooltip(new Tooltip(ResourceI18N.get(RES, "quality.sort.tooltip")));

		tfSearch = new TextField();
		tfSearch.setPromptText(ResourceI18N.get(RES, "quality.search.prompt"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		HBox line = new HBox(2,cbWhat, cbType, cbCategory, btnSort);
		HBox.setHgrow(line, Priority.ALWAYS);
		line.setMaxWidth(Double.MAX_VALUE);
		cbWhat.setMaxWidth(Double.MAX_VALUE);
		getChildren().addAll(line, tfSearch);

		VBox.setMargin(tfSearch, new Insets(5,0,5,0));
		checkCategoryVisibility();
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

		cbWhat.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "Selection changed "+n);
			refreshAvailable();
		});
		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "Selection changed "+n);
			refreshAvailable();
		});
		cbCategory.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "Selection changed "+n);
			refreshAvailable();
		});

		tfSearch.textProperty().addListener( (ov,o,n) -> refreshAvailable());
	}

	//-------------------------------------------------------------------
	private void refreshAvailable() {
		What n = cbWhat.getValue();
		QualityCategory c = cbCategory.getValue();
		QualityType restrictType = cbType.getValue();
		final String search = tfSearch.getText().toLowerCase();
		List<Quality> unfiltered = parent.getController().getAvailable();
		List<Quality> filtered = unfiltered.stream()
			.filter(q -> allowedTypes.contains(q.getType()))
			.filter(q -> (n==What.ALL || (n==What.NEGATIVE && !q.isPositive()) || (n==What.POSITIVE && q.isPositive())))
			.filter(q -> (restrictType==null || restrictType==q.getType()))
			.filter(q -> (c==null || (!allowedCategories.isEmpty() && q.getCategory()==c)))
			.filter(q -> (search==null || search.isBlank() || q.getName().toLowerCase().contains(search)))
			.collect(Collectors.toList());
		logger.log(Level.DEBUG, "{0} items now", filtered.size());
		logger.log(Level.DEBUG, "byName ="+compareByName);
		logger.log(Level.DEBUG, "byKarma="+compareByKarma);

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

	//-------------------------------------------------------------------
	private void checkCategoryVisibility() {
		cbCategory.setVisible(!allowedCategories.isEmpty());
		cbCategory.setManaged(!allowedCategories.isEmpty());
		cbType.setVisible(allowedTypes.size()>1);
		cbType.setManaged(allowedTypes.size()>1);
	}

	//-------------------------------------------------------------------
	public void setShowSURGE(boolean show, QualityCategory...categories) {
		if (!show) {
			allowedTypes.removeAll(List.of(QualityCategory.values()));
		} else {
			for (QualityCategory cat : QualityCategory.values()) {
				if (!allowedCategories.contains(cat))
					allowedCategories.add(cat);
			}
		}
		checkCategoryVisibility();
	}

}
