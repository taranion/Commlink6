package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.jfx.section.ComplexDataItemListSection;
import de.rpgframework.shadowrun6.DataStructure;
import de.rpgframework.shadowrun6.DataStructureValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.pane.DataStructureSelector;

/**
 * @author stefa
 *
 */
public abstract class DataStructuresSection extends ComplexDataItemListSection<DataStructure, DataStructureValue> {

	private final static Logger logger = System.getLogger(DataStructuresSection.class.getPackageName());

	public static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(DataStructuresSection.class.getPackageName()+".Section");

	private SR6CharacterController control;
	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 * @param title
	 */
	public DataStructuresSection(String title) {
		super(title);
		list.setCellFactory( lv -> new ComplexDataItemValueListCell<DataStructure,DataStructureValue>( () -> control.getDataStructureController()));
	}

	//-------------------------------------------------------------------
	protected abstract Decision[] requestUserDecisions(DataStructure value);

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.WARNING, "opening data structure selection dialog  "+control.getDataStructureController());

		DataStructureSelector selector = new DataStructureSelector(control.getDataStructureController(), null, Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES,"section.datastructure.selector.title"), selector, CloseType.OK, CloseType.CANCEL);

		CloseType close = (CloseType) FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.ERROR,"Closed with "+close);
		if (close==CloseType.OK) {
			DataStructure data = selector.getSelected();
			logger.log(Level.INFO, "Selected focus: "+data);
			OperationResult<DataStructureValue> result = null;
			if (data.getChoices().isEmpty()) {
				result = control.getDataStructureController().select(data);
			} else {
				Decision[] dec = requestUserDecisions(data);
				logger.log(Level.INFO, "User decisions for focus: "+dec);
				if (dec!=null) {
					// Not cancelled
					result = control.getDataStructureController().select(data, dec);
				}
			}
			if (result!=null && result.hasError()) {
				BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, result.getError());
			}

		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(DataStructureValue item) {
		logger.log(Level.DEBUG, "onDelete "+item);
		if (control.getDataStructureController().deselect(item)) {
			list.getItems().remove(item);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	@Override
	public void refresh() {
		logger.log(Level.TRACE, "refresh");

		if (model!=null)
			setData(model.getDataStructures());
		else
			setData(new ArrayList<>());
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		assert ctrl!=null;
		control = ctrl;
		model = (Shadowrun6Character) ctrl.getModel();
		refresh();
	}

}
