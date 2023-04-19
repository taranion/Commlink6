package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.stream.IntStream;

import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author prelle
 *
 */
public class ModSlotConfigPaneSkin extends SkinBase<ModSlotConfigPane> {

	private final static Logger logger = System.getLogger(ModSlotConfigPane.class.getPackageName());

	private final static String STATE_PLUS = "state-plus";
	private final static String STATE_MINUS = "state-minus";

	private final static double PADDING = 25;
	private final static double RADIUS = 100;

	private final static Color IDLE = Color.color(0.6, .8, .7, 0.5);
	private final static Color HOVER = Color.color(0.6, .8, .7, 1);

	private Arc[] arcs;
	private Pane group;
	private Label lbChassisVal;
	private Label lbElectronicsVal;
	private Label lbPowerTrainVal;
	private Label lbPowerChass;
	private Label lbChassElec;
	private Label lbElecPower;
	private Polygon[] arrowHeadClockwise, arrowHeadCounter;
	private Circle[] btnSegment;
	private Tooltip[] tooltip;

	//-------------------------------------------------------------------
	protected ModSlotConfigPaneSkin(ModSlotConfigPane control) {
		super(control);

		arcs = new Arc[6];
		group = new Pane();
		for (int i=0; i<6; i++) {
			arcs[i] = new Arc();
			arcs[i].setCenterX(RADIUS+PADDING);
			arcs[i].setCenterY(RADIUS+PADDING);
			arcs[i].setRadiusX(RADIUS);
			arcs[i].setRadiusY(RADIUS);
			arcs[i].setStartAngle(i*60+38);
			arcs[i].setLength(44);
			arcs[i].setType(ArcType.OPEN);
			arcs[i].setFill(Color.LAVENDER);
			arcs[i].setStroke(Color.BLACK);
			arcs[i].setStrokeWidth(2);
			arcs[i].setFill(Color.TRANSPARENT);
			group.getChildren().add(arcs[i]);
		}

		lbChassisVal = new Label("01");
		lbChassisVal.setStyle("-fx-font-weight: bold");
		lbChassisVal.relocate(RADIUS+PADDING -7, PADDING -7);
		group.getChildren().add(lbChassisVal);
		lbElectronicsVal = new Label("02");
		lbElectronicsVal.setStyle("-fx-font-weight: bold;");
		lbElectronicsVal.relocate(RADIUS+PADDING +RADIUS*0.78, RADIUS+PADDING +RADIUS*0.38);
		group.getChildren().add(lbElectronicsVal);
		lbPowerTrainVal = new Label("03");
		lbPowerTrainVal.setStyle("-fx-font-weight: 600");
		lbPowerTrainVal.relocate(RADIUS+PADDING -RADIUS*0.90, RADIUS+PADDING +RADIUS*0.38);
		group.getChildren().add(lbPowerTrainVal);

		lbPowerChass = new Label("a");
		lbPowerChass.relocate(RADIUS+PADDING -RADIUS*0.9, RADIUS+PADDING -RADIUS*0.6);
		lbChassElec  = new Label("b");
		lbChassElec.relocate(RADIUS+PADDING +RADIUS*0.85, RADIUS+PADDING -RADIUS*0.6);
		lbElecPower  = new Label("c");
		lbElecPower.relocate(RADIUS+PADDING -7, RADIUS+PADDING +RADIUS-10);
		group.getChildren().addAll(lbPowerChass,lbChassElec,lbElecPower);

		double[] polyPoints = new double[]{
				6,-3,
				0, 9,
				-6, -3
		};

		arrowHeadClockwise = new Polygon[6];
		int i=0;
		for (int w=51; w<360; w+=60) {
			Point2D p = getPointForAngle(w);
			Polygon poly = new Polygon(polyPoints);
			poly.relocate(p.getX()-6, p.getY()-6);
			poly.setRotate(w-90);
			arrowHeadClockwise[i++] = poly;
			group.getChildren().add(poly);
			poly.setVisible(false);
		}

		arrowHeadCounter = new Polygon[6];
		i=0;
		for (int w=9; w<360; w+=60) {
			Point2D p = getPointForAngle(w);
			Polygon poly = new Polygon(polyPoints);
			poly.relocate(p.getX()-6, p.getY()-6);
			poly.setRotate(w+90);
			arrowHeadCounter[i++] = poly;
			group.getChildren().add(poly);
			poly.setVisible(false);
		}

		btnSegment = new Circle[6];
		tooltip = new Tooltip[6];
		i=0;
		for (int w=30; w<360; w+=60) {
			Point2D p = getPointForAngle(w);
			Circle btn = new Circle(p.getX()-6, p.getY(), 10);
			btn.setFill(Color.color(0.6, .8, .7, 0.5));
			btn.relocate(p.getX()-10, p.getY()-10);

			tooltip[i] = new Tooltip();
			tooltip[i].setShowDelay(Duration.millis(200));
			Tooltip.install(btn, tooltip[i]);

			group.getChildren().add(btn);
			btn.setOnMouseEntered(ev -> btn.setFill(HOVER) );
			btn.setOnMouseExited (ev -> btn.setFill(IDLE ) );
			btn.setOnMouseClicked(ev -> clickedSegment(btn));
			btnSegment[i++] = btn;
		}

		makeLabel(0, ItemHook.VEHICLE_CHASSIS.getName());
		makeLabel(120, ItemHook.VEHICLE_ELECTRONICS.getName());
		makeLabel(240, ItemHook.VEHICLE_POWERTRAIN.getName());


		getChildren().add(group);

		getSkinnable().configProperty().addListener( (o,v,n) -> refresh());
		refresh();
	}

