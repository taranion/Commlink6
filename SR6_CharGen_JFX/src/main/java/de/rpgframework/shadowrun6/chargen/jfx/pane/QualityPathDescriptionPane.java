package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;

import de.rpgframework.jfx.ADescriptionPane;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathStep;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class QualityPathDescriptionPane extends ADescriptionPane<QualityPath> {
	
	private GenericDescriptionVBox descr;
	private GenericDescriptionVBox stepDescr;
	private VisualQualityPathPane visual;

	//-------------------------------------------------------------------
	/**
	 */
	public QualityPathDescriptionPane() {
		getStyleClass().add("description-pane");
		visual = new VisualQualityPathPane();
		descr = new GenericDescriptionVBox(
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
		stepDescr = new GenericDescriptionVBox(
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault()));
		
		HBox inner = new HBox(20);		
		inner.getChildren().add(visual);
		inner.getChildren().add(new VBox(10,descr,stepDescr));
		
		visual.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> setStepData(n));
		getChildren().add(inner);
	}

	//-------------------------------------------------------------------
	public void setData(QualityPath value) {
		descr.setData(value);
		visual.setData(value);
		stepDescr.setVisible(false);
	}

	//-------------------------------------------------------------------
	public void setStepData(QualityPathStep value) {
		stepDescr.setData(value);
		stepDescr.setVisible(value!=null);
	}

}
