package de.rpgframework.shadowrun6;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import org.prelle.simplepersist.Element;

import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.shadowrun.MetaType;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemTemplateList;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class SR6MetaType extends MetaType {

	@Element(name="geardef")
	protected ItemTemplateList gearDef;

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
		if (variantOf==null) return null;
		return ShadowrunReference.resolve(ShadowrunReference.METATYPE, variantOf);
	}

	//-------------------------------------------------------------------
	private ItemTemplate resolveItem(String key) {
		if (gearDef==null) return null;

		for (ItemTemplate tmp : gearDef) {
			if (tmp.getId().equals(key)) {
				tmp.validate();
				return tmp;
			}
		}
		return null;
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
			mod.setSource(this);
			if (mod.getReferenceType()==null) {
				logger.log(Level.ERROR,"Missing reference type for modification "+mod+" in metatype "+id);
				continue;
			}
			if (mod instanceof DataItemModification) {
				String key = ((DataItemModification)mod).getKey();
				Object resolved = mod.getReferenceType().resolve( key );
				if (resolved==null) {
					logger.log(Level.ERROR,"Unknown reference "+mod.getReferenceType()+":'"+key+"' for modification in metatype "+id);
//					throw new IllegalArgumentException("Unknown reference "+mod.getReferenceType()+":'"+key+"' for metatype "+id);
				}
			}
		}

		// Validate gear definitions
		if (gearDef != null) {
			for (ItemTemplate tmp : gearDef) {
				tmp.validate();
				tmp.setParentItem(this);
				tmp.assignToDataSet(this.datasets.get(0));
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.MetaType#getAttributeModifications()
	 */
	@Override
	public List<Modification> getAttributeModifications() {
		return getModifications().stream().filter(mod -> mod.getReferenceType()==ShadowrunReference.ATTRIBUTE).collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.MetaType#getNonAttributeModifications()
	 */
	@Override
	public List<Modification> getNonAttributeModifications() {
		return getModifications().stream().filter(mod -> mod.getReferenceType()!=ShadowrunReference.ATTRIBUTE).collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	public List<ItemTemplate> getGearDefinitions() {
		return gearDef;
	}

}
