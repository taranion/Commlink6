package de.rpgframework.shadowrun6.chargen.jfx.selector;

import java.util.Locale;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.jfx.AFilterInjector;
import de.rpgframework.jfx.NameFilter;
import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.SelectorBuilder;
import de.rpgframework.shadowrun.ASpell;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.ComplexForm;
import de.rpgframework.shadowrun.DisciplineTechnique;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.chargen.jfx.FilterAdeptPowers;
import de.rpgframework.shadowrun.chargen.jfx.FilterComplexForms;
import de.rpgframework.shadowrun.chargen.jfx.FilterMetamagicOrEcho;
import de.rpgframework.shadowrun.chargen.jfx.FilterRituals;
import de.rpgframework.shadowrun.chargen.jfx.FilterSpells;
import de.rpgframework.shadowrun.chargen.jfx.pages.FilterQualities;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.jfx.FilterItemTemplate;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemEnhancement;

/**
 * Builder for SR6 {@link Selector} dialogs. Sets the SR6 requirement and
 * modification resolvers and ensures every selector has at least a keyword
 * search filter (and specialized filters for gear, qualities, spells, etc.).
 *
 * @author prelle
 */
public class SR6SelectorBuilder<T extends ComplexDataItem, V extends ComplexDataItemValue<T>> extends SelectorBuilder<T, V> {

	// ---
	public static <X extends ComplexDataItem, Y extends ComplexDataItemValue<X>> SR6SelectorBuilder<X, Y> builder(
			Class<X> type, Class<Y> selectedType) {
		return new SR6SelectorBuilder<>(type, selectedType);
	}

	// ---
	protected SR6SelectorBuilder(Class<T> type, Class<V> selType) {
		super(type, selType);
		this.modificationResolver = m -> Shadowrun6Tools.getModificationString(m, Locale.getDefault());
		this.requirementResolver  = r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault());
	}

	// ---
	@SuppressWarnings("unchecked")
	private AFilterInjector<T> defaultFilter() {
		if (ItemTemplate.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterItemTemplate(CarryMode.CARRIED);
		}
		if (Quality.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterQualities();
		}
		if (ASpell.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterSpells<>(
					ResourceI18N.get(SR6CharacterViewLayout.UI, "filter.search.prompt"),
					ResourceI18N.get(SR6CharacterViewLayout.UI, "filter.spell.category.all"));
		}
		if (AdeptPower.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterAdeptPowers<>();
		}
		if (ComplexForm.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterComplexForms();
		}
		if (DisciplineTechnique.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterMetamagicOrEcho();
		}
		if (Ritual.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterRituals();
		}
		if (SR6ItemEnhancement.class.isAssignableFrom(type)) {
			return (AFilterInjector<T>) new FilterItemEnhancement(
					ResourceI18N.get(SR6CharacterViewLayout.UI, "filter.search.prompt"));
		}
		return new NameFilter<>(ResourceI18N.get(SR6CharacterViewLayout.UI, "filter.search.prompt"));
	}

	// ---
	/**
	 * Adds the default filter, if none has been set, and lets the base class
	 * build the selector so that all other settings (e.g. cell factory) are applied.
	 *
	 * @see de.rpgframework.jfx.SelectorBuilder#build()
	 */
	@Override
	public Selector<T, V> build() {
		if (filter == null) {
			filter = defaultFilter();
		}
		return super.build();
	}

}
