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
import de.rpgframework.shadowrun6.CombatSectionTools.AttackTable;
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
import javafx.scene.layout.Priority;
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

//	private GridPane gridAttackHead;
	private GridPane gridIni, gridAttack, gridAttackMod;
	private GridPane gridDefense, gridDefenseMod, gridDamage;

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

		lbAttackRating = new Label("?", iViewAR);
		lbDefenseRating = new Label("?", iViewDR);
		lbAttackRating.getStyleClass().add(JavaFXConstants.STYLE_HEADING4); lbAttackRating.setStyle("-fx-text-fill: white");
		lbDefenseRating.getStyleClass().add(JavaFXConstants.STYLE_HEADING4); lbDefenseRating.setStyle("-fx-text-fill: white");
		lbAttackRating.setContentDisplay(ContentDisplay.CENTER);
		lbDefenseRating.setContentDisplay(ContentDisplay.CENTER);
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
		VBox colAttack = initAttackColumnLayout();

		// Defense column
		VBox colDefense = initDefenseColumnLayout();

		// All
		BorderPane upper = new BorderPane();
		upper.setLeft(colAttack);
		upper.setCenter(ivSilhoette);
		upper.setRight(colDefense);

		setContent(upper);
	}

	private VBox initAttackColumnLayout() {
		gridIni = new GridPane();
		gridIni.setVgap(5); gridIni.setHgap(10);

		Label hdAttacks = new Label(ResourceI18N.get(RES, "section.combat.attacks")); hdAttacks.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdAttacks.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: light");
		gridAttack = new GridPane();
		gridAttack.setVgap(5); gridAttack.setHgap(10);
		Label hdAttackMods = new Label(ResourceI18N.get(RES, "section.combat.modifications")); hdAttackMods.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdAttackMods.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: light");
		gridAttackMod = new GridPane();
		gridAttackMod.setVgap(5); gridAttackMod.setHgap(10);

//		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(120));
//		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(30));
//		gridAttackHead.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttack.getColumnConstraints().add(new ColumnConstraints(120));
		gridAttack.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttack.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttackMod.getColumnConstraints().add(new ColumnConstraints(120));
		gridAttackMod.getColumnConstraints().add(new ColumnConstraints(30));
		gridAttackMod.getColumnConstraints().add(new ColumnConstraints(30));

		hdAttacks.setMaxWidth(Double.MAX_VALUE);
		hdAttackMods.setMaxWidth(Double.MAX_VALUE);
//		VBox colAttack = new VBox(0, lbAttackRating, gridAttackHead, hdAttacks, gridAttack, hdAttackMods, gridAttackMod);
		VBox colAttack = new VBox(0, lbAttackRating, gridIni, hdAttacks, gridAttack, hdAttackMods, gridAttackMod);
		colAttack.setAlignment(Pos.TOP_LEFT);
		VBox.setVgrow(hdAttacks, Priority.ALWAYS);
		VBox.setVgrow(hdAttackMods, Priority.ALWAYS);
//		VBox.setMargin(gridAttackHead, new Insets(10, 0, 0, 0));
		VBox.setMargin(hdAttackMods, new Insets(5, 0, 0, 0));

		// Center column content of attack grid
		for (Node child : gridAttack.getChildren()) {
			int x = GridPane.getColumnIndex(child);
			int y = GridPane.getRowIndex(child);
			if (x==0) continue;
			GridPane.setConstraints(child, x, y, 1, 1, HPos.CENTER, VPos.CENTER);
		}

		return colAttack;
	}

	private VBox initDefenseColumnLayout() {
		Label hdDefenses = new Label(ResourceI18N.get(RES, "section.combat.defensepools")); hdDefenses.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdDefenses.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: light");
		Label hdDefenseMods = new Label(ResourceI18N.get(RES, "section.combat.modifications")); hdDefenseMods.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdDefenseMods.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: light");
		Label hdDamage = new Label(ResourceI18N.get(RES, "section.combat.damage")); hdDamage.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdDamage.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: light");
		gridDefense = new GridPane();
		gridDefense.setVgap(5); gridDefense.setHgap(5);
		gridDefense.getColumnConstraints().add(new ColumnConstraints(150));
		gridDefense.getColumnConstraints().add(new ColumnConstraints(30));
		gridDefenseMod = new GridPane();
		gridDefenseMod.setVgap(5); gridDefense.setHgap(5);
		gridDefenseMod.getColumnConstraints().add(new ColumnConstraints(150));
		gridDefenseMod.getColumnConstraints().add(new ColumnConstraints(30));
		gridDamage = new GridPane();
		gridDamage.setVgap(5); gridDefense.setHgap(5);
		gridDamage.getColumnConstraints().add(new ColumnConstraints(150));
		gridDamage.getColumnConstraints().add(new ColumnConstraints(30));
		hdDefenses.setMaxWidth(Double.MAX_VALUE);
		hdDefenseMods.setMaxWidth(Double.MAX_VALUE);
		hdDamage.setMaxWidth(Double.MAX_VALUE);
		VBox colDefense = new VBox(10, lbDefenseRating, hdDefenses, gridDefense, hdDefenseMods,gridDefenseMod, hdDamage, gridDamage);
		colDefense.setAlignment(Pos.TOP_RIGHT);
		VBox.setVgrow(hdDefenses, Priority.ALWAYS);
		VBox.setVgrow(hdDefenseMods, Priority.ALWAYS);
		VBox.setVgrow(hdDamage, Priority.ALWAYS);

		return colDefense;
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
	private void fillAttackTable(GridPane table, AttackTable attTable, boolean withAttackRating) {
		int count=0;
		if (attTable.col1Name!=null) {
			Label hd1 = new Label(attTable.col1Name);
			Label hd2 = new Label(attTable.col2Name);
			Label hd3 = new Label(attTable.col3Name);
			hd1.setStyle("-fx-font-size: small");
			hd2.setStyle("-fx-font-size: small");
			hd3.setStyle("-fx-font-size: small");
			table.add(hd1 , 1, 0);
			table.add(hd2 , 2, 0);
			table.add(hd3 , 3, 0);
			count++;
		}
		for (AttackEntry weapon : attTable) {
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
				Label label = new Label(weapon.getCol2());
				if (weapon.getCol2Tooltip()!=null && !weapon.getCol2Tooltip().isBlank())
					label.setTooltip(new Tooltip(weapon.getCol2Tooltip()));
				table.add(label , 2, count);
				table.add(new Label( weapon.getCol3()) , 3, count);
			} else {
				table.add(new Label( weapon.getCol3()) , 2, count);
			}
			count++;
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
		fillAttackTable(gridIni, CombatSectionTools.getInitiativeTable(model, Locale.getDefault(), WorldType.PHYSICAL), true);
		fillAttackTable(gridAttack, CombatSectionTools.getAttackTable(model, Locale.getDefault(), WorldType.PHYSICAL), true);
		fillAttackTable(gridAttackMod, CombatSectionTools.getAttackModifiers(model, Locale.getDefault(), WorldType.PHYSICAL), true);
		fillAttackTable(gridDefense, CombatSectionTools.getDefenseTable(model, Locale.getDefault(), WorldType.PHYSICAL), true);
		fillAttackTable(gridDefenseMod, CombatSectionTools.getDefenseModifiers(model, Locale.getDefault(), WorldType.PHYSICAL), true);
		fillAttackTable(gridDamage, CombatSectionTools.getDamageTable(model, Locale.getDefault(), WorldType.PHYSICAL), true);
	}

	//-------------------------------------------------------------------
	private void showAstral(Shadowrun6Character model) {
		setLabelValue(lbAttackRating, model, ShadowrunAttribute.ATTACK_RATING_ASTRAL);
		setLabelValue(lbDefenseRating, model, ShadowrunAttribute.DEFENSE_RATING_ASTRAL);

		fillAttackTable(gridIni, CombatSectionTools.getInitiativeTable(model, Locale.getDefault(), WorldType.ASTRAL), true);
		fillAttackTable(gridAttack   , CombatSectionTools.getAttackTable(model, Locale.getDefault(), WorldType.ASTRAL), true);
		fillAttackTable(gridAttackMod, CombatSectionTools.getAttackModifiers(model, Locale.getDefault(), WorldType.ASTRAL), true);
		fillAttackTable(gridDefense, CombatSectionTools.getDefenseTable(model, Locale.getDefault(), WorldType.ASTRAL), true);
		fillAttackTable(gridDefenseMod, CombatSectionTools.getDefenseModifiers(model, Locale.getDefault(), WorldType.ASTRAL), true);
		fillAttackTable(gridDamage, CombatSectionTools.getDamageTable(model, Locale.getDefault(), WorldType.ASTRAL), true);
	}

	//-------------------------------------------------------------------
	private void showMatrix(Shadowrun6Character model) {
		setLabelValue(lbAttackRating, model, ShadowrunAttribute.ATTACK_RATING_MATRIX);
		setLabelValue(lbDefenseRating, model, ShadowrunAttribute.DEFENSE_RATING_MATRIX);

		fillAttackTable(gridIni, CombatSectionTools.getInitiativeTable(model, Locale.getDefault(), WorldType.MATRIX), true);
		fillAttackTable(gridAttack, CombatSectionTools.getAttackTable(model, Locale.getDefault(), WorldType.MATRIX), false);
		fillAttackTable(gridAttackMod, CombatSectionTools.getAttackModifiers(model, Locale.getDefault(), WorldType.MATRIX), false);
		fillAttackTable(gridDefense, CombatSectionTools.getDefenseTable(model, Locale.getDefault(), WorldType.MATRIX), true);
		fillAttackTable(gridDefenseMod, CombatSectionTools.getDefenseModifiers(model, Locale.getDefault(), WorldType.MATRIX), true);
		fillAttackTable(gridDamage, CombatSectionTools.getDamageTable(model, Locale.getDefault(), WorldType.MATRIX), true);
	}

	//-------------------------------------------------------------------
	private void showMatrixUV(Shadowrun6Character model) {
		// TODO Auto-generated method stub

	}

}
