package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author Stefan Prelle
 *
 */
public abstract class ControllerImpl<A> implements PartialController<A> {
	
	protected static Logger logger = LogManager.getLogger(ControllerImpl.class.getPackageName());

	protected static Random random = new Random();
	
	protected SR6CharacterController parent;
	protected List<ToDoElement> todos;
	protected List<Choice> choices;
	
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
	 * @see org.prelle.splittermond.chargen.gen.GenerationProcessingStep#getDecisionsToMake()
	 */
	@Override
	public List<Choice> getChoices() {
		return choices;
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#decide(java.lang.Object, de.rpgframework.genericrpg.data.Choice, de.rpgframework.genericrpg.data.Decision)
	 */
	@Override
	public void decide(A decideFor, Choice choice, Decision decision) {
		logger.warn("TODO: decide "+decision+" from "+decideFor);
		parent.getModel().addDecision(decision);
//		model.setDecision(choice);
//		cached.put(decision.getChoice(), decision);
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	@Override
	public void roll() {
		
	}

}
