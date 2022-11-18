package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.Section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.chargen.RuleConfiguration;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.jfx.NumericalValueField;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Rules;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author prelle
 *
 */
public class EssenceSection extends Section {

	private final static Logger logger = System.getLogger(GearSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());

	private Label hdAcclimation;
	private Label hdTranshumanism;
	private Label lbEssenceHole;
	private Label lbEssenceLost;
	private Label lbEssenceRemain;
	
	private NumericalValueField<Quality,QualityValue> nfAcclimation;
	private NumericalValueField<MetamagicOrEcho,MetamagicOrEchoValue> nfTranshumanism;
	
	private GridPane grid;

	private SR6CharacterController control;
	private ShadowrunCharacter model;
	private ObjectProperty<ComplexDataItem> showHelpFor = new SimpleObjectProperty<>();
	
	
	//-------------------------------------------------------------------
	public EssenceSection(String title) {
		super.setId(title);
		setTitle(title);
		initComponents();
		initLayout();
		initInteractivity();
		setContent(grid);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		nfAcclimation   = new NumericalValueField<Quality,QualityValue>();
		nfTranshumanism = new NumericalValueField<MetamagicOrEcho,MetamagicOrEchoValue>();
		lbEssenceHole   = new Label("?");
		lbEssenceLost   = new Label("?");
		lbEssenceRemain = new Label("?");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		hdAcclimation   = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.acclimation"));
		hdTranshumanism = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.transhumanism"));
		Label hdEssenceHole   = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essHole"));
		Label hdEssenceLost   = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essLost"));
		Label hdEssenceRemain = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essRemain"));
		grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(5);
		grid.add(hdAcclimation, 0, 0);
		grid.add(nfAcclimation, 1, 0);
		grid.add(hdTranshumanism, 0, 1);
		grid.add(nfTranshumanism, 1, 1);
		grid.add(hdEssenceHole  , 0, 2);
		grid.add(lbEssenceHole  , 1, 2);
		grid.add(hdEssenceLost  , 0, 3);
		grid.add(lbEssenceLost  , 1, 3);
		grid.add(hdEssenceRemain, 0, 4);
		grid.add(lbEssenceRemain, 1, 4);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		nfAcclimation.setOnAction(ev -> showHelpFor.set((ComplexDataItem) nfAcclimation.getUserData()));
		nfTranshumanism.setOnAction(ev -> showHelpFor.set((ComplexDataItem) nfTranshumanism.getUserData()));
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<ComplexDataItem> showHelpForProperty() {
		return showHelpFor;
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		logger.log(Level.DEBUG, "updateController");
		this.control = ctrl;
		model = ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	public void refresh() {
		logger.log(Level.DEBUG, "refresh");
		
		//Acclimation
		Quality acclimQual = Shadowrun6Core.getItem(Quality.class, "augmentation_acclimation"); 
		NumericalValueController<Quality, QualityValue> qCtrl = control.getQualityController();		
		QualityValue qVal = model.getQuality("augmentation_acclimation");
		if (acclimQual!=null) {
			if (qVal==null) {
				qVal = new QualityValue(acclimQual, 0);
			}
			nfAcclimation.setData(qVal, new SimpleObjectProperty<NumericalValueController<Quality, QualityValue>>(qCtrl));
			nfAcclimation.setUserData(acclimQual);
		}
		
		// Transhumanism
		MetamagicOrEcho transhum = Shadowrun6Core.getItem(MetamagicOrEcho.class, "transhumanism");
		NumericalValueController<MetamagicOrEcho, MetamagicOrEchoValue> tCtrl = control.getMetamagicOrEchoController();
		MetamagicOrEchoValue mVal = model.getMetamagicOrEcho("transhumanism");
		if (transhum != null) {
			if (mVal == null) {
				mVal = new MetamagicOrEchoValue(transhum);
			}
			nfTranshumanism.setData(mVal, new SimpleObjectProperty<NumericalValueController<MetamagicOrEcho, MetamagicOrEchoValue>>(tCtrl));
			nfTranshumanism.setUserData(Shadowrun6Core.getItem(MetamagicOrEcho.class, "transhumanism"));
		}
		boolean allowTransh = Boolean.parseBoolean(Shadowrun6Rules.ALLOW_TRANSHUMANISM.getDefaultValue()) && (transhum!=null);
		RuleConfiguration conf =  model.getRuleValue(Shadowrun6Rules.ALLOW_TRANSHUMANISM);
		if (conf!=null)
			allowTransh = Boolean.parseBoolean( conf.getValueString() );
		nfTranshumanism.setVisible(allowTransh);
		hdTranshumanism.setVisible(allowTransh);
		
		lbEssenceLost.setText(String.valueOf( ((double)model.getEssenceCost())/1000.0));
		// Essence 
		AttributeValue<ShadowrunAttribute> val = model.getAttribute(ShadowrunAttribute.ESSENCE);
		if (val != null) {
			if (val != null)
				lbEssenceRemain.setText(String.valueOf(val.getModifiedValue() / 1000.0));
			// Essence hole
			val = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
			if (val != null)
				lbEssenceHole.setText(String.valueOf(val.getModifiedValue() / 1000.0));
		}
	}	
}
