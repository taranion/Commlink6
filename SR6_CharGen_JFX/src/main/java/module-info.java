module de.rpgframework.shadowrun6.chargen.jfx {
	exports de.rpgframework.shadowrun6.chargen.jfx;
	exports de.rpgframework.shadowrun6.chargen.jfx.pane;
	exports de.rpgframework.shadowrun6.chargen.jfx.selector;
	exports de.rpgframework.shadowrun6.chargen.jfx.listcell;
	exports de.rpgframework.shadowrun6.chargen.jfx.page;
	exports de.rpgframework.shadowrun6.chargen.jfx.wizard;
	exports de.rpgframework.shadowrun6.chargen.jfx.section;

	requires com.google.gson;
	requires de.rpgframework.core;
	requires de.rpgframework.javafx;
	requires de.rpgframework.rules;
	requires de.rpgframework.shadowrun6.core;
	requires de.rpgframework.shadowrun6.data;
	requires de.rpgframework.shadowrun6.chargen;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.extensions;
	requires javafx.fxml;
	requires javafx.graphics;
	requires org.controlsfx.controls;
	requires shadowrun.common;
	requires shadowrun.common.chargen;
	requires shadowrun.common.chargen.jfx;
	requires simple.persist;
}