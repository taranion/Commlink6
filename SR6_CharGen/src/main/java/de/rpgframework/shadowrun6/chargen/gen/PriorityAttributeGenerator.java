package de.rpgframework.shadowrun6.chargen.gen;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.PerAttributePoints;
import de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;

/**
 * @author stefa
 *
 */
public class PriorityAttributeGenerator extends CommonAttributeGenerator implements PriorityAttributeController {

	private final static Logger logger = LogManager.getLogger(PriorityAttributeGenerator.class.getPackageName());
	
	//-------------------------------------------------------------------
	public PriorityAttributeGenerator(SR6CharacterController parent) {
		super(parent);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canDecreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canDecreaseAdjust(ShadowrunAttribute key) {
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		return per.adjust>0;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canIncreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canIncreaseAdjust(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#increaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean increaseAdjust(ShadowrunAttribute key) {
		logger.info("increaseAdjust("+key+")");
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		per.adjust++;
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#decreaseAdjust(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean decreaseAdjust(ShadowrunAttribute key) {
		logger.info("decreaseAdjust("+key+")");
		PerAttributePoints per = parent.getModel().getCharGenSettings(SR6PrioritySettings.class).perAttrib.get(key);
		per.adjust--;
		return true;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canDecreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canDecreaseAttrib(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canIncreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canIncreaseAttrib(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#increaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean increaseAttrib(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#decreaseAttrib(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean decreaseAttrib(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canDecreaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canDecreaseKarma(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#canIncreaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean canIncreaseKarma(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#increaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean increaseKarma(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.PriorityAttributeController#decreaseKarma(de.rpgframework.shadowrun.ShadowrunAttribute)
	 */
	@Override
	public boolean decreaseKarma(ShadowrunAttribute key) {
		// TODO Auto-generated method stub
		return false;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// TODO Auto-generated method stub
		return null;
	}

}
