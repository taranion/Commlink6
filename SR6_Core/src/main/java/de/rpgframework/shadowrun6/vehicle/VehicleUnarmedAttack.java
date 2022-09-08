package de.rpgframework.shadowrun6.vehicle;

import java.util.ArrayList;
import java.util.List;

import de.rpgframework.genericrpg.PoolCalculation;
import de.rpgframework.shadowrun6.items.Damage;

/**
 * @author stefan
 *
 */
public class VehicleUnarmedAttack {
	
	private VehicleOperationMode operationMode;
	private int attackRating;
	private List<PoolCalculation> attackRatingCalc;
	private int pool;
	private List<PoolCalculation> poolCalc;
	private Damage damage;

	//---------------------------------------------------------
	public VehicleUnarmedAttack(VehicleOperationMode mode) {
		this.operationMode = mode;
		attackRatingCalc = new ArrayList<>();
		poolCalc = new ArrayList<>();
	}

	//---------------------------------------------------------
	/**
	 * @return the attackRating
	 */
	public int getAttackRating() {
		return attackRating;
	}

	//---------------------------------------------------------
	/**
	 * @param attackRating the attackRating to set
	 */
	public void setAttackRating(int attackRating) {
		this.attackRating = attackRating;
	}

	//---------------------------------------------------------
	/**
	 * @return the attackRatingCalc
	 */
	public List<PoolCalculation> getAttackRatingCalculation() {
		return attackRatingCalc;
	}

	//---------------------------------------------------------
	/**
	 * @param attackRatingCalc the attackRatingCalc to set
	 */
	public void setAttackRating(List<PoolCalculation> attackRatingCalc) {
		this.attackRatingCalc = attackRatingCalc;
	}

	//---------------------------------------------------------
	/**
	 * @return the pool
	 */
	public int getPool() {
		return pool;
	}

	//---------------------------------------------------------
	/**
	 * @param pool the pool to set
	 */
	public void setPool(int pool) {
		this.pool = pool;
	}

	//---------------------------------------------------------
	/**
	 * @return the poolCalc
	 */
	public List<PoolCalculation> getPoolCalculation() {
		return poolCalc;
	}

	//---------------------------------------------------------
	/**
	 * @param poolCalc the poolCalc to set
	 */
	public void setPool(List<PoolCalculation> poolCalc) {
		this.poolCalc = poolCalc;
	}

	//---------------------------------------------------------
	/**
	 * @return the damage
	 */
	public Damage getDamage() {
		return damage;
	}

	//---------------------------------------------------------
	/**
	 * @param damage the damage to set
	 */
	public void setDamage(Damage damage) {
		this.damage = damage;
	}

	//---------------------------------------------------------
	/**
	 * @return the operationMode
	 */
	public VehicleOperationMode getOperationMode() {
		return operationMode;
	}

}
