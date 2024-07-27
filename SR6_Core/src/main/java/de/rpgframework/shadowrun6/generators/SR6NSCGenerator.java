package de.rpgframework.shadowrun6.generators;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rpgframework.classification.Classification;
import de.rpgframework.classification.ClassificationType;
import de.rpgframework.classification.GenericClassificationType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.random.Actor;
import de.rpgframework.random.DataType;
import de.rpgframework.random.VariableHolderNode;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.NPCType;
import de.rpgframework.shadowrun.generators.RunVariable;
import de.rpgframework.shadowrun.generators.ShadowrunNSCGenerator;
import de.rpgframework.shadowrun.generators.ShadowrunTaxonomy;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class SR6NSCGenerator extends ShadowrunNSCGenerator {

	private final static Logger logger = System.getLogger(SR6NSCGenerator.class.getPackageName());

	//-------------------------------------------------------------------
	/**
	 */
	public SR6NSCGenerator() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.RandomGenerator#getRequiredVariables()
	 */
	@Override
	public Collection<ClassificationType> getRequiredVariables() {
		return List.of(GenericClassificationType.RULES);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.RandomGenerator#matchesFilter(de.rpgframework.classification.Classification)
	 */
	@Override
	public boolean matchesFilter(Classification<?> filter) {
		if (filter==RoleplayingSystem.SHADOWRUN6) return true;
		return super.matchesFilter(filter);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.RandomGenerator#understandsHint(de.rpgframework.classification.Classification)
	 */
	@Override
	public boolean understandsHint(ClassificationType filter) {
		if (filter==GenericClassificationType.RULES) return true;
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.RandomGenerator#generate(de.rpgframework.classification.Classification[])
	 */
	@Override
	public Object generate(VariableHolderNode context) {
		logger.log(Level.INFO, "ENTER: createNSC()");
		logger.log(Level.INFO, "variables = "+context.getGenericVariables());
		logger.log(Level.INFO, "classifier = "+context.getHints());
		try {
			Actor actor =  (Actor)super.generate(context);
			Integer minRat = context.getVariable(RunVariable.PROF_EXPECTED_MIN);
			Integer maxRat = context.getVariable(RunVariable.PROF_EXPECTED_MAX);
			if (maxRat==0) maxRat=12;
			NPCType npcType = (NPCType) getHint(ShadowrunTaxonomy.NPCTYPE, context.getHints());
			ContactType conType = (ContactType) getHint(ShadowrunTaxonomy.CONTACT, context.getHints());
			logger.log(Level.WARNING, "To Do: Find a matching rulespecific object for  "+npcType+"/"+conType+" and rating "+minRat+"-"+maxRat);

			List<SR6NPC> possible = new ArrayList<>();
			for (SR6NPC npc : Shadowrun6Core.getItemList(SR6NPC.class)) {
				logger.log(Level.INFO, "Check "+npc.getId()+": "+npc.getType()+"/"+npc.getTypes()+" with rating "+npc.getRating());
				if (minRat!=null && npc.getRating()<minRat) continue;
				if (maxRat!=null && npc.getRating()>maxRat) continue;
				if (npcType!=null && npc.getType()!=npcType) continue;
				if (conType!=null && !npc.isContactType(conType)) continue;
				possible.add(npc);
			}
			logger.log(Level.INFO, "Found "+possible.size()+" NPCs to choose from");
			if (!possible.isEmpty()) {
				SR6NPC npc = possible.get(RANDOM.nextInt(possible.size()));
				logger.log(Level.INFO, "Chose "+npc.getName());
				actor.setRuleData(npc);
			}
			return actor;
		} finally {
			logger.log(Level.INFO, "LEAVE: createNSC()");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.RandomGenerator#getProvidedData()
	 */
	@Override
	public Collection<DataType> getProvidedData() {
		return List.of(DataType.ACTOR_BASEDATA, DataType.RULEDATA);
	}
}
