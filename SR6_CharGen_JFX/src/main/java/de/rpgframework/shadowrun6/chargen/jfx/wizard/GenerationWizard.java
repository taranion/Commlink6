package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.prelle.javafx.skin.WizardSkin;

import com.itextpdf.text.pdf.PdfPageEventHelper;

import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.jfx.wizard.WizardPageGenerator;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAttributes;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageName;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPagePriority;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageMagicOrResonance;
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
import javafx.util.Callback;

/**
 * @author prelle
 *
 */
public class GenerationWizard extends Wizard implements ControllerListener {

	private final static Logger logger = System.getLogger(GenerationWizard.class.getName());
	
	private GeneratorWrapper wrapper;
	
	private WizardPageGenerator<ShadowrunAttribute ,Shadowrun6Character, CommonSR6CharacterGenerator> chargen;
//	private WizardPageProfiles profiles;
	private WizardPagePriority<SR6Skill, SR6SkillValue, Shadowrun6Character, SR6PrioritySettings> prios;
	private WizardPageMetatype race;
	private WizardPageLifePath1 lifepath1;
	private SR6WizardPageChangeling surge;
	private SR6WizardPageMagicOrResonance magic;
	private SR6WizardPageQualities qualities;
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
		
		initPages();
		initInteractivtiy();
		setShowProgress(false);
		refresh();
	}
	
	//-------------------------------------------------------------------
	private List<WizardPage> getPageList() {
		List<WizardPage> ret = new ArrayList<>();
		for (WizardPageType type : wrapper.getWrapped().getWizardPages()) {
			switch (type) {
			case PRIORITIES  : ret.add(    prios); break;
			case METATYPE    : ret.add(     race); break;
			case SR6_LIFEPATH1:ret.add(lifepath1); break;
			case SURGE       : ret.add(    surge); break;
			case MAGIC_OR_RESONANCE: ret.add(magic); break;
			case QUALITIES   : ret.add(qualities); break;
			case ATTRIBUTES  : ret.add(   attrib); break;
//			case RECOMMENDER : ret.add(profiles); break;
			case NAME        : ret.add(   name); break;
			default:
				logger.log(Level.ERROR,"Unsupported page type "+type);
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
		lifepath1 = new WizardPageLifePath1(this, wrapper);
		surge  = new SR6WizardPageChangeling(this, wrapper);
		magic  = new SR6WizardPageMagicOrResonance(this, wrapper);
		qualities = new SR6WizardPageQualities(this, wrapper);
		attrib = new SR6WizardPageAttributes(this, wrapper.getWrapped());
//		profiles=new WizardPageProfiles(this, wrapper.getWrapped(), new AutoGenerator(wrapper.getWrapped()));
		name   = new WizardPageName<>(this, wrapper);
		
		getPages().add(chargen);
		getPages().addAll(getPageList());
		logger.log(Level.WARNING, "Pages: "+getPages());
	}
	
	//-------------------------------------------------------------------
	private void initInteractivtiy() {
		wrapper.addListener(this);
		canBeFinishedCallback = (wizard) -> wrapper.canBeFinished(); // new Callback<Wizard, Boolean>() {
		
		setConfirmCancelCallback(new Callback<Wizard, Boolean>() {

			@Override
			public Boolean call(Wizard param) {
				logger.log(Level.WARNING, "ToDo: ask user to confirm cancellation");
				
				CloseType type = FlexibleApplication.getInstance().showAlertAndCall(AlertType.CONFIRMATION, "Really cancel?", "Do you really want to quit?");
				logger.log(Level.WARNING, "User confirmed cancellation: "+type);
				if (type==CloseType.OK || type==CloseType.YES)
					return Boolean.TRUE;
				return Boolean.FALSE;
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.INFO, "Controller event: "+type);
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			// Remove all pages as event listeners
			for (WizardPage page : getPages()) {
				if (page instanceof ControllerListener) {
					logger.log(Level.INFO, "Remove listening page "+page);
					wrapper.removeListener((ControllerListener) page);
				}
			}
			
			getPages().retainAll(chargen);
			logger.log(Level.INFO, "Add pages for new generator");
			for (WizardPage page : getPageList()) {
				if (page instanceof ControllerListener) {
					logger.log(Level.INFO, "Add listening page "+page);
					wrapper.addListener((ControllerListener) page);
				}
			}
			getPages().addAll(getPageList());
		}
		
		logger.log(Level.INFO, "Pages now");
		for (WizardPage page : getPages()) {
			logger.log(Level.INFO, "- "+page);
		}
//		attrib.refresh();
		// Update buttons
		refresh();
	}

}
