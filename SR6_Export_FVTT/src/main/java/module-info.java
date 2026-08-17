module de.rpgframework.shadowrun6.export.fvtt  {
	exports de.rpgframework.shadowrun6.export.fvtt;
	exports de.rpgframework.shadowrun6.foundry;
	exports de.rpgframework.foundry;
	opens de.rpgframework.shadowrun6.export.fvtt to com.google.gson;
	opens de.rpgframework.shadowrun6.foundry to com.google.gson;
	opens de.rpgframework.foundry to com.google.gson;

	requires de.rpgframework.core;
	requires transitive de.rpgframework.rules;
	requires transitive de.rpgframework.shadowrun6.core;
	requires shadowrun.common;
	requires com.google.gson;
}