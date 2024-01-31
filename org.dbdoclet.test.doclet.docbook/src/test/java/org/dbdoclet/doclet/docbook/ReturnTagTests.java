package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class ReturnTagTests extends AbstractTestCase {

	@Test
	public void testReturnTag() throws DocletException, IOException {

		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/ReturnTag.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/ReturnTag.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}
}