	//-------------------------------------------------------------------
	private void makeLabelCircular(double centerAngle, String text) {
		double perLetter = 3.5;
		double startAngle = centerAngle - (text.length()/2.0 * perLetter);
		if (centerAngle>90 && centerAngle<=270) {
			StringBuilder builder = new StringBuilder(text);
			builder.reverse();
			text = builder.toString();
		}

		int rad = (centerAngle<90)?125:120;

		for (int i=0; i<text.length(); i++) {
			double angle = startAngle +i*perLetter;
			Point2D p = getPointForAngle(angle, rad);
			char c = text.charAt(i);
			Text t = new Text(String.valueOf(c));
			t.relocate(p.getX(), p.getY());
			if (centerAngle>90 && centerAngle<=270) {
				t.setRotate(angle+180);
			} else {
				t.setRotate(angle);
			}
			group.getChildren().add(t);
		}
	}

	//-------------------------------------------------------------------
	private void makeLabel(double centerAngle, String text) {
		int inner = (int)RADIUS-30;
		Text t = new Text(text);
		Point2D p = getPointForAngle(centerAngle, inner);
		t.relocate(p.getX()+30+PADDING -text.length()*3, p.getY()+45);
			if (centerAngle>90 && centerAngle<=270) {
				t.setRotate(centerAngle+180);
			} else {
				t.setRotate(centerAngle);
			}
			group.getChildren().add(t);
	}

	//-------------------------------------------------------------------
	private static Point2D getPointForAngle(double angle) {
		angle = (angle-90)%360;
		double rad = Math.toRadians(angle);
		double y = Math.sin(rad)*RADIUS +RADIUS+PADDING;
		double x = Math.cos(rad)*RADIUS +RADIUS+PADDING;
		return new Point2D(x, y);
	}

	//-------------------------------------------------------------------
	private static Point2D getPointForAngle(double angle, int radius) {
		angle = (angle-90)%360;
		double rad = Math.toRadians(angle);
		double y = Math.sin(rad)*radius +radius+PADDING -25;
		double x = Math.cos(rad)*radius +radius+PADDING-25;
		return new Point2D(x, y);
	}

	//-------------------------------------------------------------------
	private void clickedSegment(Circle btn) {
		int pos = IntStream.range(0, 6).filter(i -> btn == btnSegment[i]).findFirst().orElse(-1);
		logger.log(Level.INFO, "Clicked {0}",pos);
		switch (pos) {
		case 0: getSkinnable().fire( ModSlotConfigPane.FROM_ELECTRONICS_TO_CHASSIS); break;
		case 1: getSkinnable().fire( ModSlotConfigPane.FROM_CHASSIS_TO_ELECTRONICS); break;
		case 2: getSkinnable().fire( ModSlotConfigPane.FROM_POWER_TO_ELECTRONICS  ); break;
		case 3: getSkinnable().fire( ModSlotConfigPane.FROM_ELECTRONICS_TO_POWER  ); break;
		case 4: getSkinnable().fire( ModSlotConfigPane.FROM_CHASSIS_TO_POWER ); break;
		case 5: getSkinnable().fire( ModSlotConfigPane.FROM_POWER_TO_CHASSIS ); break;
		}
	}

	//-------------------------------------------------------------------
	private boolean canBeUsed(int pos) {
		switch (pos) {
		case 0: return getSkinnable().canUse( ModSlotConfigPane.FROM_ELECTRONICS_TO_CHASSIS);
		case 1: return getSkinnable().canUse( ModSlotConfigPane.FROM_CHASSIS_TO_ELECTRONICS);
		case 2: return getSkinnable().canUse( ModSlotConfigPane.FROM_POWER_TO_ELECTRONICS  );
		case 3: return getSkinnable().canUse( ModSlotConfigPane.FROM_ELECTRONICS_TO_POWER  );
		case 4: return getSkinnable().canUse( ModSlotConfigPane.FROM_CHASSIS_TO_POWER );
		case 5: return getSkinnable().canUse( ModSlotConfigPane.FROM_POWER_TO_CHASSIS );
		}
		return true;
	}

	//-------------------------------------------------------------------
	void refresh() {
		ModSlotConfigPane config = getSkinnable();
		logger.log(Level.TRACE, "refresh");
		lbChassisVal    .setText( String.valueOf( config.chassisProperty().get()));
		lbElectronicsVal.setText( String.valueOf( config.electronicsProperty().get()));
		lbPowerTrainVal .setText( String.valueOf( config.powerProperty().get()));

		switch (getSkinnable().chassisStateProperty().get()) {
		case NEUTRAL:
			lbChassisVal.getStyleClass().removeAll(STATE_MINUS, STATE_PLUS);
			break;
		case PLUS:
			lbChassisVal.getStyleClass().remove(STATE_MINUS);
			lbChassisVal.getStyleClass().add(STATE_PLUS);
			break;
		case MINUS:
			lbChassisVal.getStyleClass().add(STATE_MINUS);
			lbChassisVal.getStyleClass().remove(STATE_PLUS);
			break;
		}

		update(lbChassElec, arrowHeadClockwise[1], arrowHeadCounter[0], config.chassisToElectronicsProperty().get());
		update(lbElecPower, arrowHeadClockwise[3], arrowHeadCounter[2], config.electronicsToPowerProperty().get());
		update(lbPowerChass, arrowHeadClockwise[5], arrowHeadCounter[4], config.powerToChassisProperty().get());

		for (int i=0; i<6; i++) {
			boolean canBeUsed = canBeUsed(i);
			btnSegment[i].setVisible(canBeUsed);
		}
	}

	//-------------------------------------------------------------------
	private void update(Label lb, Polygon cWise, Polygon ccWise, int val) {
		lb.setText( String.valueOf( Math.abs( val )));
		cWise.setVisible(val>0);
		ccWise.setVisible(val<0);
	}

}
