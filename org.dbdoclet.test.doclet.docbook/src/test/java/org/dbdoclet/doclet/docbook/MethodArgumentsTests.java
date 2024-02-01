package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class MethodArgumentsTests extends AbstractTestCase {

	@Test
	public void testMethodArguments() throws DocletException, IOException {

		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/MethodArguments.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/MethodArguments.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}
}
