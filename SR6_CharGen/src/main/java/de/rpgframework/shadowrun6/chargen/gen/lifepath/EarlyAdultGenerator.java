package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.rpgframework.character.RuleSpecificCharacterObject;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.PartialController;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.IAttribute;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6RejectReasons;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.ChildhoodGenerator.SimpleSkillController;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 *
 */
public class EarlyAdultGenerator implements PartialController<Quality> {

	protected static Logger logger = System.getLogger(EarlyAdultGenerator.class.getPackageName());

	private SR6LifepathCharacterGenerator parent;
	protected List<ToDoElement> todos;
	private List<SR6Skill> availableSkills;

	//-------------------------------------------------------------------
	public EarlyAdultGenerator(SR6LifepathCharacterGenerator parent) {
		this.parent = parent;
		this.todos = new ArrayList<>();

		availableSkills = List.of("athletics","close_combat","con","electronics","influence","outdoors","perception","stealth")
				.stream().map(key -> Shadowrun6Core.getSkill(key)).toList();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.PartialController#getToDos()
	 */
	@Override
	public List<ToDoElement> getToDos() {
		return todos;
	}

	//-------------------------------------------------------------------
	public List<SR6Skill> getAvailableSkills() {
		return Shadowrun6Core.getItemList(SR6Skill.class);
	}

//	//-------------------------------------------------------------------
//	public List<Quality> getAvailable1() {
//		return Shadowrun6Core.getItemList(SR6Quality.class).stream()
//				.filter(p -> parent.showDataItem(p))
//				.filter(p -> p.isFreeSelectable())
//				.collect(Collectors.toList());
//	}
//
//	//-------------------------------------------------------------------
//	public List<Quality> getAvailable2() {
//		if (parent.getSettings().getBornQuality1()==null) return List.of();
//		return Shadowrun6Core.getItemList(SR6Quality.class).stream()
//				.filter(p -> parent.showDataItem(p))
//				.filter(p -> p.isFreeSelectable())
//				.filter(p -> !parent.getModel().hasQuality(p.getId()))
//				// If you choose two qualities, one must be a positive quality and the other must be a negative quality
//				.filter(p -> p.isPositive() == !parent.getSettings().getBornQuality1().isPositive())
//				.collect(Collectors.toList());
//	}
//
//	//-------------------------------------------------------------------
//	public Possible canBeSelected1(Quality value, Decision... decisions) {
//		if (getAvailable1().contains(value)) {
//			return Shadowrun6Tools.checkDecisionsAndRequirements(parent.getModel(), value, decisions);
//		}
//		return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_NOT_AVAILABLE, value.getName());
//	}
//
//	//-------------------------------------------------------------------
//	public Possible canBeSelected2(Quality value, Decision... decisions) {
//		if (getAvailable2().contains(value)) {
//			return Shadowrun6Tools.checkDecisionsAndRequirements(parent.getModel(), value, decisions);
//		}
//		return new Possible(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.IMPOSS_NOT_AVAILABLE, value.getName());
//	}
//
//	//-------------------------------------------------------------------
//	public OperationResult<QualityValue> selectQuality1(Quality quality, Decision... decisions) {
//		Possible poss = canBeSelected1(quality);
//		if (!poss.get()) {
//			logger.log(Level.WARNING, "Trying to select({0}) but not possible because {1}", quality, poss.getMostSevere());
//			return new OperationResult<QualityValue>(poss);
//		}
//
//		QualityValue ret = new QualityValue(quality, 1);
//		ret.setInjectedBy(this);
//		for (Decision dec : decisions) ret.addDecision(dec);
//		parent.getSettings().setBornQuality1(quality);
//
//		if (parent.getModel().hasQuality(quality.getId())) {
//			ret = parent.getModel().getQuality(quality.getId());
//			ret.setDistributed(1);
//		} else {
//			parent.getModel().addQuality(ret);
//		}
//		logger.log(Level.INFO, "Select primary quality {0}\n", quality.getId());
//
//		parent.runProcessors();
//		return new OperationResult<QualityValue>(ret);
//	}
//
//	//-------------------------------------------------------------------
//	public OperationResult<QualityValue> selectQuality2(Quality quality, Decision... decisions) {
//		Possible poss = canBeSelected2(quality);
//		if (!poss.get()) {
//			logger.log(Level.WARNING, "Trying to select({0}) but not possible because {1}", quality, poss.getMostSevere());
//			return new OperationResult<QualityValue>(poss);
//		}
//
//		QualityValue ret = new QualityValue(quality, 0);
//		for (Decision dec : decisions) ret.addDecision(dec);
//
//		parent.getModel().addQuality(ret);
//		parent.getSettings().setBornQuality2(quality);
//		logger.log(Level.INFO, "Select secondary quality {0}", quality.getId());
//
//		parent.runProcessors();
//		return new OperationResult<QualityValue>(ret);
//	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		todos.clear();

		// Skills
		if (parent.getSettings().getEarlyAdultSkill()==null) {
			todos.add( new ToDoElement(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.TODO_EARLY_SKILL_MISSING));
		} else {
			ValueModification mod = new ValueModification(ShadowrunReference.SKILL, parent.getSettings().getEarlyAdultSkill().getId(), 4, EarlyAdultGenerator.class, ValueType.NATURAL);
			unprocessed.add(mod);
		}
		if (parent.getSettings().getEarlyAdultAttribute()==null) {
			todos.add( new ToDoElement(Severity.STOPPER, SR6RejectReasons.RES, SR6RejectReasons.TODO_EARLY_ATTRIBUTE_MISSING));
		} else {
			ValueModification mod = new ValueModification(ShadowrunReference.ATTRIBUTE, parent.getSettings().getEarlyAdultAttribute().name(), 5, EarlyAdultGenerator.class, ValueType.NATURAL);
			unprocessed.add(mod);
		}

		int startNuyen = parent.getRuleController().getRuleValueAsInteger(Shadowrun6Rules.CHARGEN_LIFEPATH_START_NUYEN);
		unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.NUYEN.name(), startNuyen));
		unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.CONTACT_POINTS.name(), 4));
		return unprocessed;
	}

	@Override
	public <A extends IAttribute, M extends RuleSpecificCharacterObject<A, ?, ?, ?>> CharacterController<A, M> getCharacterController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shadowrun6Character getModel() {
		return parent.getModel();
	}

	@Override
	public void roll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<UUID> getChoiceUUIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decide(Quality decideFor, UUID choice, Decision decision) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	public void selectChildhoodArea(String value) {
		parent.getSettings().setChildhoodArea(value);
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	public OneOrTwoQualityController getQualityController() {
		return new OneOrTwoQualityController(parent,
				() -> parent.getSettings().getChildhoodQuality1(),
				 q -> parent.getSettings().setChildhoodQuality1((SR6Quality) q),
				() -> parent.getSettings().getChildhoodQuality2(),
				 q -> parent.getSettings().setChildhoodQuality2((SR6Quality) q)
				 );
	}

	public SimpleSkillController getSkillController() {
		return new SimpleSkillController() {

			@Override
			public boolean canBeSelected(SR6Skill skill) {
				return true;
			}

			@Override
			public boolean isSelected(SR6Skill skill) {
				return parent.getSettings().getEarlyAdultSkill()==skill;
			}

			@Override
			public void select(SR6Skill skill) {
				parent.getSettings().setEarlyAdultSkill(skill);
				logger.log(Level.WARNING, "Selected early adult skill {0}", skill);
				parent.runProcessors();
			}

			@Override
			public void deselect(SR6Skill skill) {
				parent.getSettings().setEarlyAdultSkill(null);
				logger.log(Level.INFO, "Deselected early adult skill {0}", skill);
				parent.runProcessors();
			}
		};
	}

	//-------------------------------------------------------------------
	public List<ShadowrunAttribute> getAvailableAttributes() {
		return List.of(ShadowrunAttribute.primaryValues());
	}

	//-------------------------------------------------------------------
	public void selectAttribute(ShadowrunAttribute attribute) {
		parent.getSettings().setEarlyAdultAttribute(attribute);
		parent.runProcessors();
	}

	//-------------------------------------------------------------------
	public void deselectAttribute(ShadowrunAttribute attribute) {
		if (parent.getSettings().getEarlyAdultAttribute()==attribute) {
			parent.getSettings().setEarlyAdultAttribute(null);
			parent.runProcessors();
		}
	}
}


