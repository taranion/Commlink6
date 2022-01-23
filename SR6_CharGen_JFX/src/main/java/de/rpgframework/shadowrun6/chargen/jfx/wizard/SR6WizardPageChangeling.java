package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.javafx.layout.ResponsiveBox;

import de.rpgframework.ResourceI18N;
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
import javafx.scene.layout.HBox;

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

	//-------------------------------------------------------------------
	public SR6WizardPageChangeling(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.title"));
		initComponents();
		initLayout();
		initInteractivity();
		
		charGen.addListener(this);
		logger.log(Level.WARNING, "<init> with chargen="+charGen);
		logger.log(Level.WARNING, "            wrapped="+charGen.getWrapped());
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		selection = new ComplexDataItemControllerNode<>(charGen.getQualityController());
		selection.setFilterNode(new QualityFilterNode(RES, selection, QualityType.METAGENIC));
		
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
		
		Function<Requirement,String> resolver = (r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
		bxDescription = new GenericDescriptionVBox(resolver);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		ResponsiveBox responsive = new ResponsiveBox(selection, bxDescription);
		setContent(responsive);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		selection.showHelpForProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "show help for "+n);
			bxDescription.setData(n);
		});
	}

	//-------------------------------------------------------------------
	private void refresh() {
//		side.setPoints(charGen.getComplexFormController().getComplexFormsLeft());
//		side.setKarma(charGen.getCharacter().getKarmaFree());

		/*
		 * Enable or disable page
		 */
		BodyType type = charGen.getModel().getBodytype();
		if (type!=null) {
			// Enable or disable page
//			if (type.usesResonance()) {
//				logger.debug(type+" uses resonance - enable page");
//				activeProperty().set(true);
//			} else {
//				logger.debug(type+" does not use resonance - disable page");
//				activeProperty().set(false);
//			}
//		} else {
//			logger.warn("No magic or resonance type selected yet");
//			activeProperty().set(false);
		}

//		selectPane.refresh();
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

	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.WARNING,"handleControllerEvent("+type+", "+Arrays.toString(param)+")");
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
