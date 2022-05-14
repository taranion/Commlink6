package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.Section;

import com.onexip.flexboxfx.FlexBox;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.RPGFrameworkJavaFX;
import de.rpgframework.jfx.section.AppearanceSection;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SpliMoCharacterController;
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
public class BasicDataPage extends Page implements IShadowrunCharacterControllerProvider<SpliMoCharacterController> {
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(BasicDataPage.class.getName());

	private SpliMoCharacterController control;
	
	private BasicDataSection secBaseData;
	private AppearanceSection secPortrait;
	private FlexBox flex;
	private Section secAttrib;
	private QualitySection secQualities;
	
	private OptionalNodePane layout;
	
	//-------------------------------------------------------------------
	public BasicDataPage() {
		// Flow 1
		initBaseData();
		initPortrait();	
		
		flex = new FlexBox();
		flex.setHorizontalSpace(20);
		flex.setVerticalSpace(20);
		flex.getChildren().addAll(secBaseData, secPortrait);
		
		// Flow 2
		initAttributes();
		initQualities();
		FlexBox flex2 = new FlexBox();
		flex2.setHorizontalSpace(20);
		flex2.setVerticalSpace(20);
		flex2.getChildren().addAll(secAttrib, secQualities);
		
		ScrollPane scroll = new ScrollPane(new VBox(20,flex, flex2));
		scroll.setFitToWidth(true);
		
		layout = new OptionalNodePane(scroll, new Label("Langer Text"));
		layout.setTitle("Erklärung");
		setContent(layout);
//		setTitle("Basics");
		
		
		secQualities.showHelpForProperty().addListener( (ov,o,n) -> {
			System.out.println("secQuality.showHelpFor: "+n);
			if (n==null) {
				layout.setOptional(null);
			} else {
				Label descTitle = new Label();
				descTitle.getStyleClass().add(JavaFXConstants.STYLE_HEADING3);
				descTitle.setStyle("-fx-text-fill: highlight");
				Label descSources = new Label();
				TextFlow description = new TextFlow();
				
				descTitle.setText(n.getName());
				QualityValue v = (QualityValue)n;
				descSources.setText(RPGFrameworkJavaFX.createSourceText(v.getResolved()));
				
				VBox box = new VBox(descTitle, descSources, description);
				RPGFrameworkJavaFX.parseMarkupAndFillTextFlow(description, v.getResolved().getDescription());
				layout.setOptional(box);
			}
		});
	}
	
	//-------------------------------------------------------------------
	private void initBaseData() {
		secBaseData = new BasicDataSection(ResourceI18N.get(RES, "page.basicdata.section.basic.title"));
		secBaseData.setMaxHeight(Double.MAX_VALUE);
	}
	
	//-------------------------------------------------------------------
	private void initPortrait() {
		secPortrait = new AppearanceSection();
//		Image img = new Image("/mugshot.jpg");
//		secPortrait.iView.setImage(img);
	}
	
	//-------------------------------------------------------------------
	private void initAttributes() {
		secAttrib = new AttributeSection(ResourceI18N.get(RES, "page.basicdata.section.attributes.title"), null);
//		((AttributeSection)secAttrib).updateController(ctrl);
	}
	
	//-------------------------------------------------------------------
	private void initQualities() {
		secQualities = new QualitySection(ResourceI18N.get(RES, "page.basicdata.section.qualities.title"),
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
		secQualities.showHelpForProperty().addListener( (ov,o,n) -> {
			System.getLogger("shadowrun6").log(Level.INFO, "ShowHelpFor "+n);
			if (n!=null) {
				layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
			}
		});
//		((QualitySection)secQualities).updateController(ctrl);
	}
	
	//-------------------------------------------------------------------
	public void setController(SpliMoCharacterController ctrl) {
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
	public SpliMoCharacterController getCharacterController() {
		return control;
	}
	
}
