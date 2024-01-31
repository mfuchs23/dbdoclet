package org.dbdoclet.doclet.docbook;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbdoclet.option.OptionException;

public class ExceptionHandler {

	private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

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

		logger.log(Level.SEVERE, "Unexpected error!", oops);
	}

}
