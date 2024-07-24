package org.dbdoclet.doclet8.docbook;

import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.option.OptionException;

public class ExceptionHandler {

	private static final Log logger = LogFactory.getLog(ExceptionHandler.class);

	public static void handleException(Throwable oops) {

		Throwable cause = oops;

		while (cause.getCause() != null) {
			cause = cause.getCause();
		}

		if (cause instanceof OptionException) {
			System.out.println(cause.getMessage());
			return;
		}

		if (cause instanceof NoSuchElementException) {
			System.out.println(cause.getMessage());
			return;
		}

		String msg = oops.getMessage();

		if (msg == null) {
			msg = "";
		}

		logger.fatal(msg, oops);
	}

}
