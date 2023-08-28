module de.rpgframework.shadowrun6.export.json  {
	exports de.rpgframework.shadowrun6.export.json;
	opens de.rpgframework.shadowrun6.export.json.model to com.google.gson;

	requires de.rpgframework.core;
	requires transitive de.rpgframework.rules;
	requires transitive de.rpgframework.shadowrun6.core;
	requires shadowrun.common;
	requires com.google.gson;
	requires java.desktop;
}