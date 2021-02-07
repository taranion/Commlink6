package de.rpgframework.shadowrun6;

import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6MetaType extends MetaType {

	//-------------------------------------------------------------------
	/**
	 */
	public SR6MetaType() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param id
	 */
	public SR6MetaType(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.MetaType#getVariantOf()
	 */
	@Override
	public MetaType getVariantOf() {
		return ShadowrunReference.resolve(ShadowrunReference.METATYPE, variantOf);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.data.DataItem#validate()
	 */
	@Override
	public void validate() {
		getName();
		getPageReferences();
		getDescription();
		
		for (Modification mod : modifications) {
			if (mod.getReferenceType()==null) {
				logger.error("Missing reference type for modification "+mod+" in metatype "+id);
				continue;
			}
			if (mod instanceof DataItemModification) {
				String key = ((DataItemModification)mod).getKey();
				Object resolved = mod.getReferenceType().resolve( key );
				if (resolved==null) {
					logger.error("Unknown reference "+mod.getReferenceType()+":'"+key+"' for modification in metatype "+id);
//					throw new IllegalArgumentException("Unknown reference "+mod.getReferenceType()+":'"+key+"' for metatype "+id);
				}
			}
		}
	}

}
