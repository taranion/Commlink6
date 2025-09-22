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
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.AlternateBodySection;
import de.rpgframework.shadowrun6.chargen.jfx.section.CritterPowerSection;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class DrakePage extends Page {

	private final static Logger logger = System.getLogger(DrakePage.class.getPackageName());

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private transient SR6CharacterController ctrl;

	private AlternateBodySection secBody;
	private MetamagicOrEchoSection secMeta;
	private CritterPowerSection   secCrit;

	private HBox flex;
	private ImageView ivShape;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public DrakePage() {
		super(ResourceI18N.get(RES, "page.drake.title"));
		initBody();
		initCritterPower();
		initDracogenesis();
		initComponents();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		ivShape = new ImageView();
		ivShape.setImage(new Image(DrakePage.class.getResourceAsStream("Sechsarm.png")));
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new HBox();
		flex.setSpacing(20);
		flex.getChildren().addAll(secBody,secMeta, secCrit);

		layout = new OptionalNodePane(flex, new Label(ResourceI18N.get(RES,"select.for.description")));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
//		secSINs.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void initBody() {
		secBody = new AlternateBodySection(
				ResourceI18N.get(RES, "page.drake.section.body"), null);
		secBody.setMaxHeight(100);
		FlexGridPane.setMinWidth(secBody, 4);
		FlexGridPane.setMinHeight(secBody, 2);
		FlexGridPane.setMediumWidth(secBody, 5);
		FlexGridPane.setMediumHeight(secBody, 2);
		FlexGridPane.setMaxWidth(secBody, 4);
		FlexGridPane.setMaxHeight(secBody, 2);
	}

	//-------------------------------------------------------------------
	private void initCritterPower() {
		secCrit = new CritterPowerSection() {
			{
				btnAdd.setVisible(false);
				btnDel.setVisible(false);
			}
			public void refresh() {
				logger.log(Level.TRACE, "refresh");

				BodyForm body = ctrl.getModel().getBodyForm(BodyType.DRAKE);
				if (body!=null) {
					setData(body.getCritterPowers());
				}
			}
		};
		secCrit.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secCrit, 4);
		FlexGridPane.setMinHeight(secCrit, 6);
		FlexGridPane.setMediumWidth(secCrit, 5);
		FlexGridPane.setMediumHeight(secCrit, 8);
		secCrit.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n) );
	}

	//-------------------------------------------------------------------
	private void initDracogenesis() {
		secMeta = new MetamagicOrEchoSection(
				ResourceI18N.get(RES, "page.drake.section.dracogenesis"),
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				MetamagicOrEcho.Type.DRACOGENESIS_POWER
				);
		secMeta.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secMeta, 4);
		FlexGridPane.setMinHeight(secMeta, 6);
		FlexGridPane.setMediumWidth(secMeta, 5);
		FlexGridPane.setMediumHeight(secMeta, 8);
		secMeta.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n) );
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

		secBody.updateController(ctrl);
		secCrit.updateController(ctrl);
		secMeta.updateController(ctrl);

		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secBody.refresh();
		secCrit.refresh();
		secMeta.refresh();
	}

}
