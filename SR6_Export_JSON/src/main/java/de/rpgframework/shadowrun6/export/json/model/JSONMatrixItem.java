package de.rpgframework.shadowrun6.export.json.model;

import java.util.List;

import de.rpgframework.shadowrun6.items.ItemSubType;

public class JSONMatrixItem {
    public String name;
    public int dataProcessing;
    public int firewall;
    public int attack;
    public int sleaze;
    public int deviceRating;
    public String type;
    public ItemSubType subType;
    public String page;
    public int concurrentPrograms;
    public List<JSONItemAccessory> accessories;
    public List<JSONItem> programs;
    public String description;
}
