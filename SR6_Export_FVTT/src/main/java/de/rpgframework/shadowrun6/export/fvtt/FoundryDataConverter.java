package de.rpgframework.shadowrun6.export.fvtt;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;

import de.rpgframework.foundry.ItemData;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.shadowrun.SpellFeatureReference;
import de.rpgframework.shadowrun.SpellValue;
import de.rpgframework.shadowrun6.SR6Spell;
import de.rpgframework.shadowrun6.foundry.FVTTSpell;

public class FoundryDataConverter {

	private final static Logger logger = System.getLogger(FoundryDataConverter.class.getPackageName());

	//-------------------------------------------------------------------
	public static ItemData<FVTTSpell> convertSpell(SR6Spell item, Locale loc) {
		FVTTSpell spell = new FVTTSpell();

		spell.genesisID = item.getId();
		spell.category  = item.getCategory().name().toLowerCase();
		spell.duration  = item.getDuration().name().toLowerCase();
		spell.drain     = item.getDrain();
		spell.range     = item.getRange().name().toLowerCase();
		spell.type      = item.getType().name().toLowerCase();
		if (item.getDamage()!=null)
			spell.damage    = item.getDamage().name().toLowerCase();
		spell.isOpposed   = item.isOpposed();
		spell.withEssence = item.isEssence();
		spell.wildDie     = item.isWild();
		for (SpellFeatureReference ref : item.getFeatures()) {
			switch (ref.getFeature().getId()) {
			case "indirect": spell.combatSpellType="spells_indirect"; break;
			case "direct": spell.combatSpellType="spells_direct"; break;
			case "sense_single": spell.multiSense=false; break;
			case "sense_multi": spell.multiSense=true; break;
			}
		}

		spell.description    = item.getDescription();
		for (PageReference pr : item.getPageReferences()) {
			if(pr.getLanguage().equals(loc.getLanguage())) {
				spell.product = pr.getProduct().getID().toLowerCase();
				spell.page    = pr.getPage();
				break;
			}
		}

		ItemData<FVTTSpell> foundry = new ItemData<FVTTSpell>(item.getName(loc), "spell", spell);
		return foundry;
	}

	//-------------------------------------------------------------------
	public static ItemData<FVTTSpell> convertSpell(SpellValue<SR6Spell> item, Locale loc) {
		return convertSpell(item.getModifyable(), loc);
	}

}
