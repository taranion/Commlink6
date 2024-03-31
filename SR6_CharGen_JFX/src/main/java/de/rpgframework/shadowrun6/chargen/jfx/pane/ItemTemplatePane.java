package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;
import java.util.function.Function;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.Node;

/**
 * @author prelle
 *
 */
public class ItemTemplatePane extends GenericDescriptionVBox {

	private Node perTypeStats;
	private CarryMode carry;

	// -------------------------------------------------------------------
	public ItemTemplatePane(Function<Requirement, String> requirementResolver, CarryMode carry) {
		super(requirementResolver, Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		this.carry = carry;
	}

	//-------------------------------------------------------------------
	public ItemTemplatePane(Function<Requirement, String> requirementResolver, ItemTemplate item, CarryMode carry) {
		super(requirementResolver, Shadowrun6Tools.modificationResolver(Locale.getDefault()), item);
		this.carry = carry;
		setData(item);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.GenericDescriptionVBox#setData(de.rpgframework.genericrpg.data.DataItem)
	 */
	@Override
	public void setData(DataItem data) {
		super.setData(data);

		if (perTypeStats != null) {
			super.inner.getChildren().remove(perTypeStats);
		}

		if (data != null) {
			CarryMode realCarry = carry;
			if (realCarry==null) {
				ItemTemplate temp = (ItemTemplate) data;
				if (temp.getUsages().isEmpty()) {
					realCarry = temp.getVariants().iterator().next().getUsages().get(0).getMode();
				} else {
					realCarry = temp.getUsages().get(0).getMode();
				}
			}
			perTypeStats = ItemUtilJFX.getItemInfoNode((ItemTemplate)data, null, realCarry);
			if (perTypeStats != null) {
				super.inner.getChildren().add(0, perTypeStats);
			}
		}
	}

}
