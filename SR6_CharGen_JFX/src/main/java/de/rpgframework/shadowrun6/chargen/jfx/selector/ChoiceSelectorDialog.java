package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
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
	
	private List<Node> toDeleteOnMentorSpirit = new ArrayList<>();
	private BooleanProperty chooseAdeptAdvantages = new SimpleBooleanProperty(false);
	
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
		
		Shadowrun6Character model = ctrl.getModel();
		chooseAdeptAdvantages.setValue( model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesPowers());
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
	private Node addLabel(String title) {
		Label lbName = new Label(title);
		lbName.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		content.getChildren().add(lbName);
		return lbName;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Decision[] apply(T item, List<Choice> choices) {
		logger.log(Level.INFO, "ENTER apply({0}, {1})", item, choices);
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
				processChoice(item,choice, null);
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
			logger.log(Level.INFO, "LEAVE apply({0}, {1} with {2})", item, choices, closed);
		}
	}

	// -------------------------------------------------------------------
	private List<Node>  processChoice(ComplexDataItem item, Choice choice, String forceTitle) {
		logger.log(Level.DEBUG, "Choice " + choice);
		List<Node> ret = new ArrayList<>();
		
		ret.add( addLabel(
				(forceTitle==null)
				?
				item.getChoiceName(choice, Locale.getDefault())
				:
				forceTitle)
				);
		switch ((ShadowrunReference) choice.getChooseFrom()) {
		case ATTRIBUTE:
			ret.add( handleATTRIBUTE(item, choice));
			break;
		case MENTOR_SPIRIT:
			ret.add( handleMENTOR_SPIRIT(item, choice) );
			break;
		case SKILL:
			ret.add( handleSKILL(item, choice) );
			break;
		case SUBSELECT:
			ret.add( handleSUBSELECT(item, choice) );
			break;
		case TEXT:
			ret.add( handleTEXT(item, choice) );
			break;
		default:
			System.err.println("Not implemented: choosing from " + choice.getChooseFrom());
			logger.log(Level.ERROR, "Not implemented: choosing from " + choice.getChooseFrom());
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private Node handleSKILL(ComplexDataItem item, Choice choice) {
		ChoiceBox<SR6Skill> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<SR6Skill>() {
			public SR6Skill fromString(String value) { return null;}
			public String toString(SR6Skill value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
					Shadowrun6Core.getItemList(SR6Skill.class).stream().filter(s -> ids.contains(s.getId())).collect(Collectors.toList())
					);			
		} else {
			cbSub.getItems().addAll(Shadowrun6Core.getItemList(SR6Skill.class));
		}
		Collections.sort(cbSub.getItems(), new Comparator<SR6Skill>() {
			public int compare(SR6Skill o1, SR6Skill o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));
			updateButtons(); 
			showHelpFor(n); });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleATTRIBUTE(ComplexDataItem item, Choice choice) {
		ChoiceBox<ShadowrunAttribute> cbSub = new ChoiceBox<>();
		cbSub.setConverter(new StringConverter<ShadowrunAttribute>() {
			public ShadowrunAttribute fromString(String value) { return null;}
			public String toString(ShadowrunAttribute value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbSub.getItems().addAll(
					List.of(ShadowrunAttribute.values()).stream().filter(s -> ids.contains(s.name())).collect(Collectors.toList())
					);			
		} else {
			cbSub.getItems().addAll(ShadowrunAttribute.primaryValues());
		}
		Collections.sort(cbSub.getItems(), new Comparator<ShadowrunAttribute>() {
			public int compare(ShadowrunAttribute o1, ShadowrunAttribute o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbSub.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.name()));
			updateButtons(); 
		 });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private void populateAdeptChoices(MentorSpirit n) {
		for (Modification tmp: n.getAdeptModifications()) {
			DataItemModification mod = (DataItemModification)tmp;
			Object val = mod.getResolvedKey();
			if (val instanceof ComplexDataItem) {
				ComplexDataItem cplx = (ComplexDataItem) val;
				for (Choice choice : cplx.getChoices()) {
					logger.log(Level.INFO, "...... has choice "+choice);
					Choice cloned = (Choice) choice.clone();
					if (mod.getChoiceOptions()!=null && mod.getConnectedChoice()!=null && mod.getConnectedChoice().equals(choice.getUUID())) {
						logger.log(Level.DEBUG, "Restrict choice options from {0} to {1}", Arrays.toString(cloned.getChoiceOptions()), mod.getChoiceOptions());
						cloned.setChoiceOptions(mod.getChoiceOptions());
					}
					List<Node> added = processChoice(cplx, cloned, cplx.getName());
					added.forEach(node -> {
						node.visibleProperty().bind(chooseAdeptAdvantages);
						node.managedProperty().bind(chooseAdeptAdvantages);
					});
				}
			}
		}
	}

	//-------------------------------------------------------------------
	private void populateMagicianChoices(MentorSpirit n) {
		for (Modification tmp: n.getMagicianModifications()) {
			DataItemModification mod = (DataItemModification)tmp;
			Object val = mod.getResolvedKey();
			if (val instanceof ComplexDataItem) {
				ComplexDataItem cplx = (ComplexDataItem) val;
				for (Choice choice : cplx.getChoices()) {
					logger.log(Level.INFO, "...... has choice "+choice);
					List<Node> added = processChoice(cplx, choice, cplx.getName());
					added.forEach(node -> {
						node.visibleProperty().bind(chooseAdeptAdvantages.not());
						node.managedProperty().bind(chooseAdeptAdvantages.not());
					});
				}
			}
		}
	}
	
	//-------------------------------------------------------------------
	private Node handleMENTOR_SPIRIT(ComplexDataItem item, Choice choice) {
		ChoiceBox<MentorSpirit> cbMentor = new ChoiceBox<>();
		cbMentor.setConverter(new StringConverter<MentorSpirit>() {
			public MentorSpirit fromString(String value) { return null;}
			public String toString(MentorSpirit value) {
				if (value==null) return "-";
				return value.getName();
			}
		});
		// All but only given options?
		if (choice.getChoiceOptions()!=null) {
			List<String> ids = List.of(choice.getChoiceOptions());
			cbMentor.getItems().addAll(
					Shadowrun6Core.getItemList(MentorSpirit.class).stream().filter(s -> ids.contains(s.getId())).collect(Collectors.toList())
					);			
		} else {
			cbMentor.getItems().addAll(Shadowrun6Core.getItemList(MentorSpirit.class));
		}
		Collections.sort(cbMentor.getItems(), new Comparator<MentorSpirit>() {
			public int compare(MentorSpirit o1, MentorSpirit o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}});
		cbMentor.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));
			
			// Clear content from previous mentor spirit selection
			if (o!=null && !o.getChoices().isEmpty()) {
				choices.removeAll(o.getChoices());
			}			
			content.getChildren().removeAll(toDeleteOnMentorSpirit);
			
			// Memorize current GUI elements
			List<Node> current = new ArrayList<Node>(content.getChildren());
			
			// Populate with generic decisions
			logger.log(Level.INFO, "Choices of Mentor spirit: "+n.getChoices());
			for (Choice tmpChoice : n.getChoices()) {
				processChoice(item, tmpChoice, null);
				choices.add(choice);
			}
			populateMagicianChoices(n);
			populateAdeptChoices(n);
			
			// Compare with now updated
			List<Node> updated = new ArrayList<Node>(content.getChildren());
			toDeleteOnMentorSpirit  = updated.stream().filter(i -> !current.contains(i)).collect(Collectors.toList());

			
			updateButtons(); 
			showHelpFor(n);
		 });
		content.getChildren().add(cbMentor);
		
		Shadowrun6Character model = ctrl.getModel();
		if ( model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesPowers() && model.getMagicOrResonanceType().usesSpells()) {
			addLabel(ResourceI18N.get(RES, "choice.magician_adept"));

			ChoiceBox<MagicOrResonanceType> cbMagOrAdp = new ChoiceBox<>();
			cbMagOrAdp.getItems().add(Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician"));
			cbMagOrAdp.getItems().add(Shadowrun6Core.getItem(MagicOrResonanceType.class, "adept"));
			cbMagOrAdp.setConverter(new StringConverter<MagicOrResonanceType>() {
				public String toString(MagicOrResonanceType v) {return (v!=null)?v.getName():null;}
				public MagicOrResonanceType fromString(String string) {return null;}
			});
			content.getChildren().add(cbMagOrAdp);
			cbMagOrAdp.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
				logger.log(Level.DEBUG, "Use advantages for "+n);
				chooseAdeptAdvantages.setValue (n!=null && n.usesPowers());
			});
		}
		
		return cbMentor;
	}

	//-------------------------------------------------------------------
	private Node handleSUBSELECT(ComplexDataItem item, Choice choice) {
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
			logger.log(Level.DEBUG, "Chose {0} for {1}", n, choice.getUUID());
			decisions.put(choice, new Decision(choice, n.getId()));
			updateButtons(); 
			showHelpFor(n); });
		content.getChildren().add(cbSub);
		return cbSub;
	}

	//-------------------------------------------------------------------
	private Node handleTEXT(ComplexDataItem item, Choice choice) {
		TextField tfDescr = new TextField();
		content.getChildren().add(tfDescr);
		tfDescr.textProperty().addListener( (ov,o,n) -> {
			decisions.put(choice, new Decision(choice, n));
			updateButtons(); 
		});
		return tfDescr;
	}
	
}
