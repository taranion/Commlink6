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
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.ComplexDataItemListFilter;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class AdeptPowerFilterNode extends ComplexDataItemListFilter<AdeptPower,AdeptPowerValue> {

	private final static Logger logger = System.getLogger(AdeptPowerFilterNode.class.getPackageName());

	private enum Sort {
		NAME,
		COST
	}

	private ResourceBundle RES;
	private Button btnSort;
	private TextField tfSearch;

	private Comparator<AdeptPower> compareByName = new Comparator<AdeptPower>() {
		public int compare(AdeptPower q1, AdeptPower q2) {
			return Collator.getInstance().compare(q1.getName(), q2.getName());
		}
	};
	private Comparator<AdeptPower> compareByKarma = new Comparator<AdeptPower>() {
		public int compare(AdeptPower q1, AdeptPower q2) {
			int c = Float.compare(q1.getCost(), q2.getCost());
			if (c==0)
				c = Collator.getInstance().compare(q1.getName(), q2.getName());
			return c;
		}
	};

	private Sort currentSort = Sort.NAME;

	//-------------------------------------------------------------------
	public AdeptPowerFilterNode(ResourceBundle RES, ComplexDataItemControllerNode<AdeptPower, AdeptPowerValue> parent) {
		super(parent);
		this.RES = RES;
		initComponents();
		initLayout();
		initInteractivity();
		refreshAvailable();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		btnSort = new Button(null,new SymbolIcon("sort"));
		btnSort.setTooltip(new Tooltip(ResourceI18N.get(RES, "pages.adeptpowers.sort.tooltip")));

		tfSearch = new TextField();
		tfSearch.setPromptText(ResourceI18N.get(RES, "page.adeptpowers.search.prompt"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		HBox line = new HBox(btnSort);
		HBox.setHgrow(line, Priority.ALWAYS);
		line.setMaxWidth(Double.MAX_VALUE);
		getChildren().addAll(line, tfSearch);

		VBox.setMargin(tfSearch, new Insets(5,0,5,0));
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		btnSort.setOnAction(ev -> {
			if (currentSort==Sort.NAME)
				currentSort=Sort.COST;
			else
				currentSort=Sort.NAME;
			logger.log(Level.INFO, "Sort changed to "+currentSort);

			refreshAvailable();
		});

		tfSearch.textProperty().addListener( (ov,o,n) -> refreshAvailable());
	}

	//-------------------------------------------------------------------
	private void refreshAvailable() {
		final String search = tfSearch.getText().toLowerCase();
		List<AdeptPower> unfiltered = parent.getController().getAvailable();
		List<AdeptPower> filtered = unfiltered.stream()
			.filter(q -> (search==null || search.isBlank() || q.getName().toLowerCase().contains(search)))
			.collect(Collectors.toList());
//		logger.log(Level.INFO, "{0} items now", filtered.size());
//		logger.log(Level.DEBUG, "byName ="+compareByName);
//		logger.log(Level.DEBUG, "byKarma="+compareByKarma);

		switch (currentSort) {
		case NAME : Collections.sort(filtered, compareByName); break;
		case COST: Collections.sort(filtered, compareByKarma); break;
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
