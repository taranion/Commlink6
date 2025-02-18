package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.NodeWithTitle;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.SetItem;
import de.rpgframework.genericrpg.SetItemValue;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.IAttribute;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.ComplexDataItemControllerOneColumnSkin;
import de.rpgframework.jfx.DataItemPane;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.jfx.CommonShadowrunJFXResourceHook;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityListCell;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.QualityFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.SR6ReferenceTypeConverter;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SR6WizardPageChangeling extends WizardPage implements ControllerListener{

	private final static Logger logger = System.getLogger(SR6WizardPageChangeling.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	private GeneratorWrapper charGen;

	private Label lbNetKarma;

	private DataItemPane<SetItem> contentPane;
	private ComplexDataItemControllerNode<Quality, QualityValue> selection;
	private NumberUnitBackHeader backHeader;
	/* When TRUE asynchronous updates are happening and user events shall be ignored */
	private boolean updating;

	private transient SetItem none;
	private Comparator<SetItem> comparator;
	private ChoiceSelectorDialog<SetItem, SetItemValue> selectorDialog;

	//-------------------------------------------------------------------
	public SR6WizardPageChangeling(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.surge.title"));
		none = new SetItem();
		none.setId("general");
		initComponents();
		initContentPane();
		initLayout();
		initInteractivity();

		charGen.addListener(this);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes" })
	private void initComponents() {
		selectorDialog = new ChoiceSelectorDialog<>(new ComplexDataItemController<SetItem, SetItemValue>() {
			@Override
			public <A extends IAttribute, M extends RuleSpecificCharacterObject<A, ?, ?, ?>> CharacterController<A, M> getCharacterController() {
				return (CharacterController<A, M>) charGen;
			}
			@Override
			public <C extends RuleSpecificCharacterObject<? extends IAttribute, ?, ?, ?>> C getModel() {
				return (C) charGen.getModel();
			}
			@Override
			public List<ToDoElement> getToDos() {
				return List.of();
			}
			@Override
			public void roll() {}

			@Override
			public List<UUID> getChoiceUUIDs() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void decide(SetItem decideFor, UUID choice, Decision decision) {
				// TODO Auto-generated method stub

			}

			@Override
			public List<Modification> process(List<Modification> unprocessed) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<SetItem> getAvailable() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<SetItemValue> getSelected() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RecommendationState getRecommendationState(SetItem value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RecommendationState getRecommendationState(SetItemValue value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Choice> getChoicesToDecide(SetItem value) {
				return value.getChoices();
			}
			@Override
			public Possible canBeSelected(SetItem value, Decision... decisions) {
				return Possible.TRUE;
			}
			@Override
			public OperationResult<SetItemValue> select(SetItem value, Decision... decisions) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public Possible canBeDeselected(SetItemValue value) {
				return Possible.TRUE;
			}
			@Override
			public boolean deselect(SetItemValue value) {return false;}
			@Override
			public float getSelectionCost(SetItem data, Decision... decisions) { return data.getCost(); }
			@Override
			public String getSelectionCostString(SetItem data) { return String.valueOf(getSelectionCost(data)); }
			@Override
			public Choice getAsChoice(ComplexDataItem value, UUID uuid) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public ModificationChoice getAsModificationChoice(ComplexDataItem value, UUID uuid) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		comparator = new Comparator<SetItem>() {
			public int compare(SetItem meta1, SetItem meta2) {
				if (meta1==none) return -1;
				if (meta2==none) return +1;
				return meta1.getName().compareTo(meta2.getName());
			};
		};

		lbNetKarma = new Label("?");
		lbNetKarma.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		selection = new ComplexDataItemControllerNode<>(charGen.getQualityController());
		QualityFilterNode filter = new QualityFilterNode(RES, selection, QualityType.METAGENIC);
		filter.setShowSURGE(true);
		selection.setFilterNode(filter);
		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.METAGENIC);
		selection.setRequirementResolver(Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		selection.setModificationResolver(Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.surge.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.surge.placeholder.selected"));

		selection.setAvailableCellFactory(lv -> new QualityListCell(selection.getController(), Shadowrun6Tools.requirementResolver(Locale.getDefault())));
		selection.setSelectedCellFactory(lv -> new QualityValueListCell( ()->charGen, true));
		selection.setShowHeadings(false);
		selection.setOptionCallback(new ChoiceSelectorDialog<>(selection.getController()));
		selection.setSkin(new ComplexDataItemControllerOneColumnSkin(selection));

//		bxDescription = new GenericDescriptionVBox(
//				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
//				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	private void initContentPane() {

		contentPane = new DataItemPane<SetItem>(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault())
				);
		contentPane.setShowDecisionColumn(true);
		contentPane.setShowStatsColumn(false);
		contentPane.setId("species");
		contentPane.setImageConverter(new Callback<SetItem,Image>(){
			public Image call(SetItem value) {
				String name = "images/metatypes/surge_"+value.getId()+".png";
				logger.log(Level.INFO, "Search "+name);
				InputStream in = CommonShadowrunJFXResourceHook.class.getResourceAsStream(name);
				if (in!=null) {
					Image img = new Image(in);
					if (img.isError()) {
						System.err.println("Error loading "+name+": "+img.getException());
					}
					return img;
				}
				logger.log(Level.ERROR, "Missing resource "+CommonShadowrunJFXResourceHook.class.getPackage().getName()+" + "+name);
				System.err.println("Missing resource "+CommonShadowrunJFXResourceHook.class.getPackage().getName()+" + "+name);
				return null;
			}});
		contentPane.setModificationConverter((m) -> Shadowrun6Tools.getModificationString(contentPane.getSelectedItem(),m, Locale.getDefault()));
		contentPane.setReferenceTypeConverter(new SR6ReferenceTypeConverter<>());
		contentPane.setNameConverter( meta -> {
			if (meta==null) return "-";
			String suffix = " ("+meta.getCost()+" Karma)";
			return "- "+meta.getName()+suffix;
		});
//		contentPane.setModificationConverter((m) -> SplitterTools.getModificationString(contentPane.getSelectedItem(),m));
//		contentPane.setChoiceConverter((c) -> SplitterTools.getChoiceString(contentPane.getSelectedItem(), c));
		contentPane.setUseForChoices(charGen.getModel());
		contentPane.setDecisionHandler( (r,c) -> {
			logger.log(Level.WARNING, "ToDo: make decision");
			System.err.println("SR6WizardPageChangeling: ToDo: make decision "+c);
//			SplitterJFXUtil.openDecisionDialog(r, c, null);
		});

		List<SetItem> items = Shadowrun6Core.getItemList(SetItem.class).stream()
				.filter(p -> charGen.showDataItem(p))
				.collect(Collectors.toList());
		if (!items.isEmpty()) {
			none.assignToDataSet( items.get(0).getAssignedDataSets().iterator().next() );
		}
		items.add(0, none);
		Collections.sort(items, comparator);
		logger.log(Level.WARNING, "Available: "+items);

		contentPane.setItems(items);
		contentPane.setShowDecisionColumn(false);

	}

	//-------------------------------------------------------------------
	private void initLayout() {
		contentPane.setCustomNode1(new NodeWithTitle(ResourceI18N.get(RES,"tab.custom"), selection));

		setContent(contentPane);
		//ResponsiveBox responsive = new ResponsiveBox(selection, bxDescription);
//		AutoBox responsive = new AutoBox();
//		responsive.getContent().addAll(selection, bxDescription);
		Label hdPointsSpent = new Label(ResourceI18N.get(RES, "page.surge.pointsSpent"));
		Label lbMax = new Label("/30");
		HBox line = new HBox(5, hdPointsSpent, lbNetKarma, lbMax);
		selection.setSelectedListHead(line);

		// Back header
		backHeader = new NumberUnitBackHeader("Karma");
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
		super.setBackHeader(backHeader);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		contentPane.selectedItemProperty().addListener( (ov,o,n) -> {
			if (updating) return;
			if (n==null) return;
			logger.log(Level.ERROR, "ToDo: Select "+n);
			userSelects(n);
			refresh();
		});

	}

	//-------------------------------------------------------------------
	/**
	 * Enable or disable page
	 */
	private void refresh() {
		updating = true;
		try {
			Shadowrun6Character model = charGen.getModel();
			backHeader.setValue(model.getKarmaFree());

		lbNetKarma.setText(String.valueOf( ((CommonQualityGenerator)charGen.getQualityController()).getKarmaForSURGE()));
		BodyType type = charGen.getModel().getBodytype();
		if (type!=null) {
			// Enable or disable page
			boolean isMetaHuman = type!=BodyType.SHAPESHIFTER;
			if (isMetaHuman) {
				logger.log(Level.DEBUG, type+" can have metagenic qualities - enable page");
				activeProperty().set(true);
			} else {
				logger.log(Level.DEBUG, type+" is not a metahuman - disable page");
				activeProperty().set(false);
			}
		} else {
			logger.log(Level.WARNING, "No body type selected yet");
			activeProperty().set(false);
		}

		List<SetItem> items = Shadowrun6Core.getItemList(SetItem.class).stream()
			.filter(p -> charGen.showDataItem(p))
			.filter(p -> Shadowrun6Tools.areRequirementsMet(model, p, new Decision[0]).get())
			.collect(Collectors.toList());
		items.add(0, none);
		Collections.sort(items, comparator);
		contentPane.setItems(items);

			if (model.getSurgeCollective()!=null && items.contains(model.getSurgeCollective().getResolved()) && model.getSurgeCollective().getResolved() != contentPane.getSelectedItem()) {
				contentPane.setSelectedItem(model.getSurgeCollective().getResolved());
			} else {
				contentPane.setSelectedItem(none);
			}
		} finally {
			updating = false;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageVisited()
	 */
	@Override
	public void pageVisited() {
		logger.log(Level.INFO, "pageVisited");
		refresh();
		selection.refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.DEBUG, "RCV {0}",type);
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			selection.setController(charGen.getQualityController());
		}
		if (type==BasicControllerEvents.CHARACTER_CHANGED || type==BasicControllerEvents.GENERATOR_CHANGED) {
			selection.setOptionCallback(new ChoiceSelectorDialog<>(selection.getController()));
			selection.refresh();
			refresh();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		logger.log(Level.WARNING,"setResponsiveMode({0})", value);
		selection.setShowHeadings(value!=WindowMode.MINIMAL);
	}

	// -------------------------------------------------------------------
	private void userSelects(SetItem toSelect) {
		logger.log(Level.INFO, "userSelects(" + toSelect + ")");
		SetItemValue ret = new SetItemValue(toSelect);
		// Is there a need for a selection
		if (!toSelect.getChoices().isEmpty()) {
			// Yes, user must choose
			List<Choice> options = toSelect.getChoices();
			logger.log(Level.DEBUG, "called getChoicesToDecide returns {0} choices", options.size());
			Platform.runLater( () -> {
				Decision[] decisions = selectorDialog.apply(toSelect, options);
				if (decisions != null) {
					for (Decision dec : decisions) {
						logger.log(Level.WARNING, "Add decision", dec);
						ret.addDecision(dec);
					}
				}
				charGen.getModel().setSurgeCollective(ret);
				charGen.runProcessors();
			});
		} else {
			charGen.getModel().setSurgeCollective(ret);
			charGen.runProcessors();
		}
    }

}
