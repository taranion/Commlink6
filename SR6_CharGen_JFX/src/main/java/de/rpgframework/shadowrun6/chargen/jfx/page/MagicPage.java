package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.requirements.Requirement;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.chargen.jfx.pane.SpellDescriptionPane;
import de.rpgframework.shadowrun.chargen.jfx.section.MetamagicOrEchoSection;
import de.rpgframework.shadowrun.chargen.jfx.section.RitualSection;
import de.rpgframework.shadowrun.chargen.jfx.section.SpellSection;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.AdeptPowerSection;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class MagicPage extends Page {

	private final static Logger logger = System.getLogger(MagicPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private AdeptPowerSection secAdeptPowers;
	private SpellSection<SR6Spell> secSpells;
	private MetamagicOrEchoSection secMeta;
	private RitualSection secRituals;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public MagicPage() {
		super(ResourceI18N.get(RES, "page.magic.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initPowers();
		initSpells();
		initMetamagic();
		initRituals();
	}
	
	//-------------------------------------------------------------------
	private void initPowers() {
		secAdeptPowers = new AdeptPowerSection(ResourceI18N.get(RES, "page.magic.section.adeptpowers"));
		secAdeptPowers.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secAdeptPowers, 4);
		FlexGridPane.setMinHeight(secAdeptPowers, 6);
		FlexGridPane.setMediumWidth(secAdeptPowers, 5);
		FlexGridPane.setMediumHeight(secAdeptPowers, 6);
	}
	
	//-------------------------------------------------------------------
	private void initSpells() {
		secSpells = new SpellSection<SR6Spell>(
				ResourceI18N.get(RES, "page.magic.section.spells"),
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault())
				);
		secSpells.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secSpells, 4);
		FlexGridPane.setMinHeight(secSpells, 6);
		FlexGridPane.setMediumWidth(secSpells, 5);
		FlexGridPane.setMediumHeight(secSpells, 8);
	}
	
	//-------------------------------------------------------------------
	private void initMetamagic() {
		secMeta = new MetamagicOrEchoSection(
				ResourceI18N.get(RES, "page.magic.section.metamagic"),
				r -> Shadowrun6Tools.getRequirementString((Requirement)r, Locale.getDefault()), 
				MetamagicOrEcho.Type.METAMAGIC
				);
		secMeta.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secMeta, 4);
		FlexGridPane.setMinHeight(secMeta, 6);
		FlexGridPane.setMediumWidth(secMeta, 5);
		FlexGridPane.setMediumHeight(secMeta, 8);
	}
	
	//-------------------------------------------------------------------
	private void initRituals() {
		secRituals = new RitualSection(
				ResourceI18N.get(RES, "page.magic.section.rituals"),
				r -> Shadowrun6Tools.getRequirementString(r, Locale.getDefault())
				);
		secRituals.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secRituals, 4);
		FlexGridPane.setMinHeight(secRituals, 6);
		FlexGridPane.setMediumWidth(secRituals, 5);
		FlexGridPane.setMediumHeight(secRituals, 6);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secAdeptPowers, secSpells, secMeta, secRituals);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		secSpells.showHelpForProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				logger.log(Level.INFO, "Show spell "+n);
				SpellDescriptionPane pane = new SpellDescriptionPane();
				pane.setData(n.getModifyable());
				layout.setOptional(pane);
				layout.setTitle(n.getModifyable().getName());
			}
		});
		secAdeptPowers.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
		secMeta.showHelpForProperty().addListener( (ov,o,n) -> showDescription((MetamagicOrEchoValue)n));
		secRituals.showHelpForProperty().addListener( (ov,o,n) -> showDescription((RitualValue)n));
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		
		secAdeptPowers.updateController(ctrl);
		secSpells.updateController(ctrl);
		secMeta.updateController(ctrl);
		secRituals.updateController(ctrl);
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secAdeptPowers.refresh();
		secSpells.refresh();
		secMeta.refresh();
	}

}
