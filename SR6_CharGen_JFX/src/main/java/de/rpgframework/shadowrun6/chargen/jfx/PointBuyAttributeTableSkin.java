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
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PointBuyAttributeGenerator;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.SkinProperties;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class PointBuyAttributeTableSkin extends SkinBase<PointBuyAttributeTable<?,?,?>> {

	private final static Logger logger = System.getLogger(PointBuyAttributeTableSkin.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle
			.getBundle(ShadowrunAttributeTable.class.getName());

	private ToggleSwitch tsExpertMode;
	private Label lbPoints1, lbPoints2;
	private GridPane grid;
	private VBox layout;

	private Label headDecOut, headAdjust, headAttrib, headKarma, headIncOut, headResult;
	private ToggleButton headBtnAdjust, headBtnAttrib, headBtnKarma;
	private ToggleGroup toggles = new ToggleGroup();

	private Map<ShadowrunAttribute, Label>  lblRec = new HashMap<>();
	private Map<ShadowrunAttribute, Label>  lblNam = new HashMap<>();

	private Map<ShadowrunAttribute, Button> btnDecAllMin = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnDecAdj = new HashMap<>();
	private Map<ShadowrunAttribute, Label>  lblAdj = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnIncAdj = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnDecPnt = new HashMap<>();
	private Map<ShadowrunAttribute, Label>  lblPnt = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnIncPnt = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnDecKar = new HashMap<>();
	private Map<ShadowrunAttribute, Label>  lblKar = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnIncKar = new HashMap<>();

	private Map<ShadowrunAttribute, Button> btnDecAll = new HashMap<>();
	private Map<ShadowrunAttribute, Label>  lblAll = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnIncAll = new HashMap<>();
	private Map<ShadowrunAttribute, Button> btnIncAllMin = new HashMap<>();

	private Map<ShadowrunAttribute, List<Control>> allPerAttr = new HashMap<>();

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
	public PointBuyAttributeTableSkin(PointBuyAttributeTable<?, ?, ?> control) {
		super(control);
		initComponents();
		initLayout();
		initInteractivity();
		updateLayout();

		toggles.selectToggle(headBtnAttrib);
		refresh();
	}

	//-------------------------------------------------------------------
	private PointBuyAttributeGenerator getController() {
		if (getSkinnable().getController()==null) return null;
		return (PointBuyAttributeGenerator) getSkinnable().getController().getAttributeController();
	}

	//-------------------------------------------------------------------
	private Button createButton(Map<ShadowrunAttribute,Button> map,
			ShadowrunAttribute key, String icon, int x, int y) {
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
	private Label createLabel(Map<ShadowrunAttribute,Label> map,
			ShadowrunAttribute key, int x, int y) {
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
	private void createName(Map<ShadowrunAttribute,Label> map,
			ShadowrunAttribute key, int x, int y) {
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
	private void createRecomLabel(Map<ShadowrunAttribute,Label> map,
			ShadowrunAttribute key, int x, int y) {
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

		lbPoints1 = new Label("?");
		lbPoints2 = new Label("?");

		lbPoints1.setStyle("-fx-text-fill: -fx-text-base-color");
		lbPoints2.setStyle("-fx-text-fill: -fx-text-base-color");

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


		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
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
			switch (key) {
			case MAGIC:
			case RESONANCE:
				x += 3;
				break;
			default:
				createButton(btnDecPnt, key, "remove", x++, y);
				createLabel(lblPnt, key, x++, y);
				createButton(btnIncPnt, key, "add", x++, y);
			}

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
		Label hdPoints1 = new Label(ResourceI18N.get(RES, "head.adjust")+":");
		Label hdPoints2 = new Label(ResourceI18N.get(RES, "head.attrib")+":");

		HBox line = new HBox(5, tsExpertMode, hdPoints1, lbPoints1, hdPoints2, lbPoints2);
		HBox.setMargin(hdPoints1, new Insets(0,0,0,10));
		HBox.setMargin(hdPoints2, new Insets(0,0,0,10));

		layout = new VBox(5, line, grid);
		getChildren().add(layout);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AttributeValue<ShadowrunAttribute> value(ShadowrunAttribute key) {
		return ((ShadowrunCharacter)getController().getModel()).getAttribute(key);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		getSkinnable().useExpertModeProperty().addListener( (ov,o,n) -> updateLayout());
		tsExpertMode.selectedProperty().bindBidirectional(getSkinnable().useExpertModeProperty());

		btnDecAdj.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decreasePoints(value(e.getKey())); refresh();}));
		btnIncAdj.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increasePoints(value(e.getKey())); refresh();}));
		btnDecPnt.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decreasePoints2(value(e.getKey())); refresh();}));
		btnIncPnt.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increasePoints2(value(e.getKey())); refresh();}));
		btnDecKar.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decreasePoints3(value(e.getKey())); refresh();}));
		btnIncKar.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increasePoints3(value(e.getKey())); refresh();}));
		btnDecAll.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().decrease(getSkinnable().getController().getModel().getAttribute(e.getKey())); refresh();}));
		btnIncAll.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {getController().increase(getSkinnable().getController().getModel().getAttribute(e.getKey())); refresh();}));
		btnDecAllMin.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {decreaseMinimal(e.getKey()); refresh();}));
		btnIncAllMin.entrySet().forEach(e -> e.getValue().setOnAction(ev -> {increaseMinimal(e.getKey()); refresh();}));

		getSkinnable().showMagicProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "showMagic changed from {0} to {1}",o,n);
			for (Control ctrl : allPerAttr.get(ShadowrunAttribute.MAGIC)) {
				ctrl.setVisible(true);
				ctrl.setManaged(true);
				updateLayout();
			}
		});
		getSkinnable().showResonanceProperty().addListener( (ov,o,n) -> {
			logger.log(Level.INFO, "showReso changed from {0} to {1}",o,n);
			for (Control ctrl : allPerAttr.get(ShadowrunAttribute.RESONANCE)) {
				ctrl.setVisible(true);
				ctrl.setManaged(true);
			}
			updateLayout();
		});


        final ObservableMap<Object, Object> properties = getSkinnable().getProperties();
        properties.remove(SkinProperties.REFRESH);
        properties.remove(SkinProperties.WINDOW_MODE);
        properties.addListener(propertiesMapListener);

        toggles.selectedToggleProperty().addListener( (ov,o,n) -> {
        	logger.log(Level.DEBUG, "Toggle changed to {}",n);
        	refresh();
        	});
		logger.log(Level.DEBUG, "After initInteractivity() grid has "+grid.getChildrenUnmodifiable().size()+" children");
	}

	//-------------------------------------------------------------------
	private void updateLayout() {
		logger.log(Level.DEBUG, "updateLayout");
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
		if (allPerAttr.get(ShadowrunAttribute.MAGIC)!=null) grid.getChildren().removeAll(allPerAttr.get(ShadowrunAttribute.MAGIC));
		if (allPerAttr.get(ShadowrunAttribute.RESONANCE)!=null) grid.getChildren().removeAll(allPerAttr.get(ShadowrunAttribute.RESONANCE));
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
		logger.log(Level.DEBUG, "  removeAll() done");
	}

	//-------------------------------------------------------------------
	private void updateLayoutSimple() {
		logger.log(Level.DEBUG, "updateLayoutSimple");
		boolean showMagic  = getSkinnable().isShowMagic();
		boolean showReson  = getSkinnable().isShowResonance();

		int y=0;
		grid.add(headResult, 2, y, 3,1);

		lblNam.get(ShadowrunAttribute.MAGIC).setVisible(showMagic);
		lblNam.get(ShadowrunAttribute.RESONANCE).setVisible(showReson);
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			y++;
			if (key==ShadowrunAttribute.MAGIC) {
				if (!showMagic)
					continue;
				grid.add(lblRec.get(key), 0, y);
				grid.add(lblNam.get(key), 1, y);
			}
			if (key==ShadowrunAttribute.RESONANCE) {
				if (!showReson)
					continue;
				grid.add(lblRec.get(key), 0, y);
				grid.add(lblNam.get(key), 1, y);
			}

			grid.add(btnDecAll.get(key), 2, y);
			grid.add(   lblAll.get(key), 3, y);
			grid.add(btnIncAll.get(key), 4, y);

			btnDecAll.get(key).setVisible(getController()!=null);
			btnIncAll.get(key).setVisible(getController()!=null);
		}
		grid.getColumnConstraints().add(new ColumnConstraints(30));
		grid.getColumnConstraints().add(new ColumnConstraints(150));
		grid.getColumnConstraints().add(new ColumnConstraints()); // Dec
		grid.getColumnConstraints().add(new ColumnConstraints(50)); // Value

		grid.setGridLinesVisible(true);
	}

	//-------------------------------------------------------------------
	private void updateLayoutExpertMinimal() {
		logger.log(Level.INFO, "updateLayoutExpertMinimal");
		boolean showMagic  = getSkinnable().isShowMagic();
		boolean showReson  = getSkinnable().isShowResonance();

		int y=0;
		grid.add(headDecOut   , 2, y, 1,1);
		grid.add(headBtnAdjust, 3, y, 1,1);
		grid.add(headBtnAttrib, 4, y, 1,1);
		grid.add(headBtnKarma , 5, y, 1,1);
		grid.add(headIncOut   , 6, y, 1,1);
		grid.add(headResult   , 7, y, 1,1);

		lblNam.get(ShadowrunAttribute.MAGIC).setVisible(showMagic);
		lblNam.get(ShadowrunAttribute.RESONANCE).setVisible(showReson);
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			y++;
			if (key==ShadowrunAttribute.MAGIC) {
				if (!showMagic)
					continue;
				grid.add(lblRec.get(key), 0, y);
				grid.add(lblNam.get(key), 1, y);
			}
			if (key==ShadowrunAttribute.RESONANCE) {
				if (!showReson)
					continue;
				grid.add(lblRec.get(key), 0, y);
				grid.add(lblNam.get(key), 1, y);
			}

			grid.add(btnDecAllMin.get(key), 2, y);
			grid.add(      lblAdj.get(key), 3, y);
			if (key!=ShadowrunAttribute.MAGIC && key!=ShadowrunAttribute.RESONANCE && key!=ShadowrunAttribute.EDGE)
				grid.add(  lblPnt.get(key), 4, y);
			grid.add(      lblKar.get(key), 5, y);
			grid.add(btnIncAllMin.get(key), 6, y);
			grid.add(      lblAll.get(key), 7, y);

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
		boolean showMagic  = getSkinnable().isShowMagic();
		boolean showReson  = getSkinnable().isShowResonance();

		int y=0;
		grid.add(headAdjust, 2, y, 3,1);
		grid.add(headAttrib, 5, y, 3,1);
		grid.add(headKarma , 8, y, 3,1);
		grid.add(headResult,11, y, 1,1);

		lblNam.get(ShadowrunAttribute.MAGIC).setVisible(showMagic);
		lblNam.get(ShadowrunAttribute.RESONANCE).setVisible(showReson);
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			y++;
			if (key==ShadowrunAttribute.MAGIC) {
				if (!showMagic)
					continue;
				grid.add(lblRec.get(key), 0, y);
				grid.add(lblNam.get(key), 1, y);
			}
			if (key==ShadowrunAttribute.RESONANCE) {
				if (!showReson)
					continue;
				grid.add(lblRec.get(key), 0, y);
				grid.add(lblNam.get(key), 1, y);
			}

			grid.add(btnDecAdj.get(key), 2, y);
			grid.add(   lblAdj.get(key), 3, y);
			grid.add(btnIncAdj.get(key), 4, y);
			if (key!=ShadowrunAttribute.MAGIC && key!=ShadowrunAttribute.RESONANCE&& key!=ShadowrunAttribute.EDGE) {
				grid.add(btnDecPnt.get(key), 5, y);
				grid.add(   lblPnt.get(key), 6, y);
				grid.add(btnIncPnt.get(key), 7, y);
			}
			grid.add(btnDecKar.get(key), 8, y);
			grid.add(   lblKar.get(key), 9, y);
			grid.add(btnIncKar.get(key),10, y);
			grid.add(   lblAll.get(key),11, y);
		}
		ColumnConstraints nameColConst = new ColumnConstraints();
		nameColConst.setMinWidth(140);
		//nameColConst.setPrefWidth(250);
		nameColConst.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().add(new ColumnConstraints()); // Recommend
		grid.getColumnConstraints().add(nameColConst);
		grid.getColumnConstraints().add(new ColumnConstraints()); // DecAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints()); // IncAdj
		grid.getColumnConstraints().add(new ColumnConstraints()); // DecAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints()); // IncAdj
		grid.getColumnConstraints().add(new ColumnConstraints()); // DecAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // Adjust
		grid.getColumnConstraints().add(new ColumnConstraints()); // IncAdj
		grid.getColumnConstraints().add(new ColumnConstraints(40)); // All
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void refresh() {
		logger.log(Level.DEBUG, "refresh with "+grid.getChildrenUnmodifiable().size()+" children");
		if (getSkinnable().getController()==null) return;
		@SuppressWarnings("rawtypes")
		ShadowrunCharacter model = (ShadowrunCharacter) getSkinnable().getController().getModel();
		SR6PointBuySettings settings = (SR6PointBuySettings) model.getCharGenSettings(SR6PointBuySettings.class);

		String label1 = String.format("%d+%d /12", getController().getPointsLeft(), ((SR6PointBuyAttributeGenerator)getController()).getCreatedSpecial());
		String label2 = String.format("%d+%d /20", getController().getPointsLeft2(), ((SR6PointBuyAttributeGenerator)getController()).getCreatedAttrib());
		lbPoints1.setText(label1);
		lbPoints1.setTooltip(new Tooltip(ResourceI18N.format(RES, "sr6pointbuy.tooltip", settings.characterPoints,
				getController().getPointsLeft(), getController().getPointsLeft2())
				));
		lbPoints2.setText(label2);

		lblRec.entrySet().forEach(e -> {
			if (getController()==null) return;
			RecommendationState state = getController().getRecommendationState(e.getKey());
			e.getValue().setVisible(state!=null && state!=RecommendationState.NEUTRAL);
			});

		btnDecAdj.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreasedPoints(value(e.getKey())).get()));
		btnIncAdj.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreasedPoints(value(e.getKey())).get()));
		btnDecAdj.entrySet().forEach(e -> e.getValue().setVisible (getController().canBeDecreasedPoints(value(e.getKey())).get()));
		btnIncAdj.entrySet().forEach(e -> e.getValue().setVisible (getController().canBeIncreasedPoints(value(e.getKey())).get()));
		btnDecPnt.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreasedPoints2(value(e.getKey())).get()));
		btnIncPnt.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreasedPoints2(value(e.getKey())).get()));
		btnDecKar.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreasedPoints3(value(e.getKey())).get()));
		btnIncKar.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreasedPoints3(value(e.getKey())).get()));
		btnDecAll.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeDecreased(value(e.getKey())).get()));
		btnIncAll.entrySet().forEach(e -> e.getValue().setDisable(!getController().canBeIncreased(value(e.getKey())).get()));
		btnDecAllMin.entrySet().forEach(e -> e.getValue().setDisable(!canBeDecreasedMinimal(e.getKey())));
		btnIncAllMin.entrySet().forEach(e -> e.getValue().setDisable(!canBeIncreasedMinimal(e.getKey())));

		for (ShadowrunAttribute key : ShadowrunAttribute.primaryAndSpecialValues()) {
			AttributeValue<ShadowrunAttribute> val = model.getAttribute(key);
			PerAttributePoints per = settings.perAttrib.get(key);

			lblAll.get(key).setText(String.valueOf(val.getDistributed()));
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
	private boolean canBeDecreasedMinimal(ShadowrunAttribute attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle()==headBtnAdjust) {
				return getController().canBeDecreasedPoints(value(attribute)).get();
			} else if (toggles.getSelectedToggle()==headBtnAttrib) {
				return getController().canBeDecreasedPoints2(value(attribute)).get();
			} else if (toggles.getSelectedToggle()==headBtnKarma) {
				return getController().canBeDecreasedPoints3(value(attribute)).get();
			}
		}

		return getController().canBeDecreased(value(attribute)).get();
	}

	// -------------------------------------------------------------------
	private boolean canBeIncreasedMinimal(ShadowrunAttribute attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle() == headBtnAdjust) {
				return getController().canBeIncreasedPoints(value(attribute)).get();
			} else if (toggles.getSelectedToggle() == headBtnAttrib) {
				return getController().canBeIncreasedPoints2(value(attribute)).get();
			} else {
				return getController().canBeIncreasedPoints3(value(attribute)).get();
			}
		}

		return getController().canBeIncreased(value(attribute)).get();
	}

	// -------------------------------------------------------------------
	private void decreaseMinimal(ShadowrunAttribute attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle() == headBtnAdjust) {
				getController().decreasePoints2(value(attribute)).get();
			} else if (toggles.getSelectedToggle() == headBtnAttrib) {
				getController().decreasePoints2(value(attribute)).get();
			} else if (toggles.getSelectedToggle() == headBtnKarma) {
				getController().decreasePoints3(value(attribute)).get();
			}
			return;
		}

		getController().decrease(value(attribute)).get();
	}

	// -------------------------------------------------------------------
	private void increaseMinimal(ShadowrunAttribute attribute) {
		if (getSkinnable().isUseExpertMode()) {
			if (toggles.getSelectedToggle() == headBtnAdjust) {
				getController().increasePoints(value(attribute)).get();
			} else if (toggles.getSelectedToggle() == headBtnAttrib) {
				getController().increasePoints2(value(attribute)).get();
			} else if (toggles.getSelectedToggle() == headBtnKarma) {
				getController().increasePoints3(value(attribute)).get();
			}
			return;
		}

		getController().increase(value(attribute)).get();
	}

}
