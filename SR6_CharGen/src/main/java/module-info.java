module de.rpgframework.shadowrun6.chargen {
	exports de.rpgframework.shadowrun6.chargen.charctrl;
	exports de.rpgframework.shadowrun6.chargen.gen;
	exports de.rpgframework.shadowrun6.chargen.lvl;

	requires transitive de.rpgframework.core;
	requires transitive de.rpgframework.rules;
	requires transitive de.rpgframework.shadowrun6.core;
	requires transitive de.rpgframework.shadowrun6.data;
	requires transitive shadowrun.common;
	requires transitive shadowrun.common.chargen;
	requires simple.persist;
}