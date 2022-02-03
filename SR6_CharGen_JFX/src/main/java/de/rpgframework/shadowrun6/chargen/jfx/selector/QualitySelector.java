package de.rpgframework.shadowrun6.chargen.jfx.selector;

import org.prelle.javafx.ManagedDialog;

import de.rpgframework.shadowrun.Quality;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * @author prelle
 *
 */
public class QualitySelector extends ManagedDialog {
	
	public enum PosOrNeg {
		ALL,
		POSITIVE,
		NEGATIVE
	}
	
	
	private ChoiceBox<PosOrNeg> cbPosOrNeg;
	private ChoiceBox<Quality.QualityType> cbType;
	private ChoiceBox<Quality.QualityCategory> cbSubType;
	private TextField search;
	
	
}
