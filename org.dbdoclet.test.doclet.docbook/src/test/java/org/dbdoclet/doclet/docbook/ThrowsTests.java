package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.common.doc.DocletException;
import org.junit.Test;

public class ThrowsTests extends AbstractTestCase {

	@Test
	public void testThrowsTests() throws DocletException, IOException {
	
		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/ThrowsTags.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/ThrowsTags.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}	
}
