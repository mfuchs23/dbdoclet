package org.dbdoclet.doclet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic.Kind;

import org.dbdoclet.option.OptionException;
import org.dbdoclet.option.OptionList;
import org.dbdoclet.service.ResourceServices;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Reporter;

public abstract class AbstractDoclet implements Doclet {

	private static final boolean OK = true;

	protected DeprecatedDocletOptions deprecated_options = null;
	protected Reporter reporter;

	private Locale locale;

	public AbstractDoclet() {
		super();
	}

	public DeprecatedDocletOptions getOptions() {
		return deprecated_options;
	}

	public void setOptions(String[][] args) throws OptionException {
		deprecated_options = new DeprecatedDocletOptions(args);
	}

	public static boolean validOptions(String[][] args,
			Reporter reporter) {

		DeprecatedDocletOptions options = new DeprecatedDocletOptions(args);
		OptionList optionList = options.getOptionList();
		boolean rc = optionList.validate();

		org.dbdoclet.option.Option<?> destFileOption = optionList.findOption("destination-file");
		org.dbdoclet.option.Option<?> destDirOption = optionList.findOption("destination-directory");
		String usageText = "";
		try {
			usageText = ResourceServices
					.getResourceAsString("resource/dbdoclet_usage.txt");
		} catch (IOException oops) {
			usageText = oops.getMessage();
		}
		
		if (destFileOption.isUnset() == false && destDirOption.isUnset() == false) {
			reporter.print(Kind.ERROR, "Options --destination-file and --destination-directory may not be used together!");
			reporter.print(Kind.NOTE, usageText);
		}
		
		if (rc == false) {
			reporter.print(Kind.ERROR, optionList.getError());
			reporter.print(Kind.NOTE, usageText);
		}

		return rc;
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

		if ((deprecated_options == null) || (deprecated_options.isQuiet() == false)) {
			System.out.println(str);
		}
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

		if ((deprecated_options == null) || (deprecated_options.isQuiet() == false)) {

			String buffer = MessageFormat.format(str, param1);
			System.out.println(buffer);
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