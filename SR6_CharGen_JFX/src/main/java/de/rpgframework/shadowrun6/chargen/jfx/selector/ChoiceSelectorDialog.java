package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.OptionalNodePane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class ChoiceSelectorDialog<T extends ComplexDataItem, V extends ComplexDataItemValue<T> > extends ManagedDialog implements BiFunction<T, List<Choice>, Decision[]> {
	
	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(ChoiceSelectorDialog.class.getName());
	
	private final static Logger logger = System.getLogger(ChoiceSelectorDialog.class.getPackageName());

	private FlexibleApplication app;
	private ComplexDataItemController<T,V> ctrl;
	
	private OptionalNodePane optional;
	private GenericDescriptionVBox<DataItem> bxDesc;
	private VBox content;
	private Label lbProblem;
	private NavigButtonControl btnCtrl;
	
	private T item;
	private List<Choice> choices;
	private Map<Choice, Decision> decisions = new LinkedHashMap<>();
	
	//-------------------------------------------------------------------
	public ChoiceSelectorDialog(FlexibleApplication app, ComplexDataItemController<T,V> ctrl) {
		super("Select",null, CloseType.CANCEL, CloseType.OK);
		this.app = app;
		this.ctrl = ctrl;
		
		content = new VBox(10);
		bxDesc  = new GenericDescriptionVBox<>(null);
		optional= new OptionalNodePane(content, bxDesc);
		lbProblem = new Label();
		lbProblem.setStyle("-fx-text-fill: -fx-accent");
		
		setContent(new VBox(10,optional, lbProblem));
	}

	//-------------------------------------------------------------------
	private void showHelpFor(DataItem item) {
		bxDesc.setData(item);
		optional.setTitle(item.getName());
	}

	//-------------------------------------------------------------------
	private Decision[] getDecisions() {
		Decision[] ret = new Decision[choices.size()];
		for (int i=0; i<choices.size(); i++) {
			ret[i] = decisions.get(choices.get(i));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private void updateButtons() {
		logger.log(Level.WARNING, "updateButtons with "+ctrl);
		
		Possible possible = ctrl.canBeSelected(item, getDecisions() );
		logger.log(Level.INFO, "canBeSelected returns "+possible);
		// Set status
		ToDoElement problem = possible.getMostSevere();
		if (problem==null) {
			lbProblem.setText(null);			
		} else {
			lbProblem.setText(problem.getMessage(Locale.getDefault()));
			switch (problem.getSeverity()) {
			case STOPPER: lbProblem.setStyle("-fx-text-fill: -fx-accent"); break;
			case WARNING: lbProblem.setStyle("-fx-text-fill: primary"); break;
			case INFO   : lbProblem.setStyle("-fx-text-fill: --fx-text-base-color"); break;
			}
		}
		
		if (!possible.get() || (problem!=null && problem.getSeverity()==Severity.WARNING)) {
			btnCtrl.setDisabled(CloseType.OK, true);
		} else {
			btnCtrl.setDisabled(CloseType.OK, false);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Decision[] apply(T item, List<Choice> choices) {
		logger.log(Level.INFO, "ENTER apply({}, {})", item, choices);
		this.item = item;
		this.choices = choices;
		CloseType closed = null;
		try {
			decisions.clear();

			setTitle(ResourceI18N.format(RES, "title", item.getName()));
			content.getChildren().clear();
			// Minimal intro text
			Label explain = new Label(ResourceI18N.get(RES, "explain"));
			explain.setWrapText(true);
			content.getChildren().add(explain);

			for (Choice choice : choices) {
				logger.log(Level.DEBUG, "Choice " + choice);

				Label lbName = new Label(item.getChoiceName(choice, Locale.getDefault()));
				lbName.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
				content.getChildren().add(lbName);

				switch ((ShadowrunReference) choice.getChooseFrom()) {
				case SUBSELECT:
					handleSUBSELECT(item, choice);
					break;
				case TEXT:
					handleTEXT(item, choice);
					break;
				default:
					logger.log(Level.ERROR, "Not implemented: choosing from " + choice.getChooseFrom());
				}
			}

			btnCtrl = new NavigButtonControl();
			btnCtrl.initialize(app, this);
			btnCtrl.setDisabled(CloseType.OK, true);
			updateButtons();
			closed = app.showAlertAndCall(this, btnCtrl);
			logger.log(Level.DEBUG, "Closed with "+closed);
			if (closed==CloseType.CANCEL)
				return null;
			return getDecisions();
		} finally {
			logger.log(Level.INFO, "LEAVE apply({}, {} with {})", item, choices, closed);
		}
	}

	//-------------------------------------------------------------------
	private void handleSUBSELECT(T item, Choice choice) {
		ChoiceBox<ChoiceOption> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<ChoiceOption>() {
			public ChoiceOption fromString(String value) { return null;}
			public String toString(ChoiceOption value) {
				if (value==null) return "-";
				String name = item.getChoiceOptionStrings(choice, value, Locale.getDefault())[0];
				if (value.getCost()!=0) {
					if ( Math.round(value.getCost())==value.getCost()) {
						name+=" (+"+((int)(value.getCost()))+" Karma)";
					} else {
						name+=" ("+value.getCost()+" Karma)";
					}
				}
				return name;
			}
		});
		for (ChoiceOption opt : choice.getSubOptions()) {
			logger.log(Level.DEBUG, "  sub option "+opt);
			cbSub.getItems().add(opt);
			
		}
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {} for {}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));
			updateButtons(); 
			showHelpFor(n); });
		content.getChildren().add(cbSub);
	}

	//-------------------------------------------------------------------
	private void handleTEXT(T item, Choice choice) {
		TextField tfDescr = new TextField();
		content.getChildren().add(tfDescr);
		tfDescr.textProperty().addListener( (ov,o,n) -> {
			decisions.put(choice, new Decision(choice, n));
			updateButtons(); 
		});
	}
	
}
