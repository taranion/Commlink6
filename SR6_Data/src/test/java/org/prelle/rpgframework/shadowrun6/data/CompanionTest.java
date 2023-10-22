package org.prelle.rpgframework.shadowrun6.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;

/**
 * @author prelle
 *
 */
public class CompanionTest {

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
	}

	//-------------------------------------------------------------------
	@Before
	public void beforeEach() {
		model = new Shadowrun6Character();
	}

	//-------------------------------------------------------------------
	@Test
	public void testWings() {
		Shadowrun6Tools.runProcessors(model, Locale.ENGLISH);
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR);
		assertEquals(1, aVal.getModifier());

		// Left Arm 1
		Quality wingsT = Shadowrun6Core.getItem(SR6Quality.class, "functional_wings");
		QualityValue wings = new QualityValue(wingsT, 0);
		wings.addDecision(new Decision("da1c74d5-0b31-4241-abc8-78d045e7b698", "type2"));
		model.addQuality(wings);

		Shadowrun6Tools.runProcessors(model, Locale.ENGLISH);

		assertFalse(model.getVirtualCarriedItems().isEmpty());
		List<CarriedItem<ItemTemplate>> close = model.getCarriedItems().stream().filter(ci -> ci.getAsObject(SR6ItemAttribute.ITEMTYPE).getModifiedValue()==ItemType.WEAPON_CLOSE_COMBAT).collect(Collectors.toList());
		for (CarriedItem<?> closeItem : close) {
			System.out.println("Close combat with "+closeItem);
		}
		assertEquals(2, close.size());

		System.out.println("Movement = "+model.getAttribute(ShadowrunAttribute.MOVEMENT));

		assertNotNull(model.getBodyForms().get(0).getMovementGround());
		assertNotNull(model.getBodyForms().get(0).getMovementWater());
		assertNotNull(model.getBodyForms().get(0).getMovementAir());
	}

}
