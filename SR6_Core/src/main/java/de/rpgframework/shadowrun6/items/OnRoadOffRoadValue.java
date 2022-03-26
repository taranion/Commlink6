package de.rpgframework.shadowrun6.items;

/**
 * @author prelle
 *
 */
public class OnRoadOffRoadValue {
	
	private float onRoad;
	private float offRoad;

	//-------------------------------------------------------------------
	public OnRoadOffRoadValue() {
	}

	//-------------------------------------------------------------------
	public OnRoadOffRoadValue(int val) {
		set(val);
	}

	//-------------------------------------------------------------------
	public OnRoadOffRoadValue(float on, float off) {
		set(on,off);
	}

	//-------------------------------------------------------------------
	public OnRoadOffRoadValue(OnRoadOffRoadValue copy) {
		set(copy.onRoad, copy.offRoad);
	}

	//-------------------------------------------------------------------
	public boolean equals(Object o) {
		if (o instanceof OnRoadOffRoadValue) {
			OnRoadOffRoadValue other = (OnRoadOffRoadValue)o;
			if (onRoad !=other.getOnRoad()) return false;
			if (offRoad!=other.getOffRoad()) return false;
			return true;
		}
		return false;
	}

	//-------------------------------------------------------------------
	public void set(int val) {
		onRoad = val;
		offRoad= val;
	}

	//-------------------------------------------------------------------
	public void set(float on, float off) {
		onRoad = on;
		offRoad= off;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the onRoad
	 */
	public int getOnRoad() {
		return Math.round(onRoad);
	}
	public float getOnRoadFloat() {
		return onRoad;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the offRoad
	 */
	public int getOffRoad() {
		return Math.round(offRoad);
	}
	public float getOffRoadFloat() {
		return offRoad;
	}

	//-------------------------------------------------------------------
	public String toString() {
		if (onRoad==offRoad)
			return String.valueOf( (int)onRoad);
		else
			return ((int)onRoad)+"/"+(int)offRoad;
	}

	//-------------------------------------------------------------------
	public void add(OnRoadOffRoadValue value) {
		offRoad += value.offRoad;
		onRoad  += value.onRoad;
	}

}
