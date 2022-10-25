package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.ResourceBundle;

import org.prelle.javafx.CloseType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;
import org.prelle.javafx.Mode;
import org.prelle.javafx.OptionalNodePane;
import org.prelle.javafx.Page;
import org.prelle.javafx.layout.FlexGridPane;

import de.rpgframework.ResourceI18N;
import de.rpgframework.genericrpg.HistoryElement;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.data.ComplexDataItem;
import de.rpgframework.genericrpg.data.ComplexDataItemValue;
import de.rpgframework.genericrpg.data.GenericRPGTools;
import de.rpgframework.jfx.GenericDescriptionVBox;
import de.rpgframework.jfx.section.HistoryElementSection;
import de.rpgframework.shadowrun.Contact;
import de.rpgframework.shadowrun.ContactType;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.SIN.FakeRating;
import de.rpgframework.shadowrun6.Shadowrun6Tools;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.SR6HistoryElementListCell;
import de.rpgframework.shadowrun6.chargen.jfx.pane.SR6RewardPane;
import de.rpgframework.shadowrun6.chargen.jfx.section.CreationSection;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

/**
 * @author prelle
 *
 */
public class CareerPage extends Page {

	private final static Logger logger = System.getLogger(CareerPage.class.getPackageName());
	
	private final static ResourceBundle RES = ResourceBundle.getBundle(SR6CharacterViewLayout.class.getName());
	
	private transient SR6CharacterController ctrl;
	
	private CreationSection secCreation;
	private HistoryElementSection secHistory;
	
	private FlexGridPane flex;
	private OptionalNodePane layout;

	//-------------------------------------------------------------------
	public CareerPage() {
		super(ResourceI18N.get(RES, "page.career.title"));
		initComponents();
		initLayout();
		initInteractivity();
	}
	
	//-------------------------------------------------------------------
	private void initComponents() {
		initCreationInfo();
		initHistoryInfo();
	}
	
	//-------------------------------------------------------------------
	private void initCreationInfo() {
		secCreation = new CreationSection();
		secCreation.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secCreation, 4);
		FlexGridPane.setMinHeight(secCreation, 6);
		FlexGridPane.setMediumWidth(secCreation, 5);
		FlexGridPane.setMediumHeight(secCreation, 6);
	}
	
	//-------------------------------------------------------------------
	private void initHistoryInfo() {
		secHistory = new HistoryElementSection() {
			public void onAdd() {
				logger.log(Level.ERROR, "onAdd");
				SR6RewardPane box = new SR6RewardPane(null);
				ManagedDialog dialog = new ManagedDialog(ResourceI18N.get(RES, "page.career.reward.add.title"), box, CloseType.OK, CloseType.CANCEL)
						.setImage(new Image(getClass().getResourceAsStream("Ork1.png")))
						.setImagePosition(HPos.RIGHT);
				CloseType close = FlexibleApplication.getInstance().showAlertAndCall(dialog, null);
				if (close==CloseType.OK) {
					Reward reward = box.getData();
					logger.log(Level.WARNING, "Add reward "+reward);
					Shadowrun6Tools.applyReward(ctrl.getModel(), reward);
					ctrl.runProcessors();
					secHistory.setData( GenericRPGTools.convertToHistoryElementList(ctrl.getModel(), false));
					refresh();
				}
			}
			public void onDelete(HistoryElement elem) {
				logger.log(Level.ERROR, "onDelete "+elem);
				for (Reward reward : elem.getGained()) {
					Shadowrun6Tools.removeReward(ctrl.getModel(), reward);
				}
				ctrl.runProcessors();
				secHistory.setData( GenericRPGTools.convertToHistoryElementList(ctrl.getModel(), false));
				refresh();
			}
		};
		secHistory.getListView().setCellFactory( (lv) -> new SR6HistoryElementListCell(RES));
		secHistory.setMaxHeight(Double.MAX_VALUE);
		FlexGridPane.setMinWidth(secHistory, 5);
		FlexGridPane.setMinHeight(secHistory, 6);
		FlexGridPane.setMediumWidth(secHistory, 5);
		FlexGridPane.setMediumHeight(secHistory, 9);
	}
	
	//-------------------------------------------------------------------
	private void initLayout() {
		flex = new FlexGridPane();
		flex.setSpacing(20);
		flex.getChildren().addAll(secCreation, secHistory);
		
		layout = new OptionalNodePane(flex, new Label("Select something to get a description"));
		setContent(layout);
		super.setMode(Mode.REGULAR);
	}
	
	//-------------------------------------------------------------------
	private void initInteractivity() {
//		secSINs.showHelpForProperty().addListener( (ov,o,n) -> showDescription(n));
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
	private void showDescription(Contact n) {
		logger.log(Level.INFO, "Show contact type "+n);
		if (n==null || n.getType()==null) {
			layout.setOptional(null);
		} else {
			ContactType t = n.getType();
			GenericDescriptionVBox desc = new GenericDescriptionVBox( null);
			desc.setData(t.getName(Locale.getDefault()), null, t.getDescription(Locale.getDefault()));
			layout.setOptional( desc);
			layout.setTitle(t.getName(Locale.getDefault()));
		}
	}

	//-------------------------------------------------------------------
	private void showDescription(SIN n) {
		logger.log(Level.INFO, "Show SIN "+n);
		if (n==null ) {
			layout.setOptional(null);
		} else {
			FakeRating rating = n.getQuality();
			GenericDescriptionVBox desc = new GenericDescriptionVBox( null);
			desc.setData(rating.name(), null, n.getDescription());
			layout.setOptional( desc);
			layout.setTitle(rating.name());
		}
	}
	
	//-------------------------------------------------------------------
	public void setController(SR6CharacterController ctrl) {
		logger.log(Level.INFO, "setController");
		if (ctrl==null)
			throw new NullPointerException("controller is null");
		this.ctrl = ctrl;
		
		secCreation.updateController(ctrl);

		refresh();
	}
	
	//-------------------------------------------------------------------
	public void refresh() {
		secCreation.refresh();
		secHistory.refresh();
		
		secHistory.setData( GenericRPGTools.convertToHistoryElementList(ctrl.getModel(), false));
	}

}
