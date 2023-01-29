package de.rpgframework.shadowrun6.items;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.items.ItemFlag;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public enum SR6ItemFlag implements ItemFlag {

	// Item has magic, potency and shelf life choices
	ALCHEMICAL_PREPARATION,
	// Item is subject to augmentation quality changes
	AUGMENTATION,
	// Item is considered a matrix device
	MATRIX_DEVICE,
	// Item should not get an automatic caseless variant while loading
	NO_CASELESS_AMMO,
	// This weapon uses caseless ammunition
	USES_CASELESS,
	// Power Plays
	CHEAP_KNOCK_OFF,
	// The value of this (armor) is added to the main armor
	CUMULATIVE,
	// From the dice pool, convert one die to a wild die
	CONVERT_ONE_DIE_TO_WILD,
	// Is marked to be the primary item of its kind by the user
	PRIMARY,
	/*
	 * CarriedItems flagged with this shall not be taken into account
	 * (for defense rating calculations)
	 * AUTO FLAG - not to be used by user
	 */
	IGNORE_FOR_CALCULATIONS,
	CODEMOD,
	/** Firing Squad p.61: Add alternate usage as melee weapon */
	MELEE_HARDENING_ALTERNATE,

	// For Body Shop
	/** Nuyen base cost is Karma of selection multiplied with 1000 */
	NUYEN_COST_KARMA1000,
	/** Essence base cost is Karma divided by 10 */
	ESSENCE_COST_KARMA_10
	;


	private static MultiLanguageResourceBundle RES = Shadowrun6Core.getI18nResources();

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.ItemFlag#getName()
	 */
	@Override
	public String getName() {
		return getName(Locale.getDefault());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.ItemFlag#getName(java.util.Locale)
	 */
	@Override
	public String getName(Locale loc) {
		return RES.getString("itemflag."+this.name().toLowerCase(), loc);
	}


}
