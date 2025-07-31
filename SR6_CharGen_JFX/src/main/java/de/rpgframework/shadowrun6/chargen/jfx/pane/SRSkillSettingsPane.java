package de.rpgframework.shadowrun6.chargen.jfx.pane;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.prelle.javafx.TitledComponent;

import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.shadowrun.chargen.charctrl.ISkillController;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SRSkillSettingsPane extends VBox {

	private final static Logger logger = System.getLogger(SRSkillSettingsPane.class.getPackageName());
	
	private  ISkillController<SR6Skill, SR6SkillValue> control;
	private UUID nameUUID;
	private SR6SkillValue data;
	
	private TextField tfName;
	private boolean eventBlocked;
	
	private Map<SkillSpecialization<SR6Skill>, CheckBox> boxesSpecia;
	private Map<SkillSpecialization<SR6Skill>, CheckBox> boxesExpert;
	
	//-------------------------------------------------------------------
	public SRSkillSettingsPane(SR6SkillValue data, ISkillController<SR6Skill, SR6SkillValue> ctrl) {
		this.data = data;
		this.control = ctrl;
		if (!data.getModifyable().getChoices().isEmpty()) {
			nameUUID = data.getModifyable().getChoices().get(0).getUUID();
		}
		
		initComponents();
		initLayout();
		initInteractivity();
		refresh();
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		tfName = new TextField();		
		boxesSpecia = new HashMap<>();
		boxesExpert = new HashMap<>();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void initLayout() {	
		setSpacing(20);
		
		TitledComponent tcName = new TitledComponent("Name", tfName);
		if (nameUUID!=null)
		getChildren().add(tcName);
		
		GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);
		int y=0;
		boxesSpecia.clear();
		boxesExpert.clear();
		for (SkillSpecialization<?> spec : data.getSkill().getSpecializations()) {
			y++;
			Label lbName = new Label(spec.getName(Locale.getDefault()));
			CheckBox cbSpecia = new CheckBox();
			CheckBox cbExpert = new CheckBox();
			grid.add(lbName, 0, y);
			grid.add(cbSpecia, 1, y);
			grid.add(cbExpert, 2, y);
			boxesSpecia.put((SkillSpecialization<SR6Skill>) spec, cbSpecia);
			boxesExpert.put((SkillSpecialization<SR6Skill>) spec, cbExpert);
			
			cbSpecia.selectedProperty().addListener( (ov,o,n) -> changeCheckbox(cbSpecia, (SkillSpecialization<SR6Skill>) spec, false, n));
			cbExpert.selectedProperty().addListener( (ov,o,n) -> changeCheckbox(cbExpert, (SkillSpecialization<SR6Skill>) spec, true, n));
		}
		getChildren().add(grid);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		if (nameUUID!=null) {
			tfName.textProperty().addListener( (ov,o,n) -> data.updateDecision(nameUUID, n));
		}
	}

	//-------------------------------------------------------------------
	private void changeCheckbox(CheckBox cb, SkillSpecialization<SR6Skill> spec, boolean expert, boolean selected) {
		if (eventBlocked) return;
		
		if (selected) {
			OperationResult<SkillSpecializationValue<SR6Skill>> res = control.select(data, spec, expert);
			if (!res.wasSuccessful()) 
				logger.log(Level.WARNING, "Selecting specialization failed: {}", res.toString());
		} else {
			SkillSpecializationValue<SR6Skill> sVal = data.getSpecialization(spec);
			boolean res = control.deselect(data,sVal);
			if (!res) 
				logger.log(Level.WARNING, "Deselecting specialization failed");
		}
		refresh();
	}
	

	//-------------------------------------------------------------------
	private void refresh() {
		eventBlocked = true;
		
		if (nameUUID!=null && data.getDecision(nameUUID)!=null) {
			tfName.setText(data.getDecision(nameUUID).getValue());
		} else
			tfName.setText(null);
		
		for (SkillSpecialization<SR6Skill> spec : boxesSpecia.keySet()) {
			CheckBox cb = boxesSpecia.get(spec);
			SkillSpecializationValue<SR6Skill> val =  data.getSpecialization(spec);
			boolean selected = val!=null && val.getDistributed()==0;
			cb.setSelected(selected);
			if (selected) {
				cb.setDisable( !control.canDeselectSpecialization(data, val).get());
			} else {
				cb.setDisable( !control.canSelectSpecialization(data, spec, false).get());
			}
		}
		for (SkillSpecialization<SR6Skill> spec : boxesExpert.keySet()) {
			CheckBox cb = boxesExpert.get(spec);
			SkillSpecializationValue<SR6Skill> val =  data.getSpecialization(spec);
			logger.log(Level.DEBUG, "refreshing "+spec+" is "+val+" / "+data.getSpecializations());
			boolean selected = val!=null && val.getDistributed()==1;
			logger.log(Level.DEBUG, "set expertise "+spec+" to "+selected);
			cb.setSelected(selected);
			logger.log(Level.DEBUG, "set expertise selected to "+selected);
			if (selected) {
				cb.setDisable( !control.canDeselectSpecialization(data, val).get());
			} else {
				cb.setDisable( !control.canSelectSpecialization(data, spec, true).get());
			}
		}
		eventBlocked = false;
		
	}

}
