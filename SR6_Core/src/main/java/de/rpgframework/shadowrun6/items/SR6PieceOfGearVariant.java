package de.rpgframework.shadowrun6.items;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.IgnoreMissingAttributes;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.DataItemTypeKey;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.genericrpg.items.PieceOfGearVariant;
import de.rpgframework.shadowrun.items.AugmentationQuality;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id = "variant")
//@IgnoreMissingAttributes("id")
public class SR6PieceOfGearVariant extends PieceOfGearVariant<SR6VariantMode> {
	
	@Attribute
	private ItemHook slot;
	@Attribute
	private float size;
	/**
	 * Can the gear only be picked by modifications (TRUE) or
	 * can the user freely select it (FALSE)
	 */
	@Attribute(name="modonly")
	private boolean modOnly;

	//-------------------------------------------------------------------
	public SR6PieceOfGearVariant() {
	}

	//-------------------------------------------------------------------
	public SR6PieceOfGearVariant(SR6VariantMode mode) {
		this.mode = mode;
	}

	//-------------------------------------------------------------------
	public SR6PieceOfGearVariant(SR6VariantMode mode, ItemHook slot) {
		this(mode);
		this.slot = slot;
	}

	//-------------------------------------------------------------------
	public ItemHook getSlot() {
		return slot;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.items.AGearData#getTypeData()
	 */
	@Override
	public List<? extends IGearTypeData> getTypeData() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
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
		if (flags.contains(ItemTemplate.FLAG_AUGMENTATION) && this.getChoice(ItemTemplate.UUID_AUGMENTATION_QUALITY)==null) {
			Choice choice = new Choice(ItemTemplate.UUID_AUGMENTATION_QUALITY, ShadowrunReference.AUGMENTATION_QUALITY);
//			choice.setChoiceOptions("STANDARD,ALPHA,BETA,DELTA,USED");
			choices.add(choice);
		}

	}
}
