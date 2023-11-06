package de.rpgframework.shadowrun6;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.modification.AllowModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;

/**
 * @author prelle
 *
 */
public class DrakeTypeValue extends ComplexDataItemValue<DrakeType> {

	@ElementList(type = AttributeValue.class, entry = "attributes")
    protected List<AttributeValue<ShadowrunAttribute>> attributes;

	//-------------------------------------------------------------------
	public DrakeTypeValue() {
		attributes = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	/**
	 * @param data
	 */
	public DrakeTypeValue(DrakeType data) {
		super(data);
		attributes = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public List<ShadowrunAttribute> getDrakeAttributes() {
		return getResolved().getOutgoingModifications().stream()
			.filter(m -> m instanceof AllowModification)
			.map(m -> (ShadowrunAttribute)((AllowModification)m).getResolvedKey())
			.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	public AttributeValue<ShadowrunAttribute> getAttribute(ShadowrunAttribute key) {
		for (AttributeValue<ShadowrunAttribute> val : attributes) {
			if (val.getModifyable()==key)
				return val;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public List<AttributeValue<ShadowrunAttribute>> getAttributes() {
		List<AttributeValue<ShadowrunAttribute>> ret = new ArrayList<AttributeValue<ShadowrunAttribute>>(attributes);
		return ret;
	}

	//-------------------------------------------------------------------
	public void setAttribute(AttributeValue<ShadowrunAttribute> val) {
		if (val.getModifyable().isDerived())
			return;
		else
			attributes.add(val);
	}

}
