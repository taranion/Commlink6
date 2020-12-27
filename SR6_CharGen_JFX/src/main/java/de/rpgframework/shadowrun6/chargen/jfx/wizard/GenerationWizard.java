package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.ArrayList;
import java.util.List;

import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.rpgframework.jfx.wizard.WizardPageGenerator;

import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;

/**
 * @author stefa
 *
 */
public class GenerationWizard extends Wizard {
	
	private GeneratorWrapper wrapper;
	
	private WizardPageGenerator<Shadowrun6Character, CommonSR6CharacterGenerator> chargen;
//	private WizardPageProfiles profiles;
	private WizardPageMetatype race;
//	private WizardPageCulture culture;
//	private WizardPageAttributes attrib;

	//-------------------------------------------------------------------
	public GenerationWizard(GeneratorWrapper charGen) {
		setTitle("Hello");
		this.wrapper = charGen;
		charGen.runProcessors();
		
		initPages();
		initInteractivtiy();
		refresh();
	}
	
	//-------------------------------------------------------------------
	private List<WizardPage> getPageList() {
		List<WizardPage> ret = new ArrayList<>();
		for (WizardPageType type : wrapper.getWrapped().getWizardPages()) {
			switch (type) {
//			case ATTRIBUTES : ret.add( attrib); break;
//			case CULTURE    : ret.add(culture); break;
			case METATYPE   : ret.add(   race); break;
//			case RECOMMENDER: ret.add(profiles); break;
			default:
				logger.error("Unsupported page type "+type);
			}
		}
		return ret;
	}
	
	//-------------------------------------------------------------------
	private void initPages() {
		chargen= new WizardPageGenerator(this, wrapper, CharacterGeneratorRegistry.getGenerators());
		race   = new WizardPageMetatype(this, wrapper);
//		culture= new WizardPageCulture(this, wrapper);
//		attrib = new WizardPageAttributes(this, wrapper.getAttributeController());
//		profiles=new WizardPageProfiles(this, wrapper.getWrapped(), new AutoGenerator(wrapper.getWrapped()));
		
		getPages().add(chargen);		
		getPages().addAll(getPageList());
	}
	
	//-------------------------------------------------------------------
	private void initInteractivtiy() {
		wrapper.addListener( (type, param) -> {
			logger.info("Received "+type);
			if (type==BasicControllerEvents.GENERATOR_CHANGED) {
				getPages().retainAll(chargen);
				getPages().addAll(getPageList());
			}
			
//			attrib.refresh();
			// Update buttons
			refresh();
		});		
		
		canBeFinishedCallback = (wizard) -> wrapper.canBeFinished(); // new Callback<Wizard, Boolean>() {
//
//			@Override
//			public Boolean call(Wizard arg0) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
	}
	
}
