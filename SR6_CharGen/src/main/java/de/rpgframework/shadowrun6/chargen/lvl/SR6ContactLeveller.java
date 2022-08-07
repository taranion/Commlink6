package de.rpgframework.shadowrun6.chargen.lvl;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6ContactController;

/**
 * @author prelle
 *
 */
public class SR6ContactLeveller extends ControllerImpl<Contact> implements SR6ContactController {

	//-------------------------------------------------------------------
	public SR6ContactLeveller(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#getPointsLeft()
	 */
	@Override
	public int getPointsLeft() {
		return 0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#canCreateContact()
	 */
	@Override
	public Possible canCreateContact() {
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#createContact()
	 */
	@Override
	public OperationResult<Contact> createContact() {
		Possible poss = canCreateContact();
		if (!poss.get()) {
			logger.log(Level.ERROR, "Trying to create a contact, which is not allowed");
			return new OperationResult<>(poss);
		}
		
		Contact contact = new Contact();
		getModel().addContact(contact);
		logger.log(Level.INFO, "Added contact");
		
		parent.runProcessors();
		
		return new OperationResult<Contact>(contact);
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
	public Possible canIncreaseRating(Contact con) {
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#increaseRating(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean increaseRating(Contact con) {
		Possible poss = canIncreaseRating(con);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Tried to increase contact rating although not possible: "+poss.getMostSevere());
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
	public Possible canDecreaseRating(Contact con) {
		if (con.getRating()<=1)
			return new Possible(IRejectReasons.IMPOSS_MIN_LEVEL_REACHED);
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#decreaseRating(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean decreaseRating(Contact con) {
		if (!canDecreaseRating(con).get()) {
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
	public Possible canIncreaseLoyalty(Contact con) {
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#increaseLoyalty(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean increaseLoyalty(Contact con) {
		Possible poss = canIncreaseLoyalty(con);
		if (!poss.get()) {
			logger.log(Level.ERROR, "Tried to increase contact loyalty although not possible: "+poss.getMostSevere());
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
	public Possible canDecreaseLoyalty(Contact con) {
		if (con.getLoyalty()<=1)
			return new Possible(IRejectReasons.IMPOSS_MIN_LEVEL_REACHED);
		return Possible.TRUE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.charctrl.IContactController#decreaseLoyalty(de.rpgframework.shadowrun.Contact)
	 */
	@Override
	public boolean decreaseLoyalty(Contact con) {
		if (!canDecreaseLoyalty(con).get()) {
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
			
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");			
		}
		return unprocessed;
	}

}
