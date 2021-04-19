package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.Section;

import com.onexip.flexboxfx.FlexBox;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.RPGFrameworkJavaFX;
import de.rpgframework.jfx.section.AppearanceSection;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.PriorityCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.QualityGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.section.AttributeSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.BasicDataSection;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/**
 * @author prelle
 *
 */
public class BasicDataPage extends Page {
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(BasicDataPage.class.getName());

	private Section secBaseData;
	private AppearanceSection secPortrait;
	private FlexBox flex;
	private Section secAttrib;
	private QualitySection secQualities;
	
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
		
		OptionalNodePane layout = new OptionalNodePane(scroll, new Label("Langer Text"));
		layout.setTitle("Erklärung");
		setContent(layout);
		setTitle("Basics");
		
		
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
		secBaseData = new BasicDataSection();
		secBaseData.setMaxHeight(Double.MAX_VALUE);
	}
	
	//-------------------------------------------------------------------
	private void initPortrait() {
		secPortrait = new AppearanceSection();
		Image img = new Image("/mugshot.jpg");
		secPortrait.iView.setImage(img);
	}
	
	//-------------------------------------------------------------------
	private void initAttributes() {
		PriorityCharacterGenerator charGen = new PriorityCharacterGenerator();
		charGen.start(new Shadowrun6Character());
		secAttrib = new AttributeSection(ResourceI18N.get(RES, "page.basicdata.section.attributes.title"), charGen, null);
	}
	
	//-------------------------------------------------------------------
	private void initQualities() {
		PriorityCharacterGenerator charGen = new PriorityCharacterGenerator();
		Shadowrun6Character model = new Shadowrun6Character();
		model.addQuality(new QualityValue(Shadowrun6Core.getItem(Quality.class, "built_tough"), 2));
		charGen.start(model);
		secQualities = new QualitySection(ResourceI18N.get(RES, "page.basicdata.section.qualities.title"), new QualityGenerator(charGen));
	}
	
}
