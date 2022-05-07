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
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.chargen.jfx.pane.SpellDescriptionPane;
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
	}
	
	//-------------------------------------------------------------------
	private void initPowers() {
		secAdeptPowers = new AdeptPowerSection(ResourceI18N.get(RES, "page.magic.section.adeptpowers"));
		secAdeptPowers.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secAdeptPowers, 4);
		FlexGridPane.setMinHeight(secAdeptPowers, 6);
		FlexGridPane.setMediumWidth(secAdeptPowers, 8);
		FlexGridPane.setMediumHeight(secAdeptPowers, 4);
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
		FlexGridPane.setMediumWidth(secSpells, 8);
		FlexGridPane.setMediumHeight(secSpells, 4);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secAdeptPowers, secSpells);
		
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
//		secKnowl.selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
//		secLang .selectedSkillProperty().addListener( (ov,o,n) -> showDescription(n));
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox<Quality>( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
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
		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secAdeptPowers.refresh();
		secSpells.refresh();
	}

}
