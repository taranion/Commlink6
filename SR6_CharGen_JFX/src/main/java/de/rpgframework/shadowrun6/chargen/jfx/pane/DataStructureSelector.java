package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.ResponsiveControl;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.IDataStructureController;

/**
 * @author prelle
 *
 */
public class DataStructureSelector extends Selector<DataStructure, DataStructureValue> implements ResponsiveControl {

	private final static Logger logger = System.getLogger(DataStructureSelector.class.getPackageName());

	//-------------------------------------------------------------------
	public DataStructureSelector(IDataStructureController ctrl, AFilterInjector<DataStructure> filter, Function<Requirement, String> req) {
		super(ctrl, Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()), filter);
		listPossible.setCellFactory(lv -> new ComplexDataItemListCell<DataStructure>( () -> ctrl, req) );
		listPossible.setStyle("-fx-min-width: 30em;");
		Collections.sort(listPossible.getItems(), new Comparator<DataStructure>() {
			public int compare(DataStructure o1, DataStructure o2) {
				return Collator.getInstance().compare(o1.getName(Locale.getDefault()), o2.getName(Locale.getDefault()));
			}
		});

		// Button control
    	listPossible.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
    		logger.log(Level.DEBUG, "Selected {0}", n);
    		Possible poss = ctrl.canBeSelected(n);
    		logger.log(Level.DEBUG, "Selection possible = {0}",poss);
    		if (btnCtrl!=null) {
    			btnCtrl.setDisabled(CloseType.OK, !poss.get());
    		}
    	});
	}

	//-------------------------------------------------------------------
	/**
	 * @param ctrl
	 * @param baseFilter
	 * @param filter
	 */
	public DataStructureSelector(IDataStructureController ctrl, Predicate<DataStructure> baseFilter,  AFilterInjector<DataStructure> filter) {
		super(ctrl, baseFilter, Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()), filter);
		listPossible.setCellFactory(lv -> new ComplexDataItemListCell<DataStructure>( () -> ctrl, Shadowrun6Tools.requirementResolver(Locale.getDefault())) );
		listPossible.setStyle("-fx-pref-width: 30em;");
		Collections.sort(listPossible.getItems(), new Comparator<DataStructure>() {
			public int compare(DataStructure o1, DataStructure o2) {
				return Collator.getInstance().compare(o1.getName(Locale.getDefault()), o2.getName(Locale.getDefault()));
			}
		});
	}

}
