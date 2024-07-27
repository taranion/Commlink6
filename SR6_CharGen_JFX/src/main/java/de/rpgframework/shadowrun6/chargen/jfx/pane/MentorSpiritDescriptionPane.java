package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;
import java.util.function.Function;

import de.rpgframework.genericrpg.data.DataItemValue;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MentorSpirit;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class MentorSpiritDescriptionPane extends GenericDescriptionVBox {

	private VBox extra;

	//-------------------------------------------------------------------
	public MentorSpiritDescriptionPane(Function<Requirement,String> requirementResolver) {
		super(requirementResolver, Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	public MentorSpiritDescriptionPane(Function<Requirement,String> requirementResolver, SR6CharacterController ctrl, MentorSpirit item) {
		super(requirementResolver, Shadowrun6Tools.modificationResolver(Locale.getDefault()));
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

//	//-------------------------------------------------------------------
//	public void setData(MentorSpirit data) {
//		System.getLogger(MentorSpiritDescriptionPane.class.getPackageName()).log(Level.WARNING, "setData");
//		super.setData(data);
//		extra.getChildren().clear();
//
//		Node node = ItemUtilJFX.getItemInfoNode(data, ctrl, true);
//		extra.getChildren().add(node);
//	}

}
