/**
 * 
 */
package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.shadowrun6.persist.SpellFeatureConverter;

/**
 * @author prelle
 *
 */

public class SpellFeatureReference {
	
	@Attribute
	@AttribConvert(SpellFeatureConverter.class)
	private SpellFeature ref;

	//-------------------------------------------------------------------
	public SpellFeatureReference() {	
	}

	//-------------------------------------------------------------------
	public SpellFeatureReference(SpellFeature feat) {
		this.ref = feat;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.valueOf(ref);
	}

	//-------------------------------------------------------------------
	public SpellFeature getFeature() {
		return ref;
	}

}
