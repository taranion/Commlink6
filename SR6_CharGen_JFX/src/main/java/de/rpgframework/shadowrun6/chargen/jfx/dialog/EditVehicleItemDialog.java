package de.rpgframework.shadowrun6.chargen.jfx.dialog;

import java.lang.System.Logger.Level;

import org.prelle.javafx.ScreenManagerProvider;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.ModSlotConfigPane;
import de.rpgframework.shadowrun6.chargen.jfx.pane.ModSlotConfigPane.ConversionEvent;
import de.rpgframework.shadowrun6.items.ItemTemplate;

/**
 * @author Stefan Prelle
 *
 */
public class EditVehicleItemDialog extends EditCarriedItemDialog {

	private ModSlotConfigPane cpSlots;

	//--------------------------------------------------------------------
	public EditVehicleItemDialog(SR6CharacterController ctrl, CarriedItem<ItemTemplate> data) {
		super(ctrl, data);
		cpSlots.setConfig(data);
	}

	//--------------------------------------------------------------------
	protected void initCompoments() {
		super.initCompoments();

		cpSlots = new ModSlotConfigPane();
		cpSlots.setOnConversion(new ModSlotConfigPane.ModSlotDefaultHandler(cpSlots) {
			public void handle(ConversionEvent event) {
				super.handle(event);
				logger.log(Level.INFO, "item changed");
				refresh();
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * Position all elements that are NOT AvailableSlots
	 */
	protected void positionNonSlots() {
		super.positionNonSlots();

		view.setOverrideComponent(11, cpSlots);
		cpSlots.refresh();

	}

}
