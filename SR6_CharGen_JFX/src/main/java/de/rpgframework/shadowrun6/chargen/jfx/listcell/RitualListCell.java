package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.rpgframework.shadowrun.Ritual;
import de.rpgframework.shadowrun.RitualFeature;
import de.rpgframework.shadowrun.RitualFeatureReference;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RitualListCell extends ListCell<Ritual> {
	
	private final static Map<RitualFeature, Image> IMAGES = new HashMap<>();
	
	private ImageView iview;
	private Label lblName;
	private Label lblLine1;
	
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
	public RitualListCell() {
		initComponents();
		initLayout();
		initStyle();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		lblName  = new Label();
		lblLine1 = new Label();
		
		iview = new ImageView();
		iview.setFitHeight(48);
		iview.setFitWidth(48);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		bxData = new VBox();
		bxData.getChildren().addAll(lblName, lblLine1);
		
		bxLayout = new HBox(5);
		bxLayout.getChildren().addAll(iview, bxData);
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
	public void updateItem(Ritual item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setGraphic(bxLayout);
			lblName.setText(item.getName().toUpperCase());
			lblName.setStyle("-fx-font-family: Roboto-Medium; ");
			lblLine1.setText(makeFeatureString(item));
//			iview.setImage(IMAGES.get(item.getCategory()));
		}
	}
	
	static String makeFeatureString(Ritual ritual) {
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