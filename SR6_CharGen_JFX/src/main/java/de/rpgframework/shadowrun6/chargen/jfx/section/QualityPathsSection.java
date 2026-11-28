package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.prelle.javafx.ApplicationScreen;
import javafx.scene.control.ButtonType;
import org.prelle.javafx.FlexibleApplication;
import org.prelle.javafx.ManagedDialog;

import de.rpgframework.ResourceI18N;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.jfx.section.ListSection;
import de.rpgframework.shadowrun6.QualityPath;
import de.rpgframework.shadowrun6.QualityPathValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6SelectAndDecide;
import de.rpgframework.shadowrun6.chargen.jfx.page.QualityPathPage;
import de.rpgframework.shadowrun6.chargen.jfx.pane.QualityPathSelector;

/**
 * @author prelle
 *
 */
public class QualityPathsSection extends ListSection<QualityPathValue> {

	private final static Logger logger = System.getLogger(QualityPathsSection.class.getPackageName());

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(QualityPathsSection.class.getPackageName()+".Section");

	protected SR6CharacterController control;
	protected Shadowrun6Character model;

	//-------------------------------------------------------------------
	public QualityPathsSection() {
		super(RES.getString("section.qualitypaths.title"));
		list.setCellFactory(lv -> new ComplexDataItemValueListCell<QualityPath,QualityPathValue>(() -> control.getQualityPathController()){
			{
				addAction(new CellAction("Edit",ICON_PEN, ResourceI18N.get(RES, "label.edit"), (act,item) -> editClicked(item)));
			}
			protected OperationResult<QualityPathValue> editClicked(QualityPathValue ref) {
				logger.log(Level.WARNING, "TODO: editClicked for "+getClass());
				QualityPathPage page = new QualityPathPage(control,ref);
				ApplicationScreen screen = new ApplicationScreen(page);
				FlexibleApplication.getInstance().openScreen(screen);
				return new OperationResult<QualityPathValue>(ref);
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onAdd()
	 */
	@Override
	protected void onAdd() {
		SR6SelectAndDecide.of(control, control.getQualityPathController(), QualityPath.class, QualityPathValue.class)
				.title(ResourceI18N.get(RES, "section.qualitypaths.selector.title"))
				.execute();
		list.refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.jfx.section.ListSection#onDelete(java.lang.Object)
	 */
	@Override
	protected void onDelete(QualityPathValue item) {
		// TODO Auto-generated method stub

	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
//		logger.log(Level.DEBUG, "updateController");
		this.control = ctrl;
		model = ctrl.getModel();
		refresh();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.Section#refresh()
	 */
	public void refresh() {
		if (model==null) return;

		list.getItems().setAll(model.getQualityPaths());
	}

}
