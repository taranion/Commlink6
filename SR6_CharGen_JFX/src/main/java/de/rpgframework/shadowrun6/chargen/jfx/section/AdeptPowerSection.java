package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.rpgframework.ResourceI18N;
import de.rpgframework.jfx.ListSection;
import de.rpgframework.shadowrun.AdeptPowerValue;
import de.rpgframework.shadowrun.ShadowrunCharacter;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.jfx.section.QualitySection;
import de.rpgframework.shadowrun6.SR6SkillValue;

/**
 * @author prelle
 *
 */
public class AdeptPowerSection extends ListSection<AdeptPowerValue> {

	private final static Logger logger = System.getLogger(QualitySection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(QualitySection.class.getPackageName()+".Selection");

	private IShadowrunCharacterController control;
	private ShadowrunCharacter model;

	//-------------------------------------------------------------------
	public AdeptPowerSection(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(AdeptPowerValue item) {
		logger.log(Level.DEBUG, "onDelete");
//		if (control.getSkillController().deselect(item)) {
//			list.getItems().remove(item);
//		}
	}

	//-------------------------------------------------------------------
	public void updateController(IShadowrunCharacterController ctrl) {
		assert ctrl!=null;
		control = ctrl;
		model = (ShadowrunCharacter) ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void refresh() {
		logger.log(Level.TRACE, "refresh");

		if (model!=null)
			setData(model.getAdeptPowers());
		else
			setData(new ArrayList<>());
	}
	

}
