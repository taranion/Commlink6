package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.function.Function;

import org.prelle.javafx.ResponsiveControl;

import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.Selector;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathValue;
import de.rpgframework.shadowrun6.chargen.charctrl.IQualityPathController;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

/**
 * @author prelle
 *
 */
public class QualityPathSelector extends Selector<QualityPath, QualityPathValue> implements ResponsiveControl {
	
	private QualityPathDescriptionPane descr;
	
	//-------------------------------------------------------------------
	public QualityPathSelector(IQualityPathController ctrl, Function<Requirement,String> resolver) {
		super(ctrl, resolver, null);
		if (descr==null)
			descr = new QualityPathDescriptionPane();
		listPossible.setCellFactory(lv -> new ListCell<QualityPath>() {
			public void updateItem(QualityPath item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);					
				} else {
					setText(item.getName());
				}
			}
		});

	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.Selector#getDescriptionNode(de.rpgframework.genericrpg.data.ComplexDataItem)
	 */
	@Override
	protected Pane getDescriptionNode(QualityPath selected) {
		if (descr==null)
			descr = new QualityPathDescriptionPane();
		if (selected!=null)
			descr.setData(selected);
		return descr;
	}

}
