package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger.Level;

import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.gen.PerSkillPoints;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunSkillTable;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunSkillTableSkin;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;

/**
 * @author prelle
 *
 */
public class SR6SkillTablePointBuySkin extends ShadowrunSkillTableSkin<SR6Skill, SR6SkillValue, Shadowrun6Character> {

	public SR6SkillTablePointBuySkin(ShadowrunSkillTable<SR6Skill, SR6SkillValue, Shadowrun6Character> control,
			SkillType... allowedTypes) {
		super(control, allowedTypes);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPoints1(SR6SkillValue sVal) {
		Shadowrun6Character model = getSkinnable().getController().getModel();
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints points = settings.get(sVal);
		if (points!=null) {
			return points.points1;
		}
//		logger.log(Level.ERROR, "Cannot determine points1 for {} since it isn't in the SR6PrioritySettings", sVal);
		return 0;
	}

	@Override
	public int getPoints2(SR6SkillValue sVal) {
		Shadowrun6Character model = getSkinnable().getController().getModel();
		SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
		PerSkillPoints points = settings.get(sVal);
		if (points!=null) {
			return points.points2;
		}
//		logger.log(Level.ERROR, "Cannot determine points2 for {} since it isn't in the SR6PrioritySettings", sVal);
		return 0;
	}

}
