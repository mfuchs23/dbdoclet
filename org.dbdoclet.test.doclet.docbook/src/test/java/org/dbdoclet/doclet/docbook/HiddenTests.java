package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.common.doc.DocletException;
import org.junit.Test;

public class HiddenTests extends AbstractTestCase {

	@Test
	public void testHiddenPackage() throws DocletException, IOException {
	
		javadocTestClass("-cp", sourcePath, "org.dbdoclet.doclet.hidden");
		javadocStandardTestClass("-cp", sourcePath, "org.dbdoclet.doclet.hidden");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}	

	@Test
	public void testHiddenTests() throws DocletException, IOException {
	
		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/HiddenTags.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/HiddenTags.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}	
}
