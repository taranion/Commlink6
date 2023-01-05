package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathStep;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

/**
 * @author prelle
 *
 */
public class VisualQualityPathPane extends Group {

	private final static int WIDTH_UNIT = 60;

	private final static Logger logger = System.getLogger(VisualQualityPathPane.class.getPackageName());

	private class PathNode {
		QualityPathStep step;
		int x,y;
		public PathNode(QualityPathStep step, int lvl) {
			this.step = step;
			y=lvl;
		}
	}

	private QualityPath data;
	private Map<Integer,List<PathNode>> nodesPerLevel = new LinkedHashMap<>();
	private Map<String,PathNode> nodesByID = new HashMap<>();
	private Map<QualityPathStep, Circle> dotsByStep = new HashMap<>();
	private SelectionModel<QualityPathStep> selectionModel;

	//-------------------------------------------------------------------
	public VisualQualityPathPane() {
		selectionModel = new SingleSelectionModel<QualityPathStep>() {
			protected QualityPathStep getModelItem(int index) {
				return data.getSteps().get(index);
			}
			protected int getItemCount() {
				return data.getSteps().size();
			}
		};

		selectionModel.selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) {
				Circle old = dotsByStep.get(o);
				if (old!=null)
					old.setFill(Color.TRANSPARENT);
			}
			if (n!=null) {
				Circle dot = dotsByStep.get(n);
				if (dot!=null)
					dot.setFill(Color.WHEAT);
			}
		});
	}

	//-------------------------------------------------------------------
	public SelectionModel<QualityPathStep> getSelectionModel() {
		return selectionModel;
	}

	//-------------------------------------------------------------------
	public void setData(QualityPath path) {
		this.data = path;
		calculateNodePositions();
		redraw();
	}

	//-------------------------------------------------------------------
	private void calculateNodePositions() {
		// Determine how "wide" the tree is per level and
		// the maximum width
		nodesPerLevel.clear();
		nodesByID.clear();
		dotsByStep.clear();
		int maxWidth=0;
		for (QualityPathStep step : data.getSteps()) {
			List<PathNode> nodes = nodesPerLevel.get(step.getLevel());
			if (nodes==null) {
				nodes=new ArrayList<>();
				nodesPerLevel.put(step.getLevel(), nodes);
			}
			PathNode node = new PathNode(step, step.getLevel());
			nodesByID.put(step.getId(), node);
			nodes.add(node);
			maxWidth = Math.max(maxWidth, nodes.size());
		}

		int width = maxWidth + (maxWidth-1);

		// Now that each node has an Y position and we know how many
		// nodes there a per Y-level, distribute the nodes per level evenly
		// ...X...   ..X..   .X.
		// ..X.X..   .X.X.   X.X
		// .X.X.X.   X.X.X
		// X.X.X.X
		int middle = (width-1)/2;
		for (int y=0; y<nodesPerLevel.size(); y++) {
			List<PathNode> nodes = nodesPerLevel.get(y);
			int nodesPerLine = nodes.size();
			int start = middle - (int)(nodesPerLine/2);
			int x = start;
			for (PathNode node : nodes) {
				node.x = x;
				x+=2;
				logger.log(Level.DEBUG, "Node {0} is at {1},{2}", node.step.getId(), node.x, node.y);
			}
		}
	}

	private void redraw() {
		getChildren().clear();
		for (int lvl=0; lvl<nodesPerLevel.size(); lvl++) {
			List<PathNode> nodes = nodesPerLevel.get(lvl);
			for (PathNode node : nodes) {
				double x = node.x * WIDTH_UNIT + WIDTH_UNIT/2;
				double y = node.y * 100;
				System.err.println("VisualQualityPathPane: x="+x);
				Circle dot = new Circle(x, y, 10, Color.TRANSPARENT);
				dot.setStroke(Color.WHEAT);
				dot.setStrokeWidth(2);
				dot.setOnMouseEntered(ev -> dot.setStroke(Color.DEEPPINK));
				dot.setOnMouseExited(ev -> dot.setStroke(Color.WHEAT));
				dot.setOnMouseClicked(ev -> {
					logger.log(Level.INFO, "Clicked "+node.step);
					selectionModel.select(node.step);
					});
				dotsByStep.put(node.step, dot);
				getChildren().add(dot);

				// Node name
				Text text = new Text(x+12,y, node.step.getName());
				text.setStroke(Color.WHITE);
				text.setStyle("-fx-font-family: 'Segoe UI'");
				text.setStrokeWidth(1);
				DropShadow shadow = new DropShadow(1, Color.BLACK);
				shadow.setSpread(1.0);
				text.setEffect(shadow);
				getChildren().add(text);

				// Lines
				List<String> foo = node.step.getNextSteps();
				for (String next : foo) {
					PathNode endNode = nodesByID.get(next);
					if (endNode==null) {
						logger.log(Level.ERROR, "QualityPath ''{0}'': No such next node ''{1}''", data.getId(), next);
						continue;
					}

					double ex = endNode.x * WIDTH_UNIT + WIDTH_UNIT/2;
					double ey = endNode.y * 100;
//					Line line = new Line(x, y, ex, ey);
					CubicCurve line = new CubicCurve(x, y+13, x, y+50, ex, ey-65, ex, ey-15);
					line.setFill(Color.TRANSPARENT);
					if (ex>x) {
						line.setControlX1(x+(ex-x)/2);
					} else if (ex<x) {
						line.setControlX1(x-(x-ex)/2);
					}
					line.setStrokeWidth(3);
					line.setStroke(Color.WHEAT);

					double scale=WIDTH_UNIT/2;//size/2d;
				    Point2D tan=evalDt(line,1f).normalize().multiply(scale);
				    Point2D ori=eval(line,1f);
					Path arrowEnd=new Path();
				    arrowEnd.getElements().add(new MoveTo(ori.getX()-0.2*tan.getX()-0.2*tan.getY(),
				                                        ori.getY()-0.2*tan.getY()+0.2*tan.getX()));
				    arrowEnd.getElements().add(new LineTo(ori.getX(), ori.getY()));
				    arrowEnd.getElements().add(new LineTo(ori.getX()-0.2*tan.getX()+0.2*tan.getY(),
				                                        ori.getY()-0.2*tan.getY()-0.2*tan.getX()));
				    arrowEnd.setStroke(Color.WHEAT);
				    arrowEnd.setStrokeWidth(3);
					getChildren().add(line);
					getChildren().add(arrowEnd);

				}
			}
		}
		// Change preferred width

	}

	/**
	 * Evaluate the cubic curve at a parameter 0<=t<=1, returns a Point2D
	 * @param c the CubicCurve
	 * @param t param between 0 and 1
	 * @return a Point2D
	 */
	private Point2D eval(CubicCurve c, float t){
	    Point2D p=new Point2D(Math.pow(1-t,3)*c.getStartX()+
	            3*t*Math.pow(1-t,2)*c.getControlX1()+
	            3*(1-t)*t*t*c.getControlX2()+
	            Math.pow(t, 3)*c.getEndX(),
	            Math.pow(1-t,3)*c.getStartY()+
	            3*t*Math.pow(1-t, 2)*c.getControlY1()+
	            3*(1-t)*t*t*c.getControlY2()+
	            Math.pow(t, 3)*c.getEndY());
	    return p;
	}

	/**
	 * Evaluate the tangent of the cubic curve at a parameter 0<=t<=1, returns a Point2D
	 * @param c the CubicCurve
	 * @param t param between 0 and 1
	 * @return a Point2D
	 */
	private Point2D evalDt(CubicCurve c, float t){
	    Point2D p=new Point2D(-3*Math.pow(1-t,2)*c.getStartX()+
	            3*(Math.pow(1-t, 2)-2*t*(1-t))*c.getControlX1()+
	            3*((1-t)*2*t-t*t)*c.getControlX2()+
	            3*Math.pow(t, 2)*c.getEndX(),
	            -3*Math.pow(1-t,2)*c.getStartY()+
	            3*(Math.pow(1-t, 2)-2*t*(1-t))*c.getControlY1()+
	            3*((1-t)*2*t-t*t)*c.getControlY2()+
	            3*Math.pow(t, 2)*c.getEndY());
	    return p;
	}
}

class Arrow extends Group {

     final Line line;

    public Arrow() {
        this(new Line(), new Line(), new Line());
    }

    private static final double arrowLength = 20;
    private static final double arrowWidth = 7;

    private Arrow(Line line, Line arrow1, Line arrow2) {
        super(line, arrow1, arrow2);
        this.line = line;
        InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();

            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);

            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
                double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part ortogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
        };

        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // start/end properties

    public final void setStartX(double value) {
        line.setStartX(value);
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        line.setEndX(value);
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        line.setEndY(value);
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

}