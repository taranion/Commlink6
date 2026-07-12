package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.function.Function;

import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.LifepathModule;

/**
 * Read-only description pane for life modules.
 */
public class LifepathModuleDescriptionPane extends GenericDescriptionVBox<LifepathModule> {

	//-------------------------------------------------------------------
	public LifepathModuleDescriptionPane(Function<Requirement, String> requirementResolver,
			Function<Modification, String> modificationResolver) {
		super(requirementResolver, modificationResolver);
		description.setOnMouseEntered(null);
		description.setOnMouseExited(null);
	}

}
