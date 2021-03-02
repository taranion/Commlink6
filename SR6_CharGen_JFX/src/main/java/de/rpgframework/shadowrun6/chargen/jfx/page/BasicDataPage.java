package de.rpgframework.shadowrun6.chargen.jfx.page;

import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.Section;
import org.prelle.javafx.SymbolIcon;

import com.onexip.flexboxfx.FlexBox;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class BasicDataPage extends Page {

	private Section secBaseData;
	private AppearanceSection secPortrait;
	private FlowPane flow1;
	private Section secAttrib;
	private Section secQualities;
	
	private Button btnPortraitEdit;
	private Button btnPortraitRemove;
	
	//-------------------------------------------------------------------
	public BasicDataPage() {
		// Flow 1
		initBaseData();
		initPortrait();	
		
//		flow1 = new FlowPane(secBaseData, secPortrait);
//		flow1.setHgap(20);
//		flow1.setVgap(20);
//		secBaseData.setMaxWidth(Double.MAX_VALUE);
		
		FlexBox flex = new FlexBox();
		flex.setHorizontalSpace(20);
		flex.setVerticalSpace(20);
		flex.getChildren().addAll(secBaseData, secPortrait);
		
		// Flow 2
		initAttributes();
		initQualities();
//		FlowPane flow2 = new FlowPane(secAttrib, secQualities);
//		flow2.setHgap(20);
//		flow2.setVgap(20);
		
		FlexBox flex2 = new FlexBox();
		flex2.setHorizontalSpace(20);
		flex2.setVerticalSpace(20);
		flex2.getChildren().addAll(secAttrib, secQualities);
				
		
		OptionalNodePane layout = new OptionalNodePane(new VBox(20,flex, flex2), new Label("Langer Text"));
		layout.setTitle("Erklärung");
		setContent(layout);
		setTitle("Basics");
	}
	
	//-------------------------------------------------------------------
	private void initBaseData() {
		secBaseData = new BasicDataSection();
	}
	
	//-------------------------------------------------------------------
	private void initPortrait() {
		secPortrait = new AppearanceSection();
		Image img = new Image("/mugshot.jpg");
		secPortrait.iView.setImage(img);
//		btnPortraitEdit = new Button(null, new SymbolIcon("Edit"));
//		btnPortraitEdit.setTooltip(new Tooltip("Change image"));
//		btnPortraitRemove = new Button(null, new SymbolIcon("delete"));
//		btnPortraitRemove.setTooltip(new Tooltip("Delete image"));
//		
//		ImageView iView = new ImageView();
//		iView.setFitWidth(200);
//		iView.setFitHeight(200);
//		Image img = new Image("/mugshot.jpg");
//		iView.setImage(img);
//		secPortrait = new Section("Portrait", iView);
//		secPortrait.getButtons().addAll(btnPortraitEdit, btnPortraitRemove);
	}
	
	//-------------------------------------------------------------------
	private void initAttributes() {
		PriorityCharacterGenerator charGen = new PriorityCharacterGenerator();
		charGen.start(new Shadowrun6Character());
		secAttrib = new AttributeSection("Attributes", charGen, null);
	}
	
	//-------------------------------------------------------------------
	private void initQualities() {
		PriorityCharacterGenerator charGen = new PriorityCharacterGenerator();
		Shadowrun6Character model = new Shadowrun6Character();
		model.addQuality(new QualityValue(Shadowrun6Core.getItem(Quality.class, "built_tough"), 2));
		charGen.start(model);
		secQualities = new QualitySection("Qualities", new QualityGenerator(charGen));
	}
	
}
