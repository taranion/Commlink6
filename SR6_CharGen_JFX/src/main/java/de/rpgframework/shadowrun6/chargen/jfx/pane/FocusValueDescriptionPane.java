package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger.Level;
import java.util.function.Function;

import de.rpgframework.genericrpg.data.DataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class FocusValueDescriptionPane extends GenericDescriptionVBox {

	private SR6CharacterController ctrl;
	private VBox extra;
	
	//-------------------------------------------------------------------
	public FocusValueDescriptionPane(Function<Requirement,String> requirementResolver, SR6CharacterController ctrl) {
		super(requirementResolver);
		this.ctrl = ctrl;
	}
	
	//-------------------------------------------------------------------
	public FocusValueDescriptionPane(Function<Requirement,String> requirementResolver, SR6CharacterController ctrl, FocusValue item) {
		super(requirementResolver);
		this.ctrl = ctrl;
		setData(item);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.GenericDescriptionVBox#initLayout()
	 */
	protected void initLayout() {
		super.initLayout();
		extra = new VBox(5);
		inner.getChildren().add(0, extra);
		setStyle("-fx-max-width: 20em");

	}
	
	//-------------------------------------------------------------------
	public void setData(FocusValue data) {
		System.getLogger(FocusValueDescriptionPane.class.getPackageName()).log(Level.WARNING, "setData");
		super.setData(data);
		extra.getChildren().clear();
		
		Node node = ItemUtilJFX.getItemInfoNode(data, ctrl);
		extra.getChildren().add(node);
	}
	
	//-------------------------------------------------------------------
	public void setData(DataItemValue<?> data) {
		if (data instanceof FocusValue)
			setData( (FocusValue)data );
		else
			super.setData(data);
	}
}
