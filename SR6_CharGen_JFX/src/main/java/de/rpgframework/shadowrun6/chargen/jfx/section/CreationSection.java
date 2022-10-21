package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.Section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class CreationSection extends Section {

	private final static Logger logger = System.getLogger(CreationSection.class.getPackageName());
	
	private final static ResourceBundle RES = PropertyResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	protected SR6CharacterController control;
	protected Shadowrun6Character model;

	private Label lbNotes;
	
	//-------------------------------------------------------------------
	public CreationSection() {
		super(ResourceI18N.get(RES, "section.creation.title"), null);
		initComponents();
		setContent(lbNotes);
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		lbNotes = new Label("?");
		lbNotes.setWrapText(true);
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		logger.log(Level.DEBUG, "updateController");
		this.control = ctrl;
		model = ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	public void refresh() {
		if (model==null) return;
		
		List<String> lines = CharacterGeneratorRegistry.getGenerationInfoStrings(model, Locale.getDefault());
		lbNotes.setText( String.join("\n", lines));
	}	

}
