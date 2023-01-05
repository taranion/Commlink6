package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.IMartialArtsController;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditMartialArtsValueDialog;
import javafx.scene.control.Label;

/**
 * @author prelle
 *
 */
public class MartialArtsValueListCell extends ComplexDataItemValueListCell<MartialArts, MartialArtsValue> {

	private final static Logger logger = System.getLogger(MartialArtsValueListCell.class.getPackageName());

	private Label lbTechniques;

	//-------------------------------------------------------------------
	public MartialArtsValueListCell(Supplier<ComplexDataItemController<MartialArts, MartialArtsValue>> ctrlProv) {
		super(ctrlProv);
		lbTechniques = new Label();
		lbTechniques.setWrapText(true);
		bxCenter.getChildren().add(2, lbTechniques);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(MartialArtsValue item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			IMartialArtsController control = (IMartialArtsController) controlProvider.get();
			Shadowrun6Character model = control.getModel();
//			lblType.setText(String.valueOf(item.getCost()));
			try {
				name.setText(item.getResolved().getName(Locale.getDefault()));
				model.getTechniques(item.getResolved());

				List<String> techniqueNames = model.getTechniques(item.getResolved()).stream().map(tech -> tech.getResolved().getName()).collect(Collectors.toList());
				lbTechniques.setText(String.join(",", techniqueNames));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.cells.ComplexDataItemValueListCell#editClicked(de.rpgframework.genericrpg.data.ComplexDataItemValue)
	 */
	@Override
	protected void editClicked(MartialArtsValue ref) {
		logger.log(Level.INFO,"edit martial arts "+getItem());
		MartialArtsValue data = getItem();
		IMartialArtsController control = (IMartialArtsController) controlProvider.get();
		EditMartialArtsValueDialog dialog = new EditMartialArtsValueDialog(control, data);

//		ITechniqueController tCtrl = control.getTechniqueController(data);
//		ComplexDataItemControllerNode<Technique, TechniqueValue> node = new ComplexDataItemControllerNode<>(tCtrl);
//		node.setAvailableCellFactory(lv -> new TechniqueListCell(tCtrl));
//		CloseType result = FlexibleApplication.getInstance().showAlertAndCall(AlertType.CONFIRMATION, "header", node);

		CloseType result = FlexibleApplication.getInstance().showAlertAndCall(dialog, dialog.getButtonControl());

		if (result==CloseType.OK) {
			this.requestLayout();
		}
	}

}
