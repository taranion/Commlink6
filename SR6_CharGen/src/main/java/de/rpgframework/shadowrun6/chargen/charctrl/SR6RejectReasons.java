package de.rpgframework.shadowrun6.chargen.charctrl;

import de.rpgframework.shadowrun.chargen.charctrl.IRejectReasons;

/**
 * @author prelle
 *
 */
public interface SR6RejectReasons extends IRejectReasons {
	
	public final static String IMPOSS_QUALITY_ALREADY_6 = "impossible.qualityAlready6";
	public final static String IMPOSS_QUALITY_KARMAGAIN = "impossible.qualityMoreThan20Karma";
	public final static String IMPOSS_ALREADY_MAX_LIMIT = "impossible.cannotMaxMoreAttributes";
	
	public final static String TODO_ATTRIB_REMAIN_ADJUST = "impossible.remainingAdjustmentPoints";
	public final static String TODO_ATTRIB_REMAIN_ATTRIB = "impossible.remainingAttributePoints";
	
	public final static String TODO_SKILL_REMAIN_POINTS  = "impossible.remainingSkillPoints";
	public final static String TODO_SKILL_REMAIN_POINTS2 = "impossible.remainingKnowledgePoints";
	
	public final static String TODO_QUALITY_TOO_MANY     = "impossible.tooManyQualities";
	public final static String TODO_QUALITY_KARMAGAIN    = "impossible.gainedMoreThan20Karma";

}
