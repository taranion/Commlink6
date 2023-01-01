package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.util.Locale;

import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author stefa
 *
 */
public class SR6QualitySection extends QualitySection {

	//-------------------------------------------------------------------
	public SR6QualitySection() {
		super(Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.section.QualitySection#handleChoices(de.rpgframework.shadowrun.Quality)
	 */
	@Override
	protected Decision[] handleChoices(Quality data) {
		ChoiceSelectorDialog<Quality, QualityValue> dialog = new ChoiceSelectorDialog<Quality, QualityValue>(control.getQualityController());
		return dialog.apply(data, data.getChoices());
	}
}
