package de.rpgframework.shadowrun6.chargen.jfx.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

import org.prelle.javafx.ResponsiveControlManager;
import org.prelle.javafx.WindowMode;

import de.rpgframework.ResourceI18N;
import de.rpgframework.shadowrun.MagicOrResonanceOption;
import de.rpgframework.shadowrun.MagicOrResonanceType;
import de.rpgframework.shadowrun.MetaTypeOption;
import de.rpgframework.shadowrun.Priority;
import de.rpgframework.shadowrun.PriorityOption;
import de.rpgframework.shadowrun.PriorityTableEntry;
import de.rpgframework.shadowrun.PriorityType;
import de.rpgframework.shadowrun.chargen.jfx.wizard.PriorityTable;
import de.rpgframework.shadowrun6.SR6MetaType;
import de.rpgframework.shadowrun6.Shadowrun6Character;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.chargen.gen.SR6PrioritySettings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author prelle
 *
 */
public class SR6PriorityTable extends PriorityTable<Shadowrun6Character, SR6PrioritySettings> {

	private static PropertyResourceBundle RES = (PropertyResourceBundle) ResourceBundle.getBundle(SR6PriorityTable.class.getName());
	
	private BiFunction<PriorityType,Priority,PriorityTableEntry> resolver; 

	//-------------------------------------------------------------------
	public SR6PriorityTable(BiFunction<PriorityType,Priority,PriorityTableEntry> resolver) {
		super();
		this.resolver = resolver;
		
		if (ResponsiveControlManager.getCurrentMode()==WindowMode.MINIMAL) {
			refreshMinimal();
		}
	}
	

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.PriorityTable#getMetatypeNode(de.rpgframework.shadowrun.Priority)
	 */
	@Override
	public Node getMetatypeNode(Priority prio) {
		PriorityTableEntry entry = resolver.apply(PriorityType.METATYPE, prio); 
		List<String> names = new ArrayList<>();
		
		entry.getOptions().forEach(opt -> names.add(Shadowrun6Core.getItem(SR6MetaType.class, ((MetaTypeOption)opt).getType()).getName()));
		String ret = String.join(", ", names)+" ("+entry.getAdjustmentPoints()+")";

		return new Label(ret);
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.PriorityTable#getAttributeNode(de.rpgframework.shadowrun.Priority)
	 */
	@Override
	public Node getAttributeNode(Priority prio) {
		Label ret = new Label();
		switch (prio) {
		case A: ret.setText("24"); break;
		case B: ret.setText("16"); break;
		case C: ret.setText("12"); break;
		case D: ret.setText("8"); break;
		case E: ret.setText("2"); break;			
		}
		ret.setStyle("-fx-font-size: 200%");
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.PriorityTable#getMagicOrResonanceNode(de.rpgframework.shadowrun.Priority)
	 */
	@Override
	public Node getMagicOrResonanceNode(Priority prio) {
		PriorityTableEntry entry = resolver.apply(PriorityType.MAGIC, prio);
//		logger.info("----"+entry);
		VBox ret = new VBox();
		
		int oldPoints = -1;
		StringBuffer buf = null;
		MagicOrResonanceType lastType = null;
		for ( PriorityOption opt : entry.getOptions() ) {
//			logger.info("*******"+opt);
			MagicOrResonanceType type = Shadowrun6Core.getItem(MagicOrResonanceType.class, ((MagicOrResonanceOption)opt).getType() );
			int points = ((MagicOrResonanceOption)opt).getValue();
//			logger.info("  type="+type+"  points="+points);
			if (points!=oldPoints) {
//				logger.info(" change from "+oldPoints+" to "+points+"   Buffer is "+buf);
				// Print old
				if (buf!=null) {
					Label part1 = new Label(buf.toString()+":");
					part1.setStyle("-fx-font-weight: bold");
					Label part2 = new Label(ResourceI18N.get(RES, (lastType.getId().equals("technomancer")?"label.resonance":"label.magic"))+" "+oldPoints);
					HBox line = new HBox(5, part1, part2);
					ret.getChildren().add(line);
					buf = new StringBuffer(type.getName());
				} else {
					buf = new StringBuffer(type.getName());
				}
			} else {
				// Same points
				if (buf!=null) {
					
				}
				buf.append(", "+type.getName());
			}
			oldPoints = points;
			lastType = type;
		};
//		logger.info(" after loop buffer is "+buf+" and points were "+oldPoints+"   and lastType="+lastType);
		Label part1 = new Label(buf.toString()+":");
		part1.setStyle("-fx-font-weight: bold");
		Label part2 = new Label(ResourceI18N.get(RES, (lastType.getId().equals("technomancer")?"label.resonance":"label.magic"))+" "+oldPoints);
		HBox line = new HBox(5, part1, part2);
		if (lastType.getId().equals("mundane"))
			line = new HBox(5, new Label(lastType.getName()));
		ret.getChildren().add(line);
		
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.PriorityTable#getSkillsNode(de.rpgframework.shadowrun.Priority)
	 */
	@Override
	public Node getSkillsNode(Priority prio) {
		Label ret = new Label();
		switch (prio) {
		case A: ret.setText("32"); break;
		case B: ret.setText("24"); break;
		case C: ret.setText("20"); break;
		case D: ret.setText("16"); break;
		case E: ret.setText("10"); break;			
		}
		ret.setStyle("-fx-font-size: 200%");
		return ret;
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.shadowrun.chargen.jfx.wizard.PriorityTable#getResourcesNode(de.rpgframework.shadowrun.Priority)
	 */
	@Override
	public Node getResourcesNode(Priority prio) {
		int nuyen = 0;
		switch (prio) {
		case A: nuyen = 450000; break;
		case B: nuyen = 275000; break;
		case C: nuyen = 150000; break;
		case D: nuyen = 50000; break;
		case E: nuyen = 8000; break;		
		}

		Label ret =new Label(String.format(RES.getString("format.nuyen"), nuyen));
		ret.setStyle("-fx-font-size: 150%");
		return ret;
	}

}
