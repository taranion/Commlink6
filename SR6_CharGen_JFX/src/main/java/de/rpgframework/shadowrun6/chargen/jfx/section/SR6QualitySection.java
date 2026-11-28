package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger.Level;
import java.util.Locale;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell.CellAction;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.jfx.SR6JFXTools;
import de.rpgframework.shadowrun6.chargen.jfx.SR6SelectAndDecide;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6QualityValueListCell;

/**
 * @author stefa
 *
 */
public class SR6QualitySection extends QualitySection {

	//-------------------------------------------------------------------
	public SR6QualitySection() {
		super(Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
		list.setCellFactory(cell -> new SR6QualityValueListCell(this, true) {
			protected boolean canPerformAction(QualityValue item, CellAction action) {
				return false; //!item.getDecisions().isEmpty();
			}
			protected OperationResult<QualityValue> editClicked(QualityValue ref) {
				logger.log(Level.WARNING, "TODO: editClicked");
				Decision[] decisions = handleEditChoices(ref);

				return new OperationResult<>();
			}
		});
	}

	//-------------------------------------------------------------------
	@Override
	protected void onAdd() {
		SR6SelectAndDecide.of(control, control.getQualityController(), Quality.class, QualityValue.class)
				.execute();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.section.QualitySection#handleChoices(de.rpgframework.shadowrun.Quality)
	 */
	@Override
	protected Decision[] handleChoices(Quality data) {
		return SR6JFXTools.askDecisions(control.getQualityController(), data);
	}
}
