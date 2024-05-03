package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;
import java.util.function.Function;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class CarriedItemDescriptionPane extends GenericDescriptionVBox<ItemTemplate> {

	private SR6CharacterController ctrl;
	private VBox extra;

	//-------------------------------------------------------------------
	public CarriedItemDescriptionPane(Function<Requirement,String> requirementResolver, SR6CharacterController ctrl) {
		super(requirementResolver, Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		this.ctrl = ctrl;
	}

	//-------------------------------------------------------------------
	public CarriedItemDescriptionPane(Function<Requirement,String> requirementResolver, SR6CharacterController ctrl, CarriedItem<ItemTemplate> item) {
		super(requirementResolver, Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		this.ctrl = ctrl;
		setData(item);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.GenericDescriptionVBox#initExtraComponents()
	 */
	@Override
	protected void initExtraComponents() {
		super.initExtraComponents();
		extra = new VBox(5);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.GenericDescriptionVBox#initExtraLayout()
	 */
	@Override
	protected void initExtraLayout() {
		super.initExtraLayout();
		inner.getChildren().add(0, extra);
		setStyle("-fx-max-width: 20em");
	}

	//-------------------------------------------------------------------
	public void setData(CarriedItem<ItemTemplate> data) {
//		System.getLogger(CarriedItemDescriptionPane.class.getPackageName()).log(Level.DEBUG, "setData {0}",data);

		super.setData(data);
		if (extra!=null)
			extra.getChildren().clear();

		Node node = ItemUtilJFX.getItemInfoNode(data, ctrl, true);
		if (node!=null && extra!=null)
			extra.getChildren().add(node);
	}

}
