package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.NodeWithTitleSkeleton;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rpgframework.ResourceI18N;
import de.rpgframework.character.Gender;
import de.rpgframework.jfx.DataItemSpinnerPane;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author stefa
 *
 */
public class WizardPageMetatype extends WizardPage {
	
	private final static Logger logger = LoggerFactory.getLogger(WizardPageMetatype.class);
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(WizardPageMetatype.class.getName());

	private GeneratorWrapper charGen;
	
	private DataItemSpinnerPane<SR6MetaType> contentPane;
	
	private ChoiceBox<Gender> cbGender;
	private Button btnRoll;
	private TextField tfSize;
	private TextField tfWeight;
	private FlowPane customNode1;
	
	//-------------------------------------------------------------------
	public WizardPageMetatype(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.title"));
		initComponents();
		initLayout();
		initInteractivity();
//		refreshDataTab();
		
		contentPane.getValueFactory().setValue(Shadowrun6Core.getItem(SR6MetaType.class, "human"));
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		contentPane = new DataItemSpinnerPane<SR6MetaType>();
		contentPane.setId("species");
		contentPane.setImageConverter(new Function<SR6MetaType,Image>(){
			public Image apply(SR6MetaType value) {				
				InputStream in = SR6CharacterViewLayout.class.getResourceAsStream("images/metatype_"+value.getId()+".png");
				if (in!=null)
					return new Image(in);
				logger.error("Missing resource "+SR6CharacterViewLayout.class.getName()+" + images/metatype_"+value.getId()+".png");
				return null;
			}});
//		contentPane.setModificationConverter((m) -> SplitterTools.getModificationString(contentPane.getSelectedItem(),m));
//		contentPane.setChoiceConverter((c) -> SplitterTools.getChoiceString(contentPane.getSelectedItem(), c));
		contentPane.setModel(charGen.getModel());
		contentPane.setDecisionHandler( (r,c) -> {
			logger.warn("ToDo: make decision");
//			SplitterJFXUtil.openDecisionDialog(r, c, null);
		});
		contentPane.setItems(Shadowrun6Core.getItemList(SR6MetaType.class));
		
		/*
		 * Custom node
		 */
		btnRoll  = new Button(ResourceI18N.get(RES, "button.roll"));
		btnRoll.setStyle("-fx-background-color: dark; -fx-text-fill: light");
		cbGender = new ChoiceBox<>();
		cbGender.getItems().addAll(Gender.values());
		cbGender.setConverter(new StringConverter<Gender>() {
			public String toString(Gender key) {return ResourceI18N.get(RES,"gender."+key.name().toLowerCase());}
			public Gender fromString(String key) {return Gender.valueOf(key.toUpperCase());}
		});
		tfSize   = new TextField();
		tfSize.setPrefColumnCount(3);
		tfWeight = new TextField();
		tfWeight.setPrefColumnCount(3);
	}
	
	//-------------------------------------------------------------------
	private void addToCustom(Node node, String prop) {
		Label ret = new Label(ResourceI18N.get(RES, prop));
		ret.getStyleClass().add(JavaFXConstants.STYLE_HEADING5);
		
		customNode1.getChildren().add(new VBox(5, ret, node));
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		setContent(contentPane);
		
		customNode1 = new FlowPane(10, 10);
		addToCustom(cbGender, "label.gender");
		addToCustom(new HBox(5, tfSize, new Label("cm")), "label.size");
		addToCustom(new HBox(5, tfWeight, new Label("kg")), "label.weight");
		
		VBox cust = new VBox(10, btnRoll, customNode1);
		contentPane.setCustomNode1(new NodeWithTitleSkeleton(ResourceI18N.get(RES,"tab.custom"), cust));
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
		contentPane.selectedItemProperty().addListener( (ov,o,n) -> {
			IMetatypeController<SR6MetaType> ctrl = charGen.getMetatypeController();
			if (ctrl==null) {
				logger.error(charGen.getClass()+".getMetatypeController returns null  (internal "+charGen.getWrapped()+" ) of "+charGen);
			} else
			ctrl.select(n);
//			ctrl.rollGender();
			refresh();
		});
		
		btnRoll.setOnAction(ev -> {
			logger.info("Roll");
			IMetatypeController<SR6MetaType> ctrl = charGen.getMetatypeController();
//			ctrl.rollEyes();
//			ctrl.rollGender();
//			ctrl.rollHair();
//			ctrl.rollSizeAndWeight();
			refresh();
		});
		
		cbGender.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> charGen.getModel().setGender(n));
		tfSize.textProperty().addListener( (ov,o,n) -> {
			try {
				int size = Integer.parseInt(n);
//				charGen.getModel().setSize(size);
				tfSize.getStyleClass().remove("invalid");
			} catch (NumberFormatException e) {
				if (!tfSize.getStyleClass().contains("invalid"))
					tfSize.getStyleClass().add("invalid");
			}
		});
	}
	
	//-------------------------------------------------------------------
	private void refresh() {
		Shadowrun6Character model = charGen.getModel();
		cbGender.setValue(model.getGender());
//		tfHair.setText(charGen.getModel().getHairColor());
//		tfEyes.setText(charGen.getModel().getEyeColor());
//		tfSkin.setText(charGen.getModel().getSkinColor());
//		try {
//			tfSize.setText(String.valueOf(model.getSize()));
//			tfWeight.setText(String.valueOf(model.getWeight()));
//		} catch (Exception e) {
//			logger.warn("Found invalid data in textfields: "+e);
//		}
	}

//	//-------------------------------------------------------------------
//	private void refreshDataTab() {
//		logger.info("set data of "+spinner.getValue().getId());
//		// Data
//		List<String> attr = spinner.getValue().getModifications().stream()
//				.filter(m -> (m instanceof ValueModification))
//				.map( r -> SplitterTools.getModificationString(r))
//				.collect(Collectors.toList());
//		List<String> other = spinner.getValue().getModifications().stream()
//				.filter(m -> !(m instanceof ValueModification))
//				.map( r -> SplitterTools.getModificationString(r))
//				.collect(Collectors.toList());
//		Label leftLabel = new Label(String.join("\n", attr));
//		leftLabel.setWrapText(true);
//		Label rightLabel = new Label(String.join("\n", other));
//		rightLabel.setWrapText(true);
////		HBox columns = new HBox(20,leftLabel, rightLabel);
//		TilePane columns = new TilePane(Orientation.HORIZONTAL, 20, 10, leftLabel, rightLabel);
//		columns.setPrefColumns(2);
//		ScrollPane dataScroll = new ScrollPane(columns);
//		dataScroll.setMaxHeight(Double.MAX_VALUE);
//		tabData.setContent(dataScroll);
//	}

}
