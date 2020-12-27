package de.rpgframework.shadowrun6.persist;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	
	private final static Logger logger = LogManager.getLogger("shadowrun.persist");

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
				logger.error("No such skill: "+skillID);
				throw new ReferenceException(ShadowrunReference.SKILLSPECIALIZATION, idref);
			}
			String specialID = tok.nextToken();

			special = skill.getSpecialization(specialID);
			if (special==null)
				special = skill.getSpecialization(idref);
			if (special==null) {
				logger.error("No specialization '"+idref+"' or '"+specialID+"' in skill "+skillID);
				for (SkillSpecialization spec : skill.getSpecializations()) {
					logger.error("  Known: "+spec.getId());
				}
				throw new ReferenceException(ShadowrunReference.SKILLSPECIALIZATION, idref);
			}
			return special;
		} catch (NoSuchElementException nse) {
			logger.error("Invalid skill specialization reference: "+idref);
			throw new ReferenceException(ShadowrunReference.SKILLSPECIALIZATION, idref);
		}
	}

}
