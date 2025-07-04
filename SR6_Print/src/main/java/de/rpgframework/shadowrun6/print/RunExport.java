package de.rpgframework.shadowrun6.print;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.rpgframework.MultiLanguageResourceBundle;
import de.rpgframework.genericrpg.data.SkillSpecialization;
import de.rpgframework.genericrpg.data.SkillSpecializationValue;
import de.rpgframework.random.Actor;
import de.rpgframework.random.Plot;
import de.rpgframework.random.PlotNode;
import de.rpgframework.random.PlotNode.Type;
import de.rpgframework.random.ResultExport;
import de.rpgframework.random.TextLine;
import de.rpgframework.shadowrun.ShadowrunAttribute;
import de.rpgframework.shadowrun.generators.RunGenerator;
import de.rpgframework.shadowrun6.SR6NPC;
import de.rpgframework.shadowrun6.SR6Skill;
import de.rpgframework.shadowrun6.SR6SkillValue;
import de.rpgframework.shadowrun6.Shadowrun6Core;

/**
 * @author prelle
 *
 */
public class RunExport implements ResultExport {
	
	private static Color GREY_BACKGROUND = new Color(237,236,231);
	private static Color TABLE_HEADER_BACKGROUND = new Color(226,226,219);
	private static Font TABLE_HEADER_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(151,4,112));
	private static Font TABLE_DATA_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(0,0,0));
	private static Font BOLD = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(0,0,0));
	private static Font REGULAR = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(0,0,0));

	private MultiLanguageResourceBundle PLOT_RES = new MultiLanguageResourceBundle(
			RunGenerator.class.getName().toString(), Locale.ENGLISH, Locale.GERMAN);
	//-------------------------------------------------------------------
	/**
	 */
	public RunExport() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @see de.rpgframework.random.ResultExport#export(java.lang.Object, java.io.OutputStream, java.util.Locale)
	 */
	@Override
	public void export(Object toExport, OutputStream out, Locale loc) {
		Plot plot = (Plot)toExport;
		Rectangle size = (loc==Locale.ENGLISH)?PageSize.LETTER:PageSize.A4;
		try {
			Path tempFile = Paths.get("/tmp/run.pdf");//Files.createTempFile("shadowrun6", ".pdf");
			// TODO Auto-generated method stub
			Document document = new Document(size);
			document.setMargins(30, 30, 20, 30);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile.toFile()));
           writer.setPageEvent(new PdfBackground("src/main/resources/sr_background.jpg"));

			writer.createXmpMetadata();
//        writer.setPageEvent(new Footer());
			

	         //Set column parameters
	         float offSet = 36;
	         float gutter = 13;
	         float columnWidth = (size.getWidth() - offSet * 2) / 2 - gutter;
	         float columnHeight = size.getHeight() - offSet * 2;

	         //Define column areas
