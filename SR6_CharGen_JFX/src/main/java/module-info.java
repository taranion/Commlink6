module de.rpgframework.shadowrun6.chargen.jfx {
	exports de.rpgframework.shadowrun6.chargen.jfx;
	exports de.rpgframework.shadowrun6.chargen.jfx.pane;
	exports de.rpgframework.shadowrun6.chargen.jfx.selector;
	exports de.rpgframework.shadowrun6.chargen.jfx.listcell;
	exports de.rpgframework.shadowrun6.chargen.jfx.page;
	exports de.rpgframework.shadowrun6.chargen.jfx.wizard;
	exports de.rpgframework.shadowrun6.chargen.jfx.section;

	opens de.rpgframework.shadowrun6.chargen.jfx;
	opens de.rpgframework.shadowrun6.chargen.jfx.listcell to javafx.fxml;
	opens de.rpgframework.shadowrun6.chargen.jfx.page to javafx.fxml;
	opens de.rpgframework.shadowrun6.chargen.jfx.section to javafx.fxml;
	opens de.rpgframework.shadowrun6.chargen.jfx.wizard to javafx.fxml;

	requires com.google.gson;
	requires de.rpgframework.core;
	requires de.rpgframework.javafx;
	requires de.rpgframework.rules;
	requires transitive de.rpgframework.shadowrun6.core;
	requires de.rpgframework.shadowrun6.data;
	requires transitive de.rpgframework.shadowrun6.chargen;
	requires javafx.base;
	requires javafx.controls;
	requires transitive javafx.extensions;
	requires javafx.fxml;
	requires javafx.graphics;
	requires org.controlsfx.controls;
	requires shadowrun.common;
	requires shadowrun.common.chargen;
	requires shadowrun.common.chargen.jfx;
	requires simple.persist;
	requires lombok;
}