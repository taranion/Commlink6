package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.section.PersonaSection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.PersonaConfigPane;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.items.SR6ItemFlag;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SR6PersonaSection extends PersonaSection<Shadowrun6Character> {

	protected static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6PersonaSection.class.getPackageName()+".Section");

	private PersonaConfigPane paneDevices;

	private Label lbDefenseRating;
	private Label lbDefensePool;

	//-------------------------------------------------------------------
	public SR6PersonaSection(String title) {
		super(title);

		initDeviceSelector();
		initRuleSpecific();
	}

	//-------------------------------------------------------------------
	private void initDeviceSelector() {
		paneDevices = new PersonaConfigPane();

		super.setDeviceSelectNode(paneDevices);
	}

	//-------------------------------------------------------------------
	private void initRuleSpecific() {
		lbDefenseRating   = new Label();
		lbDefensePool     = new Label("?");
		Label hdDefRating = new Label(ResourceI18N.get(RES, "section.persona.defenseRating"));
		Label hdDefPool   = new Label(ResourceI18N.get(RES, "section.persona.defensePool"));
		GridPane ruleSpec = new GridPane();
		ruleSpec.setHgap(10);
		ruleSpec.setVgap(5);
		ruleSpec.add(hdDefRating  , 0, 0);
		ruleSpec.add(lbDefenseRating, 1, 0);
		ruleSpec.add(hdDefPool    , 0, 1);
		ruleSpec.add(lbDefensePool, 1, 1);

		setRuleSpecificNode(ruleSpec);
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		paneDevices.updateController(ctrl);
		super.updateController(ctrl);
	}

	//-------------------------------------------------------------------
	public void refresh() {
		paneDevices.refresh();
		List<CarriedItem<ItemTemplate>> asItems = new ArrayList<>();
		List<CarriedItem<ItemTemplate>> dfItems = new ArrayList<>();
		CarriedItem<ItemTemplate> primaryAS = null;
		CarriedItem<ItemTemplate> primaryDF = null;
		for (CarriedItem<ItemTemplate> item : model.getCarriedItemsRecursive()) {
			if (!item.hasFlag(SR6ItemFlag.MATRIX_DEVICE))
				continue;

			if (item.hasAttribute(SR6ItemAttribute.ATTACK)) {
				asItems.add(item);
				if (item.hasFlag(SR6ItemFlag.PRIMARY))
					primaryAS = item;
			} else if (item.hasAttribute(SR6ItemAttribute.DATA_PROCESSING)) {
				dfItems.add(item);
				if (item.hasFlag(SR6ItemFlag.PRIMARY))
					primaryDF = item;
			} else {
				logger.log(Level.WARNING, "Found matrix item with either ATTACK nor DATA_PROCESSING: {0}", item);
			}
		}
		logger.log(Level.DEBUG, "refresh with asItems={0} and dfItems={1}", asItems, dfItems);
		logger.log(Level.DEBUG, "refresh with primaryAS={0} and primaryDF={1}", primaryAS, primaryDF);


		// Update ASDF values
		if (primaryAS!=null) {
			lbAttack.setText( String.valueOf(primaryAS.getAsValue(SR6ItemAttribute.ATTACK).getModifiedValue()));
			lbSleaze.setText( String.valueOf(primaryAS.getAsValue(SR6ItemAttribute.SLEAZE).getModifiedValue()));
		} else {
			lbAttack.setText("-"); lbSleaze.setText("-");
		}
		if (primaryDF!=null) {
			lbDatap.setText( String.valueOf(primaryDF.getAsValue(SR6ItemAttribute.DATA_PROCESSING).getModifiedValue()));
			lbFirew.setText( String.valueOf(primaryDF.getAsValue(SR6ItemAttribute.FIREWALL).getModifiedValue()));
		} else {
			lbDatap.setText("-"); lbFirew.setText("-");
		}

		// Defense rating
		AttributeValue<ShadowrunAttribute> aVal = model.getAttribute(ShadowrunAttribute.DEFENSE_RATING_MATRIX);
		if (aVal==null)
			lbDefenseRating.setText("?");
		else {
			lbDefenseRating.setText( String.valueOf(aVal.getModifiedValue()) );
			if (aVal.getPool()!=null) {
				lbDefenseRating.setText( aVal.getPool().toString() );
				lbDefenseRating.setTooltip(new Tooltip(aVal.getPool().toExplainString()));
			} else {
				lbDefenseRating.setTooltip(null);
			}
		}
	}

}
