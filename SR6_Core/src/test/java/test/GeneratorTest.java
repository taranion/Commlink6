package test;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.simplepersist.Persister;

import de.rpgframework.classification.Gender;
import de.rpgframework.classification.Genre;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.genericrpg.modification.ModifiedObjectType;
import de.rpgframework.random.GeneratorType;
import de.rpgframework.random.Plot;
import de.rpgframework.random.RandomGeneratorRegistry;
import de.rpgframework.shadowrun.generators.GenericShadowrunGenerators;
import de.rpgframework.shadowrun.generators.FilterCulture;
import de.rpgframework.shadowrun.generators.TextExport;
import de.rpgframework.shadowrun6.generators.Shadowrun6Generators;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class GeneratorTest {

	private static Persister persist = new Persister();

	//-------------------------------------------------------------------
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Persister.putContext(Persister.PREFIX_KEY_INTERFACE+"."+ModifiedObjectType.class.getName(), ShadowrunReference.class);
		GenericShadowrunGenerators.initialize();
		Shadowrun6Generators.initialize();
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	//-------------------------------------------------------------------
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	//-------------------------------------------------------------------
	@Test
	public void test() throws Exception {
//		Object gen =RandomGeneratorRegistry.generate(GeneratorType.NAME_PERSON, List.of(FilterCulture.ADL), Gender.FEMALE);
//		System.out.println("Generated name: "+gen);
//		assertNotNull(gen);

		Object gen =RandomGeneratorRegistry.generate(GeneratorType.RUN, List.of(Genre.CYBERPUNK), List.of(FilterCulture.ADL, RoleplayingSystem.SHADOWRUN6), Map.of());
		System.out.println("Generated plot: "+gen);
		assertNotNull(gen);
		persist.write( (Plot)gen, System.out);

		TextExport txtExport = new TextExport();
		txtExport.export(gen, System.out, Locale.GERMAN);
		System.out.println("Plot export done");
	}

}
