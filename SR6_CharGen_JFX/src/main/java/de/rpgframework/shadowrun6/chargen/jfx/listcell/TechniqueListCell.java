package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.stream.Collectors;

import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.Possible;
import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.MartialArtsValue;
import de.rpgframework.shadowrun6.Technique;
import de.rpgframework.shadowrun6.chargen.charctrl.IMartialArtsController;
import de.rpgframework.shadowrun6.chargen.charctrl.ITechniqueController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

public class TechniqueListCell extends ComplexDataItemListCell<Technique> {

	private final static Logger logger = System.getLogger(TechniqueListCell.class.getPackageName());

	private final static String NORMAL_STYLE = "skill-cell";

	private ITechniqueController control;
	private MartialArtsValue learnIn;
	private Technique data;

	private Label lineName;
	private StackPane stack;
	private ImageView imgRecommended;
	private Label lblCategory;

	//-------------------------------------------------------------------
	public TechniqueListCell(ITechniqueController ctrl) {
		this.control = ctrl;
		this.learnIn = learnIn;
		lblCategory = new Label();
		lblCategory.getStyleClass().add("type-label");
		StackPane.setAlignment(lblCategory, Pos.CENTER_RIGHT);

		// Content
		lineName = new Label();
		lineName.setStyle("-fx-font-weight: bold;");
		StackPane.setAlignment(lineName, Pos.CENTER_LEFT);

		// Recommended icon
		imgRecommended = new ImageView();
		imgRecommended.setFitHeight(16);
		imgRecommended.setFitWidth(16);

		stack = new StackPane();
		//		stack.setAlignment(Pos.TOP_RIGHT);
		stack.getChildren().addAll(lblCategory, lineName, imgRecommended);
		StackPane.setAlignment(imgRecommended, Pos.TOP_RIGHT);
		this.setOnDragDetected(event -> dragStarted(event));
		this.setOnMouseClicked(ev -> {if (ev.getClickCount()==2) selected();});
		getStyleClass().add(NORMAL_STYLE);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(Technique item, boolean empty) {
		super.updateItem(item, empty);
		data = item;

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			lblCategory.setText( String.join(", ", item.getCategories().stream().map(cat -> cat.getName(Locale.getDefault())).collect(Collectors.toList())) );
			lineName.setText(((Technique)item).getName());
//			imgRecommended.setVisible(control.isRecommended((Technique) item));
			setGraphic(stack);
		}
	}

	//-------------------------------------------------------------------
	private void dragStarted(MouseEvent event) {
		Node source = (Node) event.getSource();

		/* drag was detected, start a drag-and-drop gesture*/
		/* allow any transfer mode */
		Dragboard db = source.startDragAndDrop(TransferMode.ANY);

		/* Put a string on a dragboard */
		ClipboardContent content = new ClipboardContent();
		if (data==null)
			return;
		content.putString("technique:"+data.getId());
		db.setContent(content);

		/* Drag image */
		WritableImage snapshot = source.snapshot(new SnapshotParameters(), null);
		db.setDragView(snapshot);

		event.consume();
	}

	//-------------------------------------------------------------------
	private void selected() {
		logger.log(Level.WARNING,"Select "+data);
//		if (data.getChoice()!=null) {
//			switch (data.getChoice()) {
//			case MELEE_TYPE:
//				ChoiceBox choices = new ChoiceBox<>();
////				break;
//			default:
//				logger.error("Unsupported choice type: "+data.getChoice());
//			}
//		}

		Possible poss = control.canBeSelected(data);
		if (poss.get()) {
			control.select(data);
		} else {
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 0, "Not selectable: technique '"+data.getId()+"' in style '"+learnIn.getResolved().getId()+"': "+poss);
		}
	}

}