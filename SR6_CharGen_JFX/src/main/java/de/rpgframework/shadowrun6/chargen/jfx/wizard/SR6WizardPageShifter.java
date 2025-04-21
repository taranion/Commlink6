package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.NodeWithTitle;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.Decision;
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
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.QualityFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.SR6ReferenceTypeConverter;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class SR6WizardPageShifter extends WizardPage implements ControllerListener{

	private final static Logger logger = System.getLogger(SR6WizardPageShifter.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageShifter.class.getPackageName()+".SR6WizardPages");

	private GeneratorWrapper charGen;

	private TextField tfAnimal;
	private Label lbCurrent;
	private Label lbMinimum;

	private DataItemPane<ChoiceOption> contentPane;
	private ComplexDataItemControllerNode<Quality, QualityValue> selection;
	private NumberUnitBackHeader backHeader;
	/* When TRUE asynchronous updates are happening and user events shall be ignored */
	private boolean updating;

	//-------------------------------------------------------------------
	public SR6WizardPageShifter(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.shifter.title"));
		initComponents();
		initContentPane();
		initLayout();
		initInteractivity();

		charGen.addListener(this);
		// Select first kind of shifter
		List<ChoiceOption> items = Shadowrun6Core.getItem(SR6Quality.class, "shifter")
				.getChoice(UUID.fromString("55a6dda7-7565-4108-9a04-65b7607081d3"))
				.getSubOptions();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tfAnimal  = new TextField();
		lbCurrent = new Label("+?");
		lbCurrent.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbMinimum = new Label("?");

		selection = new ComplexDataItemControllerNode<>(charGen.getShifterGenerator());
		QualityFilterNode filter = new QualityFilterNode(RES, selection, QualityType.SHIFTER);
		selection.setFilterNode(filter);
		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.SHIFTER);
		selection.setRequirementResolver(Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		selection.setModificationResolver(Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.shifter.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.shifter.placeholder.selected"));

		selection.setAvailableCellFactory(lv -> new QualityListCell(selection.getController(), Shadowrun6Tools.requirementResolver(Locale.getDefault())));
		
		selection.setSelectedCellFactory(lv -> {
			QualityValueListCell zelle = new QualityValueListCell( ()->charGen, false);
			zelle.setControlProvider( () -> charGen.getShifterGenerator() );
			return zelle;
		});
		selection.setShowHeadings(false);
		selection.setOptionCallback(new ChoiceSelectorDialog<>(selection.getController()));
		
		// Pre-pane
		Label hdAnimal = new Label(ResourceI18N.get(RES, "page.shifter.type_of_animal"));
		VBox pre = new VBox(5, hdAnimal, tfAnimal);
		pre.setStyle("-fx-padding: 0 0 1em 0");
		selection.setSelectedPre(pre);
		selection.setSkin(new ComplexDataItemControllerOneColumnSkin<Quality,QualityValue>(selection));

//		bxDescription = new GenericDescriptionVBox(
//				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
//				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	private void initContentPane() {
		contentPane = new DataItemPane<ChoiceOption>(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault())
				);
		contentPane.setShowDecisionColumn(false);
		contentPane.setShowStatsColumn(false);
		contentPane.setId("species");
		contentPane.setImageConverter(new Callback<ChoiceOption,Image>(){
			public Image call(ChoiceOption value) {
				String name = "images/metatypes/shifter_"+value.getId()+".png";
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
			System.err.println("SR6WizardPageShifter: ToDo: make decision "+c);
//			SplitterJFXUtil.openDecisionDialog(r, c, null);
		});

		List<ChoiceOption> items = Shadowrun6Core.getItem(SR6Quality.class, "shifter")
				.getChoice(UUID.fromString("55a6dda7-7565-4108-9a04-65b7607081d3"))
				.getSubOptions();
//		none.assignToDataSet( items.get(0).getAssignedDataSets().iterator().next() );
		logger.log(Level.WARNING, "Available: "+items);

		contentPane.setItems(items);
		contentPane.setShowDecisionColumn(false);

	}

	//-------------------------------------------------------------------
	private void initLayout() {
		contentPane.setCustomNode1(new NodeWithTitle(ResourceI18N.get(RES,"tab.custom"), selection));
//		selection.setSkin(new ComplexDataItemControllerOneColumnSkin<Quality,QualityValue>(selection));
		
		setContent(contentPane);
		//ResponsiveBox responsive = new ResponsiveBox(selection, bxDescription);
//		AutoBox responsive = new AutoBox();
//		responsive.getContent().addAll(selection, bxDescription);
		Label hdPointsSpent = new Label(ResourceI18N.get(RES, "page.shifter.pointsSpent"));
		HBox line = new HBox(5, hdPointsSpent, lbMinimum, lbCurrent);
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
			userSelects(n);
			refresh();
		});
		
		selection.showHelpForProperty().addListener( (obj,o,n) -> {
			logger.log(Level.INFO, "ToDo: Find a way to show description of "+n);
		});
		
		tfAnimal.textProperty().addListener( (ov,o,n) -> {
			n = n.replace("<", "").replace(">", "");
			charGen.getModel().setShifterAnimal(n);
			
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

			lbCurrent.setText("+"+String.valueOf( charGen.getShifterGenerator().getCurrentKarmaCost()));
			lbMinimum.setText(String.valueOf( charGen.getShifterGenerator().getMinimalKarmaCost()));
			BodyType type = charGen.getModel().getBodytype();
			if (type!=null) {
				// Enable or disable page
				boolean isShifter = type==BodyType.SHAPESHIFTER;
				if (isShifter) {
					logger.log(Level.DEBUG, type+" can have shifter qualities - enable page");
					activeProperty().set(true);
				} else {
					logger.log(Level.DEBUG, type+" is not a shifter - disable page");
					activeProperty().set(false);
				}
			} else {
				logger.log(Level.WARNING, "No body type selected yet");
				activeProperty().set(false);
			}

			if (model.getQuality("shifter")!=null) {
				List<Decision> decs = model.getQuality("shifter").getDecisions();
				if (!decs.isEmpty()) {
					ChoiceOption opt = model.getQuality("shifter").getResolved().getChoice(decs.get(0).getChoiceUUID()).getSubOption(decs.get(0).getValue());
					contentPane.setSelectedItem(opt);
				} else {
					contentPane.setSelectedItem(null);
				}
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
//		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
//			selection.setController(charGen.getShifterGenerator());
//		}
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
	private void userSelects(ChoiceOption toSelect) {
		logger.log(Level.INFO, "userSelects(" + toSelect + ")");
		Choice choice = Shadowrun6Core.getItem(SR6Quality.class, "shifter").getChoices().get(0);
		QualityValue shifter = charGen.getModel().getQuality("shifter");
		shifter.removeDecision(choice.getUUID());
		shifter.addDecision(new Decision(choice.getUUID(), toSelect.getId()));
		
		charGen.runProcessors();
    }

}
