package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.AlertManager;
import javafx.scene.control.ButtonType;
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
import de.rpgframework.shadowrun6.chargen.jfx.SR6JFXTools;
import de.rpgframework.shadowrun6.chargen.jfx.SR6SelectAndDecide;
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
	public AdeptPowerSection() {
		this(de.rpgframework.ResourceI18N.get(java.util.ResourceBundle.getBundle(de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout.class.getName()), "page.magic.section.adeptpowers"));
	}

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
		SR6SelectAndDecide.of(control, control.getAdeptPowerController(), AdeptPower.class, AdeptPowerValue.class)
				.title(ResourceI18N.get(RES, "section.adeptpowers.selector.title"))
				.execute();
	}

	//-------------------------------------------------------------------
	protected Decision[] handleChoices(AdeptPower data) {
		return SR6JFXTools.askDecisions(control.getAdeptPowerController(), data);
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
		if (model!=null && ctrl!=null) {
			setData(model.getAdeptPowers());
			lbTotal.setText( String.valueOf( ctrl.getMaxPowerPoints() ));
			lbUnspent.setText( String.valueOf( ctrl.getUnsedPowerPoints() ));
		} else
			setData(new ArrayList<>());
	}


}
