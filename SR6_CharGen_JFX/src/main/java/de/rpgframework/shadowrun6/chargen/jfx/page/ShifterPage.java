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
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6HistoryElementListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SR6RewardPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.BasicDataSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.BodyPlanConfigSection;
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
public class ShifterPage extends Page {

	private final static Logger logger = System.getLogger(ShifterPage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private transient SR6CharacterController ctrl;

	private MetamagicOrEchoSection secMeta;

	private HBox flex;
	private ImageView ivShape;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public ShifterPage() {
		super(ResourceI18N.get(RES, "page.shifter.title"));
		initMetamagic();
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		ivShape = new ImageView();
		ivShape.setImage(new Image(ShifterPage.class.getResourceAsStream("Sechsarm.png")));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new HBox();
		flex.setSpacing(20);
		flex.getChildren().addAll(secMeta);

		layout = new OptionalNodePane(flex, new Label(ResourceI18N.get(RES,"select.for.description")));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		secSINs.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void initMetamagic() {
		secMeta = new MetamagicOrEchoSection(
				ResourceI18N.get(RES, "page.shifter.section.animalism"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				MetamagicOrEcho.Type.ANIMALISM
				);
		secMeta.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secMeta, 4);
		FlexGridPane.setMinHeight(secMeta, 6);
		FlexGridPane.setMediumWidth(secMeta, 5);
		FlexGridPane.setMediumHeight(secMeta, 8);
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

		secMeta.updateController(ctrl);

		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secMeta.refresh();
//		secHistory.refresh();
//
//		secHistory.setData( GenericRPGTools.convertToHistoryElementList(ctrl.getModel(), false));
	}

}
