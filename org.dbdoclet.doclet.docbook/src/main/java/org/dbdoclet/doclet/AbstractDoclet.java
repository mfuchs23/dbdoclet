package org.dbdoclet.doclet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.dbdoclet.doclet.docbook.ExceptionHandler;
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

		if (rc == false) {
			reporter.printError(optionList.getError());
			try {
				reporter.printNotice(ResourceServices
						.getResourceAsString("resource/dbdoclet_usage.txt"));
			} catch (IOException oops) {
				ExceptionHandler.handleException(oops);
			}
		}

		return rc;
	}

	public static int optionLength(String option) {

		if (option.equals("-d") || option.equals("-profile")
				|| option.equals("-tag")) {
			return 2;
		}

		return 1;
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