package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import com.onexip.flexboxfx.FlexBox;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.section.AppearanceSection;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.AttributeSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.BasicDataSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SkillSection;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MatrixPage extends Page {

	private final static Logger logger = System.getLogger(MatrixPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
//	private SkillSection secNormal;
//	private SkillSection secKnowl;
//	private SkillSection secLang;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;
	
	private GenericDescriptionVBox descBox ;

	//-------------------------------------------------------------------
	public MatrixPage() {
		super(ResourceI18N.get(RES, "page.matrix.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initBaseData();
//		initKnowledge();
//		initLanguage();
		
		descBox = new GenericDescriptionVBox<>((r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
	}
	
	//-------------------------------------------------------------------
	private void initBaseData() {
//		secNormal = new SkillSection(ResourceI18N.get(RES, "page.skills.section.normal"), SkillType.ACTION);
//		secNormal.setMaxHeight(Double.MAX_VALUE);
//		FlexGridPane.setMinWidth(secNormal, 4);
//		FlexGridPane.setMinHeight(secNormal, 6);
//		FlexGridPane.setMediumWidth(secNormal, 8);
//		FlexGridPane.setMediumHeight(secNormal, 4);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		
		flex = new FlexGridPane();
		flex.setSpacing(20);
//		flex.getChildren().addAll(secNormal, secKnowl, secLang);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
//		secNormal.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
//		secKnowl.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
//		secLang .selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(SR6SkillValue n) {
		if (n==null) {
			layout.setOptional(new Label("Langer Text"));
		} else {
			descBox.setData(n.getModifyable());
			layout.setOptional(descBox);
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
//		secKnowl.updateController(ctrl);
//		secLang.updateController(ctrl);
//		secNormal.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
//		secNormal.refresh();
//		secLang.refresh();
//		secKnowl.refresh();
	}

}
