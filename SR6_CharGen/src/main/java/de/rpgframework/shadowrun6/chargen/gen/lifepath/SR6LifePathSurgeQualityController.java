package de.rpgframework.shadowrun6.chargen.gen.lifepath;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import de.rpgframework.genericrpg.Possible;
import de.rpgframework.genericrpg.chargen.OperationResult;
import de.rpgframework.genericrpg.data.Choice;
import de.rpgframework.genericrpg.data.ChoiceOption;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.Quality;
import de.rpgframework.shadowrun.Quality.QualityType;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun6.chargen.gen.CommonQualityGenerator;

public class SR6LifePathSurgeQualityController extends CommonQualityGenerator {

	private final SR6LifepathCharacterGenerator lifepath;

	//-------------------------------------------------------------------
	public SR6LifePathSurgeQualityController(SR6LifepathCharacterGenerator parent) {
		super(parent);
		lifepath = parent;
	}

	//-------------------------------------------------------------------
	@Override
	public List<Quality> getAvailable() {
		return super.getAvailable().stream()
				.filter(quality -> quality.getType()==QualityType.METAGENIC)
				.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	@Override
	public List<QualityValue> getSelected() {
		List<String> configured = lifepath.getSettings().getSurgeQualities().stream()
				.map(QualityValue::getKey)
				.toList();
		return super.getSelected().stream()
				.filter(value -> configured.contains(value.getKey()))
				.collect(Collectors.toList());
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeSelected(Quality value, Decision... decisions) {
		QualityValue candidate = toQualityValue(value, decisions);
		int candidateCost = getSurgeKarmaCost(candidate);
		karmaSURGE = value!=null && value.getType()==QualityType.METAGENIC && !value.isPositive()
				? Math.min(getKarmaForSURGE(), 30 - Math.abs(candidateCost))
				: getKarmaForSURGE();
		return super.canBeSelected(value, decisions);
	}

	//-------------------------------------------------------------------
	@Override
	public OperationResult<QualityValue> select(Quality quality, Decision... decisions) {
		Possible possible = canBeSelected(quality, decisions);
		if (!possible.get()) {
			logger.log(Level.WARNING, "Trying to select({0}) but not possible because {1}", quality, possible.getMostSevere());
			return new OperationResult<QualityValue>(possible);
		}

		QualityValue value = toQualityValue(quality, decisions);
		value.setInjectedBy(this);
		lifepath.getSettings().addSurgeQuality(value);

		if (!model.hasQuality(quality.getId())) {
			model.addQuality(value);
		}
		parent.runProcessors();
		QualityValue selected = model.getQuality(quality.getId());
		return new OperationResult<QualityValue>(selected!=null?selected:value);
	}

	//-------------------------------------------------------------------
	private QualityValue toQualityValue(Quality quality, Decision... decisions) {
		QualityValue value = new QualityValue(quality, 1);
		for (Decision decision : decisions) {
			if (decision!=null)
				value.addDecision(decision);
		}
		return value;
	}

	//-------------------------------------------------------------------
	@Override
	public int getKarmaForSURGE() {
		return lifepath.getSettings().getSurgeQualities().stream()
				.mapToInt(SR6LifePathSurgeQualityController::getSurgeKarmaCost)
				.sum();
	}

	//-------------------------------------------------------------------
	public static int getSurgeKarmaCost(QualityValue value) {
		if (value==null || value.getResolved()==null)
			return 0;
		int cost = value.getResolved().getKarmaCost();
		for (Decision decision : value.getDecisions()) {
			if (decision==null)
				continue;
			Choice choice = value.getResolved().getChoice(decision.getChoiceUUID());
			if (choice==null)
				continue;
			ChoiceOption option = choice.getSubOption(decision.getValue());
			if (option!=null)
				cost += (int) option.getCost();
		}
		return value.getResolved().isPositive()?cost:-cost;
	}

	//-------------------------------------------------------------------
	@Override
	public Possible canBeDeselected(QualityValue value) {
		if (value==null)
			return Possible.FALSE;
		boolean selected = lifepath.getSettings().getSurgeQualities().stream()
				.anyMatch(configured -> value.getKey().equals(configured.getKey()));
		return selected?Possible.TRUE:Possible.FALSE;
	}

	//-------------------------------------------------------------------
	@Override
	public boolean deselect(QualityValue value) {
		if (!canBeDeselected(value).get())
			return false;
		lifepath.getSettings().removeSurgeQuality(value);
		super.deselect(value);
		parent.runProcessors();
		return true;
	}
}
