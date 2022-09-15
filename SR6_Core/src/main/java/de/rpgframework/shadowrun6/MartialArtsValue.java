package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.data.ComplexDataItemValue;

/**
 * @author prelle
 *
 */
@Root(name = "martialartval")
public class MartialArtsValue extends ComplexDataItemValue<MartialArts> {

	//-------------------------------------------------------------------
	public MartialArtsValue() {
	}

	//-------------------------------------------------------------------
	public MartialArtsValue(MartialArts learnedIn) {
		super(learnedIn);
	}

}
