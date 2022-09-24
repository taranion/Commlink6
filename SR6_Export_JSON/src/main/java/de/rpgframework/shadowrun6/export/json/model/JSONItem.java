package de.rpgframework.shadowrun6.export.json.model;

import java.util.List;

public class JSONItem {
    public String name;
    public int count;
    public int rating;
    public String type;
    public String subType;
    public String page;
    public List<JSONItemAccessory> accessories;
    public String description;
}
