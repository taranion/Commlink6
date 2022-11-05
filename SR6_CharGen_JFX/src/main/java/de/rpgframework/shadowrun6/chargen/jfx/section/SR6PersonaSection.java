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
import de.rpgframework.shadowrun.chargen.jfx.section.PersonaSection;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
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
	
	private ChoiceBox<CarriedItem<ItemTemplate>> cbASDevice;
	private ChoiceBox<CarriedItem<ItemTemplate>> cbDFDevice;
	
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
		cbASDevice = new ChoiceBox<>();
		cbDFDevice = new ChoiceBox<>();
		cbASDevice.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public String toString(CarriedItem<ItemTemplate> item) { return (item!=null)?item.getNameWithoutRating():"-"; }
			public CarriedItem<ItemTemplate> fromString(String string) { return null; }
		});
		cbDFDevice.setConverter(new StringConverter<CarriedItem<ItemTemplate>>() {
			public String toString(CarriedItem<ItemTemplate> item) { return (item!=null)?item.getNameWithoutRating():"-"; }
			public CarriedItem<ItemTemplate> fromString(String string) { return null; }
		});
		
		Label hdAccess = new Label(ResourceI18N.get(super.RES, "section.persona.device.access"));
		Label hdDeck   = new Label(ResourceI18N.get(super.RES, "section.persona.device.deck"));
		hdAccess.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		hdDeck.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		
		GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);
		grid.add(hdAccess  , 0, 0);
		grid.add(cbASDevice, 1, 0);
		grid.add(hdDeck    , 0, 1);
		grid.add(cbDFDevice, 1, 1);
		GridPane.setFillWidth(cbASDevice, true);
		GridPane.setFillWidth(cbDFDevice, true);
		
		super.setDeviceSelectNode(grid);
		
		cbASDevice.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) o.removeFlag(SR6ItemFlag.PRIMARY);
			if (n!=null) n.addFlag(SR6ItemFlag.PRIMARY);
			logger.log(Level.INFO, "Set primary AS device to {0}", n);
			if (control!=null)
				control.runProcessors();
		});
		cbDFDevice.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (o!=null) o.removeFlag(SR6ItemFlag.PRIMARY);
			if (n!=null) n.addFlag(SR6ItemFlag.PRIMARY);
			logger.log(Level.INFO, "Set primary DF device to {0}", n);
			if (control!=null)
				control.runProcessors();
		});
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
	public void refresh() {
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
		logger.log(Level.WARNING, "refresh with asItems={0} and dfItems={1}", asItems, dfItems);
		logger.log(Level.WARNING, "refresh with primaryAS={0} and primaryDF={1}", primaryAS, primaryDF);
		
		// Update, if necessary
		if (!cbASDevice.getItems().containsAll(asItems)) {
			cbASDevice.getItems().setAll(asItems);
		}
		if (!cbDFDevice.getItems().containsAll(dfItems)) {
			cbDFDevice.getItems().setAll(dfItems);
		}
		cbASDevice.setValue(primaryAS);
		cbDFDevice.setValue(primaryDF);
		
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
