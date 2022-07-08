package org.prelle.rpgframework.shadowrun6.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prelle.simplepersist.StringValueConverter;

import de.rpgframework.genericrpg.items.Formula;
import de.rpgframework.genericrpg.items.ItemAttributeDefinition;
import de.rpgframework.genericrpg.persist.IntegerArrayConverter;
import de.rpgframework.shadowrun.items.Availability;
import de.rpgframework.shadowrun.persist.AvailabilityConverter;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;
import de.rpgframework.shadowrun6.items.Damage;
import de.rpgframework.shadowrun6.items.ItemTemplate;
import de.rpgframework.shadowrun6.items.SR6ItemAttribute;
import de.rpgframework.shadowrun6.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
public class GenerateExcelTest {
	
	private static Workbook wb;
	
	//-------------------------------------------------------------------
	@BeforeClass
	public static void beforeClass() {
		System.setProperty("logdir", "/tmp");
		Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init( );
		System.err.println("Found "+Shadowrun6Core.getDataSets().size()+" datasets");
		Shadowrun6Core.removeDataSet( Shadowrun6Core.getDataSets().get(2) );
		Shadowrun6Core.removeDataSet( Shadowrun6Core.getDataSets().get(1) );
		
		wb = new XSSFWorkbook();
	}
	
	//-------------------------------------------------------------------
	@AfterClass
	public static void afterClass() {
		try  (OutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
		    wb.write(fileOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------
	@Test
	public void generateWeaponSheet() {
		Sheet sheet = wb.createSheet("Weapons");
		List<ItemTemplate> list = Shadowrun6Core.getItemList(ItemTemplate.class);
		
		int rowNum=0;
		for (ItemTemplate item : list) {
			Row row = sheet.createRow(rowNum++);
			Cell cell = row.createCell(0);
			cell.setCellValue(item.getName());
			
			// Availability
			createCell(row, item, 1, SR6ItemAttribute.AVAILABILITY, new AvailabilityConverter());
			createCell(row, item, 2, SR6ItemAttribute.PRICE, null);
			if (item.getAttribute(SR6ItemAttribute.DAMAGE)!=null)
				createCell(row, item, 3, SR6ItemAttribute.DAMAGE, new WeaponDamageConverter());
			if (item.getAttribute(SR6ItemAttribute.ATTACK_RATING)!=null)
				createCell(row, item, 4, SR6ItemAttribute.ATTACK_RATING, new IntegerArrayConverter());
		}
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
	}

	//-------------------------------------------------------------------
	private void createCell(Row row, ItemTemplate item, int col, SR6ItemAttribute attr, StringValueConverter<?> conv) {
		Cell cell = row.createCell(col, CellType.STRING);
		ItemAttributeDefinition dmgDef = item.getAttribute(attr);
		if (dmgDef==null) {
			return ;
			//throw new NullPointerException("No "+attr+" definition for "+item);
		}
		Formula form = dmgDef.getFormula();
		if (form.isResolved()) {
			if (form.isObject()) {
				Object res = form.getAsObject(conv);
				if (res instanceof int[]) {
					try {
						cell.setCellValue(((IntegerArrayConverter)conv).write( (int[])res));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					cell.setCellValue(Arrays.toString((int[])res));
				} else
					cell.setCellValue(res.toString());
			} else if (form.isInteger()) {
				cell.setCellValue(form.getAsInteger());
			} else if (form.isFloat()) {
				cell.setCellValue(form.isFloat());
			} else {
				cell.setCellValue("?1?");
			}
		} else {
			cell.setCellValue(dmgDef.getRawValue());
		}
	}
	
}
