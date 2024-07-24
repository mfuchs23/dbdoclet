package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.dbdoclet.doclet.common.doc.DocletException;
import org.junit.Test;

public class BookManagerTests extends AbstractTestCase {

	@Test
	public void test_1() throws DocletException, IOException {

		String srcpath = "org.dbdoclet.music";
		String classpath = sourcePath;

		javadocTestPackage("-cp", classpath, srcpath);
		javadocStandardTestPackage("-cp", classpath, srcpath);
		printDocBookFile();
		viewPdf();
	}
}
