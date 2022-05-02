package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6ContactController;

/**
 * @author prelle
 *
 */
public class SR6ContactGenerator extends ControllerImpl<Contact> implements SR6ContactController {
	
	private int pointsLeft;

	protected SR6ContactGenerator(SR6CharacterController parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return pointsLeft;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#canCreateContact()
	 */
	@Override
	public boolean canCreateContact() {
		return pointsLeft>=2;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#createContact()
	 */
	@Override
	public Contact createContact() {
		if (!canCreateContact()) {
			logger.log(Level.ERROR, "Trying to create a contact, which is not allowed");
			return null;
		}
		
		Contact contact = new Contact();
		getModel().addContact(contact);
		logger.log(Level.INFO, "Added contact");
		
		parent.runProcessors();
		
		return contact;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#removeContact(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public void removeContact(Contact con) {
		boolean success = getModel().removeContact(con);
		logger.log(Level.INFO, "Removed contact");
		if (success) {
			parent.runProcessors();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#canIncreaseRating(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean canIncreaseRating(Contact con) {
		int max = Math.min(8, getModel().getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue());
		if (con.getRating()>=max)
			return false;
		return pointsLeft>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#increaseRating(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean increaseRating(Contact con) {
		if (!canIncreaseRating(con)) {
			logger.log(Level.ERROR, "Tried to increase contact rating although not possible");
			return false;
		}
		
		con.setRating(con.getRating()+1);
		logger.log(Level.INFO, "Increased contact rating of "+con.getName());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#canDecreaseRating(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean canDecreaseRating(Contact con) {
		return con.getRating()>1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#decreaseRating(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean decreaseRating(Contact con) {
		if (!canDecreaseRating(con)) {
			logger.log(Level.ERROR, "Tried to decrease contact rating although not possible");
			return false;
		}
		
		con.setRating(con.getRating()-1);
		logger.log(Level.INFO, "Decreased contact rating of "+con.getName());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#canIncreaseLoyalty(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean canIncreaseLoyalty(Contact con) {
		int max = Math.min(8, getModel().getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue());
		if (con.getLoyalty()>=max)
			return false;
		return pointsLeft>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#increaseLoyalty(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean increaseLoyalty(Contact con) {
		if (!canIncreaseLoyalty(con)) {
			logger.log(Level.ERROR, "Tried to increase contact loyalty although not possible");
			return false;
		}
		
		con.setLoyalty(con.getLoyalty()+1);
		logger.log(Level.INFO, "Increased contact loyalty of "+con.getName());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#canDecreaseLoyalty(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean canDecreaseLoyalty(Contact con) {
		return con.getLoyalty()>1;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#decreaseLoyalty(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean decreaseLoyalty(Contact con) {
		if (!canDecreaseLoyalty(con)) {
			logger.log(Level.ERROR, "Tried to decrease contact loyalty although not possible");
			return false;
		}
		
		con.setLoyalty(con.getLoyalty()-1);
		logger.log(Level.INFO, "Decreased contact loyalty of "+con.getName());
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		List<Modification> unprocessed = new ArrayList<>();
		try {
			todos.clear();

			for (Modification tmp : previous) {
//				if (tmp.getReferenceType()==ShadowrunReference.CONTACT) {
//				} else
					unprocessed.add(tmp);
			}
			
			// Calculate points left
			pointsLeft = getModel().getAttribute(ShadowrunAttribute.CHARISMA).getModifiedValue() * 6;
			
			// Now pay contacts
			for (Contact tmp : getModel().getContacts()) {
				int cost = tmp.getLoyalty() + tmp.getRating();
				logger.log(Level.INFO, "Pay {0} contact points {1} (R={2}/L={3})", cost, tmp.getName(),tmp.getRating(), tmp.getLoyalty());
				pointsLeft -= cost;
			}
			
			if (pointsLeft>0) {
				todos.add(new ToDoElement(Severity.WARNING, SR6CharacterGenerator.RES, IRejectReasons.TODO_CONTACT_POINTS_LEFT, pointsLeft));
			}
			
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");			
		}
		return unprocessed;
	}

}
