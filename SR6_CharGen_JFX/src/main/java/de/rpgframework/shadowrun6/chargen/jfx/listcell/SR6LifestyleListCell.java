package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.Iterator;
import java.util.Locale;

import de.rpgframework.shadowrun.Lifestyle;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.jfx.listcell.ALifestyleListCell;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6LifestyleListCell extends ALifestyleListCell<SR6Lifestyle> {

	//-------------------------------------------------------------------
	public SR6LifestyleListCell(IShadowrunCharacterControllerProvider<IShadowrunCharacterController> ctrlProv) {
		super(ctrlProv);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.listcell.ALifestyleListCell#addDetails(de.rpgframework.shadowrun.Lifestyle, javafx.scene.layout.FlowPane)
	 */
	protected void addDetails(SR6Lifestyle data, FlowPane flow) {
		super.addDetails(data, flow);
		
//		for (Quality qual : data.getComforts())
	}
	
}
