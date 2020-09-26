/**
 * 
 */
package org.prelle.shadowrun6;

import java.util.Locale;

import de.rpgframework.ResourceI18N;

/**
 * @author prelle
 *
 */
public enum DamageType {
	PHYSICAL,
	STUN,
	PHYSICAL_SPECIAL,
	STUN_SPECIAL,
	FIRE,
	COLD,
	ELECTRICITY,
	CHEMICAL
	;
	@Deprecated
	public String getName()           { return ResourceI18N.get(ShadowrunCore.getI18nResources().getDefault(),"damagetype."+name().toLowerCase()); }
	@Deprecated
	public String getShortName()      { return ResourceI18N.get(ShadowrunCore.getI18nResources().getDefault(),"damagetype."+name().toLowerCase()+".short"); }
	public String getName(Locale loc) { return ResourceI18N.get(ShadowrunCore.getI18nResources(), loc, "damagetype."+name().toLowerCase()); }
	public String getShortName(Locale loc) { return ResourceI18N.get(ShadowrunCore.getI18nResources(), loc, "damagetype."+name().toLowerCase()+".short"); }

}
