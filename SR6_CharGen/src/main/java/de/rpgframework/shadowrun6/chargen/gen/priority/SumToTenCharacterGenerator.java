package de.rpgframework.shadowrun6.chargen.gen.priority;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.genericrpg.chargen.GeneratorId;
import de.rpgframework.shadowrun.chargen.gen.PriorityTableController;
import de.rpgframework.shadowrun.chargen.gen.SumToTenPriorityTableController;
import de.rpgframework.shadowrun6.Shadowrun6Character;

/**
 * @author prelle
 *
 */
@GeneratorId("sumto10")
public class SumToTenCharacterGenerator extends PriorityCharacterGenerator {

	private static MultiLanguageResourceBundle RES = new MultiLanguageResourceBundle(SumToTenCharacterGenerator.class,
			Locale.ENGLISH, Locale.GERMAN);

	//-------------------------------------------------------------------
	public SumToTenCharacterGenerator() {
	}


	//-------------------------------------------------------------------
	public SumToTenCharacterGenerator(Shadowrun6Character model, CharacterHandle handle) {
		super(model, handle);
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.gen.IShadowrunCharacterGenerator#getId()
	 */
	@Override
	public String getId() {
		return "sumto10";
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getName()
	 */
	@Override
	public String getName() {
		return RES.getString("generator.name");
	}

	// -------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return RES.getString("generator.desc");
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun6.chargen.gen.priority.shadowrun5.chargen.gen.PriorityCharacterGenerator#createPriorityTableController()
	 */
	@Override
	protected PriorityTableController<Shadowrun6Character,SR6PrioritySettings> createPriorityTableController() {
		return new SumToTenPriorityTableController<Shadowrun6Character, SR6PrioritySettings>(this, SR6PrioritySettings.class, resolver);
	}

}
