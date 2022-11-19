package de.rpgframework.shadowrun6.chargen.gen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.items.CarryMode;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.CreatePoints;
import de.rpgframework.shadowrun6.PowerLevel;
import de.rpgframework.shadowrun6.SR6Lifestyle;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.lifepath.SR6LifePathSettings;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.PointBuyCharacterGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuySettings;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PrioritySettings;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6GearTool;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

public class ResetGenerator implements ProcessingStep {

	protected final static Logger logger = System.getLogger(ResetGenerator.class.getPackageName()+".reset");

	protected SR6CharacterGenerator charGen;
	
	//-------------------------------------------------------------------
	public ResetGenerator(SR6CharacterGenerator charGen) {
		this.charGen = charGen;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {
		// Reset all attributes
		Shadowrun6Character model = charGen.getModel();
		
		// Remove all items that are auto-injected
		for (CarriedItem<ItemTemplate> tmp : model.getCarriedItems()) {
			if (tmp.getInjectedBy()!=null) {
				logger.log(Level.WARNING, "Remove item {0} injected from {1}", tmp.getKey(), tmp.getInjectedBy());
				model.removeCarriedItem(tmp);
			}
		}
		
		// Remove all lifestyles that are auto-injected
		for (SR6Lifestyle tmp : model.getLifestyles()) {
			if (tmp.getInjectedBy()!=null)
				model.removeLifestyle(tmp);
		}
		
		model.setKarmaFree(50);
		model.setKarmaInvested(0);
		PowerLevel level = model.getPowerLevel();
		if (level==null) {
			level=PowerLevel.STANDARD;
			model.setPowerLevel(level);
		}
		
		if (charGen instanceof LifePathCharacterGenerator) {
			SR6LifePathSettings settings = model.getCharGenSettings(SR6LifePathSettings.class);
			switch (level) {
			case STREET_LEVEL:
				unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.LIFEPATH_MODULES.name(), 6));
				break;
			case EILTE:
				unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.LIFEPATH_MODULES.name(), 10));
				break;
			default:
				unprocessed.add(new ValueModification(ShadowrunReference.CREATION_POINTS, CreatePoints.LIFEPATH_MODULES.name(), 8));
				break;				
			}
		} else if (charGen instanceof PointBuyCharacterGenerator) {
			SR6PointBuySettings settings = model.getCharGenSettings(SR6PointBuySettings.class);
			settings.perAttrib.get(ShadowrunAttribute.MAGIC).base=0;
			settings.perAttrib.get(ShadowrunAttribute.RESONANCE).base=0;
			settings.cpBoughtAttrib = 0;
			settings.cpBoughtSpecial = 0;
			settings.cpToResources = 0;
			settings.cpToSkills = 0;
			settings.sumSpellsRituals = 0;
			switch (level) {
			case STREET_LEVEL:
				settings.characterPoints = 80;
				break;
			case EILTE:
				settings.characterPoints = 120;
				break;
			default:
				settings.characterPoints = 100;
				break;				
			}
			logger.log(Level.INFO, "Start with {0} character points", settings.characterPoints);
		} else {
			SR6PrioritySettings settings = model.getCharGenSettings(SR6PrioritySettings.class);
			if (level==PowerLevel.PRIME_RUNNER)
				model.setKarmaFree(100);
		}
		
		// Ensure there is a device for unused software
		CarriedItem<ItemTemplate> item = model.getCarriedItem(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE);
		if (item==null) {
			item = new CarriedItem<ItemTemplate>(Shadowrun6Core.getItem(ItemTemplate.class,"meta_link"), null, CarryMode.CARRIED);
			item.setUuid(ItemTemplate.UUID_UNUSED_SOFTWARE_DEVICE);
			model.addCarriedItem(item);
			SR6GearTool.recalculate("", model, item);
		}
		
		return unprocessed;
	}

}
