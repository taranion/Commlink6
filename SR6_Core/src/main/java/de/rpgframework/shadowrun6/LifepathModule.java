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
	@Override
	public String getName() {
		return getName(Locale.getDefault());
	}

	//-------------------------------------------------------------------
	@Override
	public String getName(Locale loc) {
		String ret = super.getName(loc);
		if (ret!=null && !ret.equals(getTypeString()+":"+id) && !ret.equals(getTypeString()+"."+id))
			return ret;
		return formatId(id);
	}

	//-------------------------------------------------------------------
	private static String formatId(String id) {
		String normalized = id.replace('_', ' ').replace('-', ' ');
		StringBuilder buf = new StringBuilder(normalized.length());
		boolean capitalize = true;
		for (int i=0; i<normalized.length(); i++) {
			char c = normalized.charAt(i);
			if (Character.isLetter(c) && capitalize) {
				buf.append(Character.toUpperCase(c));
				capitalize = false;
			} else {
				buf.append(c);
				capitalize = Character.isWhitespace(c) || c=='(' || c=='/';
			}
		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.ComplexDataItem#validate()
	 */
	@Override
	public void validate() {
		super.validate();

		// Check for i18n strings in decisions
		for (Modification mod : getOutgoingModifications()) {
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
