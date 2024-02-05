package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class InlineLinkTests extends AbstractTestCase {

	@Test
	public void testInlineLinks() throws DocletException, IOException {

		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/InlineLinks.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/InlineLinks.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}
}
