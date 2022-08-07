package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.chargen.jfx.section.ContactSection;
import de.rpgframework.shadowrun.chargen.jfx.section.LifestyleSection;
import de.rpgframework.shadowrun.chargen.jfx.section.SINSection;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6LifestyleListCell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class CareerPage extends Page {

	private final static Logger logger = System.getLogger(CareerPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private transient SR6CharacterController ctrl;
	
//	private SINSection secSINs;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public CareerPage() {
		super(ResourceI18N.get(RES, "page.career.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
//		initSINs();
	}
	
//	//-------------------------------------------------------------------
//	private void initSINs() {
//		secSINs = new SINSection(ResourceI18N.get(RES, "page.life.section.sins"));
//		secSINs.setMaxHeight(Double.MAX_VALUE);
//		FlexGridPane.setMinWidth(secSINs, 4);
//		FlexGridPane.setMinHeight(secSINs, 6);
//		FlexGridPane.setMediumWidth(secSINs, 6);
//		FlexGridPane.setMediumHeight(secSINs, 9);
//	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
//		flex.getChildren().addAll(secContacts, secSINs, secLifestyles);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
//		secSINs.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
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
			GenericDescriptionVBox desc = new GenericDescriptionVBox( null);
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
			GenericDescriptionVBox desc = new GenericDescriptionVBox( null);
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
		
//		secSINs.updateController(ctrl);
//		
//		if (ctrl.getClass().getSimpleName().contains("Generator")) {
//			secContacts.setMode(Mode.BACKDROP);
//		} else {
//			secContacts.setMode(Mode.REGULAR);
//		}

		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
//		secSINs.refresh();
	}

}
