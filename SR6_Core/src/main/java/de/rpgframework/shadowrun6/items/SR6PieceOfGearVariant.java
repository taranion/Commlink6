package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;
import org.prelle.simplepersist.ElementListUnion;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "variant")
//@IgnoreMissingAttributes("id")
public class SR6PieceOfGearVariant extends PieceOfGearVariant<SR6VariantMode> {

	/**
	 * Can the gear only be picked by modifications (TRUE) or
	 * can the user freely select it (FALSE)
	 */
	@Attribute(name="modonly")
	private boolean modOnly;

	@ElementListUnion({
		@ElementList(entry="ammo", type = AmmunitionData.class, inline = true),
		@ElementList(entry="weapon", type = WeaponData.class, inline = true),
		@ElementList(entry="armor", type=ArmorData.class, inline=true),
		@ElementList(entry="matrix", type=MatrixData.class, inline=true),
		@ElementList(entry="vehicle", type=VehicleData.class, inline=true),
	})
	private List<IGearTypeData> shortcuts = new ArrayList<>();

	//-------------------------------------------------------------------
	public SR6PieceOfGearVariant() {
	}

	//-------------------------------------------------------------------
	public SR6PieceOfGearVariant(SR6VariantMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
		return shortcuts;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the modOnly
	 */
	public boolean isModOnly() {
		return modOnly;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.AGearData#validate()
	 */
	@Override
	public void validate() {
		super.validate();
		// If it has an AUGMENTATION flag, add that decision
		if (flags.contains(SR6ItemFlag.AUGMENTATION.name()) && this.getChoice(ItemTemplate.UUID_AUGMENTATION_QUALITY)==null) {
			Choice choice = new Choice(ItemTemplate.UUID_AUGMENTATION_QUALITY, ShadowrunReference.AUGMENTATION_QUALITY);
//			choice.setChoiceOptions("STANDARD,ALPHA,BETA,DELTA,USED");
			choices.add(choice);
		}

	}
}
