package de.rpgframework.shadowrun6.chargen.jfx.page;

import java.lang.System.Logger;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.jfx.SR6CharacterViewLayout;
import de.rpgframework.shadowrun6.chargen.jfx.section.CreationSection;
import de.rpgframework.shadowrun6.chargen.jfx.section.SR6CareerHistorySection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for RewardsPage.fxml (Career history and creation info).
 */
public class RewardsPageController {

	private final static Logger logger = System.getLogger(RewardsPageController.class.getPackageName());

	@FXML private CreationSection secCreation;
	@FXML private SR6CareerHistorySection secHistory;
	@FXML private Button btnAddReward;
	@FXML private Button btnAddDowntime;

	private Runnable onChange;

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		if (secHistory != null) {
			secHistory.setTitle(ResourceI18N.get(SR6CharacterViewLayout.UI, "page.career.history"));
			secHistory.setOnChange(() -> {
				if (secCreation != null) secCreation.refresh();
				if (onChange != null) onChange.run();
			});
		}
	}

	//-------------------------------------------------------------------
	public void setOnChange(Runnable r) {
		this.onChange = r;
	}

	//-------------------------------------------------------------------
	@FXML
	public void addReward(ActionEvent ev) {
		if (secHistory != null) secHistory.addReward();
	}

	//-------------------------------------------------------------------
	@FXML
	public void addDowntimeAction(ActionEvent ev) {
		if (secHistory != null) secHistory.addDowntimeAction();
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		if (secCreation != null) secCreation.updateController(ctrl);
		if (secHistory != null) secHistory.updateController(ctrl);
		refresh();
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (secCreation != null) secCreation.refresh();
		if (secHistory != null) secHistory.refresh();
	}

}
