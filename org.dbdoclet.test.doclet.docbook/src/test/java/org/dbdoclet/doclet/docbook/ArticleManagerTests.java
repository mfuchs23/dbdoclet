package org.dbdoclet.doclet.docbook;

import java.io.IOException;

import org.dbdoclet.doclet.common.doc.DocletException;
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

	@Test
	public void testPackageTestComment() throws DocletException, IOException {

		String srcpath = "org.dbdoclet.test.doclet.comment";
		String classpath = sourcePath;

		javadocTestPackage("-cp", classpath, srcpath);
		javadocStandardTestPackage("-cp", classpath, srcpath);
		printDocBookFile();
		viewPdf();
	}
}
