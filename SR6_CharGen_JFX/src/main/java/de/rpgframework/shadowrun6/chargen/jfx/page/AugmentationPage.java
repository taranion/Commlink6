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
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.EssenceSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.GearSection;
import de.rpgframework.shadowrun6.items.ItemType;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class AugmentationPage extends Page {

	private final static Logger logger = System.getLogger(AugmentationPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private EssenceSection secTrans;
	private GearSection secCyber;
	private GearSection secBio;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private GenericDescriptionVBox descBox ;

	//-------------------------------------------------------------------
	public AugmentationPage() {
		super(ResourceI18N.get(RES, "page.augmentation.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initEssence();
		initCyberware();
		initBioware();
		
		descBox = new GenericDescriptionVBox<>((r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
	}
	
	//-------------------------------------------------------------------
	private void initCyberware() {
		secCyber = new GearSection(
				ResourceI18N.get(RES, "page.augmentation.section.cyberware"),
				ItemType.CYBERWARE
				);
		secCyber.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secCyber, 4);
		FlexGridPane.setMinHeight(secCyber, 6);
		FlexGridPane.setMediumWidth(secCyber, 5);
		FlexGridPane.setMediumHeight(secCyber, 6);
		FlexGridPane.setMaxWidth(secCyber, 5);
		FlexGridPane.setMaxHeight(secCyber, 9);
	}
	
	//-------------------------------------------------------------------
	private void initBioware() {
		secBio = new GearSection(
				ResourceI18N.get(RES, "page.augmentation.section.bioware"),
				ItemType.BIOWARE
				);
		secBio.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secBio, 4);
		FlexGridPane.setMinHeight(secBio, 6);
		FlexGridPane.setMediumWidth(secBio, 5);
		FlexGridPane.setMediumHeight(secBio, 6);
		FlexGridPane.setMaxWidth(secBio, 5);
		FlexGridPane.setMaxHeight(secBio, 9);
	}
	
	//-------------------------------------------------------------------
	private void initEssence() {
		secTrans = new EssenceSection(ResourceI18N.get(RES, "page.augmentation.section.essence"));
		secTrans.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secTrans, 4);
		FlexGridPane.setMinHeight(secTrans, 4);
		FlexGridPane.setMediumWidth(secTrans, 4);
		FlexGridPane.setMediumHeight(secTrans, 4);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secTrans, secCyber, secBio);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secCyber.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secBio  .showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secTrans.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItem n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n));
			layout.setTitle(n.getName());
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		secCyber.updateController(ctrl);
		secBio  .updateController(ctrl);
		secTrans.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secCyber.refresh();
		secBio  .refresh();
		secTrans.refresh();
	}

}
