package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class ApiTests extends AbstractTestCase {

	@Test
	public void testApiTests() throws DocletException, IOException {

		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/ApiTags.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/ApiTags.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}
}
