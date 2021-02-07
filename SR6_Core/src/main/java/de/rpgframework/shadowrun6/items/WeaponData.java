package de.rpgframework.shadowrun6.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.prelle.simplepersist.AttribConvert;
import org.prelle.simplepersist.Attribute;

import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.items.AGearData;
import de.rpgframework.genericrpg.items.IGearTypeData;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.persist.AttackRatingConverter;
import de.rpgframework.shadowrun6.persist.FireModesConverter;
import de.rpgframework.shadowrun6.persist.SkillConverter;
import de.rpgframework.shadowrun6.persist.SkillSpecializationConverter;
import de.rpgframework.shadowrun6.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
public class WeaponData implements IGearTypeData {

	private transient ItemTemplate parent;
	@Attribute
	@AttribConvert(value=SkillConverter.class)
	private SR6Skill skill;
	@Attribute
	@AttribConvert(value=SkillSpecializationConverter.class)
	private SkillSpecialization spec;
	@Attribute(name="attack")
	@AttribConvert(value=AttackRatingConverter.class)
	private int[] attackRating;

	@Attribute(name="dmg")
	@AttribConvert(WeaponDamageConverter.class)
	protected Damage damage;
	@Attribute(name="mode")
	@AttribConvert(FireModesConverter.class)
	private List<FireMode> mode;
//	@Attribute(name="ammo")
//	@AttribConvert(AmmunitionConverter.class)
//	private List<AmmunitionSlot> ammo;
	@Attribute(name="wild")
	private boolean useWildDie;
	@Attribute(name="nowifi")
	private boolean noWiFi;

	//-------------------------------------------------------------------
	/**
	 */
	public WeaponData() {
//		ammo = new ArrayList<>();
	}

	//-------------------------------------------------------------------
	public String getName() {
		if (parent!=null)
			return parent.getName();
		return "?";
	}

	//--------------------------------------------------------------------
	/**
	 * Convenience for getSkillSpecialization().getSkill()
	 * @return the skill
	 */
	public SR6Skill getSkill() {
		if (skill!=null)
			return skill;
		return (SR6Skill) spec.getSkill();
	}

	//--------------------------------------------------------------------
	/**
	 * @return the damage
	 */
	public Damage getDamage() {
		return damage;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the spec
	 */
	public SkillSpecialization getSpecialization() {
		return spec;
	}

	//-------------------------------------------------------------------
	/**
	 * @param spec the spec to set
	 */
	public void setSpecialization(SkillSpecialization spec) {
		if (spec!=null)
			this.skill = (SR6Skill) spec.getSkill();
		this.spec = spec;
	}

	//-------------------------------------------------------------------
	/**
	 * @param skill the skill to set
	 */
	public void setSkill(SR6Skill skill) {
		this.skill = skill;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damage the damage to set
	 */
	public void setDamage(Damage damage) {
		this.damage = damage;
	}

//	//--------------------------------------------------------------------
//	public ItemAttributeValue getRecoilCompensation(List<Modification> mods) {
//		return new ItemAttributeValue(ItemAttribute.RECOIL_COMPENSATION, recoilCompensation, mods);
//	}

	//-------------------------------------------------------------------
	/**
	 * @return the mode
	 */
	public List<FireMode> getFireModes() {
		return mode;
	}

	//-------------------------------------------------------------------
	public List<String> getFireModeNames(Locale loc) {
		List<String> ret = new ArrayList<String>();
		if (mode!=null) {
			for (FireMode tmp : mode)
				ret.add(tmp.getName(loc));
		}
		return ret;
	}

//	//--------------------------------------------------------------------
//	public List<AmmunitionSlot> getAmmunition() {
//		return ammo;
//	}
//
//	//-------------------------------------------------------------------
//	public List<String> getAmmunitionNames() {
//		List<String> ret = new ArrayList<String>();
//		if (ammo!=null) {
//			for (AmmunitionSlot tmp : ammo)
//				ret.add(tmp.toString());
//		}
//		return ret;
//	}

	//--------------------------------------------------------------------
	public boolean isMeleeWeapon() {
		return skill!=null && (
				skill.getId().equals("unarmed")
				|| skill.getId().equals("blades")
				|| skill.getId().equals("clubs")
				|| skill.getId().equals("exoticClose")
				);
	}

	//--------------------------------------------------------------------
	public boolean isRangedWeapon() {
		return skill!=null && !isMeleeWeapon();
	}

	//-------------------------------------------------------------------
	/**
	 * @param mode the mode to set
	 */
	public void setFireModes(List<FireMode> mode) {
		this.mode = mode;
	}

//	//-------------------------------------------------------------------
//	/**
//	 * @param ammo the ammo to set
//	 */
//	public void setAmmunition(AmmunitionSlot ammo) {
//		this.ammo = Collections.singletonList(ammo);
//	}
//
//	//-------------------------------------------------------------------
//	/**
//	 * @param ammo the ammo to set
//	 */
//	public void setAmmunition(List<AmmunitionSlot> ammo) {
//		this.ammo = ammo;
//	}

	//-------------------------------------------------------------------
	/**
	 * @return the parent
	 */
	public ItemTemplate getParent() {
		return parent;
	}

	//-------------------------------------------------------------------
	/**
	 * @param parent the parent to set
	 */
	public void setParent(ItemTemplate parent) {
		this.parent = parent;
	}

	//-------------------------------------------------------------------
	public int[] getAttackRating() {
		return attackRating;
	}

	//-------------------------------------------------------------------
	/**
	 * @param attackRating the attackRating to set
	 */
	public void setAttackRating(int[] attackRating) {
		this.attackRating = attackRating;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the useWildDie
	 */
	public boolean isUseWildDie() {
		return useWildDie;
	}

	//-------------------------------------------------------------------
	/**
	 * @param useWildDie the useWildDie to set
	 */
	public void setUseWildDie(boolean useWildDie) {
		this.useWildDie = useWildDie;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the noWiFi
	 */
	public boolean hasNoWiFi() {
		return noWiFi;
	}

	//-------------------------------------------------------------------
	/**
	 * @param noWiFi the noWiFi to set
	 */
	public void setNoWiFi(boolean noWiFi) {
		this.noWiFi = noWiFi;
	}

	@Override
	public void copyToAttributes(AGearData copyTo) {
		// TODO Auto-generated method stub
		if (damage!=null)
			copyTo.setAttribute(SR6ItemAttribute.DAMAGE, damage);
	}

}
