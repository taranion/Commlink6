package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import com.itextpdf.text.pdf.PdfPageEventHelper;

import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.jfx.wizard.WizardPageGenerator;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAttributes;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageName;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPagePriority;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
import de.rpgframework.shadowrun6.chargen.gen.Shadowrun6Rules;

/**
 * @author prelle
 *
 */
public class GenerationWizard extends Wizard {

	private final static Logger logger = LogManager.getLogger(GenerationWizard.class);
	
	private GeneratorWrapper wrapper;
	
	private WizardPageGenerator<ShadowrunAttribute ,Shadowrun6Character, CommonSR6CharacterGenerator> chargen;
//	private WizardPageProfiles profiles;
	private WizardPagePriority<SR6Skill, SR6SkillValue, Shadowrun6Character, SR6PrioritySettings> prios;
	private WizardPageMetatype race;
	private SR6WizardPageChangeling surge;
	private WizardPageAttributes attrib;
	private WizardPageName<SR6Skill, SR6SkillValue, Shadowrun6Character> name;
	private Function<Class<CommonSR6CharacterGenerator>,String[]> nameGetter = gen -> {
		String name = SR6CharacterGenerator.RES.getString("chargen."+gen.getSimpleName());
		String desc = SR6CharacterGenerator.RES.getString("chargen."+gen.getSimpleName()+".desc");
		return new String[]{name,desc};
	}; 

	//-------------------------------------------------------------------
	public GenerationWizard(GeneratorWrapper charGen) {
		setTitle("Unreplaced Wizard Title");
		setPlain(false);
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
			case PRIORITIES : ret.add(  prios); break;
			case METATYPE   : ret.add(   race); break;
			case SURGE      : ret.add(  surge); break;
			case ATTRIBUTES : ret.add( attrib); break;
//			case CULTURE    : ret.add(culture); break;
//			case RECOMMENDER: ret.add(profiles); break;
			case NAME       : ret.add(   name); break;
			default:
				logger.error("Unsupported page type "+type);
			}
		}
		return ret;
	}
	
	//-------------------------------------------------------------------
	private void initPages() {
		chargen= new WizardPageGenerator(this, wrapper, 
				CharacterGeneratorRegistry.getGenerators(),
				Shadowrun6Core.getItemList(RuleInterpretation.class),
				Shadowrun6Rules.values(),
				nameGetter);
		prios  = new WizardPagePriority<>(this, wrapper.getWrapped(), new SR6PriorityTable( (type,prio) -> Shadowrun6Core.getPriorityTableEntry(type, prio)));
		race   = new WizardPageMetatype(this, wrapper);
		surge  = new SR6WizardPageChangeling(this, wrapper);
//		culture= new WizardPageCulture(this, wrapper);
		attrib = new SR6WizardPageAttributes(this, wrapper.getWrapped());
//		profiles=new WizardPageProfiles(this, wrapper.getWrapped(), new AutoGenerator(wrapper.getWrapped()));
		name   = new WizardPageName<>(this, wrapper);
		
		getPages().add(chargen);
		getPages().addAll(getPageList());
		logger.warn("Pages: "+getPages());
	}
	
	//-------------------------------------------------------------------
	private void initInteractivtiy() {
		wrapper.addListener( (type, param) -> {
			logger.info("Received "+type);
			if (type==BasicControllerEvents.GENERATOR_CHANGED) {
				getPages().retainAll(chargen);
				logger.info("Add pages for new generator");
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
