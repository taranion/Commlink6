package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.Section;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun.BodyForm;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

/**
 * 
 */
public class AlternateBodySection extends Section {

	private final static Logger logger = System.getLogger(AlternateBodySection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(CritterPowerSection.class.getPackageName()+".Section");

	private Shadowrun6Character model;
	private SR6CharacterController control;
	
	private Map<ShadowrunAttribute,Label> attribLabels;
	
	private GridPane attribGrid = new GridPane();
	
	//-------------------------------------------------------------------
	/**
	 */
	public AlternateBodySection() {
		this(ResourceI18N.get(RES, "section.altbody.title"),null);
	}

	//-------------------------------------------------------------------
	/**
	 * @param title
	 * @param content
	 */
	public AlternateBodySection(String title, Node content) {
		super(title, content);
		initComponents();
		initLayout();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		attribLabels = new HashMap<>();
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
			Label label = new Label("?");
			label.setTextAlignment(TextAlignment.CENTER);
			label.setAlignment(Pos.CENTER);
			label.setMaxWidth(40);
			attribLabels.put(key, label);
			GridPane.setFillWidth(label, true);
			GridPane.setHgrow(label, Priority.ALWAYS);
		}
	}

	// -------------------------------------------------------------------
	private void initLayout() {
		attribGrid = new GridPane();
		int x=0;
		for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
			Label heading = new Label(key.getShortName());
			heading.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
			ColumnConstraints cons = new ColumnConstraints(40);
			cons.setFillWidth(true);
			heading.setTextAlignment(TextAlignment.CENTER);
			heading.setAlignment(Pos.CENTER);
			heading.setMaxWidth(40);
			attribGrid.add(heading, x, 0);
			attribGrid.add(attribLabels.get(key), x, 1);
			attribGrid.getColumnConstraints().add(cons);
			x++;
		}
		
		HBox layout = new HBox(20, attribGrid);
		setContent(layout);
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		assert ctrl!=null;
		control = ctrl;
		model = (Shadowrun6Character) ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	@Override
	public void refresh() {
		logger.log(Level.TRACE, "refresh");

		
		if (model!=null) {
			BodyForm bForm = model.getBodyForm(model.getBodytype());
			for (ShadowrunAttribute key : ShadowrunAttribute.primaryValues()) {
				AttributeValue<ShadowrunAttribute> aVal = bForm.getAttributeValue(key);
				attribLabels.get(key).setText(aVal.getDisplayString());
			}
		} 
			
	}

}
