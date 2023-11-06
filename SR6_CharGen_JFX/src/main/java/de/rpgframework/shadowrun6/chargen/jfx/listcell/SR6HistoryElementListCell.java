package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.ResourceBundle;

import org.prelle.javafx.FontIcon;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.SymbolIcon;
import org.prelle.javafx.WindowMode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.HistoryElement;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.modification.DataItemModification;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.jfx.cells.HistoryElementListCell;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.TilePane;

/**
 * @author prelle
 *
 */
public class SR6HistoryElementListCell extends HistoryElementListCell implements ResponsiveControl {
	
	private Label hdKarma, hdMoney, hdHeat, hdRep;
	private Label lbKarma, lbMoney, lbHeat, lbRep;

	//-------------------------------------------------------------------
	public SR6HistoryElementListCell(ResourceBundle res) {
		super(res);
		
		hdKarma = new Label(":", new SymbolIcon("Education"));
		hdKarma.setTooltip(new Tooltip(ResourceI18N.get(res, "label.karma")));
		hdMoney = new Label(":", new SymbolIcon("Nuyen"));
		hdMoney.setTooltip(new Tooltip(ResourceI18N.get(res, "label.nuyen")));
		hdHeat  = new Label(":", new FontIcon("\uD83D\uDD25"));
		hdHeat.setTooltip(new Tooltip(ResourceI18N.get(res, "label.heat")));
		hdRep   = new Label(":", new SymbolIcon("FavoriteStarFill"));
		hdRep.setTooltip(new Tooltip( ResourceI18N.get(res, "label.reputation")));
		lbKarma = new Label(null, hdKarma);
		lbKarma.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbMoney = new Label(null, hdMoney);
		lbMoney.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbHeat  = new Label(null, hdHeat);
		lbHeat.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbRep   = new Label(null, hdRep);
		lbRep.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		
		lbKarma.setMinWidth(60);
		lbMoney.setMinWidth(60);
		lbHeat .setMinWidth(60);
		lbRep  .setMinWidth(60);
	}

	//-------------------------------------------------------------------
	private int getAttributeChange(HistoryElement item, ShadowrunAttribute key) {
		int sum = 0;
		for (Reward reward : item.getGained()) {
			for (Modification tmp : reward.getIncomingModifications()) {
				if (tmp instanceof ValueModification && tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE) {
					ValueModification mod = (ValueModification)tmp;
					if (mod.getResolvedKey()==key)
						sum += mod.getValue();
				}
			}
		}
		// Now expenses
		for (DataItemModification tmp : item.getSpent()) {
			if (tmp instanceof ValueModification && tmp.getReferenceType()==ShadowrunReference.ATTRIBUTE && tmp.getResolvedKey()==key) {
				sum += ((ValueModification)tmp).getValue();
			}
		}

		return sum;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.cells.HistoryElementListCell#updateRuleData(javafx.scene.layout.TilePane, de.rpgframework.genericrpg.HistoryElement)
	 */
	@Override
	protected void updateRuleData(TilePane tiles, HistoryElement item) {
		tiles.getChildren().clear();
		tiles.setPrefColumns(4);
		tiles.getChildren().addAll(lbKarma, lbMoney, lbHeat, lbRep);
		tiles.setMaxWidth(Double.MAX_VALUE);

		int karma = item.getTotalExperience();
		lbKarma.setVisible(karma!=0);
		lbKarma.setText(String.valueOf(karma));

		int money = item.getTotalMoney();
		lbMoney.setVisible(money!=0);
		lbMoney.setText(String.valueOf(money));
		
		int heat = getAttributeChange(item, ShadowrunAttribute.HEAT);
		lbHeat.setVisible(heat!=0);
		lbHeat.setText( (heat!=0)?(String.valueOf(heat)):"");
		
		int rep = getAttributeChange(item, ShadowrunAttribute.REPUTATION);
		lbRep.setVisible(rep!=0);
		lbRep.setText( (rep!=0)?(String.valueOf(rep)):"");
	}

	@Override
	public void setResponsiveMode(WindowMode value) {
		if (value==WindowMode.MINIMAL) {
		}
	}

}
