package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.ListSection;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6AdeptPowerController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class AdeptPowerSection extends ListSection<AdeptPowerValue> {

	private final static Logger logger = System.getLogger(QualitySection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(QualitySection.class.getPackageName()+".Selection");

	private IShadowrunCharacterController control;
	private ShadowrunCharacter model;
	
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
//		list.setCellFactory(cell -> new SpellValueListCell<T>(control));
		
		lbUnspent = new Label("?");
		lbUnspent.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
		lbTotal = new Label("?");
		lbTotal.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
	}

	// -------------------------------------------------------------------
	private void initLayout() {
		Label hdPPTotal = new Label(ResourceI18N.get(RES, "adeptpowerssection.ppTotal"));
		Label hdPPUnspent = new Label(ResourceI18N.get(RES, "adeptpowerssection.ppUnspent"));
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
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(AdeptPowerValue item) {
		logger.log(Level.DEBUG, "onDelete");
//		if (control.getSkillController().deselect(item)) {
//			list.getItems().remove(item);
//		}
	}

	//-------------------------------------------------------------------
	public void updateController(IShadowrunCharacterController ctrl) {
		assert ctrl!=null;
		control = ctrl;
		model = (ShadowrunCharacter) ctrl.getModel();
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
