package de.rpgframework.shadowrun6;

/**
 * @author prelle
 *
 */
public class AttackEntry {

	private String name;
	private String col1;
	private String col1Tooltip;
	private String col2;
	private String col2Tooltip;
	private String col3;
	private String col3Tooltip;

	//-------------------------------------------------------------------
	public AttackEntry(String name) {
		this.name = name;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the col1
	 */
	public String getCol1() {
		return col1;
	}

	//-------------------------------------------------------------------
	/**
	 * @param col1 the col1 to set
	 */
	public void setCol1(String col1) {
		this.col1 = col1;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the col1Tooltip
	 */
	public String getCol1Tooltip() {
		return col1Tooltip;
	}

	//-------------------------------------------------------------------
	/**
	 * @param col1Tooltip the col1Tooltip to set
	 */
	public void setCol1Tooltip(String col1Tooltip) {
		this.col1Tooltip = col1Tooltip;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the col2
	 */
	public String getCol2() {
		return col2;
	}

	//-------------------------------------------------------------------
	/**
	 * @param col2 the col2 to set
	 */
	public void setCol2(String col2) {
		this.col2 = col2;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the col2Tooltip
	 */
	public String getCol2Tooltip() {
		return col2Tooltip;
	}

	//-------------------------------------------------------------------
	/**
	 * @param col2Tooltip the col2Tooltip to set
	 */
	public void setCol2Tooltip(String col2Tooltip) {
		this.col2Tooltip = col2Tooltip;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the col3
	 */
	public String getCol3() {
		return col3;
	}

	//-------------------------------------------------------------------
	/**
	 * @param col3 the col3 to set
	 */
	public void setCol3(String col3) {
		this.col3 = col3;
	}

	//-------------------------------------------------------------------
	/**
	 * @return the col3Tooltip
	 */
	public String getCol3Tooltip() {
		return col3Tooltip;
	}

	//-------------------------------------------------------------------
	/**
	 * @param col3Tooltip the col3Tooltip to set
	 */
	public void setCol3Tooltip(String col3Tooltip) {
		this.col3Tooltip = col3Tooltip;
	}

}
