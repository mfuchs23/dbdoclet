package org.dbdoclet.doclet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.dbdoclet.option.Option;
import org.dbdoclet.option.OptionException;
import org.dbdoclet.option.OptionList;
import org.dbdoclet.service.ResourceServices;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;

public class AbstractDoclet extends Doclet {

	@Inject
	protected ResourceBundle res;

	protected DocletOptions options = null;

	public AbstractDoclet() {
		super();
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	public DocletOptions getOptions() {
		return options;
	}

	public void setOptions(String[][] args) throws OptionException {
		options = new DocletOptions(args);
	}

	public static boolean validOptions(String[][] args,
			DocErrorReporter reporter) {

		DocletOptions options = new DocletOptions(args);
		OptionList optionList = options.getOptionList();
		boolean rc = optionList.validate();

		Option<?> destFileOption = optionList.findOption("destination-file");
		Option<?> destDirOption = optionList.findOption("destination-directory");
		String usageText = "";
		try {
			usageText = ResourceServices
					.getResourceAsString("resource/dbdoclet_usage.txt");
		} catch (IOException oops) {
			usageText = oops.getMessage();
		}
		
		if (destFileOption.isUnset() == false && destDirOption.isUnset() == false) {
			reporter.printError("Options --destination-file and --destination-directory may not be used together!");
			reporter.printNotice(usageText);
		}
		
		if (rc == false) {
			reporter.printError(optionList.getError());
			reporter.printNotice(usageText);
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

		if ((options == null) || (options.isQuiet() == false)) {
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

		if ((options == null) || (options.isQuiet() == false)) {

			String buffer = MessageFormat.format(str, param1);
			System.out.println(buffer);
		}
	}

}