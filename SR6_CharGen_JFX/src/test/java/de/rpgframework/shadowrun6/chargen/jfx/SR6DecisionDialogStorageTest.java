package de.rpgframework.shadowrun6.chargen.jfx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.items.PieceOfGear;
import de.rpgframework.jfx.pane.DecisionDialog.DecisionDialogConfig;
import de.rpgframework.shadowrun.chargen.jfx.preview.SnapshotHelper;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

/**
 * Unit test for {@link SR6DecisionDialogStorage} and related helpers.
 */
public class SR6DecisionDialogStorageTest {

	private SR6DecisionDialogStorage storage;
	private Map<Choice, Decision> decisions;

	@BeforeClass
	public static void startToolkit() {
		SnapshotHelper.initToolkit();
	}

	@Before
	public void setUp() {
		storage = new SR6DecisionDialogStorage();
		decisions = new LinkedHashMap<>();
	}

	@Test
	public void testVariantHelpers() {
		UUID variantChoiceUUID = PieceOfGear.VARIANT;
		UUID otherChoiceUUID = UUID.randomUUID();

		Decision d1 = new Decision(variantChoiceUUID, "var1");
		Decision d2 = new Decision(otherChoiceUUID, "optA");
		Decision[] all = new Decision[] { d1, d2 };

		assertEquals("var1", SR6SelectAndDecide.variant(all));

		Decision[] without = SR6SelectAndDecide.withoutVariant(all);
		assertEquals(1, without.length);
		assertEquals(otherChoiceUUID, without[0].getChoiceUUID());
	}

	@Test
	public void testDamageElementChoiceNode() {
		Choice choice = new Choice(UUID.randomUUID(), ShadowrunReference.DAMAGE_ELEMENT);
		DecisionDialogConfig<String> config = DecisionDialogConfig.<String>builder()
				.item("Test Item")
				.build();

		Node node = SnapshotHelper.onFxThread(() -> storage.processChoice(null, config, choice, decisions, _ -> {}));
		assertNotNull(node);
		assertNotNull(node instanceof ChoiceBox<?>);
	}

	@Test
	public void testElementChoiceNode() {
		Choice choice = new Choice(UUID.randomUUID(), ShadowrunReference.ELEMENT);
		DecisionDialogConfig<String> config = DecisionDialogConfig.<String>builder()
				.item("Test Item")
				.build();

		Node node = SnapshotHelper.onFxThread(() -> storage.processChoice(null, config, choice, decisions, _ -> {}));
		assertNotNull(node);
		assertNotNull(node instanceof ChoiceBox<?>);
	}

	@Test
	public void testContextHandling() {
		assertNull(storage.getContext());
		storage.setContext(null);
		assertNull(storage.getContext());
	}
}
