package de.rpgframework.shadowrun6.persist;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SkillSpecializationConverter implements StringValueConverter<SkillSpecialization> {

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#write(org.prelle.simplepersist.XmlNode, java.lang.Object)
	 */
	@Override
	public String write(SkillSpecialization value) throws Exception {
		return value.getSkill().getId()+"/"+value.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.simplepersist.StringValueConverter#read(org.prelle.simplepersist.Persister.ParseNode, javax.xml.stream.events.StartElement)
	 */
	@Override
	public SkillSpecialization read(String idref) throws Exception {
		StringTokenizer tok = new StringTokenizer(idref, "/");
		SkillSpecialization special = null;
		try {
			String skillID   = tok.nextToken();
			SR6Skill skill = Shadowrun6Core.getSkill(skillID);
			if (skill==null) {
				throw new ReferenceException(ShadowrunReference.SKILLSPECIALIZATION, idref);
			}
			String specialID = tok.nextToken();

			special = skill.getSpecialization(specialID);
			if (special==null)
				special = skill.getSpecialization(idref);
			if (special==null) {
				LogManager.getLogger("shadowrun6.persist").error("No specialization '"+idref+"' or '"+specialID+"' in skill "+skillID);
				for (SkillSpecialization spec : skill.getSpecializations()) {
					LogManager.getLogger("shadowrun6.persist").error("  Known: "+spec.getId());
				}
				throw new ReferenceException(ShadowrunReference.SKILLSPECIALIZATION, idref);
			}
			return special;
		} catch (NoSuchElementException nse) {
			throw new ReferenceException(ShadowrunReference.SKILLSPECIALIZATION, idref);
		}
	}

}
