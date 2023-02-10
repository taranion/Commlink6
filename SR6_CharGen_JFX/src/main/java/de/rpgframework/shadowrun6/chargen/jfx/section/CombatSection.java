package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.AttackEntry;
import de.rpgframework.shadowrun6.CombatSectionTools;
import de.rpgframework.shadowrun6.Shadowrun6Action;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.WorldType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class CombatSection extends Section {

	private final static Logger logger = System.getLogger(CombatSection.class.getPackageName());

	private final static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CombatSection.class.getPackageName()+".Section");

	private WorldType type;
	private Label lbAttackRating, lbDefenseRating;
	private Label lbDefensePool, lbFullDefense;
	private Label lbResistDamage;
	private Label lbDevMon, lbStnMon, lbPhyMon;
	private Label lbIni, lbIniVR;

	private GridPane gridAttackHead;
	private GridPane gridAttack, gridDefense;
	private GridPane gridAttackMod, gridDefenseMod;

	//-------------------------------------------------------------------
	public CombatSection(WorldType type) {
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
		lbFullDefense = new Label("?");
		lbFullDefense.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
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
		String file = "Silhouette.png";
		if (type==WorldType.ASTRAL)
			file = "Silhouette_Magie.png";
		else if (type==WorldType.MATRIX)
			file = "Silhouette_Matrix.png";
		ImageView ivSilhoette = new ImageView(new Image(getClass().getResourceAsStream(file)));
		ivSilhoette.setFitHeight(300);
		ivSilhoette.setPreserveRatio(true);

		// Attack column
		gridAttackHead = new GridPane();
		gridAttackHead.setVgap(5); gridAttackHead.setHgap(10);
		Label hdPool = new Label(ResourceI18N.get(RES, "section.combat.pool"));
		Label hdAR   = new Label(ResourceI18N.get(RES, "section.combat.ar"));
		Label hdDV   = new Label(SR6ItemAttribute.DAMAGE.getShortName());
		gridAttackHead.add(hdPool , 1, 0);
		gridAttackHead.add(hdAR , 2, 0);
		gridAttackHead.add(hdDV , 3, 0);

		Label hdAttacks = new Label(ResourceI18N.get(RES, "section.combat.attacks")); hdAttacks.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		gridAttack = new GridPane();
		gridAttack.setVgap(5); gridAttack.setHgap(10);
		Label hdAttackMods = new Label(ResourceI18N.get(RES, "section.combat.attackmods")); hdAttackMods.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		gridAttackMod = new GridPane();
		gridAttackMod.setVgap(5); gridAttackMod.setHgap(10);

		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(120));
		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttack.getColumnConstraints().add(new ColumnConstraints(120));
		gridAttack.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttack.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttackMod.getColumnConstraints().add(new ColumnConstraints(120));
		gridAttackMod.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttackMod.getColumnConstraints().add(new ColumnConstraints(30));

		VBox colAttack = new VBox(0, lbAttackRating, gridAttackHead, hdAttacks, gridAttack, hdAttackMods, gridAttackMod);
		colAttack.setAlignment(Pos.TOP_LEFT);
		VBox.setMargin(gridAttackHead, new Insets(10, 0, 0, 0));
		VBox.setMargin(hdAttackMods, new Insets(5, 0, 0, 0));

		// Defense column
		gridDefense = new GridPane();
		gridDefense.setVgap(5); gridDefense.setHgap(5);
		VBox colDefense = new VBox(10, lbDefenseRating, gridDefense);
		colDefense.setAlignment(Pos.TOP_RIGHT);

		switch (type) {
//		case PHYSICAL:
//			initPhysical();
//			break;
		case ASTRAL:
			initAstral(colAttack, colDefense);
			break;
		case MATRIX:
			initMatrix(colAttack, colDefense);
			break;
		}
		// Center column content of attack grid
		for (Node child : gridAttack.getChildren()) {
			int x = GridPane.getColumnIndex(child);
			int y = GridPane.getRowIndex(child);
			if (x==0) continue;
			GridPane.setConstraints(child, x, y, 1, 1, HPos.CENTER, VPos.CENTER);
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

		// Re-Do heading
		gridAttackHead.getChildren().clear();
		Label hdPool = new Label(ResourceI18N.get(RES, "section.combat.pool"));
		Label hdDV   = new Label(SR6ItemAttribute.DAMAGE.getShortName());
		gridAttackHead.add(hdPool , 1, 0);
		gridAttackHead.add(hdDV , 2, 0);
		gridAttackHead.getColumnConstraints().clear();
		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(120));
		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(30));

		// Defense
		Label hdDefensePool = new Label(ShadowrunAttribute.DEFENSE_POOL_MATRIX.getName());
		Label hdFullDefense = new Label("- "+ResourceI18N.get(RES, "section.combat.fulldefense"));
		Label hdResistDamage= new Label(ShadowrunAttribute.RESIST_DAMAGE.getName());
		Label hdDevMon = new Label(ResourceI18N.get(RES, "section.combat.matrix.device_monitor")); hdDevMon.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		gridDefense.add(hdDefensePool, 0, 0);
		gridDefense.add(lbDefensePool, 1, 0);
		gridDefense.add(hdFullDefense, 0, 1);
		gridDefense.add(lbFullDefense, 1, 1);
		gridDefense.add(hdResistDamage, 0, 2);
		gridDefense.add(lbResistDamage, 1, 2);
		gridDefense.add(hdDevMon      , 0, 3);
		gridDefense.add(lbDevMon      , 1, 3);
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
	}

	//-------------------------------------------------------------------
	public void setData(Shadowrun6Character model) {
		gridAttack.getChildren().clear();
		gridAttackMod.getChildren().clear();
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
		lbStnMon.setText(String.valueOf(model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getModifiedValue()));
		lbPhyMon.setText(String.valueOf(model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getModifiedValue()));
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
	private void fillAttackTable(GridPane table, List<AttackEntry> list, boolean withAttackRating) {
		int count=0;
		for (AttackEntry weapon : list) {
			count++;
			if (count==6) break;
			Label hdName = new Label(weapon.getName());
			//hdName.setStyle("-fx-max-width: 8em");
			table.add(hdName  , 0, count);
			if (weapon.getCol1()!=null) {
				Label label = new Label(weapon.getCol1());
				if (weapon.getCol1Tooltip()!=null && !weapon.getCol1Tooltip().isBlank())
					label.setTooltip(new Tooltip(weapon.getCol1Tooltip()));
				table.add(label , 1, count);
			}
			if (withAttackRating) {
				table.add(new Label( weapon.getCol2()) , 2, count);
				table.add(new Label( weapon.getCol3()) , 3, count);
			} else {
				table.add(new Label( weapon.getCol3()) , 2, count);
			}
		}
	}

	//-------------------------------------------------------------------
	private void showPhysical(Shadowrun6Character model) {
		if (model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getPool()!=null) {
			lbAttackRating.setText(model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getPool().toString() );
			lbAttackRating.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.ATTACK_RATING_PHYSICAL).getPool().toExplainString()) );
		} else {
			logger.log(Level.ERROR, "No ATTACK_RATING_PHYSICAL pool calculated");
		}
		if (model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getPool()!=null) {
			lbDefenseRating.setText(model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getPool().toString() );
			lbDefenseRating.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_PHYSICAL).getPool().toExplainString()) );
		} else {
			logger.log(Level.ERROR, "No DEFENSE_RATING_PHYSICAL pool calculated");
		}

		// Show up to 5 weapons
		fillAttackTable(gridAttack, CombatSectionTools.getAttackTable(model, Locale.getDefault(), WorldType.PHYSICAL), true);
		fillAttackTable(gridAttackMod, CombatSectionTools.getAttackModifiers(model, Locale.getDefault(), WorldType.PHYSICAL), true);
	}

	//-------------------------------------------------------------------
	private void showAstral(Shadowrun6Character model) {
		setLabelValue(lbAttackRating, model, ShadowrunAttribute.ATTACK_RATING_ASTRAL);
		setLabelValue(lbDefenseRating, model, ShadowrunAttribute.DEFENSE_RATING_ASTRAL);

		// Initiative
		setLabelValue(lbIni, model, ShadowrunAttribute.INITIATIVE_ASTRAL, ShadowrunAttribute.INITIATIVE_DICE_ASTRAL);

		fillAttackTable(gridAttack, CombatSectionTools.getAttackTable(model, Locale.getDefault(), WorldType.ASTRAL), true);
		fillAttackTable(gridAttackMod, CombatSectionTools.getAttackModifiers(model, Locale.getDefault(), WorldType.ASTRAL), true);
	}

	//-------------------------------------------------------------------
	private void showMatrix(Shadowrun6Character model) {
		setLabelValue(lbAttackRating, model, ShadowrunAttribute.ATTACK_RATING_MATRIX);
		setLabelValue(lbDefenseRating, model, ShadowrunAttribute.DEFENSE_RATING_MATRIX);

		// Initiative
		setLabelValue(lbIni, model, ShadowrunAttribute.INITIATIVE_MATRIX, ShadowrunAttribute.INITIATIVE_DICE_MATRIX);
		setLabelValue(lbIniVR, model, ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD, ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD);

		fillAttackTable(gridAttack, CombatSectionTools.getAttackTable(model, Locale.getDefault(), WorldType.MATRIX), false);
		fillAttackTable(gridAttackMod, CombatSectionTools.getAttackModifiers(model, Locale.getDefault(), WorldType.MATRIX), false);

//		lbDmgAttack1.setText( String.valueOf((int)Math.round( ((double)model.getPersona().getAttack().getModifiedValue())/2.0d)) );
//		lbDmgAttack2.setText("1*");
//		lbPoolAttack1.setText( ""+
//		Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("cracking"), "cybercombat"));
//		lbPoolAttack2.setText( ""+
//		Shadowrun6Tools.getSkillPool(model, Shadowrun6Core.getSkill("cracking"), "cybercombat"));

		// Defense
		lbDefensePool.setText(model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_MATRIX).getPool().toString() );
		lbDefensePool.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_MATRIX).getPool().toExplainString()) );
		lbFullDefense.setText(model.getAttribute(ShadowrunAttribute.FULL_DEFENSE_POOL_MATRIX).getPool().toString() );
		lbFullDefense.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.FULL_DEFENSE_POOL_MATRIX).getPool().toExplainString()) );

		// Resist Matrix Damage
		lbResistDamage.setText(model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE_MATRIX).getPool().toString() );
		lbResistDamage.setTooltip(new Tooltip(model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE_MATRIX).getPool().toExplainString()) );

		// Device Monitor
		lbDevMon.setText(String.valueOf(model.getPersona().getMonitor().length));
	}

	//-------------------------------------------------------------------
	private void showMatrixUV(Shadowrun6Character model) {
		// TODO Auto-generated method stub

	}

}
