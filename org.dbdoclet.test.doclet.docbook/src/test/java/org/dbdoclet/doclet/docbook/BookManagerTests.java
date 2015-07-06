package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class BookManagerTests extends AbstractTestCase {

	@Test
	public void test_1() throws DocletException, IOException {

		String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
		String classpath = sourcePath;

		javadoc("-cp", classpath, srcpath);
		printDocBookFile();
        String value = xpath("/db:book");
        assertNotNull("Book", value);
	}
}
