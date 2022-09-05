package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.javafx.Wizard;

import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.pane.ContactDetailPane;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageContacts;

/**
 * @author prelle
 *
 */
public class SR6WizardPageContacts extends WizardPageContacts {
	
	private final static Logger logger = System.getLogger(SR6WizardPageContacts.class.getPackageName());

	//-------------------------------------------------------------------
	public SR6WizardPageContacts(Wizard wizard, IShadowrunCharacterController<?, ?, ?,?> charGen) {
		super(wizard, charGen);
	}
	
	//-------------------------------------------------------------------
	protected ContactDetailPane getRuleSpecificNode() {
		return new ContactDetailPane();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageContacts#addClicked()
	 */
	@Override
	public void addClicked() {
		// TODO Auto-generated method stub
		logger.log(Level.WARNING, "ToDo: add");
		
		Contact toAdd = new Contact("Hans", ContactType.GOVERNMENT, "Agent", 3, 2);
		selection.getItems().add(toAdd);
	}

}
