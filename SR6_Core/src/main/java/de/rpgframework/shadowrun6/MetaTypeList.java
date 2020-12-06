package de.rpgframework.shadowrun6;

import java.util.ArrayList;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
@Root(name="metatypes")
@ElementList(entry="metatype",type=SR6MetaType.class)
public class MetaTypeList extends ArrayList<SR6MetaType> {

}
