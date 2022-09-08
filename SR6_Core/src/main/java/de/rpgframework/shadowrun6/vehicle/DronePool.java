package de.rpgframework.shadowrun6.vehicle;

public class DronePool {
	public int manual;
	public int autonomous;
	public int viaRCC;
	public int rigged;
	
	public String toString() {
		return manual+" "+autonomous+" "+viaRCC+" "+rigged;
	}
}