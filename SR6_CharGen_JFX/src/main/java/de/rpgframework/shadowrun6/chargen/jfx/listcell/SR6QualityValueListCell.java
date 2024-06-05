package de.rpgframework.shadowrun6.chargen.jfx.listcell;

import java.lang.System.Logger.Level;
import java.util.UUID;

import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.shadowrun.QualityValue;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterController;
import de.rpgframework.shadowrun.chargen.charctrl.IShadowrunCharacterControllerProvider;
import de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.SignatureManeuver;

/**
 *
 */
public class SR6QualityValueListCell extends QualityValueListCell {

	private final static UUID SIGNATURE_MANEUVER_DECISION = UUID.fromString("1031aa98-982c-4544-b781-219514da7886");

	//-------------------------------------------------------------------
	/**
	 * @param ctrlProv
	 * @param withKarma
	 */
	public SR6QualityValueListCell(IShadowrunCharacterControllerProvider<IShadowrunCharacterController> ctrlProv,
			boolean withKarma) {
		super(ctrlProv, withKarma);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.listcell.QualityValueListCell#updateItem(de.rpgframework.shadowrun.QualityValue, boolean)
	 */
	@Override
	public void updateItem(QualityValue item, boolean empty) {
		super.updateItem(item, empty);

		if (!empty && item!=null) {
			// Special handling for signature maneuvers
			if (item.getKey().equals("signature_maneuver")) {
				Decision dec = item.getDecision(SIGNATURE_MANEUVER_DECISION);
				logger.log(Level.INFO, "Decision = "+dec);
				if (dec!=null) {
					try {
						UUID key = dec.getValueAsUUID();
						SignatureManeuver move = (SignatureManeuver) ((Shadowrun6Character)controlProvider.get().getModel()).getSignatureManeuvers().stream().filter(m -> m.getUuid().equals(key)).findFirst().get();
						decision.setText(  move.getName() );
					} catch (Exception e) {
						logger.log(Level.ERROR, "Failed determining signaure maneuver",e);
					}
				}
			}
		}
	}
}
