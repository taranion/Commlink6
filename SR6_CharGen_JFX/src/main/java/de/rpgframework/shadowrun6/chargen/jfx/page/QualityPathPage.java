package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.ResponsiveBox;

import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.pane.VisualQualityPathPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

/**
 * @author prelle
 *
 */
public class QualityPathPage extends Page {

	private final static Logger logger = System.getLogger(QualityPathPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private SR6CharacterController control;
	private QualityPathValue selected;
		
	private ResponsiveBox flex;
	private VisualQualityPathPane visual;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public QualityPathPage(SR6CharacterController control, QualityPathValue data) {
		super(data.getResolved().getName());
		this.control = control;
		this.selected = data;
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		visual = new VisualQualityPathPane();
		visual.setData(selected.getResolved());
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
//		visual.prefHeight(400);
//		AcrylicPane acryl = new AcrylicPane(visual, false);
//		acryl.prefHeightProperty().bind(list.heightProperty());
		flex = new ResponsiveBox(visual);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
	}

	//-------------------------------------------------------------------
	private void showDescription(ComplexDataItemValue<? extends ComplexDataItem> n) {
		logger.log(Level.INFO, "Show description "+n);
		if (n==null) {
			layout.setOptional(null);
		} else {
			layout.setOptional( new GenericDescriptionVBox( r->Shadowrun6Tools.getRequirementString(r, Locale.getDefault()), n.getModifyable()));
			layout.setTitle(n.getModifyable().getName());
		}
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
	}

}
