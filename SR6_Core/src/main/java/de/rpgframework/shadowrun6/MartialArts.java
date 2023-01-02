package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun.AMartialArts;
import de.rpgframework.shadowrun6.Technique.Category;
import de.rpgframework.shadowrun6.persist.TechniqueCategoryConverter;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="martialart")
public class MartialArts extends AMartialArts {

	@Attribute(name="cat")
	@AttribConvert(TechniqueCategoryConverter.class)
	private List<Category> categories;
	@Attribute(name="sign")
	private String signatureTechniqueID;
	private transient Technique signatureTechnique;

	//-------------------------------------------------------------------
	public MartialArts() {
		super();
	}

	//-------------------------------------------------------------------
	/**
	 * @return the category
	 */
	public Collection<Category> getCategories() {
		return new ArrayList<Technique.Category>(categories);
	}

	//-------------------------------------------------------------------
	/**
	 * @return the signatureTechnique
	 */
	public Technique getSignatureTechnique() {
		if (signatureTechnique==null) {
			signatureTechnique = Shadowrun6Core.getItem(Technique.class, signatureTechniqueID);
		}
		return signatureTechnique;
	}

	//-------------------------------------------------------------------
	/**
	 * @param signatureTechnique the signatureTechnique to set
	 */
	public void setSignatureTechnique(Technique signatureTechnique) {
		this.signatureTechnique = signatureTechnique;
	}

}
