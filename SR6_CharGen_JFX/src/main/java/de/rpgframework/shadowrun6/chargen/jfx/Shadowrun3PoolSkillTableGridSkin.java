package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.controlsfx.control.ToggleSwitch;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.WindowMode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.RecommendationState;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.APrioritySettings;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeGenerator;
import de.rpgframework.shadowrun.chargen.jfx.SkinProperties;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySkillGenerator;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class Shadowrun3PoolSkillTableGridSkin extends SkinBase<Shadowrun2PoolSkillTable> {

	private final static Logger logger = System.getLogger(Shadowrun3PoolSkillTableGridSkin.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle
			.getBundle(Shadowrun2PoolSkillTable.class.getName());

	private ToggleSwitch tsExpertMode;
	private Label lbAdjust, lbPoints, lbKarma;
	private GridPane grid;
	private VBox layout;
	
	private Label headDecOut, headAdjust, headAttrib, headKarma, headIncOut, headResult;
	private ToggleButton headBtnAdjust, headBtnAttrib, headBtnKarma;
	private ToggleGroup toggles = new ToggleGroup();
	
	private Map<SR6SkillValue, Label>  lblRec = new HashMap<>();
	private Map<SR6SkillValue, Label>  lblNam = new HashMap<>();

	private Map<SR6SkillValue, Button> btnDecAllMin = new HashMap<>();
	private Map<SR6SkillValue, Button> btnDecAdj = new HashMap<>();
	private Map<SR6SkillValue, Label>  lblAdj = new HashMap<>();
	private Map<SR6SkillValue, Button> btnIncAdj = new HashMap<>();
	private Map<SR6SkillValue, Button> btnDecPnt = new HashMap<>();
	private Map<SR6SkillValue, Label>  lblPnt = new HashMap<>();
	private Map<SR6SkillValue, Button> btnIncPnt = new HashMap<>();
	private Map<SR6SkillValue, Button> btnDecKar = new HashMap<>();
	private Map<SR6SkillValue, Label>  lblKar = new HashMap<>();
	private Map<SR6SkillValue, Button> btnIncKar = new HashMap<>();

	private Map<SR6SkillValue, Button> btnDecAll = new HashMap<>();
	private Map<SR6SkillValue, Label>  lblAll = new HashMap<>();
	private Map<SR6SkillValue, Button> btnIncAll = new HashMap<>();
	private Map<SR6SkillValue, Button> btnIncAllMin = new HashMap<>();
	
	private Map<SR6SkillValue, List<Control>> allPerAttr = new HashMap<>();

	private MapChangeListener<Object, Object> propertiesMapListener = c -> {
        if (! c.wasAdded()) return;
        if (SkinProperties.WINDOW_MODE.equals(c.getKey())) {
            updateLayout();
            getSkinnable().requestLayout();
            getSkinnable().getProperties().remove(SkinProperties.WINDOW_MODE);
        }
        if (SkinProperties.REFRESH.equals(c.getKey())) {
            refresh();
        }
    };

	//-------------------------------------------------------------------
	public Shadowrun3PoolSkillTableGridSkin(Shadowrun2PoolSkillTable control) {
		super(control);
		initComponents();
		initLayout();
		initInteractivity();
		updateLayout();
		
		toggles.selectToggle(headBtnAttrib);
	}

	//-------------------------------------------------------------------
	private SR6PrioritySkillGenerator getController() {
		if (getSkinnable().getController()==null) return null;
		return (SR6PrioritySkillGenerator) getSkinnable().getController().getSkillController();
	}
	
	//-------------------------------------------------------------------
	private Button createButton(Map<SR6SkillValue,Button> map, 
			SR6SkillValue key, String icon, int x, int y) {
		Button btn = new Button(null, new SymbolIcon(icon));
		map.put(key, btn);
		GridPane.setMargin(btn, new Insets(5,10,5,10));
		
		grid.add(btn, x, y);
		
		// In allPer
		List<Control> list = allPerAttr.get(key);
		if (list==null) {
			list = new ArrayList<>();
			allPerAttr.put(key, list);
		}
		list.add(btn);
		return btn;
	}
	
	//-------------------------------------------------------------------
	private Label createLabel(Map<SR6SkillValue,Label> map, 
			SR6SkillValue key, int x, int y) {
		Label lab = new Label("?");
		lab.setMaxWidth(Double.MAX_VALUE);
		lab.setMaxHeight(Double.MAX_VALUE);
		lab.setAlignment(Pos.CENTER);
		map.put(key, lab);
//		GridPane.setMargin(lab, new Insets(0,5,0,5));
		grid.add(lab, x, y);
		
		// In allPer
		List<Control> list = allPerAttr.get(key);
		if (list==null) {
			list = new ArrayList<>();
			allPerAttr.put(key, list);
		}
		list.add(lab);
		
		return lab;
	}
	
	//-------------------------------------------------------------------
	private void createName(Map<SR6SkillValue,Label> map, 
			SR6SkillValue key, int x, int y) {
		Label lab = new Label(key.getName());
		lab.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);			
		map.put(key, lab);
		
		grid.add(lab, x, y);
		
		// In allPer
		List<Control> list = allPerAttr.get(key);
		if (list==null) {
			list = new ArrayList<>();
			allPerAttr.put(key, list);
		}
		list.add(lab);
	}
	
	//-------------------------------------------------------------------
	private void createRecomLabel(Map<SR6SkillValue,Label> map, 
			SR6SkillValue key, int x, int y) {
		Label lab = new Label(null, new SymbolIcon("favorite"));
		map.put(key, lab);
		
		
		// In allPer
		List<Control> list = allPerAttr.get(key);
		if (list==null) {
			list = new ArrayList<>();
			allPerAttr.put(key, list);
		}
		list.add(lab);
		grid.add(lab, x, y);
	}
	
	//-------------------------------------------------------------------
	private Label createHeading(String key, int x, int y, int span) {
		Label lab = new Label( (key!=null)?ResourceI18N.get(RES, key):"");
		lab.getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD);
		lab.setStyle("-fx-padding: 2px");
		lab.setMaxWidth(Double.MAX_VALUE);
		grid.add(lab, x, y, span,1);
		return lab;
	}
	
	//-------------------------------------------------------------------
	private ToggleButton createToggle(String key) {
		ToggleButton lab = new ToggleButton( (key!=null)?ResourceI18N.get(RES, key):"");
		lab.getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD+"-toggle");
		lab.setMaxWidth(Double.MAX_VALUE);
		lab.setToggleGroup(toggles);
		return lab;
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		tsExpertMode = new ToggleSwitch("Expert");
		lbAdjust = new Label("?");
		lbPoints = new Label("?");
		lbKarma  = new Label("?");
		lbAdjust.setStyle("-fx-text-fill: -fx-text-base-color");
		lbPoints.setStyle("-fx-text-fill: -fx-text-base-color");
		lbKarma .setStyle("-fx-text-fill: -fx-text-base-color");
		
		grid = new GridPane();
