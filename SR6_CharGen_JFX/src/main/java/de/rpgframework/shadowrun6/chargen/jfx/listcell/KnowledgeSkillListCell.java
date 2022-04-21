package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.SymbolIcon;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.charctrl.ISkillController;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class KnowledgeSkillListCell extends ListCell<SR6SkillValue> {

//	private static PropertyResourceBundle UI = SR6Constants.RES;

	private final static String NORMAL_STYLE = "knowledge-skill-cell";

	private final static Logger logger = System.getLogger(KnowledgeSkillListCell.class.getPackageName());

	private IShadowrunCharacterControllerProvider<IShadowrunCharacterController> controlProvider;
	private ScreenManagerProvider provider;

	private HBox layout;
	private Label name;
	private Button btnEdit;
	private SymbolIcon lblLock;

	//-------------------------------------------------------------------
	public KnowledgeSkillListCell(IShadowrunCharacterControllerProvider<IShadowrunCharacterController> ctrlProv, ScreenManagerProvider provider) {
		this.controlProvider = ctrlProv;
		this.provider = provider;
		

		// Content		
		layout  = new HBox(5);
		name    = new Label();
		btnEdit = new Button("\uE1C2");
		btnEdit.getStyleClass().add("mini-button");
		lblLock = new SymbolIcon("lock");
		lblLock.setMaxWidth(50);

		initStyle();
		initLayout();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initStyle() {
		name.setStyle("-fx-font-weight: bold");

		btnEdit.setStyle("-fx-background-color: transparent; -fx-border-color: -fx-outer-border;");

		getStyleClass().add(NORMAL_STYLE);
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		HBox line1 = new HBox(10);
		line1.getChildren().addAll(name, lblLock);		
		layout.getChildren().addAll(line1);

		name.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(name, Priority.ALWAYS);

		setAlignment(Pos.CENTER);
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	private void initInteractivity() {
		btnEdit.setOnAction(event -> {
			editClicked(this.getItem());
		});

		this.setOnDragDetected(event -> dragStarted(event));
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		logger.log(Level.DEBUG, "drag started for "+this.getItem());
		if (this.getItem()==null)
			return;

		//		logger.log(Level.DEBUG, "canBeDeselected = "+charGen.canBeDeselected(data));
		//		logger.log(Level.DEBUG, "canBeTrashed    = "+((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true));
		//		if (!charGen.canBeDeselected(data) && !((charGen instanceof ResourceLeveller)?((ResourceLeveller)charGen).canBeTrashed(data):true))
		//			return;

		Node source = (Node) event.getSource();
		logger.log(Level.DEBUG, "drag src = "+source);

		/* drag was detected, start a drag-and-drop gesture*/
		/* allow any transfer mode */
		Dragboard db = source.startDragAndDrop(TransferMode.ANY);

		/* Put a string on a dragboard */
		ClipboardContent content = new ClipboardContent();
		//        String id = "skill:"+data.getModifyable().getId()+"|desc="+data.getDescription()+"|idref="+data.getIdReference();
		String id = "qualityval:"+this.getItem().getModifyable().getId();
		content.putString(id);
		db.setContent(content);

		/* Drag image */
		WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
		db.setDragView(snapshot);

		event.consume();	
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(SR6SkillValue item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item==null) {
			setText(null);
			setGraphic(null);			
		} else {
			IShadowrunCharacterController ctrl = controlProvider.getCharacterController();
			ShadowrunCharacter<?, ?> model = (ShadowrunCharacter<?, ?>) ctrl.getModel();
			ISkillController charGen = ctrl.getSkillController();
			
			
			name.setText(item.getName(Locale.getDefault()));
			name.setText(item.getDecisionString(Locale.getDefault()));
//			attrib.setText(attr.getShortName()+" "+charGen.getModel().getAttribute(attr).getModifiedValue());
			//			tfDescr.setText(item.getModifyable().getAttribute1().getName());
//			tfDescr.setText(item.getDecisionString(Locale.getDefault()));
//			imgRecommended.setVisible(charGen.isRecommended(item.getModifyable()));
			Possible removeable = charGen.canBeDeselected(item);
			lblLock.setVisible( !removeable.get());
			setGraphic(layout);
		}
	}

	//-------------------------------------------------------------------
	private void editClicked(SR6SkillValue ref) {
		logger.log(Level.DEBUG, "editClicked");

//		TextField tf = new TextField(ref.getDescription());
//		// Prevent entering XML relevant chars
//		tf.textProperty().addListener( (ov,o,n) -> {
//			if (n.indexOf('<')>0) { n = n.substring(0, n.indexOf('<')); tf.setText(n); }
//			if (n.indexOf('>')>0) { n = n.substring(0, n.indexOf('>')); tf.setText(n); }
//			if (n.indexOf('"')>0) { n = n.substring(0, n.indexOf('"')); tf.setText(n); }
//			if (n.indexOf('&')>0) { n = n.substring(0, n.indexOf('&')); tf.setText(n); }
//		});
	}

}