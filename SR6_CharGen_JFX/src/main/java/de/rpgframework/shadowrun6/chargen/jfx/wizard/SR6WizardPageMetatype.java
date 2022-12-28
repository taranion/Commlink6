package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.JavaFXConstants;
import org.prelle.javafx.NodeWithTitle;
import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.Wizard;
import org.prelle.javafx.WizardPage;

import de.rpgframework.ResourceI18N;
import de.rpgframework.classification.Gender;
import de.rpgframework.genericrpg.chargen.BasicControllerEvents;
import de.rpgframework.genericrpg.chargen.ControllerEvent;
import de.rpgframework.genericrpg.chargen.ControllerListener;
import de.rpgframework.jfx.DataItemSpinnerPane;
import de.rpgframework.jfx.wizard.NumberUnitBackHeader;
import de.rpgframework.shadowrun.BodyType;
import de.rpgframework.shadowrun.chargen.charctrl.IMetatypeController;
import de.rpgframework.shadowrun.chargen.jfx.CommonShadowrunJFXResourceHook;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.gen.GeneratorWrapper;
import de.rpgframework.shadowrun6.chargen.jfx.SR6ReferenceTypeConverter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class SR6WizardPageMetatype extends WizardPage implements ControllerListener {
	
	private final static Logger logger = System.getLogger(SR6WizardPageMetatype.class.getPackageName()+".meta");
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6WizardPageGear.class.getPackageName()+".SR6WizardPages");

	private GeneratorWrapper charGen;
	
	private DataItemSpinnerPane<SR6MetaType> contentPane;
	
	private ChoiceBox<Gender> cbGender;
	private Button btnRoll;
	private TextField tfSize;
	private TextField tfWeight;
	private ChoiceBox<BodyType> cbSpecialBody;
	private FlowPane customNode1;
	private NumberUnitBackHeader backHeader;
	
	private Comparator<SR6MetaType> comparator;
	/* When TRUE asynchronous updates are happening and user events shall be ignored */
	private boolean updating;
	
	//-------------------------------------------------------------------
	public SR6WizardPageMetatype(Wizard wizard, GeneratorWrapper charGen) {
		super(wizard);
		this.charGen = charGen;
		setTitle(ResourceI18N.get(RES, "page.title"));
		initComponents();
		initLayout();
//		refreshDataTab();
		
		contentPane.getValueFactory().setValue(charGen.getModel().getMetatype());
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		comparator = new Comparator<SR6MetaType>() {
			public int compare(SR6MetaType meta1, SR6MetaType meta2) {
				if (meta1.getId().equals("human")) return -1;
				if (meta2.getId().equals("human")) return +1;
				if (meta1.getVariantOf()!=null && meta1.getVariantOf().getId().equals("human") && (!meta2.getId().equals("human"))) return -1;
				if (meta2.getVariantOf()!=null && meta2.getVariantOf().getId().equals("human") && (!meta1.getId().equals("human"))) return +1;
				String comp1 = meta1.getName();
				if (meta1.getVariantOf()!=null)
					comp1 = meta1.getVariantOf().getName()+" / "+comp1;
				String comp2 = meta2.getName();
				if (meta2.getVariantOf()!=null)
					comp2 = meta2.getVariantOf().getName()+" / "+comp2;
				return comp1.compareTo(comp2);
			};
		};
		
		contentPane = new DataItemSpinnerPane<SR6MetaType>();
		contentPane.setId("species");
		contentPane.setImageConverter(new Function<SR6MetaType,Image>(){
			public Image apply(SR6MetaType value) {	
				String name = (value.getVariantOf()==null)
						?
								"images/metatypes/metatype_"+value.getId()+".jpg"
								:
								"images/metatypes/metatype_"+value.getVariantOf().getId()+"_"+value.getId()+".jpg";
				InputStream in = CommonShadowrunJFXResourceHook.class.getResourceAsStream(name);
				System.err.println(getClass()+": Search for "+name+" = "+in);
				if (in!=null) {
					Image img = new Image(in);
					if (img.isError()) {
						System.err.println("Error loading "+name+": "+img.getException());
					}
					return img;
				}
				logger.log(Level.ERROR, "Missing resource "+CommonShadowrunJFXResourceHook.class.getPackage().getName()+" + "+name);
				System.err.println("Missing resource "+CommonShadowrunJFXResourceHook.class.getPackage().getName()+" + "+name);
				return null;
			}});
		contentPane.setModificationConverter((m) -> Shadowrun6Tools.getModificationString(contentPane.getSelectedItem(),m));
		contentPane.setReferenceTypeConverter(new SR6ReferenceTypeConverter<>());
		contentPane.setNameConverter( meta -> {
			if (meta==null) return "-";
			String suffix = " ("+meta.getKarma()+" Karma)";
			if (meta.getVariantOf()==null) {
				if (meta.getKarma()==0)
					return meta.getName();
				return meta.getName()+suffix;
			}
			return "- "+meta.getName()+suffix;
		});
//		contentPane.setModificationConverter((m) -> SplitterTools.getModificationString(contentPane.getSelectedItem(),m));
//		contentPane.setChoiceConverter((c) -> SplitterTools.getChoiceString(contentPane.getSelectedItem(), c));
		contentPane.setModel(charGen.getModel());
		contentPane.setDecisionHandler( (r,c) -> {
			logger.log(Level.WARNING, "ToDo: make decision");
//			SplitterJFXUtil.openDecisionDialog(r, c, null);
		});
		
		List<SR6MetaType> items = ((IMetatypeController<SR6MetaType>)charGen.getMetatypeController()).getAvailable().stream()
				.map(mo -> (SR6MetaType)mo.getResolved())
				.filter(p -> charGen.showDataItem(p))
				.collect(Collectors.toList());
		Collections.sort(items, comparator);
		logger.log(Level.WARNING, "Available: "+items);
		
		contentPane.setItems(items);
		contentPane.setShowDecisionColumn(false);
		
		/*
		 * Custom node
		 */
		btnRoll  = new Button(ResourceI18N.get(RES, "button.roll"));
		btnRoll.setStyle("-fx-background-color: accent; -fx-text-fill: light");
		cbGender = new ChoiceBox<>();
		cbGender.getItems().addAll(Gender.values());
		cbGender.setConverter(new StringConverter<Gender>() {
			public String toString(Gender key) {
				if (key==null) return "?";
				return ResourceI18N.get(RES,"gender."+key.name().toLowerCase());
			}
			public Gender fromString(String key) {return Gender.valueOf(key.toUpperCase());}
		});
		tfSize   = new TextField();
		tfSize.setPrefColumnCount(3);
		tfWeight = new TextField();
		tfWeight.setPrefColumnCount(3);
		
		cbSpecialBody = new ChoiceBox<BodyType>();
		cbSpecialBody.getItems().addAll(BodyType.values());
		cbSpecialBody.setConverter(new StringConverter<BodyType>() {
			public String toString(BodyType key) {
				if (key==null) return "?";
				return ResourceI18N.get(RES,"bodytype."+key.name().toLowerCase());
			}
			public BodyType fromString(String key) {return BodyType.valueOf(key.toUpperCase());}
		});
		cbSpecialBody.setValue(BodyType.METAHUMAN);
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
		addToCustom(cbSpecialBody, "label.bodytype");
		
		VBox cust = new VBox(10, btnRoll, customNode1);
		contentPane.setCustomNode1(new NodeWithTitle(ResourceI18N.get(RES,"tab.custom"), cust));
		
		// Be smaller in larger screens, but higher on smaller screens
		if (ResponsiveControlManager.getCurrentMode()==WindowMode.MINIMAL) {
			customNode1.setStyle("-fx-max-width: 40em");
		} else {
			customNode1.setStyle("-fx-max-width: 12em");
		}

		// Back header
		backHeader = new NumberUnitBackHeader(ResourceI18N.get(RES, "label.karma"));
		backHeader.setValue(charGen.getModel().getKarmaFree());
		HBox.setMargin(backHeader, new Insets(0,10,0,10));
//		if (ResponsiveControlManager.getCurrentMode()==WindowMode.EXPANDED) {
//			super.setBackHeader(null);
//		} else {
			super.setBackHeader(backHeader);
//		}
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initInteractivity() {
		contentPane.selectedItemProperty().addListener( (ov,o,n) -> {
			if (updating) return;
			if (n==null) return;
			IMetatypeController<SR6MetaType> ctrl = charGen.getMetatypeController();
			if (ctrl==null) {
				logger.log(Level.ERROR, charGen.getClass()+".getMetatypeController returns null  (internal "+charGen.getWrapped()+" ) of "+charGen);
			} else
			ctrl.select(n);
			refresh();
		});
		
		btnRoll.setOnAction(ev -> roll());
		setOnExtraActionHandler( button -> onExtraAction(button));
		
		cbGender.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> charGen.getModel().setGender(n));
		cbSpecialBody.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			charGen.getMetatypeController().selectBodyType(n);
		});
		tfSize.textProperty().addListener( (ov,o,n) -> {
			if (updating) return;
			try {
				int size = Integer.parseInt(n);
				charGen.getModel().setSize(size);
				tfSize.getStyleClass().remove("invalid");
			} catch (NumberFormatException e) {
				if (!tfSize.getStyleClass().contains("invalid"))
					tfSize.getStyleClass().add("invalid");
			}
		});
		tfWeight.textProperty().addListener( (ov,o,n) -> {
			if (updating) return;
			try {
				int weight = Integer.parseInt(n);
				charGen.getModel().setWeight(weight);
				tfWeight.getStyleClass().remove("invalid");
			} catch (NumberFormatException e) {
				if (!tfWeight.getStyleClass().contains("invalid"))
					tfWeight.getStyleClass().add("invalid");
			}
		});
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#pageVisited()
	 */
	@Override
	public void pageVisited() {
		refresh();
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void refresh() {
		updating = true;
		try {
			Shadowrun6Character model = charGen.getModel();
			backHeader.setValue(model.getKarmaFree());
			cbGender.setValue(model.getGender());
			try {
				tfSize.setText(String.valueOf(model.getSize()));
				tfWeight.setText(String.valueOf(model.getWeight()));
			} catch (Exception e) {
				logger.log(Level.WARNING, "Found invalid data in textfields: " + e);
			}
			
			List<SR6MetaType> items = ((IMetatypeController<SR6MetaType>)charGen.getMetatypeController()).getAvailable().stream()
					.map(mo -> (SR6MetaType)mo.getResolved())
					.filter(p -> charGen.showDataItem(p))
					.collect(Collectors.toList());
			Collections.sort(items, comparator);
			if (items.contains(null))
				items.remove(null);
			contentPane.setItems(items);

			if (items.contains(model.getMetatype()) && model.getMetatype() != contentPane.getSelectedItem()) {
				contentPane.setSelectedItem(model.getMetatype());
			}

			// Only metahumans can use body type
			if (model.getMetatype() != null) {
				cbSpecialBody.setDisable(!model.getMetatype().isMetahuman());
				if (!model.getMetatype().isMetahuman()) {
					cbSpecialBody.setValue(BodyType.METAHUMAN);
				}
			}
		} finally {
			updating = false;
		}
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.WizardPage#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
		super.setResponsiveMode(value);
		
		if (value==WindowMode.MINIMAL) {
			customNode1.setStyle("-fx-max-width: 40em");
		} else {
			customNode1.setStyle("-fx-max-width: 12em");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.ControllerListener#handleControllerEvent(de.rpgframework.genericrpg.chargen.ControllerEvent, java.lang.Object[])
	 */
	@Override
	public void handleControllerEvent(ControllerEvent type, Object... param) {
		logger.log(Level.WARNING, "RCV {0}",type);
		if (type==BasicControllerEvents.CHARACTER_CHANGED) 
			refresh();
		
		if (type==BasicControllerEvents.GENERATOR_CHANGED) {
			refresh();
//			bxLine.setManaged(charGen.getAdeptPowerController().canBuyPowerPoints());
//			bxLine.setVisible(charGen.getAdeptPowerController().canBuyPowerPoints());
		}
	}
	
	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void roll() {
		logger.log(Level.INFO, "Roll");
		IMetatypeController<SR6MetaType> ctrl = charGen.getMetatypeController();
		ctrl.randomizeSizeWeight();
		refresh();

	}
	
	//-------------------------------------------------------------------
	private void onExtraAction(CloseType button) {
		switch (button) {
		case RANDOMIZE:
			IMetatypeController<SR6MetaType> ctrl = charGen.getMetatypeController();
			ctrl.roll();
			break;
		default:
			logger.log(Level.WARNING, "ToDo: handle "+button);
		}
	}

}
