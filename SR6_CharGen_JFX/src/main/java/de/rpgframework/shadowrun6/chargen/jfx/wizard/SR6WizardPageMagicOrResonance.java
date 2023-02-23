package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.TitledComponent;
import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.Tradition;
import de.rpgframework.shadowrun.chargen.charctrl.IMagicOrResonanceController;
import de.rpgframework.shadowrun.chargen.gen.IPriorityGenerator;
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageMagicOrResonance;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.karma.KarmaCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SR6WizardPageMagicOrResonance extends WizardPageMagicOrResonance {

	/* For mystic adepts */
	private Label lbTotal;
	private Label lbMagic;
	private Label lbPower;
	private Button btnDec;
	private Button btnInc;
	private TitledComponent tcDist;

	/* For aspected magicians */
	private ChoiceBox<SR6Skill> cbAspectSkill;

	/* For (aspected) magicians and mystic adepts*/
	private ChoiceBox<Tradition> cbTradition;

	//-------------------------------------------------------------------
	public SR6WizardPageMagicOrResonance(Wizard wizard, IShadowrunCharacterGenerator<?, ?, ?,?> charGen) {
		super(wizard, charGen);

		if (charGen.getModel().getCharGenUsed()!=null) {
			if (charGen.getModel().getCharGenUsed().equals("pointbuy")) {
				lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith("page.mortypecell.cost", charGen.getMagicOrResonanceController()));
			} else if (charGen.getModel().getCharGenUsed().equals("karma")) {
				lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith("page.mortypecell.karma", charGen.getMagicOrResonanceController()));
			} else
				lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith("page.mortypecell", charGen.getMagicOrResonanceController()));
		}

		/* For mystic adepts */
		lbTotal = new Label("?");
		lbMagic = new Label("?");
		lbPower = new Label("?");
		btnDec  = new Button("<");
		btnInc  = new Button(">");
		/* For aspected magicians */
		cbAspectSkill = new ChoiceBox<>();
		cbAspectSkill.getItems().addAll(Shadowrun6Core.getSkill("sorcery"), Shadowrun6Core.getSkill("conjuring"), Shadowrun6Core.getSkill("enchanting"));
		cbAspectSkill.setConverter(new StringConverter<SR6Skill>() {
			public String toString(SR6Skill value) {
				if (value==null) return "-";
				return value.getName();
			}
			public SR6Skill fromString(String string) { return null; }
		});
		/* For (aspected) magicians and mystic adepts */
		cbTradition = new ChoiceBox<>();
		cbTradition.getItems().addAll(Shadowrun6Core.getItemList(Tradition.class));
		Collections.sort(cbTradition.getItems(), new Comparator<Tradition>() {
			public int compare(Tradition o1, Tradition o2) {
				// TODO Auto-generated method stub
				return o1.getName().compareTo(o2.getName());
			}
		});
		cbTradition.setConverter(new StringConverter<Tradition>() {
			public String toString(Tradition value) {
				if (value==null) return "-";
				return value.getName();
			}
			public Tradition fromString(String string) { return null; }
		});

		refresh();

		initMysticAdeptNode();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbTradition.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			charGen.getMagicOrResonanceController().selectTradition(n);
			descTradition.setData(n);
		});

		btnDec.setOnAction( ev -> {
			SR6PrioritySettings prio = charGen.getModel().getCharGenSettings(SR6PrioritySettings.class);
			if (prio.getMagicForPP()>0) prio.setMagicForPP( prio.getMagicForPP()-1);
			lbPower.setText(String.valueOf(prio.getMagicForPP()));
			lbMagic.setText(String.valueOf(prio.mysticAdeptMaxPoints - prio.getMagicForPP()));
			lbTotal.setText(String.valueOf(prio.mysticAdeptMaxPoints));
		});
		btnInc.setOnAction( ev -> {
			SR6PrioritySettings prio = charGen.getModel().getCharGenSettings(SR6PrioritySettings.class);
			if (prio.getMagicForPP()<prio.mysticAdeptMaxPoints) prio.setMagicForPP( prio.getMagicForPP()+1);
			lbPower.setText(String.valueOf(prio.getMagicForPP()));
			lbMagic.setText(String.valueOf(prio.mysticAdeptMaxPoints - prio.getMagicForPP()));
			lbTotal.setText(String.valueOf(prio.mysticAdeptMaxPoints));
		});

		cbAspectSkill.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "Change aspect skill to "+n);
			charGen.getModel().setAspectSkill(n);
			charGen.runProcessors();
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageMagicOrResonance#getChoiceConfigNode()
	 */
	@Override
	protected Node getChoiceConfigNode(MagicOrResonanceType type) {
		if (type==null) return null;

		switch (type.getId()) {
		case "mysticadept": return initMysticAdeptNode();
		case "magician"   : return initMagicianNode();
		case "aspectedmagician": return initAspectedMagicianNode();
		}

		return null;


	}

	//-------------------------------------------------------------------
	protected void updateChoiceConfigNode(MagicOrResonanceType type, ShadowrunCharacter<?, ?,?,?> model) {
		if (model.getTradition()!=null) {
			descTradition.setData(model.getTradition());
		}
		cbTradition.setValue(model.getTradition());

		Object obj = model.getCharGenSettings(Object.class);
		if (obj instanceof SR6PrioritySettings) {
			logger.log(Level.WARNING, "------------------>Priority");
			if (type==Shadowrun6Core.getItem(MagicOrResonanceType.class, "mysticadept")) {
				SR6PrioritySettings prio = model.getCharGenSettings(SR6PrioritySettings.class);
				lbTotal.setText(String.valueOf(prio.mysticAdeptMaxPoints));
				lbMagic.setText(String.valueOf(prio.mysticAdeptMaxPoints - prio.getMagicForPP()));
				lbPower.setText(String.valueOf(prio.getMagicForPP()));
			} else if (type==Shadowrun6Core.getItem(MagicOrResonanceType.class, "magician")) {
				SR6PrioritySettings prio = model.getCharGenSettings(SR6PrioritySettings.class);
			} else if (type==Shadowrun6Core.getItem(MagicOrResonanceType.class, "aspectedmagician")) {
				SR6PrioritySettings prio = model.getCharGenSettings(SR6PrioritySettings.class);
			}
		} else {
			logger.log(Level.WARNING, "------------------>"+obj);
		}


	}

	//-------------------------------------------------------------------
	private Node initMagicianNode() {
		/* Spell users */
		TitledComponent tcTrad = new TitledComponent(ResourceI18N.get(UI, "wizard.page.mortype.label.tradition"), cbTradition);

		return new VBox(10, tcTrad, descTradition);
	}

	//-------------------------------------------------------------------
	private Node initAspectedMagicianNode() {
		/* Spell users */
		TitledComponent tcTrad   = new TitledComponent(ResourceI18N.get(UI, "wizard.page.mortype.label.tradition"), cbTradition);
		TitledComponent tcAspect = new TitledComponent(ResourceI18N.get(UI, "wizard.page.mortype.label.aspect"), cbAspectSkill);

		return new VBox(10, tcAspect, tcTrad, descTradition);
	}

	//-------------------------------------------------------------------
	private Node initMysticAdeptNode() {
		/* Mystic adept */
		Label hdMagician = new Label(ResourceI18N.get(UI, "wizard.page.mortype.label.magician"));
		Label hdAdept    = new Label(ResourceI18N.get(UI, "wizard.page.mortype.label.adept"));
		hdMagician.getStyleClass().add("base");
		hdAdept.getStyleClass().add("base");
		if (lbTotal != null) {
			lbTotal.setAlignment(Pos.CENTER);
			lbTotal.setMaxWidth(Double.MAX_VALUE);
		}
		GridPane mysticGrid = new GridPane();
		mysticGrid.setStyle("-fx-vgap: 0.5em; -fx-hgap: 1em;");
		mysticGrid.add(hdMagician, 0, 0, 2,1);
		try {
			mysticGrid.add(   lbTotal, 2, 0);
			mysticGrid.add(   hdAdept, 3, 0, 2,1);
			mysticGrid.add(   lbMagic, 0, 1);
			mysticGrid.add(    btnDec, 1, 1);
			mysticGrid.add(    btnInc, 3, 1);
			mysticGrid.add(   lbPower, 4, 1);
			GridPane.setHalignment(lbMagic, HPos.RIGHT);
			GridPane.setFillWidth(lbTotal, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TitledComponent tcTrad = new TitledComponent(ResourceI18N.get(UI, "wizard.page.mortype.label.tradition"), cbTradition);
		tcDist = new TitledComponent(ResourceI18N.get(UI, "wizard.page.mortype.label.distribute"), mysticGrid);

		VBox ret = new VBox(10, tcTrad, tcDist, descTradition);
		if (charGen.getModel().getCharGenUsed()!=null && charGen.getModel().getCharGenUsed().equals("pointbuy")) {
			ret.getChildren().remove(tcDist);
		}

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			logger.log(Level.WARNING,"RCV {0} : {1}", type, Arrays.toString(param));
			if (param[0] instanceof PointBuyCharacterGenerator) {
				lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith("page.mortypecell.cost", charGen.getMagicOrResonanceController()));
				tcDist.setVisible(false);
				tcDist.setManaged(false);
			} else if (param[0] instanceof KarmaCharacterGenerator) {
				lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith("page.mortypecell.karma",charGen.getMagicOrResonanceController()));
				tcDist.setVisible(false);
				tcDist.setManaged(false);
			} else if (param[0] instanceof IPriorityGenerator) {
				tcDist.setVisible(true);
				tcDist.setManaged(true);
			}
			refresh();
		} else {
			super.handleControllerEvent(type, param);
		}
	}

}

