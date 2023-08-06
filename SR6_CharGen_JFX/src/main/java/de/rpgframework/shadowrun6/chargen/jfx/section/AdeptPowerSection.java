package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertManager;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.NavigButtonControl;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.Selector;
import de.rpgframework.jfx.section.ListSection;
import de.rpgframework.shadowrun.AdeptPower;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.chargen.jfx.listcell.AdeptPowerValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class AdeptPowerSection extends ListSection<AdeptPowerValue> {

	private final static Logger logger = System.getLogger(AdeptPowerSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(AdeptPowerSection.class.getPackageName()+".Section");

	private SR6CharacterController control;
	private Shadowrun6Character model;

	private Label lbUnspent, lbTotal;

	//-------------------------------------------------------------------
	public AdeptPowerSection(String title) {
		super(title);
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
//		list.setStyle("-fx-min-height: 10em; -fx-pref-height: 30em; -fx-pref-width: 25em");
		list.setCellFactory(cell -> new AdeptPowerValueListCell( () -> control.getAdeptPowerController()));

		lbUnspent = new Label("?");
		lbUnspent.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
		lbTotal = new Label("?");
		lbTotal.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
	}

	// -------------------------------------------------------------------
	private void initLayout() {
		Label hdPPTotal = new Label(ResourceI18N.get(RES, "section.adeptpowers.ppTotal"));
		Label hdPPUnspent = new Label(ResourceI18N.get(RES, "section.adeptpowers.ppUnspent"));
		HBox headerNode = new HBox(5, hdPPTotal, lbTotal, hdPPUnspent, lbUnspent);
		HBox.setMargin(hdPPUnspent, new Insets(0,0,0,5));
		headerNode.setAlignment(Pos.CENTER_LEFT);
		setHeaderNode(headerNode);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.DEBUG, "opening power selection dialog");

		Selector<AdeptPower,AdeptPowerValue> selector = new Selector<AdeptPower,AdeptPowerValue>(control.getAdeptPowerController(), null,
				Shadowrun6Tools.requirementResolver(Locale.getDefault()),
				Shadowrun6Tools.modificationResolver(Locale.getDefault()),
				null){
		};
		NavigButtonControl btnCtrl = new NavigButtonControl();
    	btnCtrl.setDisabled(CloseType.OK, true);
    	selector.setButtonControl(btnCtrl);

    	ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES,"section.adeptpowers.selector.title"), selector, CloseType.OK, CloseType.CANCEL);
    	btnCtrl.initialize(FlexibleApplication.getInstance(), dialog);

		CloseType close = (CloseType) FlexibleApplication.getInstance().showAndWait(dialog);
		logger.log(Level.DEBUG,"Closed with "+close);
		if (close==CloseType.OK) {
			AdeptPower data = selector.getSelected();
			logger.log(Level.DEBUG, "Selected adept power: "+data);
			OperationResult<AdeptPowerValue> res = null;
			if (!data.getChoices().isEmpty()) {
				Decision[] dec = handleChoices(data);
				if (dec==null) return;
				res = control.getAdeptPowerController().select(data, dec);
			} else {
				res = control.getAdeptPowerController().select(data);
			}
			if (res.wasSuccessful()) {
				logger.log(Level.INFO, "Selecting {0} was successful", data);
			} else {
				logger.log(Level.WARNING, "Selecting {0} failed: {1}", data, res.getError());
				AlertManager.showAlertAndCall(javafx.scene.control.Alert.AlertType.ERROR, "Failed adding", res.getError());
			}
		}
	}
	//-------------------------------------------------------------------
	protected Decision[] handleChoices(AdeptPower data) {
		ChoiceSelectorDialog<AdeptPower, AdeptPowerValue> dialog = new ChoiceSelectorDialog<AdeptPower, AdeptPowerValue>(control.getAdeptPowerController());
		return dialog.apply(data, data.getChoices());
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(AdeptPowerValue item) {
		if (control.getAdeptPowerController().deselect(item)) {
			list.getItems().remove(item);
		}
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		assert ctrl!=null;
		control = ctrl;
		model = ctrl.getModel();
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

		SR6AdeptPowerController ctrl = (SR6AdeptPowerController) control.getAdeptPowerController();
		if (model!=null) {
			setData(model.getAdeptPowers());
			lbTotal.setText( String.valueOf( ctrl.getMaxPowerPoints() ));
			lbUnspent.setText( String.valueOf( ctrl.getUnsedPowerPoints() ));
		} else
			setData(new ArrayList<>());
	}


}
