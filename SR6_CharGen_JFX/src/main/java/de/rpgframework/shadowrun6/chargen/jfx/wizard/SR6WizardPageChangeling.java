package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.gen.QualityGenerator;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityListCell;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.QualityFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6WizardPageChangeling extends WizardPage implements ControllerListener{
	
	private final static Logger logger = System.getLogger(SR6WizardPageChangeling.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageChangeling.class.getName());

	private GeneratorWrapper charGen;
	
	private Function<Requirement,String> requirementResolver;
	private Label lbNetKarma;
	private ComplexDataItemControllerNode<Quality, QualityValue> selection;
	private GenericDescriptionVBox bxDescription;
	private OptionalNodePane layout;
	private NumberUnitBackHeader backHeader;

	//-------------------------------------------------------------------
	public SR6WizardPageChangeling(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.title"));
		initComponents();
		initLayout();
		initInteractivity();
		
		charGen.addListener(this);
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes" })
	private void initComponents() {
		requirementResolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
		
		lbNetKarma = new Label("?");
		lbNetKarma.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		selection = new ComplexDataItemControllerNode<>(charGen.getQualityController());
		QualityFilterNode filter = new QualityFilterNode(RES, selection, QualityType.METAGENIC);
		filter.setShowSURGE(true);
		selection.setFilterNode(filter);
		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.METAGENIC);
		selection.setRequirementResolver(requirementResolver);
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "placeholder.selected"));
		
		selection.setAvailableCellFactory(lv -> new QualityListCell(selection.getController()));
		selection.setSelectedCellFactory(lv -> new QualityValueListCell(
				new IShadowrunCharacterControllerProvider<IShadowrunCharacterController>() {
					public IShadowrunCharacterController getCharacterController() {
						return charGen;
					}}, 
				null, true));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		selection.setOptionCallback(new ChoiceSelectorDialog<>(selection.getController()));
		
		bxDescription = new GenericDescriptionVBox(requirementResolver);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		layout = new OptionalNodePane(selection, bxDescription);
		//ResponsiveBox responsive = new ResponsiveBox(selection, bxDescription);
//		AutoBox responsive = new AutoBox();
//		responsive.getContent().addAll(selection, bxDescription);
		Label hdPointsSpent = new Label(ResourceI18N.get(RES, "surge.pointsSpent"));
		Label lbMax = new Label("/30");
		HBox line = new HBox(5, hdPointsSpent, lbNetKarma, lbMax);
		selection.setSelectedListHead(line);
		setContent(layout);

		// Back header
		backHeader = new NumberUnitBackHeader("Karma");
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
		super.setBackHeader(backHeader);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		selection.showHelpForProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "show help for "+n);
			bxDescription.setData(n);
			if (n!=null) {
				layout.setTitle(n.getName());
			} else {
				layout.setTitle(null);
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * Enable or disable page
	 */
	private void refresh() {
		backHeader.setValue(charGen.getModel().getKarmaFree());
		
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
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageVisited()
	 */
	@Override
	public void pageVisited() {
		logger.log(Level.INFO, "pageVisited");
		selection.refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
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

}
