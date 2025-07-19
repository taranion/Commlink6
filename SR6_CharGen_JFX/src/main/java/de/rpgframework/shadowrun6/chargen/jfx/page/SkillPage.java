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
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.KnowledgeSkillSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SkillSection;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class SkillPage extends Page {

	private final static Logger logger = System.getLogger(SkillPage.class.getPackageName());

//	private final static ResourceBundle RES = ResourceBundle.getBundle(BasicDataPage.class.getPackageName()+".Pages");
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private SkillSection secNormal;
	private KnowledgeSkillSection secKnowl;
	private SkillSection secLang;

	private FlexGridPane flex;
	private OptionalNodePane layout;

	private GenericDescriptionVBox descBox ;

	//-------------------------------------------------------------------
	public SkillPage() {
		super(ResourceI18N.get(RES, "page.skills.title"));
		initComponents();
		initLayout();
		initInteractivity();
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		initBaseData();
		initKnowledge();
		initLanguage();

		descBox = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	private void initBaseData() {
		secNormal = new SkillSection(ResourceI18N.get(RES, "page.skills.section.normal"), SkillType.COMBAT, SkillType.RESONANCE, SkillType.PHYSICAL, SkillType.SOCIAL, SkillType.TECHNICAL, SkillType.MAGIC);
		secNormal.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secNormal, 4);
		FlexGridPane.setMinHeight(secNormal, 6);
		FlexGridPane.setMediumWidth(secNormal, 6);
		FlexGridPane.setMediumHeight(secNormal, 6);
//		FlexGridPane.setMaxWidth(secNormal, 7);
		FlexGridPane.setMaxHeight(secNormal, 8);
		secNormal.flexWidthProperty().addListener( (ov,o,n) -> {
			FlexGridPane.setMediumWidth(secNormal, (Integer)n);
			flex.refresh();
		});
	}

	//-------------------------------------------------------------------
	private void initKnowledge() {
		secKnowl = new KnowledgeSkillSection(ResourceI18N.get(RES, "page.skills.section.knowledge"));
//		secKnowl.setMaxHeight(Double.MAX_VALUE);
//		secKnowl.setStyle("-fx-min-height: 10em; -fx-pref-height: 30em; -fx-max-width: 15em");
		FlexGridPane.setMinWidth(secKnowl, 4);
		FlexGridPane.setMediumWidth(secKnowl, 4);
		FlexGridPane.setMaxWidth(secKnowl, 5);
		FlexGridPane.setMinHeight(secKnowl, 6);
	}

	//-------------------------------------------------------------------
	private void initLanguage() {
		secLang = new SkillSection(ResourceI18N.get(RES, "page.skills.section.language"), SkillType.LANGUAGE);
		secLang.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secLang, 4);
		FlexGridPane.setMediumWidth(secLang, 4);
		FlexGridPane.setMaxWidth(secLang, 5);
		FlexGridPane.setMinHeight(secLang, 6);
		FlexGridPane.setMediumHeight(secLang, 6);
	}

	//-------------------------------------------------------------------
	private void initLayout() {

		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secNormal, secKnowl, secLang);

		layout = new OptionalNodePane(flex, new Label(ResourceI18N.get(RES,"select.for.description")));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		secNormal.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
		secKnowl.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
		secLang .selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(SR6SkillValue n) {
		if (n==null) {
			layout.setOptional(new Label("Langer Text"));
		} else {
			descBox.setData(n.getModifyable());
			layout.setTitle(n.getModifyable().getName());
			layout.setOptional(descBox);
		}
	}

	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");

		secKnowl.updateController(ctrl);
		secLang.updateController(ctrl);
		secNormal.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		secNormal.refresh();
		secLang.refresh();
		secKnowl.refresh();
	}

}
