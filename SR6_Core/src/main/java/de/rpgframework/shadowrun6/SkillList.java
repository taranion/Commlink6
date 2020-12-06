/**
 * 
 */
package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="skills")
@ElementList(entry="skill",type=SR6Skill.class,inline=true)
public class SkillList extends ArrayList<SR6Skill> {

	private static final long serialVersionUID = 550291427112058863L;

	//-------------------------------------------------------------------
	/**
	 */
	public SkillList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public SkillList(Collection<? extends SR6Skill> c) {
		super(c);
	}

	//-------------------------------------------------------------------
	public List<SR6Skill> getSkills() {
		return this;
	}
}
