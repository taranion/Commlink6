package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Wizard;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.MetamagicOrEcho;
import de.rpgframework.shadowrun.MetamagicOrEchoValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;

/**
 * @author prelle
 *
 */
public class SR6WizardPageAnimalism extends AWizardPageMetaOrEcho {

	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageBornThisWay.class.getPackageName()+".SR6WizardPages");

	private final static Logger logger = System.getLogger(SR6WizardPageAnimalism.class.getPackageName()+".animalism");

	//-------------------------------------------------------------------
	public SR6WizardPageAnimalism(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard, charGen);
		setTitle(ResourceI18N.get(RES, "page.animalism.title"));
	}

	//-------------------------------------------------------------------
	protected void initComponents() {
		super.initComponents();
		selection = new ComplexDataItemControllerNode<>(((SR6CharacterController)charGen).getAnimalismController());
		selection.setAvailablePlaceholder(ResourceI18N.get(RES, "page.animalism.placeholder.available"));
		selection.setSelectedPlaceholder(ResourceI18N.get(RES, "page.animalism.placeholder.selected"));
//		selection.setFilterNode(new QualityFilterNode(RES, selection, QualityType.NORMAL));
		selection.setOptionCallback(new ChoiceSelectorDialog<>( ((SR6CharacterController)charGen).getAnimalismController()));
//		selection.setSelectedFilter(qv -> qv.getModifyable().getType()==QualityType.NORMAL);
		selection.setAvailableCellFactory(lv -> new ComplexDataItemListCell<MetamagicOrEcho>( () -> selection.getController(), Shadowrun6Tools.requirementResolver(Locale.getDefault())));
		selection.setSelectedCellFactory(lv -> new ComplexDataItemValueListCell<MetamagicOrEcho,MetamagicOrEchoValue>( () -> selection.getController()));

		bxDescription = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()));
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.AWizardPageMetaOrEcho#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		lbGrade.setText( String.valueOf(((SR6CharacterController)charGen).getAnimalismController().getGrade()));

		if (charGen.getModel().getBodytype()==BodyType.SHAPESHIFTER) {
			logger.log(Level.DEBUG, "Animalism allowed enable page");
			activeProperty().set(true);
		} else {
			logger.log(Level.DEBUG, "Not a shifter - disable page");
			activeProperty().set(false);
		}
	}

	//-------------------------------------------------------------------
	public void pageVisited() {
		refresh();
	}

}