//		grid.setVgap(5);
		grid.setGridLinesVisible(false);
		
		int y=0;
		createHeading("head.attribute", 1, y, 1);
		headDecOut=createHeading(null, 2, y, 1);
		headAdjust=createHeading("head.adjust", 3, y, 3);
		headAdjust.setPadding(new Insets(0, 5, 0, 5));
		headAttrib=createHeading("head.attrib", 6, y, 3);
		headAttrib.setPadding(new Insets(0, 5, 0, 5));
		headKarma =createHeading("head.karma", 9, y, 3);
		headKarma.setPadding(new Insets(0, 5, 0, 5));
		headIncOut=createHeading(null, 12, y, 1);
		headResult=createHeading("head.result", 13, y, 3);
		
		headBtnAdjust = createToggle("head.adjust.short");
		headBtnAttrib = createToggle("head.attrib.short");
		headBtnKarma  = createToggle("head.karma.short");
		y++;
		
		
		for (SR6SkillValue key : getSkinnable().getController().getModel().getSkillValues()) {
			if (key.getModifyable().getType()==SkillType.KNOWLEDGE)
				continue;
			int x=0;
			createRecomLabel(lblRec, key, x++, y);
			createName(lblNam, key, x++, y);

			// Outer DEC button
			createButton(btnDecAllMin, key, "remove", x++, y);
			
			// Adjustment points
			createButton(btnDecAdj, key, "remove", x++, y);
			createLabel (lblAdj, key, x++, y);
			createButton(btnIncAdj, key, "add", x++, y);
			
			// Attribute points
				createButton(btnDecPnt, key, "remove", x++, y);
				createLabel(lblPnt, key, x++, y);
				createButton(btnIncPnt, key, "add", x++, y);
			
			// Karma
			createButton(btnDecKar, key, "remove", x++, y);
			createLabel (lblKar, key, x++, y);
			createButton(btnIncKar, key, "add", x++, y);
			
			// Outer INC button
			createButton(btnIncAllMin, key, "add", x++, y);
			
			// All-in-one
			createButton(btnDecAll, key, "remove", x++, y);
			createLabel (lblAll, key, x++, y);
			createButton(btnIncAll, key, "add", x++, y);

			y++;
		}
		
