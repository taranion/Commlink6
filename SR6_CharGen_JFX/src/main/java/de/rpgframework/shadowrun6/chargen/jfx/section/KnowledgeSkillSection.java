package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import de.rpgframework.genericrpg.chargen.CharacterController;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.section.ListSection;
import de.rpgframework.shadowrun.SkillType;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.listcell.KnowledgeSkillListCell;
import de.rpgframework.shadowrun6.chargen.jfx.selector.ChoiceSelectorDialog;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * @author prelle
 *
 */
@SuppressWarnings("rawtypes")
public class KnowledgeSkillSection extends ListSection<SR6SkillValue> implements IShadowrunCharacterControllerProvider<IShadowrunCharacterController> {
	
	protected Logger logger = System.getLogger(getClass().getPackageName());

	private SR6CharacterController control;

	//-------------------------------------------------------------------
	public KnowledgeSkillSection(String title) {
		super(title);
		
		list.setCellFactory(cell -> new KnowledgeSkillListCell(this, null));
		
		list.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			if (n!=null) {
				btnDel.setDisable( !control.getSkillController().canBeDeselected(n).get() );
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		logger.log(Level.DEBUG, "onAdd");
		SR6Skill skill = Shadowrun6Core.getSkill("knowledge");
		ChoiceSelectorDialog<SR6Skill, SR6SkillValue> dialog = new ChoiceSelectorDialog<>(control.getSkillController());
		Decision[] decisions = dialog.apply(skill, skill.getChoices());
		OperationResult<SR6SkillValue> sVal = control.getSkillController().select(skill, decisions);
		if (sVal.wasSuccessful()) {
			list.getItems().add(sVal.get());
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(SR6SkillValue item) {
		logger.log(Level.DEBUG, "onDelete");
		if (control.getSkillController().deselect(item)) {
			list.getItems().remove(item);
		}
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<SR6SkillValue> selectedSkillProperty() {
		return list.getSelectionModel().selectedItemProperty();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	public void refresh() {
		if (control!=null) {
			list.getItems().setAll(control.getModel().getSkillValues(SkillType.KNOWLEDGE));
		} else {
			list.getItems().clear();
		}
	}

	//-------------------------------------------------------------------
	public void updateController(CharacterController ctrl) {
		control = (SR6CharacterController) ctrl;
		logger.log(Level.INFO, "#############updateController with model "+control.getModel());
		if (control.getModel()==null) throw new NullPointerException("Controller has NULL as model");
//		table.setModel(control.getModel());
//		table.setController(control.getSkillController());
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.genericrpg.chargen.CharacterControllerProvider#getCharacterController()
	 */
	@Override
	public IShadowrunCharacterController getCharacterController() {
		return control;
	}

}
