package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.Iterator;

import org.prelle.javafx.SymbolIcon;

import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualFeatureReference;
import de.rpgframework.shadowrun.RitualValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RitualValueListCell extends ListCell<RitualValue> {
	
//	private final static Map<RitualFeature, Image> IMAGES = new HashMap<>();
	
	private IShadowrunCharacterControllerProvider<IShadowrunCharacterController> ctrlProv;
	private ImageView iview;
	private Label lblName;
	private Label lblLine1;
	private StackPane stack;
	private SymbolIcon lblLock;
	
	private HBox bxLayout;
	private VBox bxData;
	
	//-------------------------------------------------------------------
	static {
//		IMAGES.put(Category.COMBAT   , new Image(ClassLoader.getSystemResourceAsStream("images/shadowrun/ritualtype.combat.png")));
//		IMAGES.put(Category.HEALTH   , new Image(ClassLoader.getSystemResourceAsStream("images/shadowrun/ritualtype.health.png")));
//		IMAGES.put(Category.DETECTION, new Image(ClassLoader.getSystemResourceAsStream("images/shadowrun/ritualtype.detection.png")));
//		IMAGES.put(Category.ILLUSION , new Image(ClassLoader.getSystemResourceAsStream("images/shadowrun/ritualtype.illusion.png")));
//		IMAGES.put(Category.MANIPULATION, new Image(ClassLoader.getSystemResourceAsStream("images/shadowrun/ritualtype.manipulation.png")));
	}
	
	//-------------------------------------------------------------------
	public RitualValueListCell(IShadowrunCharacterControllerProvider<IShadowrunCharacterController> ctrlProv) {
		this.ctrlProv = ctrlProv;
		initComponents();
		initLayout();
		initStyle();
		
		getStyleClass().add("ritualvalue-list-cell");
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		lblName  = new Label();
		lblLine1 = new Label();
		
		iview = new ImageView();
		iview.setFitHeight(48);
		iview.setFitWidth(48);
		lblLock = new SymbolIcon("lock");
		lblLock.setMaxWidth(50);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		bxData = new VBox();
		bxData.getChildren().addAll(lblName, lblLine1);
		
		bxLayout = new HBox(5);
		bxLayout.getChildren().addAll(iview, bxData);

		stack = new StackPane();
		stack.setMaxWidth(Double.MAX_VALUE);
		stack.setAlignment(Pos.CENTER_RIGHT);
		stack.getChildren().addAll(lblLock, bxLayout);
		StackPane.setAlignment(lblLock, Pos.TOP_RIGHT);
	}
	
	//-------------------------------------------------------------------
	private void initStyle() {
		lblName.setStyle("-fx-font-family: Roboto-Medium; ");
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(RitualValue item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setGraphic(stack);
			Ritual ritual = item.getModifyable();
			lblName.setText(ritual.getName().toUpperCase());
			lblName.setStyle("-fx-font-family: Roboto-Medium; ");
			lblLine1.setText(makeFeatureString(ritual));
//			iview.setImage(IMAGES.get(ritual.getCategory()));
			lblLock.setVisible( !ctrlProv.getCharacterController().getRitualController().canBeDeselected(item).get());
		}
	}
	
	//-------------------------------------------------------------------
	private static String makeFeatureString(Ritual ritual) {
		StringBuffer buf = new StringBuffer();
		Iterator<RitualFeatureReference> it = ritual.getFeatures().iterator();
		while (it.hasNext()) {
			RitualFeatureReference ref = it.next();
			buf.append(ref.getResolved().getName());
			if (it.hasNext())
				buf.append(", ");
		}
		return buf.toString();
	}
}