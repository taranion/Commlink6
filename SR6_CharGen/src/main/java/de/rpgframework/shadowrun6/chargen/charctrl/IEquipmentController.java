package de.rpgframework.shadowrun6.chargen.charctrl;

import java.util.List;

import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun6.items.ItemHook;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author prelle
 *
 */
public interface IEquipmentController extends 
	ComplexDataItemController<ItemTemplate, CarriedItem<ItemTemplate>>, 
	NumericalValueController<ItemTemplate, CarriedItem<ItemTemplate>> {


	//-------------------------------------------------------------------
	/**
	 * Get all items that are embeddable in the given object
	 */
	public List<ItemTemplate> getEmbeddableIn(CarriedItem<ItemTemplate> ref, ItemHook slot);

	public Possible canBeEmbedded(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, Decision...decisions);

	public OperationResult<CarriedItem<ItemTemplate>> embed(CarriedItem<ItemTemplate> container, ItemHook slot, ItemTemplate value, Decision...decisions);

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
