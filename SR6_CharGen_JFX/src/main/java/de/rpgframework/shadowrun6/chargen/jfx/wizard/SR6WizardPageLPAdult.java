package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathModuleGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.LifepathModuleListCell;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.LifepathModuleValueListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.LifepathModuleDescriptionPane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 */
public class SR6WizardPageLPAdult extends WizardPage implements ControllerListener {

	private final static Logger logger = System.getLogger(SR6WizardPageLPAdult.class.getPackageName());
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageLPAdult.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterGenerator charGen;
	protected SR6LifePathModuleGenerator modules;
	protected ComplexDataItemControllerNode<LifepathModule, LifepathModuleValue> selection;
	protected GenericDescriptionVBox bxDescription;
	protected OptionalNodePane layout;
	private VBox content;
	private NumberUnitBackHeader backHeader;
	private Label lbIntro;
	private Label lbCount;

	//-------------------------------------------------------------------
	public SR6WizardPageLPAdult(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		this.modules = charGen.getWrapped().getLifePathModuleGenerator();
		setTitle(ResourceI18N.get(RES, "page.adult.title"));
		initComponents();
		initLayout();
		initInteractivity();
		charGen.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		bxDescription = new LifepathModuleDescriptionPane(Shadowrun6Tools.requirementResolver(Locale.getDefault()), Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		lbCount = new Label();
		ensureSelection();
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		backHeader = new NumberUnitBackHeader("Karma");
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
		super.setBackHeader(backHeader);

		lbIntro = new Label();
		lbIntro.setWrapText(true);
		content = new VBox(10, lbIntro, lbCount);
		if (selection!=null)
			content.getChildren().add(selection);
		layout = new OptionalNodePane(content, bxDescription);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (selection!=null)
			initSelectionInteractivity();
	}

	//-------------------------------------------------------------------
	private void ensureSelection() {
		if (selection!=null || modules==null)
			return;
		selection = new ComplexDataItemControllerNode<>(modules);
		selection.setAvailableStyle("-fx-min-width: 20em; -fx-max-width: 28em");
		selection.setSelectedStyle("-fx-min-width: 20em; -fx-max-width: 30em");
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.adult.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.format(RES, "page.adult.placeholder.selected", modules.getMaximumModules()));
		selection.setShowHeadings(ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL);
		selection.setRequirementResolver(Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		selection.setModificationResolver(Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		selection.setAvailableCellFactory(lv -> new LifepathModuleListCell(() -> selection.getController(), Shadowrun6Tools.requirementResolver(Locale.getDefault())));
		selection.setSelectedCellFactory(lv -> new LifepathModuleValueListCell(() -> selection.getController()));
		selection.setOptionCallback(new ChoiceSelectorDialog<>(modules));
		initSelectionInteractivity();
		if (content!=null && !content.getChildren().contains(selection))
			content.getChildren().add(selection);
	}

	//-------------------------------------------------------------------
	private void initSelectionInteractivity() {
		selection.showHelpForProperty().addListener((ov, o, n) -> {
			bxDescription.setData(n);
			layout.setTitle(n!=null?n.getName():null);
		});
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		boolean isLifepath = charGen.getId().equals("lifepath");
		activeProperty().set(isLifepath);
		if (!isLifepath || modules==null)
			return;
		ensureSelection();
		backHeader.setValue(charGen.getModel().getKarmaFree());
		int maximumModules = modules.getMaximumModules();
		lbIntro.setText(ResourceI18N.format(RES, "page.adult.intro", maximumModules));
		lbCount.setText(ResourceI18N.format(RES, "page.adult.count", modules.getSelected().size(), maximumModules));
		if (selection!=null) {
			selection.setSelectedPlaceholder(ResourceI18N.format(RES, "page.adult.placeholder.selected", maximumModules));
			selection.refresh();
		}
	}

	//-------------------------------------------------------------------
	@Override
	public void pageVisited() {
		refresh();
	}

	//-------------------------------------------------------------------
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.INFO, "RCV " + type + " with " + Arrays.toString(param));
		if (type == BasicControllerEvents.GENERATOR_CHANGED) {
			charGen = (SR6CharacterGenerator) param[0];
			modules = charGen.getLifePathModuleGenerator();
			if (modules!=null) {
				if (selection==null) {
					ensureSelection();
				} else {
					selection.setController(modules);
					selection.setOptionCallback(new ChoiceSelectorDialog<>(modules));
				}
			}
		}
		if (type == BasicControllerEvents.CHARACTER_CHANGED || type == BasicControllerEvents.GENERATOR_CHANGED) {
			refresh();
		}
	}
}
