package de.rpgframework.shadowrun6;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.rpgframework.genericrpg.Pool;
import de.rpgframework.genericrpg.ValueType;
import de.rpgframework.genericrpg.data.AttributeValue;
import de.rpgframework.genericrpg.data.Decision;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.CarriedItem;
import de.rpgframework.shadowrun.ASpell.Category;
import de.rpgframework.shadowrun.FocusValue;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.items.FireMode;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.ItemType;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
public class CombatSectionTools {

	public static class AttackTable extends ArrayList<AttackEntry> {
		public String col1Name, col2Name, col3Name;
		public int numColumns=3;
		public AttackTable(String c1, String c2, String c3) {
			this.col1Name = c1;
			this.col2Name = c2;
			this.col3Name = c3;
		}
		public AttackTable() {
		}
	}

	private final static Logger logger = System.getLogger("de.rpgframework.shadowrun6");

	//-------------------------------------------------------------------
	/**
	 * Get attack table - to be used in CombatSections
	 */
	public static AttackTable getAttackTable(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getAttackTablePhysical(model,loc);
		case ASTRAL   : return getAttackTableAstral(model,loc);
		case MATRIX   : return getAttackTableMatrix(model,loc);
		case MATRIX_UV: return getAttackTableMatrixUV(model,loc);
		}
		return new AttackTable();

	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackTablePhysical(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable(
				SR6ItemAttribute.ATTACK_RATING.getShortName(loc),
				SR6ItemAttribute.DAMAGE.getShortName(loc),
				Shadowrun6Core.getI18nResources().getString("label.pool", loc)
				);

		/* Unarmed */
		SR6Skill skill = Shadowrun6Core.getSkill("close_combat");
		AttackEntry entry = new AttackEntry(skill.getSpecialization("unarmed").getName(loc));
		Pool<Integer> pool = Shadowrun6Tools.getSkillPool(model, skill, "unarmed");
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);

