package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.LifepathModule;
import javafx.scene.control.Tooltip;

/**
 * Shows life module prerequisites as subtitle instead of generic source/page text.
 */
public class LifepathModuleListCell extends ComplexDataItemListCell<LifepathModule> {

	private static final ResourceBundle RES = ResourceBundle.getBundle("de.rpgframework.shadowrun6.chargen.jfx.wizard.SR6WizardPages");

	private Function<Requirement, String> requirementResolver;

	//-------------------------------------------------------------------
	public LifepathModuleListCell(
			Supplier<ComplexDataItemController<LifepathModule, ? extends ComplexDataItemValue<LifepathModule>>> controlProv,
			Function<Requirement, String> requirementResolver) {
		super(controlProv, requirementResolver);
		this.requirementResolver = requirementResolver;
	}

	//-------------------------------------------------------------------
	@Override
	public void updateItem(LifepathModule item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item==null)
			return;

		lbName.setText(item.getName(Locale.getDefault()));
		List<String> requirements = item.getRequirements().stream()
				.map(req -> requirementResolver!=null?requirementResolver.apply(req):String.valueOf(req))
				.filter(req -> req!=null && !req.isBlank())
				.collect(Collectors.toList());
		String subtitle = requirements.isEmpty()?getTypeLabel(item):String.join(", ", requirements);
		lbSource.setText(subtitle);
		lbSource.setTooltip(subtitle.isBlank()?null:new Tooltip(subtitle));
	}

	//-------------------------------------------------------------------
	private String getTypeLabel(LifepathModule item) {
		if (item.getType()==null)
			return "";
		String key = "page.adult.module.type."+item.getType().name();
		try {
			return RES.getString(key);
		} catch (MissingResourceException e) {
			return item.getType().name();
		}
	}

}
