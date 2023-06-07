package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class ArticleManagerTests extends AbstractTestCase {

	@Test
	public void testPackageMusic() throws DocletException, IOException {

		String srcpath = "org.dbdoclet.music";
		String classpath = sourcePath;

		javadocTestPackage("-cp", classpath, srcpath);
		javadocStandardTestPackage("-cp", classpath, srcpath);
		printDocBookFile();
		viewPdf();
	}
}
