package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.ResponsiveControl;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.Selector;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.IMartialArtsController;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.MartialArtsListCell;

/**
 * @author prelle
 *
 */
public class MartialArtsSelector extends Selector<MartialArts, MartialArtsValue> implements ResponsiveControl {

	private final static Logger logger = System.getLogger(MartialArtsSelector.class.getPackageName());

	//-------------------------------------------------------------------
	public MartialArtsSelector(IMartialArtsController ctrl, AFilterInjector<MartialArts> filter) {
		super(ctrl, Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()), filter);
		listPossible.setCellFactory(lv -> new MartialArtsListCell(ctrl) );
		listPossible.setStyle("-fx-min-width: 30em;");
		Collections.sort(listPossible.getItems(), new Comparator<MartialArts>() {
			public int compare(MartialArts o1, MartialArts o2) {
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
	public MartialArtsSelector(IMartialArtsController ctrl, Predicate<MartialArts> baseFilter,  AFilterInjector<MartialArts> filter) {
		super(ctrl, baseFilter, Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()), filter);
		listPossible.setCellFactory(lv -> new MartialArtsListCell(ctrl) );
		listPossible.setStyle("-fx-pref-width: 30em;");
		Collections.sort(listPossible.getItems(), new Comparator<MartialArts>() {
			public int compare(MartialArts o1, MartialArts o2) {
				return Collator.getInstance().compare(o1.getName(Locale.getDefault()), o2.getName(Locale.getDefault()));
			}
		});
	}

}
