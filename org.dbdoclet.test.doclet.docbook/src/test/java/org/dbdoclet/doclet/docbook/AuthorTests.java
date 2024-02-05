package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class AuthorTests extends AbstractTestCase {

	@Test
	public void testAuthorTests() throws DocletException, IOException {

		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/AuthorTag.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/AuthorTag.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}
}
