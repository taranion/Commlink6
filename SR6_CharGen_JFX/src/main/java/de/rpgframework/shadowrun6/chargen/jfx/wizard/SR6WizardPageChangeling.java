package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.FlexibleApplication;
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
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityListCell;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.QualityFilterNode;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author prelle
 *
 */
public class SR6WizardPageChangeling extends WizardPage implements ControllerListener{
	
	private final static Logger logger = System.getLogger(SR6WizardPageChangeling.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageChangeling.class.getName());

	private GeneratorWrapper charGen;
	
	private ComplexDataItemControllerNode<Quality, QualityValue> selection;
	private GenericDescriptionVBox<Quality> bxDescription;
	private OptionalNodePane layout;

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
		selection = new ComplexDataItemControllerNode<>(charGen.getQualityController());
		selection.setFilterNode(new QualityFilterNode(RES, selection, QualityType.METAGENIC));
		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.METAGENIC);
		
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "placeholder.selected"));
		
		selection.setAvailableCellFactory(lv -> new QualityListCell(selection.getController()));
		selection.setSelectedCellFactory(lv -> new QualityValueListCell(
				new IShadowrunCharacterControllerProvider<IShadowrunCharacterController>() {
					public IShadowrunCharacterController getCharacterController() {
						return charGen;
					}}, 
				null));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		selection.setOptionCallback(new ChoiceSelectorDialog<>(FlexibleApplication.getInstance(), selection.getController()));
		
		Function<Requirement,String> resolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
		bxDescription = new GenericDescriptionVBox(resolver);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		layout = new OptionalNodePane(selection, bxDescription);
		//ResponsiveBox responsive = new ResponsiveBox(selection, bxDescription);
//		AutoBox responsive = new AutoBox();
//		responsive.getContent().addAll(selection, bxDescription);
		setContent(layout);
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
		if (type==BasicControllerEvents.CHARACTER_CHANGED) 
			refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		logger.log(Level.WARNING,"setResponsiveMode({})", value);
		selection.setShowHeadings(value!=WindowMode.MINIMAL);
	}

}
