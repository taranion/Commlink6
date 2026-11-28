package de.rpgframework.shadowrun6.chargen.jfx;

import java.lang.System.Logger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.prelle.javafx.Tabs;

import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.page.BasicPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.CombatPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.CyberwarePageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.GearPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.MagicPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.MatrixPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.ResonancePageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.RewardsPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.SkillPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.SocialPageController;
import de.rpgframework.shadowrun6.chargen.jfx.page.VehiclesPageController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

/**
 * Overview controller managing the vertical tabs and scrolling content pages.
 */
public class CharacterOverviewController {

	private final static Logger logger = System.getLogger(CharacterOverviewController.class.getPackageName());

	private SR6CharacterController control;

	@FXML private Label tabBasic;
	@FXML private Label tabSkills;
	@FXML private Label tabCyber;
	@FXML private Label tabCombat;
	@FXML private Label tabMatrix;
	@FXML private Label tabMagic;
	@FXML private Label tabResonance;
	@FXML private Label tabVehicle;
	@FXML private Label tabGear;
	@FXML private Label tabSocial;
	@FXML private Label tabRewards;
	@FXML private ScrollPane scroll;

	@FXML private Tabs tabs;
	@FXML private GridPane pageBasic;
	@FXML private GridPane pageSkills;
	@FXML private GridPane pageCyber;
	@FXML private GridPane pageCombat;
	@FXML private GridPane pageMatrix;
	@FXML private GridPane pageMagic;
	@FXML private GridPane pageResonance;
	@FXML private GridPane pageVehicles;
	@FXML private GridPane pageGear;
	@FXML private GridPane pageSocial;
	@FXML private GridPane pageRewards;

	@FXML private BasicPageController pageBasicController;
	@FXML private SkillPageController pageSkillsController;
	@FXML private CyberwarePageController pageCyberController;
	@FXML private CombatPageController pageCombatController;
	@FXML private MatrixPageController pageMatrixController;
	@FXML private MagicPageController pageMagicController;
	@FXML private ResonancePageController pageResonanceController;
	@FXML private VehiclesPageController pageVehiclesController;
	@FXML private GearPageController pageGearController;
	@FXML private SocialPageController pageSocialController;
	@FXML private RewardsPageController pageRewardsController;

	/** Tabs and the pages they point to, in the order the pages appear */
	private List<Map.Entry<Label, Node>> tabPages;
	/** Set while the scroll position is changed by code, or the tab by scrolling */
	private boolean syncing;

	//-------------------------------------------------------------------
	public CharacterOverviewController() {
	}

	//-------------------------------------------------------------------
	@FXML
	public void initialize() {
		tabPages = List.of(
				Map.entry(tabBasic    , pageBasic),
				Map.entry(tabSkills   , pageSkills),
				Map.entry(tabCyber    , pageCyber),
				Map.entry(tabCombat   , pageCombat),
				Map.entry(tabMatrix   , pageMatrix),
				Map.entry(tabMagic    , pageMagic),
				Map.entry(tabResonance, pageResonance),
				Map.entry(tabVehicle  , pageVehicles),
				Map.entry(tabGear     , pageGear),
				Map.entry(tabSocial   , pageSocial),
				Map.entry(tabRewards  , pageRewards)
				);

		tabs.selectedProperty().addListener((_, _, n) -> {
			if (syncing || n == null) return;
			tabPages.stream()
				.filter(e -> e.getKey() == n)
				.findFirst()
				.ifPresent(e -> scrollTo(e.getValue()));
		});

		// Select the tab matching the scroll position
		scroll.vvalueProperty().addListener((_, _, _) -> selectTabForScrollPosition());
	}

	//-------------------------------------------------------------------
	private void scrollTo(Node node) {
		double scrollable = scrollableHeight();
		double y = node.getBoundsInParent().getMinY();

		// scrolling values range from 0 to 1
		syncing = true;
		try {
			scroll.setVvalue((scrollable > 0) ? Math.clamp(y / scrollable, 0.0, 1.0) : 0.0);
		} finally {
			syncing = false;
		}

		// just for usability
		node.requestFocus();
	}

