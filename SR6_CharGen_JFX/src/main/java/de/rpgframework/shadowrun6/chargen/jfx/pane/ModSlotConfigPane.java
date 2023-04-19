package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;

import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.ItemAttributeNumericalValue;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class ModSlotConfigPane extends Control {

	private final static Logger logger = System.getLogger(ModSlotConfigPane.class.getPackageName());

	public static enum State {
		PLUS,
		NEUTRAL,
		MINUS
	}

    public static final EventType<ConversionEvent> FROM_CHASSIS_TO_ELECTRONICS =  new EventType<ConversionEvent>(Event.ANY, "FROM_CHASSIS_TO_ELECTRONICS");
    public static final EventType<ConversionEvent> FROM_ELECTRONICS_TO_CHASSIS =  new EventType<ConversionEvent>(Event.ANY, "FROM_ELECTRONICS_TO_CHASSIS");
    public static final EventType<ConversionEvent> FROM_ELECTRONICS_TO_POWER =  new EventType<ConversionEvent>(Event.ANY, "FROM_ELECTRONICS_TO_POWER");
    public static final EventType<ConversionEvent> FROM_POWER_TO_ELECTRONICS =  new EventType<ConversionEvent>(Event.ANY, "FROM_POWER_TO_ELECTRONICS");
    public static final EventType<ConversionEvent> FROM_POWER_TO_CHASSIS =  new EventType<ConversionEvent>(Event.ANY, "FROM_POWER_TO_CHASSIS");
    public static final EventType<ConversionEvent> FROM_CHASSIS_TO_POWER =  new EventType<ConversionEvent>(Event.ANY, "FROM_CHASSIS_TO_POWER");

	@SuppressWarnings("serial")
	public static class ConversionEvent extends Event {
		public ConversionEvent(EventType<ConversionEvent> eventType) {
			super(eventType);
		}

	}

	private ObjectProperty<CarriedItem<ItemTemplate>> config = new SimpleObjectProperty<>();
	private IntegerProperty chassisToElectronics = new SimpleIntegerProperty(0);
	private IntegerProperty electronicsToPower = new SimpleIntegerProperty(0);
	private IntegerProperty powerToChassis = new SimpleIntegerProperty(0);
	private IntegerProperty chassis     = new SimpleIntegerProperty(0);
	private IntegerProperty electronics = new SimpleIntegerProperty(0);
	private IntegerProperty power       = new SimpleIntegerProperty(0);

	private ObjectProperty<State> chassisState = new SimpleObjectProperty<State>(State.NEUTRAL);
	private ObjectProperty<State> electroState = new SimpleObjectProperty<State>(State.NEUTRAL);
	private ObjectProperty<State> powertrState = new SimpleObjectProperty<State>(State.NEUTRAL);

	//-------------------------------------------------------------------
	public ModSlotConfigPane() {
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	public Skin<?> createDefaultSkin() {
		return new ModSlotConfigPaneSkin(this);
	}

	//-------------------------------------------------------------------
    public void fire(EventType<ConversionEvent> type) {
        if (!isDisabled()) {
        	ConversionEvent event = new ConversionEvent(type);
        	onConversion.get().handle(event);
        	if (!event.isConsumed())
        		fireEvent(new ConversionEvent(type));
        }
    }

	//-------------------------------------------------------------------
    public final ObjectProperty<EventHandler<ConversionEvent>> onConversionProperty() { return onConversion; }
    public final void setOnConversion(EventHandler<ConversionEvent> value) { onConversionProperty().set(value); }
    public final EventHandler<ConversionEvent> getOnConversion() { return onConversionProperty().get(); }
    private ObjectProperty<EventHandler<ConversionEvent>> onConversion = new ObjectPropertyBase<EventHandler<ConversionEvent>>(new ModSlotDefaultHandler(this)) {
//        @Override protected void invalidated() {
//            setEventHandler(ConversionEvent.ACTION, get());
//        }

        @Override
        public Object getBean() {
            return ModSlotConfigPane.this;
        }

        @Override
        public String getName() {
            return "onConversion";
        }
    };

	//-------------------------------------------------------------------
    public final ObjectProperty<CarriedItem<ItemTemplate>> configProperty() { return config; }
    public final void setConfig(CarriedItem<ItemTemplate> value) { configProperty().set(value); refresh();}
    public final CarriedItem<ItemTemplate> getConfig() { return configProperty().get(); }

    //-------------------------------------------------------------------
    public final ReadOnlyIntegerProperty chassisToElectronicsProperty() { return chassisToElectronics; }
    public final ReadOnlyIntegerProperty electronicsToPowerProperty() { return electronicsToPower; }
    public final ReadOnlyIntegerProperty powerToChassisProperty() { return powerToChassis; }

    //-------------------------------------------------------------------
    public final ReadOnlyObjectProperty<State> chassisStateProperty() { return chassisState; }
    public final ReadOnlyObjectProperty<State> electronicsStateProperty() { return electroState; }
    public final ReadOnlyObjectProperty<State> powertrainStateProperty() { return powertrState; }

    //-------------------------------------------------------------------
    public final ReadOnlyIntegerProperty chassisProperty() { return chassis; }
    public final ReadOnlyIntegerProperty electronicsProperty() { return electronics; }
    public final ReadOnlyIntegerProperty powerProperty() { return power; }

    //-------------------------------------------------------------------
    public void refresh() {
    	if (config.get()==null) return;
		logger.log(Level.TRACE, "refresh");
    	ItemAttributeNumericalValue<SR6ItemAttribute> attChassis = config.get().getAsValue(SR6ItemAttribute.MODSLOTS_CHASSIS);
    	ItemAttributeNumericalValue<SR6ItemAttribute> attElectro = config.get().getAsValue(SR6ItemAttribute.MODSLOTS_ELECTRONICS);
    	ItemAttributeNumericalValue<SR6ItemAttribute> attPowertr = config.get().getAsValue(SR6ItemAttribute.MODSLOTS_POWERTRAIN);
    	chassis.set(attChassis.getModifiedValue());
    	electronics.set(attElectro.getModifiedValue());
    	power.set(attPowertr.getModifiedValue());
    	if (attChassis.getModifier()<0) chassisState.set(State.MINUS);
    	else if (attChassis.getModifier()>0) chassisState.set(State.PLUS);
    	else chassisState.set(State.NEUTRAL);

    	logger.log(Level.DEBUG,"Chassis {0}",config.get().getAsValue(SR6ItemAttribute.MODSLOTS_CHASSIS));

    	int[] changes = config.get().getModificationSlotChanges();
    	logger.log(Level.DEBUG,"changes {0}",Arrays.toString(changes));
    	chassisToElectronics.set(changes[0]);
    	electronicsToPower.set(changes[1]);
    	powerToChassis.set(changes[2]);

    	if (getSkin()!=null)
    		((ModSlotConfigPaneSkin)getSkin()).refresh();
    }


	//-------------------------------------------------------------------
	public boolean canUse(EventType<ConversionEvent> type) {
		CarriedItem<ItemTemplate> carried = getConfig();
		int[] changes = carried.getModificationSlotChanges();
//		if (type==FROM_CHASSIS_TO_ELECTRONICS) {
//			return changes[0]<=0;
//		} else if (type==FROM_ELECTRONICS_TO_CHASSIS) {
//			changes[0]-=2;
//		} else if (type==FROM_ELECTRONICS_TO_POWER) {
//			changes[1]+=2;
//		} else if (type==FROM_POWER_TO_ELECTRONICS) {
//			changes[1]-=2;
//		} else if (type==FROM_POWER_TO_CHASSIS) {
//			changes[2]+=2;
//		} else if (type==FROM_CHASSIS_TO_POWER) {
//			changes[2]-=2;
//		}
		return true;
	}

    public static class ModSlotDefaultHandler implements EventHandler<ConversionEvent> {

    	private ModSlotConfigPane pane;

    	public ModSlotDefaultHandler(ModSlotConfigPane pane) {
    		this.pane = pane;
    	}


		//-------------------------------------------------------------------
		/**
		 * @see javafx.event.EventHandler#handle(javafx.event.Event)
		 */
		@Override
		public void handle(ConversionEvent event) {
			CarriedItem<ItemTemplate> carried = pane.getConfig();
			int[] changes = carried.getModificationSlotChanges();
			EventType<ConversionEvent> type = (EventType<ConversionEvent>) event.getEventType();
	    	logger.log(Level.WARNING,"handle {0}",type);
			if (type==FROM_CHASSIS_TO_ELECTRONICS) {
				changes[0]+=2;
			} else if (type==FROM_ELECTRONICS_TO_CHASSIS) {
				changes[0]-=2;
			} else if (type==FROM_ELECTRONICS_TO_POWER) {
				changes[1]+=2;
			} else if (type==FROM_POWER_TO_ELECTRONICS) {
				changes[1]-=2;
			} else if (type==FROM_POWER_TO_CHASSIS) {
				changes[2]+=2;
			} else if (type==FROM_CHASSIS_TO_POWER) {
				changes[2]-=2;
			}

			carried.setModificationSlotChanges(changes);
			SR6GearTool.recalculate("", null, carried);
	    	pane.refresh();
		}

    }

}
