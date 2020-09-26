/**
 * 
 */
package org.prelle.shadowrun6.persist;

import org.prelle.shadowrun6.modifications.ShadowrunReference;

/**
 * @author prelle
 *
 */
@SuppressWarnings("serial")
public class ReferenceException extends RuntimeException {
	
	private ShadowrunReference type;
	private String reference;
	private Object context;

	//--------------------------------------------------------------------
	public ReferenceException(ShadowrunReference type, String ref) {
		super("Invalid reference to "+type+" '"+ref+"'");
		this.type = type;
		this.reference = ref;
	}
	//--------------------------------------------------------------------
	public ReferenceException(ShadowrunReference type, String ref, Object context) {
		super("Invalid reference to "+type+" '"+ref+"' in context "+context);
		this.type = type;
		this.reference = ref;
		this.context = context;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the type
	 */
	public ShadowrunReference getType() {
		return type;
	}

	//--------------------------------------------------------------------
	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}
	//-------------------------------------------------------------------
	/**
	 * @return the context
	 */
	public Object getContext() {
		return context;
	}

}
