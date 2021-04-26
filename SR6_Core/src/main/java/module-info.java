module de.rpgframework.shadowrun6.core {
	exports de.rpgframework.shadowrun6;
	exports de.rpgframework.shadowrun6.log;
	exports de.rpgframework.shadowrun6.persist;
	exports de.rpgframework.shadowrun6.items;
	exports de.rpgframework.shadowrun6.modifications;

	requires de.rpgframework.core;
	requires de.rpgframework.rules;
	requires java.xml;
	requires transitive org.apache.logging.log4j;
	requires shadowrun.common;
	requires simple.persist;
}