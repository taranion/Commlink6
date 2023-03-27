package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.RuleInterpretation;
import de.rpgframework.jfx.wizard.WizardPageGenerator;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.gen.WizardPageType;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageLifestyles;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageRituals;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageAdeptPowers;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageContacts;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageName;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPagePriority;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSINs;
import de.rpgframework.shadowrun.chargen.jfx.wizard.WizardPageSpells;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.CharacterGeneratorRegistry;
import de.rpgframework.shadowrun6.chargen.gen.CommonSR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import javafx.util.Callback;

/**
 * @author prelle
 * @param <WizardPageAdeptPowers>
 *
 */
public class GenerationWizard extends Wizard implements ControllerListener {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageQualities.class.getPackageName()+".SR6WizardPages");

	private final static Logger logger = System.getLogger(GenerationWizard.class.getName());

	private GeneratorWrapper wrapper;

	private WizardPageGenerator<ShadowrunAttribute ,Shadowrun6Character, CommonSR6CharacterGenerator> chargen;
//	private WizardPageProfiles profiles;
	private WizardPagePriority<SR6Skill, SR6SkillValue, Shadowrun6Character, SR6PrioritySettings> prios;
	private SR6WizardPageMetatype race;
	private WizardPageLifePath1 lifepath1;
	private SR6WizardPageDrake drake;
	private SR6WizardPageChangeling surge;
	private SR6WizardPageMagicOrResonance magic;
	private SR6WizardPageQualities qualities;
	private SR6WizardPageAttributes attrib;
	private SR6WizardPageSkills skills;
	private SR6WizardPageSpells spells;
	private SR6WizardPageRituals rituals;
	private WizardPageAdeptPowers powers;
	private SR6WizardPageComplexForms complexForms;
	private SR6WizardPageMetaOrEcho metaEchoes;
	private SR6WizardPageGear gear;
	private WizardPageName<SR6Skill, SR6SkillValue, Shadowrun6Character> name;
	private WizardPageContacts contacts;
	private WizardPageSINs sins;
	private AWizardPageLifestyles lifestyles;
	private Function<Class<CommonSR6CharacterGenerator>,String[]> nameGetter = gen -> {
		String name = SR6CharacterGenerator.RES.getString("chargen."+gen.getSimpleName());
		String desc = SR6CharacterGenerator.RES.getString("chargen."+gen.getSimpleName()+".desc");
		return new String[]{name,desc};
	};

	//-------------------------------------------------------------------
	public GenerationWizard(GeneratorWrapper charGen) {
		super(CloseType.CANCEL, CloseType.PREVIOUS, CloseType.RANDOMIZE, CloseType.NEXT, CloseType.FINISH);
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
			case PRIORITIES   : ret.add(    prios); break;
			case METATYPE     : ret.add(     race); break;
			case SR6_LIFEPATH1:ret.add(lifepath1); break;
//			case DRAKE        : ret.add(    drake); break;
			case SURGE        : ret.add(    surge); break;
			case MAGIC_OR_RESONANCE: ret.add(magic); break;
			case QUALITIES    : ret.add(qualities); break;
			case ATTRIBUTES   : ret.add(   attrib); break;
			case SKILLS       : ret.add(   skills); break;
			case SPELLS       : ret.add(   spells); break;
			case RITUALS      : ret.add(  rituals); break;
			case POWERS       : ret.add(   powers); break;
			case COMPLEX_FORMS: ret.add(complexForms); break;
			case METAECHO     : ret.add(metaEchoes); break;
			case GEAR         : ret.add(   gear); break;
			case CONTACTS     : ret.add(contacts); break;
			case SIN_LICENSE  : ret.add(   sins); break;
			case LIFESTYLE    : ret.add(lifestyles); break;
			case NAME         : ret.add(   name); break;
			default:
				logger.log(Level.ERROR,"Unsupported page type "+type);
			}
		}
		return ret;
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initPages() {
		chargen= new WizardPageGenerator(this, wrapper,
				CharacterGeneratorRegistry.getGenerators(),
				Shadowrun6Core.getItemList(RuleInterpretation.class),
				Shadowrun6Rules.values(),
				nameGetter);
		prios  = new WizardPagePriority<>(this, wrapper.getWrapped(), new SR6PriorityTable( (type,prio) -> Shadowrun6Core.getPriorityTableEntry(type, prio)));
		race   = new SR6WizardPageMetatype(this, wrapper);
		lifepath1 = new WizardPageLifePath1(this, wrapper);
		surge  = new SR6WizardPageChangeling(this, wrapper);
		magic  = new SR6WizardPageMagicOrResonance(this, wrapper) {
			protected void refresh() {
				super.refresh();
				backHeaderCP.setVisible(charGen.getModel().hasCharGenSettings(SR6PointBuySettings.class));
				backHeaderCP.setManaged(charGen.getModel().hasCharGenSettings(SR6PointBuySettings.class));
				backHeaderCP.setValue(charGen.getModel().hasCharGenSettings(SR6PointBuySettings.class)?charGen.getModel().getCharGenSettings(SR6PointBuySettings.class).characterPoints:0	);
			}
		};
		drake     = new SR6WizardPageDrake(this, wrapper);
		qualities = new SR6WizardPageQualities(this, wrapper);
		attrib = new SR6WizardPageAttributes(this, wrapper.getWrapped());
		skills = new SR6WizardPageSkills(this, wrapper.getWrapped());
		spells = new SR6WizardPageSpells(this, wrapper);
		rituals = new SR6WizardPageRituals(this, wrapper);
		powers = new SR6WizardPageAdeptPowers(this, wrapper);
		complexForms = new SR6WizardPageComplexForms(this, wrapper);
		metaEchoes   = new SR6WizardPageMetaOrEcho(this, wrapper);
		gear   = new SR6WizardPageGear(this, wrapper);
//		profiles=new WizardPageProfiles(this, wrapper.getWrapped(), new AutoGenerator(wrapper.getWrapped()));
		contacts = new SR6WizardPageContacts(this, wrapper.getWrapped());
		sins   = new WizardPageSINs(this, wrapper);
		lifestyles = new AWizardPageLifestyles(this, wrapper);
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

				CloseType type = FlexibleApplication.getInstance().showAlertAndCall(AlertType.CONFIRMATION, ResourceI18N.get(RES, "confirm.cancel.title"), ResourceI18N.get(RES, "confirm.cancel.mess"));
				logger.log(Level.WARNING, "User confirmed cancellation: "+type);
				if (type==CloseType.OK || type==CloseType.YES) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		});

		addExtraButton(CloseType.RANDOMIZE, ev -> {
			if (getCurrentPage().getOnExtraActionHandler()!=null) {
				getCurrentPage().getOnExtraActionHandler().accept(CloseType.RANDOMIZE);
			} else {
				logger.log(Level.WARNING, "No handler for RANDOMIZE on page {0}",getCurrentPage());
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

		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			for (WizardPage page : getPages()) {
				if (page instanceof ControllerListener && !wrapper.hasListener((ControllerListener) page)) {
					logger.log(Level.WARNING, "Page {0} is a ControllerListener but not registered", page.getClass());
					((ControllerListener)page).handleControllerEvent(type, param);
				}
			}
		}

//		logger.log(Level.DEBUG, "Pages now");
//		for (WizardPage page : getPages()) {
//			logger.log(Level.DEBUG, "- "+page);
//		}
		// Update buttons
		refresh();
	}

}
