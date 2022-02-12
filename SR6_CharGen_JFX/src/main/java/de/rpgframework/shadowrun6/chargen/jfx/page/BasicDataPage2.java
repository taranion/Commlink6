package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.Section;
import org.prelle.javafx.layout.FlexGridPane;

import com.onexip.flexboxfx.FlexBox;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.RPGFrameworkJavaFX;
import de.rpgframework.jfx.section.AppearanceSection;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.AttributeSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.BasicDataSection;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/**
 * @author prelle
 *
 */
public class BasicDataPage2 extends Page implements IShadowrunCharacterControllerProvider<SR6CharacterController> {

	private final static Logger logger = System.getLogger(BasicDataPage2.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(BasicDataPage.class.getName());

	private SR6CharacterController control;
	
	private BasicDataSection secBaseData;
	private AppearanceSection secPortrait;
	private FlexGridPane flex;
	private AttributeSection secAttrib;
	private QualitySection secQualities;
	
	private OptionalNodePane layout;
	
	//-------------------------------------------------------------------
	public BasicDataPage2() {
		logger.log(Level.DEBUG, "init<>");
		// Flow 1
		initBaseData();
		initPortrait();	
		
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secBaseData, secPortrait);
		
		// Flow 2
		initAttributes();
		initQualities();
		flex.getChildren().addAll(secAttrib, secQualities);
//		FlexBox flex2 = new FlexBox();
//		flex2.setHorizontalSpace(20);
//		flex2.setVerticalSpace(20);
//		flex2.getChildren().addAll(secAttrib, secQualities);
		
		ScrollPane scroll = new ScrollPane(new VBox(20,flex));
		scroll.setFitToWidth(true);
		
		layout = new OptionalNodePane(scroll, new Label("Langer Text 2"));
		layout.setTitle("Erklärung");
		setContent(layout);
//		setTitle("Basics");
		
		
//		secQualities.showHelpForProperty().addListener( (ov,o,n) -> {
//			System.out.println("secQuality.showHelpFor: "+n);
//			if (n==null) {
//				layout.setOptional(null);
//			} else {
//				Label descTitle = new Label();
//				descTitle.getStyleClass().add(JavaFXConstants.STYLE_HEADING3);
//				descTitle.setStyle("-fx-text-fill: highlight");
//				Label descSources = new Label();
//				TextFlow description = new TextFlow();
//				
//				descTitle.setText(n.getName());
//				QualityValue v = (QualityValue)n;
//				descSources.setText(RPGFrameworkJavaFX.createSourceText(v.getResolved()));
//				
//				VBox box = new VBox(descTitle, descSources, description);
//				RPGFrameworkJavaFX.parseMarkupAndFillTextFlow(description, v.getResolved().getDescription());
//				layout.setOptional(box);
//			}
//		});
	}
	
	//-------------------------------------------------------------------
	private void initBaseData() {
		secBaseData = new BasicDataSection(ResourceI18N.get(RES, "page.basicdata.section.basic.title"));
		secBaseData.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secBaseData, 2);
		FlexGridPane.setMinHeight(secBaseData, 2);
//		FlexGridPane.setMaxWidth(secBaseData, 3);
//		FlexGridPane.setMediumHeight(secBaseData, 2);
	}
	
	//-------------------------------------------------------------------
	private void initPortrait() {
		secPortrait = new AppearanceSection();
//		Image img = new Image("/mugshot.jpg");
//		secPortrait.iView.setImage(img);
		FlexGridPane.setMinWidth(secPortrait, 2);
		FlexGridPane.setMediumWidth(secPortrait, 4);
		FlexGridPane.setMinHeight(secPortrait, 3);
		FlexGridPane.setMediumHeight(secPortrait, 2);
	}
	
	//-------------------------------------------------------------------
	private void initAttributes() {
		secAttrib = new AttributeSection(ResourceI18N.get(RES, "page.basicdata.section.attributes.title"), null);
//		((AttributeSection)secAttrib).updateController(ctrl);
		
		FlexGridPane.setMinWidth(secAttrib, 4);
		FlexGridPane.setMediumWidth(secAttrib, 5);
		FlexGridPane.setMinHeight(secAttrib, 3);
	}
	
	//-------------------------------------------------------------------
	private void initQualities() {
		secQualities = new QualitySection(
				ResourceI18N.get(RES, "page.basicdata.section.qualities.title"),
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
//		((QualitySection)secQualities).updateController(ctrl);
		FlexGridPane.setMinWidth(secQualities, 2);
		FlexGridPane.setMinHeight(secQualities, 3);
		secQualities.showHelpForProperty().addListener( (ov,o,n) -> {
			System.getLogger("shadowrun6").log(Level.INFO, "ShowHelpFor "+n);
			if (n!=null) {
				layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
				layout.setTitle(n.getModifyable().getName());
			}
		});
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		this.control = ctrl;
		((BasicDataSection)secBaseData).updateController(ctrl);
		((AttributeSection)secAttrib).updateController(ctrl);
		((QualitySection)secQualities).updateController(ctrl);
		((AppearanceSection)secPortrait).updateController(ctrl);
		secBaseData.refresh();
		secAttrib.refresh();
		secQualities.refresh();
		secPortrait.refresh();
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterControllerProvider#getCharacterController()
	 */
	public SR6CharacterController getCharacterController() {
		return control;
	}
	
}
