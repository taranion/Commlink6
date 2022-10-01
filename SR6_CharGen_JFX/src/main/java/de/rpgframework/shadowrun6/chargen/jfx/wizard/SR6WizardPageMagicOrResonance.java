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
import de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageMagicOrResonance;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
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
	
	/* For aspected magicians */
	private ChoiceBox<SR6Skill> cbAspectSkill;
	
	/* For (aspected) magicians and mystic adepts*/
	private ChoiceBox<Tradition> cbTradition;

	//-------------------------------------------------------------------
	public SR6WizardPageMagicOrResonance(Wizard wizard, IShadowrunCharacterGenerator<?, ?, ?,?> charGen) {
		super(wizard, charGen);
		
		System.err.println("SR6WizardPageMagicOrResonance: "+charGen.getModel().getCharGenUsed());
		if (charGen.getModel().getCharGenUsed()!=null && charGen.getModel().getCharGenUsed().equals("pointbuy")) {
			lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith());
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
			if (prio.mysticAdeptPowerPoints>0) prio.mysticAdeptPowerPoints--;
			lbPower.setText(String.valueOf(prio.mysticAdeptPowerPoints));
			lbMagic.setText(String.valueOf(prio.mysticAdeptMaxPoints - prio.mysticAdeptPowerPoints));
			lbTotal.setText(String.valueOf(prio.mysticAdeptMaxPoints));
		});
		btnInc.setOnAction( ev -> {
			SR6PrioritySettings prio = charGen.getModel().getCharGenSettings(SR6PrioritySettings.class);
			if (prio.mysticAdeptPowerPoints<prio.mysticAdeptMaxPoints) prio.mysticAdeptPowerPoints++;
			lbPower.setText(String.valueOf(prio.mysticAdeptPowerPoints));
			lbMagic.setText(String.valueOf(prio.mysticAdeptMaxPoints - prio.mysticAdeptPowerPoints));
			lbTotal.setText(String.valueOf(prio.mysticAdeptMaxPoints));
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
				lbMagic.setText(String.valueOf(prio.mysticAdeptMaxPoints - prio.mysticAdeptPowerPoints));
				lbPower.setText(String.valueOf(prio.mysticAdeptPowerPoints));
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
		TitledComponent tcDist = new TitledComponent(ResourceI18N.get(UI, "wizard.page.mortype.label.distribute"), mysticGrid);
		
		return new VBox(10, tcTrad, tcDist, descTradition);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			logger.log(Level.WARNING,"RCV {} : {}", type, Arrays.toString(param));
			if (param[0] instanceof PointBuyCharacterGenerator) {
				lvMoRType.setCellFactory( lv -> new MagicOrResonanceCellWith());
			}
			refresh();
		} else {
			super.handleControllerEvent(type, param);
		}
	}

}

class MagicOrResonanceCellWith extends ListCell<MagicOrResonanceType> {

	protected static PropertyResourceBundle SR6UI = (PropertyResourceBundle) ResourceBundle
			.getBundle(SR6WizardPageMagicOrResonance.class.getName());

	private HBox layout;
	private VBox vlayout;
	private Label lblHeading;
	private Label lblSecond;
	private Label lblKarma;

	//--------------------------------------------------------------------
	public MagicOrResonanceCellWith() {
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
			int cost =0;
			if (item.usesMagic() || item.usesResonance())
				cost = 10;
			lblKarma.setText( ResourceI18N.format(SR6UI, "mortypecell.cost", cost) );
		}
	}
}

