package de.rpgframework.shadowrun6.items;

import java.util.List;
import java.util.UUID;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.Root;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author Stefan
 *
 */
@Root(name="alchemy")
public class AlchemyData implements IGearTypeData {

	/**
	 * For which items is the accessory usable
	 */
	@Attribute
	private String spell;
	@Attribute(name="drain")
	private int drain;
	@Attribute(name="trigger")
	private String trigger;

	//--------------------------------------------------------------------
	public AlchemyData() {
//		usewith  = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "AlchemyData(spell="+spell+")";
	}

	@Override
	public void copyToAttributes(AGearData copyTo) {
		copyTo.addFlags(List.of("ALCHEMICAL_PREPARATION"));
//		copyTo.setAttribute(SR6ItemAttribute.DRAIN, drain);
//		copyTo.setAttribute(SR6ItemAttribute.DEFENSE_SOCIAL, social);
//		copyTo.setAttribute(SR6ItemAttribute.DAMAGE_REDUCTION, damageReduction);
		ItemTemplate item = (ItemTemplate)copyTo;
		if (item.getChoice(ItemTemplate.UUID_RATING)==null) {
			Choice choice = new Choice(ItemTemplate.UUID_RATING, ShadowrunReference.ITEM_ATTRIBUTE, "RATING");
			choice.setChoiceOptions(new String[] {"4","5","6","7","8"});
			choice.setI18NKey("potency");
			item.addChoice(choice);
		}
		if (item.getChoice("MAGIC")==null) {
			Choice choice = new Choice(UUID.fromString("96afe3d6-adf9-4e78-829c-b25f2c7f6b76"), ShadowrunReference.ATTRIBUTE, "MAGIC");
			choice.setChoiceOptions(new String[] {"4","5","6","7","8"});
			item.addChoice(choice);
		}
		if (item.getChoice(UUID.fromString("96afe3d6-adf9-4e78-829c-b25f2c7f6b77"))==null) {
			Choice choice = new Choice(UUID.fromString("96afe3d6-adf9-4e78-829c-b25f2c7f6b77"), ShadowrunReference.TEXT, null);
			choice.setI18NKey("shelflife");
			item.addChoice(choice);
		}

		if (item.getVariant("extended")==null) {
			SR6PieceOfGearVariant variant = new SR6PieceOfGearVariant();
			variant.setId("extended");
			item.addVariant(variant);
		}
	}

}
