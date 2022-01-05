package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.ToDoElement;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.ToDoElement.Severity;
import de.rpgframework.genericrpg.data.ASkillValue;
import de.rpgframework.genericrpg.data.ApplyTo;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.genericrpg.requirements.ValueRequirement;
import de.rpgframework.shadowrun.AShadowrunSkill;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun.chargen.gen.PriorityPointBuySkillController;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6SkillGenerator;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class PointBuySkillGenerator extends CommonSkillController implements SR6SkillGenerator, SR6SkillController, PriorityPointBuySkillController<SR6Skill, SR6SkillValue> {

	private int pointsSkills;
	private int skillsFromCP;
//	private int pointsLangAndKnow;

	//-------------------------------------------------------------------
	public PointBuySkillGenerator(SR6CharacterGenerator parent) {
		super(parent);
		
		// Ensure native language skill is present
		boolean found = false;
//		for (SkillValue val : parent.getModel().getSkillValues(SkillType.LANGUAGE)) {
//			if (val.getPoints()==SkillValue.LANGLEVEL_NATIVE)
//				found=true;
//		}
//		if (!found)
//			parent.getModel().addSkill(new SkillValue(Shadowrun6Core.getSkill("language"), SkillValue.LANGLEVEL_NATIVE));
	}

	@Override
	public String getHumanReadable(Possible result, Locale loc) {
		// TODO Auto-generated method stub
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.SR6SkillController.charctrl.ISkillController#getPointsLeftInKnowledgeAndLanguage()
	 */
	@Override
	public int getPointsLeftInKnowledgeAndLanguage() {
		return 0; //pointsLangAndKnow;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.SR6SkillController.charctrl.ISkillController#getPointsLeftSkills()
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
	 * @see de.rpgframework.shadowrun6.chargen.gen.CommonSkillController#canBeSelected(SR6Skill)
	 */
	@Override
	public Possible canBeSelected(SR6Skill data) {
		if (pointsSkills>0)
			return Possible.TRUE;
//		if (pointsLangAndKnow>0 && (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)) {
//			return Possible.TRUE;			
//		}
		// No points left - maybe with karma?
		int karma = (data.getType()==SkillType.KNOWLEDGE || data.getType()==SkillType.LANGUAGE)?3:5;
		if (model.getKarmaFree()>=karma)
			return Possible.TRUE;
		return new Possible(new ValueRequirement(ShadowrunReference.ATTRIBUTE, "KARMA", karma));
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
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, "Cannot increase skill "+ref+" because current value "+ref.getModifiedValue()+" is already at maximum "+maximum);
			return false;
		}
		
		Collection<ASkillValue> alreadyMaxed = getMaximizedSkills();

		// Only allow to max an attribute, if there isn't one already
		if ((ref.getDistributed()+1)>=(6 + ref.getModifiedValue(ValueType.MAX))) {
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, "Increasing "+ref.getModifyable()+" would reach maximum of "+(6+ref.getModifiedValue(ValueType.MAX))+".  Is already one maxed = "+alreadyMaxed);
			return alreadyMaxed.isEmpty();
		}
		
//		// Only can raise magic skills, if MagicOrResonance allows it
//		if (type==SkillType.MAGIC && !model.getMagicOrResonanceType().usesSpells()) {
//			logger.log(Level.INFO, "Cannot raise "+ref+" because char doesn't use spells");
//			return false;
//		}
		
		

		// Enough points
		int karmaCost = getIncreaseCost(model,(AShadowrunSkill) ref.getModifyable());
		if (type!=SkillType.KNOWLEDGE && type!=SkillType.LANGUAGE) {
			if (getPointsLeftSkills()<=0 && karmaCost>model.getKarmaFree()) {
				logger.log(Level.DEBUG, "Cannot increase because no points ("+getPointsLeftSkills()+") left and not enough karma ("+model.getKarmaFree()+") to raise to "+(ref.getDistributed()+1));
				return false;
			}
		} else {
			// Is language or knowledge
			if (!(getPointsLeftInKnowledgeAndLanguage()>0 || getIncreaseCost(model,(AShadowrunSkill) ref.getModifyable())<=model.getKarmaFree())) {
				logger.log(Level.DEBUG, "Cannot increase because no points ("+getPointsLeftInKnowledgeAndLanguage()+") left and not enough karma ("+model.getKarmaFree()+") to raise");				
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
		
		if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "ENTER process");
		try {
			// Reset values
			pointsSkills   = 12;
			skillsFromCP   = 0;
//			pointsLangAndKnow = 0;
			todos.clear();
			
			for (Modification tmp : previous) {
//				if (tmp instanceof ValueModification) {
//					ValueModification mod = (ValueModification)tmp;
//					if (ApplyTo.POINTS==mod.getApplyTo()) {
//						ShadowrunReference ref = (ShadowrunReference)mod.getReferenceType();
//						switch (ref) {
//						case SKILL:
//							logger.log(Level.DEBUG, "Add "+mod.getValue()+" skill points from "+tmp.getSource());
//							pointsSkills += mod.getValue();
//							break;
//						case SKILL_KNOWLEDGE:
//							logger.log(Level.DEBUG, "Add "+mod.getValue()+" knowledge skill points from "+tmp.getSource());
//							pointsLangAndKnow += mod.getValue();
//							break;
//						default:
//							unprocessed.add(mod);
//						}						
//					}					
//				} else {
					unprocessed.add(tmp);
//				}
			}

			Shadowrun6Character model = parent.getModel();
			SR6PointBuySettings settings = getModel().getCharGenSettings(SR6PointBuySettings.class);
			for (SR6Skill key : Shadowrun6Core.getItemList(SR6Skill.class)) {
				PerSkillPoints per = settings.perSkill.get(key.getId());
				if (per == null) {
					logger.log(Level.WARNING, "No data for " + key);
					continue;
				}
				/* 
				 * Pay skilll points 
				 */
				int required = per.regular;
				if (required>0) {
					if (pointsSkills>0) {
						int pay = Math.min(pointsSkills, required);
						logger.log(Level.DEBUG, "Pay {} free CP for {}", pay, key);
						pointsSkills -= pay;
						required -= pay;
					}
					// If not enough, convert
					if (required>0) {
						int pay = required*2;
						logger.log(Level.DEBUG, "Convert {} CP to {} skill points for {}", pay, required, key);
						settings.characterPoints -= pay;
						settings.cpToSkills += required;
						required -= pay;
					}
				}
			}
			logger.log(Level.DEBUG, "Finish with {} skill points", pointsSkills);
			if (logger.isLoggable(Level.TRACE))
				logger.log(Level.TRACE, settings.toSkillString());

			
			logger.log(Level.INFO, "{} CP converted to {} skills", settings.cpToSkills*2, settings.cpToSkills);
			
			/*
			 * Ensure limits of 20 are in kept
			 */
			if (settings.cpToSkills>20) {
				todos.add(new ToDoElement(Severity.STOPPER, "Too many CP converted to skill points"));
			}
			if (pointsSkills>0) {
				todos.add(new ToDoElement(Severity.STOPPER, pointsSkills+" skill points left to spend"));
			}

		} catch (Exception e) {
			
		} finally {
			if (logger.isLoggable(Level.TRACE)) logger.log(Level.TRACE, "LEAVE process");
		}
		
		return unprocessed;
	}

	@Override
	public int getPointsLeft() {
		return pointsSkills;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityPointBuySkillController#canDecreasePoints(de.rpgframework.shadowrun.AShadowrunSkill)
	 */
	@Override
	public Possible canDecreasePoints(SR6Skill key) {
		PerSkillPoints per = parent.getModel().getCharGenSettings(SR6PointBuySettings.class).perSkill.get(key);
		return (per.regular>0)?Possible.TRUE:Possible.FALSE;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityPointBuySkillController#canIncreasePoints(de.rpgframework.shadowrun.AShadowrunSkill)
	 */
	@Override
	public Possible canIncreasePoints(SR6Skill key) {
		// Only 20 attribute points may be generated from CP
		if (skillsFromCP >= 20)
			return new Possible("skill.points.already20CP");
		// Every conversion costs 2 CP
		if (parent.getModel().getCharGenSettings(SR6PointBuySettings.class).characterPoints < 2)
			return new Possible("skill.points.notEnoughCP");

		return Possible.TRUE;
	}

	@Override
	public boolean increasePoints(SR6Skill key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decreasePoints(SR6Skill key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Possible canDecreaseKarma(SR6Skill key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Possible canIncreaseKarma(SR6Skill key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean increaseKarma(SR6Skill key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decreaseKarma(SR6Skill key) {
		// TODO Auto-generated method stub
		return false;
	}

}
