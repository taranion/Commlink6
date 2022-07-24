module de.rpgframework.shadowrun6.core {
	exports de.rpgframework.shadowrun6;
	exports de.rpgframework.shadowrun6.generators;
	exports de.rpgframework.shadowrun6.filter;
	exports de.rpgframework.shadowrun6.items;
	exports de.rpgframework.shadowrun6.log;
	exports de.rpgframework.shadowrun6.persist;
	exports de.rpgframework.shadowrun6.modifications;
	exports de.rpgframework.shadowrun6.proc;

	requires transitive de.rpgframework.core;
	requires transitive de.rpgframework.rules;
	requires java.xml;
	requires transitive shadowrun.common;
	requires simple.persist;
	requires com.google.gson;
}