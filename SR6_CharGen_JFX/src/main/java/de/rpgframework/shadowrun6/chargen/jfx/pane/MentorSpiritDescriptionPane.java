package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger.Level;
import java.util.function.Function;

import de.rpgframework.genericrpg.data.DataItemValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.ItemUtilJFX;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MentorSpiritDescriptionPane extends GenericDescriptionVBox {

	private VBox extra;
	
	//-------------------------------------------------------------------
	public MentorSpiritDescriptionPane(Function<Requirement,String> requirementResolver) {
		super(requirementResolver);
	}
	
	//-------------------------------------------------------------------
	public MentorSpiritDescriptionPane(Function<Requirement,String> requirementResolver, SR6CharacterController ctrl, MentorSpirit item) {
		super(requirementResolver);
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
	
//	//-------------------------------------------------------------------
//	public void setData(MentorSpirit data) {
//		System.getLogger(MentorSpiritDescriptionPane.class.getPackageName()).log(Level.WARNING, "setData");
//		super.setData(data);
//		extra.getChildren().clear();
//		
//		Node node = ItemUtilJFX.getItemInfoNode(data, ctrl, true);
//		extra.getChildren().add(node);
//	}
	
	//-------------------------------------------------------------------
	public void setData(DataItemValue<?> data) {
		super.setData(data);
	}
}
