package de.rpgframework.shadowrun6.foundry;

/**
 * @author prelle
 *
 */
public class DefenseRating {
	
	public class DRValue {
		public int mod = 0;
	}
	
	public DRValue physical;
	public DRValue astral;
	public DRValue social;

	//-------------------------------------------------------------------
	/**
	 */
	public DefenseRating() {
		physical   = new DRValue();
		astral     = new DRValue();
		social     = new DRValue();
	}

}
