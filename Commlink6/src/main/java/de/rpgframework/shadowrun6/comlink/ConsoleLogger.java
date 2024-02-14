package de.rpgframework.shadowrun6.comlink;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

/**
 * @author prelle
 *
 */
public class ConsoleLogger implements Logger {

	private String name;
	private Level minLevel;

	//-------------------------------------------------------------------
	public ConsoleLogger(String name, Level minLevel) {
		this.name = name;
		this.minLevel = minLevel;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.System.Logger#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.System.Logger#isLoggable(java.lang.System.Logger.Level)
	 */
	@Override
	public boolean isLoggable(Level level) {
		return level.getSeverity()>=minLevel.getSeverity();
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.System.Logger#log(java.lang.System.Logger.Level, java.util.ResourceBundle, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
		if (!isLoggable(level)) return;
		StringWriter out = new StringWriter();
		thrown.printStackTrace(new PrintWriter(out));

		String prefix = "";
		try {
			throw new RuntimeException("trace");
		} catch (Exception e) {
			StackTraceElement element = e.getStackTrace()[2];
			if (element.getClassName().equals("de.rpgframework.MultiLanguageResourceBundle"))
				element = e.getStackTrace()[5];
			prefix="("+element.getClassName().substring(element.getClassName().lastIndexOf(".")+1)+".java:"+element.getLineNumber()+") : ";
		}

		Instant now = Instant.now();
		System.out.printf("[%10s][%7s][%10s]: %s\n%s", now, level, name, prefix+msg, out.toString());
		if (ComLinkMain.out!=null && !ComLinkMain.out.checkError()) {
			ComLinkMain.out.printf("[%s][%s]: %s - %s", now, level, name, prefix+msg, thrown);
			ComLinkMain.out.append(out.toString());
			ComLinkMain.out.flush();
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.lang.System.Logger#log(java.lang.System.Logger.Level, java.util.ResourceBundle, java.lang.String, java.lang.Object[])
	 */
	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		if (!isLoggable(level)) return;
		String prefix = "";
		try {
			throw new RuntimeException("trace");
		} catch (Exception e) {
			StackTraceElement element = e.getStackTrace()[2];
			if (element.getClassName().equals("de.rpgframework.MultiLanguageResourceBundle"))
				element = e.getStackTrace()[5];
			prefix="("+element.getClassName().substring(element.getClassName().lastIndexOf(".")+1)+".java:"+element.getLineNumber()+") : ";
		}
		LocalDateTime now = LocalDateTime.now();
		try {
			if (params!=null && params.length>0) {
				System.out.printf("[%7s][%10s]: %s%n", level, name, prefix+MessageFormat.format(format, params));
				if (ComLinkMain.out!=null && !ComLinkMain.out.checkError()) {
					ComLinkMain.out.printf("[%s][%7s][%10s]: %s%n", now ,level, name, prefix+MessageFormat.format(format, params));
				}
			} else {
				System.out.printf("[%7s][%10s]: %s%n", level, name, prefix+format);
				if (ComLinkMain.out!=null && !ComLinkMain.out.checkError()) {
					ComLinkMain.out.printf("[%s][%7s][%10s]: %s%n", now ,level, name, prefix+format);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
