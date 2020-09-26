/**
 * 
 */
package org.prelle.shadowrun6;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="features")
@ElementList(entry="feature",type=SpellFeature.class)
public class SpellFeatureList extends ArrayList<SpellFeature> {

}
