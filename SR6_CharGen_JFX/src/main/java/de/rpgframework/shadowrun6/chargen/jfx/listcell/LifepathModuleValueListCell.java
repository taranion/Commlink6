package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.chargen.ComplexDataItemController;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.jfx.cells.ComplexDataItemValueListCell;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.LifepathModule;
import de.rpgframework.shadowrun6.LifepathModuleValue;
import de.rpgframework.shadowrun6.SR6Quality;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;

/**
 * Renders selected life modules with resolved choice labels instead of raw ids.
 */
public class LifepathModuleValueListCell extends ComplexDataItemValueListCell<LifepathModule, LifepathModuleValue> {

	private Label lbChoices;

	//-------------------------------------------------------------------
	public LifepathModuleValueListCell(Supplier<ComplexDataItemController<LifepathModule, LifepathModuleValue>> ctrlProv) {
		super(ctrlProv);
		lbChoices = new Label();
		lbChoices.setWrapText(true);
	}

	//-------------------------------------------------------------------
	@Override
	public Parent getContentNode(LifepathModuleValue item) {
		return lbChoices;
	}

	//-------------------------------------------------------------------
	@Override
	public void updateItem(LifepathModuleValue item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item==null) {
			lbChoices.setText(null);
			return;
		}

		LifepathModule module = item.getResolved();
		if (module==null || module.getChoices()==null) {
			lbChoices.setText(null);
			return;
		}

		List<String> rawDecisions = module.getChoices().stream()
				.map(choice -> item.getDecision(choice.getUUID()))
				.filter(decision -> decision!=null && decision.getValue()!=null && !decision.getValue().isBlank())
				.map(Decision::getValue)
				.collect(Collectors.toList());
		List<String> decisions = module.getChoices().stream()
				.map(choice -> resolveDecision(item, choice))
				.filter(text -> text!=null && !text.isBlank())
				.collect(Collectors.toList());
		String subtitle = String.join(", ", decisions);
		boolean replaced = replaceRawDecisionLabels(getGraphic(), rawDecisions, subtitle);
		lbChoices.setText(replaced?null:subtitle);
	}

	//-------------------------------------------------------------------
	private boolean replaceRawDecisionLabels(Node node, List<String> rawValues, String replacement) {
		if (node==null)
			return false;
		boolean replaced = false;
		if (node instanceof Label label && rawValues.contains(label.getText())) {
			label.setText(replacement);
			replaced = true;
		}
		if (node instanceof Parent parent) {
			for (Node child : parent.getChildrenUnmodifiable()) {
				replaced |= replaceRawDecisionLabels(child, rawValues, replacement);
			}
		}
		return replaced;
	}

	//-------------------------------------------------------------------
	private String resolveDecision(LifepathModuleValue value, Choice choice) {
		Decision decision = value.getDecision(choice.getUUID());
		if (decision==null || decision.getValue()==null || decision.getValue().isBlank())
			return null;

		String raw = decision.getValue();
		if (choice.getChooseFrom()==ShadowrunReference.QUALITY) {
			SR6Quality quality = Shadowrun6Core.getItem(SR6Quality.class, raw);
			if (quality!=null)
				return quality.getName(Locale.getDefault());
		}
		if (choice.getChooseFrom()==ShadowrunReference.SKILL) {
			SR6Skill skill = Shadowrun6Core.getSkill(raw);
			if (skill!=null)
				return skill.getName(Locale.getDefault());
		}
		if (choice.getChooseFrom()==ShadowrunReference.ATTRIBUTE) {
			try {
				return ShadowrunAttribute.valueOf(raw).getName(Locale.getDefault());
			} catch (IllegalArgumentException e) {
				// fall through to raw value
			}
		}
		return raw;
	}

}
