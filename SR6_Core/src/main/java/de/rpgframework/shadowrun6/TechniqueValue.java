package de.rpgframework.shadowrun6;

import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItemValue;

/**
 * @author prelle
 *
 */
public class TechniqueValue extends ComplexDataItemValue<Technique> {
	
	@Attribute(name="style")
	private String martialArtID;
	private MartialArts martialArt;

	//-------------------------------------------------------------------
	public TechniqueValue() {
	}

	//-------------------------------------------------------------------
	public TechniqueValue(Technique data, MartialArts learnedIn) {
		super(data);
		if (data==null) throw new NullPointerException("Technique");
		if (learnedIn==null) throw new NullPointerException("MartialArt style");
		this.martialArt = learnedIn;
		this.martialArtID = learnedIn.getId();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the martialArt
	 */
	public MartialArts getMartialArt() {
		if (martialArt==null)
			martialArt = Shadowrun6Core.getItem(MartialArts.class, martialArtID);
		return martialArt;
	}

	//-------------------------------------------------------------------
	/**
	 * @param martialArt the martialArt to set
	 */
	public void setMartialArt(MartialArts value) {
		this.martialArt = value;
		this.martialArtID = value.getId();
	}

}
