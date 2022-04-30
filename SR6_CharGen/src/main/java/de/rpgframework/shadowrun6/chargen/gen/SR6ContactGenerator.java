package de.rpgframework.shadowrun6.chargen.gen;

import java.util.List;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.chargen.charctrl.IContactController;
import de.rpgframework.shadowrun6.chargen.charctrl.ControllerImpl;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
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

	@Override
	public Contact createContact() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeContact(Contact con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canIncreaseRating(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean increaseRating(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDecreaseRating(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decreaseRating(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canIncreaseLoyalty(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean increaseLoyalty(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDecreaseLoyalty(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decreaseLoyalty(Contact con) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return null;
	}

}
