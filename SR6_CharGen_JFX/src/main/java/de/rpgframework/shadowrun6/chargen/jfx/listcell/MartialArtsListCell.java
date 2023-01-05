package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.prelle.javafx.JavaFXConstants;

import de.rpgframework.jfx.cells.ComplexDataItemListCell;
import de.rpgframework.shadowrun6.MartialArts;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.IMartialArtsController;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * @author prelle
 *
 */
public class MartialArtsListCell extends ComplexDataItemListCell<MartialArts> {

	private StackPane stack;
	private ImageView imgRecommended;
	private Label lblCategory;

	//-------------------------------------------------------------------
	/**
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MartialArtsListCell(IMartialArtsController ctrl) {
		super( (Supplier)() -> ctrl, Shadowrun6Tools.requirementResolver(Locale.getDefault()));
		lblCategory = new Label();
		lblCategory.getStyleClass().add(JavaFXConstants.STYLE_TEXT_TERTIARY);
		StackPane.setAlignment(lblCategory, Pos.CENTER_RIGHT);

		// Content
		lbName.getStyleClass().remove(JavaFXConstants.STYLE_HEADING5);
		lbName.getStyleClass().add(JavaFXConstants.STYLE_HEADING4);
		StackPane.setAlignment(lbName, Pos.CENTER_LEFT);

		// Recommended icon
		imgRecommended = new ImageView();
		imgRecommended.setFitHeight(16);
		imgRecommended.setFitWidth(16);

		stack = new StackPane();
		//		stack.setAlignment(Pos.TOP_RIGHT);
		stack.getChildren().addAll(lblCategory, lbName, imgRecommended);
		StackPane.setAlignment(imgRecommended, Pos.TOP_RIGHT);
	}

	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(MartialArts item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			lblCategory.setText( String.join(", ", item.getCategories().stream().map(cat -> cat.getName(Locale.getDefault())).collect(Collectors.toList())) );
			lbName.setText(((MartialArts)item).getName(Locale.getDefault()));
//			imgRecommended.setVisible(control.isRecommended((MartialArts) item));
			setGraphic(stack);
		}
	}

}
