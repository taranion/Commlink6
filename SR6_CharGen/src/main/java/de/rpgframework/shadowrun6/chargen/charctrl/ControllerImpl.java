package de.rpgframework.shadowrun6.chargen.charctrl;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.ModificationChoice;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author Stefan Prelle
 *
 */
public abstract class ControllerImpl<A> implements PartialController<A> {

	protected static Logger logger = System.getLogger(ControllerImpl.class.getPackageName());

	protected static Random random = new Random();

	protected SR6CharacterController parent;
	protected List<ToDoElement> todos;
	protected List<UUID> choices;

	//-------------------------------------------------------------------
	protected ControllerImpl(SR6CharacterController parent) {
		this.parent = parent;
		this.todos  = new ArrayList<>();
		this.choices= new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#getCharacterController()
	 */
	@Override
	public CharacterController<ShadowrunAttribute,Shadowrun6Character> getCharacterController() {
		return parent;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#getModel()
	 */
	@Override
	public Shadowrun6Character getModel() {
		return parent.getModel();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.Controller#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return todos;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#getChoiceUUIDs()
	 */
	@Override
	public List<UUID> getChoiceUUIDs() {
		return choices;
	}

	//-------------------------------------------------------------------
	public Choice getAsChoice(ComplexDataItem value, UUID uuid) {
		return value.getChoice(uuid);
	}

	//-------------------------------------------------------------------
	public ModificationChoice getAsModificationChoice(ComplexDataItem value, UUID uuid) {
		return value.getModificationChoice(uuid);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#decide(java.lang.Object, de.rpgframework.genericrpg.data.Choice, de.rpgframework.genericrpg.data.Decision)
	 */
	@Override
	public void decide(A decideFor, UUID choice, Decision decision) {
		logger.log(Level.WARNING, "TODO: decide "+decision+" from "+decideFor);
		parent.getModel().addDecision(decision);
//		model.setDecision(choice);
//		cached.put(decision.getChoice(), decision);
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	@Override
	public void roll() {
		logger.log(Level.ERROR, "roll() not implemented");
	}

	//-------------------------------------------------------------------
	protected void updateChoices(ComplexDataItem value) {
		choices.clear();
		choices.addAll(value.getChoices().stream().map(c -> c.getUUID()).toList());
		choices.addAll(value.getOutgoingModifications().stream()
			.filter(m -> m instanceof ModificationChoice)
			.map(mc -> ((ModificationChoice)mc).getUUID()).toList());
	}

}
