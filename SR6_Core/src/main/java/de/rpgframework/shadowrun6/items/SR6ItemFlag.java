package de.rpgframework.shadowrun6.items;

/**
 * @author prelle
 *
 */
public enum SR6ItemFlag {

	// Item is subject to augmentation quality changes
	AUGMENTATION,
	// Item is considered a matrix device
	MATRIX_DEVICE,
	// Item should not get an automatic caseless variant while loading
	NO_CASELESS_AMMO,
	// This weapon uses caseless ammunition
	USES_CASELESS,
	// From the dice pool, convert one die to a wild die
	CONVERT_ONE_DIE_TO_WILD,
	
}
