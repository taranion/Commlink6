package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;

import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.Root;

/**
 * @author prelle
 *
 */
@Root(name="qpaths")
@ElementList(entry="qpath",type=QualityPath.class,inline=true)
public class QualityPathList extends ArrayList<QualityPath> {

	private static final long serialVersionUID = -2864844515871126068L;

	//-------------------------------------------------------------------
	/**
	 */
	public QualityPathList() {
	}

	//-------------------------------------------------------------------
	/**
	 * @param c
	 */
	public QualityPathList(Collection<? extends QualityPath> c) {
		super(c);
	}

}