	//-------------------------------------------------------------------
	/**
	 * @return Height of the content that is not visible in the viewport
	 */
	private double scrollableHeight() {
		double height = scroll.getContent().getBoundsInLocal().getHeight();
		return height - scroll.getViewportBounds().getHeight();
	}

	//-------------------------------------------------------------------
	/**
	 * Select the tab of the page that occupies the upper part of the viewport,
	 * without scrolling to the top of that page.
	 */
	private void selectTabForScrollPosition() {
		if (syncing || tabPages == null) return;
		double viewport = scroll.getViewportBounds().getHeight();
		double top = scroll.getVvalue() * Math.max(0, scrollableHeight());
		// At the very end of the content, the last visible page counts
		double line = (scroll.getVvalue() >= 1.0) ? top + viewport - 1 : top + viewport / 4.0;

		Optional<Label> current = tabPages.stream()
			.filter(e -> e.getKey() != null && e.getValue().isVisible() && e.getValue().isManaged())
			.filter(e -> e.getValue().getBoundsInParent().getMinY() <= line)
			.reduce((_, b) -> b)
			.map(Map.Entry::getKey);
		current
			.filter(tab -> tabs.selectedProperty().get() != tab)
			.ifPresent(tab -> {
				syncing = true;
				try {
					tabs.selectedProperty().set(tab);
				} finally {
					syncing = false;
				}
			});
	}

	//-------------------------------------------------------------------
	public void refresh() {
		if (pageBasicController != null) pageBasicController.refresh();
		if (pageSkillsController != null) pageSkillsController.refresh();
		if (pageCyberController != null) pageCyberController.refresh();
		if (pageCombatController != null) pageCombatController.refresh();
		if (pageMatrixController != null) pageMatrixController.refresh();
		if (pageMagicController != null) pageMagicController.refresh();
		if (pageResonanceController != null) pageResonanceController.refresh();
		if (pageVehiclesController != null) pageVehiclesController.refresh();
		if (pageGearController != null) pageGearController.refresh();
		if (pageSocialController != null) pageSocialController.refresh();
		if (pageRewardsController != null) pageRewardsController.refresh();

		if (control != null && control.getModel() != null) {
			MagicOrResonanceType mtype = control.getModel().getMagicOrResonanceType();
			boolean usesMagic = mtype != null && mtype.usesMagic();
			if (pageMagic != null) {
				pageMagic.setVisible(usesMagic);
				pageMagic.setManaged(usesMagic);
			}
			if (tabMagic != null) {
				tabMagic.setVisible(usesMagic);
				tabMagic.setManaged(usesMagic);
			}

			boolean usesResonance = mtype != null && mtype.usesResonance();
			if (pageResonance != null) {
				pageResonance.setVisible(usesResonance);
				pageResonance.setManaged(usesResonance);
			}
			if (tabResonance != null) {
				tabResonance.setVisible(usesResonance);
				tabResonance.setManaged(usesResonance);
			}
		}
		showCareerPages();
	}

	//-------------------------------------------------------------------
	/**
	 * Runs and downtime only exist after character creation
	 */
	private void showCareerPages() {
		boolean career = control != null && !(control instanceof SR6CharacterGenerator);
		Stream.of(pageRewards, tabRewards).forEach(node -> {
			if (node != null) {
				node.setVisible(career);
				node.setManaged(career);
			}
		});
	}

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController control) {
		this.control = control;
		if (pageBasicController != null) pageBasicController.updateController(control);
		if (pageSkillsController != null) pageSkillsController.updateController(control);
		if (pageCyberController != null) pageCyberController.updateController(control);
		if (pageCombatController != null) pageCombatController.updateController(control);
		if (pageMatrixController != null) pageMatrixController.updateController(control);
		if (pageMagicController != null) pageMagicController.updateController(control);
		if (pageResonanceController != null) pageResonanceController.updateController(control);
		if (pageVehiclesController != null) pageVehiclesController.updateController(control);
		if (pageGearController != null) pageGearController.updateController(control);
		if (pageSocialController != null) pageSocialController.updateController(control);
		if (pageRewardsController != null) {
			pageRewardsController.updateController(control);
			pageRewardsController.setOnChange(this::refresh);
		}
		showCareerPages();
	}

}