//		grid.getColumnConstraints().add(new ColumnConstraints());
//		grid.getColumnConstraints().add(new ColumnConstraints(200,300,400));
//		for (int i=0; i<11; i++) {
//			ColumnConstraints c = new ColumnConstraints();
//			grid.getColumnConstraints().add(c);
//			columnsExpert.add(c);
//		}
		logger.log(Level.DEBUG, "After initComponents() grid has "+grid.getChildrenUnmodifiable().size()+" children");
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		Label hdAdjust = new Label(ResourceI18N.get(RES, "head.adjust")+":");
		Label hdAttrib = new Label(ResourceI18N.get(RES, "head.attrib")+":");
		Label hdKarma  = new Label(ResourceI18N.get(RES, "head.karma")+":");
		
		HBox line = new HBox(5, tsExpertMode, hdAdjust, lbAdjust, hdAttrib, lbPoints, hdKarma, lbKarma);
		HBox.setMargin(hdAttrib, new Insets(0,0,0,10));
		HBox.setMargin(hdKarma, new Insets(0,0,0,10));
		layout = new VBox(5, line, grid);
		getChildren().add(layout);		
		logger.log(Level.DEBUG, "After initLayout() grid has "+grid.getChildrenUnmodifiable().size()+" children");
	}
	
	//-------------------------------------------------------------------
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private AttributeValue<SR6SkillValue> value(SR6SkillValue key) {
//		return ((ShadowrunCharacter)getController().getModel()).getAttribute(key);
//	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		getSkinnable().useExpertModeProperty().addListener( (ov,o,n) -> updateLayout());
		tsExpertMode.selectedProperty().bindBidirectional(getSkinnable().useExpertModeProperty());
		
		btnDecAdj.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decreasePoints(e.getKey()); refresh();}));
		btnIncAdj.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increasePoints(e.getKey()); refresh();}));
		btnDecPnt.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decreasePoints2(e.getKey()); refresh();}));
		btnIncPnt.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increasePoints2(e.getKey()); refresh();}));
		btnDecKar.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decreasePoints3(e.getKey()); refresh();}));
		btnIncKar.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increasePoints3(e.getKey()); refresh();}));
		btnDecAll.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decrease(e.getKey()); refresh();}));
		btnIncAll.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increase(e.getKey()); refresh();}));
		btnDecAllMin.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {decreaseMinimal(e.getKey()); refresh();}));
		btnIncAllMin.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {increaseMinimal(e.getKey()); refresh();}));
		
		
        final ObservableMap<Object, Object> properties = getSkinnable().getProperties();
        properties.remove(SkinProperties.REFRESH);
        properties.remove(SkinProperties.WINDOW_MODE);
        properties.addListener(propertiesMapListener);
        
        toggles.selectedToggleProperty().addListener( (ov,o,n) -> refresh());
		logger.log(Level.DEBUG, "After initInteractivity() grid has "+grid.getChildrenUnmodifiable().size()+" children");
	}

	//-------------------------------------------------------------------
	private void updateLayout() {
		logger.log(Level.INFO, "updateLayout");
		removeAll();
		
		boolean expertMode = getSkinnable().isUseExpertMode();
		boolean enoughSpace= ResponsiveControlManager.getCurrentMode()!=WindowMode.MINIMAL;

		if (!expertMode) {
			toggles.selectToggle(null);
			updateLayoutSimple();
		} else {
			if (enoughSpace) {
				toggles.selectToggle(null);
				updateLayoutExpertNormal();
			} else {
				toggles.selectToggle(headBtnAttrib);
				updateLayoutExpertMinimal();
			}
		}
		
		if (getController()!=null) {
			refresh();
		}
		logger.log(Level.DEBUG, "After updateLayout() grid has "+grid.getChildrenUnmodifiable().size()+" children");
	}
	
	//-------------------------------------------------------------------
	private void removeAll() {
		
//		grid.getChildren().retainAll(lblNam.values());
		grid.getChildren().removeAll(headAdjust, headAttrib, headKarma, headResult, headDecOut, headIncOut, headBtnAdjust, headBtnAttrib, headBtnKarma);
		grid.getChildren().removeAll(btnDecAdj.values());
		grid.getChildren().removeAll(lblAdj.values());
		grid.getChildren().removeAll(btnIncAdj.values());
		grid.getChildren().removeAll(btnDecPnt.values());
		grid.getChildren().removeAll(lblPnt.values());
		grid.getChildren().removeAll(btnIncPnt.values());
		grid.getChildren().removeAll(btnDecKar.values());
		grid.getChildren().removeAll(lblKar.values());
		grid.getChildren().removeAll(btnIncKar.values());
		grid.getChildren().removeAll(btnDecAll.values());
		grid.getChildren().removeAll(lblAll.values());
		grid.getChildren().removeAll(btnIncAll.values());
		grid.getChildren().removeAll(btnDecAllMin.values());
		grid.getChildren().removeAll(btnIncAllMin.values());
		
		grid.getColumnConstraints().clear();
		logger.log(Level.INFO, "  removeAll() done");
	}
	
	//-------------------------------------------------------------------
	private void updateLayoutSimple() {
		logger.log(Level.INFO, "updateLayoutSimple");
		
		int y=0;
		grid.add(headResult, 2, y, 3,1);
		
		for (SR6SkillValue key : getSkinnable().getController().getModel().getSkillValues()) {
			y++;
//			logger.log(Level.INFO, "Put "+key+" at "+y);
			grid.add(btnDecAll.get(key), 2, y);
			grid.add(   lblAll.get(key), 3, y);
			grid.add(btnIncAll.get(key), 4, y);
			
			btnDecAll.get(key).setVisible(getController()!=null);
			btnIncAll.get(key).setVisible(getController()!=null);
		}
		grid.getColumnConstraints().add(new ColumnConstraints(15,30,30));
		grid.getColumnConstraints().add(new ColumnConstraints(80,150,200));
		grid.getColumnConstraints().add(new ColumnConstraints()); // Dec
		grid.getColumnConstraints().add(new ColumnConstraints(50)); // Value
		
//		grid.setGridLinesVisible(true);
	}
	
	//-------------------------------------------------------------------
	private void updateLayoutExpertMinimal() {
		logger.log(Level.INFO, "updateLayoutExpertMinimal");
		
		int y=0;
		grid.add(headDecOut   , 2, y, 1,1);
		grid.add(headBtnAdjust, 3, y, 1,1);
		grid.add(headBtnAttrib, 4, y, 1,1);
		grid.add(headBtnKarma , 5, y, 1,1);
		grid.add(headIncOut   , 6, y, 1,1);
		grid.add(headResult   , 7, y, 1,1);

		for (SR6SkillValue key : getSkinnable().getController().getModel().getSkillValues()) {
			y++;
			
			grid.add(btnDecAllMin.get(key), 2, y);
			grid.add(      lblAdj.get(key), 3, y);
			grid.add(      lblKar.get(key), 5, y);
			grid.add(btnIncAllMin.get(key), 6, y);
			grid.add(      lblAll.get(key), 7, y);
			
			GridPane.setFillWidth(lblAdj.get(key), true);
			GridPane.setFillHeight(lblAdj.get(key), true);
			if (lblPnt.get(key)!=null)
				GridPane.setFillWidth(lblPnt.get(key), true);
			GridPane.setFillHeight(lblPnt.get(key), true);
			GridPane.setFillWidth(lblKar.get(key), true);
			
			btnDecAllMin.get(key).setVisible(getController()!=null);
			btnIncAllMin.get(key).setVisible(getController()!=null);
		}
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints()); // Dec
		grid.getColumnConstraints().add(new ColumnConstraints(45)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints(45)); // 
		grid.getColumnConstraints().add(new ColumnConstraints(45)); // 
	}
	
	//-------------------------------------------------------------------
	private void updateLayoutExpertNormal() {
		logger.log(Level.INFO, "updateLayoutExpertNormal");
		
		int y=0;
		grid.add(headAdjust, 2, y, 3,1);
		grid.add(headAttrib, 5, y, 3,1);
		grid.add(headKarma , 8, y, 3,1);
		grid.add(headResult,11, y, 1,1);

		for (SR6SkillValue key : getSkinnable().getController().getModel().getSkillValues()) {
			y++;
			
			grid.add(btnDecAdj.get(key), 2, y);
			grid.add(   lblAdj.get(key), 3, y);
			grid.add(btnIncAdj.get(key), 4, y);
			grid.add(btnDecKar.get(key), 8, y);
			grid.add(   lblKar.get(key), 9, y);
			grid.add(btnIncKar.get(key),10, y);
			grid.add(   lblAll.get(key),11, y);
		}
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints()); // DecAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints()); // IncAdj
		grid.getColumnConstraints().add(new ColumnConstraints()); // DecAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints()); // IncAdj
		grid.getColumnConstraints().add(new ColumnConstraints()); // DecAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints()); // IncAdj
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void refresh() {
		logger.log(Level.INFO, "refresh with "+grid.getChildrenUnmodifiable().size()+" children");
		if (getSkinnable().getController()==null) return;
		ShadowrunCharacter model = (ShadowrunCharacter) getSkinnable().getController().getModel();

		lbAdjust.setText(String.valueOf(getController().getPointsLeft()));
		lbPoints.setText(String.valueOf(getController().getPointsLeft2()));
		lbKarma .setText(String.valueOf(getController().getPointsLeft3()));
		
		lblRec.entrySet().forEach(e -> {
			if (getController()==null) return;
			RecommendationState state = getController().getRecommendationState(e.getKey());
			e.getValue().setVisible(state!=null && state!=RecommendationState.NEUTRAL);
			});
		
		btnDecAdj.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreasedPoints(e.getKey()).get()));
		btnIncAdj.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreasedPoints(e.getKey()).get()));
		btnDecAdj.entrySet().forEach(e -> e.getValue().setVisible (getController().canBeDecreasedPoints(e.getKey()).get()));
		btnIncAdj.entrySet().forEach(e -> e.getValue().setVisible (getController().canBeIncreasedPoints(e.getKey()).get()));
		btnDecPnt.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreasedPoints2(e.getKey()).get()));
		btnIncPnt.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreasedPoints2(e.getKey()).get()));
		btnDecKar.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreasedPoints3(e.getKey()).get()));
		btnIncKar.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreasedPoints3(e.getKey()).get()));
		btnDecAll.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreased(e.getKey()).get()));
		btnIncAll.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreased(e.getKey()).get()));
		btnDecAllMin.entrySet().forEach(e -> e.getValue().setDisable(!canBeDecreasedMinimal(e.getKey())));
		btnIncAllMin.entrySet().forEach(e -> e.getValue().setDisable(!canBeIncreasedMinimal(e.getKey())));
		
		APrioritySettings settings = (APrioritySettings) model.getCharGenSettings(APrioritySettings.class);
		for (SR6SkillValue key : getSkinnable().getController().getModel().getSkillValues()) {
			PerAttributePoints per = settings.perAttrib.get(key);
			
			lblAll.get(key).setText(String.valueOf(key.getDistributed()));
			if (lblAdj.get(key)!=null) {
				lblAdj.get(key).setText(String.valueOf(per.points1));
				if (toggles.getSelectedToggle()==headBtnAdjust) {
					lblAdj.get(key).setStyle("-fx-background-color: primary; -fx-text-fill: -fx-base");
				} else {
					lblAdj.get(key).setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-text-base-color");
				}
			}
			if (lblPnt.get(key)!=null) {
				lblPnt.get(key).setText(String.valueOf(per.points2));
				if (toggles.getSelectedToggle()==headBtnAttrib) {
					lblPnt.get(key).setStyle("-fx-background-color: primary; -fx-text-fill: -fx-base");
				} else {
					lblPnt.get(key).setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-text-base-color");
				}
			}
			lblKar.get(key).setText(String.valueOf(per.points3));
			if (toggles.getSelectedToggle()==headBtnKarma) {
				lblKar.get(key).setStyle("-fx-background-color: primary; -fx-text-fill: -fx-base");
			} else {
				lblKar.get(key).setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-text-base-color");
			}
		}
	}

	//-------------------------------------------------------------------
	private boolean canBeDecreasedMinimal(SR6SkillValue attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle()==headBtnAdjust) {
				return getController().canBeDecreasedPoints(attribute).get();
			} else if (toggles.getSelectedToggle()==headBtnAttrib) {
				return getController().canBeDecreasedPoints2(attribute).get();
			} else if (toggles.getSelectedToggle()==headBtnKarma) {
				return getController().canBeDecreasedPoints3(attribute).get();
			}
		}
		
		return getController().canBeDecreased(attribute).get();
	}

	// -------------------------------------------------------------------
	private boolean canBeIncreasedMinimal(SR6SkillValue attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle() == headBtnAdjust) {
				return getController().canBeIncreasedPoints(attribute).get();
			} else if (toggles.getSelectedToggle() == headBtnAttrib) {
				return getController().canBeIncreasedPoints2(attribute).get();
			} else {
				return getController().canBeIncreasedPoints3(attribute).get();
			}
		}

		return getController().canBeIncreased(attribute).get();
	}

	// -------------------------------------------------------------------
	private void decreaseMinimal(SR6SkillValue attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle() == headBtnAdjust) {
				getController().decreasePoints2(attribute).get();
			} else if (toggles.getSelectedToggle() == headBtnAttrib) {
				getController().decreasePoints2(attribute).get();
			} else if (toggles.getSelectedToggle() == headBtnKarma) {
				getController().decreasePoints3(attribute).get();
			}
			return;
		}

		getController().decrease(attribute).get();
	}
	
	// -------------------------------------------------------------------
	private void increaseMinimal(SR6SkillValue attribute) {
		logger.log(Level.INFO, "increaseMinimal(" + attribute + ") while " + toggles.getSelectedToggle());
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle() == headBtnAdjust) {
				getController().increasePoints(attribute).get();
			} else if (toggles.getSelectedToggle() == headBtnAttrib) {
				getController().increasePoints2(attribute).get();
			} else if (toggles.getSelectedToggle() == headBtnKarma) {
				getController().increasePoints3(attribute).get();
			}
			return;
		}

		getController().increase(attribute).get();
	}

}
