package de.rpgframework.shadowrun6.chargen.jfx;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.shadowrun6.Spell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author stefa
 *
 */
public class SpellDescriptionPageController {
	
	private final static Logger logger = LogManager.getLogger(SpellDescriptionPageController.class);

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label type;

    @FXML
    private Label difficulty;

    @FXML
    private Label cost;

    @FXML
    private Label castduration;

    @FXML
    private Label castrange;

    @FXML
    private Label duration;

    @FXML
    private Label description;

    @FXML
    private Label enhanced;

    @FXML
    private Label enhanceDescr;

	//-------------------------------------------------------------------
	/**
	 */
	public SpellDescriptionPageController() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	@FXML
    void initialize() {
        assert type != null : "fx:id=\"type\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert difficulty != null : "fx:id=\"difficulty\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert cost != null : "fx:id=\"cost\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert castduration != null : "fx:id=\"castduration\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert castrange != null : "fx:id=\"castrange\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert duration != null : "fx:id=\"duration\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert description != null : "fx:id=\"description\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert enhanced != null : "fx:id=\"enhanced\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        assert enhanceDescr != null : "fx:id=\"enhanceDescr\" was not injected: check your FXML file 'SpellDescription.fxml'.";
        
        description.setStyle("-fx-max-width: 30em");

    }

	//-------------------------------------------------------------------
	public void setData(Spell spell) {
		// Spell Type
		StringBuffer buf = new StringBuffer();
//		for (Iterator<SpellType> it=spell.getTypes().iterator(); it.hasNext(); ) {
//			buf.append(it.next().getName());
//			if (it.hasNext())
//				buf.append(", ");
//		}
//		type.setText(buf.toString());
//
//		// Difficulty
//		difficulty.setText(String.valueOf(spell.getDifficultyString()));		
//		// Cost
//		cost.setText(SplitterTools.getFocusString(spell.getCost()));
//		// Cast duration
//		castduration.setText(spell.getCastDurationString());
//		// Range
//		castrange.setText(spell.getCastRangeString());
//		// Spell Duration
//		duration.setText(spell.getSpellDurationString());
//		// Effect
//		description.setText(spell.getDescription());
//		enhanced.setText(spell.getEnhancementString());
//		enhanceDescr.setText(spell.getEnhancementDescription());

	}

}
