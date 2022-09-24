package de.rpgframework.shadowrun6.export.json.model;

import java.util.List;

public class JSONVehicle {
    public String name;
    public String type;
    public String subtype;
    public int handlOn;
    public int handlOff;
    public int pilot;
    public int body;
    public int accelOn;
    public int accelOff;
    public int speedIntOn;
    public int speedIntOff;
    public int speed;
    public int armor;
    public int sensor;
    public int seats;
    public List<JSONItemAccessory> accessories;
    public String page;
    public String description;
}
