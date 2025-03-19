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
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.Shadowrun6Character;
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
	private Label lbEssenceHoleArt;
	private Label lbEssenceHole;
	private Label lbEssenceLost;
	private Label lbEssenceRemain;

	private NumericalValueField<Quality,QualityValue> nfAcclimation;
	private NumericalValueField<MetamagicOrEcho,MetamagicOrEchoValue> nfTranshumanism;

	private GridPane grid;

	private SR6CharacterController control;
	private Shadowrun6Character model;
	private ObjectProperty<ComplexDataItem> showHelpFor = new SimpleObjectProperty<>();

	private static Quality acclimQual = Shadowrun6Core.getItem(SR6Quality.class, "augmentation_acclimation");
	private static MetamagicOrEcho transhum = Shadowrun6Core.getItem(MetamagicOrEcho.class, "transhumanism");

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
		nfAcclimation   = new NumericalValueField<Quality,QualityValue>( ()-> {
				QualityValue val = model.getQuality(acclimQual.getId());
				return (val==null)?(new QualityValue(acclimQual,0)):val;
				});
		nfTranshumanism = new NumericalValueField<MetamagicOrEcho,MetamagicOrEchoValue>( () -> {
			MetamagicOrEchoValue val = model.getMetamagicOrEcho(transhum.getId());
			return (val==null)?(new MetamagicOrEchoValue(transhum)):val;
			});
		lbEssenceHole   = new Label("?");
		lbEssenceHoleArt= new Label("?");
		lbEssenceLost   = new Label("?");
		lbEssenceRemain = new Label("?");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		hdAcclimation   = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.acclimation"));
		hdTranshumanism = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.transhumanism"));
		Label hdEssenceHole   = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essHole"));
		Label hdEssenceHoleArt= new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essHoleArtificial"));
		Label hdEssenceLost   = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essLost"));
		Label hdEssenceRemain = new Label(ResourceI18N.get(RES, "page.augmentation.section.essence.essRemain"));
		grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(5);
		grid.add(hdAcclimation   , 0, 0);
		grid.add(nfAcclimation   , 1, 0);
		grid.add(hdTranshumanism , 0, 1);
		grid.add(nfTranshumanism , 1, 1);
		grid.add(hdEssenceHoleArt, 0, 2);
		grid.add(lbEssenceHoleArt, 1, 2);
		grid.add(hdEssenceHole  , 0, 3);
		grid.add(lbEssenceHole  , 1, 3);
		grid.add(hdEssenceLost  , 0, 4);
		grid.add(lbEssenceLost  , 1, 4);
		grid.add(hdEssenceRemain, 0, 5);
		grid.add(lbEssenceRemain, 1, 5);
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
		nfAcclimation.setController(control.getQualityController());
		nfTranshumanism.setController(control.getMetamagicOrEchoController());

		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	public void refresh() {
		//Acclimation
		NumericalValueController<Quality, QualityValue> qCtrl = control.getQualityController();
		QualityValue qVal = model.getQuality(acclimQual.getId());
		if (acclimQual!=null) {
			if (qVal==null) {
				qVal = new QualityValue(acclimQual, 0);
			}
//			nfAcclimation.setData(qVal, new SimpleObjectProperty<NumericalValueController<Quality, QualityValue>>(qCtrl));
			nfAcclimation.setUserData(acclimQual);
		}
		nfAcclimation.refresh();

		// Transhumanism
		MetamagicOrEcho transhum = Shadowrun6Core.getItem(MetamagicOrEcho.class, "transhumanism");
		NumericalValueController<MetamagicOrEcho, MetamagicOrEchoValue> tCtrl = control.getMetamagicOrEchoController();
		MetamagicOrEchoValue mVal = model.getMetamagicOrEcho("transhumanism");
		if (transhum != null) {
			if (mVal == null) {
				mVal = new MetamagicOrEchoValue(transhum);
			}
//			nfTranshumanism.setData(mVal, new SimpleObjectProperty<NumericalValueController<MetamagicOrEcho, MetamagicOrEchoValue>>(tCtrl));
			nfTranshumanism.setUserData(Shadowrun6Core.getItem(MetamagicOrEcho.class, "transhumanism"));
		}
		boolean allowTransh = Boolean.parseBoolean(Shadowrun6Rules.ALLOW_TRANSHUMANISM.getDefaultValue()) && (transhum!=null);
		RuleConfiguration conf =  model.getRuleValue(Shadowrun6Rules.ALLOW_TRANSHUMANISM);
		if (conf!=null)
			allowTransh = Boolean.parseBoolean( conf.getValueString() );
		nfTranshumanism.setVisible(allowTransh);
		hdTranshumanism.setVisible(allowTransh);
		nfTranshumanism.refresh();

		lbEssenceLost.setText(String.valueOf( ((double)model.getEssenceCost())/1000.0));
		lbEssenceHole.setText(String.valueOf( ((double)model.getEssenceHoleUnused())/1000.0));
		// Essence
		AttributeValue<ShadowrunAttribute> essVal = model.getAttribute(ShadowrunAttribute.ESSENCE);
		AttributeValue<ShadowrunAttribute> essHol = model.getAttribute(ShadowrunAttribute.ESSENCE_HOLE);
		if (essVal != null) {
			lbEssenceRemain.setText(String.valueOf(essVal.getModifiedValue() / 1000.0));
		}
		if (essHol!=null) {
			lbEssenceHoleArt.setText(String.valueOf( essHol.getModifiedValue()/1000.0));
		} else {
			lbEssenceHoleArt.setText("-");
		}
	}
}
