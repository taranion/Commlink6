package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.jfx.section.ListSection;
import de.rpgframework.shadowrun.CritterPower;
import de.rpgframework.shadowrun.CritterPowerValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.SignatureManeuver;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CritterPowerController;
import de.rpgframework.shadowrun6.chargen.jfx.dialog.EditSignatureManeuverDialog;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SignatureManeuverListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class SignatureManeuverSection extends ListSection<SignatureManeuver> {

	private final static Logger logger = System.getLogger(SignatureManeuverSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SignatureManeuverSection.class.getPackageName()+".Section");

	private IShadowrunCharacterController control;
	private Shadowrun6Character model;

	private Label lbUnspent, lbTotal;

	//-------------------------------------------------------------------
	public SignatureManeuverSection() {
		super(ResourceI18N.get(RES, "section.signaturemaneuvers.title"));
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
//		list.setStyle("-fx-min-height: 10em; -fx-pref-height: 30em; -fx-pref-width: 25em");
		list.setCellFactory(cell -> new SignatureManeuverListCell());

		lbUnspent = new Label("?");
		lbUnspent.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
		lbTotal = new Label("?");
		lbTotal.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
	}

	// -------------------------------------------------------------------
	private void initLayout() {
//		Label hdPPTotal = new Label(ResourceI18N.get(RES, "section.adeptpowers.ppTotal"));
//		Label hdPPUnspent = new Label(ResourceI18N.get(RES, "section.adeptpowers.ppUnspent"));
//		HBox headerNode = new HBox(5, hdPPTotal, lbTotal, hdPPUnspent, lbUnspent);
//		HBox.setMargin(hdPPUnspent, new Insets(0,0,0,5));
//		headerNode.setAlignment(Pos.CENTER_LEFT);
//		setHeaderNode(headerNode);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		SignatureManeuver toAdd = new SignatureManeuver();
		EditSignatureManeuverDialog dialog = new EditSignatureManeuverDialog(toAdd, false);
		CloseType result = FlexibleApplication.getInstance().showAlertAndCall(dialog, dialog.getButtonControl());
		logger.log(Level.INFO,"Adding signature maneuver dialog closed with {0}",result);
		if (result==CloseType.OK || result==CloseType.APPLY) {
			logger.log(Level.INFO,"Adding signature maneuver: "+toAdd);
			model.addSignatureManeuver(toAdd);
			getListView().getItems().add(toAdd);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(SignatureManeuver item) {
		logger.log(Level.DEBUG, "onDelete");
//		if (control.getSkillController().deselect(item)) {
//			list.getItems().remove(item);
//		}
	}

	//-------------------------------------------------------------------
	public void updateController(IShadowrunCharacterController ctrl) {
		assert ctrl!=null;
		control = ctrl;
		model = (Shadowrun6Character) ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void refresh() {
		logger.log(Level.TRACE, "refresh");

		SR6CritterPowerController ctrl = (SR6CritterPowerController) control.getCritterPowerController();
		if (model!=null) {
			setData(model.getSignatureManeuvers());
		} else
			setData(new ArrayList<>());
	}


}