class MagicOrResonanceCellWith extends ListCell<MagicOrResonanceType> {

	protected static PropertyResourceBundle SR6UI = (PropertyResourceBundle) ResourceBundle
			.getBundle(SR6WizardPageMagicOrResonance.class.getPackageName()+".SR6WizardPages");

	private HBox layout;
	private VBox vlayout;
	private Label lblHeading;
	private Label lblSecond;
	private Label lblKarma;

	private String i18nKey;
	private IMagicOrResonanceController ctrl;

	//--------------------------------------------------------------------
	public MagicOrResonanceCellWith(String i18nKey, IMagicOrResonanceController ctrl) {
		this.i18nKey = i18nKey;
		this.ctrl    = ctrl;
		lblHeading = new Label();
		lblSecond = new Label();
		lblKarma = new Label();
		lblKarma.setStyle("-fx-font-size:150%");
		lblHeading.getStyleClass().add("base");

		vlayout = new VBox(5, lblHeading, lblSecond);

		layout = new HBox(10);
		layout.getChildren().addAll(vlayout, lblKarma);
		layout.setAlignment(Pos.CENTER);

		vlayout.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(vlayout, Priority.ALWAYS);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(MagicOrResonanceType item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setGraphic(layout);
			lblHeading.setText(item.getName());
			int cost = ctrl.getCost(item);
			lblKarma.setText( ResourceI18N.format(SR6UI, i18nKey, cost) );
		}
	}
}

