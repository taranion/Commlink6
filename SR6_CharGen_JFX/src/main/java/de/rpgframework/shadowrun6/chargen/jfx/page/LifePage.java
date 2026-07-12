package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.Section;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.LifestyleQuality;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.chargen.jfx.section.ContactSection;
import de.rpgframework.shadowrun.chargen.jfx.section.LifestyleSection;
import de.rpgframework.shadowrun.chargen.jfx.section.SINSection;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6LifestyleListCell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class LifePage extends Page {

	private final static Logger logger = System.getLogger(LifePage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private transient SR6CharacterController ctrl;

	private SINSection secSINs;
	private LifestyleSection<SR6Lifestyle> secLifestyles;
	private ContactSection secContacts;
	private Section secBackground;
	private TextArea taBabyBackground;
	private TextArea taChildhoodBackground;
	private TextArea taAdultBackground;
	private TextArea taBackgroundNotes;
	private boolean updatingBackgroundFields;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private GenericDescriptionVBox descBox ;

	/** Shall extended contact rules from the 6WC be used? */
	private CheckBox cbExtended;

	//-------------------------------------------------------------------
	public LifePage() {
		super(ResourceI18N.get(RES, "page.life.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initSINs();
		initContacts();
		initLifestyles();
		initBackgrounds();

		descBox = new GenericDescriptionVBox(Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	private void initSINs() {
		secSINs = new SINSection(ResourceI18N.get(RES, "page.life.section.sins"));
		secSINs.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secSINs, 4);
		FlexGridPane.setMinHeight(secSINs, 6);
		FlexGridPane.setMediumWidth(secSINs, 5);
		FlexGridPane.setMediumHeight(secSINs, 7);
	}

	//-------------------------------------------------------------------
	private void initContacts() {
		secContacts = new ContactSection(ResourceI18N.get(RES, "page.life.section.contacts"));
		secContacts.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secContacts, 4);
		FlexGridPane.setMinHeight(secContacts, 6);
		FlexGridPane.setMediumWidth(secContacts, 5);
		FlexGridPane.setMediumHeight(secContacts, 9);

		cbExtended = new CheckBox(Shadowrun6Rules.CHARGEN_EXTENDED_CONTACT.getName(Locale.getDefault()));
		secContacts.setSecondaryContent(cbExtended);
	}

	//-------------------------------------------------------------------
	private void initLifestyles() {
		secLifestyles = new LifestyleSection<SR6Lifestyle>(ResourceI18N.get(RES, "page.life.section.lifestyles"), () -> new SR6Lifestyle(Shadowrun6Core.getItem(LifestyleQuality.class, "low")));
		secLifestyles.getListView().setCellFactory( lv -> new SR6LifestyleListCell( () -> ctrl));
		secLifestyles.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secLifestyles, 4);
		FlexGridPane.setMinHeight(secLifestyles, 6);
		//FlexGridPane.setMediumWidth(secLifestyles, 6);
	}

	//-------------------------------------------------------------------
	private void initBackgrounds() {
		taBabyBackground = createBackgroundArea();
		taChildhoodBackground = createBackgroundArea();
		taAdultBackground = createBackgroundArea();
		taBackgroundNotes = createBackgroundArea();

		VBox backgroundContent = new VBox(8,
				new Label(ResourceI18N.get(RES, "page.life.background.baby")),
				taBabyBackground,
				new Label(ResourceI18N.get(RES, "page.life.background.childhood")),
				taChildhoodBackground,
				new Label(ResourceI18N.get(RES, "page.life.background.adult")),
				taAdultBackground,
				new Label(ResourceI18N.get(RES, "page.life.background.notes")),
				taBackgroundNotes
				);
		secBackground = new Section(ResourceI18N.get(RES, "page.life.section.background"), backgroundContent);
		secBackground.setMaxWidth(Double.MAX_VALUE);
		secBackground.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secBackground, 4);
		FlexGridPane.setMinHeight(secBackground, 8);
		FlexGridPane.setMediumWidth(secBackground, 5);
		FlexGridPane.setMediumHeight(secBackground, 9);
	}

	//-------------------------------------------------------------------
	private TextArea createBackgroundArea() {
		TextArea area = new TextArea();
		area.setWrapText(true);
		area.setPrefRowCount(3);
		area.setMaxWidth(Double.MAX_VALUE);
		return area;
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secContacts, secSINs, secLifestyles, secBackground);

		layout = new OptionalNodePane(flex, new Label(ResourceI18N.get(RES,"select.for.description")));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secSINs.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
//		secElectro.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secLifestyles.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secContacts.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		taBabyBackground.textProperty().addListener((ov,o,n) -> {
			if (!updatingBackgroundFields && ctrl!=null)
				ctrl.getModel().setLifepathBabyBackground(n);
		});
		taChildhoodBackground.textProperty().addListener((ov,o,n) -> {
			if (!updatingBackgroundFields && ctrl!=null)
				ctrl.getModel().setLifepathChildhoodBackground(n);
		});
		taAdultBackground.textProperty().addListener((ov,o,n) -> {
			if (!updatingBackgroundFields && ctrl!=null)
				ctrl.getModel().setLifepathAdultBackground(n);
		});
		taBackgroundNotes.textProperty().addListener((ov,o,n) -> {
			if (!updatingBackgroundFields && ctrl!=null)
				ctrl.getModel().setBackgroundNotes(n);
		});
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox( Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}

	//-------------------------------------------------------------------
	private void showDescription(Contact n) {
		logger.log(Level.INFO, "Show contact type "+n);
		if (n==null || n.getType()==null) {
			layout.setOptional(null);
		} else {
			ContactType t = n.getType();
			GenericDescriptionVBox desc = new GenericDescriptionVBox(
					Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault())
					);
			desc.setData(t.getName(Locale.getDefault()), null, t.getDescription(Locale.getDefault()));
			layout.setOptional( desc);
			layout.setTitle(t.getName(Locale.getDefault()));
		}
	}

	//-------------------------------------------------------------------
	private void showDescription(SIN n) {
		logger.log(Level.INFO, "Show SIN "+n);
		if (n==null ) {
			layout.setOptional(null);
		} else {
			FakeRating rating = n.getQuality();
			GenericDescriptionVBox desc = new GenericDescriptionVBox( Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()));
			desc.setData(rating.name(), null, n.getDescription());
			layout.setOptional( desc);
			layout.setTitle(rating.name());
		}
	}

	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		this.ctrl = ctrl;

		secContacts.updateController(ctrl);
		secSINs.updateController(ctrl);
		secLifestyles.updateController(ctrl);

		if (ctrl.getClass().getSimpleName().contains("Generator")) {
			secContacts.setMode(Mode.BACKDROP);
		} else {
			secContacts.setMode(Mode.REGULAR);
		}

		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secContacts.refresh();
		secSINs.refresh();
		secLifestyles.refresh();
		refreshBackgrounds();
	}

	//-------------------------------------------------------------------
	private void refreshBackgrounds() {
		if (ctrl==null)
			return;
		updatingBackgroundFields = true;
		try {
			setTextIfChanged(taBabyBackground, ctrl.getModel().getLifepathBabyBackground());
			setTextIfChanged(taChildhoodBackground, ctrl.getModel().getLifepathChildhoodBackground());
			setTextIfChanged(taAdultBackground, ctrl.getModel().getLifepathAdultBackground());
			setTextIfChanged(taBackgroundNotes, ctrl.getModel().getBackgroundNotes());
		} finally {
			updatingBackgroundFields = false;
		}
	}

	//-------------------------------------------------------------------
	private void setTextIfChanged(TextArea area, String value) {
		String text = value!=null?value:"";
		if (!text.equals(area.getText()))
			area.setText(text);
	}

}
