package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class DeprecatedTests extends AbstractTestCase {

	@Test
	public void testDeprecatedTests() throws DocletException, IOException {
	
		javadocTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/DeprecatedTags.java");
		javadocStandardTestClass("-cp", sourcePath, "src/main/java/org/dbdoclet/doclet/DeprecatedTags.java");
		printDocBookFile();
		viewPdf();
		viewHtml();
	}
	
    @Test
    public void testDeprecatedList() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
        String classpath = sourcePath;

        javadoc("-cp", classpath, srcpath);
        String value = xpath("//db:modifier[. = '@Deprecated']");
        assertEquals("@Deprecated", value);
    }
}