		try {
			List<CarriedItem<ItemTemplate>> weapons = model.getCarriedItems(ItemType.weaponTypes());
			int count=0;
			for (CarriedItem<ItemTemplate> weapon : weapons) {
				count++;
				if (count==5) break;
				entry = new AttackEntry(weapon.getNameWithoutRating(loc));
				ret.add(entry);
				// Col1: Pool
				entry.setCol1( String.valueOf(Shadowrun6Tools.getWeaponPoolCalculation(model, weapon).getValue(ValueType.AUGMENTED)) );
				entry.setCol1Tooltip( Shadowrun6Tools.getWeaponPoolCalculation(model, weapon).toExplainString() );
				// Col2: AR
				int[] ar = (int[])weapon.getAsObject(SR6ItemAttribute.ATTACK_RATING).getModifiedValue();
				if (ar[1]==0) {
					entry.setCol2(String.valueOf(ar[0]));
				} else {
					entry.setCol2(String.valueOf(ar[1]));
				}
				// Col3: Dmg
				entry.setCol3(  Shadowrun6Tools.getWeaponDamage(model, weapon).toString() );
		}
		} catch (Throwable e) {
			logger.log(Level.ERROR, "Failed creating attack table",e);
		}
		return ret;
	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackTableAstral(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();

		SR6Skill skill = Shadowrun6Core.getSkill("astral");
		Pool<Integer> pool = Shadowrun6Tools.getSkillPool(model, skill, ShadowrunAttribute.WILLPOWER, "astral_combat");

		/* Unarmed */
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getSkill("close_combat").getSpecialization("unarmed").getName(loc));
		// Col1: Pool
		entry.setCol1(pool.toString());
		entry.setCol1Tooltip(pool.toExplainString());
		// Col2: AR
		pool = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_ASTRAL).getPool();
		entry.setCol2(pool.toString());
		entry.setCol2Tooltip(pool.toExplainString());
		// Col3: DMG
		AttributeValue<ShadowrunAttribute> tradAttr = null;
		if (model.getMagicOrResonanceType()!=null && model.getMagicOrResonanceType().usesSpells() && model.getTradition()!=null) {
			tradAttr = model.getAttribute(model.getTradition().getTraditionAttribute());
		}
		if (tradAttr!=null) {
			entry.setCol3(String.valueOf( Math.round(( (double)tradAttr.getModifiedValue() / 2.0))));
		}
		ret.add(entry);

		/* One for each weapon focus */
		for (FocusValue focus : model.getFoci()) {
			if (!focus.getKey().equals("weapon_focus"))
				continue;
			Decision dec = focus.getDecision( focus.getResolved().getChoice(ShadowrunReference.CARRIED).getUUID() );
			if (dec==null) {
				logger.log(Level.WARNING, "Found a weapon focus, but no decision for CarriedItem");
				continue;
			}
			CarriedItem<ItemTemplate> weapon = model.getCarriedItem( dec.getValueAsUUID() );
			skill = weapon.getAsObject(SR6ItemAttribute.SKILL).getModifiedValue();
			SkillSpecialization<SR6Skill> spec = weapon.getAsObject(SR6ItemAttribute.SKILL_SPECIALIZATION).getModifiedValue();
			pool = Shadowrun6Tools.getSkillPool(model, skill, ShadowrunAttribute.WILLPOWER, spec.getId());
			entry = new AttackEntry(weapon.getNameWithoutRating(loc));
			entry.setCol1(pool.toString());
			entry.setCol1Tooltip(pool.toExplainString());
			pool = model.getAttribute(ShadowrunAttribute.ATTACK_RATING_ASTRAL).getPool();
			entry.setCol2(pool.toString());
			entry.setCol2Tooltip(pool.toExplainString());
			entry.setCol3(  Shadowrun6Tools.getWeaponDamage(model, weapon).toString() );
			ret.add(entry);
		}

		/* Add up to 3 combat spells */
		skill = Shadowrun6Core.getSkill("sorcery");
		try {
			pool = Shadowrun6Tools.getSkillPool(model, skill, ShadowrunAttribute.MAGIC, "spellcasting");
			List<SR6Spell> spells = model.getSpells().stream()
					.map(sv -> sv.getResolved())
					.filter(s-> s.getCategory()==Category.COMBAT)
					.toList();
			int count=0;
			for (SR6Spell spell : spells) {
				count++;
				if (count==4) break;
				entry = new AttackEntry(spell.getName(loc));
				ret.add(entry);
				entry.setCol2( String.valueOf( model.getAttribute(ShadowrunAttribute.ATTACK_RATING_ASTRAL).getModifiedValue() ));
				Damage dv = new Damage();
				dv.setDistributed( (int)Math.round( (double)model.getAttribute(ShadowrunAttribute.MAGIC).getModifiedValue() /2.0) );
				entry.setCol3(  dv.toString() );
				entry.setCol1( pool.toString() );
				entry.setCol1Tooltip( pool.toExplainString() );
		}
		} catch (Throwable e) {
			logger.log(Level.ERROR, "Failed creating attack table",e);
		}


		return ret;
	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackTableMatrix(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();

		SR6Skill skill = Shadowrun6Core.getSkill("cracking");
		Pool<Integer> cracking = Shadowrun6Tools.getSkillPool(model, skill, "cybercombat");
		int attack = model.getPersona().getAttack().getModifiedValue();

		/* Data Spike */
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "data_spike").getName(loc));
		Damage dmg = new Damage();
		dmg.setDistributed( (int)Math.round( (double)attack / 2.0));
		entry.setCol1(cracking.toString());
		entry.setCol1Tooltip(cracking.toExplainString());
		entry.setCol3(dmg.toString());
		ret.add(entry);

		/* Tarpit */
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "tarpit").getName(loc));
		entry.setCol1(cracking.toString());
		entry.setCol1Tooltip(cracking.toExplainString());
		entry.setCol3("1*");
		ret.add(entry);

		/* IC Slicer */
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "ic_slicer").getName(loc)+" *");
		entry.setCol1(cracking.toString());
		entry.setCol1Tooltip(cracking.toExplainString());
		entry.setCol3( String.valueOf(attack) );
		ret.add(entry);
		return ret;
	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackTableMatrixUV(Shadowrun6Character model, Locale loc) {
		return getAttackTableMatrix(model, loc);
	}

	//-------------------------------------------------------------------
	/**
	 * Get modifications for the attack table
	 */
	public static List<AttackEntry> getAttackModifiers(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getAttackModifiersPhysical(model,loc);
		case ASTRAL   : return getAttackModifiersAstral(model,loc);
		case MATRIX   : return getAttackModifiersMatrix(model,loc);
		case MATRIX_UV: return getAttackModifiersMatrixUV(model,loc);
		}
		return new ArrayList<>();

	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackModifiersPhysical(Shadowrun6Character model, Locale loc) {
		// Col1: Pool
		// Col2: AR
		// Col3: Dmg
		AttackTable ret = new AttackTable();
		// Semi Automatic
		AttackEntry entry = new AttackEntry(FireMode.SEMI_AUTOMATIC.getName(loc));
		entry.setCol2("-2");
		entry.setCol3("+1");
		ret.add(entry);
		// Burst fire (narrow)
		entry = new AttackEntry(FireMode.BURST_FIRE.getName(loc)+" "+Shadowrun6Core.getI18nResources().getString("label.burstfire.narrow", loc));
		entry.setCol2("-4");
		entry.setCol3("+2");
		ret.add(entry);
		// Call a shot
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "call_a_shot").getName(loc));
		entry.setCol1("-4");
		entry.setCol3("+2");
		ret.add(entry);
		// Take aim
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "take_aim").getName(loc));
		entry.setCol3("+1");
		ret.add(entry);

		return ret;
	}
	//-------------------------------------------------------------------
	private  static List<AttackEntry> getAttackModifiersAstral(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable("Drain","Dmg","Area");
		// Semi Automatic
		AttackEntry entry = new AttackEntry("Amp Up");
		entry.setCol1("+2");
		entry.setCol2("+1");
		ret.add(entry);
		// Burst fire (narrow)
		entry = new AttackEntry("Increase Area");
		entry.setCol1("+1");
		entry.setCol3("+2m");
		ret.add(entry);
		return ret;
	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackModifiersMatrix(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		// Take aim
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "virtual_aim").getName(loc));
		entry.setCol1("+1");
		ret.add(entry);
		return ret;
	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackModifiersMatrixUV(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		return ret;
	}

}
