package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.HistoryElement;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.section.HistoryElementSection;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6HistoryElementListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SR6RewardPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.CreationSection;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class BodyPage extends Page {

	private final static Logger logger = System.getLogger(BodyPage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private transient SR6CharacterController ctrl;

	private HBox flex;
	private ImageView ivShape;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public BodyPage() {
		super(ResourceI18N.get(RES, "page.body.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		ivShape = new ImageView();
		ivShape.setImage(new Image(BodyPage.class.getResourceAsStream("Sechsarm.png")));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new HBox();
		flex.setSpacing(20);
		flex.getChildren().addAll(ivShape);

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
			layout.setOptional( new GenericDescriptionVBox(Shadowrun6Tools.requirementResolver(Locale.getDefault()),
					Shadowrun6Tools.modificationResolver(Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}

	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		this.ctrl = ctrl;

//		secCreation.updateController(ctrl);

		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
//		secCreation.refresh();
//		secHistory.refresh();
//
//		secHistory.setData( GenericRPGTools.convertToHistoryElementList(ctrl.getModel(), false));
	}

}
