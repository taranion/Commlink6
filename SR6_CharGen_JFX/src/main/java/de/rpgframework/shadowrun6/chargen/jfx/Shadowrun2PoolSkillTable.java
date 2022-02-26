package de.rpgframework.shadowrun6.chargen.jfx;

import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.WindowMode;

import de.rpgframework.jfx.rules.AttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.SkinProperties;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;

/**
 * @author prelle
 *
 */
public abstract class Shadowrun2PoolSkillTable extends Control implements ResponsiveControl {
	
	private SR6CharacterGenerator controller;
	private ObjectProperty<AttributeTable.Mode> mode = new SimpleObjectProperty<>(AttributeTable.Mode.CAREER);
	/**
	 * Use expert mode for priority generators
	 */
	private BooleanProperty useExpertMode;
	protected BooleanProperty expertModeAvailable;

	//-------------------------------------------------------------------
	public Shadowrun2PoolSkillTable(SR6CharacterGenerator ctrl) {
		controller = ctrl;
		expertModeAvailable = new SimpleBooleanProperty(false);
		useExpertMode = new SimpleBooleanProperty(false);
	}

	//-------------------------------------------------------------------
	public SR6CharacterGenerator getController() { return controller; }

	//-------------------------------------------------------------------
	public ObjectProperty<AttributeTable.Mode> modeProperty() { return mode; }
	public AttributeTable.Mode getMode() { return mode.get(); }
	public Shadowrun2PoolSkillTable setMode(AttributeTable.Mode value) { mode.set(value); return this; }

	//-------------------------------------------------------------------
	public BooleanProperty useExpertModeProperty() { return useExpertMode; }
	public boolean isUseExpertMode() { return useExpertMode.get(); }
	public Shadowrun2PoolSkillTable setUseExpertMode(boolean value) { useExpertMode.set(value); return this; }

	//-------------------------------------------------------------------
	public ReadOnlyBooleanProperty expertModeAvailableProperty() { return expertModeAvailable; }
	public boolean isExpertModeAvailable() { return expertModeAvailable.get(); }

	//-------------------------------------------------------------------
   public void refresh() {
        getProperties().put(SkinProperties.REFRESH, Boolean.TRUE);
    }

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
        getProperties().put(SkinProperties.WINDOW_MODE, Boolean.TRUE);
	}

}
