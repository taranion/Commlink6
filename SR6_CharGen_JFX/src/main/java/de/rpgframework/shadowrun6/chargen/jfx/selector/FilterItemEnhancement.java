package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.IRefreshableList;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * @author prelle
 *
 */
public class FilterItemEnhancement extends AFilterInjector<SR6ItemEnhancement> {

	private final static Logger logger = System.getLogger(FilterItemEnhancement.class.getPackageName());

	private TextField tfSearch;

	//-------------------------------------------------------------------
	public FilterItemEnhancement() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.AFilterInjector#updateChoices(java.util.List)
	 */
	@Override
	public void updateChoices(List<SR6ItemEnhancement> available) {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.AFilterInjector#addFilter(de.rpgframework.jfx.FilteredListPage, javafx.scene.layout.FlowPane)
	 */
	@Override
	public void addFilter(IRefreshableList page, Pane filterPane) {

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
	public List<SR6ItemEnhancement> applyFilter(List<SR6ItemEnhancement> input) {
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