//	         Rectangle[] columns = {
//	             new Rectangle(offSet, offSet, columnWidth, columnHeight),
//	             new Rectangle(offSet + columnWidth + gutter, offSet, columnWidth, columnHeight)};
//	         document.setRenderer(new ColumnDocumentRenderer(document, columns));    
			
			document.open();
	         MultiColumnText mct = new MultiColumnText();
	         mct.addRegularColumns(document.left(), document.right(), gutter, 2);
			int chapterNum=0;
			Chapter chapter = null;
			for (PlotNode section : plot.getChildNodes()) {

		         String title = PLOT_RES.getString("section." + section.getType().name().toLowerCase(), loc);
				if (section.getType()==Type.EXPOSITION || section.getType()==Type.REWARD) {
					Paragraph para = new Paragraph(title, new Font(Font.HELVETICA, 20, Font.NORMAL));
					chapter = new Chapter(para, (++chapterNum));
					document.add(chapter);
			         mct = new MultiColumnText();
			         mct.addRegularColumns(document.left(), document.right(), gutter, 2);
				} else {
					Paragraph para = new Paragraph(title, new Font(Font.HELVETICA, 16, Font.NORMAL));
//					Section sect = chapter.addSection(para);
//					document.add(para);
					mct.addElement(para);
				}


		         for (TextLine line : section.getLines()) {
					StringBuffer buf = new StringBuffer();
					for (String key : line.getKeys()) {
						String trans = PLOT_RES.getString(key, loc).trim();
						buf.append(trans + " ");
					}
					if (!buf.toString().trim().endsWith(".")) {
						Paragraph para = new Paragraph(buf.toString().trim() + ".");
						mct.addElement(para);
					} else {
						Paragraph para = new Paragraph(buf.toString().trim());
						mct.addElement(para);
					}
				}
		         document.add(mct);
			}
			
			// Add actors
			chapter = new Chapter("NPCs", (++chapterNum));
			document.add(chapter);
	         mct = new MultiColumnText();
	         mct.addRegularColumns(document.left(), document.right(), gutter, 2);
			for (Actor actor : plot.getActors()) {
				PdfPTable table = export(actor, mct, loc, columnWidth);
//				document.add(table);
			}
			document.add(mct);

			PdfContentByte canvas = writer.getDirectContentUnder();
	        Image image = Image.getInstance("src/main/resources/sr_background.jpg");
	        image.scaleAbsoluteWidth(PageSize.A4.getWidth());
	        image.scaleAbsoluteHeight(PageSize.A4.getHeight());
	        image.setAbsolutePosition(0, 0);
	        canvas.addImage(image);
			
			document.addCreationDate();
			document.addAuthor("Stefan Prelle");
			document.addCreator("Run Generator");
			document.addKeywords("Shadowrun");
			document.addProducer();
			//document.addSubject("Migration auf Tengo Centraflex");
			document.addTitle("Randomly generated run");
			document.close();
			System.out.println("Wrote to "+tempFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// -------------------------------------------------------------------
	private PdfPTable export(Actor actor, MultiColumnText mct, Locale loc, float columnWidth) throws DocumentException {
		Paragraph para = new Paragraph("Type "+actor.getRole()+": "+actor.getName()+", "+actor.getGender(), new Font(Font.HELVETICA, 16, Font.NORMAL));
		mct.addElement(para);
		
		SR6NPC npc = (SR6NPC) actor.getRuleData();
		if (npc!=null) {
		para = new Paragraph(npc.getName(loc));
		mct.addElement(para);
		}
		int columns = 9;
		int magicOrResonance = -1;
		String magicOrResName = null;
		if (npc!=null) {
			if (npc.getAttribute(ShadowrunAttribute.MAGIC).getDistributed()>0) {
				columns++;
				magicOrResName = ShadowrunAttribute.MAGIC.getShortName(loc);
				magicOrResonance = npc.getAttribute(ShadowrunAttribute.MAGIC).getDistributed();
			} else if (npc.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed()>0) {
				magicOrResName = ShadowrunAttribute.RESONANCE.getShortName(loc);
				magicOrResonance = npc.getAttribute(ShadowrunAttribute.RESONANCE).getDistributed();
			}
		}
		PdfPTable table = new PdfPTable(columns);
		//table.setTotalWidth(120);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
//		table.setLockedWidth(true);
		table.addCell(createHeaderCell(ShadowrunAttribute.BODY     .getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.AGILITY  .getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.REACTION .getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.STRENGTH .getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.WILLPOWER.getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.LOGIC    .getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.INTUITION.getShortName(loc)));
		table.addCell(createHeaderCell(ShadowrunAttribute.CHARISMA .getShortName(loc)));
		if (columns==10)
			table.addCell(createHeaderCell(magicOrResName));
		table.addCell(createHeaderCell(ShadowrunAttribute.ESSENCE .getShortName(loc)));
	
		// Attributes
		if (npc!=null) {
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.BODY     ).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.AGILITY  ).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.REACTION ).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.STRENGTH ).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.WILLPOWER).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.LOGIC    ).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.INTUITION).getModifiedValue()+""));
			table.addCell(createDataCell(npc.getAttribute(ShadowrunAttribute.CHARISMA ).getModifiedValue()+""));
			if (columns==10)
				table.addCell(createDataCell(magicOrResonance+""));
			table.addCell(createDataCell("?"));
		}
		
		mct.addElement(table);
		
		Paragraph skills = new Paragraph();
		skills.add(new Chunk(PLOT_RES.getString("label.skills", loc)+": ", BOLD));
		List<String> elements = new ArrayList<>();
		if (npc!=null) {
			for (SR6SkillValue skill : npc.getSkillValues()) {
				String name = skill.getSkill().getName(loc)+" "+skill.getDistributed();
				if (!skill.getSpecializations().isEmpty()) {
					name+=" (";
					List<String> inner = new ArrayList<>();
					for (SkillSpecializationValue<SR6Skill> spec : skill.getSpecializations()) {
						SkillSpecialization<SR6Skill> special = Shadowrun6Core.getItem(SkillSpecialization.class, spec.getKey());
						if (special==null) {
							inner.add(spec.getKey()+" +"+spec.getDistributed());
						} else {
							inner.add(special.getName(loc)+" +"+spec.getDistributed());
						}
					}
					name+=String.join(",", inner);
					name+=")";
				}
				elements.add(name);
			}
		}
		skills.add(new Chunk(String.join(", ", elements), REGULAR));
		
		PdfPTable outer = new PdfPTable(1);
		outer.setWidthPercentage(100);
		outer.setSpacingBefore(5);
//		outer.setWidthPercentage(100);
		PdfPCell c1 = new PdfPCell(skills);
		c1.setBackgroundColor(GREY_BACKGROUND);
		outer.addCell(c1);
		
		mct.addElement(outer);
		
		return table;
	}

	//-------------------------------------------------------------------
	private static PdfPCell createHeaderCell(String content) {
		PdfPCell c1 = new PdfPCell(new Phrase(content, TABLE_HEADER_FONT));
		c1.setBackgroundColor(TABLE_HEADER_BACKGROUND);
		return c1;
		
	}

	//-------------------------------------------------------------------
	private static PdfPCell createDataCell(String content) {
		PdfPCell c1 = new PdfPCell(new Phrase(content, TABLE_DATA_FONT));
		c1.setBackgroundColor(GREY_BACKGROUND);
		return c1;
		
	}
	
}
