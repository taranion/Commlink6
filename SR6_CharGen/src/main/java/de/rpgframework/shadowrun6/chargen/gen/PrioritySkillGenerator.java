package de.rpgframework.shadowrun6.chargen.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.ASkillValue;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.data.ISkill;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.AShadowrunSkill;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PrioritySkillGenerator extends CommonSkillController implements SR6SkillGenerator, SR6SkillController {

	private int pointsSkills;
	private int pointsLangAndKnow;
	private List<ToDoElement> normalToDos;
	private List<ToDoElement> knowledgeToDos;

	//-------------------------------------------------------------------
	public PrioritySkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
		normalToDos    = new ArrayList<>();
		knowledgeToDos = new ArrayList<>();
		
		// Ensure native language skill is present
		boolean found = false;
//		for (SkillValue val : parent.getModel().getSkillValues(SkillType.LANGUAGE)) {
//			if (val.getPoints()==SkillValue.LANGLEVEL_NATIVE)
//				found=true;
//		}
//		if (!found)
//			parent.getModel().addSkill(new SkillValue(Shadowrun6Core.getSkill("language"), SkillValue.LANGLEVEL_NATIVE));
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.SR6SkillController.charctrl.SkillController#getPointsLeftInKnowledgeAndLanguage()
	 */
	@Override
	public int getPointsLeftInKnowledgeAndLanguage() {
		return pointsLangAndKnow;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.SR6SkillController.charctrl.SkillController#getPointsLeftSkills()
	 */
	@Override
	public int getPointsLeftSkills() {
		return pointsSkills;
	}

	//-------------------------------------------------------------------
	private List<ASkillValue> getMaximizedSkills() {
		List<ASkillValue> maxed = new ArrayList<ASkillValue>();
		for (ASkillValue val : model.getSkillValues()) {
			if (val.getDistributed() >= (6+val.getModifiedValue(ValueType.MAX)))
				maxed.add(val);
		}
		return maxed;
	}

	//-------------------------------------------------------------------
	private int getIncreaseCost(ShadowrunCharacter model, AShadowrunSkill key) {
		ASkillValue sVal = model.getSkillValue(key);

		if (key.getType()==SkillType.KNOWLEDGE || key.getType()==SkillType.LANGUAGE) 
			return 3;
		
		int newVal = (sVal==null)?1:(sVal.getModifiedValue()+1);
		return newVal*5;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#canBeIncreased(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean canBeIncreased(SR6SkillValue ref) {
		SkillType type = ((SR6Skill)ref.getModifyable()).getType();
		// Cannot increase knowledge skills
		if (type==SkillType.KNOWLEDGE)
			return false;
//		// Is automatically added
//		if (model.isAutoSkill(ref))
//			return false;
		
		// Maximum not reached yet
		int maximum = 6  + ref.getModifiedValue(ValueType.MAX);
		if (type==SkillType.LANGUAGE) {
			// Language skills have different maximums
			maximum = SR6SkillValue.LANGLEVEL_EXPERT;
		}
		if (ref.getDistributed()>=maximum) {
			if (logger.isTraceEnabled())
				logger.trace("Cannot increase skill "+ref+" because current value "+ref.getModifiedValue()+" is already at maximum "+maximum);
			return false;
		}
		
		Collection<ASkillValue> alreadyMaxed = getMaximizedSkills();

		// Only allow to max an attribute, if there isn't one already
		if ((ref.getDistributed()+1)>=(6 + ref.getModifiedValue(ValueType.MAX))) {
			if (logger.isTraceEnabled())
				logger.trace("Increasing "+ref.getModifyable()+" would reach maximum of "+(6+ref.getModifiedValue(ValueType.MAX))+".  Is already one maxed = "+alreadyMaxed);
			return alreadyMaxed.isEmpty();
		}
		
//		// Only can raise magic skills, if MagicOrResonance allows it
//		if (type==SkillType.MAGIC && !model.getMagicOrResonanceType().usesSpells()) {
//			logger.info("Cannot raise "+ref+" because char doesn't use spells");
//			return false;
//		}
		
		

		// Enough points
		int karmaCost = getIncreaseCost(model,(AShadowrunSkill) ref.getModifyable());
		if (type!=SkillType.KNOWLEDGE && type!=SkillType.LANGUAGE) {
			if (getPointsLeftSkills()<=0 && karmaCost>model.getKarmaFree()) {
				logger.debug("Cannot increase because no points ("+getPointsLeftSkills()+") left and not enough karma ("+model.getKarmaFree()+") to raise to "+(ref.getDistributed()+1));
				return false;
			}
		} else {
			// Is language or knowledge
			if (!(getPointsLeftInKnowledgeAndLanguage()>0 || getIncreaseCost(model,(AShadowrunSkill) ref.getModifyable())<=model.getKarmaFree())) {
				logger.debug("Cannot increase because no points ("+getPointsLeftInKnowledgeAndLanguage()+") left and not enough karma ("+model.getKarmaFree()+") to raise");				
			}

			return getPointsLeftInKnowledgeAndLanguage()>0 || karmaCost<=model.getKarmaFree();
		}

		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.NumericalValueController#increase(de.rpgframework.genericrpg.NumericalValue)
	 */
	@Override
	public boolean increase(SR6SkillValue ref) {
		boolean hasBeenIncreased = super.increase(ref);
		if (!hasBeenIncreased)
			return false;
		
		parent.runProcessors();
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> previous) {
		List<Modification> unprocessed = new ArrayList<>();
		
		logger.trace("START: Skills");
		try {
			// Reset values
			pointsSkills   = 0;
			pointsLangAndKnow = 0;
			normalToDos.clear();
			knowledgeToDos.clear();
			
			for (Modification tmp : previous) {
				if (tmp instanceof ValueModification) {
					ValueModification mod = (ValueModification)tmp;
					if (ApplyTo.POINTS==mod.getApplyTo()) {
						ShadowrunReference ref = (ShadowrunReference)mod.getReferenceType();
						switch (ref) {
						case SKILL:
							logger.debug("Add "+mod.getValue()+" skill points from "+tmp.getSource());
							pointsSkills += mod.getValue();
							break;
						case SKILL_KNOWLEDGE:
							logger.debug("Add "+mod.getValue()+" knowledge skill points from "+tmp.getSource());
							pointsLangAndKnow += mod.getValue();
							break;
						default:
							unprocessed.add(mod);
						}						
					}					
				} else {
					unprocessed.add(tmp);
				}
			}
		} catch (Exception e) {
			
		} finally {
			logger.trace("STOP : Skills");
		}
		
		return unprocessed;
	}

}
