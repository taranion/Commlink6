package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.ShadowrunAction;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="action")
public class Shadowrun6Action extends ShadowrunAction {

	@Attribute(name="iattr")
	private SR6ItemAttribute itemAttribute;

}
