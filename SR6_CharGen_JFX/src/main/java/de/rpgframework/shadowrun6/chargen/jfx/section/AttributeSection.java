package de.rpgframework.shadowrun6.chargen.jfx.section;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.Section;

import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.chargen.charctrl.IAttributeController;
import de.rpgframework.shadowrun.chargen.jfx.KarmaAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.LevellingAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.PriorityAttributeTable;
import de.rpgframework.shadowrun.chargen.jfx.ShadowrunAttributeTable;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.chargen.charctrl.SR6CharacterController;
import de.rpgframework.shadowrun6.chargen.gen.free.SR6FreeAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.karma.SR6KarmaAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.pointbuy.SR6PointBuyAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.gen.priority.SR6PriorityAttributeGenerator;
import de.rpgframework.shadowrun6.chargen.jfx.PointBuyAttributeTable;
import de.rpgframework.shadowrun6.chargen.lvl.SR6AttributeLeveller;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author prelle
 *
 */
public class AttributeSection extends Section {

	private final static Logger logger = System.getLogger(AttributeSection.class.getPackageName());

	private ShadowrunAttributeTable<SR6Skill,SR6SkillValue,Shadowrun6Character> table;

	private SR6CharacterController control;

	private IntegerProperty flexWidthProperty = new SimpleIntegerProperty(4);

	//-------------------------------------------------------------------
	public AttributeSection(String title, ScreenManagerProvider provider) {
		super(title, null);
		logger.log(Level.DEBUG, "<init>");

		initLayoutNormal();
		initInteractivity();
	}

	//-------------------------------------------------------------------
	private void initLayoutNormal() {
		setContent(table);
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
	}

	//-------------------------------------------------------------------
	public ReadOnlyIntegerProperty flexWidthProperty() { return flexWidthProperty; }

	//-------------------------------------------------------------------
	public void updateController(SR6CharacterController ctrl) {
		this.control = ctrl;
		Shadowrun6Character model = ctrl.getModel();

		IAttributeController attrib = ctrl.getAttributeController();
		logger.log(Level.DEBUG, "updateController to {0}", attrib.getClass().getSimpleName());
		if (attrib instanceof SR6PriorityAttributeGenerator) {
			table = new PriorityAttributeTable<>(ctrl);
			((PriorityAttributeTable<?,?,?>)table).useExpertModeProperty().addListener( (ov,o,n) -> flexWidthProperty.set(n?9:6));
		} else if (attrib instanceof SR6PointBuyAttributeGenerator) {
			table = new PointBuyAttributeTable<>(ctrl);
		} else if (attrib instanceof SR6KarmaAttributeGenerator) {
			table = new KarmaAttributeTable<>(ctrl);
		} else if (attrib instanceof SR6FreeAttributeGenerator) {
			table = new KarmaAttributeTable<>(ctrl);
		} else if (attrib instanceof SR6AttributeLeveller) {
			table = new LevellingAttributeTable<>(ctrl);
		} else {
			System.err.println("AttributeSection: change controller to unsupported");
			logger.log(Level.ERROR, "Don't support controller "+model.getCharGenUsed());
		}

		MagicOrResonanceType mor = model.getMagicOrResonanceType();
		if (mor != null) {
			table.setShowMagic(mor.usesMagic());
			table.setShowResonance(mor.usesResonance());
		}

		setContent(table);
	}

	//-------------------------------------------------------------------
	public void refresh() {
		logger.log(Level.DEBUG, "refresh: {0}",table.getSkin());
		if (control!=null) {
			Shadowrun6Character model = control.getModel();
			MagicOrResonanceType mor = model.getMagicOrResonanceType();
			if (mor != null) {
				table.setShowMagic(mor.usesMagic());
				table.setShowResonance(mor.usesResonance());
			}
		}
		table.refresh();
	}

}
