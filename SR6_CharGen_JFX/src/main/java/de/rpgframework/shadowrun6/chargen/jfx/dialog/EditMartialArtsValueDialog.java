package de.rpgframework.shadowrun6.chargen.jfx.dialog;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;
import org.prelle.javafx.OptionalNodePane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.jfx.ComplexDataItemControllerNode;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.Technique;
import de.rpgframework.shadowrun6.TechniqueValue;
import de.rpgframework.shadowrun6.chargen.charctrl.IMartialArtsController;
import de.rpgframework.shadowrun6.chargen.charctrl.ITechniqueController;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.TechniqueListCell;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * @author prelle
 *
 */
public class EditMartialArtsValueDialog extends ManagedDialog implements ControllerListener {

	private static PropertyResourceBundle UI = (PropertyResourceBundle) ResourceBundle.getBundle(EditMartialArtsValueDialog.class.getPackageName()+".Dialogs");

	private final static Logger logger = System.getLogger(EditMartialArtsValueDialog.class.getPackageName());

	private ITechniqueController control;

	private NavigButtonControl btnControl;

	private ComplexDataItemControllerNode<Technique, TechniqueValue> node;
	private OptionalNodePane content;
	private GenericDescriptionVBox description;

	//--------------------------------------------------------------------
	public EditMartialArtsValueDialog(IMartialArtsController ctrl, MartialArtsValue toEdit) {
		super(ResourceI18N.format(UI,"dialog.editmartialart.title", toEdit.getResolved().getName()), null, CloseType.APPLY);
		this.control  = ctrl.getTechniqueController(toEdit);
		btnControl = new NavigButtonControl();

		initCompoments();
		initLayout();
		initInteractivity();
		refresh();

		control.getCharacterController().addListener(this);
	}

	//--------------------------------------------------------------------
	private void initCompoments() {
		node = new ComplexDataItemControllerNode<>(control);
		node.setAvailableCellFactory(lv -> new TechniqueListCell(control));
		node.setSelectedCellFactory(lv -> new ComplexDataItemValueListCell<>(()->control ));
		//lvSelected .setStyle("-fx-min-width: 25em;");
		description = new GenericDescriptionVBox(
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault())
				);
	}

	//--------------------------------------------------------------------
	private void initLayout() {
		content = new OptionalNodePane(node, description);

		this.setMaxHeight(Double.MAX_VALUE);
		setContent(content);
	}

	//--------------------------------------------------------------------
	private void initInteractivity() {
		node.showHelpForProperty().addListener( (ov,o,n) -> {
			description.setData(n);
		});
	}

	//--------------------------------------------------------------------
	private void refresh() {
		node.refresh();
	}

	//-------------------------------------------------------------------
	public NavigButtonControl getButtonControl() {
		return btnControl;
	}

	//-------------------------------------------------------------------
	public void onClose(CloseType closeType) {
		control.getCharacterController().removeListener(this);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.shadowrun6.gen.event.GenerationEventListener#handleGenerationEvent(org.prelle.shadowrun6.gen.event.GenerationEvent)
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		if (type==BasicControllerEvents.CHARACTER_CHANGED) {
			refresh();
		}
	}

	//-------------------------------------------------------------------
	/*
	 * Select
	 */
	private void dragDroppedAvailable(DragEvent event) {
		/* if there is a string data on dragboard, read it and use it */
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasString()) {
			String enhanceID = db.getString();
			// Get reference for ID
			if (enhanceID.startsWith("technique:")) {
				String id = enhanceID.substring("technique:".length());
				Technique master = Shadowrun6Core.getItem(Technique.class,id);
				logger.log(Level.INFO,"Try to select technique "+master);
				OperationResult<TechniqueValue> added = control.select(master);
				if (added!=null && added.wasSuccessful()) {
					refresh();
//					lvAvailable.getItems().remove(master);
//					if (!lvSelected.getListView().getItems().contains(added.get()))
//						lvSelected.getListView().getItems().add(added.get());
					//        			updateCost();
				}
			}
		}
		/* let the source know whether the string was successfully
		 * transferred and used */
		event.setDropCompleted(success);

		event.consume();
	}

	//-------------------------------------------------------------------
	/*
	 * Select
	 */
	private void dragOverAvailable(DragEvent event) {
		Node target = (Node) event.getSource();
		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
			String enhanceID = event.getDragboard().getString();
			// Get reference for ID
			if (enhanceID.startsWith("technique:")) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
		}
	}

	//	//-------------------------------------------------------------------
	//	/*
	//	 * Deselect
	//	 */
	//	private void dragDroppedSelected(DragEvent event) {
	//       /* if there is a string data on dragboard, read it and use it */
	//        Dragboard db = event.getDragboard();
	//        boolean success = false;
	//        if (db.hasString()) {
	//            String enhanceID = db.getString();
	//        	// Get reference for ID
	//        	if (enhanceID.startsWith("technique:")) {
	//        		String id = enhanceID.substring("lifestyleoption:".length());
	//        		LifestyleOption master = ShadowrunCore.getLifestyleOption(id);
	//         		if (master!=null) {
	//         			lvSelected.getItems().remove(master);
	//         			if (!lvAvailable.getItems().contains(master))
	//         				lvAvailable.getItems().add(master);
	//    				updateCost();
	//         		}
	//        	}
	//        }
	//        /* let the source know whether the string was successfully
	//         * transferred and used */
	//        event.setDropCompleted(success);
	//
	//        event.consume();
	//	}
	//
	//	//-------------------------------------------------------------------
	//	/*
	//	 * Deselect
	//	 */
	//	private void dragOverSelected(DragEvent event) {
	//		Node target = (Node) event.getSource();
	//		if (event.getGestureSource() != target && event.getDragboard().hasString()) {
	//			String enhanceID = event.getDragboard().getString();
	//			// Get reference for ID
	//			if (enhanceID.startsWith("lifestyleoption:")) {
	//				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	//			}
	//		}
	//	}

}
