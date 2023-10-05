package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.TitledComponent;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifepathCharacterGenerator;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class WizardPageLifePath1 extends WizardPage {

	protected final static Logger logger = System.getLogger(WizardPageLifePath1.class.getPackageName());

	protected static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle
			.getBundle(WizardPageLifePath1.class.getName());

	protected IShadowrunCharacterGenerator<?, ?, ?,?> charGen;

	private ChoiceBox<MagicOrResonanceType> cbMoRType;
	private OptionalNodePane layout;
	private GenericDescriptionVBox bxDescription;
	private transient MagicOrResonanceType current;

	/* For aspected magicians */
	private ChoiceBox<SR6Skill> cbAspectSkill;

	/* For (aspected) magicians and mystic adepts*/
	private ChoiceBox<Tradition> cbTradition;
	private TitledComponent tcTradition;
	private TitledComponent tcAspect;
	protected GenericDescriptionVBox descTradition;

	private TextField tfNationality;
	private TextField tfLanguage;
	private Label lbQuality1, lbQuality2;
	private Button btnQuality1, btnQuality2;

	//-------------------------------------------------------------------
	public WizardPageLifePath1(Wizard wizard, SR6CharacterGenerator charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(UI, "wizard.page.lifepath1.title"));
		initComponents();
		initLayout();
		initInteractivity();

		if (ResponsiveControlManager.getCurrentMode()==WindowMode.MINIMAL) {
			cbMoRType.setValue(charGen.getModel().getMagicOrResonanceType());
		}

		refresh();
	}

	// -------------------------------------------------------------------
	private void initComponents() {
		cbMoRType = new ChoiceBox<MagicOrResonanceType>();
		cbMoRType.getItems().addAll(charGen.getMagicOrResonanceController().getAvailable());
		bxDescription = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));

		cbMoRType.setConverter(new StringConverter<MagicOrResonanceType>() {
			public String toString(MagicOrResonanceType type) { return (type!=null)?type.getName():"-";}
			public MagicOrResonanceType fromString(String arg0) {return null;}
		});

		/* For (aspected) magicians and mystic adepts */
		cbTradition = new ChoiceBox<>();
		cbTradition.getItems().addAll(Shadowrun6Core.getItemList(Tradition.class));
		Collections.sort(cbTradition.getItems(), new Comparator<Tradition>() {
			public int compare(Tradition o1, Tradition o2) {
				// TODO Auto-generated method stub
				return o1.getName().compareTo(o2.getName());
			}
		});
		cbTradition.setConverter(new StringConverter<Tradition>() {
			public String toString(Tradition value) {
				if (value==null) return "-";
				return value.getName();
			}
			public Tradition fromString(String string) { return null; }
		});
		descTradition = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));

		/* For aspected magicians */
		cbAspectSkill = new ChoiceBox<>();
		cbAspectSkill.getItems().addAll(Shadowrun6Core.getSkill("sorcery"), Shadowrun6Core.getSkill("conjuring"), Shadowrun6Core.getSkill("enchanting"));
		cbAspectSkill.setConverter(new StringConverter<SR6Skill>() {
			public String toString(SR6Skill value) {
				if (value==null) return "-";
				return value.getName();
			}
			public SR6Skill fromString(String string) { return null; }
		});


		tfNationality = new TextField();
		tfNationality.setPrefColumnCount(7);
		tfLanguage    = new TextField();
		tfLanguage.setPrefColumnCount(7);

		lbQuality1 = new Label("?");
		lbQuality2 = new Label();
		btnQuality1= new Button(ResourceI18N.get(UI, "wizard.page.lifepath1.button_quality"));
		btnQuality2= new Button(ResourceI18N.get(UI, "wizard.page.lifepath1.button_quality"));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		TitledComponent tcMagOrRes    = new TitledComponent(ResourceI18N.get(UI, "wizard.page.lifepath1.mortype"), cbMoRType);
		                tcTradition   = new TitledComponent(ResourceI18N.get(UI, "wizard.page.lifepath1.tradition"), cbTradition);
		                tcAspect      = new TitledComponent(ResourceI18N.get(UI, "wizard.page.lifepath1.aspect")   , cbAspectSkill);
		TitledComponent tcNationality = new TitledComponent(ResourceI18N.get(UI, "wizard.page.lifepath1.nationality"), tfNationality);
		TitledComponent tcLanguage    = new TitledComponent(ResourceI18N.get(UI, "wizard.page.lifepath1.language"), tfLanguage);
		FlowPane flow1 = new FlowPane(10,10,tcMagOrRes, tcAspect, tcTradition);
		FlowPane flow2 = new FlowPane(10,10,tcNationality, tcLanguage);

		Label head1 = new Label(ResourceI18N.get(UI, "wizard.page.lifepath1.quality1"));
		Label head2 = new Label(ResourceI18N.get(UI, "wizard.page.lifepath1.quality2"));
		head1.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		head2.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);
		grid.add(head1      , 0, 0, 2,1);
		grid.add(lbQuality1 , 0, 1);
		grid.add(btnQuality1, 1, 1);
		grid.add(head2      , 0, 2, 2,1);
		grid.add(lbQuality2 , 0, 3);
		grid.add(btnQuality2, 1, 3);

		VBox box = new VBox(10, flow1, flow2, grid, descTradition);
		ScrollPane scroll = new ScrollPane(box);
		scroll.setFitToWidth(true);

		layout = new OptionalNodePane(scroll, bxDescription);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbMoRType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			charGen.getMagicOrResonanceController().select(n);
			layout.setTitle( (n!=null)?n.getName():"");
			bxDescription.setData(n);
			refresh();
		});

		cbTradition.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			descTradition.setData(n);
			charGen.getMagicOrResonanceController().selectTradition(n);
		});

		tfLanguage.textProperty().addListener( (ov,o,n) -> {
			((SR6LifepathCharacterGenerator)charGen).setNativeLanguage(n);
		});
		tfNationality.textProperty().addListener( (ov,o,n) -> {
			logger.log(Level.WARNING, "ToDo: set nationality");
		});
	}

	// -------------------------------------------------------------------
	private void refresh() {
		Shadowrun6Character model = (Shadowrun6Character) charGen.getModel();
		cbMoRType.setValue(model.getMagicOrResonanceType());

		if (model.getMagicOrResonanceType()==null) {
			tcTradition.setVisible(false);
			tcTradition.setManaged(true);
			descTradition.setVisible(false);
			descTradition.setManaged(true);
			tcAspect.setVisible(false);
			tcAspect.setManaged(true);
		} else {
			tcTradition.setVisible(model.getMagicOrResonanceType().usesSpells());
			tcTradition.setManaged(model.getMagicOrResonanceType().usesSpells());
			descTradition.setVisible(model.getMagicOrResonanceType().usesSpells());
			descTradition.setManaged(model.getMagicOrResonanceType().usesSpells());
			tcAspect.setVisible(model.getMagicOrResonanceType().isAspected());
			tcAspect.setManaged(model.getMagicOrResonanceType().isAspected());
		}

		if (model.getTradition()!=null) {
			descTradition.setData(model.getTradition());
		}
		cbTradition.setValue(model.getTradition());


	}

	//-------------------------------------------------------------------
	/**
	 * Called from Wizard when page is shown to user
	 */
	@Override
	public void pageVisited() {
		cbMoRType.getItems().setAll(charGen.getMagicOrResonanceController().getAvailable());
		refresh();
	}

}
