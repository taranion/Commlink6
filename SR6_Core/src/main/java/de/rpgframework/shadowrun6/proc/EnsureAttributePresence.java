package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeFloatValue;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class EnsureAttributePresence implements ProcessingStep {

	private final static Logger logger = System.getLogger("shadowrun6.proc");

	private Shadowrun6Character model;

	// -------------------------------------------------------------------
	public EnsureAttributePresence(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	private void ensureAttribute(ShadowrunAttribute key, int val) {
		AttributeValue<ShadowrunAttribute> attr = model.getAttribute(key);
		if (attr== null) {
			attr = new AttributeValue<ShadowrunAttribute>(key, val);
			model.setAttribute(attr);
		}
	}
	
	//-------------------------------------------------------------------
	@Override
	public List<Modification> process(List<Modification> previous) {
		for (ShadowrunAttribute key : ShadowrunAttribute.values()) {
			ensureAttribute(key, 0);
		}
		
		return previous;
	}

}
