package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.ChildhoodGenerator.SimpleSkillController;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.EarlyAdultGenerator;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 */
public class SR6WizardPageLPEarlyAdult extends WizardPage implements ControllerListener {

	private final static Logger logger = System.getLogger(SR6WizardPageLPEarlyAdult.class.getPackageName());
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageLPEarlyAdult.class.getPackageName()+".SR6WizardPages");

	protected SR6CharacterGenerator charGen;
	protected EarlyAdultGenerator earlyAdult;
	private TilePane tpSkills;
	private TilePane tpAttributes;
	private NumberUnitBackHeader backHeader;

	//-------------------------------------------------------------------
	public SR6WizardPageLPEarlyAdult(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		this.earlyAdult = charGen.getWrapped().getEarlyAdultGenerator();
		setTitle(ResourceI18N.get(RES, "page.teenager.title"));
		initComponents();
		initLayout();
		charGen.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tpSkills = new TilePane(10, 10);
		tpSkills.setPrefColumns(4);
		tpAttributes = new TilePane(10, 10);
		tpAttributes.setPrefColumns(4);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		backHeader = new NumberUnitBackHeader("Karma");
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
		super.setBackHeader(backHeader);

		Label lbSkill = new Label(ResourceI18N.get(RES, "page.teenager.skill"));
		lbSkill.setWrapText(true);
		Label lbAttribute = new Label(ResourceI18N.get(RES, "page.teenager.attribute"));
		lbAttribute.setWrapText(true);

		ScrollPane skillScroll = new ScrollPane(tpSkills);
		skillScroll.setFitToWidth(true);
		skillScroll.setPrefViewportHeight(260);
		VBox content = new VBox(20, lbSkill, skillScroll, lbAttribute, tpAttributes);
		content.setPadding(new Insets(0, 0, 10, 0));
		setContent(content);
	}

	//-------------------------------------------------------------------
	private void updateSkillButtons() {
		tpSkills.getChildren().clear();
		if (earlyAdult==null) return;
		SimpleSkillController ctrl = earlyAdult.getSkillController();
		List<SR6Skill> skills = earlyAdult.getAvailableSkills().stream()
				.sorted(Comparator.comparing(SR6Skill::getName))
				.toList();
		for (SR6Skill skill : skills) {
			ToggleButton btn = new ToggleButton(skill.getName());
			btn.setMaxWidth(Double.MAX_VALUE);
			btn.setSelected(ctrl.isSelected(skill));
			btn.setOnAction(ev -> {
				if (ctrl.isSelected(skill)) {
					ctrl.deselect(skill);
				} else {
					ctrl.select(skill);
				}
				refresh();
			});
			tpSkills.getChildren().add(btn);
		}
	}

	//-------------------------------------------------------------------
	private void updateAttributeButtons() {
		tpAttributes.getChildren().clear();
		if (earlyAdult==null) return;
		ShadowrunAttribute selected = charGen.getModel().getCharGenSettings(de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathSettings.class).getEarlyAdultAttribute();
		for (ShadowrunAttribute attribute : earlyAdult.getAvailableAttributes()) {
			ToggleButton btn = new ToggleButton(attribute.getName());
			btn.setMaxWidth(Double.MAX_VALUE);
			btn.setSelected(attribute==selected);
			btn.setOnAction(ev -> {
				if (attribute==charGen.getModel().getCharGenSettings(de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathSettings.class).getEarlyAdultAttribute()) {
					earlyAdult.deselectAttribute(attribute);
				} else {
					earlyAdult.selectAttribute(attribute);
				}
				refresh();
			});
			tpAttributes.getChildren().add(btn);
		}
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		boolean isLifepath = charGen.getId().equals("lifepath");
		activeProperty().set(isLifepath);
		if (!isLifepath || earlyAdult==null)
			return;
		backHeader.setValue(charGen.getModel().getKarmaFree());
		updateSkillButtons();
		updateAttributeButtons();
	}

	//-------------------------------------------------------------------
	@Override
	public void pageVisited() {
		refresh();
	}

	//-------------------------------------------------------------------
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.INFO, "RCV " + type + " with " + Arrays.toString(param));
		if (type == BasicControllerEvents.GENERATOR_CHANGED) {
			charGen = (SR6CharacterGenerator) param[0];
			earlyAdult = charGen.getEarlyAdultGenerator();
		}
		if (type == BasicControllerEvents.CHARACTER_CHANGED || type == BasicControllerEvents.GENERATOR_CHANGED) {
			refresh();
		}
	}
}
