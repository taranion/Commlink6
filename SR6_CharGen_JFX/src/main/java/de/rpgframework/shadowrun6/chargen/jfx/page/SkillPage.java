package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.Page.Mode;
import com.onexip.flexboxfx.FlexBox;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.section.SkillSection;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SkillPage extends Page {
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SkillPage.class.getName());
	
	private SR6CharacterController ctrl;
	
	private SkillSection secNormal;
	private SkillSection secCombat;
	private SkillSection secMagic;
	
	private OptionalNodePane layout;
	private GenericDescriptionVBox<SR6Skill> descBox;

	//-------------------------------------------------------------------
	public SkillPage(SR6CharacterController ctrl) {
		super(ResourceI18N.get(RES, "page.skills.title"));
		this.ctrl = ctrl;
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		secNormal = new SkillSection(ResourceI18N.get(RES, "page.skills.section.normal"), ctrl, SkillType.ACTION);
		secNormal.setMaxHeight(Double.MAX_VALUE);
		secCombat = new SkillSection(ResourceI18N.get(RES, "page.skills.section.combat"), ctrl, SkillType.COMBAT);
		secCombat.setMaxHeight(Double.MAX_VALUE);
		secMagic = new SkillSection(ResourceI18N.get(RES, "page.skills.section.magic"), ctrl, SkillType.MAGIC);
		secMagic.setMaxHeight(Double.MAX_VALUE);
		
		descBox = new GenericDescriptionVBox<>((r) -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		VBox column2 = new VBox(20, secCombat, secMagic);
		
		FlexBox flow1 = new FlexBox();
		flow1.setVerticalSpace(20);
		flow1.setHorizontalSpace(20);
		flow1.getChildren().addAll(secNormal, column2);
		
		layout = new OptionalNodePane(flow1, new Label("Langer Text"));
		layout.setTitle("Erklärung");
		setContent(layout);
//		setTitle("Basics");
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secNormal.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
		secCombat.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
		secMagic .selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
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

}
