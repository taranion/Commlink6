package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.controlsfx.control.ToggleSwitch;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.public_skins.GridPaneTableViewSkin.HeaderLine;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.ISkillController;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.ISR6PointBuyGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.karma.SR6KarmaSkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySkillGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySkillGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.SR6SkillTablePrioSkin;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SRSkillSettingsPane;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author prelle
 *
 */
public class SR6WizardPageSkills extends WizardPageSkills<SR6Skill, SR6SkillValue, Shadowrun6Character> {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	private final static Logger logger = System.getLogger(SR6WizardPageSkills.class.getPackageName()+".skill");

	protected NumberUnitBackHeader backHeaderCP;
	private ToggleSwitch tsExpertMode;
	private Label lbPoints, lbPoints2, lbPoints3;

	//-------------------------------------------------------------------
	public SR6WizardPageSkills(Wizard wizard, SR6CharacterGenerator charGen) {
		super(wizard, charGen);
		logger.log(Level.INFO, "Created with charGen="+charGen);
		if (getContent()==null) {
			logger.log(Level.ERROR, "No content");
		}
		bxDescription.setResolver( req -> Shadowrun6Tools.getRequirementString(req, Locale.getDefault()));

		table.setActionCallback(sVal -> openActionDialog(sVal));
		knowTable.setActionCallback(sVal -> openActionDialog(sVal));
		//table.setSelectedListModifier(data -> injectHeader(data));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills#initComponents()
	 */
	protected void initComponents() {
		super.initComponents();
		tsExpertMode = new ToggleSwitch("Expert");
		tsExpertMode.visibleProperty().bind(table.expertModeAvailableProperty());
		lbPoints = new Label("?");
		lbPoints2 = new Label("?");
		lbPoints3 = new Label("?");
		lbPoints.setStyle("-fx-text-fill: -fx-text-base-color");
		lbPoints2.setStyle("-fx-text-fill: -fx-text-base-color");
		lbPoints3.setStyle("-fx-text-fill: -fx-text-base-color");

		btnAddKnow.setText("+"+Shadowrun6Core.getSkill("knowledge").getName());
		btnAddLang.setText("+"+Shadowrun6Core.getSkill("language").getName());

		table.setCellVisibilityFactory( v -> {
			return !(v.getKey().equals("knowledge") || v.getKey().equals("language"));} );
	}

	//-------------------------------------------------------------------
	protected void initBackHeader() {
		backHeaderKarma = new NumberUnitBackHeader(ResourceI18N.get(UI, "label.karma"));
		backHeaderKarma.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeaderKarma, new Insets(0,10,0,10));
		backHeaderCP = new NumberUnitBackHeader(ResourceI18N.get(RES, "label.pointbuy.cp"));
		backHeaderCP.setVisible(true);
		HBox.setMargin(backHeaderCP, new Insets(0,10,0,10));

		Region buf = new Region();
		buf.setMaxWidth(Double.MAX_VALUE);
		HBox box = new HBox(backHeaderKarma, backHeaderCP);
		HBox backHeader = new HBox(10, box, buf, new SymbolIcon("setting"));
		HBox.setHgrow(buf, Priority.ALWAYS);
		//backHeader.setMaxWidth(Double.MAX_VALUE);
		HBox.setMargin(box, new Insets(0,0,0,10));
		HBox.setMargin(backHeader.getChildren().get(2), new Insets(0,10,0,0));
		super.setBackHeader(backHeader);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills#initInteractivity()
	 */
	@Override
	protected void initInteractivity() {
		super.initInteractivity();

		setOnExtraActionHandler( button -> onExtraAction(button));

		table.useExpertModeProperty().bind(tsExpertMode.selectedProperty());
		tsExpertMode.visibleProperty().bind(table.expertModeAvailableProperty());

		showAllSkillsProperty().addListener( (ov,o,n) -> refresh());
	}

	//-------------------------------------------------------------------
	protected void addKnowledgeClicked() {
		SR6Skill skill = Shadowrun6Core.getSkill("knowledge");
		logger.log(Level.DEBUG, "addClicked for {0}", skill);
		Decision[] dec = makeDecision(skill);
		if (dec == null)
			return;
		OperationResult<SR6SkillValue> ret = charGen.getSkillController().select(skill, dec);
		logger.log(Level.WARNING, "Add result was {0}", ret);
	}

	//-------------------------------------------------------------------
	protected void addLanguageClicked() {
		SR6Skill skill = Shadowrun6Core.getSkill("language");
		logger.log(Level.DEBUG, "addClicked for {0}", skill);
		Decision[] dec = makeDecision(skill);
		if (dec == null)
			return;
		OperationResult<SR6SkillValue> ret = charGen.getSkillController().select(skill, dec);
		logger.log(Level.WARNING, "Add result was {0}", ret);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills#getGeneratorLine()
	 */
	@Override
	protected Pane getGeneratorLine() {
		ISkillController<SR6Skill, SR6SkillValue> skillCtrl = charGen.getSkillController();
		if (skillCtrl instanceof SR6PrioritySkillGenerator) {
//			tsExpertMode = new ToggleSwitch("Expert");
//			tsExpertMode.visibleProperty().bind(table.expertModeAvailableProperty());
			Label hdPoints1 = new Label(ResourceI18N.get(RES, "page.skills.head.points")+":");
			Label hdPoints2 = new Label(ResourceI18N.get(RES, "page.skills.head.points2")+":");

			HBox line = new HBox(5, tsExpertMode, hdPoints1, lbPoints, hdPoints2, lbPoints2);
			HBox.setMargin(hdPoints2, new Insets(0,0,0,10));
			return line;
		} else if (skillCtrl instanceof SR6PointBuySkillGenerator) {
//			tsExpertMode = new ToggleSwitch("Expert");
//			tsExpertMode.visibleProperty().bind(table.expertModeAvailableProperty());

			Label hdPoints1 = new Label(((SR6PointBuySkillGenerator)skillCtrl).getColumn1()+":");
			Label hdPoints2 = new Label(ResourceI18N.get(RES, "page.skills.head.pointbuy_converted")+":");

			HBox line = new HBox(5, tsExpertMode, hdPoints1, lbPoints, hdPoints2, lbPoints2);
			HBox.setMargin(hdPoints2, new Insets(0,0,0,10));
			return line;
		} else if (skillCtrl instanceof SR6KarmaSkillGenerator) {
		} else {
			System.err.println("SR6WizardPageSkills: No generator line for "+skillCtrl);
		}
		return null;
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills#getTableForController(de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator)
//	 */
//	@Override
//	protected ShadowrunSkillTable<SR6Skill,SR6SkillValue, Shadowrun6Character> getTableForController(
//			IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, ?, Shadowrun6Character> controller) {
//		logger.log(Level.WARNING, "getTableForController("+controller+")");
//		// TODO Auto-generated method stub
//		IShadowrunCharacterGenerator<SR6Skill, SR6SkillValue, ?, Shadowrun6Character> realCtrl = controller;
//		if (controller instanceof GeneratorWrapper) {
//			realCtrl = ((GeneratorWrapper)controller).getWrapped();
//		}
//
//		ShadowrunSkillTable<SR6Skill,SR6SkillValue, Shadowrun6Character> ret = new ShadowrunSkillTable<>(controller, Mode.GENERATE);
//		if (realCtrl instanceof IPriorityGenerator) {
//			ret.setSkin(new SR6SkillTablePrioSkin(ret));
//			backHeaderCP.setVisible(false);
//		} else if (realCtrl instanceof PointBuyCharacterGenerator) {
//			ret.setSkin(new SR6SkillTablePointBuySkin(ret));
//			backHeaderCP.setVisible(true);
//		} else {
//			logger.log(Level.ERROR, "Don't know what to return for "+realCtrl);
//			System.err.println("SR6WizardPageSkills: Don't know what to return for "+realCtrl);
//		}
//		return ret;
//	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSkills#makeDecision(de.rpgframework.shadowrun.AShadowrunSkill)
	 */
	@Override
	protected Decision[] makeDecision(SR6Skill skill) {
		logger.log(Level.WARNING, "overriding makeDecision() in "+getClass());
		ChoiceSelectorDialog<SR6Skill, SR6SkillValue> dialog = new ChoiceSelectorDialog<>(super.charGen.getSkillController());
		return dialog.apply(skill, skill.getChoices());
	}

	//-------------------------------------------------------------------
	private CloseType openActionDialog(SR6SkillValue sVal) {
		logger.log(Level.INFO, "openActionDialog({0})", sVal);

		SRSkillSettingsPane pane = new SRSkillSettingsPane(sVal, charGen.getSkillController());
		ManagedDialog dialog = new ManagedDialog("Settings", pane, CloseType.OK);
		CloseType close = FlexibleApplication.getInstance().showAlertAndCall(dialog, null);
		return close;
	}

	//-------------------------------------------------------------------
	private void injectHeader(List<SR6SkillValue> data) {
		boolean hadKnowledgeHeader = false;
		boolean hadLanguageHeader = false;
		for (int i=0; i<data.size(); i++) {
			SR6SkillValue sVal = data.get(i);
			SR6Skill skill = sVal.getModifyable();

			if (sVal instanceof SR6SkillValueHeader) {
				if (skill.getType()==SkillType.KNOWLEDGE) hadKnowledgeHeader=true;
				if (skill.getType()==SkillType.LANGUAGE ) hadLanguageHeader=true;
				continue;
			}

			if (skill.getType()==SkillType.KNOWLEDGE && !hadKnowledgeHeader) {
				SR6SkillValueHeader toAdd = new SR6SkillValueHeader(skill.getType().getName(Locale.getDefault()));
				toAdd.setResolved(skill);
				data.add(i++, toAdd);
				hadKnowledgeHeader=true;
				continue;
			}
			if (skill.getType()==SkillType.LANGUAGE && !hadLanguageHeader) {
				SR6SkillValueHeader toAdd = new SR6SkillValueHeader(skill.getType().getName(Locale.getDefault()));
				toAdd.setResolved(skill);
				data.add(i++, toAdd);
				hadLanguageHeader=true;
				continue;
			}
		}

	}

	//-------------------------------------------------------------------
	private void onExtraAction(CloseType button) {
		switch (button) {
		case RANDOMIZE:
			charGen.getSkillController().roll();
			refresh();
			break;
		default:
			logger.log(Level.WARNING, "ToDo: handle "+button);
		}
	}

	//-------------------------------------------------------------------
	protected void refresh() {
		logger.log(Level.WARNING, "refresh() - current table controller is "+table.getController()+" - it should be "+charGen.getSkillController());
		super.refresh();
		ISkillController<SR6Skill, SR6SkillValue> skillCtrl = charGen.getSkillController();
		if (skillCtrl instanceof SR6PrioritySkillGenerator) {
			lbPoints .setText( String.valueOf( ((SR6PrioritySkillGenerator)skillCtrl).getPointsLeft()));
			lbPoints2.setText( String.valueOf( ((SR6PrioritySkillGenerator)skillCtrl).getPointsLeft2()));
			backHeaderCP.setVisible(false);
			if (!(table.getSkin() instanceof SR6SkillTablePrioSkin)) {

			}
		} else
		if (skillCtrl instanceof SR6PointBuySkillGenerator) {
			lbPoints .setText( String.valueOf( ((SR6PointBuySkillGenerator)skillCtrl).getPointsLeft()));
			lbPoints2.setText( String.valueOf( ((SR6PointBuySkillGenerator)skillCtrl).getConvertiblePoints() ));
			backHeaderCP.setValue( ((ISR6PointBuyGenerator)charGen).getSettings().characterPoints );
			backHeaderCP.setVisible(true);
		} else {
			backHeaderCP.setVisible(false);
		}
	}

}

class SR6SkillValueHeader extends SR6SkillValue implements HeaderLine {

	private String name;

	public SR6SkillValueHeader(String header) {
		this.name = header;
	}

	public String getName() {
		return name;
	}
}

