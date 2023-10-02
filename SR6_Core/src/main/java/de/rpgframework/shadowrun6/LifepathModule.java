package de.rpgframework.shadowrun6;

import java.util.Locale;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ModificationChoice;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="lifemod")
public class LifepathModule extends ComplexDataItem {

	public enum Type {
		ADULT,
		CHOICES,
		EVENT
	}

	@Attribute
	private Type type;

	//-------------------------------------------------------------------
	public LifepathModule() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.ComplexDataItem#validate()
	 */
	@Override
	public void validate() {
		super.validate();

		// Check for i18n strings in decisions
		for (Modification mod : getModifications()) {
			if (mod instanceof ModificationChoice) {
				for (Modification m2 :((ModificationChoice)mod).getModificiations()) {
					if (m2 instanceof DataItemModification) {
						checkModification( (DataItemModification) m2);
					}
				}
			}
			if (mod instanceof DataItemModification) {
				checkModification( (DataItemModification) mod);
			}
		}
	}

	//-------------------------------------------------------------------
	private void checkModification(DataItemModification mod) {
		for (Decision dec : ((DataItemModification)mod).getDecisions()) {
			String val = dec.getValue();
			if (val.startsWith("i18n.")) {
				// Build i18n key
				String key = "lifemod."+id+"."+val.substring(5);
				getLocalizedString(Locale.getDefault(), key);
			}
		}

	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

}
