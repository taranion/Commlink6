package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.List;

import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;

/**
 * @author prelle
 *
 */
public interface IEquipmentController extends 
	ComplexDataItemController<ItemTemplate, CarriedItem<ItemTemplate>>, 
	NumericalValueController<ItemTemplate, CarriedItem<ItemTemplate>> {

	//-------------------------------------------------------------------
	public List<ItemTemplate> getAvailable(CarryMode mode, ItemType...types);
	
	//-------------------------------------------------------------------
	/**
	 * Check if the user is allowed to select the item
	 * @param value  Item to select
	 * @param variant Variant to select
	 * @param decisions Decisions made
	 * @return Selection allowed or not
	 * @throws IllegalArgumentException Thrown if a decision is missing or invalid
	 */
	public Possible canBeSelected(ItemTemplate value, String variant, CarryMode mode, Decision... decisions);
	
	//-------------------------------------------------------------------
	/**
	 * Add/Select the item using the given decisions
	 * @param value  Item to select
	 * @param variant Variant to select
	 * @param decisions Decisions made
	 * @return value instance of selected item
	 * @throws IllegalArgumentException Thrown if a decision is missing or invalid
	 */
	public OperationResult<CarriedItem<ItemTemplate>> select(ItemTemplate value, String variant, CarryMode mode, Decision... decisions);


	//-------------------------------------------------------------------
	/**
	 * Get all items that are embeddable in the given object
	 */
	public List<ItemTemplate> getEmbeddableIn(CarriedItem<ItemTemplate> ref, ItemHook slot);

	//-------------------------------------------------------------------
	public Possible canBeEmbedded(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, String variant, Decision...decisions);

	//-------------------------------------------------------------------
	public OperationResult<CarriedItem<ItemTemplate>> embed(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, String variant, Decision...decisions);

	//-------------------------------------------------------------------
	public int getConvertedKarma();

	//-------------------------------------------------------------------
	public int getConversionRateKarma();

	//-------------------------------------------------------------------
	public boolean canIncreaseConversion();

	//-------------------------------------------------------------------
	public boolean increaseConversion();

	//-------------------------------------------------------------------
	public boolean canDecreaseConversion();

	//-------------------------------------------------------------------
	public boolean decreaseConversion();
	
}
