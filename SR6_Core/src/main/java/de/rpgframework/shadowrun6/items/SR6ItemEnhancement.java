package de.rpgframework.shadowrun6.items;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.items.AShadowrunItemEnhancement;

/**
 * @author Stefan
 *
 */
@DataItemTypeKey(id="itemmod")
public class SR6ItemEnhancement extends AShadowrunItemEnhancement {

	@Attribute
	protected ItemType type;
	@Attribute
	protected ItemSubType subtype;
	@Attribute(name="modonly")
	private boolean selectableByModificationOnly;

	//--------------------------------------------------------------------
	public SR6ItemEnhancement() {
	}

	//-------------------------------------------------------------------
	public ItemType getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the subtype
	 */
	public ItemSubType getSubtype() {
		return subtype;
	}

//	//-------------------------------------------------------------------
//	public String getConditionKey(HasOptionalCondition mod) {
//		return getId()+".cond."+mod.getConditionIndex();
//	}
//
//	//-------------------------------------------------------------------
//	public String getConditionName(HasOptionalCondition mod) {
//		String key = getId()+".cond."+mod.getConditionIndex();
//		if (i18n==null) 
//			return id;
//		try {
//			return Resource.get(i18n,key);
//		} catch (MissingResourceException e) {
//			if (!reportedKeys.contains(e.getKey())) {
//				reportedKeys.add(e.getKey());
//				logger.warn(String.format("key missing:    %s   %s", i18n.getBaseBundleName(), e.getKey()));
//				if (MISSING!=null) {
//					MISSING.println(e.getKey()+"=");
//				}
//			}
//			return e.getKey();
//		}
//	}

	//-------------------------------------------------------------------
	public boolean isSelectableByModificationOnly() {
		return selectableByModificationOnly;
	}

	//-------------------------------------------------------------------
	public void setSelectableByModificationOnly(boolean selectableByModificationOnly) {
		this.selectableByModificationOnly = selectableByModificationOnly;
	}

}
