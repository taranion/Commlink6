package de.rpgframework.shadowrun6.chargen.jfx.section;

import org.prelle.javafx.Section;
import org.prelle.javafx.TitledComponent;

import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

/**
 * @author prelle
 *
 */
public class BasicDataSection extends Section {

	private TextField tfStreetName;
	private TextField tfRealName;
	private TextField tfAge;
	
	public BasicDataSection() {
		super("Basisdaten", null);
		
		initComponents();
		initLayout();
	}
	
	private void initComponents() {
		tfStreetName = new TextField("Slick");
		tfRealName   = new TextField("Herbert Jamijeck");
		tfStreetName.setPrefColumnCount(10);
		tfAge        = new TextField("24");
		tfAge.setPrefColumnCount(3);
	}
	
	private void initLayout() {
		FlowPane layout = new FlowPane(
				new TitledComponent("Street Name",  tfStreetName), 
				new TitledComponent("Real Name", tfRealName), 
				 new TitledComponent("Age", tfAge));
		layout.setPrefWrapLength(300);
		layout.setVgap(5);
		layout.setHgap(10);
		setContent(layout);
	}
	
}
