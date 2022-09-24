package de.rpgframework.shadowrun6.export.json.model;

import java.util.List;

public class JSONDrone {
    public String name;
    public String type;
    public String subtype;
    public int count;
    public int handlOn;
    public int handlOff;
    public int accelOn;
    public int accelOff;
    public int speedIntOn;
    public int speedIntOff;
    public int speed;
    public int body;
    public int armor;
    public int pilot;
    public int sensor;
    public String page;
    public List<JSONItemAccessory> accessories;
    public String description;
}
