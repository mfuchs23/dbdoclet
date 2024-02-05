package org.dbdoclet.doclet;

import java.util.Locale;
import java.util.logging.Logger;

import javax.lang.model.SourceVersion;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;

public abstract class AbstractDoclet implements Doclet {

	protected Reporter reporter;
	protected Locale locale;

	public AbstractDoclet() {
		super();
	}

	/**
	 * Any doclet that uses custom options must have a method called
	 * optionLength(String option) that returns an int. For each custom option
	 * that you want your doclet to recognize, optionLength must return the
	 * number of separate pieces or tokens in the option. For our example, we
	 * want to be able to use the custom option of the form -tag mytag. This
	 * option has two pieces, the -tag option itself and its value, so the
	 * optionLength method in our doclet must return 2 for the -tag option. The
	 * optionsLength method should return 0 for unrecognized options.
	 * 
	 * @param option
	 * @return int - number of tokens
	 */
	public static int optionLength(String option) {

		if (option.equals("-d") || option.equals("-f") || option.equals("-profile")
				|| option.equals("-tag") || option.equals("-title")) {
			return 2;
		}

		return 0;
	}

	public void println(String str) {

		if (str == null) {
			throw new IllegalArgumentException(
					"The argument str must not be null!");
		}
		
		Logger.getAnonymousLogger().info(str);
	}

	public void println(String str, String param1) {

		if (str == null) {
			throw new IllegalArgumentException(
					"The argument str must not be null!");
		}

		if (param1 == null) {
			throw new IllegalArgumentException(
					"The argument param1 must not be null!");
		}
	}

	@Override
	public void init(Locale locale, Reporter reporter) {
		this.locale = locale;
		this.reporter = reporter;	
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}


	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

}