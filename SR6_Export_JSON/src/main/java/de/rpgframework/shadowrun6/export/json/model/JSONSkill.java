package de.rpgframework.shadowrun6.export.json.model;

import java.util.List;

public class JSONSkill {
    public String name;
    public String id;
    public String attribute;
    public int rating;
    public int pool;
    public List<JSONSkillSpecialization> specializations;
    public List<String> influencedBy;
    public String description;
}
