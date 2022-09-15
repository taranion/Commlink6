package de.rpgframework.shadowrun6;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.shadowrun6.Technique.Category;
import de.rpgframework.shadowrun6.persist.TechniqueCategoryConverter;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="martialart")
public class MartialArts extends ComplexDataItem {
	
	@Attribute(name="cat")
	@AttribConvert(TechniqueCategoryConverter.class)
	private List<Category> categories;
	@Attribute(name="sign")
	private String signatureTechniqueID;
	private transient Technique signatureTechnique;

	//-------------------------------------------------------------------
	public MartialArts() {
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
