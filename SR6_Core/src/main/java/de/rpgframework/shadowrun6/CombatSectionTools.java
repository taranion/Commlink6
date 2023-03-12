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
import de.rpgframework.shadowrun6.items.AmmunitionType;
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
		public int numColumns=1;
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
	public static AttackTable getInitiativeTable(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getInitiativeTablePhysical(model,loc);
		case ASTRAL   : return getInitiativeTableAstral(model,loc);
		case MATRIX   : return getInitiativeTableMatrix(model,loc);
//		case MATRIX_UV: return getInitiativeTable(model,loc);
		}
		return new AttackTable();
	}

	//-------------------------------------------------------------------
	private  static AttackTable getInitiativeTablePhysical(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		ret.numColumns=1;

		AttackEntry entry = new AttackEntry(ShadowrunAttribute.INITIATIVE_PHYSICAL.getName(loc));
		Pool<Integer> pool1 = model.getAttribute(ShadowrunAttribute.INITIATIVE_PHYSICAL).getPool();
		Pool<Integer> pool2 = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_PHYSICAL).getPool();
		entry.setCol1( pool1.toString()+" + "+pool2.toString()+Shadowrun6Core.getI18nResources().getString("label.d6", loc) );
		entry.setCol1Tooltip( pool1.toExplainString()+"\n"+pool2.toExplainString() );
		ret.add(entry);

		return ret;
	}

	//-------------------------------------------------------------------
	private  static AttackTable getInitiativeTableAstral(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		ret.numColumns=1;

		AttackEntry entry = new AttackEntry(ShadowrunAttribute.INITIATIVE_ASTRAL.getName(loc));
		Pool<Integer> pool1 = model.getAttribute(ShadowrunAttribute.INITIATIVE_ASTRAL).getPool();
		Pool<Integer> pool2 = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_ASTRAL).getPool();
		entry.setCol1( pool1.toString()+" + "+pool2.toString()+Shadowrun6Core.getI18nResources().getString("label.d6", loc) );
		entry.setCol1Tooltip( pool1.toExplainString()+"\n"+pool2.toExplainString() );
		ret.add(entry);

		return ret;
	}

	//-------------------------------------------------------------------
	private  static AttackTable getInitiativeTableMatrix(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable(
				ShadowrunAttribute.INITIATIVE_MATRIX.getShortName(loc),
				ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD.getShortName(loc),
				ShadowrunAttribute.INITIATIVE_MATRIX_VR_HOT.getShortName(loc)
				);
		ret.numColumns=3;

		AttackEntry entry = new AttackEntry(ShadowrunAttribute.INITIATIVE_PHYSICAL.getName(loc));
		Pool<Integer> pool1 = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX).getPool();
		Pool<Integer> pool2 = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX).getPool();
		entry.setCol1( pool1.toString()+"+ "+pool2.getValue(ValueType.NATURAL)+Shadowrun6Core.getI18nResources().getString("label.d6", loc) );
		entry.setCol1Tooltip( pool1.toExplainString()+"\n"+pool2.toExplainString() );
		pool1 = model.getAttribute(ShadowrunAttribute.INITIATIVE_MATRIX_VR_COLD).getPool();
		pool2 = model.getAttribute(ShadowrunAttribute.INITIATIVE_DICE_MATRIX_VR_COLD).getPool();
		entry.setCol2( pool1.toString()+"+"+pool2.getValue(ValueType.NATURAL)+Shadowrun6Core.getI18nResources().getString("label.d6", loc) );
		entry.setCol2Tooltip( pool1.toExplainString()+"\n"+pool2.toExplainString() );
		ret.add(entry);

		return ret;
	}

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
				Shadowrun6Core.getI18nResources().getString("label.pool", loc),
				SR6ItemAttribute.ATTACK_RATING.getShortName(loc),
				SR6ItemAttribute.DAMAGE.getShortName(loc)
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
				entry.setCol1( String.valueOf(Shadowrun6Tools.getWeaponPoolCalculation(model, weapon).getValue(ValueType.NATURAL)) );
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
		AttackTable ret = new AttackTable(
				Shadowrun6Core.getI18nResources().getString("label.pool", loc),
				Shadowrun6Core.getI18nResources().getString("label.ar", loc),
				SR6ItemAttribute.DAMAGE.getShortName(loc)
				);

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
		AttackTable ret = new AttackTable(
				Shadowrun6Core.getI18nResources().getString("label.pool", loc),
				SR6ItemAttribute.DAMAGE.getShortName(loc),
				null
				);

		SR6Skill skill = Shadowrun6Core.getSkill("cracking");
		Pool<Integer> cracking = Shadowrun6Tools.getSkillPool(model, skill, "cybercombat");
		int attack = model.getPersona().getAttack().getModifiedValue();

		/* Data Spike */
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "data_spike").getName(loc));
		Damage dmg = new Damage();
		dmg.setDistributed( (int)Math.round( (double)attack / 2.0));
		entry.setCol1(cracking.toString());
		entry.setCol1Tooltip(cracking.toExplainString());
		entry.setCol2(dmg.toString());
		ret.add(entry);

		/* Tarpit */
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "tarpit").getName(loc));
		entry.setCol1(cracking.toString());
		entry.setCol1Tooltip(cracking.toExplainString());
		entry.setCol2("1*");
		ret.add(entry);

		/* IC Slicer */
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "ic_slicer").getName(loc)+" *");
		entry.setCol1(cracking.toString());
		entry.setCol1Tooltip(cracking.toExplainString());
		entry.setCol2( String.valueOf(attack) );
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
	public static AttackTable getAttackModifiers(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getAttackModifiersPhysical(model,loc);
		case ASTRAL   : return getAttackModifiersAstral(model,loc);
		case MATRIX   : return getAttackModifiersMatrix(model,loc);
		case MATRIX_UV: return getAttackModifiersMatrixUV(model,loc);
		}
		return new AttackTable();

	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackModifiersPhysical(Shadowrun6Character model, Locale loc) {
		AttackTable attackTable = getAttackTablePhysical(model, loc);
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
		// APDS
		if (attackTable.size()<5) {
			AmmunitionType ammo = Shadowrun6Core.getItem(AmmunitionType.class, "apds");
			entry = new AttackEntry(ammo.getName(loc));
			entry.setCol2("+1");
			entry.setCol3("-1");
			ret.add(entry);
		}
		// Gel
		if (attackTable.size()<4) {
			AmmunitionType ammo = Shadowrun6Core.getItem(AmmunitionType.class, "explosive");
			entry = new AttackEntry(ammo.getName(loc));
			entry.setCol3("+1");
			ret.add(entry);
		}

		return ret;
	}
	//-------------------------------------------------------------------
	private  static AttackTable getAttackModifiersAstral(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable(
				Shadowrun6Core.getI18nResources().getString("label.drain.short", loc),
				Shadowrun6Core.getI18nResources().getString("label.area.short", loc),
				Shadowrun6Core.getI18nResources().getString("label.damage.short", loc)
				);
		// Semi Automatic
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getI18nResources().getString("label.spell.amp_up", loc));
		entry.setCol1("+2");
		entry.setCol3("+1");
		ret.add(entry);
		// Burst fire (narrow)
		entry = new AttackEntry(Shadowrun6Core.getI18nResources().getString("label.spell.increase_area", loc));
		entry.setCol1("+1");
		entry.setCol2("+2m");
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

	//-------------------------------------------------------------------
	/**
	 * Get attack table - to be used in CombatSections
	 */
	public static AttackTable getDefenseTable(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getDefenseTablePhysical(model,loc);
		case ASTRAL   : return getDefenseTableAstral(model,loc);
		case MATRIX   : return getDefenseTableMatrix(model,loc);
//		case MATRIX_UV: return getAttackTableMatrixUV(model,loc);
		}
		return new AttackTable();
	}

	//-------------------------------------------------------------------
	private  static AttackTable getDefenseTablePhysical(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		// Physical
		AttackEntry entry = new AttackEntry(ShadowrunAttribute.DEFENSE_POOL_PHYSICAL.getShortName(loc));
		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_PHYSICAL).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		// Direct Combat Spells
		entry = new AttackEntry(ShadowrunAttribute.DEFENSE_POOL_COMBAT_DIRECT.getShortName(loc));
		pool = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_COMBAT_DIRECT).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		// Indirect Combat Spells
		entry = new AttackEntry(ShadowrunAttribute.DEFENSE_POOL_COMBAT_INDIRECT.getShortName(loc));
		pool = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_COMBAT_INDIRECT).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);

		return ret;
	}

	//-------------------------------------------------------------------
	private  static AttackTable getDefenseTableAstral(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		// Physical
		AttackEntry entry = new AttackEntry(ShadowrunAttribute.DEFENSE_POOL_ASTRAL.getShortName(loc));
		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_ASTRAL).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);

		return ret;
	}

	//-------------------------------------------------------------------
	private  static AttackTable getDefenseTableMatrix(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		// Physical
		AttackEntry entry = new AttackEntry(ShadowrunAttribute.DEFENSE_POOL_MATRIX.getName(loc));
		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.DEFENSE_POOL_MATRIX).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);

		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Get attack table - to be used in CombatSections
	 */
	public static AttackTable getDefenseModifiers(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getDefenseModifiersPhysical(model,loc);
//		case ASTRAL   : return getAttackTableAstral(model,loc);
		case MATRIX   : return getDefenseModifiersMatrix(model,loc);
//		case MATRIX_UV: return getAttackTableMatrixUV(model,loc);
		}
		return new AttackTable();
	}

	//-------------------------------------------------------------------
	private  static AttackTable getDefenseModifiersPhysical(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		// Full Defense
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "full_defense").getName(loc));
		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.WILLPOWER).getPool();
		entry.setCol1( "+"+pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		// Block / Blocken
		SR6Skill skill = Shadowrun6Core.getSkill("close_combat");
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "block").getName(loc));
		pool = Shadowrun6Tools.getSkillPoolCalculationWithoutAttribute(model, skill);
		entry.setCol1( "+"+pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		// Dodge / Ausweichen
		skill = Shadowrun6Core.getSkill("athletics");
		entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "dodge").getName(loc));
		pool = Shadowrun6Tools.getSkillPoolCalculationWithoutAttribute(model, skill);
		entry.setCol1( "+"+pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		return ret;
	}

	//-------------------------------------------------------------------
	private  static AttackTable getDefenseModifiersMatrix(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
		// Full Defense
		AttackEntry entry = new AttackEntry(Shadowrun6Core.getItem(Shadowrun6Action.class, "full_matrix_defense").getName(loc));
		entry.setCol1( "+"+model.getPersona().getFirewall().getModifiedValue() );
		entry.setCol1Tooltip( SR6ItemAttribute.FIREWALL.getName(loc) );
		ret.add(entry);
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * Get attack table - to be used in CombatSections
	 */
	public static AttackTable getDamageTable(Shadowrun6Character model, Locale loc, WorldType type) {
		switch (type) {
		case PHYSICAL : return getDamageTablePhysical(model,loc);
		case ASTRAL   : return getDamageTableAstral(model,loc);
//		case MATRIX   : return getAttackTableMatrix(model,loc);
//		case MATRIX_UV: return getAttackTableMatrixUV(model,loc);
		}
		return new AttackTable();
	}

	//-------------------------------------------------------------------
	private static AttackTable getDamageTablePhysical(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();
//		// Stun monitor
//		AttackEntry entry = new AttackEntry(ShadowrunAttribute.STUN_MONITOR.getShortName(loc));
//		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.STUN_MONITOR).getPool();
//		entry.setCol1( pool.toString() );
//		entry.setCol1Tooltip( pool.toExplainString() );
//		ret.add(entry);
//		// Stun monitor
//		entry = new AttackEntry(ShadowrunAttribute.PHYSICAL_MONITOR.getShortName(loc));
//		pool = model.getAttribute(ShadowrunAttribute.PHYSICAL_MONITOR).getPool();
//		entry.setCol1( pool.toString() );
//		entry.setCol1Tooltip( pool.toExplainString() );
//		ret.add(entry);

		// RESIST_DAMAGE
		AttackEntry entry = new AttackEntry(ShadowrunAttribute.RESIST_DAMAGE.getName(loc));
		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		// RESIST_TOXIN
		entry = new AttackEntry(ShadowrunAttribute.RESIST_TOXIN.getName(loc));
		pool = model.getAttribute(ShadowrunAttribute.RESIST_TOXIN).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);

		return ret;
	}

	//-------------------------------------------------------------------
	private static AttackTable getDamageTableAstral(Shadowrun6Character model, Locale loc) {
		AttackTable ret = new AttackTable();

		// RESIST_DAMAGE
		AttackEntry entry = new AttackEntry(ShadowrunAttribute.RESIST_DAMAGE_ASTRAL.getShortName(loc));
		Pool<Integer> pool = model.getAttribute(ShadowrunAttribute.RESIST_DAMAGE_ASTRAL).getPool();
		entry.setCol1( pool.toString() );
		entry.setCol1Tooltip( pool.toExplainString() );
		ret.add(entry);
		// RESIST_DRAIN
		if (model.getMagicOrResonanceType().usesSpells()) {
			entry = new AttackEntry(ShadowrunAttribute.RESIST_DRAIN.getShortName(loc));
			pool = model.getAttribute(ShadowrunAttribute.RESIST_DRAIN).getPool();
			entry.setCol1( pool.toString() );
			entry.setCol1Tooltip( pool.toExplainString() );
			ret.add(entry);
		}
		// RESIST_DRAIN_ADEPT
		if (model.getMagicOrResonanceType().usesPowers()) {
			entry = new AttackEntry(ShadowrunAttribute.RESIST_DRAIN_ADEPT.getShortName(loc));
			pool = model.getAttribute(ShadowrunAttribute.RESIST_DRAIN_ADEPT).getPool();
			entry.setCol1( pool.toString() );
			entry.setCol1Tooltip( pool.toExplainString() );
			ret.add(entry);
		}

		return ret;
	}

}
