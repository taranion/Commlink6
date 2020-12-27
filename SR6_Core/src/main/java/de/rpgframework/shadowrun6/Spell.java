/**
 *
 */
package de.rpgframework.shadowrun6;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.Attribute;
import org.prelle.simplepersist.ElementList;

import de.rpgframework.genericrpg.data.DataItem;
import de.rpgframework.genericrpg.data.DataItemTypeKey;

/**
 * @author prelle
 *
 */
@DataItemTypeKey(id="spell")
public class Spell extends DataItem implements Comparable<Spell> {

	private static Logger logger = LogManager.getLogger("shadowrun");

	public enum Category {
		COMBAT,
		DETECTION,
		HEALTH,
		ILLUSION,
		MANIPULATION
		;
		public String getName() { return Shadowrun6Core.getI18nResources().getString("spellcategory."+name().toLowerCase()); }
		public String getShortName() { return Shadowrun6Core.getI18nResources().getString("spellcategory."+name().toLowerCase()+".short"); }
		public String getName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spellcategory."+name().toLowerCase(), loc); }
		public String getShortName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spellcategory."+name().toLowerCase()+".short", loc); }
	}

	public enum Type {
		PHYSICAL,
		MANA
		;
		public String getName() { return Shadowrun6Core.getI18nResources().getString("spelltype."+name().toLowerCase()); }
		public String getShortName() { return Shadowrun6Core.getI18nResources().getString("spelltype."+name().toLowerCase()+".short"); }
		public String getName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spelltype."+name().toLowerCase(), loc); }
		public String getShortName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spelltype."+name().toLowerCase()+".short", loc); }
	}

	public enum Range {
		LINE_OF_SIGHT,
		LINE_OF_SIGHT_AREA,
		TOUCH,
		SELF,
		SELF_AREA,
		;
		public String getName() { return Shadowrun6Core.getI18nResources().getString("spellrange."+name().toLowerCase()); }
		public String getShortName() { return Shadowrun6Core.getI18nResources().getString("spellrange."+name().toLowerCase()+".short"); }
		public String getName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spellrange."+name().toLowerCase(), loc); }
		public String getShortName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spellrange."+name().toLowerCase()+".short", loc); }
	}

	public enum Duration {
		INSTANTANEOUS,
		SUSTAINED,
		PERMANENT,
		LIMITED,
		SPECIAL
		;
		public String getName() { return Shadowrun6Core.getI18nResources().getString("spellduration."+name().toLowerCase()); }
		public String getShortName() { return Shadowrun6Core.getI18nResources().getString("spellduration."+name().toLowerCase()+".short"); }
		public String getName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spellduration."+name().toLowerCase(), loc); }
		public String getShortName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spellduration."+name().toLowerCase()+".short", loc); }
	}

//	public enum Damage {
//		PHYSICAL,
//		STUN,
//		SPECIAL
//		;
//		public String getName() { return Shadowrun6Core.getI18nResources().getString("spelldamage."+name().toLowerCase()); }
//		public String getShortName() { return Shadowrun6Core.getI18nResources().getString("spelldamage."+name().toLowerCase()+".short"); }
//		public String getName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spelldamage."+name().toLowerCase(), loc); }
//		public String getShortName(Locale loc) { return Shadowrun6Core.getI18nResources().getString("spelldamage."+name().toLowerCase()+".short", loc); }
//	}

	@Attribute
	private String id;
	@Attribute(name="cat")
	private Category category;
	@Attribute
	private Type   type;
	@Attribute
	private Range  range;
	@Attribute(name="dmg")
	private DamageType damage;
	@Attribute(name="dur")
	private Duration duration;
	@Attribute
	private int  drain;

	@ElementList(entry="spellfeature",type=SpellFeatureReference.class, inline=true)
	private List<SpellFeatureReference> features;

	//-------------------------------------------------------------------
	public Spell() {
		features = new ArrayList<SpellFeatureReference>();
	}

	//-------------------------------------------------------------------
	public Spell(String id) {
		this();
		this.id = id;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}

	//-------------------------------------------------------------------
	public String dump() {
		StringBuffer buf = new StringBuffer();
		buf.append(id+"\n");
		buf.append(features+"\n");
		buf.append("Type:"+type+"  \tRange:"+range+"   Damage:"+damage+"\n");
		buf.append("Duration:"+duration+"  \tDrain:F"+drain);

		return buf.toString();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Spell o) {
		int foo = category.compareTo(o.getCategory());
		if (foo!=0)
			return foo;
		return Collator.getInstance().compare(getName(), o.getName());
	}

	//-------------------------------------------------------------------
	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	//-------------------------------------------------------------------
	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	//-------------------------------------------------------------------
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the range
	 */
	public Range getRange() {
		return range;
	}

	//-------------------------------------------------------------------
	/**
	 * @param range the range to set
	 */
	public void setRange(Range range) {
		this.range = range;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the damage
	 */
	public DamageType getDamage() {
		return damage;
	}

	//-------------------------------------------------------------------
	/**
	 * @param damage the damage to set
	 */
	public void setDamage(DamageType damage) {
		this.damage = damage;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}

	//-------------------------------------------------------------------
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the drain
	 */
	public int getDrain() {
		return drain;
	}

	//-------------------------------------------------------------------
	/**
	 * @param drain the drain to set
	 */
	public void setDrain(int drain) {
		this.drain = drain;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the features
	 */
	public List<SpellFeatureReference> getFeatures() {
		return features;
	}

	//-------------------------------------------------------------------
	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<SpellFeatureReference> features) {
		this.features = features;
	}

}
