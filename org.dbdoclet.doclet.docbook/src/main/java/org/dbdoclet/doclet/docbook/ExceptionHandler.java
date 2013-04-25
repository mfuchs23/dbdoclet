package org.dbdoclet.doclet.docbook;

import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletContext;
import org.dbdoclet.option.OptionException;

import com.sun.javadoc.ClassDoc;

public class ExceptionHandler {

	private static final Log logger = LogFactory.getLog(ExceptionHandler.class);

	public static void handleException(Throwable oops) {
		handleException(null, oops);
	}

	public static void handleException(DocletContext context, Throwable oops) {

		Throwable cause = oops;

		while (cause.getCause() != null) {
			cause = cause.getCause();
		}

		if (context != null) {

			ClassDoc classDoc = context.getClassDoc();

			if (classDoc != null) {

				logger.fatal(classDoc.qualifiedName(), oops);
				return;
			}
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
