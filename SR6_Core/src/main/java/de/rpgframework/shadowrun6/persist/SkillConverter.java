package de.rpgframework.shadowrun6.persist;

import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SkillConverter implements StringValueConverter<SR6Skill> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SR6Skill value) throws Exception {
		return value.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SR6Skill read(String idref) throws Exception {
		SR6Skill skill = Shadowrun6Core.getSkill(idref);
		if (skill==null)
			throw new ReferenceException(ShadowrunReference.SKILL, idref);

		return skill;
	}

}
