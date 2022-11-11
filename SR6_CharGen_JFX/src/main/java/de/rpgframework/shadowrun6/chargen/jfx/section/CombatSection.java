package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class CombatSection extends Section {

	private final static Logger logger = System.getLogger(CombatSection.class.getPackageName());

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CombatSection.class.getPackageName()+".Section");
	
	public enum Type {
		PHYSICAL,
		ASTRAL,
		MATRIX,
		MATRIX_UV
	}
	
	private Type type;
	private Label lbAttackRating, lbDefenseRating;
	private Label lbDefensePool;
	private Label lbResistDamage;
	private Label lbDevMon, lbStnMon, lbPhyMon;
	private Label lbIni, lbIniVR;
	
	private GridPane gridAttack, gridDefense;
	
	private Label lbDmgAttack1, lbDmgAttack2;
	private Label lbPoolAttack1, lbPoolAttack2;
//	private Label hdDevMon, lbStnMon, lbPhyMon;
//	private HBox bxDevMon, bxStnMon, bxPhyMon;

	//-------------------------------------------------------------------
	public CombatSection(Type type) {
		super(ResourceI18N.get(RES, "section.combat."+type.name().toLowerCase()), null);
		this.type = type;
		
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		ImageView iViewAR = new ImageView(new Image(getClass().getResourceAsStream("icon_ar.png")));
		ImageView iViewDR = new ImageView(new Image(getClass().getResourceAsStream("icon_dr.png")));
		iViewAR.setFitWidth(50);
		iViewAR.setPreserveRatio(true);
		iViewDR.setFitWidth(50);
		iViewDR.setPreserveRatio(true);
		
		lbIni = new Label("?");
		lbIniVR = new Label("?");
		lbAttackRating = new Label("?", iViewAR);
		lbDefenseRating = new Label("?", iViewDR);
		lbAttackRating.getStyleClass().add(JavaFXConstants.STYLE_HEADING4); lbAttackRating.setStyle("-fx-text-fill: white");
		lbDefenseRating.getStyleClass().add(JavaFXConstants.STYLE_HEADING4); lbDefenseRating.setStyle("-fx-text-fill: white");
		lbAttackRating.setContentDisplay(ContentDisplay.CENTER);
		lbDefenseRating.setContentDisplay(ContentDisplay.CENTER);
		
		lbDefensePool = new Label("?");
		lbDefensePool.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbResistDamage = new Label("?");
		lbResistDamage.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbDevMon = new Label("?");
		lbDevMon.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbPhyMon = new Label("?");
		lbPhyMon.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbStnMon = new Label("?");
		lbStnMon.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		ImageView ivSilhoette = new ImageView(new Image(getClass().getResourceAsStream("Silhouette_Matrix.png")));
		ivSilhoette.setFitHeight(300);
		ivSilhoette.setPreserveRatio(true);

		// Attack column
		Label hdAttacks = new Label(ResourceI18N.get(RES, "section.combat.attacks")); hdAttacks.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		gridAttack = new GridPane();
		gridAttack.setVgap(5); gridAttack.setHgap(5);
		VBox colAttack = new VBox(10, lbAttackRating, hdAttacks, gridAttack);
		colAttack.setAlignment(Pos.TOP_LEFT);
		
		// Defense column
		gridDefense = new GridPane();
		gridDefense.setVgap(5); gridDefense.setHgap(5);
		VBox colDefense = new VBox(10, lbDefenseRating, gridDefense);
		colDefense.setAlignment(Pos.TOP_RIGHT);

		switch (type) {
//		case PHYSICAL:
//			showPhysical(model);
//			break;
		case ASTRAL:
			initAstral(colAttack, colDefense);
			break;
		case MATRIX:
			initMatrix(colAttack, colDefense);
			break;
		}
		
		Label hdStnMon = new Label(ShadowrunAttribute.STUN_MONITOR.getName());
		Label hdPhyMon = new Label(ShadowrunAttribute.PHYSICAL_MONITOR.getName()); 
		gridDefense.add(hdStnMon      , 0, 5);
		gridDefense.add(lbStnMon      , 1, 5);
		gridDefense.add(hdPhyMon      , 0, 6);
		gridDefense.add(lbPhyMon      , 1, 6);
		
		
		// All
		BorderPane upper = new BorderPane();
		upper.setLeft(colAttack);
		upper.setCenter(ivSilhoette);
		upper.setRight(colDefense);
		
		setContent(upper);
	}

	//-------------------------------------------------------------------
	private void initMatrix(VBox colAttack, VBox colDefense) {
		// Initiative table
		Label hdInitiative = new Label(ResourceI18N.get(RES, "section.combat.initiative"));
		Label hdAR = new Label(ResourceI18N.get(RES, "section.combat.matrix.ar"));
		Label hdVR = new Label(ResourceI18N.get(RES, "section.combat.matrix.vr"));
		GridPane gridIni = new GridPane();
		gridIni.setHgap(5);
		gridIni.add(hdAR, 1,0);
		gridIni.add(hdVR, 2,0);
		gridIni.add(hdInitiative, 0,1);
		gridIni.add(lbIni  , 1,1);
		gridIni.add(lbIniVR, 2,1);
		colAttack.getChildren().add(1, gridIni);
		
		
		Label hdDataSpike = new Label(Shadowrun6Core.getItem(Shadowrun6Action.class, "data_spike").getName());
		Label hdTarPit    = new Label(Shadowrun6Core.getItem(Shadowrun6Action.class, "tarpit").getName());
		lbDmgAttack1 = new Label("?"); lbDmgAttack1.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbDmgAttack2    = new Label("?"); lbDmgAttack2   .getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbPoolAttack1 = new Label("?"); lbPoolAttack1.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbPoolAttack2    = new Label("?"); lbPoolAttack2   .getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		
		gridAttack.add(hdDataSpike    , 0, 0);
		gridAttack.add(lbDmgAttack1 , 1, 0);
		gridAttack.add(lbPoolAttack1, 2, 0);
		gridAttack.add(hdTarPit       , 0, 1);
		gridAttack.add(lbDmgAttack2    , 1, 1);
		gridAttack.add(lbPoolAttack2   , 2, 1);
		
		// Defense
		Label hdDefensePool = new Label(ShadowrunAttribute.DEFENSE_POOL_MATRIX.getName());
		Label hdResistDamage= new Label(ShadowrunAttribute.RESIST_DAMAGE.getName());
		Label hdDevMon = new Label(ResourceI18N.get(RES, "section.combat.matrix.device_monitor")); hdDevMon.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		gridDefense.add(hdDefensePool, 0, 0);
		gridDefense.add(lbDefensePool, 1, 0);
		gridDefense.add(hdResistDamage, 0, 1);
		gridDefense.add(lbResistDamage, 1, 1);
		gridDefense.add(hdDevMon      , 0, 2);
		gridDefense.add(lbDevMon      , 1, 2);
	}

	//-------------------------------------------------------------------
	private void initAstral(VBox colAttack, VBox colDefense) {
		// Initiative table
		Label hdInitiative = new Label(ResourceI18N.get(RES, "section.combat.initiative"));
		GridPane gridIni = new GridPane();
		gridIni.setHgap(5);
		gridIni.add(hdInitiative, 0,0);
		gridIni.add(lbIni  , 1,0);
		colAttack.getChildren().add(1, gridIni);
		
		Label hdUnarmed = new Label(ResourceI18N.get(RES, "label.unarmed"));
		Label hdCasting = new Label(ResourceI18N.get(RES, "label.spellcasting"));
		lbDmgAttack1 = new Label("?"); lbDmgAttack1.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		lbDmgAttack2    = new Label("?"); lbDmgAttack2   .getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbPoolAttack1 = new Label("?"); lbPoolAttack1.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbPoolAttack2    = new Label("?"); lbPoolAttack2   .getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		
		gridAttack.add(hdUnarmed    , 0, 0);
		gridAttack.add(lbDmgAttack1 , 1, 0);
		gridAttack.add(lbPoolAttack1, 2, 0);
		gridAttack.add(hdCasting       , 0, 1);
		gridAttack.add(lbDmgAttack2    , 1, 1);
		gridAttack.add(lbPoolAttack2   , 2, 1);
	}
	
	//-------------------------------------------------------------------
	public void setData(Shadowrun6Character model) {
		switch (type) {
		case PHYSICAL:
			showPhysical(model);
			break;
		case ASTRAL:
			showAstral(model);
			break;
		case MATRIX:
			showMatrix(model);
			break;
		case MATRIX_UV:
			showMatrixUV(model);
			break;
		}
	}

	//-------------------------------------------------------------------
	private static void setLabelValue(Label label, Shadowrun6Character model, ShadowrunAttribute key) {
		label.setText(model.getAttribute(key).getPool().toString() );
		label.setTooltip(new Tooltip(model.getAttribute(key).getPool().toExplainString()) );
	}

	//-------------------------------------------------------------------
	private static void setLabelValue(Label label, Shadowrun6Character model, ShadowrunAttribute key, ShadowrunAttribute key2) {
		label.setText(model.getAttribute(key).getPool().toString()+"+"+model.getAttribute(key2).getPool().toString()+"D6" );
		label.setTooltip(new Tooltip(model.getAttribute(key).getPool().toExplainString()+"\n+\n"+model.getAttribute(key2).getPool().toExplainString()) );
	}

	//-------------------------------------------------------------------
	private void showPhysical(Shadowrun6Character model) {
		lbAttackRating.setText(model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getPool().toString() );
		lbAttackRating.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getPool().toExplainString()) );
		lbDefenseRating.setText(model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getPool().toString() );
		lbDefenseRating.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getPool().toExplainString()) );
	}

	//-------------------------------------------------------------------
	private void showAstral(Shadowrun6Character model) {
		setLabelValue(lbAttackRating, model, ShadowrunAttribute.ATTACK_RATING_ASTRAL);
		setLabelValue(lbDefenseRating, model, ShadowrunAttribute.DEFENSE_RATING_ASTRAL);
		
		// Initiative
		setLabelValue(lbIni, model, ShadowrunAttribute.INITIATIVE_ASTRAL, ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);
		
		lbDmgAttack1.setText("-");
		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesMagic()) {
			if (model.getMagicOrResonanceType().usesSpells() && model.getTradition()!=null) {
				lbDmgAttack1.setText( String.valueOf((int)Math.round( ((double)model.getAttribute(model.getTradition().getTraditionAttribute()).getModifiedValue())/2.0d)) );
				lbDmgAttack1.setTooltip(new Tooltip(model.getTradition().getTraditionAttribute().getName()+"/2") );
			} else if (model.getMagicOrResonanceType().usesPowers()) {
				lbDmgAttack1.setText( String.valueOf((int)Math.round( ((double)model.getAttribute(ShadowrunAttribute.BODY).getModifiedValue())/2.0d)) );
				lbDmgAttack1.setTooltip(new Tooltip(ShadowrunAttribute.BODY.getName()+"/2") );
			}
		}
		lbDmgAttack2.setText( String.valueOf((int)Math.round( ((double)model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue())/2.0d)) );
		lbDmgAttack2.setTooltip(new Tooltip(ShadowrunAttribute.MAGIC.getName()+"/2") );
		lbPoolAttack1.setText( ""+Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("astral"), ShadowrunAttribute.WILLPOWER, "astral_combat"));
		lbPoolAttack1.setTooltip(new Tooltip(ShadowrunAttribute.MAGIC.getName()+"/2") );
		lbPoolAttack2.setText( ""+Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("sorcery"), "spellcasting"));
	}

	//-------------------------------------------------------------------
	private void showMatrix(Shadowrun6Character model) {
		setLabelValue(lbAttackRating, model, ShadowrunAttribute.ATTACK_RATING_MATRIX);
		setLabelValue(lbDefenseRating, model, ShadowrunAttribute.DEFENSE_RATING_MATRIX);
		
		// Initiative
		setLabelValue(lbIni, model, ShadowrunAttribute.INITIATIVE_MATRIX, ShadowrunAttribute.INITIATIVE_DICE_MATRIX);
		setLabelValue(lbIniVR, model, ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD, ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD);
		
		
		lbDmgAttack1.setText( String.valueOf((int)Math.round( ((double)model.getPersona().getAttack().getModifiedValue())/2.0d)) );
		lbDmgAttack2.setText("1*");
		lbPoolAttack1.setText( ""+
		Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("cracking"), "cybercombat"));
		
		// Defense
		lbDefensePool.setText(model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_MATRIX).getPool().toString() );
		lbDefensePool.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_MATRIX).getPool().toExplainString()) );
		
		// Resist Matrix Damage
		lbResistDamage.setText(model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE_MATRIX).getPool().toString() );
		lbResistDamage.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE_MATRIX).getPool().toExplainString()) );
		
		// Device Monitor
		lbDevMon.setText(String.valueOf(model.getPersona().getMonitor().length));
		lbStnMon.setText(String.valueOf(model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getModifiedValue()));
		lbPhyMon.setText(String.valueOf(model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getModifiedValue()));
		
		
	}

	//-------------------------------------------------------------------
	private void showMatrixUV(Shadowrun6Character model) {
		// TODO Auto-generated method stub
		
	}

}
