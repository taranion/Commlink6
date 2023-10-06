package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;

import org.prelle.javafx.ResponsiveControl;

import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
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
	public QualityPathSelector(IQualityPathController ctrl) {
		super(ctrl, Shadowrun6Tools.requirementResolver(Locale.getDefault()), Shadowrun6Tools.modificationResolver(Locale.getDefault()), null);
		if (descr==null)
			descr = new QualityPathDescriptionPane();
		listPossible.setCellFactory(lv -> new ComplexDataItemListCell<>(
				() -> ctrl,
				Shadowrun6Tools.requirementResolver(Locale.getDefault())));

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
