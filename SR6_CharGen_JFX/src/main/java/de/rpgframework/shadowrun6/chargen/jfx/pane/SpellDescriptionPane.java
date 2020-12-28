package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun6.Spell;
import de.rpgframework.shadowrun6.SpellFeatureReference;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author stefa
 *
 */
public class SpellDescriptionPane extends VBox {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(SpellDescriptionPane.class.getName());
	
	public SpellDescriptionPane(Spell spell) {
		getStyleClass().add("description-pane");
		StringBuffer buf;
		
		// Features
		Label lbFeatures  = new Label();
		lbFeatures.setWrapText(true);
		lbFeatures.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
		buf = new StringBuffer();
		List<String> feats = new ArrayList<>();
		for (SpellFeatureReference ref : spell.getFeatures()) {
			feats.add(ref.getFeature().getName());
		}
		if (!feats.isEmpty()) {
			buf.append("( "+String.join(", ", feats)+" )");
		}
		lbFeatures.setText(buf.toString());
		
		// Spell Table
		Label hdRange = new Label(ResourceI18N.get(UI,"label.spell.range"));
		Label lbRange = new Label(spell.getRange().getShortName());
		Label hdType  = new Label(ResourceI18N.get(UI,"label.spell.type"));
		Label lbType  = new Label(spell.getType().getShortName());
		Label hdDurat = new Label(ResourceI18N.get(UI,"label.spell.duration"));
		Label lbDurat = new Label(spell.getDuration().getShortName());
		Label hdDrain = new Label(ResourceI18N.get(UI,"label.spell.drain"));
		Label lbDrain = new Label(spell.getDuration().getShortName());
		hdRange.setMaxWidth(Double.MAX_VALUE);
		hdType.setMaxWidth(Double.MAX_VALUE);
		hdDurat.setMaxWidth(Double.MAX_VALUE);
		hdDrain.setMaxWidth(Double.MAX_VALUE);
		
		hdRange.getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD);
		hdType.getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD);
		hdDurat.getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD);
		hdDrain.getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD);
		
		lbRange.getStyleClass().add(JavaFXConstants.STYLE_TABLE_DATA);
		lbType.getStyleClass().add(JavaFXConstants.STYLE_TABLE_DATA);
		lbDurat.getStyleClass().add(JavaFXConstants.STYLE_TABLE_DATA);
		lbDrain.getStyleClass().add(JavaFXConstants.STYLE_TABLE_DATA);
		
		GridPane grid = new GridPane();
		grid.add(hdRange, 0, 0);
		grid.add(lbRange, 0, 1);
		grid.add(hdType , 1, 0);
		grid.add(lbType , 1, 1);
		grid.add(hdDurat, 2, 0);
		grid.add(lbDurat, 2, 1);
		grid.add(hdDrain, 3, 0);
		grid.add(lbDrain, 3, 1);
		for (int i=0; i<4; i++) {
		ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(25);
	    col1.setHalignment(HPos.CENTER);
	    col1.setFillWidth(true);
	    grid.getColumnConstraints().addAll(col1);
		}
		grid.setStyle("-fx-background-color: #e9e9e2;");
		grid.setMaxWidth(Double.MAX_VALUE);
		
		// Effect
		Label lblDescr = new Label(spell.getDescription());
		lblDescr.setWrapText(true);

		
		getChildren().addAll(
				lbFeatures, 
				grid,
				lblDescr);
		VBox.setMargin(lblDescr, new Insets(20,0,0,0));
		VBox.setMargin(    grid, new Insets(20,0,0,0));
	}

}
