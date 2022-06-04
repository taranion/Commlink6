package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class ItemTemplatePane extends GenericDescriptionVBox<ItemTemplate> {

	private Node perTypeStats;

	// -------------------------------------------------------------------
	public ItemTemplatePane(Function<Requirement, String> requirementResolver) {
		super(requirementResolver);
	}

	public ItemTemplatePane(Function<Requirement, String> requirementResolver, ItemTemplate item) {
		super(requirementResolver, item);
		setData(item);
	}

	// -------------------------------------------------------------------
	public void setData(ItemTemplate data) {
		super.setData(data);

		if (perTypeStats != null) {
			super.inner.getChildren().remove(perTypeStats);
		}

		if (data != null) {
			perTypeStats = ItemUtilJFX.getItemInfoNode(data, null);
			if (perTypeStats != null) {
				super.inner.getChildren().add(2, perTypeStats);
			}
		}
	}

}
