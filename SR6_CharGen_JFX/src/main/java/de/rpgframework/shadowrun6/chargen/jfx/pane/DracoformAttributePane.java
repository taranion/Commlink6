package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.NumericalValueController;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.jfx.NumericalValueField;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun6.DrakeTypeValue;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6DrakeController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class DracoformAttributePane extends GridPane {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(DracoformAttributePane.class.getPackageName()+".Panes");

	private Label[] lbAttrib;
	private NumericalValueField<ShadowrunAttribute, AttributeValue<ShadowrunAttribute>>[] tfAttrib;
	private Label lbPtLeft, lbPtMax;
	private SR6DrakeController ctrl;
	private ObjectProperty<NumericalValueController<ShadowrunAttribute, AttributeValue<ShadowrunAttribute>>> propAttrCtrl = new SimpleObjectProperty<>();

	//-------------------------------------------------------------------
	public DracoformAttributePane() {
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initComponents() {
		lbAttrib = new Label[4];
		tfAttrib = new NumericalValueField[4];
		for (int i=0; i<4; i++) {
			lbAttrib[i] = new Label();
			lbAttrib[i].getStyleClass().add(JavaFXConstants.STYLE_TABLE_HEAD);
			lbAttrib[i].setMaxWidth(Double.MAX_VALUE);
			tfAttrib[i] = new NumericalValueField<>();
			tfAttrib[i].getStyleClass().add(JavaFXConstants.STYLE_TABLE_DATA);
		}

		lbPtLeft = new Label("6");
		lbPtLeft.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		lbPtLeft.setStyle("-fx-text-fill: highlight");
		lbPtMax  = new Label("6");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		Label lbLeftText = new Label(ResourceI18N.get(RES,"pane.dracoform.pointsleft"));
		HBox line = new HBox(5, lbPtLeft, new Label("/"), lbPtMax, lbLeftText);
		//setHgap(10);
		Label lbAttributes = new Label(ResourceI18N.get(RES,"pane.dracoform.attributes"));
		lbAttributes.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);

		add(lbAttributes, 0,0);
		add(line        , 0,1);
		GridPane.setMargin(lbAttributes, new Insets(0,10,0,0));
		GridPane.setValignment(line, VPos.CENTER);

		for (int i=0; i<lbAttrib.length; i++) {
			add(lbAttrib[i], i+1, 0 );
			add(tfAttrib[i], i+1, 1 );
			GridPane.setFillWidth(lbAttrib[i], true);
			GridPane.setHalignment(lbAttrib[i], HPos.CENTER);
			GridPane.setMargin(tfAttrib[i], new Insets(0,6,0,6));
		}
	}

	//-------------------------------------------------------------------
	public void setController(SR6DrakeController ctrl) {
		this.ctrl = ctrl;
		propAttrCtrl.set( ctrl.getAttributeController() );
		int i=0;
		DrakeTypeValue body = ctrl.getCharacterController().getModel().getDrakeType();
		for (ShadowrunAttribute attr : ctrl.getDracoformAttributes()) {
			lbAttrib[i].setText(attr.getShortName(Locale.getDefault()));

			if (body!=null) {
				AttributeValue<ShadowrunAttribute> aVal = body.getAttribute(attr);
				if (aVal==null) {
					try {
						throw new NullPointerException("No attribute value "+attr+" in body "+body);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				for (NumericalValueField<ShadowrunAttribute, AttributeValue<ShadowrunAttribute>> tf : tfAttrib) {
				tfAttrib[i].setValueFactory( () -> body.getAttribute(attr));
				tfAttrib[i].setController(propAttrCtrl.get());
//					tfAttrib[i].setData(aVal, propAttrCtrl);
//				}
			}
			i++;
		}
	}

	//-------------------------------------------------------------------
	public void refresh() {
		for (NumericalValueField<ShadowrunAttribute, AttributeValue<ShadowrunAttribute>> tf : tfAttrib) {
			tf.refresh();
		}

		if (ctrl!=null) {
			lbPtLeft.setText( String.valueOf(ctrl.getAdjustmentPointsLeft()));
			lbPtMax.setText( String.valueOf(ctrl.getAdjustmentPointsMax()));
		}
	}

}
