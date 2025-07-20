package de.rpgframework.shadowrun6.proc;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.rpgframework.character.ProcessingStep;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventListener;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.genericrpg.Reward;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.genericrpg.modification.Modification;
import de.rpgframework.genericrpg.modification.ValueModification;
import de.rpgframework.shadowrun.LicenseValue;
import de.rpgframework.shadowrun.SIN;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * Clean up character from old mistakes / design decisions
 * @author prelle
 *
 */
public class FixCharacterStep implements ProcessingStep {

	private final static Logger logger = System.getLogger(FixCharacterStep.class.getPackageName()+".reset");

	private Shadowrun6Character model;

	//-------------------------------------------------------------------
	/**
	 */
	public FixCharacterStep(Shadowrun6Character model) {
		this.model = model;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.character.ProcessingStep#process(java.util.List)
	 */
	@Override
	public List<Modification> process(List<Modification> unprocessed) {

		// Clean up empty flags
		for (CarriedItem<ItemTemplate> item : model.getCarriedItems()) {
			item.clearEmptyFlags();
		}

		fixLicencesWithoutSIN();
		fixMissingHeat();
		
		return unprocessed;
	}

	//-------------------------------------------------------------------
	private void fixLicencesWithoutSIN() {
		Optional<SIN> sin = model.getSINs().stream().findFirst();
		List<String> mess = new ArrayList<>();
		if (sin.isPresent()) {
			for (LicenseValue lic : model.getLicenses()) {
				if (lic.getSIN()==null) {
					lic.setSIN(sin.get().getUniqueId());
					mess.add(String.format("SIN-less license %s assigned to SIN %s", lic.getName(), sin.get().getName()));
				}
			}
		}
		if (!mess.isEmpty()) {
			mess.add(0, "Character "+model.getName());
			mess.add("\nPlease verify character and save it again");
			BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 0, String.join("\r\n", mess));
		}
	}
	
	//-------------------------------------------------------------------
	private void fixMissingHeat() {
		// Calculate heat, if it doesn't exit
		int heat = 0;
		AttributeValue<ShadowrunAttribute> heatVal = model.getAttribute(ShadowrunAttribute.HEAT);
		if (heatVal==null) {
			heatVal = new AttributeValue<ShadowrunAttribute>(ShadowrunAttribute.HEAT);
			model.setAttribute(heatVal);
			try {
				for (Reward reward : model.getRewards()) {
					ValueModification modHeat = reward.getModification(ShadowrunReference.ATTRIBUTE, ShadowrunAttribute.HEAT.name());
					if (modHeat!=null) {
						heat += modHeat.getValue();
						if (heat<0) heat=0;
					}
				}
				model.getAttribute(ShadowrunAttribute.HEAT).setDistributed(heat);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
