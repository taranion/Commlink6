/**
 * 
 */
package org.prelle.shadowrun6;

import org.prelle.shadowrun6.persist.SpellFeatureConverter;
import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

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
